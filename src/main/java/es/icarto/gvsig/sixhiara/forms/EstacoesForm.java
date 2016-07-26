package es.icarto.gvsig.sixhiara.forms;

import java.io.InputStream;

import org.gvsig.fmap.mapcontext.layers.vectorial.FLyrVect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jeta.forms.components.panel.FormPanel;
import com.jeta.forms.gui.common.FormException;

import es.icarto.gvsig.navtableforms.AbstractForm;
import es.icarto.gvsig.navtableforms.gui.tables.handler.AlphanumericTableHandler;

@SuppressWarnings("serial")
public class EstacoesForm extends AbstractForm {

	private static final Logger logger = LoggerFactory
			.getLogger(EstacoesForm.class);
	public static final String LAYERNAME = "Estacoes";
	public static final String PKFIELD = "cod_estac";
	public static final String ABEILLE = "forms/estacoes.xml";
	public static final String METADATA = "rules/estacoes.xml";

	public EstacoesForm(FLyrVect layer) {
		super(layer);
		addTableHandler(new AlphanumericTableHandler(
				DadosPluviometricosSubForm.TABLENAME, getWidgetComponents(),
				PKFIELD, DadosPluviometricosSubForm.colNames,
				DadosPluviometricosSubForm.colAlias));
		addTableHandler(new AlphanumericTableHandler(
				DadosHidrometricosSubForm.TABLENAME, getWidgetComponents(),
				PKFIELD, DadosHidrometricosSubForm.colNames,
				DadosHidrometricosSubForm.colAlias));
	}

	@Override
	public String getXMLPath() {
		return this.getClass().getClassLoader().getResource(METADATA).getPath();
	}

	@Override
	public FormPanel getFormBody() {
		if (formBody == null) {
			InputStream stream = getClass().getClassLoader()
					.getResourceAsStream(ABEILLE);
			try {
				formBody = new FormPanel(stream);
			} catch (FormException e) {
				logger.error(e.getMessage(), e);
			}
		}
		return formBody;
	}

	@Override
	protected void fillSpecificValues() {
		super.fillSpecificValues();
	}

	@Override
	protected String getPrimaryKeyValue() {
		return getFormController().getValue(PKFIELD);
	}

}
