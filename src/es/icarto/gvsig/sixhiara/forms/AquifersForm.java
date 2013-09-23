package es.icarto.gvsig.sixhiara.forms;

import java.io.InputStream;

import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.jeta.forms.components.panel.FormPanel;
import com.jeta.forms.gui.common.FormException;

import es.icarto.gvsig.navtableforms.AbstractForm;

@SuppressWarnings("serial")
public class AquifersForm extends AbstractForm {

    public static final String LAYERNAME = "Acuiferos";
    public static final String PKFIELD = "cod_acuif";
    public static final String ABEILLE = "ui/aquifers.xml";
    public static final String METADATA = "metadata/aquifers.xml";

    public AquifersForm(FLyrVect layer) {
	super(layer);
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
    public String getXMLPath() {
	return this.getClass().getClassLoader().getResource(METADATA).getPath();
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
