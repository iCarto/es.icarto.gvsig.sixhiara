package es.icarto.gvsig.sixhiara.plots;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.Map;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import javax.swing.JButton;
import javax.swing.JPanel;

import org.gvsig.andami.ui.mdiManager.IWindow;
import org.gvsig.andami.ui.mdiManager.IWindowListener;
import org.gvsig.andami.ui.mdiManager.WindowInfo;

import es.icarto.gvsig.sixhiara.forms.ChartToImage;

@SuppressWarnings("serial")
public class ChartPanel extends JPanel implements IWindow, IWindowListener {

	private JFXPanel jfxPanel = new JFXPanel();
	private WebEngine engine;
	private Scene scene;

	private WindowInfo windowInfo;
	private final Map<String, Number[]> data;
	private int firstYear;
	private int lastYear;
	private String chartLabels;
	private String datasets;

	public ChartPanel(Map<String, Number[]> data, int firstYear, int lastYear) {
		super(new BorderLayout());
		this.firstYear = firstYear;
		this.lastYear = lastYear;
		this.data = data;
		prepareData();
		Platform.setImplicitExit(false);
		initComponents();
	}

	public void prepareData() {
		this.chartLabels = "var chartLabels = [";
		for (int i = firstYear; i <= lastYear; i++) {
			chartLabels += "'" + i + "',";
		}
		chartLabels = chartLabels.substring(0, chartLabels.length() - 1) + "]";
		// 'January', 'February', 'March', 'April', 'May', 'June'];"
		String template = " { label: '%s', fill: false, lineTension: 0.1, backgroundColor: 'rgba(75,192,192,0.4)', borderColor: 'rgba(75,192,192,1)', borderCapStyle: 'butt', borderDash: [], borderDashOffset: 0.0, borderJoinStyle: 'miter', pointBorderColor: 'rgba(75,192,192,1)', pointBackgroundColor: '#fff', pointBorderWidth: 1, pointHoverRadius: 5, pointHoverBackgroundColor: 'rgba(75,192,192,1)', pointHoverBorderColor: 'rgba(220,220,220,1)', pointHoverBorderWidth: 2, pointRadius: 1, pointHitRadius: 10, data: %s, spanGaps: false, },";

		datasets = "var datasets = [";
		for (String key : data.keySet()) {
			String myData = "[";
			for (Number n : data.get(key)) {
				myData += (n == null ? "null" : n.toString()) + ",";
			}
			myData = myData.substring(0, myData.length() - 1) + "]";
			datasets += String.format(template, key, myData);
		}
		datasets += "]";

	}

	private void initComponents() {
		createScene();
		jfxPanel.setPreferredSize(new Dimension(550, 550));
		add(jfxPanel, BorderLayout.CENTER);
		initToolBar();
		setPreferredSize(new Dimension(650, 650));
	}

	private void initToolBar() {
		JPanel panel = new JPanel();
		JButton bt = new JButton("go");
		bt.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						ChartToImage chartToImage = new ChartToImage();
						chartToImage.snapshot(scene);
					}
				});

			}
		});
		panel.add(bt);
		this.add(panel, BorderLayout.WEST);
	}

	private void createScene() {

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				WebView view = new WebView();
				view.setCache(false);
				engine = view.getEngine();
				scene = new Scene(view);
				jfxPanel.setScene(scene);
				System.out.println("go");
			}
		});
	}

	public void load() {

		URL resource = getClass().getClassLoader().getResource("chart.html");
		String url = resource.toExternalForm();
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				System.out.println("go2");
				engine.load(url);
				// Add a Java callback object to a WebEngine document once it
				// has
				// loaded.
				engine.getLoadWorker()
				.stateProperty()
				.addListener(
						(ObservableValue<? extends State> ov,
										State oldState, State newState) -> {
									engine.executeScript(chartLabels);
									engine.executeScript(datasets);
									engine.executeScript("document.myChart()");
								});
			}
		});
	}

	@Override
	public WindowInfo getWindowInfo() {
		if (windowInfo == null) {
			windowInfo = new WindowInfo(WindowInfo.MODELESSDIALOG);
			windowInfo.setHeight(850);
			windowInfo.setWidth(950);
		}
		return windowInfo;
	}

	@Override
	public Object getWindowProfile() {
		return null;
	}

	@Override
	public void windowActivated() {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowClosed() {
		engine = null;
		scene = null;
		jfxPanel = null;
		// Platform.exit();
	}

}