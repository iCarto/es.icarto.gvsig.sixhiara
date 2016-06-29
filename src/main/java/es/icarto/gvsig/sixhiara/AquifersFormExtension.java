package es.icarto.gvsig.sixhiara;


import org.gvsig.andami.PluginServices;

import es.icarto.gvsig.sixhiara.forms.AquifersForm;

public class AquifersFormExtension extends AbstractFormExtension {

    @Override
    public void execute(String actionCommand) {
	AquifersForm form = new AquifersForm(getLayer());
	if (form != null && form.init()) {
	    PluginServices.getMDIManager().addWindow(form);
	}
    }

    @Override
    protected String getLayerName() {
	return AquifersForm.LAYERNAME;
    }

}
