package es.icarto.gvsig.sixhiara.forms;

import es.icarto.gvsig.navtableforms.gui.tables.AbstractSubForm;


@SuppressWarnings("serial")
public class AnaliseSubForm extends AbstractSubForm {

    public static final String TABLENAME = "analise";
    public static String[] colNames = { "cod_fonte", "data_most", "ph", "oxigeno_d", "colit_tot" };
    public static String[] colAlias = { "Cod Fonte", "Fecha Muestra", "PH", "OD", "Coliformes Totais" };

    @Override
    protected void fillSpecificValues() {
    }

    @Override
    protected String getBasicName() {
	return TABLENAME;
    }

}
