package es.icarto.gvsig.sixhiara.plots;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYTitleAnnotation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.StandardXYItemLabelGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.Range;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.VerticalAlignment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.icarto.gvsig.commons.gui.AbstractIWindow;
import es.icarto.gvsig.commons.gui.SaveFileDialog;
import es.icarto.gvsig.commons.utils.Field;
import es.icarto.gvsig.sixhiara.plots.MaxValues.MaxValue;

@SuppressWarnings("serial")
public class AnalyticsChartPanel extends AbstractIWindow {

	private static final Dimension CHART_SIZE = new java.awt.Dimension(800, 600);

	private static final Logger logger = LoggerFactory
			.getLogger(AnalyticsChartPanel.class);

	private final Field field;
	private final Map<String, Number[]> data;
	private final int firstYear;
	private final int currentYear;
	private final JFreeChart chart;

	private XYSeries maxSeries = null;

	private XYSeries minSeries;

	public AnalyticsChartPanel(Map<String, Number[]> selectedFontes,
			int firstYear, int currentYear, Field field) {
		super();
		this.data = selectedFontes;
		this.firstYear = firstYear;
		this.currentYear = currentYear;
		this.field = field;
		final XYDataset dataset = createDataset();
		chart = createChart(dataset);
		customizeLimits();
		final ChartPanel chartPanel = new ChartPanel(chart, true, true, true,
				true, true);
		chartPanel.setPreferredSize(CHART_SIZE);
		add(chartPanel);
		initToolbar();
	}

	private void initToolbar() {
		JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
		JLabel label = new JLabel("Gravar em formato: ");
		toolbar.add(label);
		JButton pngBt = new JButton("PNG");
		pngBt.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				SaveFileDialog dialog = new SaveFileDialog("Ficheiros PNG",
						"png");
				File file = dialog.showDialog();
				if (file == null) {
					return;
				}

				try {
					ChartUtilities.saveChartAsPNG(file, chart,
							CHART_SIZE.width, CHART_SIZE.height);
				} catch (IOException e1) {
					logger.error(e1.getMessage(), e1);
				}
			}
		});
		JButton xlsBt = new JButton("XLS");
		xlsBt.addActionListener(new ExportToXLSActionListener(data, firstYear,
				currentYear, field));
		toolbar.add(pngBt);
		toolbar.add(xlsBt);
		this.add(toolbar, "dock south");
	}

	public void load() {
	}

	@Override
	protected JButton getDefaultButton() {
		return null;
	}

	@Override
	protected Component getDefaultFocusComponent() {
		return null;
	}

	private XYDataset createDataset() {
		final XYSeriesCollection dataset = new XYSeriesCollection();
		Integer[] years = getYears();
		initMaxMin(dataset, years);
		for (String key : data.keySet()) {
			final XYSeries series = new XYSeries(key);
			for (int i = 0; i < years.length; i++) {
				if ((data.get(key)[i]) != null) {
					series.add(years[i], data.get(key)[i]);
				}
			}
			dataset.addSeries(series);
		}
		return dataset;
	}

	private void initMaxMin(XYSeriesCollection dataset, Integer[] years) {
		MaxValue maxValue = new MaxValues().getMaxFor(field.getKey());

		if (maxValue != null) {
			if (maxValue.max != null) {
				maxSeries = new XYSeries("Máximo");
				for (int i = 0; i < years.length; i++) {
					maxSeries.add(years[i], maxValue.max);
				}
				dataset.addSeries(maxSeries);
			}
			if (maxValue.min != null) {
				minSeries = new XYSeries("Mínimo");
				for (int i = 0; i < years.length; i++) {
					minSeries.add(years[i], maxValue.min);
				}
				dataset.addSeries(minSeries);
			}
		}

	}

	private void customizeLimits() {
		XYPlot plot = chart.getXYPlot();
		if (maxSeries != null) {
			TextTitle textTitle = new TextTitle("Máximo");
			textTitle.setPaint(Color.RED);
			textTitle.setPadding(0, 0, 5, 0);
			double yyy = relativeRangeValue(maxSeries.getMaxY());
			XYTitleAnnotation xyTitleAnnotation = new XYTitleAnnotation(0.5,
					yyy, textTitle, RectangleAnchor.BOTTOM);
			plot.addAnnotation(xyTitleAnnotation);
		}

		if (minSeries != null) {
			TextTitle textTitle = new TextTitle("Mínimo");
			textTitle.setPaint(Color.BLUE);
			textTitle.setPadding(5, 0, 0, 0);
			double yyy = relativeRangeValue(minSeries.getMinY());

			XYTitleAnnotation xyTitleAnnotation = new XYTitleAnnotation(0.5,
					yyy, textTitle, RectangleAnchor.TOP);
			plot.addAnnotation(xyTitleAnnotation);
		}
	}

	private double relativeRangeValue(double value) {
		XYPlot plot = chart.getXYPlot();
		ValueAxis rangeAxis = plot.getRangeAxis();
		Range yRange = rangeAxis.getRange();
		return (value - yRange.getLowerBound()) / yRange.getLength();
	}

	private Integer[] getYears() {
		Number[] next = data.values().iterator().next();
		Integer[] years = new Integer[next.length];

		Integer year = Integer.valueOf(firstYear);
		for (int i = 0; i < years.length; i++) {
			years[i] = year;
			year++;
		}
		return years;
	}

	private JFreeChart createChart(final XYDataset dataset) {

		final JFreeChart chart = ChartFactory.createXYLineChart(
				field.getLongName(), // chart title
				"Ano", // x axis label
				field.getLongName(), // y axis label
				dataset, // data
				PlotOrientation.VERTICAL, true, // include legend
				true, // tooltips
				false // urls
				);
		initLegend(chart);

		chart.setBackgroundPaint(Color.white);

		final XYPlot plot = chart.getXYPlot();

		plot.setBackgroundPaint(Color.lightGray);
		plot.setDomainGridlinePaint(Color.white);
		plot.setRangeGridlinePaint(Color.white);

		final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
		renderer.setBaseItemLabelGenerator(new StandardXYItemLabelGenerator());
		renderer.setBaseItemLabelsVisible(true);

		int firstSeriesData = 0;
		if (maxSeries != null) {
			renderer.setSeriesShapesVisible(0, false);
			renderer.setSeriesStroke(0, new BasicStroke(3));
			renderer.setSeriesItemLabelsVisible(0, false);
			renderer.setSeriesVisibleInLegend(0, Boolean.FALSE);
			renderer.setSeriesPaint(0, Color.RED);
			firstSeriesData++;
		}
		if (minSeries != null) {
			renderer.setSeriesShapesVisible(1, false);
			renderer.setSeriesStroke(1, new BasicStroke(3));
			renderer.setSeriesItemLabelsVisible(1, false);
			renderer.setSeriesVisibleInLegend(1, Boolean.FALSE);
			renderer.setSeriesPaint(1, Color.BLUE);
			firstSeriesData++;
		}

		List<Color> colors = new ArrayList<Color>();
		colors.add(new Color(153, 112, 171));
		colors.add(new Color(140, 81, 10));
		colors.add(new Color(244, 109, 67));
		colors.add(new Color(253, 174, 97));
		colors.add(new Color(254, 224, 144));
		colors.add(new Color(255, 255, 191));
		colors.add(new Color(224, 243, 248));
		colors.add(new Color(171, 217, 233));
		colors.add(new Color(116, 173, 209));
		colors.add(new Color(26, 152, 80));

		for (int i = firstSeriesData; i < data.keySet().size()
				+ firstSeriesData; i++) {
			renderer.setSeriesPaint(i, colors.get(i - firstSeriesData));
		}

		plot.setRenderer(renderer);
		// change the auto tick unit selection to integer units only...
		final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		initXAxis(plot);

		return chart;
	}

	private void initLegend(JFreeChart chart) {
		LegendTitle legend = chart.getLegend();
		legend.setPosition(RectangleEdge.RIGHT);
		legend.setVerticalAlignment(VerticalAlignment.TOP);
		legend.setMargin(7, 5, 0, 7);
	}

	/**
	 * X Axis shows only integers without miles separator
	 */
	private void initXAxis(XYPlot plot) {
		NumberAxis domainAxis = (NumberAxis) plot.getDomainAxis();
		domainAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		NumberFormat format = NumberFormat.getIntegerInstance();
		format.setGroupingUsed(false);
		domainAxis.setNumberFormatOverride(format);
	}

}
