package es.icarto.gvsig.sixhiara.forms;

import es.icarto.gvsig.navtableforms.gui.tables.AbstractSubForm;

@SuppressWarnings("serial")
public class QuantidadeAguaSubForm extends AbstractSubForm {

	public static final String TABLENAME = "quantidade_agua";
	public static String[] colNames = { "cadastro", "data", "hora",
			"quan_agua" };
	public static String[] colAlias = { "Cadastro", "Data", "Hora",
			"Quantidade agua" };
	
	public QuantidadeAguaSubForm() {
		addCalculation(new QuantidadeAguaCalculateQuanAgua(this));
	}

	@Override
	protected String getBasicName() {
		return TABLENAME;
	}

}
