package es.icarto.gvsig.sixhiara.forms;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.gvsig.fmap.geom.primitive.Point;
import org.gvsig.fmap.mapcontext.layers.vectorial.FLyrVect;

import es.icarto.gvsig.navtableforms.AbstractForm;

public class CoordinateListener implements ActionListener {

	private final AbstractForm form;

	public CoordinateListener(AbstractForm form) {
		this.form = form;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		FLyrVect foo = form.getLayer();
		Point bar = (Point) form.getFormController().getGeom();
		System.out.println(bar.getX());
		System.out.println(bar.getY());
	}

}
