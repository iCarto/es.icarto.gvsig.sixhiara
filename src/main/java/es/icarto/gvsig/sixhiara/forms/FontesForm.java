package es.icarto.gvsig.sixhiara.forms;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import org.gvsig.fmap.mapcontext.layers.vectorial.FLyrVect;

import es.icarto.gvsig.navtableforms.gui.tables.handler.AlphanumericTableHandler;
import es.icarto.gvsig.sixhiara.forms.plots.AnalyticActionListener;

@SuppressWarnings("serial")
public class FontesForm extends BasicAbstractForm {

	public static final String LAYERNAME = "fontes";
	public static final String PKFIELD = "cod_fonte";

	public FontesForm(FLyrVect layer) {
		super(layer);
		addChained("distrito", "provincia");
		addChained("posto_adm", "distrito");
		addTableHandler(new AlphanumericTableHandler(AnaliseSubForm.TABLENAME,
				getWidgetComponents(), PKFIELD, AnaliseSubForm.colNames,
				AnaliseSubForm.colAlias));
		addTableHandler(new AlphanumericTableHandler(
				QuantidadeAguaSubForm.TABLENAME, getWidgetComponents(),
				PKFIELD, QuantidadeAguaSubForm.colNames,
				QuantidadeAguaSubForm.colAlias));
		addAnalyticsButton();
	}

	private void addAnalyticsButton() {
		java.net.URL imgURL = getClass().getClassLoader().getResource(
				"images/analytics.png");
		JButton jButton = new JButton(new ImageIcon(imgURL));
		jButton.setToolTipText("Analise de fontes");

		jButton.addActionListener(new AnalyticActionListener(layer));
		getActionsToolBar().add(jButton);
	}

	@Override
	protected String getSchema() {
		return "inventario";
	}

	@Override
	protected String getBasicName() {
		return "fontes";
	}

	@Override
	protected String getPrimaryKey() {
		return PKFIELD;
	}
}
