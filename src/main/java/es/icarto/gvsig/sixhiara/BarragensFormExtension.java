package es.icarto.gvsig.sixhiara;


import org.gvsig.andami.PluginServices;

import es.icarto.gvsig.sixhiara.forms.BarragensForm;

public class BarragensFormExtension extends AbstractFormExtension {

    @Override
    public void execute(String actionCommand) {
	BarragensForm form = new BarragensForm(getLayer());
	if (form != null && form.init()) {
	    PluginServices.getMDIManager().addWindow(form);
	}
    }

    @Override
    protected String getLayerName() {
	return BarragensForm.LAYERNAME;
    }

}
