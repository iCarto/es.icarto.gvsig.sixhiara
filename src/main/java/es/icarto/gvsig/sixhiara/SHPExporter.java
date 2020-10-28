package es.icarto.gvsig.sixhiara;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
import org.gvsig.tools.ToolsLocator;
import org.gvsig.tools.dispose.DisposableIterator;
import org.gvsig.tools.dispose.DisposeUtils;
import org.gvsig.tools.exception.BaseException;
import org.gvsig.tools.folders.FoldersManager;

import es.icarto.gvsig.commons.datasources.SHPFactory;
import es.icarto.gvsig.commons.utils.FileNameUtils;
import es.icarto.gvsig.commons.utils.Zip;

public class SHPExporter {

	private final FLyrVect layer;
	private String epsg;
	private List<String> acceptedFields = new ArrayList<String>();

	public SHPExporter(FLyrVect layer) {
		this.layer = layer;
	}

	public void execute(String outputFolder) throws Exception {
		outputFolder = outputFolder.endsWith(File.separator) ? outputFolder : outputFolder + File.separator;

		FoldersManager manager = ToolsLocator.getFoldersManager();
		File tmpDir = manager.getTemporaryFolder();
		String filePath = tmpDir.getAbsolutePath() + File.separator + layer.getName() + ".shp";
		File file = new File(filePath);
		exportTo(file);
//		createPRJ(filePath);
		String zipFile = outputFolder + layer.getName() + ".zip";
		zipTo(file, zipFile);
	}

	// TODO: Esto parece arreglado. Eliminar
//	private void createPRJ(String filePath) throws IOException {
//		String out = FileNameUtils.replaceExtension(filePath, ".prj");
//		URL url = this.getClass().getResource("/assets/fontes.prj");
//		FileUtils.copyFile(new File(url.getPath()), new File(out));
//	}

	public void setEPSG(String epsg) {
		/**
		 * When set will reproject the exported shape
		 */
		this.epsg = epsg;
	}

	public void setAcceptedFields(List<String> acceptedFields) {
		/**
		 * If set, only the fields in this list will be exported
		 */
		this.acceptedFields = acceptedFields;
	}

	public void exportTo(File file) throws BaseException {
		FeatureStore shpStore = null;
		FeatureSet dbSet = null;
		DisposableIterator dbIt = null;
		try {
			EditableFeatureType targetType = getTargetType(layer);
			String crs = this.epsg != null ? this.epsg : layer.getProjection().getAbrev();
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

	private EditableFeatureType getTargetType(FLyrVect layer) throws DataException {

		FeatureType srcType = layer.getFeatureStore().getDefaultFeatureType();
		DataManager manager = DALLocator.getDataManager();
		EditableFeatureType targetType = manager.createFeatureType();
		Iterator<FeatureAttributeDescriptor> it = srcType.iterator();
		while (it.hasNext()) {
			FeatureAttributeDescriptor attDesc = it.next();
			String attName = attDesc.getName();
			if (!acceptedFields.isEmpty() && !acceptedFields.contains(attName)) {
				continue;
			}
			int attType = attDesc.getType();
			if (attType == org.gvsig.fmap.geom.DataTypes.GEOMETRY) {
				GeometryType geomType = attDesc.getGeomType();
				EditableFeatureAttributeDescriptor add = targetType.add("geometry", attType);
				add.setGeometryType(geomType);
				targetType.setDefaultGeometryAttributeName("geometry");
			} else {
//				String newName = attName;
//				if (attName.length() > 9) {
//					newName = attName.substring(0, 9);
//					int i = 1;
//					while (targetType.get(newName) != null) {
//						newName = attName.substring(0, 7) + "_" + i;
//						i += 1;
//					}
//				}
				if (attName.length() > 9) {
					continue;
				}
				EditableFeatureAttributeDescriptor add = targetType.add(attName, attType);
				add.setSize(attDesc.getSize());
				add.setPrecision(attDesc.getPrecision());
			}

		}
		return targetType;
	}

	private EditableFeature createNewFeature(FeatureStore shpStore, Feature srcFeat) throws DataException {
		EditableFeature targetFeat = shpStore.createNewFeature();
		FeatureType targetType = targetFeat.getType();
		FeatureAttributeDescriptor[] atts = targetType.getAttributeDescriptors();
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
