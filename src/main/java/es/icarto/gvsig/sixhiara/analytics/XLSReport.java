package es.icarto.gvsig.sixhiara.analytics;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.swing.table.DefaultTableModel;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.WorkbookUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.icarto.gvsig.commons.queries.QueryFiltersI;
import es.icarto.gvsig.commons.utils.Field;
import es.icarto.gvsig.commons.xls.XLSFormatUtils;
import es.udc.cartolab.gvsig.navtable.format.DateFormatNT;

public class XLSReport {

	private static final Logger logger = LoggerFactory.getLogger(XLSReport.class);

	private static final String PATTERN = DateFormatNT.getDateFormat().toPattern();
	private final Workbook wb;
	protected Sheet sheet;
	protected QueryFiltersI filters;
	protected int colNamesRowIdx;
	private boolean[] columnsStyles;

	final int firstColIdx = 0;
	final int lastColIdx = 10;

	public class EmptyFilter implements QueryFiltersI {

		@Override
		public Collection<Field> getLocation() {
			return new ArrayList<>(0);
		}

		@Override
		public List<Field> getFields() {
			return new ArrayList<>(0);
		}

		@Override
		public boolean getSeleccionados() {
			return true; // hack para no tocar el resto del código
		}
	}

	public XLSReport() {
		this.wb = new HSSFWorkbook(); // xls
	}

	public Sheet insertSheet(String sheetname, DefaultTableModel table, QueryFiltersI filters) {
		if (filters == null) {
			this.filters = new EmptyFilter();
		} else {
			this.filters = filters;
		}
		this.columnsStyles = new boolean[table.getColumnCount()];
		Arrays.fill(this.columnsStyles, false);
		if (!this.filters.getSeleccionados()) {
			this.colNamesRowIdx = filters.getLocation().size() + 1;
		} else {
			this.colNamesRowIdx = 0;
		}

		final String safeName = WorkbookUtil.createSafeSheetName(sheetname);
		this.sheet = this.wb.createSheet(safeName);

		writeFilters();
		writeTable(table);
		stylizeSheet();

		this.sheet.setAutoFilter(
				new CellRangeAddress(this.colNamesRowIdx, this.colNamesRowIdx, 0, table.getColumnCount() - 1));
		return this.sheet;
	}

	public void writeToDisk(File outputFile) {
		try {
			_writeToDisk(outputFile);
		} catch (final IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

	private void stylizeSheet() {
		XLSFormatUtils.autoSizeColumns(this.sheet, this.colNamesRowIdx);
		final CellRangeAddress autoFilterRange = new CellRangeAddress(this.colNamesRowIdx, this.colNamesRowIdx,
				this.firstColIdx, this.lastColIdx);
		this.sheet.setAutoFilter(autoFilterRange);
	}

	protected void writeFilters() {
		if (!this.filters.getSeleccionados()) {
			short rowIdx = 0;

			for (final Field l : this.filters.getLocation()) {
				final Row row = this.sheet.createRow(rowIdx++);
				String longName = l.getLongName().trim();
				longName = longName.endsWith(":") ? longName.substring(0, longName.length() - 1) : longName;
				row.createCell(0).setCellValue(longName);
				row.createCell(1).setCellValue(l.getValue().toString());
			}
		}
	}

	private void writeTable(DefaultTableModel table) {
		writeColumnNames(table);
		writeRows(table);
	}

	private void writeColumnNames(DefaultTableModel table) {
		final Row row0 = this.sheet.createRow(this.colNamesRowIdx);

		for (int i = 0; i < table.getColumnCount(); i++) {
			final Cell cell = row0.createCell(i);
			cell.setCellValue(table.getColumnName(i));
			setHeaderBackground(cell);
		}
	}

	private void writeRows(DefaultTableModel tableModel) {
		for (int rowIdx = 0; rowIdx < tableModel.getRowCount(); rowIdx++) {
			final Row row = this.sheet.createRow(rowIdx + this.colNamesRowIdx + 1);
			for (int column = 0; column < tableModel.getColumnCount(); column++) {
				final Object value = tableModel.getValueAt(rowIdx, column);
				createCell(row, column, value);
			}
		}
	}

	private void _writeToDisk(File outputFile) throws IOException {
		FileOutputStream fileOut = null;
		try {
			fileOut = new FileOutputStream(outputFile);
			this.wb.write(fileOut);
		} finally {
			if (fileOut != null) {
				fileOut.close();
			}
			if (this.wb != null) {
				this.wb.close();
			}
		}
	}

	private void createCell(Row row, int column, Object value) {
		if (value == null) {
			row.createCell(column);
		} else if (value instanceof String) {
			row.createCell(column).setCellValue((String) value);
		} else if (value instanceof Date) {
			if (!this.columnsStyles[column]) {
				this.columnsStyles[column] = true;
				final CellStyle cellStyle = this.wb.createCellStyle();
				final CreationHelper creationHelper = this.wb.getCreationHelper();
				cellStyle.setDataFormat(creationHelper.createDataFormat().getFormat(PATTERN));
				this.sheet.setDefaultColumnStyle(column, cellStyle);
			}
			row.createCell(column).setCellValue((Date) value);
		} else if (value instanceof Number) {
			final Number doubleValue = (Number) value;
			row.createCell(column).setCellValue(doubleValue.doubleValue());
		} else if (value instanceof Boolean) {
			row.createCell(column).setCellValue((Boolean) value ? "Sí" : "No");
		} else {
			throw new AssertionError("This should never happen");
		}
	}

	private void setHeaderBackground(Cell cell) {
		// Aqua background
		final CellStyle style = this.wb.createCellStyle();
		style.setFillForegroundColor(IndexedColors.AQUA.getIndex());
		style.setFillPattern(CellStyle.SOLID_FOREGROUND);
		cell.setCellStyle(style);

	}

}