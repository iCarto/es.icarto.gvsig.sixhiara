package es.icarto.gvsig.sixhiara;

import com.iver.andami.PluginServices;

import es.icarto.gvsig.sixhiara.forms.FountainsForm;

public class FountainsFormExtension extends AbstractFormExtension {

    @Override
    public void execute(String actionCommand) {
	FountainsForm form = new FountainsForm(getLayer());
	if (form != null && form.init()) {
	    PluginServices.getMDIManager().addWindow(form);
	}
    }

    @Override
    protected String getLayerName() {
	return FountainsForm.LAYERNAME;
    }

}
