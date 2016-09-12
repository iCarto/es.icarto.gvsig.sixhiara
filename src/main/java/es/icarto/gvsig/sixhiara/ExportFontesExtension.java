package es.icarto.gvsig.sixhiara;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;

import org.gvsig.andami.PluginServices;
import org.gvsig.fmap.mapcontext.layers.vectorial.FLyrVect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.icarto.gvsig.commons.AbstractExtension;
import es.icarto.gvsig.commons.gui.AbstractIWindow;
import es.icarto.gvsig.commons.gui.FolderChooser;
import es.icarto.gvsig.commons.gui.OkCancelPanel;
import es.icarto.gvsig.commons.gui.WidgetFactory;
import es.icarto.gvsig.navtableforms.utils.TOCLayerManager;
import es.icarto.gvsig.sixhiara.forms.FontesForm;

public class ExportFontesExtension extends AbstractExtension {

	private static final Logger logger = LoggerFactory
			.getLogger(ExportFontesExtension.class);
	private FLyrVect layer;

	@Override
	public void initialize() {
		// override super
	}

	@Override
	public void execute(String actionCommand) {
		ChooseFolder chooseFolder = new ChooseFolder();
		chooseFolder.openDialog();
		String folder = chooseFolder.getFolderPath();
		if (folder == null) {
			return;
		}

		ExportFontes exportFontes = new ExportFontes(layer);
		try {
			PluginServices.getMDIManager().setWaitCursor();
			exportFontes.execute(folder);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			PluginServices.getMDIManager().restoreCursor();
		}
	}

	@Override
	public boolean isEnabled() {
		layer = new TOCLayerManager().getLayerByName(FontesForm.LAYERNAME);
		return layer != null;
	}

	private final class ChooseFolder extends AbstractIWindow implements
	ActionListener {

		private FolderChooser chooser;
		private String folderPath;
		private OkCancelPanel ok;

		public ChooseFolder() {
			String initFile = System.getProperty("user.home");
			ok = WidgetFactory.okCancelPanel(this, this, this);
			chooser = new FolderChooser(this, "Escolha o diretório de destino",
					initFile);
		}

		@Override
		protected JButton getDefaultButton() {
			return ok.getOkButton();
		}

		@Override
		protected Component getDefaultFocusComponent() {
			return chooser.getDefaultFocusComponent();
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			folderPath = null;
			if (e.getActionCommand() == OkCancelPanel.OK_ACTION_COMMAND) {
				File folder = chooser.getFolder();
				if (folder.isDirectory()) {
					folderPath = chooser.getFolderPath();
				}
			}
			closeDialog();
		}

		public String getFolderPath() {
			return folderPath;
		}
	}

}
