package es.icarto.gvsig.sixhiara;


import org.gvsig.andami.PluginServices;

import es.icarto.gvsig.sixhiara.forms.ReservoirsForm;

public class ReservoirsFormExtension extends AbstractFormExtension {

    @Override
    public void execute(String actionCommand) {
	ReservoirsForm form = new ReservoirsForm(getLayer());
	if (form != null && form.init()) {
	    PluginServices.getMDIManager().addWindow(form);
	}
    }

    @Override
    protected String getLayerName() {
	return ReservoirsForm.LAYERNAME;
    }

}
