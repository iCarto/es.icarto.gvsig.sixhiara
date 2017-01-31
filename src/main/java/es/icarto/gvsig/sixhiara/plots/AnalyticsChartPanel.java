package es.icarto.gvsig.sixhiara.plots;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYTitleAnnotation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.SymbolAxis;
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
import org.jfree.ui.HorizontalAlignment;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.Size2D;
import org.jfree.ui.VerticalAlignment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.icarto.gvsig.commons.gui.AbstractIWindow;
import es.icarto.gvsig.commons.utils.Field;
import es.icarto.gvsig.sixhiara.plots.MaxValues.MaxValue;

@SuppressWarnings("serial")
public class AnalyticsChartPanel extends AbstractIWindow {

	private static final Dimension CHART_SIZE = new java.awt.Dimension(800, 600);

	private static final Logger logger = LoggerFactory
			.getLogger(AnalyticsChartPanel.class);

	private final Field field;
	private final MaxValue maxValue;
	private final Map<String, Object[]> data;
	private final int firstYear;
	private final int currentYear;
	private final JFreeChart chart;

	private XYSeries maxSeries = null;
	private XYSeries minSeries;

	public AnalyticsChartPanel(Map<String, Object[]> selectedFontes,
			int firstYear, int currentYear, Field field) {
		super();
		this.data = selectedFontes;
		this.firstYear = firstYear;
		this.currentYear = currentYear;
		this.field = field;
		maxValue = new MaxValues().getMaxFor(field.getKey());
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
		pngBt.addActionListener(new ExportToPNGActionListener(chart,
				CHART_SIZE.width, CHART_SIZE.height));
		JButton xlsBt = new JButton("XLS");
		xlsBt.addActionListener(new ExportToXLSActionListener(data, firstYear,
				currentYear, field));
		toolbar.add(pngBt);
		toolbar.add(xlsBt);
		this.add(toolbar, "dock south");
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
				Object value = data.get(key)[i];
				if (value != null) {
					if (value instanceof Number) {
						series.add(years[i], (Number) value);
					} else if (value instanceof String) {
						Integer v = maxValue.stringToIndex(value.toString());
						series.add(years[i], v);
					}
				}
			}
			dataset.addSeries(series);
		}
		return dataset;
	}

	private void initMaxMin(XYSeriesCollection dataset, Integer[] years) {

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
		Object[] next = data.values().iterator().next();
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
		renderer.setBaseItemLabelsVisible(!maxValue.isString());

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
		colors.add(new Color(15, 206, 200));
		colors.add(new Color(245, 63, 154));
		colors.add(new Color(26, 152, 80));
		colors.add(new Color(253, 174, 97));
		colors.add(new Color(116, 173, 209));
		colors.add(new Color(255, 255, 191));
		colors.add(new Color(224, 243, 248));

		for (int i = firstSeriesData; i < data.keySet().size()
				+ firstSeriesData; i++) {
			renderer.setSeriesPaint(i, colors.get(i - firstSeriesData));
		}

		plot.setRenderer(renderer);
		initYAxis(plot);
		initXAxis(plot);

		return chart;
	}

	private class MyTextTitle extends TextTitle {

		private LegendTitle legend;

		public MyTextTitle(String s, LegendTitle legend) {
			super(s);
			this.legend = legend;
		}

		@Override
		public Size2D arrange(Graphics2D g2) {
			System.out.println("arrange");
			return super.arrange(g2);
		}

		@Override
		protected Size2D arrangeFN(Graphics2D g2, double w) {
			System.out.println("arrange fn");
			return super.arrangeFN(g2, w);
		}

		@Override
		protected Size2D arrangeRN(Graphics2D g2, Range widthRange) {
			System.out.println("arrange rn");
			return super.arrangeRN(g2, widthRange);
		}

		@Override
		protected Size2D arrangeNN(Graphics2D g2) {
			System.out.println("arrange nn");
			return super.arrangeNN(g2);
		}

		@Override
		protected Size2D arrangeRR(Graphics2D g2, Range widthRange,
				Range heightRange) {
			return super.arrangeRR(g2, widthRange, heightRange);
		}

		@Override
		public Object draw(Graphics2D g2, Rectangle2D area, Object params) {
			return super.draw(g2, area, params);

		}

		@Override
		protected void drawVertical(Graphics2D g2, Rectangle2D area) {
			// super.drawHorizontal(g2, area);
			super.drawVertical(g2, area);
		}
	}

	private void initLegend(JFreeChart chart) {

		RectangleEdge hpos = RectangleEdge.RIGHT;
		VerticalAlignment vpos = VerticalAlignment.CENTER;

		LegendTitle legend = chart.getLegend();
		legend.setPosition(hpos);
		legend.setVerticalAlignment(vpos);

		TextTitle legendText = new MyTextTitle("Fontes", legend);

		chart.addSubtitle(0, legendText);

		legendText.setPosition(hpos);
		legendText.setVerticalAlignment(vpos);

		legendText.setHorizontalAlignment(HorizontalAlignment.CENTER);
		legendText.setTextAlignment(HorizontalAlignment.CENTER);
		// legendText.setWidth(300);
		// legendText.setHeight(100);
		legendText.setExpandToFitSpace(true);
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

	private void initYAxis(final XYPlot plot) {
		if (maxValue.isString()) {
			initYAxisForString(plot);
		} else {
			// change the auto tick unit selection to integer units only...
			final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
			// rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
			if (maxValue.yAxisMax != null) {
				double lower = maxValue.yAxisMin.doubleValue();
				double upper = maxValue.yAxisMax.doubleValue();
				rangeAxis.setRange(lower, upper);
			}
		}
	}

	private void initYAxisForString(XYPlot plot) {
		String[] grade = new String[] { "< 10", "10 - 25", "25 - 50",
				"50 - 100", "100 - 250", "> 250" };
		SymbolAxis rangeAxis = new SymbolAxis(field.getLongName(), grade);
		plot.setRangeAxis(rangeAxis);

	}
}
