package org.gvsig.fmap.mapcontext.impl;

import java.awt.image.BufferedImage;

import org.gvsig.compat.CompatLocator;
import org.gvsig.fmap.mapcontext.MapContext;
import org.gvsig.fmap.mapcontext.ViewPort;

public class CachedImage {
	/**
	 * 
	 */
	private final DefaultMapContextDrawer defaultMapContextDrawer;

	/**
	 * @param defaultMapContextDrawer
	 */
	CachedImage(DefaultMapContextDrawer defaultMapContextDrawer) {
		this.defaultMapContextDrawer = defaultMapContextDrawer;
	}

	private BufferedImage partialDrawnImage;
	private BufferedImage fullDrawnImage;
	private long lastMapContextVersion;
	private long lastViewPortVersion;
	private int lastDrawnLayerPosition;

	public void setPartialDrawnImage(BufferedImage partialDrawnImage,
			MapContext mapContext, ViewPort viewPort,
			int lastDrawnLayerPosition) {
		this.partialDrawnImage = CompatLocator.getGraphicsUtils()
				.copyBufferedImage(partialDrawnImage);
		this.lastDrawnLayerPosition = lastDrawnLayerPosition;
		updateVersions(mapContext, viewPort);
	}
	
	public void updateVersions(MapContext mapContext, ViewPort viewPort) {
		this.lastMapContextVersion = mapContext.getDrawVersion();
		this.lastViewPortVersion = viewPort.getDrawVersion();			
	}

	public void setFullDrawnImage(BufferedImage fullDrawnImage) {
		this.fullDrawnImage = CompatLocator.getGraphicsUtils()
				.copyBufferedImage(fullDrawnImage);
	}

	public BufferedImage getPartialDrawnImage() {
		return partialDrawnImage;
	}

	public BufferedImage getFullDrawnImage() {
		return fullDrawnImage;
	}

	public long getMapContextVersion() {
		return lastMapContextVersion;
	}

	public int getLastDrawnLayerPosition() {
		return this.lastDrawnLayerPosition;
	}

	public boolean isValidFullDrawnImage(MapContext context) {
		// If the MapContext version has not changed, there are not any
		// changes that require redrawing any of the layers.
		return fullDrawnImage != null && !hasChangedMapContextDrawVersion();
	}

	public boolean hasChangedMapContextDrawVersion() {
		// This change detects changes in layers and the viewport also
		return this.defaultMapContextDrawer.mapContext.getDrawVersion() != this.lastMapContextVersion;
	}

	public boolean hasChangedViewPortDrawVersion() {
		// This change detects changes in the viewport
		return this.defaultMapContextDrawer.viewPort.getDrawVersion() != this.lastViewPortVersion;
	}

	public boolean isValidPartialDrawnImage(MapContext context,
			DrawList drawList) {
		if (!hasChangedMapContextDrawVersion()) {
			// Nothing has changed
			return true;
		}

		if (partialDrawnImage == null || hasChangedViewPortDrawVersion()) {
			// No image available or changes in view port
			return false;
		}

		if (drawList.size() < lastDrawnLayerPosition + 1) {
			// New list has fewer layers than before
			return false;
		}

		// There is any change in the layers drawn in the partial drawn
		// image?
		return drawList.getFirstChangedLayer() > lastDrawnLayerPosition;
	}
}