package es.icarto.gvsig.sixhiara.forms;

import es.icarto.gvsig.navtableforms.gui.tables.AbstractSubForm;

@SuppressWarnings("serial")
public class QuantidadeAguaSubForm extends AbstractSubForm {

	public static final String TABLENAME = "quantidade_agua";
	public static String[] colNames = { "cod_fonte", "data", "hora",
			"quan_agua", "q_extraer" };
	public static String[] colAlias = { "Cod Fonte", "Data", "Hora",
			"Quantidade agua", "Caudal extraído" };

	@Override
	protected String getBasicName() {
		return TABLENAME;
	}

}
