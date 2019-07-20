package es.icarto.gvsig.sixhiara;

import org.gvsig.andami.plugins.Extension;

import es.udc.cartolab.gvsig.users.utils.DBSession;

public class FeatureSwitcherExtension extends Extension {

	@Override
	public void initialize() {
	}

	@Override
	public void execute(String actionCommand) {
	}

	@Override
	public boolean isEnabled() {
		return false;
	}

	@Override
	public boolean isVisible() {
		return false;
	}

	public static boolean fontesAnalyticsButton() {
		if (!DBSession.isActive()) {
			return false;
		}
		return DBSession.getCurrentSession().getDatabase().equals("aranorte");
	}

}
