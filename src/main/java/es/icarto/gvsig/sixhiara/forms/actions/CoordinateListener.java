package es.icarto.gvsig.sixhiara.forms.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.gvsig.andami.PluginServices;
import org.gvsig.andami.ui.mdiManager.WindowInfo;

import es.icarto.gvsig.commons.locatorbycoords.LocatorByCoordsDialog;
import es.icarto.gvsig.commons.locatorbycoords.LocatorByCoordsModel;
import es.icarto.gvsig.sixhiara.LocatorByCoordsExtension;
import es.icarto.gvsig.sixhiara.forms.BasicAbstractForm;

public class CoordinateListener implements ActionListener {

	private final BasicAbstractForm form;

	public CoordinateListener(BasicAbstractForm form) {
		this.form = form;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		LocatorByCoordsExtension ext = (LocatorByCoordsExtension) PluginServices
				.getExtension(LocatorByCoordsExtension.class);
		LocatorByCoordsDialog dialog = ext.getDialog();

		String title = "Gerador de pontos. " + form.getBasicName() + ": "
				+ form.getPrimaryKeyValue();
		dialog.setWindowTitle(title);
		dialog.setWindowInfoProperties(WindowInfo.MODALDIALOG
				| WindowInfo.PALETTE);
		LocatorByCoordsModel model = dialog.getModel();
		LocatorBytCoordsAddButton bt = new LocatorBytCoordsAddButton(model,
				form, dialog);
		dialog.addButton(bt);
		dialog.openDialog();
	}
}
