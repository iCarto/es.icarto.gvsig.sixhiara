package es.icarto.gvsig.sixhiara.forms;

import es.icarto.gvsig.navtableforms.gui.tables.AbstractSubForm;

@SuppressWarnings("serial")
public class FountainsAnalyticalSubForm extends AbstractSubForm {

    public static final String TABLENAME = "analise";
    public static String[] colNames = { "COD_FONTE", "DATA_MOST", "PH",
	    "OXIGENO_D", "COLI_TOT" };
    public static String[] colAlias = { "Cod Fonte", "Fecha Muestra", "PH",
	    "OD", "Coliformes Totais" };

    @Override
    protected void fillSpecificValues() {
    }

    @Override
    protected String getBasicName() {
	return TABLENAME;
    }

}
