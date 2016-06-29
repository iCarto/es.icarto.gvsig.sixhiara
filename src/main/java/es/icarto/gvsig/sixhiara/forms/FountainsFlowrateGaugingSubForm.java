package es.icarto.gvsig.sixhiara.forms;

import es.icarto.gvsig.navtableforms.gui.tables.AbstractSubForm;

@SuppressWarnings("serial")
public class FountainsFlowrateGaugingSubForm extends AbstractSubForm {

    public static final String TABLENAME = "quantidade_agua";
    public static String[] colNames = { "cod_fonte", "data", "hora", "quan_agua", "q_extraer" };
    public static String[] colAlias = { "Cod Fonte", "Data", "Hora", "Quantidade agua", "Caudal extra�do" };

    @Override
    protected void fillSpecificValues() {

    }

    @Override
    protected String getBasicName() {
	return TABLENAME;
    }

}
