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
public class FontesForm extends AbstractForm {

	private static final Logger logger = LoggerFactory
			.getLogger(FontesForm.class);
	public static final String LAYERNAME = "Fontes";
	public static final String PKFIELD = "cod_fonte";
	public static final String ABEILLE = "forms/fontes.xml";
	public static final String METADATA = "rules/fontes.xml";

	public FontesForm(FLyrVect layer) {
		super(layer);
		addTableHandler(new AlphanumericTableHandler(AnaliseSubForm.TABLENAME,
				getWidgetComponents(), PKFIELD, AnaliseSubForm.colNames,
				AnaliseSubForm.colAlias));
		addTableHandler(new AlphanumericTableHandler(
				QuantidadeAguaSubForm.TABLENAME, getWidgetComponents(),
				PKFIELD, QuantidadeAguaSubForm.colNames,
				QuantidadeAguaSubForm.colAlias));
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
