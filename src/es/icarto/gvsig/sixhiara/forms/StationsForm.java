package es.icarto.gvsig.sixhiara.forms;

import java.io.InputStream;

import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.jeta.forms.components.panel.FormPanel;
import com.jeta.forms.gui.common.FormException;

import es.icarto.gvsig.navtableforms.AbstractForm;
import es.icarto.gvsig.navtableforms.gui.tables.handler.AlphanumericTableHandler;

@SuppressWarnings("serial")
public class StationsForm extends AbstractForm {

    public StationsForm(FLyrVect layer) {
	super(layer);
	addTableHandler(new AlphanumericTableHandler(
		StationsPluviometricDataSubForm.TABLENAME,
		getWidgetComponents(), PKFIELD,
		StationsPluviometricDataSubForm.colNames,
		StationsPluviometricDataSubForm.colAlias));
	addTableHandler(new AlphanumericTableHandler(
		StationsHydrometricDataSubForm.TABLENAME,
		getWidgetComponents(), PKFIELD,
		StationsHydrometricDataSubForm.colNames,
		StationsHydrometricDataSubForm.colAlias));
    }

    public static final String LAYERNAME = "Estacoes";
    public static final String PKFIELD = "cod_estac";
    public static final String ABEILLE = "ui/stations.xml";
    public static final String METADATA = "metadata/stations.xml";

    @Override
    public String getXMLPath() {
	return this.getClass().getClassLoader().getResource(METADATA).getPath();
    }

    @Override
    public FormPanel getFormBody() {
	if (formBody == null) {
	    InputStream stream = getClass().getClassLoader()
		    .getResourceAsStream(ABEILLE);
	    try {
		formBody = new FormPanel(stream);
	    } catch (FormException e) {
		e.printStackTrace();
	    }
	}
	return formBody;
    }

    @Override
    protected void fillSpecificValues() {
	super.fillSpecificValues();
    }

    @Override
    protected String getPrimaryKeyValue() {
	return getFormController().getValue(PKFIELD);
    }

}
