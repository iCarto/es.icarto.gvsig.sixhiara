package es.icarto.gvsig.sixhiara.forms;

import es.icarto.gvsig.navtableforms.gui.tables.AbstractSubForm;

@SuppressWarnings("serial")
public class FontesAnaliseSubForm extends AbstractSubForm {

	public static final String TABLENAME = "fontes_analise";
	public static String[] colNames = { "data_most", "ph", "nitritos", "conductiv" };
	public static String[] colAlias = { "Data Mostra", "PH", "Nitritos", "Conductividade" };

	@Override
	protected String getBasicName() {
		return TABLENAME;
	}

}
