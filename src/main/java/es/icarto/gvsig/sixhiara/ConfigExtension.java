package es.icarto.gvsig.sixhiara;

import java.io.File;
import java.text.SimpleDateFormat;

import org.gvsig.andami.Launcher;
import org.gvsig.andami.plugins.Extension;
import org.gvsig.fmap.dal.EditingNotification;
import org.gvsig.fmap.dal.EditingNotificationManager;
import org.gvsig.fmap.dal.swing.DALSwingLocator;
import org.gvsig.tools.observer.Observable;
import org.gvsig.tools.observer.Observer;

import es.icarto.gvsig.sixhiara.forms.SixhiaraFormFactory;
import es.udc.cartolab.gvsig.navtable.format.DateFormatNT;
import es.udc.cartolab.gvsig.tools.CopyFeaturesExtension;


public class ConfigExtension extends Extension {

	public static final String NAME = "SIXHIARA";
	private Observer observer;

	@Override
	public void initialize() {
		SixhiaraFormFactory.registerFormFactory();
		String defaultPath = Launcher.getAppHomeDir() + File.separator + "importacao-gps";
		CopyFeaturesExtension.setDefaultPath(defaultPath);
		skipFeatureValidation();
		configDateFormat();
	}

	private void skipFeatureValidation() {
		EditingNotificationManager editingNotificationManager = DALSwingLocator.getEditingNotificationManager();
		observer = new Observer() {
			@Override
			public void update(Observable observable, Object notification) {
				EditingNotification n = (EditingNotification) notification;
				n.setSkipFeatureValidation(true);
//		          if (n.isOfType(EditingNotification.AFTER_INSERT_FEATURE)) {
//		              Abrir formulario navtable
//		          }
			}
		};
		editingNotificationManager.addObserver(observer);
	}
	
	private void configDateFormat() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		DateFormatNT.setDateFormat(dateFormat);
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
