package es.icarto.gvsig.sixhiara.forms;

import es.icarto.gvsig.navtableforms.gui.tables.AbstractSubForm;

@SuppressWarnings("serial")
public class EstacoesAnaliseSubForm extends AbstractSubForm {

	public static final String TABLENAME = "estacoes_analise";
	public static String[] colNames = { "cod_estac", "data_med", "ph", "nitritos", "conductiv" };
	public static String[] colAlias = { "Cod Estacoe", "Data medi��o", "PH", "Nitritos", "Conductividade" };

	@Override
	protected String getBasicName() {
		return TABLENAME;
	}

}
