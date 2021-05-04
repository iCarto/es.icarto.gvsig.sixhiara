package es.icarto.gvsig.sixhiara.plots;

import static es.icarto.gvsig.commons.i18n.I18n._;

import java.awt.Component;
import java.awt.event.ActionEvent;
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
import org.gvsig.andami.ui.mdiManager.MDIManagerFactory;
import org.gvsig.fmap.dal.exception.DataException;
import org.gvsig.fmap.dal.feature.Feature;
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
import es.icarto.gvsig.sixhiara.forms.BasicAbstractForm;
import es.icarto.gvsig.sixhiara.forms.EstacoesAnaliseSubForm;
import es.icarto.gvsig.sixhiara.forms.FieldUtils;
import es.icarto.gvsig.sixhiara.forms.actions.BaseActionForSubForms;
import es.icarto.gvsig.sixhiara.plots.MaxValues.MaxValue;

public class AnalyticActionListener extends BaseActionForSubForms {

	private static final String SCHEMA = "inventario";
	private final String fkField;
	private final String dateField;
	private final FLyrVect layer;

	private static final int currentYear = Calendar.getInstance().get(Calendar.YEAR);
	private static final int firstYear = Math.max(2012, currentYear - 10);

	private static final Logger logger = LoggerFactory.getLogger(AnalyticActionListener.class);

	public AnalyticActionListener(BasicAbstractForm form, String subTableName, String fkField, String dateField) {
		super(form, subTableName);
		this.layer = form.getLayer();
		this.fkField = fkField;
		this.dateField = dateField;
	}

	private List<Field> getFields() {
		final URL resource = AnalyticActionListener.this.getClass().getClassLoader().getResource("columns.properties");
		final List<Field> fields = FieldUtils.getFields(resource.getPath(), SCHEMA, this.subTableName,
				Collections.<String>emptyList(), true);
		final List<Field> newFields = new ArrayList<>();
		final List<MaxValue> maxValues = new MaxValues().getMaxValues();
		for (final Field f : fields) {
			if (this.subTableName.equals(EstacoesAnaliseSubForm.TABLENAME)) {
				final List<String> notForEstacoes = Arrays.asList("nitratos", "nitritos", "coli_feca", "coli_tot",
						"amonio");
				if (notForEstacoes.contains(f.getKey())) {
					continue;
				}
			}
			for (final MaxValue m : maxValues) {
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
		final List<String> selectedFontes = new ArrayList<>();
		DisposableIterator it = null;
		FeatureSet set = null;
		try {
			final FeatureStore store = this.layer.getFeatureStore();

			final FeatureSelection sel = store.getFeatureSelection();
			if (sel.isEmpty()) {
				return selectedFontes;
			}

			set = store.getFeatureSet();
			it = set.fastIterator();
			while (it.hasNext()) {
				final Feature feat = (Feature) it.next();
				if (sel.isSelected(feat)) {
					final String codFonte = feat.getString(this.fkField);
					selectedFontes.add(codFonte);
				}
			}
		} catch (final DataException e) {

		} finally {
			DisposeUtils.disposeQuietly(it);
			DisposeUtils.disposeQuietly(set);
		}

		return selectedFontes;
	}

	private Map<String, Object[]> sourcesToPlotAsMap() {
		final List<String> sourcesToPlot = sourcesToPlot();
		final int numberOfYears = currentYear - firstYear + 1;
		final Map<String, Object[]> selectedFontes = new HashMap<>();
		for (final String s : sourcesToPlot) {
			selectedFontes.put(s, new Object[numberOfYears]);
		}
		return selectedFontes;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		final Map<String, Object[]> sourcesToPlot = sourcesToPlotAsMap();
		if (sourcesToPlot.isEmpty()) {
			showError("not_selected_features");
			return;
		}
		if (sourcesToPlot.size() > 10) {
			showError("more_than_ten_selected");
			return;
		}

		final List<Field> allFields = getFields();
		final ChooseFieldDialog dialog = new ChooseFieldDialog(allFields);

		if (!dialog.open().equals(OkCancelPanel.OK_ACTION_COMMAND)) {
			return;
		}
		final Field field = dialog.getField();
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

			analiseSet = getSubFormFeatureSet(this.dateField);
			analiseIt = analiseSet.fastIterator();
			while (analiseIt.hasNext()) {
				final Feature feat = (Feature) analiseIt.next();
				final String codFonte = feat.getString(this.fkField);
				final Object[] list = sourcesToPlot.get(codFonte);
				if (list != null) {
					final int year = yearFromDate(feat);
					if (year - firstYear < 0) {
						continue;
					}
					if (feat.get(field.getKey()) == null) {
						list[year - firstYear] = null;
					} else {
						list[year - firstYear] = feat.get(field.getKey());
					}
				}
			}

			final AnalyticsChartPanel window = new AnalyticsChartPanel(sourcesToPlot, firstYear, currentYear, field);
			MDIManagerFactory.getManager().addCentredWindow(window);

		} catch (final Exception ex) {
			logger.error(ex.getMessage(), ex);
		} finally {

			DisposeUtils.disposeQuietly(analiseSet);
			DisposeUtils.disposeQuietly(analiseIt);
		}
	}

	private int yearFromDate(Feature feat) {
		final java.util.Date date = feat.getDate(this.dateField);
		final Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		final int year = cal.get(Calendar.YEAR);
		return year;
	}

	private void showError(String message) {
		final String msg = _(message);
		final Component parent = (Component) PluginServices.getMainFrame();
		JOptionPane.showMessageDialog(parent, msg, "", JOptionPane.ERROR_MESSAGE);

	}

}
