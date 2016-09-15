package es.icarto.gvsig.sixhiara.plots;

import java.awt.FlowLayout;
import java.util.Collections;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import es.icarto.gvsig.commons.gui.WidgetFactory;
import es.icarto.gvsig.commons.utils.Field;

@SuppressWarnings("serial")
public class ChooseFieldPanel extends JPanel {

	private static final String PROTOTYPE_DISPLAY_VALUE = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXX";

	private final JComboBox jComboBox;

	public ChooseFieldPanel(int priority, List<Field> fields) {
		super(new FlowLayout(FlowLayout.CENTER, 0, 5));
		String jLabelText = "Seleccionar parámetro:  ";

		add(new JLabel(jLabelText));
		Collections.sort(fields);
		jComboBox = WidgetFactory.combobox();
		jComboBox.addItem(Field.EMPTY_FIELD);
		for (Field f : fields) {
			jComboBox.addItem(f);
		}
		jComboBox.setPrototypeDisplayValue(PROTOTYPE_DISPLAY_VALUE);

		add(jComboBox);

	}

	public Field getSelected() {
		Field field = null;
		Object selected = jComboBox.getSelectedItem();
		if ((selected != null) && (selected != Field.EMPTY_FIELD)) {
			field = (Field) selected;
		}
		return field;
	}
}
