package es.icarto.gvsig.sixhiara.forms;

import java.io.InputStream;

import org.gvsig.fmap.mapcontext.layers.vectorial.FLyrVect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jeta.forms.components.panel.FormPanel;
import com.jeta.forms.gui.common.FormException;

import es.icarto.gvsig.navtableforms.AbstractForm;
import es.icarto.gvsig.navtableforms.gui.tables.handler.AlphanumericTableHandler;
import es.icarto.gvsig.sixhiara.forms.images.ImagesInForms;

@SuppressWarnings("serial")
public class EstacoesForm extends AbstractForm {

	private static final Logger logger = LoggerFactory
			.getLogger(EstacoesForm.class);
	public static final String LAYERNAME = "estacoes";
	public static final String PKFIELD = "cod_estac";
	public static final String ABEILLE = "forms/estacoes.xml";
	public static final String METADATA = "rules/estacoes.xml";
	private ImagesInForms images;

	public EstacoesForm(FLyrVect layer) {
		super(layer);
		addChained("distrito", "provincia");
		addChained("posto_adm", "distrito");
		addChained("subacia", "bacia");
		addTableHandler(new AlphanumericTableHandler(
				DadosPluviometricosSubForm.TABLENAME, getWidgetComponents(),
				PKFIELD, DadosPluviometricosSubForm.colNames,
				DadosPluviometricosSubForm.colAlias));
		addTableHandler(new AlphanumericTableHandler(
				DadosHidrometricosSubForm.TABLENAME, getWidgetComponents(),
				PKFIELD, DadosHidrometricosSubForm.colNames,
				DadosHidrometricosSubForm.colAlias));
		images = new ImagesInForms(this.getFormPanel(), "inventario",
				"estacoes_imagenes", PKFIELD);
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
		images.fillSpecificValues(getPrimaryKeyValue());
	}

	@Override
	protected String getPrimaryKeyValue() {
		return getFormController().getValue(PKFIELD);
	}

	@Override
	protected void setListeners() {
		super.setListeners();
		images.setListeners();
	}

	@Override
	protected void removeListeners() {
		super.removeListeners();
		images.removeListeners();
	}

}
