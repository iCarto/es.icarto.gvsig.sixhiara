package es.icarto.gvsig.sixhiara.plots;

import static es.icarto.gvsig.commons.i18n.I18n._;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import org.gvsig.andami.PluginServices;
import org.gvsig.app.project.documents.table.TableDocument;
import org.gvsig.fmap.dal.exception.DataException;
import org.gvsig.fmap.dal.feature.Feature;
import org.gvsig.fmap.dal.feature.FeatureQuery;
import org.gvsig.fmap.dal.feature.FeatureQueryOrder;
import org.gvsig.fmap.dal.feature.FeatureSelection;
import org.gvsig.fmap.dal.feature.FeatureSet;
import org.gvsig.fmap.dal.feature.FeatureStore;
import org.gvsig.fmap.mapcontext.layers.vectorial.FLyrVect;
import org.gvsig.tools.dispose.DisposableIterator;
import org.gvsig.tools.dispose.DisposeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.icarto.gvsig.commons.gui.OkCancelPanel;
import es.icarto.gvsig.commons.utils.Field;
import es.icarto.gvsig.navtableforms.utils.TOCTableManager;
import es.icarto.gvsig.sixhiara.forms.EstacoesAnaliseSubForm;
import es.icarto.gvsig.sixhiara.forms.FieldUtils;
import es.icarto.gvsig.sixhiara.plots.MaxValues.MaxValue;

public class AnalyticActionListener implements ActionListener {

	private static final String SCHEMA = "inventario";
	private final String table;
	private final String fkField;
	private final String dateField;
	private final FLyrVect layer;

	private static final int currentYear = Calendar.getInstance().get(
			Calendar.YEAR);
	private static final int firstYear = Math.max(2012, currentYear - 10);

	private static final Logger logger = LoggerFactory
			.getLogger(AnalyticActionListener.class);

	public AnalyticActionListener(FLyrVect layer, String table, String fkField,
			String dateField) {
		this.layer = layer;
		this.table = table;
		this.fkField = fkField;
		this.dateField = dateField;
	}

	private List<Field> getFields() {
		URL resource = AnalyticActionListener.this.getClass().getClassLoader()
				.getResource("columns.properties");
		List<Field> fields = FieldUtils.getFields(resource.getPath(), SCHEMA,
				table, Collections.<String> emptyList(), true);
		List<Field> newFields = new ArrayList<Field>();
		List<MaxValue> maxValues = new MaxValues().getMaxValues();
		for (Field f : fields) {
			if (this.table.equals(EstacoesAnaliseSubForm.TABLENAME)) {
				List<String> notForEstacoes = Arrays.asList(new String[] {
						"nitratos", "nitritos", "coli_feca", "coli_tot",
				"amonio" });
				if (notForEstacoes.contains(f.getKey())) {
					continue;
				}
			}
			for (MaxValue m : maxValues) {
				if (m.k.equals(f.getKey())) {
					f.setValue(m);
					newFields.add(f);
					break;
				}
			}
		}

		return newFields;
	}

	private List<String> sourcesToPlot() {
		List<String> selectedFontes = new ArrayList<String>();
		DisposableIterator it = null;
		FeatureSet set = null;
		try {
			FeatureStore store = layer.getFeatureStore();

			FeatureSelection sel = store.getFeatureSelection();
			if (sel.isEmpty()) {
				return selectedFontes;
			}

			set = store.getFeatureSet();
			it = set.fastIterator();
			while (it.hasNext()) {
				Feature feat = (Feature) it.next();
				if (sel.isSelected(feat)) {
					String codFonte = feat.getString(fkField);
					selectedFontes.add(codFonte);
				}
			}
		} catch (DataException e) {

		} finally {
			DisposeUtils.disposeQuietly(it);
			DisposeUtils.disposeQuietly(set);
		}

		return selectedFontes;
	}

	private Map<String, Object[]> sourcesToPlotAsMap() {
		List<String> sourcesToPlot = sourcesToPlot();
		int numberOfYears = currentYear - firstYear + 1;
		Map<String, Object[]> selectedFontes = new HashMap<String, Object[]>();
		for (String s : sourcesToPlot) {
			selectedFontes.put(s, new Object[numberOfYears]);
		}
		return selectedFontes;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Map<String, Object[]> sourcesToPlot = sourcesToPlotAsMap();
		if (sourcesToPlot.isEmpty()) {
			showError("not_selected_features");
			return;
		}
		if (sourcesToPlot.size() > 10) {
			showError("more_than_ten_selected");
			return;
		}

		List<Field> allFields = getFields();
		ChooseFieldDialog dialog = new ChooseFieldDialog(allFields);

		if (!dialog.open().equals(OkCancelPanel.OK_ACTION_COMMAND)) {
			return;
		}
		Field field = dialog.getField();
		if (field == null) {
			showError("not_selected_field");
			return;
		}

		doIt(sourcesToPlot, field);

	}

	private void doIt(Map<String, Object[]> sourcesToPlot, Field field) {
		FeatureSet analiseSet = null;
		DisposableIterator analiseIt = null;
		try {

			analiseSet = getAnaliseSet();
			analiseIt = analiseSet.fastIterator();
			while (analiseIt.hasNext()) {
				Feature feat = (Feature) analiseIt.next();
				String codFonte = feat.getString(fkField);
				Object[] list = sourcesToPlot.get(codFonte);
				if (list != null) {
					int year = yearFromDate(feat);
					if ((year - firstYear) < 0) {
						continue;
					}
					if (feat.get(field.getKey()) == null) {
						list[year - firstYear] = null;
					} else {
						list[year - firstYear] = feat.get(field.getKey());
					}
				}
			}

			AnalyticsChartPanel window = new AnalyticsChartPanel(sourcesToPlot,
					firstYear, currentYear, field);
			PluginServices.getMDIManager().addCentredWindow(window);

		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
		} finally {

			DisposeUtils.disposeQuietly(analiseSet);
			DisposeUtils.disposeQuietly(analiseIt);
		}

	}

	private FeatureSet getAnaliseSet() throws DataException {
		TOCTableManager toc = new TOCTableManager();
		TableDocument tableDocument = toc.getTableDocumentByName(table);
		FeatureStore analiseStore = tableDocument.getStore();
		FeatureQuery query = analiseStore.createFeatureQuery();
		FeatureQueryOrder order = new FeatureQueryOrder();
		order.add(dateField, true);
		query.setOrder(order);
		FeatureSet analiseSet = analiseStore.getFeatureSet(query);
		return analiseSet;
	}

	private int yearFromDate(Feature feat) {
		java.util.Date date = feat.getDate(dateField);
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int year = cal.get(Calendar.YEAR);
		return year;
	}

	private void showError(String message) {
		String msg = _(message);
		Component parent = (Component) PluginServices.getMainFrame();
		JOptionPane.showMessageDialog(parent, msg, "",
				JOptionPane.ERROR_MESSAGE);

	}

}
