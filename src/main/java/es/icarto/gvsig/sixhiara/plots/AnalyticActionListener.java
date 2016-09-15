package es.icarto.gvsig.sixhiara.plots;

import static es.icarto.gvsig.commons.i18n.I18n._;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import org.gvsig.andami.PluginServices;
import org.gvsig.app.project.documents.table.TableDocument;
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
import es.icarto.gvsig.sixhiara.forms.FieldUtils;
import es.icarto.gvsig.sixhiara.plots.MaxValues.MaxValue;

public class AnalyticActionListener implements ActionListener {
	private static final String TABLE = "analise";
	private static final String FK_FIELD = "cod_fonte";
	private static final String SCHEMA = "inventario";
	private final String DATE_FIELD = "data_most";
	private final FLyrVect layer;

	private static final Logger logger = LoggerFactory
			.getLogger(AnalyticActionListener.class);

	public AnalyticActionListener(FLyrVect layer) {
		this.layer = layer;
	}

	private List<Field> getFields() {
		URL resource = AnalyticActionListener.this.getClass().getClassLoader()
				.getResource("columns.properties");
		List<Field> fields = FieldUtils.getFields(resource.getPath(), SCHEMA,
				TABLE, Collections.<String> emptyList());
		List<Field> newFields = new ArrayList<Field>();
		List<MaxValue> maxValues = new MaxValues().getMaxValues();
		for (Field f : fields) {
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

	@Override
	public void actionPerformed(ActionEvent e) {

		List<Field> allFields = getFields();
		ChooseFieldDialog dialog = new ChooseFieldDialog(allFields);
		final int firstYear = 2012;
		final int currentYear = Calendar.getInstance().get(Calendar.YEAR);
		if (dialog.open().equals(OkCancelPanel.OK_ACTION_COMMAND)) {
			Field field = dialog.getField();
			if (field == null) {
				String msg = _("not_selected_field");
				Component parent = (Component) PluginServices.getMainFrame();
				JOptionPane.showMessageDialog(parent, msg, "",
						JOptionPane.ERROR_MESSAGE);
				return;
			}

			FeatureSet set = null;
			DisposableIterator it = null;
			FeatureSet analiseSet = null;
			DisposableIterator analiseIt = null;
			try {
				FeatureStore store = layer.getFeatureStore();
				set = store.getFeatureSet();
				FeatureSelection sel = store.getFeatureSelection();
				if (sel.isEmpty()) {
					String msg = _("not_selected_features");
					Component parent = (Component) PluginServices
							.getMainFrame();
					JOptionPane.showMessageDialog(parent, msg, "",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				if (sel.getSelectedCount() > 10) {
					String msg = _("more_than_ten_selected");
					Component parent = (Component) PluginServices
							.getMainFrame();
					JOptionPane.showMessageDialog(parent, msg, "",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				it = set.fastIterator();
				Map<String, Number[]> selectedFontes = new HashMap<String, Number[]>();
				while (it.hasNext()) {
					Feature feat = (Feature) it.next();
					if (sel.isSelected(feat)) {
						String codFonte = feat.getString(FK_FIELD);
						selectedFontes.put(codFonte, new Number[currentYear
								- firstYear + 1]);
					}
				}

				TOCTableManager toc = new TOCTableManager();
				TableDocument tableDocument = toc.getTableDocumentByName(TABLE);
				FeatureStore analiseStore = tableDocument.getStore();
				FeatureQuery query = analiseStore.createFeatureQuery();
				FeatureQueryOrder order = new FeatureQueryOrder();
				order.add(DATE_FIELD, true);
				query.setOrder(order);

				analiseSet = analiseStore.getFeatureSet();
				analiseIt = analiseSet.fastIterator();
				while (analiseIt.hasNext()) {
					Feature feat = (Feature) analiseIt.next();
					String codFonte = feat.getString(FK_FIELD);
					Number[] list = selectedFontes.get(codFonte);
					if (list != null) {
						java.util.Date date = feat.getDate(DATE_FIELD);
						Calendar cal = Calendar.getInstance();
						cal.setTime(date);
						int year = cal.get(Calendar.YEAR);
						double v = feat.getDouble(field.getKey());
						list[year - firstYear] = v;
					}
				}

				AnalyticsChartPanel window = new AnalyticsChartPanel(
						selectedFontes, firstYear, currentYear, field);
				PluginServices.getMDIManager().addCentredWindow(window);
				window.load();

			} catch (Exception ex) {
				logger.error(ex.getMessage(), ex);
			} finally {
				DisposeUtils.disposeQuietly(set);
				DisposeUtils.disposeQuietly(it);
				DisposeUtils.disposeQuietly(analiseSet);
				DisposeUtils.disposeQuietly(analiseIt);
			}
		}

	}

}
