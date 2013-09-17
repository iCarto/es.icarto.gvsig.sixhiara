package es.icarto.gvsig.sixhiara.forms;

import java.io.InputStream;

import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.jeta.forms.components.panel.FormPanel;
import com.jeta.forms.gui.common.FormException;

import es.icarto.gvsig.navtableforms.AbstractForm;

@SuppressWarnings("serial")
public class FountainsForm extends AbstractForm {

    public static final String LAYERNAME = "Fontes";
    public static final String ABEILLE = "forms/fountains.xml";
    public static final String METADATA = "forms_metadata/fountains.xml";

    public FountainsForm(FLyrVect layer) {
	super(layer);
    }

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
}
