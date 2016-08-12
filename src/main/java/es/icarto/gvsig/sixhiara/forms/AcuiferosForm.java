package es.icarto.gvsig.sixhiara.forms;

import java.io.InputStream;

import org.gvsig.fmap.mapcontext.layers.vectorial.FLyrVect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jeta.forms.components.panel.FormPanel;
import com.jeta.forms.gui.common.FormException;

import es.icarto.gvsig.navtableforms.AbstractForm;
import es.icarto.gvsig.sixhiara.forms.images.ImagesInForms;

@SuppressWarnings("serial")
public class AcuiferosForm extends AbstractForm {

	private static final Logger logger = LoggerFactory
			.getLogger(AcuiferosForm.class);

	public static final String LAYERNAME = "Acuiferos";
	public static final String PKFIELD = "cod_acuif";
	public static final String ABEILLE = "forms/acuiferos.xml";
	public static final String METADATA = "rules/acuiferos.xml";
	private ImagesInForms images;

	public AcuiferosForm(FLyrVect layer) {
		super(layer);
		images = new ImagesInForms(this.getFormPanel(), "inventario",
				"acuiferos_imagenes", PKFIELD);
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
	public String getXMLPath() {
		return this.getClass().getClassLoader().getResource(METADATA).getPath();
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
