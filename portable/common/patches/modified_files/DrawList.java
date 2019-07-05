package org.gvsig.fmap.mapcontext.impl;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import org.gvsig.fmap.dal.exception.ReadException;
import org.gvsig.fmap.geom.primitive.Envelope;
import org.gvsig.fmap.mapcontext.ViewPort;
import org.gvsig.fmap.mapcontext.impl.DefaultMapContextDrawer.LayersGroupEvent;
import org.gvsig.fmap.mapcontext.layers.FLayer;
import org.gvsig.fmap.mapcontext.layers.FLayerHidesArea;
import org.gvsig.fmap.mapcontext.layers.LayerDrawEvent;
import org.gvsig.fmap.mapcontext.layers.operations.ComposedLayer;
import org.gvsig.fmap.mapcontext.layers.vectorial.GraphicLayer;
import org.gvsig.fmap.mapcontext.rendering.legend.styling.ILabelable;
import org.gvsig.tools.task.Cancellable;

public class DrawList {
	/**
	 * 
	 */
	private final DefaultMapContextDrawer defaultMapContextDrawer;
	private List layers = new ArrayList();
	private List all = new ArrayList();
	private List versions = new ArrayList();
	private DrawList previosList = null;
	private int firstLayerChanged = -1;

	public DrawList(DefaultMapContextDrawer defaultMapContextDrawer) {
		this.defaultMapContextDrawer = defaultMapContextDrawer;
	}

	public DrawList(DefaultMapContextDrawer defaultMapContextDrawer, DrawList previousList) {
		this.defaultMapContextDrawer = defaultMapContextDrawer;
		if (previousList != null) {
			this.firstLayerChanged = previousList.getLayerCount();
			this.previosList = previousList;
		}
	}

	public int getLayerCount() {
		return this.layers.size();
	}

	public int getLastLayerVisible(ViewPort viewPort) {
		Envelope area = viewPort.getAdjustedEnvelope();
		for( int n=0; n<this.layers.size()-1; n++ ) {
			FLayer layer = (FLayer) this.layers.get(n);
			if( layer instanceof FLayerHidesArea ) {
				if( ((FLayerHidesArea)(layer)).hidesThisArea(area) ) {
					return n;
				}
			}
		}
		return this.layers.size()-1;
	}

	private boolean hasChanged(FLayer layer, int pos) {
		FLayer previous = (FLayer) this.previosList.layers.get(pos);
		// String previousName = previous.getName();
		// String layerName = layer.getName();
		if (previous != layer) {
			return true;
		}
		long previousVersion = ((Long) this.previosList.versions.get(pos))
				.longValue();
		long layerVersion = layer.getDrawVersion();

		return previousVersion != layerVersion;
	}

	public void add(Object obj) {
		if (obj instanceof FLayer) {
			FLayer layer = (FLayer) obj;
			int curIndex = this.layers.size();
			if (this.firstLayerChanged >= curIndex) {
				if (this.previosList.getLayerCount() > curIndex) {
					if (this.hasChanged(layer, curIndex)) {
						this.firstLayerChanged = curIndex;
					}
				} else if (this.previosList.getLayerCount() == curIndex) {
					this.firstLayerChanged = curIndex;
				}
			}
			this.layers.add(layer);
			this.versions.add(new Long(layer.getDrawVersion()));
		} else if (!(obj instanceof LayersGroupEvent)) {
			throw new UnsupportedOperationException();
		}

		this.all.add(obj);
	}

	public int size() {
		return this.all.size();
	}

	public int getFirstChangedLayer() {
		if (this.firstLayerChanged > this.layers.size()) {
			this.firstLayerChanged = this.layers.size();
		}
		return this.firstLayerChanged;
	}

	public FLayer getLayer(int pos) {
		return (FLayer) this.layers.get(pos);
	}

	public Object get(int pos) {
		return this.all.get(pos);
	}

	public void drawLayers(BufferedImage image, Graphics2D g,
			int firstLayerToDraw, int lastLayerToDraw, Cancellable cancel,
			double scale) throws ReadException {

		if (firstLayerToDraw > lastLayerToDraw) {
			DefaultMapContextDrawer.LOG.debug("Nothing to draw");
			return;
		}

		// Find the real layer positions excluding LayersGroupEvents
		FLayer firstLayer = (FLayer) layers.get(firstLayerToDraw);
		int firstLayerPos = all.indexOf(firstLayer);
		// Look if it belongs to a group and start it
		if (firstLayerPos > 0) {
			for (int i = firstLayerPos - 1; i > 0; i++) {
				Object group = all.get(i);
				if (group instanceof LayersGroupEvent) {
					LayersGroupEvent event = (LayersGroupEvent) group;
					if (event.type == LayersGroupEvent.IN_Event) {
						event.group.beginDraw(g, this.defaultMapContextDrawer.viewPort);
					}
					break;
				}
			}
		}
		FLayer lastLayer = (FLayer) layers.get(lastLayerToDraw);
		int lastLayerPos = all.indexOf(lastLayer);

		DefaultMapContextDrawer.LOG.debug("Drawing from layer {} in position (layers: {}, all: {})"
				+ " to layer {} in position (layers: {}, all: {})",
				new Object[] { firstLayer, new Integer(firstLayerToDraw),
						new Integer(firstLayerPos), lastLayer,
						new Integer(lastLayerToDraw),
						new Integer(lastLayerPos) });

		ComposedLayer composed = null;
		for (int pos = firstLayerPos; pos <= lastLayerPos; pos++) {
			if (cancel.isCanceled()) {
				return;
			}

			Object layerOrGroup = get(pos);

			// Group drawing events management
			if (layerOrGroup instanceof LayersGroupEvent) {
				LayersGroupEvent event = (LayersGroupEvent) layerOrGroup;
				if (event.type == LayersGroupEvent.IN_Event) {
					event.group.beginDraw(g, this.defaultMapContextDrawer.viewPort);
				} else {
					event.group.endDraw(g, this.defaultMapContextDrawer.viewPort);
				}
			} else {
				FLayer layer = (FLayer) layerOrGroup;
				if (composed != null && composed.canAdd(layer)) {
					// Previous or current layer could be composed
					// Add current layer
					addToComposedLayer(composed, layer);
				} else {
					if (composed != null) {
						// Current layer can't be composed on the previous
						// composedlayer. Draw previous composed
						DefaultMapContextDrawer.LOG.debug("Drawing composed layer {} ", composed);
						draw(composed, image, g, cancel, scale);
						composed = null;
					}

					// Try if the current layer can be composed
					// Create new composed or draw current layer
					composed = layer.newComposedLayer();
					if (composed == null) {
						DefaultMapContextDrawer.LOG.debug("Drawing layer {} ", layer);
						draw(layer, image, g, cancel, scale);
					} else {
						addToComposedLayer(composed, layer);
					}
				}
			}
		}
		if (composed != null) {
			// Draw the pending composed
			draw(composed, image, g, cancel, scale);
		}

		// Check if the last layer is the last of a group and close it
		for (int i = lastLayerPos + 1; i < all.size(); i++) {
			Object group = all.get(i);
			if (group instanceof LayersGroupEvent) {
				LayersGroupEvent event = (LayersGroupEvent) group;
				if (event.type == LayersGroupEvent.OUT_Event) {
					event.group.endDraw(g, this.defaultMapContextDrawer.viewPort);
				}
				break;
			}
		}
	}

	private void addToComposedLayer(ComposedLayer composed, FLayer layer)
			throws ReadException {
		try {
			DefaultMapContextDrawer.LOG.debug("Adding layer {} to composed layer ", layer, composed);
			composed.add(layer);
		} catch (Exception e) {
			throw new ReadException("DefalutMapContexDrawer exception", e);
		}
	}

	private void draw(Object layerOrComposed, BufferedImage image,
			Graphics2D g, Cancellable cancel, double scale)
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
			int beforeDrawEventType;
			int afterDrawEventType;
			if (layerOrComposed instanceof GraphicLayer) {
				beforeDrawEventType = LayerDrawEvent.GRAPHICLAYER_BEFORE_DRAW;
				afterDrawEventType = LayerDrawEvent.GRAPHICLAYER_AFTER_DRAW;
			} else {
				beforeDrawEventType = LayerDrawEvent.LAYER_BEFORE_DRAW;
				afterDrawEventType = LayerDrawEvent.LAYER_AFTER_DRAW;
			}
			FLayer layer = (FLayer) layerOrComposed;
			drawLayer(layer, image, g, cancel, scale, beforeDrawEventType,
					afterDrawEventType);
		} else {
			ComposedLayer composed = (ComposedLayer) layerOrComposed;
			composed.draw(image, g, this.defaultMapContextDrawer.viewPort, cancel, scale);
		}
		if (labelable != null) {
			labelable.drawLabels(image, g, this.defaultMapContextDrawer.viewPort, cancel, scale,
					this.defaultMapContextDrawer.mapContext.getViewPort().getDPI());
		}

	}

	protected void drawLayer(FLayer layer, BufferedImage image,
			Graphics2D g, Cancellable cancel, double scale,
			int beforeDrawEventType, int afterDrawEventType)
			throws ReadException {
		LayerDrawEvent event = new LayerDrawEvent(layer, g, this.defaultMapContextDrawer.viewPort, beforeDrawEventType);
		this.defaultMapContextDrawer.mapContext.fireLayerDrawingEvent(event);
		layer.draw(image, g, this.defaultMapContextDrawer.viewPort, cancel, scale);
		event = new LayerDrawEvent(layer, g, this.defaultMapContextDrawer.viewPort, afterDrawEventType);
		this.defaultMapContextDrawer.mapContext.fireLayerDrawingEvent(event);
	}

}