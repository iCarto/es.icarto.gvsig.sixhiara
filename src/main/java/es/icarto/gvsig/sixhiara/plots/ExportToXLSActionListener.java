package es.icarto.gvsig.sixhiara.plots;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import javax.swing.JOptionPane;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.WorkbookUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.icarto.gvsig.commons.gui.SaveFileDialog;
import es.icarto.gvsig.commons.utils.Field;

public class ExportToXLSActionListener implements ActionListener {

	private static final Logger logger = LoggerFactory
			.getLogger(ExportToXLSActionListener.class);

	private final Map<String, Number[]> data;
	private final int firstYear;
	private final int currentYear;
	private final Field field;

	public ExportToXLSActionListener(Map<String, Number[]> selectedFontes,
			int firstYear, int currentYear, Field field) {
		this.data = selectedFontes;
		this.firstYear = firstYear;
		this.currentYear = currentYear;
		this.field = field;
	}

	private void doIt(File file) throws IOException {
		Workbook wb = new HSSFWorkbook();
		String safeName = WorkbookUtil.createSafeSheetName(field.getLongName());
		Sheet sheet = wb.createSheet(safeName);
		int rowIdx = 0;
		Row row0 = sheet.createRow(rowIdx++);
		String[] years = getYears();

		row0.createCell(0).setCellValue("Fonte");
		for (int i = 0; i < years.length; i++) {
			row0.createCell(i + 1).setCellValue(years[i]);
		}

		for (String key : data.keySet()) {
			Row row = sheet.createRow(rowIdx++);
			row.createCell(0).setCellValue(key);
			Number[] values = data.get(key);
			for (int i = 0; i < years.length; i++) {
				Cell cell = row.createCell(i + 1);
				Number value = values[i];
				if (value != null) {
					cell.setCellValue(value.doubleValue());
				}
			}

		}

		for (int i = 0; i < years.length + 1; i++) {
			sheet.autoSizeColumn(i);
		}
		FileOutputStream fileOut = null;
		try {
			fileOut = new FileOutputStream(file);
			wb.write(fileOut);
		} finally {
			if (fileOut != null) {
				fileOut.close();
			}
			if (wb != null) {
				wb.close();
			}
		}

	}

	private String[] getYears() {
		Number[] next = data.values().iterator().next();
		String[] years = new String[next.length];

		int year = firstYear;
		for (int i = 0; i < years.length; i++) {
			years[i] = year + "";
			year++;
		}
		return years;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		SaveFileDialog dialog = new SaveFileDialog("Ficheiros Excel", "xls");
		File file = dialog.showDialog();
		if (file == null) {
			return;
		}
		try {
			doIt(file);
		} catch (IOException e1) {
			JOptionPane.showMessageDialog(null, "Error exportando datos", "",
					JOptionPane.ERROR_MESSAGE);
			logger.error(e1.getMessage(), e1);
		}
	}

}
