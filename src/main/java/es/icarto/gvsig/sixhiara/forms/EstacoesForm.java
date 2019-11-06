package es.icarto.gvsig.sixhiara.forms;

import javax.swing.JButton;

import org.gvsig.fmap.mapcontext.layers.vectorial.FLyrVect;

import es.icarto.gvsig.sixhiara.navtableforms.SortedAlphanumericTableHandler;
import es.icarto.gvsig.sixhiara.plots.AnalyticActionListener;

@SuppressWarnings("serial")
public class EstacoesForm extends BasicAbstractForm {

	public static final String LAYERNAME = "estacoes";
	public static final String PKFIELD = "cod_estac";
	public static final String DATE_FIELD = "data_med";

	public EstacoesForm(FLyrVect layer) {
		super(layer);
		addChained("distrito", "provincia");
		addChained("posto_adm", "distrito");
		addChained("subacia", "bacia");
		addTableHandler(new SortedAlphanumericTableHandler(
				EstacoesAnaliseSubForm.TABLENAME, getWidgetComponents(),
				PKFIELD, EstacoesAnaliseSubForm.colNames,
				EstacoesAnaliseSubForm.colAlias));
		addTableHandler(new SortedAlphanumericTableHandler(
				DadosPluviometricosSubForm.TABLENAME, getWidgetComponents(),
				PKFIELD, DadosPluviometricosSubForm.colNames,
				DadosPluviometricosSubForm.colAlias));
		addTableHandler(new SortedAlphanumericTableHandler(
				DadosHidrometricosSubForm.TABLENAME, getWidgetComponents(),
				PKFIELD, DadosHidrometricosSubForm.colNames,
				DadosHidrometricosSubForm.colAlias));
		addAnalyticsButton();
		addNewFeatureButton();
		addCoordinatesButton();
	}

	private void addAnalyticsButton() {
		JButton button = addButton("images/analytics.png",
				"Analise de estacoes");
		button.addActionListener(new AnalyticActionListener(layer,
				EstacoesAnaliseSubForm.TABLENAME, PKFIELD, DATE_FIELD));
	}

	@Override
	protected String getSchema() {
		return "inventario";
	}

	@Override
	public String getBasicName() {
		return "estacoes";
	}

	@Override
	protected String getPrimaryKey() {
		return PKFIELD;
	}

}
