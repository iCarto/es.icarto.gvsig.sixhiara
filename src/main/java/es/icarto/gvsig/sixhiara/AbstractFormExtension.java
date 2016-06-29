package es.icarto.gvsig.sixhiara;

import org.gvsig.andami.IconThemeHelper;
import org.gvsig.andami.plugins.Extension;
import org.gvsig.fmap.mapcontext.layers.vectorial.FLyrVect;

import es.icarto.gvsig.navtableforms.utils.TOCLayerManager;
import es.icarto.gvsig.sixhiara.forms.SixhiaraFormFactory;


public abstract class AbstractFormExtension extends Extension {

    @Override
    public void initialize() {
	SixhiaraFormFactory.registerFormFactory();
	String id = this.getClass().getName();
    IconThemeHelper.registerIcon("action", id, this);
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
