package es.icarto.gvsig.sixhiara;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JOptionPane;

import org.cresques.cts.ICoordTrans;
import org.gvsig.andami.PluginServices;
import org.gvsig.fmap.dal.exception.DataException;
import org.gvsig.fmap.dal.feature.EditableFeature;
import org.gvsig.fmap.dal.feature.Feature;
import org.gvsig.fmap.dal.feature.FeatureAttributeDescriptor;
import org.gvsig.fmap.dal.feature.FeatureSet;
import org.gvsig.fmap.dal.feature.FeatureStore;
import org.gvsig.fmap.dal.feature.FeatureType;
import org.gvsig.fmap.geom.Geometry;
import org.gvsig.fmap.mapcontext.layers.vectorial.FLyrVect;
import org.gvsig.tools.dispose.DisposableIterator;
import org.gvsig.tools.dispose.DisposeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.icarto.gvsig.commons.AbstractExtension;
import es.icarto.gvsig.commons.datasources.SHPFactory;
import es.icarto.gvsig.commons.gui.AbstractIWindow;
import es.icarto.gvsig.commons.gui.FileChooser;
import es.icarto.gvsig.commons.gui.OkCancelPanel;
import es.icarto.gvsig.commons.gui.WidgetFactory;
import es.icarto.gvsig.commons.utils.Unzip;
import es.icarto.gvsig.navtableforms.utils.TOCLayerManager;

public class ImportExploracoesExtension extends AbstractExtension {

	private static final Logger logger = LoggerFactory
			.getLogger(ImportExploracoesExtension.class);

	private FLyrVect layer;

	@Override
	public void execute(String actionCommand) {
		final Component mainFrame = (Component) PluginServices.getMainFrame();
		ChooseFile chooseFile = new ChooseFile();
		chooseFile.openDialog();
		File zipFile = chooseFile.getFile();
		if (zipFile == null) {
			return;
		}
		try {
			// TODO: Usar funcionalidades de gvSIG. https://redmine.gvsig.net/redmine/issues/4327
			String tmpDir = System.getProperty("java.io.tmpdir");
			Unzip.unzip(zipFile, new File(tmpDir));
			// Comprobar que tiene el formato correcto

			long nfeats = doImport(tmpDir, layer);

			String msg = String.format("Importadas %d explorações", nfeats);
			layer.reload();
			JOptionPane.showMessageDialog(mainFrame, msg);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			JOptionPane.showMessageDialog(mainFrame, "Erro importando dados",
					"", JOptionPane.ERROR_MESSAGE);
		}
	}

	private long doImport(String tmpDir, FLyrVect layer) throws Exception {
		File shpFile = new File(tmpDir + File.separator + "exploracoes"
				+ File.separator + "exploracoes.shp");
		FeatureStore shpStore = SHPFactory
				.getFeatureStore(shpFile, "EPSG:4326");
		FeatureSet shpSet = null;
		DisposableIterator shpIt = null;
		long nfeats = 0;
		FeatureStore dbStore = layer.getFeatureStore();
		try {
			dbStore.edit();
			deleteAll(dbStore);
			shpSet = shpStore.getFeatureSet();
			shpIt = shpSet.fastIterator();
			while (shpIt.hasNext()) {
				Feature shpFeat = (Feature) shpIt.next();
				EditableFeature dbFeat = createNewFeature(dbStore, shpFeat);
				dbStore.insert(dbFeat);
				nfeats++;
			}
			dbStore.finishEditing();
		} catch (Exception e) {
			dbStore.cancelEditing();
			throw e;
		} finally {
			DisposeUtils.disposeQuietly(shpIt);
			DisposeUtils.disposeQuietly(shpSet);
			DisposeUtils.disposeQuietly(shpStore);
		}
		return nfeats;
	}

	private EditableFeature createNewFeature(FeatureStore shpStore,
			Feature srcFeat) throws DataException {
		FeatureType srcType = srcFeat.getType();

		EditableFeature targetFeat = shpStore.createNewFeature();
		FeatureType targetType = targetFeat.getType();
		FeatureAttributeDescriptor[] atts = targetType
				.getAttributeDescriptors();
		ICoordTrans ct = srcType.getDefaultSRS().getCT(layer.getProjection());
		for (int i = 0; i < atts.length; i++) {
			int attType = atts[i].getType();
			if (attType == org.gvsig.fmap.geom.DataTypes.GEOMETRY) {
				Geometry srcGeom = srcFeat.getDefaultGeometry();
				srcGeom.reProject(ct);
				targetFeat.setDefaultGeometry(srcGeom);
			} else {
				String attName = atts[i].getName();
				if (srcType.get(attName) == null) {
					continue;
				}
				Object val = srcFeat.get(attName);
				if (val == null) {
					continue;
				}
				targetFeat.set(attName, val);
			}
		}
		return targetFeat;
	}

	@Override
	public boolean isEnabled() {
		layer = new TOCLayerManager().getLayerByName("exploracoes");
		return layer != null;
	}

	private void deleteAll(FeatureStore store) throws DataException {
		FeatureSet set = null;
		DisposableIterator iterator = null;
		try {
			set = store.getFeatureSet();
			iterator = set.fastIterator();
			while (iterator.hasNext()) {
				Feature feature = (Feature) iterator.next();
				set.delete(feature);
				// iterator.remove(); // o set.delete(feature)
			}
		} finally {
			DisposeUtils.disposeQuietly(iterator);
			DisposeUtils.disposeQuietly(set);
		}
	}

	@SuppressWarnings("serial")
	private final class ChooseFile extends AbstractIWindow implements
			ActionListener {

		private FileChooser chooser;
		private File file;
		private OkCancelPanel ok;

		public ChooseFile() {
			String initFile = System.getProperty("user.home");
			ok = WidgetFactory.okCancelPanel(this, this, this);
			chooser = new FileChooser(this, "Escolha o zip coas explorações",
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
			file = null;
			if (e.getActionCommand() == OkCancelPanel.OK_ACTION_COMMAND) {
				if (chooser.isValidAndExist()) {
					file = chooser.getFile();
				}

			}
			closeDialog();
		}

		public File getFile() {
			return file;
		}
	}

}
