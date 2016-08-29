package es.icarto.gvsig.sixhiara;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.gvsig.fmap.dal.DALLocator;
import org.gvsig.fmap.dal.DataManager;
import org.gvsig.fmap.dal.exception.DataException;
import org.gvsig.fmap.dal.feature.EditableFeature;
import org.gvsig.fmap.dal.feature.EditableFeatureAttributeDescriptor;
import org.gvsig.fmap.dal.feature.EditableFeatureType;
import org.gvsig.fmap.dal.feature.Feature;
import org.gvsig.fmap.dal.feature.FeatureAttributeDescriptor;
import org.gvsig.fmap.dal.feature.FeatureSet;
import org.gvsig.fmap.dal.feature.FeatureStore;
import org.gvsig.fmap.dal.feature.FeatureType;
import org.gvsig.fmap.geom.Geometry;
import org.gvsig.fmap.geom.type.GeometryType;
import org.gvsig.fmap.mapcontext.layers.vectorial.FLyrVect;
import org.gvsig.tools.dispose.DisposableIterator;
import org.gvsig.tools.dispose.DisposeUtils;
import org.gvsig.tools.exception.BaseException;

import es.icarto.gvsig.commons.datasources.SHPFactory;
import es.icarto.gvsig.commons.utils.FileNameUtils;
import es.icarto.gvsig.commons.utils.Zip;

public class ExportFontes {

	private final FLyrVect layer;

	public ExportFontes(FLyrVect layer) {
		this.layer = layer;
	}

	public void execute(String outputFolder) throws Exception {
		String filePath = getFile();
		File file = new File(filePath);
		exportTo(file);
		createPRJ(filePath);

		String zipFile = getZipFile(outputFolder);
		zipTo(file, zipFile);
	}

	private void createPRJ(String filePath) throws IOException {
		String out = FileNameUtils.replaceExtension(filePath, ".prj");
		URL url = this.getClass().getResource("/assets/fontes.prj");
		FileUtils.copyFile(new File(url.getPath()), new File(out));
	}

	private String getZipFile(String outputFolder) {
		if (outputFolder.endsWith(File.separator)) {
			return outputFolder + "fontes.zip";
		} else {
			return outputFolder + File.separator + "fontes.zip";
		}
	}

	public void exportTo(File file) throws BaseException {
		FeatureStore shpStore = null;
		FeatureSet dbSet = null;
		DisposableIterator dbIt = null;
		try {
			EditableFeatureType targetType = getTargetType(layer);
			String crs = layer.getProjection().getAbrev();
			SHPFactory.createSHP(file, targetType, crs);

			shpStore = SHPFactory.getFeatureStore(file, crs);

			shpStore.edit(FeatureStore.MODE_APPEND);
			dbSet = layer.getFeatureStore().getFeatureSet();
			dbIt = dbSet.fastIterator();
			while (dbIt.hasNext()) {
				Feature dbFeat = (Feature) dbIt.next();
				EditableFeature shpFeat = createNewFeature(shpStore, dbFeat);
				shpStore.insert(shpFeat);
			}
			shpStore.finishEditing();
		} finally {
			DisposeUtils.disposeQuietly(shpStore);
			DisposeUtils.disposeQuietly(dbSet);
			DisposeUtils.disposeQuietly(dbIt);
		}

	}

	private void zipTo(File src, String target) throws IOException {
		String filePath = src.getAbsolutePath();
		Zip zip = new Zip();
		zip.addFile(src);
		zip.addFile(new File(FileNameUtils.replaceExtension(filePath, ".shx")));
		zip.addFile(new File(FileNameUtils.replaceExtension(filePath, ".dbf")));
		zip.addFile(new File(FileNameUtils.replaceExtension(filePath, ".prj")));
		zip.zipIt(target);
	}

	private String getFile() {
		String tmpDir = System.getProperty("java.io.tmpdir");
		String filePath = tmpDir + File.separator + "fontes.shp";
		return filePath;
	}

	private EditableFeatureType getTargetType(FLyrVect layer)
			throws DataException {
		final List<String> acceptFields = Arrays.asList("geom", "red_monit");
		FeatureType srcType = layer.getFeatureStore().getDefaultFeatureType();
		DataManager manager = DALLocator.getDataManager();
		EditableFeatureType targetType = manager.createFeatureType();
		Iterator<FeatureAttributeDescriptor> it = srcType.iterator();
		while (it.hasNext()) {
			FeatureAttributeDescriptor attDesc = it.next();
			String attName = attDesc.getName();
			if (!acceptFields.contains(attName)) {
				continue;
			}
			int attType = attDesc.getType();
			if (attType == org.gvsig.fmap.geom.DataTypes.GEOMETRY) {
				GeometryType geomType = attDesc.getGeomType();
				EditableFeatureAttributeDescriptor add = targetType.add(
						attName, attType);
				add.setGeometryType(geomType);
				targetType.setDefaultGeometryAttributeName(attName);
			} else {
				EditableFeatureAttributeDescriptor add = targetType.add(
						attName, attType);
				add.setSize(attDesc.getSize());
				add.setPrecision(attDesc.getPrecision());
			}

		}
		return targetType;
	}

	private EditableFeature createNewFeature(FeatureStore shpStore,
			Feature srcFeat) throws DataException {
		EditableFeature targetFeat = shpStore.createNewFeature();
		FeatureType targetType = targetFeat.getType();
		FeatureAttributeDescriptor[] atts = targetType
				.getAttributeDescriptors();
		for (int i = 0; i < atts.length; i++) {
			int attType = atts[i].getType();
			if (attType == org.gvsig.fmap.geom.DataTypes.GEOMETRY) {
				Geometry srcGeom = srcFeat.getDefaultGeometry();
				targetFeat.setDefaultGeometry(srcGeom);
			} else {
				String attName = atts[i].getName();
				Object val = srcFeat.get(attName);
				if (val == null) {
					continue;
				}
				targetFeat.set(attName, val);
			}
		}
		return targetFeat;
	}
}
