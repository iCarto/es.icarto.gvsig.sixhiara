package es.icarto.gvsig.sixhiara.forms.images;

import static es.icarto.gvsig.commons.i18n.I18n._;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jeta.forms.components.image.ImageComponent;

import es.udc.cartolab.gvsig.users.utils.DBSession;

public class DeleteImageListener implements ActionListener {

	private static final Logger logger = LoggerFactory.getLogger(DeleteImageListener.class);

	private final Connection connection;
	private final JButton deleteImageButton;
	private final ImagesDAO dao;
	private final ImageComponent imageComponent;
	private final String schema;
	private final String tablename;
	private final String pkField;
	private String pkValue;

	public DeleteImageListener(ImageComponent imageComponent, JButton deleteImageButton, String schema,
			String tablename, String pkField) {
		this.imageComponent = imageComponent;
		this.deleteImageButton = deleteImageButton;
		this.schema = schema;
		this.tablename = tablename;
		this.pkField = pkField;

		connection = DBSession.getCurrentSession().getJavaConnection();
		dao = new ImagesDAO();
	}

	public String getPkValue() {
		return pkValue;
	}

	public void setPkValue(String pkValue) {
		this.pkValue = pkValue;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			Object[] options = { _("delete"), _("cancel") };
			int response = JOptionPane.showOptionDialog(null, _("img_delete_warning"), _("delete"),
					JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
			if (response == JOptionPane.YES_OPTION) {
				dao.deleteImageFromDb(connection, schema, tablename, pkField, pkValue);
				new ShowImageAction(imageComponent, deleteImageButton, schema, tablename, pkField, pkValue);
			}
		} catch (SQLException e1) {
			logger.error(e1.getMessage(), e1);
		}
	}

}
