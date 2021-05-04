package es.icarto.gvsig.sixhiara.forms;

import static es.icarto.gvsig.commons.i18n.I18n._;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import org.gvsig.fmap.dal.exception.DataException;
import org.gvsig.fmap.mapcontext.layers.vectorial.FLyrVect;
import org.gvsig.tools.exception.BaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jeta.forms.components.panel.FormPanel;
import com.jeta.forms.gui.common.FormException;

import es.icarto.gvsig.commons.gui.OkCancelPanel;
import es.icarto.gvsig.commons.utils.Field;
import es.icarto.gvsig.navtable.edition.LayerEdition;
import es.icarto.gvsig.navtableforms.AbstractForm;
import es.icarto.gvsig.sixhiara.analytics.ExportAnalyticsActionListener;
import es.icarto.gvsig.sixhiara.forms.actions.CoordinateListener;
import es.icarto.gvsig.sixhiara.forms.actions.NewFeatureListener;
import es.icarto.gvsig.sixhiara.forms.images.ImagesInForms;
import es.icarto.gvsig.sixhiara.plots.AnalyticActionListener;
import es.udc.cartolab.gvsig.navtable.contextualmenu.ChooseSortFieldDialog;

@SuppressWarnings("serial")
public abstract class BasicAbstractForm extends AbstractForm {

	private static final Logger logger = LoggerFactory.getLogger(BasicAbstractForm.class);
	private final ImagesInForms images;
	private boolean autoediting;

	public BasicAbstractForm(FLyrVect layer) {
		super(layer);
		addSorterButton();
		setTitle(_(this.getBasicName()));
		this.images = new ImagesInForms(getFormPanel(), getSchema(), getBasicName() + "_imagenes", getPrimaryKey());
	}

	protected void addSorterButton() {
		final JButton button = addButton("images/sort.png", "sort_features");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final URL resource = BasicAbstractForm.this.getClass().getClassLoader()
						.getResource("columns.properties");
				final List<Field> fields = FieldUtils.getFields(resource.getPath(), getSchema(), getBasicName());
				final ChooseSortFieldDialog dialog = new ChooseSortFieldDialog(fields);

				if (dialog.open().equals(OkCancelPanel.OK_ACTION_COMMAND)) {
					final List<Field> sortedFields = dialog.getFields();
					setSortKeys(sortedFields);
				}
			}
		});
	}

	protected void addAnalyticsButton(String analyticsTable, String pkfield, String dateField) {
		final JButton button = addButton("images/analytics.png", "Analise de fontes");
		button.addActionListener(new AnalyticActionListener(this, analyticsTable, pkfield, dateField));
	}

	protected void addExportAnalyticsButton(String analyticsTable) {
		final JButton button = addButton("images/analytics.png", "Exportar Analise");
		button.addActionListener(new ExportAnalyticsActionListener(this, analyticsTable));
	}

	protected void addNewFeatureButton() {
		final JButton button = addButton("images/new_feature.png", "Adicionar feature");
		final ActionListener btListener = new NewFeatureListener(this);
		button.addActionListener(btListener);
	}

	protected void addCoordinatesButton() {
		final JButton button = addButton("images/add_coordinates_icon.png", "Adicionar ponto baseado em coordenadas");
		final ActionListener btListener = new CoordinateListener(this);
		button.addActionListener(btListener);
	}

	protected JButton addButton(String imagePath, String tooltip) {
		final URL imgURL = getClass().getClassLoader().getResource(imagePath);
		final JButton button = new JButton(new ImageIcon(imgURL));
		button.setToolTipText(tooltip);
		getActionsToolBar().add(button);
		return button;
	}

	@Override
	public FormPanel getFormBody() {
		if (this.formBody == null) {
			InputStream stream = getClass().getClassLoader().getResourceAsStream("/forms/" + getBasicName() + ".jfrm");
			if (stream == null) {
				stream = getClass().getClassLoader().getResourceAsStream("/forms/" + getBasicName() + ".xml");
			}
			try {
				this.formBody = new FormPanel(stream);
			} catch (final FormException e) {
				logger.error(e.getMessage(), e);
			}
		}
		return this.formBody;
	}

	@Override
	public String getXMLPath() {
		return this.getClass().getClassLoader().getResource("rules/" + getBasicName() + ".xml").getPath();
	}

	@Override
	protected void fillSpecificValues() {
		super.fillSpecificValues();
		this.images.fillSpecificValues(getPrimaryKeyValue());
	}

	@Override
	public String getPrimaryKeyValue() {
		return getFormController().getValue(getPrimaryKey());
	}

	@Override
	protected void setListeners() {
		super.setListeners();
		this.images.setListeners();
	}

	@Override
	protected void removeListeners() {
		super.removeListeners();
		this.images.removeListeners();
	}

	protected abstract String getSchema();

	public abstract String getBasicName();

	protected abstract String getPrimaryKey();

	/*
	 * Desde la funcionalidad de crear una nueva feature se puede poner la capa en
	 * edición automaticamente, pero no se puede cerrar del mismo modo, porqué hay
	 * que esperar a que el usuario rellene los valores obligatorios.
	 *
	 * Aquí marcamos que la edición se ha abierto de forma automática, de modo que
	 * si tras darle al botón guardar se detecta esa situación se cierra la edición
	 * también de forma automática
	 */
	public void setAutoEditing() {
		this.autoediting = true;
	}

	@Override
	public boolean saveRecord() throws DataException {
		final boolean flag = super.saveRecord();
		if (this.autoediting) {
			final LayerEdition le = new LayerEdition();
			if (this.layer.isEditing()) {
				le.stopEditing(this.layer, false);
			}
			this.autoediting = false;
		}
		return flag;
	}

	public void insertEmptyFeature() throws BaseException {
		this.navigation.insertFeature();
	}

}
