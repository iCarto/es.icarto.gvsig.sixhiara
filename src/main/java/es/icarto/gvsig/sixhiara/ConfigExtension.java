package es.icarto.gvsig.sixhiara;

import java.io.File;

import org.gvsig.andami.Launcher;
import org.gvsig.andami.plugins.Extension;

import es.icarto.gvsig.sixhiara.forms.SixhiaraFormFactory;
import es.udc.cartolab.gvsig.tools.CopyFeaturesExtension;

public class ConfigExtension extends Extension {

	@Override
	public void initialize() {
		SixhiaraFormFactory.registerFormFactory();
		String defaultPath = Launcher.getAppHomeDir() + File.separator
				+ "importacao-gps";
		CopyFeaturesExtension.setDefaultPath(defaultPath);
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

}
