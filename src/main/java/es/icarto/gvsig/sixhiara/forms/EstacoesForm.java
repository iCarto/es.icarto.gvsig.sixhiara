package es.icarto.gvsig.sixhiara.forms;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import org.gvsig.fmap.mapcontext.layers.vectorial.FLyrVect;

import es.icarto.gvsig.navtableforms.gui.tables.handler.AlphanumericTableHandler;
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
		addTableHandler(new AlphanumericTableHandler(EstacoesAnaliseSubForm.TABLENAME,
				getWidgetComponents(), PKFIELD, EstacoesAnaliseSubForm.colNames,
				EstacoesAnaliseSubForm.colAlias));
		addTableHandler(new AlphanumericTableHandler(
				DadosPluviometricosSubForm.TABLENAME, getWidgetComponents(),
				PKFIELD, DadosPluviometricosSubForm.colNames,
				DadosPluviometricosSubForm.colAlias));
		addTableHandler(new AlphanumericTableHandler(
				DadosHidrometricosSubForm.TABLENAME, getWidgetComponents(),
				PKFIELD, DadosHidrometricosSubForm.colNames,
				DadosHidrometricosSubForm.colAlias));
		addAnalyticsButton();
	}
	
	private void addAnalyticsButton() {
		java.net.URL imgURL = getClass().getClassLoader().getResource(
				"images/analytics.png");
		JButton jButton = new JButton(new ImageIcon(imgURL));
		jButton.setToolTipText("Analise de estacoes");

		jButton.addActionListener(new AnalyticActionListener(layer, EstacoesAnaliseSubForm.TABLENAME,
				PKFIELD, DATE_FIELD));
		getActionsToolBar().add(jButton);
	}

	@Override
	protected String getSchema() {
		return "inventario";
	}

	@Override
	protected String getBasicName() {
		return "estacoes";
	}

	@Override
	protected String getPrimaryKey() {
		return PKFIELD;
	}

}
