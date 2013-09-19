package es.icarto.gvsig.sixhiara.forms;

import java.io.InputStream;

import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.jeta.forms.components.panel.FormPanel;
import com.jeta.forms.gui.common.FormException;

import es.icarto.gvsig.navtableforms.AbstractForm;

@SuppressWarnings("serial")
public class ReservoirsForm extends AbstractForm {

    public static final String LAYERNAME = "Barragems";
    public static final String PKFIELD = "cod_barra";
    public static final String ABEILLE = "ui/reservoirs.xml";
    public static final String METADATA = "metadata/reservoirs.xml";

    public ReservoirsForm(FLyrVect layer) {
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
