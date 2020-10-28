package es.icarto.gvsig.sixhiara;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;

import org.gvsig.andami.ui.mdiManager.MDIManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.icarto.gvsig.commons.AbstractExtension;
import es.icarto.gvsig.commons.gui.AbstractIWindow;
import es.icarto.gvsig.commons.gui.FolderChooser;
import es.icarto.gvsig.commons.gui.OkCancelPanel;
import es.icarto.gvsig.commons.gui.WidgetFactory;

public abstract class BaseExportExtension extends AbstractExtension {

	private static final Logger logger = LoggerFactory.getLogger(BaseExportExtension.class);

	@Override
	public void execute(String actionCommand) {
		ChooseFolder chooseFolder = new ChooseFolder();
		chooseFolder.openDialog();
		String folder = chooseFolder.getFolderPath();
		if (folder == null) {
			return;
		}

		SHPExporter exporter = getExporter();
		try {
			MDIManagerFactory.getManager().setWaitCursor();
			exporter.execute(folder);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			MDIManagerFactory.getManager().restoreCursor();
		}
	}

	protected abstract SHPExporter getExporter();

	private final static class ChooseFolder extends AbstractIWindow implements ActionListener {

		private FolderChooser chooser;
		private String folderPath;
		private static String initFile = System.getProperty("user.home");
		private OkCancelPanel ok;

		public ChooseFolder() {
			ok = WidgetFactory.okCancelPanel(this, this, this);
			chooser = new FolderChooser(this, "Escolha o diretório de destino", initFile);
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
					initFile = folderPath;
				}
			}
			closeDialog();
		}

		public String getFolderPath() {
			return folderPath;
		}
	}

}
