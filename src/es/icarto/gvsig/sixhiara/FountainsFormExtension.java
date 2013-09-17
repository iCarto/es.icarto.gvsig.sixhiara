package es.icarto.gvsig.sixhiara;

import com.iver.andami.PluginServices;
import com.iver.andami.plugins.Extension;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;

import es.icarto.gvsig.navtableforms.utils.TOCLayerManager;
import es.icarto.gvsig.sixhiara.forms.FountainsForm;

public class FountainsFormExtension extends Extension {

    @Override
    public void initialize() {

    }

    @Override
    public void execute(String actionCommand) {
	FountainsForm form = new FountainsForm(getLayer());
	if (form != null && form.init()) {
	    PluginServices.getMDIManager().addWindow(form);
	}
    }

    @Override
    public boolean isEnabled() {
	if (getLayer() != null) {
	    return true;
	} else {
	    return false;
	}
    }

    @Override
    public boolean isVisible() {
	return true;
    }

    private FLyrVect getLayer() {
	return new TOCLayerManager().getLayerByName(FountainsForm.LAYERNAME);
    }

}
