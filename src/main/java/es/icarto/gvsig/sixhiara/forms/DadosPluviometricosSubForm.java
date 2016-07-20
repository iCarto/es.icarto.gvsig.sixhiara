package es.icarto.gvsig.sixhiara.forms;

import es.icarto.gvsig.navtableforms.gui.tables.AbstractSubForm;

@SuppressWarnings("serial")
public class DadosPluviometricosSubForm extends AbstractSubForm {

    public static final String TABLENAME = "dados_pluviometricos";
    public static String[] colNames = { "cod_estac", "ano", "d_chu_tot", "c_med_ano", "c_max_ano" };
    public static String[] colAlias = { "Cod Esta�on", "Ano", "N� d�as chuva", "Media diaria", "M�xima chuva" };

    @Override
    protected void fillSpecificValues() {

    }

    @Override
    protected String getBasicName() {
	return TABLENAME;
    }

}
