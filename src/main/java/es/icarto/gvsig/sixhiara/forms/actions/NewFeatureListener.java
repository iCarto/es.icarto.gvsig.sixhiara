package es.icarto.gvsig.sixhiara.forms.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

import org.gvsig.fmap.mapcontext.layers.vectorial.FLyrVect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.icarto.gvsig.navtable.edition.LayerEdition;
import es.icarto.gvsig.sixhiara.forms.BasicAbstractForm;

public class NewFeatureListener implements ActionListener {

	private static final Logger logger = LoggerFactory
			.getLogger(NewFeatureListener.class);

	private final BasicAbstractForm form;

	public NewFeatureListener(BasicAbstractForm form) {
		this.form = form;
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		FLyrVect layer = form.getLayer();
		LayerEdition le = new LayerEdition();
		if (!layer.isEditing()) {
			le.startEditing(layer);
			form.setAutoEditing();
		}
		try {
			form.insertEmptyFeature();
		} catch (Exception e1) {
			logger.error(e1.getMessage(), e1);
			if (layer.isEditing()) {
				le.stopEditing(layer, true);
			}
			JOptionPane.showMessageDialog(form,
					"Erro creando feature. Cerre e volva abrir", "Error",
					JOptionPane.ERROR_MESSAGE);

		}

	}
}
