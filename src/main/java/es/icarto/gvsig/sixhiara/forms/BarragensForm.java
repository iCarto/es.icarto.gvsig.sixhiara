package es.icarto.gvsig.sixhiara.forms;

import org.gvsig.fmap.mapcontext.layers.vectorial.FLyrVect;

@SuppressWarnings("serial")
public class BarragensForm extends BasicAbstractForm {

	public static final String LAYERNAME = "barragens";
	public static final String PKFIELD = "cod_barra";

	public BarragensForm(FLyrVect layer) {
		super(layer);
		addChained("distrito", "provincia");
		addChained("posto_adm", "distrito");
		addChained("subacia", "bacia");
		addNewFeatureButton();
		addCoordinatesButton();
	}

	@Override
	protected String getSchema() {
		return "inventario";
	}

	@Override
	public String getBasicName() {
		return "barragens";
	}

	@Override
	protected String getPrimaryKey() {
		return PKFIELD;
	}
}
