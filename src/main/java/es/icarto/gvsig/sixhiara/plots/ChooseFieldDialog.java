package es.icarto.gvsig.sixhiara.plots;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;

import org.gvsig.andami.ui.mdiManager.MDIManagerFactory;

import es.icarto.gvsig.commons.gui.AbstractIWindow;
import es.icarto.gvsig.commons.gui.OkCancelPanel;
import es.icarto.gvsig.commons.gui.WidgetFactory;
import es.icarto.gvsig.commons.utils.Field;
import net.miginfocom.swing.MigLayout;

@SuppressWarnings("serial")
public class ChooseFieldDialog extends AbstractIWindow implements
		ActionListener {

	List<ChooseFieldPanel> list = new ArrayList<ChooseFieldPanel>();
	private String status = OkCancelPanel.CANCEL_ACTION_COMMAND;
	private final OkCancelPanel okPanel;

	public ChooseFieldDialog(final List<Field> fields) {
		super(new MigLayout("insets 10, wrap 1"));
		okPanel = WidgetFactory.okCancelPanel(this, this, this);

		ChooseFieldPanel field = new ChooseFieldPanel(list.size(), fields);
		list.add(field);
		add(field);

	}

	public Field getField() {
		for (ChooseFieldPanel l : list) {
			if (l.getSelected() != null) {
				return l.getSelected();
			}
		}
		return null;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		status = e.getActionCommand();
		MDIManagerFactory.getManager().closeWindow(this);
	}

	public String open() {
		super.openDialog();
		return status;
	}

	@Override
	protected JButton getDefaultButton() {
		return okPanel.getOkButton();
	}

	@Override
	protected Component getDefaultFocusComponent() {
		return null;
	}

}
