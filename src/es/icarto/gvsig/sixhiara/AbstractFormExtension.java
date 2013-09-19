package es.icarto.gvsig.sixhiara;

import com.iver.andami.plugins.Extension;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;

import es.icarto.gvsig.navtableforms.utils.TOCLayerManager;
import es.icarto.gvsig.sixhiara.forms.SixhiaraFormFactory;

public abstract class AbstractFormExtension extends Extension {

    @Override
    public void initialize() {
	SixhiaraFormFactory.registerFormFactory();
    }

    @Override
    public void execute(String actionCommand) {

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

    protected abstract String getLayerName();

    protected FLyrVect getLayer() {
	return new TOCLayerManager().getLayerByName(getLayerName());
    }

}
