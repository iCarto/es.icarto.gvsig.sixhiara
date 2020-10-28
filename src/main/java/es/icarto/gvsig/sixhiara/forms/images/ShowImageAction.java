package es.icarto.gvsig.sixhiara.forms.images;

import static es.icarto.gvsig.commons.i18n.I18n._;

import java.awt.image.BufferedImage;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jeta.forms.components.image.ImageComponent;

import es.icarto.gvsig.commons.utils.ImageUtils;
import es.udc.cartolab.gvsig.users.utils.DBSession;

public class ShowImageAction {

	private static final Logger logger = LoggerFactory.getLogger(ShowImageAction.class);

	private final ImageComponent imageComponent;
	private final JButton addImageButton;
	private final Connection connection;
	private final String schema;
	private final String tablename;
	private final String pkField;
	private final String pkValue;

	private final ImageIcon NO_IMAGE;

	public ShowImageAction(ImageComponent imageComponent, JButton addImageButton, String schema, String tablename,
			String pkField, String pkValue) {
		URL url = this.getClass().getClassLoader().getResource("images/img_no_disponible.jpg");
		this.NO_IMAGE = new ImageIcon(url);
		this.imageComponent = imageComponent;
		this.addImageButton = addImageButton;
		this.schema = schema;
		this.tablename = tablename;
		this.pkField = pkField;
		this.pkValue = pkValue;
		connection = DBSession.getCurrentSession().getJavaConnection();
		if (showImage()) {
			addImageButton.setText(_("update_image"));
		} else {
			addImageButton.setText(_("add_image"));
		}
		imageComponent.repaint();
	}

	public boolean showImage() {
		ImagesDAO dao = new ImagesDAO();
		try {
			byte[] elementImageBytes = dao.readImageFromDb(connection, schema, tablename, pkField, pkValue);
			if (elementImageBytes == null) {
				imageComponent.setIcon(NO_IMAGE);
				return false;
			}
			BufferedImage elementImage = ImageUtils.convertByteaToImage(elementImageBytes);
			ImageIcon elementIcon = new ImageIcon(elementImage);
			imageComponent.setIcon(elementIcon);
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
		}
		return true;
	}

}
