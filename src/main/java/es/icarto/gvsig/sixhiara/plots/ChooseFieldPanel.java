package es.icarto.gvsig.sixhiara.plots;

import static es.icarto.gvsig.commons.i18n.I18n._;

import java.util.Collections;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import es.icarto.gvsig.commons.gui.WidgetFactory;
import es.icarto.gvsig.commons.utils.Field;

@SuppressWarnings("serial")
public class ChooseFieldPanel extends JPanel {

	private static final String PROTOTYPE_DISPLAY_VALUE = "XXXXXXXXXXXXXXXXXXXXXXXX";

	private final JComboBox jComboBox;

	public ChooseFieldPanel(int priority, List<Field> fields) {
		super(new MigLayout("insets 10", "[90!][][]"));
		String jLabelText = _("sort_then_by");
		if (priority == 0) {
			jLabelText = _("sort_by");
		}
		add(new JLabel(jLabelText), "cell 0 0 1 2");
		Collections.sort(fields);
		jComboBox = WidgetFactory.combobox();
		jComboBox.addItem(Field.EMPTY_FIELD);
		for (Field f : fields) {
			jComboBox.addItem(f);
		}
		jComboBox.setPrototypeDisplayValue(PROTOTYPE_DISPLAY_VALUE);

		add(jComboBox, "cell 1 0 1 2");

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
