package es.icarto.gvsig.sixhiara;

import static es.icarto.gvsig.sixhiara.ConfigExtension.NAME;

import org.gvsig.andami.PluginServices;
import org.gvsig.app.project.documents.view.gui.IView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.icarto.gvsig.commons.AbstractExtension;
import es.icarto.gvsig.commons.utils.Andami;
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
			MapDAO mapDAO = MapDAO.getInstance();
			ELLEMap map = mapDAO.getMap(view, NAME, LoadLegend.DB_LEGEND, NAME);
			map.load(view.getMapControl().getProjection());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			PluginServices.getMDIManager().restoreCursor();
		}
	}

	@Override
	public boolean isEnabled() {
		return DBSession.isActive();
	}

}
