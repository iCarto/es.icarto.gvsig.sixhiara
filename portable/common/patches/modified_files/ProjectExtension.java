/**
 * gvSIG. Desktop Geographic Information System.
 *
 * Copyright (C) 2007-2013 gvSIG Association.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 *
 * For any additional information, do not hesitate to contact us
 * at info AT gvsig.com, or visit our website www.gvsig.com.
 */
package org.gvsig.app.extension;

import java.awt.Component;
import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.prefs.Preferences;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.apache.commons.lang.StringUtils;
import org.gvsig.andami.IconThemeHelper;
import org.gvsig.andami.Launcher;
import org.gvsig.andami.Launcher.TerminationProcess;
import org.gvsig.andami.PluginServices;
import org.gvsig.andami.PluginsLocator;
import org.gvsig.andami.actioninfo.ActionInfo;
import org.gvsig.andami.actioninfo.ActionInfoManager;
import org.gvsig.andami.messages.NotificationManager;
import org.gvsig.andami.plugins.Extension;
import org.gvsig.andami.plugins.IExtension;
import org.gvsig.andami.plugins.status.IExtensionStatus;
import org.gvsig.andami.plugins.status.IUnsavedData;
import org.gvsig.andami.plugins.status.UnsavedData;
import org.gvsig.andami.ui.mdiManager.IWindow;
import org.gvsig.andami.ui.mdiManager.WindowInfo;
import org.gvsig.andami.ui.wizard.UnsavedDataPanel;
import org.gvsig.app.ApplicationLocator;
import org.gvsig.app.ApplicationManager;
import org.gvsig.app.project.Project;
import org.gvsig.app.project.ProjectManager;
import org.gvsig.app.project.documents.Document;
import org.gvsig.app.project.documents.gui.ProjectWindow;
import org.gvsig.app.project.documents.view.ViewManager;
import org.gvsig.gui.beans.swing.JFileChooser;
import org.gvsig.tools.ToolsLocator;
import org.gvsig.tools.dataTypes.DataTypes;
import org.gvsig.tools.extensionpoint.ExtensionPointManager;
import org.gvsig.tools.i18n.I18nManager;
import org.gvsig.tools.persistence.exception.PersistenceException;
import org.gvsig.tools.util.ArrayUtils;
import org.gvsig.utils.GenericFileFilter;
import org.gvsig.utils.save.AfterSavingListener;
import org.gvsig.utils.save.BeforeSavingListener;
import org.gvsig.utils.save.SaveEvent;
import org.gvsig.utils.swing.threads.IMonitorableTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Extension que proporciona controles para crear proyectos nuevos, abrirlos y
 * guardarlos. Además los tipos de tabla que soporta el proyecto son añadidos en
 * esta clase.
 *
 * @author Fernando González Cortés
 */
public class ProjectExtension extends Extension implements IExtensionStatus {
	private static final Logger LOG = LoggerFactory
			.getLogger(ProjectExtension.class);

	private static String projectPath = null;
	private ProjectWindow projectFrame;
	private Project p;
	private String lastSavePath;
	private WindowInfo seedProjectWindow;
	public static final String PROJECT_FILE_CHOOSER_ID = "PROJECT_FILECHOOSER_ID";
	/**
	 * Use UTF-8 for encoding, as it can represent characters from any language.
	 *
	 * Another sensible option would be encoding =
	 * System.getProperty("file.encoding"); but this would need some extra
	 * testing.
	 *
	 * @deprecated see PersistentManager
	 */
	@Deprecated
	public static String PROJECTENCODING = "UTF-8";

	private List<BeforeSavingListener> beforeSavingListeners = new ArrayList<BeforeSavingListener>();

	private List<AfterSavingListener> afterSavingListeners = new ArrayList<AfterSavingListener>();

	@Override
	public void initialize() {
		initializeDocumentActionsExtensionPoint();
		registerDocuments();
		registerIcons();

		File projectFile = getProjectFileFromArguments();
		if (projectFile != null) {
			// Posponemos la apertura del proyecto ya que en este momento
			// puede que no este inicializado algun plugin que precise el
			// proyecto para poderse cargar.
			PluginsLocator.getManager().addStartupTask("Open project",
					new OpenInitialProjectTask(projectFile), true, 1000);
		}
	}

	private void registerIcons() {
		IconThemeHelper.registerIcon("action", "application-project-new", this);
		IconThemeHelper
		.registerIcon("action", "application-project-open", this);
		IconThemeHelper
		.registerIcon("action", "application-project-save", this);
		IconThemeHelper.registerIcon("action", "application-project-save-as",
				this);

		IconThemeHelper.registerIcon("project", "project-icon", this);
	}

	/**
	 * Returns the file to be opened or null if no parameter or file does not
	 * exist
	 *
	 * @return
	 */
	private File getProjectFileFromArguments() {
		String[] theArgs = PluginServices.getArguments();
		if (theArgs.length < 3) {
			// application-name and extensions-folder are fixed arguments
			return null;
		}
		String lastArg = theArgs[theArgs.length - 1];
		if (StringUtils.isEmpty(lastArg)) {
			return null;
		}
		if (lastArg.startsWith("-")) {
			// Args starts with "-" are flags
			return null;
		}
		if (!lastArg.toLowerCase().endsWith(
				Project.FILE_EXTENSION.toLowerCase())) {
			LOG.info("Do not open project file, does not have the expected extension '"
					+ Project.FILE_EXTENSION + "' (" + lastArg + ").");
			return null;
		}
		File projectFile = new File(lastArg);
		if (!projectFile.exists()) {
			LOG.info("Do not open project file, '"
					+ projectFile.getAbsolutePath() + "' do not exist.");
			return null;
		}
		return projectFile;
	}

	private class OpenInitialProjectTask implements Runnable {
		private File projectFile;

		public OpenInitialProjectTask(File projectFile) {
			this.projectFile = projectFile;
		}

		@Override
		public void run() {
			if (this.projectFile == null) {
				return;
			}
			ActionInfoManager actionManager = PluginsLocator
					.getActionInfoManager();
			ActionInfo action = actionManager
					.getAction("application-project-open");
			action.execute(this.projectFile);
		}
	}

	public ProjectWindow getProjectFrame() {
		if (projectFrame == null) {
			projectFrame = new ProjectWindow();
		}
		return projectFrame;
	}

	/**
	 * Muestra la ventana con el gestor de proyectos.
	 */
	public void showProjectWindow() {
		if (seedProjectWindow != null) {
			if (seedProjectWindow.isClosed()) {
				// if it was closed, we just don't open the window now
				seedProjectWindow.setClosed(false);
				return;
			}
			WindowInfo winProps = seedProjectWindow;
			seedProjectWindow = null;
			PluginServices.getMDIManager().addWindow(getProjectFrame());
			PluginServices.getMDIManager().changeWindowInfo(getProjectFrame(),
					winProps);
		} else {
			PluginServices.getMDIManager().addWindow(getProjectFrame());
		}
	}

	/**
	 * Muestra la ventana con el gestor de proyectos, con las propiedades de
	 * ventana especificadas.
	 */
	public void showProjectWindow(WindowInfo wi) {
		seedProjectWindow = wi;
		showProjectWindow();
	}

	/**
	 * Guarda el proyecto actual en disco.
	 */
	private boolean saveProject() {
		boolean saved = false;
		// if (p.getPath() == null) {
		if (projectPath == null) {
			saved = saveAsProject(null);
		} else {
			long t1, t2;
			t1 = System.currentTimeMillis();
			saved = writeProject(new File(projectPath), p, false);
			t2 = System.currentTimeMillis();
			PluginServices.getLogger().info(
					"Project saved. " + (t2 - t1) + " miliseconds");
			getProjectFrame().refreshControls();
		}
		return saved;
	}

	private boolean saveAsProject(File file) {
		boolean saved = false;

		if (lastSavePath == null) {
			lastSavePath = projectPath;
		}

		if (file == null) {
			Preferences prefs = Preferences.userRoot().node("gvsig.foldering");
			JFileChooser jfc = new JFileChooser(PROJECT_FILE_CHOOSER_ID,
					prefs.get("ProjectsFolder", null));

			jfc.setDialogTitle(PluginServices.getText(this, "guardar_proyecto"));

			GenericFileFilter projExtensionFilter = new GenericFileFilter(
					Project.FILE_EXTENSION, MessageFormat.format(PluginServices
							.getText(this, "tipo_fichero_proyecto"),
							Project.FILE_EXTENSION));
			jfc.addChoosableFileFilter(projExtensionFilter);
			jfc.setFileFilter(projExtensionFilter);

			if (jfc.showSaveDialog((Component) PluginServices.getMainFrame()) != JFileChooser.APPROVE_OPTION) {
				return saved;
			}
			file = jfc.getSelectedFile();
		}

		if (!(file.getPath().toLowerCase().endsWith(Project.FILE_EXTENSION
				.toLowerCase()))) {
			file = new File(file.getPath() + Project.FILE_EXTENSION);
		}
		saved = writeProject(file, p);
		String filePath = file.getAbsolutePath();
		lastSavePath = filePath.substring(0,
				filePath.lastIndexOf(File.separatorChar));

		getProjectFrame().refreshControls();
		return saved;
	}

	/**
	 * Checks whether the project and related unsaved data is modified, and
	 * allows the user to save it.
	 *
	 * @return true if the data has been correctly saved, false otherwise
	 */
	private boolean askSave() {
		if (p != null && p.hasChanged()) {
			TerminationProcess process = Launcher.getTerminationProcess();
			UnsavedDataPanel panel = process.getUnsavedDataPanel();
			panel.setHeaderText(PluginServices.getText(this,
					"_Select_resources_to_save_before_closing_current_project"));
			panel.setAcceptText(
					PluginServices.getText(this, "save_resources"),
					PluginServices
					.getText(this,
							"Save_the_selected_resources_and_close_current_project"));
			panel.setCancelText(PluginServices.getText(this, "Cancel"),
					PluginServices.getText(this, "Return_to_current_project"));
			int closeCurrProj;
			try {
				closeCurrProj = process.manageUnsavedData();
				if (closeCurrProj == JOptionPane.NO_OPTION) {
					// the user chose to return to current project
					return false;
				}
			} catch (Exception e) {
				LOG.error("Some data can not be saved", e);
			}
		}
		return true;
	}

	@Override
	public void execute(String command) {
		this.execute(command, null);
	}

	@Override
	public void execute(String actionCommand, Object[] args) {
		if (actionCommand.equals("application-project-new")) {
			if (!askSave()) {
				return;
			}

			projectPath = null;
			PluginServices.getMDIManager().closeAllWindows();
			setProject(ProjectManager.getInstance().createProject());
			getProjectFrame().setProject(p);
			showProjectWindow();
			PluginServices.getMainFrame().setTitle(
					PluginServices.getText(this, "sin_titulo"));

		} else if (actionCommand.equals("application-project-open")) {
			if (!askSave()) {
				return;
			}
			File projectFile = (File) ArrayUtils.get(args, 0, DataTypes.FILE);
			if (projectFile != null && !projectFile.exists()) {
				LOG.warn("Can't load project '" + projectFile.getAbsolutePath()
						+ "', file not exist.");
				projectFile = null;
			}

			if (projectFile == null) {
				Preferences prefs = Preferences.userRoot().node(
						"gvsig.foldering");
				JFileChooser jfc = new JFileChooser(PROJECT_FILE_CHOOSER_ID,
						prefs.get("ProjectsFolder", null));

				GenericFileFilter projExtensionFilter = new GenericFileFilter(
						Project.FILE_EXTENSION, PluginServices.getText(this,
								"tipo_fichero_proyecto"));
				jfc.addChoosableFileFilter(projExtensionFilter);
				jfc.setFileFilter(projExtensionFilter);

				if (jfc.showOpenDialog((Component) PluginServices
						.getMainFrame()) != JFileChooser.APPROVE_OPTION) {
					return;
				}
				// ProjectDocument.initializeNUMS();

				projectFile = jfc.getSelectedFile();
			}

			PluginServices.getMDIManager().closeAllWindows();

			Project o = readProject(projectFile);
			setPath(projectFile.getAbsolutePath());
			// lastPath = getPath();
			if (o != null) {
				setProject(o);
			}

			getProjectFrame().setProject(p);
			PluginServices.getMainFrame().setTitle(projectFile.getName());
			getProjectFrame().refreshControls();

			// p.restoreWindowProperties();

		} else if (actionCommand.equals("application-project-save")) {
			// saveProject();
			try {
				Launcher.manageUnsavedData("there_are_unsaved_resources");
			} catch (Exception e) {
				LOG.warn("Can't manage unsaved data", e);
			}
		} else if (actionCommand.equals("application-project-save-as")) {
			File file = (File) ArrayUtils.get(args, 0, DataTypes.FILE);
			saveAsProject(file);
		}

	}

	private void createEmptyProject() {
		setProject(ProjectManager.getInstance().createProject());
		p.setName(PluginServices.getText(this, "untitled"));
		p.setModified(false);
		PluginServices.getMainFrame().setTitle(
				PluginServices.getText(this, "sin_titulo"));
		setProject(p);
		if (1 == 1) {
			createEmptyView();
		} else {
			showProjectWindow();			
		}
	}

	/**
	 * @see com.iver.mdiApp.plugins.IExtension#postInitialize()
	 */
	@Override
	public void postInitialize() {
		try {
			if (!SwingUtilities.isEventDispatchThread()) {
				SwingUtilities.invokeAndWait(new Runnable() {
					@Override
					public void run() {
						createEmptyProject();
					}
				});
			} else {
				createEmptyProject();
			}
		} catch (Exception e) {
			LOG.warn("Can't load initial project.", e);
		}

		
	}
	
	private void createEmptyView() {
		Document viewDoc = p.createDocument(ViewManager.TYPENAME);
		p.addDocument(viewDoc);
		IWindow window = viewDoc.getFactory().getMainWindow(viewDoc);
		window.getWindowInfo().setMaximized(true);
		PluginServices.getMDIManager().addWindow(window);
	}

	/**
	 * Escribe el proyecto en XML.
	 *
	 * @param file
	 *            Fichero.
	 * @param p
	 *            Proyecto.
	 */
	public boolean writeProject(File file, Project p) {
		return writeProject(file, p, true);
	}

	/**
	 * Escribe el proyecto en XML. Pero permite decidir si se pide confirmación
	 * para sobreescribir
	 *
	 * @param file
	 *            Fichero.
	 * @param p
	 *            Proyecto.
	 * @param askConfirmation
	 *            boolean
	 */
	public boolean writeProject(File file, Project p, boolean askConfirmation) {
		if (askConfirmation && file.exists()) {
			int resp = JOptionPane.showConfirmDialog((Component) PluginServices
					.getMainFrame(), PluginServices.getText(this,
							"fichero_ya_existe_seguro_desea_guardarlo"), PluginServices
							.getText(this, "guardar"), JOptionPane.YES_NO_OPTION);
			if (resp != JOptionPane.YES_OPTION) {
				return false;
			}
		}
		NotificationManager.addInfo(PluginServices.getText(this,
				"writing_project") + ": " + file.getName());

		// write it out as XML
		try {
			fireBeforeSavingFileEvent(new SaveEvent(this,
					SaveEvent.BEFORE_SAVING, file));
			p.saveState(file);
			fireAfterSavingFileEvent(new SaveEvent(this,
					SaveEvent.AFTER_SAVING, file));

			PluginServices.getMainFrame().setTitle(file.getName());
			setPath(file.toString());

		} catch (PersistenceException e) {
			String messagestack = e.getLocalizedMessageStack();
			NotificationManager.addError(
					PluginServices.getText(this, "error_writing_project")
					+ ": " + file.getName() + "\n" + messagestack, e);
			return false;
		} catch (Exception e) {
			NotificationManager.addError(
					PluginServices.getText(this, "error_writing_project")
					+ ": " + file.getName(), e);
			return false;
		}
		NotificationManager.addInfo(PluginServices.getText(this,
				"wrote_project") + ": " + file.getName());
		return true;
	}

	public Project readProject(String path) {
		Project project = ProjectManager.getInstance().createProject();

		project.loadState(new File(path));
		return project;
	}

	/**
	 * Lee del XML el proyecto.<br>
	 * <br>
	 *
	 * Reads the XML of the project.<br>
	 * It returns a project object holding all needed info that is not linked to
	 * the Project Dialog. <br>
	 * In case you want the project to be linked to the window you must set this
	 * object to the extension:<br>
	 *
	 * <b>Example:</b><br>
	 *
	 * ...<br>
	 * ...<br>
	 * Project p = ProjectExtension.readProject(projectFile);<br>
	 * ProjectExtension.setProject(p); ...<br>
	 * ...<br>
	 *
	 * @param file
	 *            Fichero.
	 *
	 * @return Project
	 *
	 */
	public Project readProject(File file) {
		Project project = ProjectManager.getInstance().createProject();

		project.loadState(file);
		Set<String> unloadedObjects = project.getUnloadedObjects();
		if (unloadedObjects != null && !unloadedObjects.isEmpty()) {
			StringBuilder builder = new StringBuilder();
			builder.append("Unloaded elements loading the project:\n");
			Iterator<String> it = unloadedObjects.iterator();
			while (it.hasNext()) {
				builder.append("\t");
				builder.append(it.next());
				builder.append("\n");
			}

			LOG.warn(builder.toString());

			ApplicationManager application = ApplicationLocator.getManager();
			I18nManager i18nManager = ToolsLocator.getI18nManager();

			application
					.messageDialog(
							i18nManager
									.getTranslation("_some_project_elements_could_not_be_loaded")
									+ "\n"
									+ i18nManager
											.getTranslation("_maybe_you_need_to_install_any_plugins")
									+ "\n\n"
									+ i18nManager
											.getTranslation("_see_error_log_for_more_information"),
							i18nManager.getTranslation("warning"),
							JOptionPane.WARNING_MESSAGE);

		}
		return project;
	}

	/**
	 * Devuelve el proyecto.
	 *
	 * @return Proyecto.
	 */
	public Project getProject() {
		return p;
	}

	/**
	 * @see org.gvsig.andami.plugins.IExtension#isEnabled()
	 */
	@Override
	public boolean isEnabled() {
		return true;
	}

	/**
	 * @see org.gvsig.andami.plugins.IExtension#isVisible()
	 */
	@Override
	public boolean isVisible() {
		return true;
	}

	/**
	 * Sets the project
	 *
	 * @param p
	 */
	public void setProject(Project p) {
		getProjectFrame().setProject(p);
		this.p = p;
	}

	private void registerDocuments() {
		ViewManager.register();
	}

	private void initializeDocumentActionsExtensionPoint() {
		ExtensionPointManager epMan = ToolsLocator.getExtensionPointManager();
		epMan.add(
				"DocumentActions_View",
				"Context menu options of the view document list"
						+ " in the project window "
						+ "(register instances of "
						+ "org.gvsig.app.project.AbstractDocumentContextMenuAction)");
	}

	public static String getPath() {
		return projectPath;
	}

	public static void setPath(String path) {
		projectPath = path;
	}

	public IWindow getProjectWindow() {
		return getProjectFrame();
	}

	@Override
	public IExtensionStatus getStatus() {
		return this;
	}

	@Override
	public boolean hasUnsavedData() {
		return p.hasChanged();
	}

	@Override
	public IUnsavedData[] getUnsavedData() {
		if (hasUnsavedData()) {
			UnsavedProject data = new UnsavedProject(this);
			IUnsavedData[] dataArray = { data };
			return dataArray;
		} else {
			return null;
		}
	}

	/**
	 * Implements the IUnsavedData interface to show unsaved projects in the
	 * Unsavad Data dialog.
	 *
	 * @author Cesar Martinez Izquierdo <cesar.martinez@iver.es>
	 */
	public class UnsavedProject extends UnsavedData {

		public UnsavedProject(IExtension extension) {
			super(extension);
		}

		@Override
		public String getDescription() {
			if (getPath() == null) {
				return PluginServices.getText(ProjectExtension.this,
						"Unnamed_new_gvsig_project_");
			} else {
				return PluginServices.getText(ProjectExtension.this,
						"Modified_project_");
			}
		}

		@Override
		public String getResourceName() {
			if (getPath() == null) {
				return PluginServices.getText(ProjectExtension.this, "Unnamed");
			} else {
				return getPath();
			}

		}

		@Override
		public boolean saveData() {
			return saveProject();
		}

		@Override
		public String getIcon() {
			return "project-icon";
		}
	}

	@Override
	public IMonitorableTask[] getRunningProcesses() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasRunningProcesses() {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Adds the specified before saving listener to receive
	 * "before saving file events" from this component. If l is null, no
	 * exception is thrown and no action is performed.
	 *
	 * @author Pablo Piqueras Bartolomé <pablo.piqueras@iver.es>
	 *
	 * @param l
	 *            the before saving listener.
	 * @see SaveEvent
	 * @see BeforeSavingListener
	 * @see #removeListener(BeforeSavingListener)
	 * @see #getBeforeSavingListeners
	 */
	public synchronized void addListener(BeforeSavingListener l) {
		if (l == null) {
			return;
		}
		if (!this.beforeSavingListeners.contains(l)) {
			this.beforeSavingListeners.add(l);
		}
	}

	/**
	 * Adds the specified after saving listener to receive
	 * "after saving file events" from this component. If l is null, no
	 * exception is thrown and no action is performed.
	 *
	 * @author Pablo Piqueras Bartolomé <pablo.piqueras@iver.es>
	 *
	 * @param l
	 *            the after saving listener.
	 * @see SaveEvent
	 * @see AfterSavingListener
	 * @see #removeListener(AfterSavingListener)
	 * @see #getAfterSavingListeners()
	 */
	public synchronized void addListener(AfterSavingListener l) {
		if (l == null) {
			return;
		}

		if (!this.afterSavingListeners.contains(l)) {
			this.afterSavingListeners.add(l);
		}

	}

	/**
	 * Returns an array of all the before saving listeners registered on this
	 * component.
	 *
	 * @author Pablo Piqueras Bartolomé <pablo.piqueras@iver.es>
	 *
	 * @return all of this component's <code>BeforeSavingListener</code>s or an
	 *         empty array if no key listeners are currently registered
	 *
	 * @see #addBeforeSavingListener(BeforeSavingListener)
	 * @see #removeBeforeSavingListener(BeforeSavingListener)
	 */
	public synchronized BeforeSavingListener[] getBeforeSavingListeners() {
		return this.beforeSavingListeners
				.toArray(new BeforeSavingListener[] {});
	}

	/**
	 * Returns an array of all the after saving listeners registered on this
	 * component.
	 *
	 * @author Pablo Piqueras Bartolomé <pablo.piqueras@iver.es>
	 *
	 * @return all of this component's <code>AfterSavingListener</code>s or an
	 *         empty array if no key listeners are currently registered
	 *
	 * @see #addAfterSavingListener(AfterSavingListener)
	 * @see #removeAfterSavingListener
	 */
	public synchronized AfterSavingListener[] getAfterSavingListeners() {
		return this.afterSavingListeners.toArray(new AfterSavingListener[] {});

	}

	/**
	 * Removes the specified before saving listener so that it no longer
	 * receives save file events from this component. This method performs no
	 * function, nor does it throw an exception, if the listener specified by
	 * the argument was not previously added to this component. If listener
	 * <code>l</code> is <code>null</code>, no exception is thrown and no action
	 * is performed.
	 *
	 * @author Pablo Piqueras Bartolomé <pablo.piqueras@iver.es>
	 *
	 * @param l
	 *            the before saving listener
	 * @see SaveEvent
	 * @see BeforeSavingListener
	 * @see #addListener(BeforeSavingListener)
	 * @see #getBeforeSavingListeners()
	 */
	public synchronized void removeListener(BeforeSavingListener l) {
		if (l == null) {
			return;
		}

		this.beforeSavingListeners.remove(l);
	}

	/**
	 * Removes the specified after saving listener so that it no longer receives
	 * save file events from this component. This method performs no function,
	 * nor does it throw an exception, if the listener specified by the argument
	 * was not previously added to this component. If listener <code>l</code> is
	 * <code>null</code>, no exception is thrown and no action is performed.
	 *
	 * @author Pablo Piqueras Bartolomé <pablo.piqueras@iver.es>
	 *
	 * @param l
	 *            the after saving listener
	 * @see SaveEvent
	 * @see AfterSavingListener
	 * @see #addListener(AfterSavingListener)
	 * @see #getAfterSavingListeners()
	 */
	public synchronized void removeListener(AfterSavingListener l) {
		if (l == null) {
			return;
		}

		this.afterSavingListeners.remove(l);
	}

	/**
	 * Reports a before saving file event.
	 *
	 * @author Pablo Piqueras Bartolomé <pablo.piqueras@iver.es>
	 *
	 * @param evt
	 *            the before saving file event
	 */
	protected void fireBeforeSavingFileEvent(SaveEvent evt) {
		if ((evt.getID() != SaveEvent.BEFORE_SAVING) || (evt.getFile() == null)) {
			return;
		}

		Iterator<BeforeSavingListener> iter = this.beforeSavingListeners
				.iterator();

		while (iter.hasNext()) {
			iter.next().beforeSaving(evt);
		}
	}

	/**
	 * Reports a after saving file event.
	 *
	 * @author Pablo Piqueras Bartolomé <pablo.piqueras@iver.es>
	 *
	 * @param evt
	 *            the after saving file event
	 */
	protected void fireAfterSavingFileEvent(SaveEvent evt) {
		if ((evt.getID() != SaveEvent.AFTER_SAVING) || (evt.getFile() == null)) {
			return;
		}
		Iterator<AfterSavingListener> iter = this.afterSavingListeners
				.iterator();

		while (iter.hasNext()) {
			iter.next().afterSaving(evt);
		}

	}
}
