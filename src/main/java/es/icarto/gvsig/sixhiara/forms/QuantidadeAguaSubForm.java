package es.icarto.gvsig.sixhiara.forms;

import es.icarto.gvsig.navtableforms.gui.tables.AbstractSubForm;

@SuppressWarnings("serial")
public class QuantidadeAguaSubForm extends AbstractSubForm {

	public static final String TABLENAME = "quantidade_agua";
	public static String[] colNames = { "data_ens", "hora_ens", "quan_agua" };
	public static String[] colAlias = { "Data", "Hora", "Quantidade agua" };

	public QuantidadeAguaSubForm() {
		addCalculation(new QuantidadeAguaCalculateQuanAgua(this));
	}

	@Override
	protected String getBasicName() {
		return TABLENAME;
	}

}
