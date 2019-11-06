package es.icarto.gvsig.sixhiara.forms.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;

import org.gvsig.fmap.dal.exception.DataException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.icarto.gvsig.commons.locatorbycoords.LocatorByCoordsButton;
import es.icarto.gvsig.commons.locatorbycoords.LocatorByCoordsDialog;
import es.icarto.gvsig.commons.locatorbycoords.LocatorByCoordsModel;
import es.icarto.gvsig.commons.referencing.gvsig.GPoint;
import es.icarto.gvsig.sixhiara.forms.BasicAbstractForm;
import es.udc.cartolab.gvsig.navtable.dataacces.LayerController;

@SuppressWarnings("serial")
public class LocatorBytCoordsAddButton extends LocatorByCoordsButton {

	private static final Logger logger = LoggerFactory
			.getLogger(LocatorBytCoordsAddButton.class);

	private final LocatorByCoordsModel lModel;
	private final BasicAbstractForm form;
	private final LocatorByCoordsDialog dialog;

	public LocatorBytCoordsAddButton(LocatorByCoordsModel lModel_,
			BasicAbstractForm form_, LocatorByCoordsDialog dialog_) {
		super();
		this.lModel = lModel_;
		this.form = form_;
		this.dialog = dialog_;
		Action action = new AbstractAction("Gravar") {
			@Override
			public void actionPerformed(ActionEvent e) {
				GPoint point = new GPoint(lModel.getPoint());
				point.reProject(form.getLayer().getProjection());
				LayerController layerController = (LayerController) form
						.getFormController();
				layerController.setGeom(point.getPoint());
				boolean wellSaved = true;
				try {
					wellSaved = form.saveRecord();
				} catch (DataException e1) {
					logger.error(e1.getMessage(), e1);
					wellSaved = false;
				}

				if (!wellSaved) {
					JOptionPane.showMessageDialog(dialog,
							"Erro gravando os dados", "Error",
							JOptionPane.ERROR_MESSAGE);
				}
				dialog.closeDialog();
			}
		};
		action.setEnabled(false);
		this.setAction(action);
	}

	@Override
	public void modelChanged(LocatorByCoordsModel model, String event) {
		this.getAction().setEnabled(model.isZoomed());
	}

}
