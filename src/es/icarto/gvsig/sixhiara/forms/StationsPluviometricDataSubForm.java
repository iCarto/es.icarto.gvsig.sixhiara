package es.icarto.gvsig.sixhiara.forms;

import es.icarto.gvsig.navtableforms.gui.tables.AbstractSubForm;

@SuppressWarnings("serial")
public class StationsPluviometricDataSubForm extends AbstractSubForm {

    public static final String TABLENAME = "dados_pluviometricos";
    public static String[] colNames = { "COD_ESTAC", "ANO", "D_CHU_TOT",
	    "C_MED_ANO", "C_MAX_ANO" };
    public static String[] colAlias = { "Cod Estaçon", "Ano", "Nº días chuva",
	    "Media diaria", "Máxima chuva" };

    @Override
    protected void fillSpecificValues() {

    }

    @Override
    protected String getBasicName() {
	return TABLENAME;
    }

}
