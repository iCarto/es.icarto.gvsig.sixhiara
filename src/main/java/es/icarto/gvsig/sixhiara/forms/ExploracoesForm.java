package es.icarto.gvsig.sixhiara.forms;

import static es.icarto.gvsig.commons.i18n.I18n._;

import javax.swing.JButton;

import org.gvsig.fmap.mapcontext.layers.vectorial.FLyrVect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.udc.cartolab.gvsig.navtable.NavTable;

@SuppressWarnings("serial")
public class ExploracoesForm extends NavTable {

	private static final Logger logger = LoggerFactory.getLogger(ExploracoesForm.class);

	public static final String LAYERNAME = "exploracoes";
	public static final String PKFIELD = "gid";
	public static final String ABEILLE = "forms/exploracoes.xml";
	public static final String METADATA = "rules/exploracoes.xml";

	public ExploracoesForm(FLyrVect layer) {
		super(layer);
	}

	@Override
	public boolean init() {
		boolean flag = super.init();
		table.setEnabled(false);
		return flag;
	}

	@Override
	public void refreshGUI() {
		super.refreshGUI();
		removeB.setEnabled(false);
		saveB.setEnabled(false);
		copyPreviousB.setEnabled(false);
		copySelectedB.setEnabled(false);
	}

	// Probably should be removed and use a factory instead
	// is duplicated with NavigationHandler
	private JButton getNavTableButton(JButton button, String iconName, String toolTipName) {
		JButton but = new JButton(getIcon(iconName));
		but.setToolTipText(_(toolTipName));
		but.addActionListener(this);
		return but;
	}

	@Override
	protected void enableSaveButton(boolean bool) {
		// nothing to do here
	}

	@Override
	protected boolean isChangedValues() {
		return false;
	}

}
