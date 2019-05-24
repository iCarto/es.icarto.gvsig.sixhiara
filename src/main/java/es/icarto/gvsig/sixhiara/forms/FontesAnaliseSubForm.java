package es.icarto.gvsig.sixhiara.forms;

import es.icarto.gvsig.navtableforms.gui.tables.AbstractSubForm;

@SuppressWarnings("serial")
public class FontesAnaliseSubForm extends AbstractSubForm {

	public static final String TABLENAME = "fontes_analise";
	public static String[] colNames = { "cadastro", "data_most", "c_ph",
			"c_nitrit", "c_conduct" };
	public static String[] colAlias = { "Cadastro", "Fecha Muestra", "PH",
			"Nitritos", "Conductividade" };

	@Override
	protected String getBasicName() {
		return TABLENAME;
	}

}
