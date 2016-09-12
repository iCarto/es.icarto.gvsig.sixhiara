package es.icarto.gvsig.sixhiara.forms;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JButton;

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
import es.icarto.gvsig.navtableforms.gui.tables.handler.AlphanumericTableHandler;
import es.icarto.gvsig.navtableforms.utils.TOCTableManager;
import es.icarto.gvsig.sixhiara.plots.ChartPanel;
import es.icarto.gvsig.sixhiara.plots.ChooseFieldDialog;

@SuppressWarnings("serial")
public class FontesForm extends BasicAbstractForm {
	private static final String FK_FIELD = "cod_fonte";
	private static final Logger logger = LoggerFactory
			.getLogger(FontesForm.class);
	public static final String LAYERNAME = "fontes";
	public static final String PKFIELD = FK_FIELD;

	public FontesForm(FLyrVect layer) {
		super(layer);
		addChained("distrito", "provincia");
		addChained("posto_adm", "distrito");
		addTableHandler(new AlphanumericTableHandler(AnaliseSubForm.TABLENAME,
				getWidgetComponents(), PKFIELD, AnaliseSubForm.colNames,
				AnaliseSubForm.colAlias));
		addTableHandler(new AlphanumericTableHandler(
				QuantidadeAguaSubForm.TABLENAME, getWidgetComponents(),
				PKFIELD, QuantidadeAguaSubForm.colNames,
				QuantidadeAguaSubForm.colAlias));
		addAnalyticsButton();
	}

	private void addAnalyticsButton() {
		java.net.URL imgURL = getClass().getClassLoader().getResource(
				"images/analytics.png");
		JButton jButton = new JButton(new ImageIcon(imgURL));
		jButton.setToolTipText("Analise de fontes");

		jButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final List<String> reservedColumns = Arrays
						.asList(new String[] { "gid", "geom", FK_FIELD,
								"fonte", "data_most", "hora_most" });
				final String DATE_FIELD = "data_most";
				List<Field> allFields = getFields(getSchema(), "analise",
						reservedColumns);
				ChooseFieldDialog dialog = new ChooseFieldDialog(allFields);
				final int firstYear = 2012;
				final int currentYear = Calendar.getInstance().get(
						Calendar.YEAR);
				if (dialog.open().equals(OkCancelPanel.OK_ACTION_COMMAND)) {
					Field field = dialog.getField();
					if (field == null) {
						// TODO. Show warning message, or disable ok bt until
						// some field is selected
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
							return;
							// TODO. show msg empty selection or disable ok
						}
						if (sel.getSelectedCount() > 10) {
							// TODO. show msg empty selection or disable ok
							return;
						}
						it = set.fastIterator();
						Map<String, Number[]> selectedFontes = new HashMap<String, Number[]>();
						while (it.hasNext()) {
							Feature feat = (Feature) it.next();
							if (sel.isSelected(feat)) {
								String codFonte = feat.getString(FK_FIELD);
								selectedFontes
										.put(codFonte, new Number[currentYear
												- firstYear + 1]);
							}
						}

						TOCTableManager toc = new TOCTableManager();
						TableDocument tableDocument = toc
								.getTableDocumentByName("analise");
						FeatureStore analiseStore = tableDocument
								.getFeatureStore();
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
								list[year - firstYear] = feat.getDouble(field
										.getKey());
							}
						}

						ChartPanel window = new ChartPanel(selectedFontes,
								firstYear, currentYear);
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

		});
		getActionsToolBar().add(jButton);
	}

	@Override
	protected String getSchema() {
		return "inventario";
	}

	@Override
	protected String getBasicName() {
		return "fontes";
	}

	@Override
	protected String getPrimaryKey() {
		return PKFIELD;
	}
}
