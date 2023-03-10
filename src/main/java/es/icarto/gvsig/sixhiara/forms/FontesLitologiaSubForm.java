package es.icarto.gvsig.sixhiara.forms;

import es.icarto.gvsig.navtableforms.gui.tables.AbstractSubForm;

@SuppressWarnings("serial")
public class FontesLitologiaSubForm extends AbstractSubForm {

	public static final String TABLENAME = "fontes_litologia";
	public static String[] colNames = { "data_lit", "camada", "litologia", "profundid", "carac_lit" };
	public static String[] colAlias = { "Data Ensaio", "Camada", "Litologia", "Profundidade (m)",
			"CaracterÝstica" };

	@Override
	protected String getBasicName() {
		return TABLENAME;
	}

}
