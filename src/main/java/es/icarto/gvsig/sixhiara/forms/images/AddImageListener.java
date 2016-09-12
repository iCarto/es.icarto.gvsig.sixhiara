package es.icarto.gvsig.sixhiara.forms.images;

import static es.icarto.gvsig.commons.i18n.I18n._;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JOptionPane;

import org.gvsig.andami.PluginServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jeta.forms.components.image.ImageComponent;

import es.icarto.gvsig.commons.gui.ImageFileChooser;
import es.icarto.gvsig.commons.utils.ImageUtils;
import es.udc.cartolab.gvsig.users.utils.DBSession;

public class AddImageListener implements ActionListener {

	private static final Logger logger = LoggerFactory
			.getLogger(AddImageListener.class);

	private final Connection connection;
	private final ImagesDAO dao;
	private final ImageComponent imageComponent;
	private final JButton addImageButton;
	private final String schema;
	private final String tablename;
	private final String pkField;
	private String pkValue;

	public String getPkValue() {
		return pkValue;
	}

	public void setPkValue(String pkValue) {
		this.pkValue = pkValue;
	}

	public AddImageListener(ImageComponent imageComponent,
			JButton addImageButton, String schema, String tablename,
			String pkField) {
		this.imageComponent = imageComponent;
		this.addImageButton = addImageButton;
		this.schema = schema;
		this.tablename = tablename;
		this.pkField = pkField;

		connection = DBSession.getCurrentSession().getJavaConnection();
		dao = new ImagesDAO();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (hasAlreadyImage()) {
			addImage(true);
		} else {
			addImage(false);
		}
	}

	private void addImage(boolean update) {
		final ImageFileChooser fileChooser = new ImageFileChooser();
		File fileImage = fileChooser.showDialog();
		if (fileImage != null) {
			try {
				BufferedImage image = ImageIO.read(fileImage);
				BufferedImage imageResized = resizeImage(image);
				dao.insertImageIntoDb(connection, schema, tablename, pkField,
						pkValue, imageResized, update);
				JOptionPane.showMessageDialog(null, _("image_msg_added"));
				new ShowImageAction(imageComponent, addImageButton, schema,
						tablename, pkField, pkValue);
			} catch (SQLException e) {
				logger.error(e.getMessage(), e);
				showWarning(_("image_msg_error"));
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
				showWarning(_("image_msg_error"));
			}

		}
	}

	private void showWarning(String msg) {
		JOptionPane.showMessageDialog(
				(Component) PluginServices.getMainFrame(), msg, _("warning"),
				JOptionPane.WARNING_MESSAGE);
	}

	private BufferedImage resizeImage(BufferedImage image) {
		int width = imageComponent.getWidth();
		int height = imageComponent.getHeight();
		Dimension scaledDim = ImageUtils.getScaledDimension(
				new Dimension(image.getWidth(), image.getHeight()),
				new Dimension(width, height));
		BufferedImage imageResized;
		if ((image.getWidth() < width) && (image.getHeight() < height)) {
			return image;
		}

		imageResized = ImageUtils.resizeImageWithHint(image,
				(int) scaledDim.getWidth(), (int) scaledDim.getHeight());
		// if (image.getWidth() > image.getHeight()) {
		// } else {
		// imageResized = ImageUtils.resizeImageWithHint(image, 136, 241);
		// }
		return imageResized;
	}

	private boolean hasAlreadyImage() {
		try {
			byte[] image = dao.readImageFromDb(connection, schema, tablename,
					pkField, pkValue);
			if (image != null) {
				return true;
			} else {
				return false;
			}
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
		}
		return false;
	}
}
