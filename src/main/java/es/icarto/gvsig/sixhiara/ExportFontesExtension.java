package es.icarto.gvsig.sixhiara;

import java.util.Arrays;

import org.gvsig.fmap.mapcontext.layers.vectorial.FLyrVect;

import es.icarto.gvsig.navtableforms.utils.TOCLayerManager;
import es.icarto.gvsig.sixhiara.forms.FontesForm;

public class ExportFontesExtension extends BaseExportExtension {

	private FLyrVect layer;

	@Override
	public boolean isEnabled() {
		layer = new TOCLayerManager().getLayerByName(FontesForm.LAYERNAME);
		return layer != null;
	}

	@Override
	protected SHPExporter getExporter() {
		SHPExporter exporter = new SHPExporter(layer);
		exporter.setAcceptedFields(Arrays.asList("geom", "red_monit"));
		return exporter;
	}

}
