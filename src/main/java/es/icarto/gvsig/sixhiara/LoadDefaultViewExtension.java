package es.icarto.gvsig.sixhiara;

import static es.icarto.gvsig.sixhiara.ConfigExtension.NAME;

import javax.swing.JSplitPane;

import org.gvsig.andami.ui.mdiManager.MDIManagerFactory;
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

	private static final Logger logger = LoggerFactory.getLogger(LoadDefaultViewExtension.class);

	@Override
	public void execute(String actionCommand) {
		MDIManagerFactory.getManager().setWaitCursor();
		try {
			final IView view = Andami.createViewIfNeeded(NAME, "EPSG:32737");
			resizeOverview(view);
			loadVectorial(view);
			zoomToBacias(view);
		} catch (final Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			MDIManagerFactory.getManager().restoreCursor();
		}
	}

	private void resizeOverview(IView iView) {
		final MapOverview mapOverview = iView.getMapOverview();
		final JSplitPane parent = (JSplitPane) mapOverview.getParent();
		parent.setDividerLocation(0.95);
	}

	private void loadVectorial(IView view) throws Exception {
		final MapDAO mapDAO = MapDAO.getInstance();
		final ELLEMap map = mapDAO.getMap(view, NAME, LoadLegend.DB_LEGEND, NAME);
		map.load(view.getMapControl().getProjection());
	}

	private void zoomToBacias(IView view) {
		final MapControl mapControl = view.getMapControl();
		final TOCLayerManager tocManager = new TOCLayerManager(mapControl);
		final FLyrVect bacias = tocManager.getLayerByName("bacias");
		try {
			final Envelope baciasEnvelope = bacias.getFullEnvelope();
			mapControl.getMapContext().zoomToEnvelope(baciasEnvelope);
		} catch (final ReadException e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Override
	public boolean isEnabled() {
		return DBSession.isActive();
	}

}
