package es.icarto.gvsig.sixhiara;

import org.gvsig.fmap.mapcontext.layers.vectorial.FLyrVect;

import es.icarto.gvsig.navtableforms.utils.TOCLayerManager;

public class ExportToUtentesExtension extends BaseExportExtension {

	@Override
	public boolean isEnabled() {
		TOCLayerManager tocLayerManager = new TOCLayerManager();
		FLyrVect[] activeLayers = tocLayerManager.getActiveLayers();
		return activeLayers.length == 1;
	}



	@Override
	protected SHPExporter getExporter() {
		FLyrVect layer = new TOCLayerManager().getActiveLayer();
		SHPExporter exporter = new SHPExporter(layer);
		exporter.setEPSG("EPSG:4326");
		return exporter;
	}


}
