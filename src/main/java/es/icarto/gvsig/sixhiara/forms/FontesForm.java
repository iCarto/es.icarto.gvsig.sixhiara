package es.icarto.gvsig.sixhiara.forms;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import org.gvsig.fmap.mapcontext.layers.vectorial.FLyrVect;

import es.icarto.gvsig.navtableforms.gui.tables.handler.AlphanumericTableHandler;
import es.icarto.gvsig.sixhiara.plots.AnalyticActionListener;

@SuppressWarnings("serial")
public class FontesForm extends BasicAbstractForm {

	public static final String LAYERNAME = "fontes";
	public static final String PKFIELD = "cadastro";
	public static final String DATE_FIELD = "data_most";

	public FontesForm(FLyrVect layer) {
		super(layer);
		addChained("distrito", "provincia");
		addChained("posto_adm", "distrito");
		addChained("bacia", "loc_unidad");
		addChained("subacia", "bacia");
		addTableHandler(new AlphanumericTableHandler(FontesAnaliseSubForm.TABLENAME,
				getWidgetComponents(), PKFIELD, FontesAnaliseSubForm.colNames,
				FontesAnaliseSubForm.colAlias));
		addTableHandler(new AlphanumericTableHandler(
				QuantidadeAguaSubForm.TABLENAME, getWidgetComponents(),
				PKFIELD, QuantidadeAguaSubForm.colNames,
				QuantidadeAguaSubForm.colAlias));
		addTableHandler(new AlphanumericTableHandler(FontesLitologiaSubForm.TABLENAME,
				getWidgetComponents(), PKFIELD, FontesLitologiaSubForm.colNames,
				FontesLitologiaSubForm.colAlias));
		addTableHandler(new AlphanumericTableHandler(
				FontesCaracHidroSubForm.TABLENAME, getWidgetComponents(),
				PKFIELD, FontesCaracHidroSubForm.colNames,
				FontesCaracHidroSubForm.colAlias));
		addAnalyticsButton();
	}

	private void addAnalyticsButton() {
		java.net.URL imgURL = getClass().getClassLoader().getResource(
				"images/analytics.png");
		JButton jButton = new JButton(new ImageIcon(imgURL));
		jButton.setToolTipText("Analise de fontes");

		jButton.addActionListener(new AnalyticActionListener(layer, FontesAnaliseSubForm.TABLENAME,
				PKFIELD, DATE_FIELD));
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
