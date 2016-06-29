package es.icarto.gvsig.sixhiara;


import org.gvsig.andami.PluginServices;

import es.icarto.gvsig.sixhiara.forms.StationsForm;

public class StationsFormExtension extends AbstractFormExtension {

    @Override
    public void execute(String actionCommand) {
	StationsForm form = new StationsForm(getLayer());
	if (form != null && form.init()) {
	    PluginServices.getMDIManager().addWindow(form);
	}
    }

    @Override
    protected String getLayerName() {
	return StationsForm.LAYERNAME;
    }
}
