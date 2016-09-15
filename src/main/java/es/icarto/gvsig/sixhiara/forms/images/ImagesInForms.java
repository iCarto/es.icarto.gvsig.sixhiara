package es.icarto.gvsig.sixhiara.forms.images;

import static es.icarto.gvsig.commons.i18n.I18n._;

import java.awt.Dimension;

import javax.swing.JButton;

import com.jeta.forms.components.image.ImageComponent;
import com.jeta.forms.components.panel.FormPanel;

public class ImagesInForms {

	private static final String DELETE_IMAGE_BUTTON = "delete_image_button";
	private static final String ADD_IMAGE_BUTTON = "add_image_button";
	private static final String IMAGE_COMPONENT = "element_image";

	private final FormPanel formPanel;
	private final String tablename;
	private final String fk;
	private final String schema;

	public ImagesInForms(FormPanel formPanel, String schema, String tablename,
			String fk) {
		this.formPanel = formPanel;
		this.schema = schema;
		this.tablename = tablename;
		this.fk = fk;
	}

	protected ImageComponent imageComponent;
	protected JButton addImageButton;
	protected JButton deleteImageButton;

	protected AddImageListener addImageListener;
	protected DeleteImageListener deleteImageListener;

	public void setListeners() {
		imageComponent = (ImageComponent) formPanel
				.getComponentByName(IMAGE_COMPONENT);
		addImageButton = (JButton) formPanel
				.getComponentByName(ADD_IMAGE_BUTTON);
		deleteImageButton = (JButton) formPanel
				.getComponentByName(DELETE_IMAGE_BUTTON);

		if (addImageListener == null) {
			addImageListener = new AddImageListener(imageComponent,
					addImageButton, schema, tablename, fk);
			addImageButton.setText(_("add_image"));
			addImageButton.addActionListener(addImageListener);
		}

		if (deleteImageListener == null) {
			deleteImageListener = new DeleteImageListener(imageComponent,
					deleteImageButton, schema, tablename, fk);
			deleteImageButton.setText(_("delete_image"));
			deleteImageButton.addActionListener(deleteImageListener);
		}

		setBiggestWidthForBut();
	}

	private void setBiggestWidthForBut() {
		Dimension addSize = addImageButton.getPreferredSize();
		Dimension delSize = deleteImageButton.getPreferredSize();
		if (addSize.getWidth() > delSize.getWidth()) {
			delSize = new Dimension(addSize.width, delSize.height);
		} else {
			addSize = new Dimension(delSize.width, addSize.height);
		}

		addImageButton.setSize(addSize);
		addImageButton.setPreferredSize(addSize);

		deleteImageButton.setSize(delSize);
		deleteImageButton.setPreferredSize(delSize);
	}

	public void removeListeners() {
		addImageButton.removeActionListener(addImageListener);
		deleteImageButton.removeActionListener(deleteImageListener);
	}

	public void fillSpecificValues(String fkValue) {
		if (addImageListener != null) {
			addImageListener.setPkValue(fkValue);
		}

		if (deleteImageListener != null) {
			deleteImageListener.setPkValue(fkValue);
		}

		// Element image
		new ShowImageAction(imageComponent, addImageButton, schema, tablename,
				fk, fkValue);
	}

}
