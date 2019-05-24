package es.icarto.gvsig.sixhiara.forms;

import es.icarto.gvsig.navtableforms.gui.tables.AbstractSubForm;

@SuppressWarnings("serial")
public class FontesLitologiaSubForm extends AbstractSubForm {
	
	public static final String TABLENAME = "fontes_litologia";
	public static String[] colNames = { "cadastro", "data_lit", "camada",
			"litologia", "profundid", "carac_lit" };
	public static String[] colAlias = { "Cadastro", "Data Ensadio", "Camada",
			"Litologia", "Profundidade (m)", "Característica" };

	@Override
	protected String getBasicName() {
		return TABLENAME;
	}

}
