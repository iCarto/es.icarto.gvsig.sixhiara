package es.icarto.gvsig.sixhiara;

import org.gvsig.fmap.dal.exception.ReadException;
import org.gvsig.fmap.mapcontext.layers.vectorial.FLyrVect;
import org.gvsig.fmap.mapcontrol.MapControl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.icarto.gvsig.commons.AbstractExtension;
import es.icarto.gvsig.commons.locatorbycoords.CoordProvider;
import es.icarto.gvsig.commons.locatorbycoords.CoordProviderFactory;
import es.icarto.gvsig.commons.locatorbycoords.LocatonByCoordsZoomButton;
import es.icarto.gvsig.commons.locatorbycoords.LocatorByCoordsDialog;
import es.icarto.gvsig.commons.locatorbycoords.LocatorByCoordsModel;
import es.icarto.gvsig.commons.map.ZoomTo;
import es.icarto.gvsig.commons.map.gvsig.Map;
import es.icarto.gvsig.commons.referencing.gvsig.GPoint;
import es.icarto.gvsig.navtableforms.utils.TOCLayerManager;

public class LocatorByCoordsExtension extends AbstractExtension {
	private static final Logger logger = LoggerFactory.getLogger(GPoint.class);

	public LocatorByCoordsDialog getDialog() {
		LocatorByCoordsDialog pane = null;

		final MapControl mapControl = getView().getMapControl();

		FLyrVect provincias = getLayer();

		try {
			CoordProvider epsg32737 = CoordProviderFactory.fromLayer(
					provincias, "UTM 37S");
			CoordProvider epsg32736 = CoordProviderFactory
					.fromReprojectedLayer(provincias, "EPSG:32736", "UTM 36S");
			CoordProvider epsg4326 = CoordProviderFactory.fromReprojectedLayer(
					provincias, "EPSG:4326", "WGS84");

			LocatorByCoordsModel model = new LocatorByCoordsModel();
			model.addCoordProvider(epsg32737);
			model.addCoordProvider(epsg32736);
			model.addCoordProvider(epsg4326);
			model.setDefaultInputProj(epsg4326);
			model.setDefaultOuputProj(epsg32737);

			final ZoomTo zoomTo = new ZoomTo(new Map(mapControl));
			model.setZoomTo(zoomTo);
			pane = new LocatorByCoordsDialog(model);
		} catch (ReadException e) {
			logger.error("Error", e);
		}
		return pane;
	}

	@Override
	public void execute(String actionCommand) {
		LocatorByCoordsDialog dialog = getDialog();
		dialog.addButton(new LocatonByCoordsZoomButton(dialog.getModel()));
		if (dialog != null) {
			dialog.openDialog();
		}
	}

	@Override
	public boolean isEnabled() {
		return getLayer() != null;
	}

	private FLyrVect getLayer() {
		return new TOCLayerManager().getLayerByName("provincias");
	}

}
