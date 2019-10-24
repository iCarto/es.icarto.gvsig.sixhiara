package es.icarto.gvsig.sixhiara.forms;

import static es.icarto.gvsig.commons.i18n.I18n._;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import org.gvsig.fmap.geom.Geometry;
import org.gvsig.fmap.geom.primitive.Point;
import org.gvsig.fmap.mapcontext.layers.vectorial.FLyrVect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jeta.forms.components.panel.FormPanel;
import com.jeta.forms.gui.common.FormException;

import es.icarto.gvsig.commons.gui.OkCancelPanel;
import es.icarto.gvsig.commons.utils.Field;
import es.icarto.gvsig.navtableforms.AbstractForm;
import es.icarto.gvsig.sixhiara.forms.images.ImagesInForms;
import es.udc.cartolab.gvsig.navtable.contextualmenu.ChooseSortFieldDialog;

@SuppressWarnings("serial")
public abstract class BasicAbstractForm extends AbstractForm {

	private static final Logger logger = LoggerFactory
			.getLogger(BasicAbstractForm.class);
	private ImagesInForms images;

	public BasicAbstractForm(FLyrVect layer) {
		super(layer);
		addSorterButton();
		setTitle(_(this.getBasicName()));
		images = new ImagesInForms(getFormPanel(), getSchema(), getBasicName()
				+ "_imagenes", getPrimaryKey());
	}

	protected void addSorterButton() {
		JButton button = addButton("images/sort.png", "sort_features");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				URL resource = BasicAbstractForm.this.getClass()
						.getClassLoader().getResource("columns.properties");
				List<Field> fields = FieldUtils.getFields(resource.getPath(),
						getSchema(), getBasicName());
				ChooseSortFieldDialog dialog = new ChooseSortFieldDialog(fields);

				if (dialog.open().equals(OkCancelPanel.OK_ACTION_COMMAND)) {
					List<Field> sortedFields = dialog.getFields();
					setSortKeys(sortedFields);
				}
			}
		});
	}
	
	protected void addNewFeatureButton() {
		JButton button = addButton("images/add_coordinates_icon.png", "Adicionar feature");
		ActionListener btListener = new CoordinateListener(this);
		button.addActionListener(btListener);
	}
	
	protected void addCoordinatesButton() {
		JButton button = addButton("images/add_coordinates_icon.png", "Adicionar ponto baseado em coordenadas");
		ActionListener btListener = new CoordinateListener(this);
		button.addActionListener(btListener);
	}
	
	protected JButton addButton(String imagePath, String tooltip) {
		URL imgURL = getClass().getClassLoader().getResource(imagePath);
		JButton button = new JButton(new ImageIcon(imgURL));
		button.setToolTipText(tooltip);
		getActionsToolBar().add(button);
		return button;
	}

	@Override
	public FormPanel getFormBody() {
		if (formBody == null) {
			InputStream stream = getClass().getClassLoader()
					.getResourceAsStream("/forms/" + getBasicName() + ".jfrm");
			if (stream == null) {
				stream = getClass().getClassLoader().getResourceAsStream(
						"/forms/" + getBasicName() + ".xml");
			}
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
		return this.getClass().getClassLoader()
				.getResource("rules/" + getBasicName() + ".xml").getPath();
	}

	@Override
	protected void fillSpecificValues() {
		super.fillSpecificValues();
		images.fillSpecificValues(getPrimaryKeyValue());
	}

	@Override
	protected String getPrimaryKeyValue() {
		return getFormController().getValue(getPrimaryKey());
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

	protected abstract String getSchema();

	protected abstract String getBasicName();

	protected abstract String getPrimaryKey();

}
