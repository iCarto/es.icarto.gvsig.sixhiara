package es.icarto.gvsig.sixhiara;


import org.gvsig.andami.PluginServices;

import es.icarto.gvsig.sixhiara.forms.AcuiferosForm;

public class AcuiferosFormExtension extends AbstractFormExtension {

    @Override
    public void execute(String actionCommand) {
	AcuiferosForm form = new AcuiferosForm(getLayer());
	if (form != null && form.init()) {
	    PluginServices.getMDIManager().addWindow(form);
	}
    }

    @Override
    protected String getLayerName() {
	return AcuiferosForm.LAYERNAME;
    }

}
