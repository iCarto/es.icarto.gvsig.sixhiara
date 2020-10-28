package es.icarto.gvsig.sixhiara.forms;

import es.icarto.gvsig.navtableforms.gui.tables.AbstractSubForm;

@SuppressWarnings("serial")
public class FontesCaracHidroSubForm extends AbstractSubForm {

	public static final String TABLENAME = "fontes_carac_hidro";
	public static String[] colNames = { "cadastro", "data_lit", "tipo_cama", "tipo_aqui", "q_aquifer", "conductiv" };
	public static String[] colAlias = { "Cadastro", "Data ensaio", "Tipo Camada", "Tipo Aquífero", "Caudal(m3/s)",
			"Conductividade(µS/cm)" };

	@Override
	protected String getBasicName() {
		return TABLENAME;
	}

}
