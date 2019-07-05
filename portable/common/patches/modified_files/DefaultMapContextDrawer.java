/**
 * gvSIG. Desktop Geographic Information System.
 *
 * Copyright (C) 2007-2013 gvSIG Association.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 *
 * For any additional information, do not hesitate to contact us
 * at info AT gvsig.com, or visit our website www.gvsig.com.
 */
package org.gvsig.fmap.mapcontext.impl;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.gvsig.compat.print.PrintAttributes;
import org.gvsig.fmap.dal.exception.ReadException;
import org.gvsig.fmap.mapcontext.ExtentHistory;
import org.gvsig.fmap.mapcontext.MapContext;
import org.gvsig.fmap.mapcontext.MapContextDrawer;
import org.gvsig.fmap.mapcontext.MapContextException;
import org.gvsig.fmap.mapcontext.MapContextLocator;
import org.gvsig.fmap.mapcontext.MapContextManager;
import org.gvsig.fmap.mapcontext.ViewPort;
import org.gvsig.fmap.mapcontext.layers.FLayer;
import org.gvsig.fmap.mapcontext.layers.FLayers;
import org.gvsig.fmap.mapcontext.layers.LayersIterator;
import org.gvsig.fmap.mapcontext.layers.operations.ComposedLayer;
import org.gvsig.fmap.mapcontext.layers.operations.LayerCollection;
import org.gvsig.fmap.mapcontext.rendering.legend.styling.ILabelable;
import org.gvsig.tools.ToolsLocator;
import org.gvsig.tools.dynobject.DynStruct;
import org.gvsig.tools.library.LibraryException;
import org.gvsig.tools.persistence.PersistenceManager;
import org.gvsig.tools.task.Cancellable;
import org.gvsig.tools.util.Callable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultMapContextDrawer implements MapContextDrawer {

	static final Logger LOG = LoggerFactory
			.getLogger(DefaultMapContextDrawer.class);

	MapContext mapContext = null;
	ViewPort viewPort = null;
	private CachedImage cachedImage = null;
	private DrawList previousDrawList = null;

	protected void checkInitialized() {
		if (mapContext == null || viewPort == null) {
			throw new IllegalStateException(
					"MapContext and ViewPort must be set");
		}
	}

	public void draw(FLayers root, BufferedImage image, Graphics2D g,
			Cancellable cancel, double scale) throws ReadException {

		this.checkInitialized();

		// With viewport changes all layers must be redrawn, discard cache
		if (cachedImage != null && cachedImage.hasChangedViewPortDrawVersion()) {
			cachedImage = null;
		}

		AffineTransform aux_at = null;
		
		if (isValidFullCachedImage()) {
		    
		    aux_at = g.getTransform();
			g.drawImage(
			    cachedImage.getFullDrawnImage(),
			    (int) -aux_at.getTranslateX(),
			    (int) -aux_at.getTranslateY(),
			    null);
			LOG.debug("Drawn full image from the cache, all layers cached");
			return;
		}

		DrawList drawList = this.createDrawList(root, cancel, scale);
		if (drawList == null || drawList.size() == 0) {
			return;
		}

		if (cancel.isCanceled()) {
			cachedImage = null;
			return;
		}

		int firstLayerToDraw;
		int lastLayerToDraw;
		if (isValidPartialCachedImage(drawList)) {
			firstLayerToDraw = 0;
			lastLayerToDraw = cachedImage.getLastDrawnLayerPosition();
			
			aux_at = g.getTransform();
			g.drawImage(
	                cachedImage.getPartialDrawnImage(),
	                (int) -aux_at.getTranslateX(),
	                (int) -aux_at.getTranslateY(),
	                null);
			
			cachedImage.updateVersions(mapContext, viewPort);
			LOG.debug("Reused image of cached layers from 0 to {}",
					new Integer(lastLayerToDraw));
		} else {
			if (cachedImage == null) {
				cachedImage = new CachedImage(this);
				// Draw all layers
				firstLayerToDraw = 0;
				//lastLayerToDraw = drawList.getLayerCount() - 1;
				lastLayerToDraw = drawList.getLastLayerVisible(viewPort);
			} else {
				// Draw the first group of layers without changes to be cached
				// next time
				firstLayerToDraw = 0;
				int firstChangedLayer = drawList.getFirstChangedLayer();
				// If negative nothing has changed, so draw all the layers
				lastLayerToDraw = firstChangedLayer < 0 ? drawList
						.getLayerCount() - 1 : firstChangedLayer - 1;
			}
			drawList.drawLayers(image, g, firstLayerToDraw, lastLayerToDraw,
					cancel, scale);
			cachedImage.setPartialDrawnImage(image, mapContext, viewPort,
					lastLayerToDraw);
		}

		if (cancel.isCanceled()) {
			cachedImage = null;
			return;
		}

		// Draw the second group of layers not cached
		firstLayerToDraw = lastLayerToDraw + 1;
		lastLayerToDraw = drawList.getLayerCount() - 1;
		drawList.drawLayers(image, g, firstLayerToDraw, lastLayerToDraw,
				cancel, scale);
		cachedImage.setFullDrawnImage(image);

		this.previousDrawList = drawList;
	}

	private boolean isValidPartialCachedImage(DrawList drawList) {
		return cachedImage != null
				&& cachedImage.isValidPartialDrawnImage(mapContext, drawList);
	}

	private boolean isValidFullCachedImage() {
		return cachedImage != null
				&& cachedImage.isValidFullDrawnImage(mapContext);
	}

	private void print(Object layerOrComposed, Graphics2D g,
			Cancellable cancel, double scale, PrintAttributes properties)
			throws ReadException {
		ILabelable labelable = null;
		ILabelable tmp = null;
		if (layerOrComposed instanceof ILabelable) {

			tmp = (ILabelable) layerOrComposed;

			if (tmp.isLabeled() && tmp.getLabelingStrategy() != null
					&& tmp.getLabelingStrategy().shouldDrawLabels(scale)) {
				labelable = tmp;
			}
		}

		if (layerOrComposed instanceof FLayer) {
			FLayer layer = (FLayer) layerOrComposed;
			layer.print(g, viewPort, cancel, scale, properties);
		} else {
			ComposedLayer composed = (ComposedLayer) layerOrComposed;
			composed.print(g, viewPort, cancel, scale, properties);
		}
		if (labelable != null) {
			labelable.printLabels(g, viewPort, cancel, scale, properties);
		}

	}

	public void setMapContext(MapContext mapContext) {
		if (this.mapContext == mapContext) {
			return;
		}
		this.clean();
		this.mapContext = mapContext;

	}

	public void setViewPort(ViewPort viewPort) {
		if (this.viewPort == viewPort) {
			return;
		}
		this.clean();
		this.viewPort = viewPort;

	}

	protected void clean() {
		this.cachedImage = null;
	}

	private class SimpleLayerIterator extends LayersIterator {

		public SimpleLayerIterator(FLayer layer) {
			this.appendLayer(layer);
		}

		public boolean evaluate(FLayer layer) {
			if (layer instanceof FLayers) {
				return false;
			}
			return layer.isAvailable() && layer.isVisible();
		}

	}

	public void dispose() {
		this.mapContext = null;
		this.viewPort = null;
		this.cachedImage = null;
		this.previousDrawList = null;
	}

	public void print(FLayers root, Graphics2D g, Cancellable cancel,
			double scale, PrintAttributes properties) throws ReadException {
		this.checkInitialized();

		List printList = this.createPrintList(root, cancel);
		if (cancel.isCanceled()) {
			return;
		}

		ComposedLayer composed = null;
		int pos;
		FLayer layer;
		int layerPos = -1;
		Object obj;
		LayersGroupEvent event;
		for (pos = 0; pos < printList.size(); pos++) {
			if (cancel.isCanceled()) {
				return;
			}

			obj = printList.get(pos);
			if (obj instanceof LayersGroupEvent) {
				event = (LayersGroupEvent) obj;
				if (event.type == LayersGroupEvent.IN_Event) {
					// System.out.println("=======Empiza a pintar grupo de capas "+
					// ((FLayers)event.group).getName() +"============");
					event.group.beginDraw(g, viewPort);
				} else {
					event.group.endDraw(g, viewPort);
					// System.out.println("=======Fin a pintar grupo de capas "+
					// ((FLayers)event.group).getName() +"============");

				}
				continue;
			}
			layerPos++;

			layer = (FLayer) obj;

			// *** Pintado de capa/composicion de capa ***
			if (composed == null) {
				composed = layer.newComposedLayer();
				if (composed != null) {
					try {
						composed.add(layer);
						// System.out.println("=======Imprimiendo composicion de pintado "+
						// (layerPos-1)+" ============");
						continue;
					} catch (Exception e) {
						throw new ReadException(
								"DefaultMapContexDrawer exception", e);
					}
				}
			} else {
				if (composed.canAdd(layer)) {
					try {
						composed.add(layer);
						// System.out.println("=== añadiendo a composicion de pintado "+
						// layerPos+ " "+layer.getName());
						continue;
					} catch (Exception e) {
						throw new ReadException(
								"DefaultMapContexDrawer exception", e);
					}
				} else {
					// System.out.println("=======Imprimiendo composicion de pintado "+
					// (layerPos-1)+" ============");
					this.print(composed, g, cancel, scale, properties);
					// composed.print( g, viewPort, cancel, scale,properties);
					composed = layer.newComposedLayer();
					if (composed != null) {
						try {
							composed.add(layer);
							// System.out.println("=== añadiendo a composicion de pintado "+
							// layerPos+ " "+layer.getName());
							continue;
						} catch (Exception e) {
							throw new ReadException(
									"DefaultMapContexDrawer exception", e);
						}
					}
				}
			}
			// System.out.println("=== imprimiendo "+ layerPos+
			// " "+layer.getName());
			this.print(layer, g, cancel, scale, properties);
			// layer.print(g, viewPort, cancel, scale,properties);
			// *** Pintado de capa/composicion de capa ***
			if (composed != null) {
				// si la composicion no se ha pintado la pintamos
				// System.out.println("=======Imprimiendo composicion de pintado "+
				// (layerPos-1)+" (ultimo) ============");
				this.print(composed, g, cancel, scale, properties);
				// composed.print(g, viewPort, cancel, scale, properties);
				composed = null;
			}
		}

	}

	private DrawList createDrawList(FLayers root, Cancellable cancel,
			double scale) {
		DrawList result = new DrawList(this, this.previousDrawList);
		Iterator iter = new MyLayerIterator((FLayer) root, scale);
		while (iter.hasNext()) {
			if (cancel.isCanceled()) {
				return null;
			}
			result.add(iter.next());
		}
		if (cancel.isCanceled()) {
			return null;
		}
		// Take into account also the Graphic layer
		result.add(mapContext.getGraphicsLayer());
		return result;
	}

	private List createPrintList(FLayers root, Cancellable cancel) {
		List result = new ArrayList();
		Iterator iter = new SimpleLayerIterator((FLayer) root);
		while (iter.hasNext()) {
			if (cancel.isCanceled()) {
				return null;
			}
			result.add(iter.next());
		}
		return result;
	}

	private class MyLayerIterator implements Iterator {
		List layersList = new ArrayList();
		int index = 0;
		double scale = 0;

		public MyLayerIterator(FLayer layer, double scale) {
			this.scale = scale;
			this.appendLayer(layer);
		}

		protected void appendLayer(FLayer layer) {
			if (layer instanceof LayerCollection) {
				appendLayers((LayerCollection) layer);
			} else if (this.evaluate(layer)) {
				layersList.add(layer);
			}
		}

		private void appendLayers(LayerCollection layers) {
			int i;
			layersList.add(new LayersGroupEvent(layers,
					LayersGroupEvent.IN_Event));
			for (i = 0; i < layers.getLayersCount(); i++) {
				appendLayer(layers.getLayer(i));
			}
			layersList.add(new LayersGroupEvent(layers,
					LayersGroupEvent.OUT_Event));
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}

		public boolean hasNext() {
			return index < layersList.size();
		}

		public Object next() {
			if (!this.hasNext()) {
				throw new NoSuchElementException();
			}
			Object aux = layersList.get(index);
			index++;
			return aux;
		}

		public boolean evaluate(FLayer layer) {
			if (layer instanceof LayerCollection) {
				return false;
			}
			return layer.isAvailable() && layer.isVisible()
					&& layer.isWithinScale(this.scale);
		}

	}

	class LayersGroupEvent {
		public static final String IN_Event = "in";
		public static final String OUT_Event = "Out";

		LayerCollection group = null;
		String type = IN_Event;

		public LayersGroupEvent(LayerCollection group, String type) {
			this.group = group;
			this.type = type;
		}

		public String getType() {
			return type;
		}

		public LayerCollection getGroup() {
			return group;
		}
	}


    public static class RegisterMapContextDrawer implements Callable {

        public Object call() {
            MapContextManager manager = MapContextLocator.getMapContextManager();
            try {
                    manager.setDefaultMapContextDrawer(DefaultMapContextDrawer.class);
            } catch (MapContextException ex) {
                    throw new RuntimeException("Can't register the default MapContextDrawer", ex);
            }
            return Boolean.TRUE;
        }
    }
}
