package es.icarto.gvsig.sixhiara;


import org.gvsig.andami.PluginServices;

import es.icarto.gvsig.sixhiara.forms.EstacoesForm;

public class EstacoesFormExtension extends AbstractFormExtension {

    @Override
    public void execute(String actionCommand) {
	EstacoesForm form = new EstacoesForm(getLayer());
	if (form != null && form.init()) {
	    PluginServices.getMDIManager().addWindow(form);
	}
    }

    @Override
    protected String getLayerName() {
	return EstacoesForm.LAYERNAME;
    }
}
