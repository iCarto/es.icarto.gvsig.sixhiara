package es.icarto.gvsig.sixhiara.forms;

import es.icarto.gvsig.navtableforms.gui.tables.AbstractSubForm;

@SuppressWarnings("serial")
public class FountainsAnalyticalSubForm extends AbstractSubForm {

    public static final String TABLENAME = "analiticas";
    public static String[] colNames = { "cod_fonte", "data_most", "ph",
	    "oxigeno_d", "coli_tot" };
    public static String[] colAlias = { "Cod Fonte", "Fecha Muestra", "PH",
	    "OD", "Coliformes Totales" };

    @Override
    protected void fillSpecificValues() {
    }

    @Override
    protected String getBasicName() {
	return TABLENAME;
    }

}
