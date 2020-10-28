package es.icarto.gvsig.sixhiara.plots;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.icarto.gvsig.commons.gui.SaveFileDialog;

final class ExportToPNGActionListener implements ActionListener {
	private static final Logger logger = LoggerFactory.getLogger(ExportToPNGActionListener.class);

	private int height;
	private int width;
	private JFreeChart chart;

	public ExportToPNGActionListener(JFreeChart chart, int width, int height) {
		this.chart = chart;
		this.width = width;
		this.height = height;

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		SaveFileDialog dialog = new SaveFileDialog("Ficheiros PNG", "png");
		File file = dialog.showDialog();
		if (file == null) {
			return;
		}

		try {
			ChartUtilities.saveChartAsPNG(file, chart, width, height);
		} catch (IOException e1) {
			logger.error(e1.getMessage(), e1);
		}
	}
}