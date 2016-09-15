package es.icarto.gvsig.sixhiara;

import static es.icarto.gvsig.sixhiara.ConfigExtension.NAME;

import javax.swing.JSplitPane;

import org.gvsig.andami.PluginServices;
import org.gvsig.app.project.documents.view.MapOverview;
import org.gvsig.app.project.documents.view.gui.IView;
import org.gvsig.fmap.dal.exception.ReadException;
import org.gvsig.fmap.geom.primitive.Envelope;
import org.gvsig.fmap.mapcontext.layers.vectorial.FLyrVect;
import org.gvsig.fmap.mapcontrol.MapControl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.icarto.gvsig.commons.AbstractExtension;
import es.icarto.gvsig.commons.utils.Andami;
import es.icarto.gvsig.navtableforms.utils.TOCLayerManager;
import es.udc.cartolab.gvsig.elle.utils.ELLEMap;
import es.udc.cartolab.gvsig.elle.utils.LoadLegend;
import es.udc.cartolab.gvsig.elle.utils.MapDAO;
import es.udc.cartolab.gvsig.users.utils.DBSession;

public class LoadDefaultViewExtension extends AbstractExtension {

	private static final Logger logger = LoggerFactory
			.getLogger(LoadDefaultViewExtension.class);

	@Override
	public void execute(String actionCommand) {
		PluginServices.getMDIManager().setWaitCursor();
		try {
			IView view = Andami.createViewIfNeeded(NAME, "EPSG:32737");
			resizeOverview(view);
			loadVectorial(view);
			zoomToBacias(view);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			PluginServices.getMDIManager().restoreCursor();
		}
	}

	private void resizeOverview(IView iView) {
		MapOverview mapOverview = iView.getMapOverview();
		JSplitPane parent = (JSplitPane) mapOverview.getParent();
		parent.setDividerLocation(0.95);
	}

	private void loadVectorial(IView view) throws Exception {
		MapDAO mapDAO = MapDAO.getInstance();
		ELLEMap map = mapDAO.getMap(view, NAME, LoadLegend.DB_LEGEND, NAME);
		map.load(view.getMapControl().getProjection());
	}

	private void zoomToBacias(IView view) {
		MapControl mapControl = view.getMapControl();
		TOCLayerManager tocManager = new TOCLayerManager(mapControl);
		FLyrVect bacias = tocManager.getLayerByName("bacias");
		try {
			Envelope baciasEnvelope = bacias.getFullEnvelope();
			mapControl.getMapContext().zoomToEnvelope(baciasEnvelope);
		} catch (ReadException e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Override
	public boolean isEnabled() {
		return DBSession.isActive();
	}

}
