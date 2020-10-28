package es.icarto.gvsig.sixhiara;


import org.gvsig.andami.ui.mdiManager.MDIManagerFactory;

import es.icarto.gvsig.sixhiara.forms.FontesForm;

public class FontesFormExtension extends AbstractFormExtension {

    @Override
    public void execute(String actionCommand) {
	FontesForm form = new FontesForm(getLayer());
	if (form != null && form.init()) {
		MDIManagerFactory.getManager().addWindow(form);
	}
    }

    @Override
    protected String getLayerName() {
	return FontesForm.LAYERNAME;
    }

}
