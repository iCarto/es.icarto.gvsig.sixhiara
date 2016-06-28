package es.icarto.gvsig.sixhiara.forms;

import es.icarto.gvsig.navtableforms.gui.tables.AbstractSubForm;

@SuppressWarnings("serial")
public class StationsHydrometricDataSubForm extends AbstractSubForm {

    public static final String TABLENAME = "dados_hidrometricos";
    public static String[] colNames = { "COD_ESTAC", "ANO", "N_MED_ANO",
	    "Q_MED_ANO", "E_MED_ANO" };
    public static String[] colAlias = { "Cod Estaçon", "Ano", "Nivel medio",
	    "Caudal medio", "Escoamento medio" };

    @Override
    protected void fillSpecificValues() {

    }

    @Override
    protected String getBasicName() {
	return TABLENAME;
    }

}
