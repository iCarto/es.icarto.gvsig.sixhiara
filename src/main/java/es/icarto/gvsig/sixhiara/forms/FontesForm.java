package es.icarto.gvsig.sixhiara.forms;

import javax.swing.JButton;

import org.gvsig.fmap.mapcontext.layers.vectorial.FLyrVect;

import es.icarto.gvsig.sixhiara.FeatureSwitcherExtension;
import es.icarto.gvsig.sixhiara.navtableforms.SortedAlphanumericTableHandler;
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
		addChained("tip_fonte", "tipo_agua");
		addTableHandler(new SortedAlphanumericTableHandler(FontesAnaliseSubForm.TABLENAME, getWidgetComponents(),
				PKFIELD, FontesAnaliseSubForm.colNames, FontesAnaliseSubForm.colAlias));
		addTableHandler(new SortedAlphanumericTableHandler(QuantidadeAguaSubForm.TABLENAME, getWidgetComponents(),
				PKFIELD, QuantidadeAguaSubForm.colNames, QuantidadeAguaSubForm.colAlias));
		addTableHandler(new SortedAlphanumericTableHandler(FontesLitologiaSubForm.TABLENAME, getWidgetComponents(),
				PKFIELD, FontesLitologiaSubForm.colNames, FontesLitologiaSubForm.colAlias));
		addTableHandler(new SortedAlphanumericTableHandler(FontesCaracHidroSubForm.TABLENAME, getWidgetComponents(),
				PKFIELD, FontesCaracHidroSubForm.colNames, FontesCaracHidroSubForm.colAlias));
		if (FeatureSwitcherExtension.fontesAnalyticsButton()) {
			addAnalyticsButton();
		}
		addNewFeatureButton();
		addCoordinatesButton();
	}

	private void addAnalyticsButton() {
		JButton button = addButton("images/analytics.png", "Analise de fontes");
		button.addActionListener(
				new AnalyticActionListener(layer, FontesAnaliseSubForm.TABLENAME, PKFIELD, DATE_FIELD));
	}

	@Override
	protected String getSchema() {
		return "inventario";
	}

	@Override
	public String getBasicName() {
		return "fontes";
	}

	@Override
	protected String getPrimaryKey() {
		return PKFIELD;
	}
}
