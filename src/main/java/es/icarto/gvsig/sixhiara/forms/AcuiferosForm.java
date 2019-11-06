package es.icarto.gvsig.sixhiara.forms;

import org.gvsig.fmap.mapcontext.layers.vectorial.FLyrVect;

@SuppressWarnings("serial")
public class AcuiferosForm extends BasicAbstractForm {

	public static final String LAYERNAME = "Acuiferos";
	public static final String PKFIELD = "cod_acuif";

	public AcuiferosForm(FLyrVect layer) {
		super(layer);
	}

	@Override
	protected String getSchema() {
		return "inventario";
	}

	@Override
	public String getBasicName() {
		return "acuiferos";
	}

	@Override
	protected String getPrimaryKey() {
		return PKFIELD;
	}
}
