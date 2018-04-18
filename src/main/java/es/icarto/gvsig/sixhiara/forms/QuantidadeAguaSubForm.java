package es.icarto.gvsig.sixhiara.forms;

import es.icarto.gvsig.navtableforms.gui.tables.AbstractSubForm;

@SuppressWarnings("serial")
public class QuantidadeAguaSubForm extends AbstractSubForm {

	public static final String TABLENAME = "quantidade_agua";
	public static String[] colNames = { "cod_fonte", "data", "hora",
			"quan_agua" };
	public static String[] colAlias = { "Cod Fonte", "Data", "Hora",
			"Quantidade agua" };
	
	public QuantidadeAguaSubForm() {
		addCalculation(new QuantidadeAguaCalculateQuanAgua(this));
	}

	@Override
	protected String getBasicName() {
		return TABLENAME;
	}

}
