package es.icarto.gvsig.sixhiara;

import org.gvsig.andami.ui.mdiManager.MDIManagerFactory;

import es.icarto.gvsig.sixhiara.forms.ExploracoesForm;
import es.udc.cartolab.gvsig.navtable.NavTable;

public class ExploracoesFormExtension extends AbstractFormExtension {

	@Override
	public void execute(String actionCommand) {
		NavTable form = new ExploracoesForm(getLayer());
		if (form != null && form.init()) {
			MDIManagerFactory.getManager().addWindow(form);
		}
	}

	@Override
	protected String getLayerName() {
		return ExploracoesForm.LAYERNAME;
	}

}
