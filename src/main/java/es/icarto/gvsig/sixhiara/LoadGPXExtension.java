package es.icarto.gvsig.sixhiara;

import static es.icarto.gvsig.commons.i18n.I18n._;

import java.awt.Component;
import java.io.File;

import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.gvsig.andami.PluginServices;
import org.gvsig.fmap.mapcontext.exceptions.LoadLayerException;
import org.gvsig.fmap.mapcontext.layers.FLayers;
import org.gvsig.fmap.mapcontext.layers.vectorial.FLyrVect;
import org.gvsig.gui.beans.swing.JFileChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.icarto.gvsig.commons.AbstractExtension;
import es.icarto.gvsig.commons.datasources.GPXFactory;

public class LoadGPXExtension extends AbstractExtension {

	private static final Logger logger = LoggerFactory.getLogger(LoadGPXExtension.class);

	@Override
	public void execute(String actionCommand) {
		String homeFolder = System.getProperty("user.home");
		JFileChooser fileChooser = new JFileChooser(getClass().getName(), homeFolder);
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		FileFilter gpxFilter = new FileNameExtensionFilter(_("gps_files"), "gpx");
		fileChooser.setFileFilter(gpxFilter);
		Component parent = (Component) PluginServices.getMainFrame();
		if (fileChooser.showDialog(parent, _("load")) == JFileChooser.APPROVE_OPTION) {
			File selectedFile = fileChooser.getSelectedFile();
			if (fileChooser.accept(selectedFile)) {
				loadGPX(selectedFile);
			}
		}
	}

	private void loadGPX(File selectedFile) {
		try {
			FLyrVect f = GPXFactory.getWaypointLyrFromGPX(selectedFile, "EPSG:4326");
			FLayers layers = getView().getMapControl().getMapContext().getLayers();
			layers.add(f);
		} catch (LoadLayerException e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Override
	public boolean isEnabled() {
		return isViewActive();
	}

}
