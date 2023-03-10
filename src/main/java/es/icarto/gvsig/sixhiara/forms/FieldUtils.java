package es.icarto.gvsig.sixhiara.forms;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import es.icarto.gvsig.commons.utils.Field;
import es.udc.cartolab.gvsig.users.utils.DBSession;

public class FieldUtils {

	private static final Logger logger = Logger.getLogger(FieldUtils.class);

	private final static List<String> reservedColumns = Arrays.asList(new String[] { "gid", "the_geom", "geom" });

	private FieldUtils() {
		throw new AssertionError("Non instantiable class");
	}

	public static List<Field> getFields(String filePath, String schema, String table, List<String> ignoreColumns,
			boolean notNull) {
		List<Field> fields = new ArrayList<Field>();
		try {
			DBSession session = DBSession.getCurrentSession();
			InputStream input = new FileInputStream(filePath);
			Properties props = new Properties();
			props.load(input);
			String[] columns;
			if (notNull) {
				List<String> columnList = session.getColumnsWithNotNulls(schema, table);
				columns = columnList.toArray(new String[0]);
			} else {
				columns = session.getColumns(schema, table);
			}
			List<String> asList = Arrays.asList(columns);

			for (String c : asList) {
				if (ignoreColumns.contains(c)) {
					continue;
				}
				String longname = props.getProperty(schema + "." + table + "." + c, c);
				fields.add(new Field(c, longname));
			}
		} catch (FileNotFoundException e) {
			logger.error(e.getStackTrace(), e);
		} catch (IOException e) {
			logger.error(e.getStackTrace(), e);
		} catch (SQLException e) {
			logger.error(e.getStackTrace(), e);
		}
		return fields;
	}

	public static List<Field> getFields(String filePath, String schema, String table) {
		return getFields(filePath, schema, table, reservedColumns, false);
	}
}
