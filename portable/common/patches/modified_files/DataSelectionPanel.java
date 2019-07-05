/**
 * gvSIG. Desktop Geographic Information System.
 *
 * Copyright (C) 2007-2013 gvSIG Association.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 *
 * For any additional information, do not hesitate to contact us
 * at info AT gvsig.com, or visit our website www.gvsig.com.
 */
package org.gvsig.datalocator.gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.TreeSet;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gvsig.andami.PluginServices;
import org.gvsig.andami.ui.mdiManager.IWindow;
import org.gvsig.andami.ui.mdiManager.WindowInfo;
import org.gvsig.app.ApplicationLocator;
import org.gvsig.datalocator.DataLocatorExtension;
import org.gvsig.fmap.dal.exception.DataException;
import org.gvsig.fmap.dal.feature.Feature;
import org.gvsig.fmap.dal.feature.FeatureAttributeDescriptor;
import org.gvsig.fmap.dal.feature.FeatureQuery;
import org.gvsig.fmap.dal.feature.FeatureSelection;
import org.gvsig.fmap.dal.feature.FeatureSet;
import org.gvsig.fmap.dal.feature.FeatureStore;
import org.gvsig.fmap.dal.feature.FeatureType;
import org.gvsig.fmap.geom.DataTypes;
import org.gvsig.fmap.geom.primitive.Envelope;
import org.gvsig.fmap.geom.primitive.Point;
import org.gvsig.fmap.mapcontext.MapContext;
import org.gvsig.fmap.mapcontext.layers.FLayer;
import org.gvsig.fmap.mapcontext.layers.vectorial.FLyrVect;
import org.gvsig.fmap.mapcontext.rendering.strategies.SelectedZoomVisitor;
import org.gvsig.tools.dataTypes.DataType;
import org.gvsig.tools.dispose.DisposableIterator;
import org.gvsig.tools.evaluator.Evaluator;
import org.gvsig.tools.evaluator.EvaluatorData;
import org.gvsig.tools.evaluator.EvaluatorException;
import org.gvsig.tools.evaluator.EvaluatorFieldsInfo;


/**
 *
 * This panel lets the user choose a vector layer from a view and a field and a value
 * and then it zooms to the extent of the features which match the value.
 *
 * @author jldominguez
 */
public class DataSelectionPanel extends JPanel
implements IWindow, ActionListener {

	private static final long serialVersionUID = 1L;

	private static final Logger logger =
        LoggerFactory.getLogger(DataSelectionPanel.class);

    private JComboBox layerComboBox = null;
	private JLabel jLabel = null;
	private JLabel jLabel1 = null;
	private JComboBox fieldValueComboBox = null;
	private JLabel jLabel2 = null;
	private JComboBox fieldNameComboBox = null;
    private WindowInfo viewInfo = null;
	private JButton goButton = null;
	private JButton closeButton = null;
	private FLayer layerToZoom = null;
	private int fieldToZoomIndex = 0;
	private Object itemToZoom = null;
	private MapContext mapCtxt = null;
	private JPanel jPanelButtons = null;


	public DataSelectionPanel(MapContext mapContext) {
		super();
		this.mapCtxt = mapContext;
        initialize();
	}


	private void initialize() {
        jLabel2 = new JLabel();
        jLabel1 = new JLabel();
        jLabel = new JLabel();
        this.setLayout(new BorderLayout());
        this.setBorder(new EmptyBorder(new Insets(0,5,5,5)));
        this.setSize(275, 90);
        jLabel.setText(PluginServices.getText(this,"Capa") + ":");
        jLabel1.setText(PluginServices.getText(this,"Campo") + ":");
        jLabel2.setText(PluginServices.getText(this,"Valor") + ":");

        // Panel Central
        GridBagLayout gbl = new GridBagLayout();
        JPanel centerPanelGrid = new JPanel(gbl);
        GridBagConstraints c = new GridBagConstraints();

        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.LINE_START;
        c.insets = new Insets(3,3,3,3);
        c.weightx = 0;

        c.gridx = 0;
        c.gridy = 0;
        centerPanelGrid.add(jLabel, c);

        c.gridx = 0;
        c.gridy = 1;
        centerPanelGrid.add(jLabel1, c);

        c.gridx = 0;
        c.gridy = 2;
        centerPanelGrid.add(jLabel2, c);

        c.anchor = GridBagConstraints.CENTER;
        c.weightx = 1;

        c.gridx = 1;
        c.gridy = 0;
        centerPanelGrid.add(getLayersComboBox(), c);

        c.gridx = 1;
        c.gridy = 1;
        centerPanelGrid.add(getFieldsComboBox(), c);

        c.gridx = 1;
        c.gridy = 2;
        centerPanelGrid.add(getValuesComboBox(), c);

        this.add(centerPanelGrid, BorderLayout.CENTER);


        // Panel Sur
        jPanelButtons = new JPanel();
        FlowLayout flowLayor = new FlowLayout(FlowLayout.RIGHT);
        jPanelButtons.setLayout(flowLayor);
        jPanelButtons.add(getGoButton(), null);
        jPanelButtons.add(getCloseButton(), null);
        this.add(jPanelButtons, BorderLayout.SOUTH);
	}


	private FeatureAttributeDescriptor[] getFields(FLyrVect vect) {

		FeatureStore featureStore;
		try {
            featureStore = vect.getFeatureStore();
            FeatureType fty = featureStore.getDefaultFeatureType();
            FeatureAttributeDescriptor[] atts = fty.getAttributeDescriptors();
            return atts;
        } catch (DataException e) {
            logger.error("While getting field names.", e);
            return new FeatureAttributeDescriptor[0];
		}

	}


	private Object[] getNewValues(FLyrVect vlayer, String fname) {

		FeatureStore featureStore;
		if (vlayer == null || fname == null) {
		    return new Object[0];
		}

		FeatureSet set = null;

		try {
			featureStore = vlayer.getFeatureStore();
			FeatureQuery query = featureStore.createFeatureQuery();
			Evaluator myEvaluator = new ValueEvaluator(fname);
			query.setFilter(myEvaluator);
			query.setAttributeNames(new String[] { fname });
			set = featureStore.getFeatureSet(query);
			/*
			 * Removing duplicated and sorting
			 */
			TreeSet<Object> treeSet =
					new TreeSet<Object>(new Comparator<Object>() {
						public int compare(Object o1, Object o2) {
							if (o1 instanceof Number && o2 instanceof Number) {
								if (((Number) o1).doubleValue() < ((Number) o2).doubleValue())
									return -1;
								if (((Number) o1).doubleValue() > ((Number) o2).doubleValue())
									return 1;
							} else if (o1 instanceof String
									&& o2 instanceof String) {
							    final Collator collator = Collator.getInstance();
							    collator.setStrength(Collator.NO_DECOMPOSITION);
							    return collator.compare((String)o1, (String)o2);
								//return ((String) o1).compareTo((String) o2);
							} else if (o1 instanceof Date && o2 instanceof Date) {
								return ((Date) o1).compareTo((Date) o2);
							}
							return 0;
						}
					});
			DisposableIterator diter = set.fastIterator();
			Feature feat = null;
			while (diter.hasNext()) {
			    feat = (Feature) diter.next();
			    treeSet.add(feat.get(fname));
			}
			diter.dispose();
			return treeSet.toArray();

		} catch (DataException e) {
		    logger.error("While getting sample values.", e);
		    return new Object[0];
		}
	}


    public WindowInfo getWindowInfo() {
        if (viewInfo == null) {
            viewInfo = new WindowInfo(WindowInfo.MODALDIALOG);
            viewInfo.setTitle(PluginServices.getText(this,"Localizador_por_atributo"));
            viewInfo.setHeight(this.getHeight());
            viewInfo.setWidth(this.getWidth());
        }
        return viewInfo;
    }
	/**
	 * This method initializes jComboBox
	 *
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getLayersComboBox() {
		if (layerComboBox == null) {
			layerComboBox = new JComboBox();

			List<FLyrVect> vs = DataLocatorExtension.getVectorLayers(mapCtxt.getLayers());
			LayersComboItem[] items = new LayersComboItem[vs.size()];
			for (int i=0; i<vs.size(); i++) {
			    items[i] = new LayersComboItem(vs.get(i));
			}
            DefaultComboBoxModel defaultModel = new DefaultComboBoxModel(items);
            layerComboBox.setModel(defaultModel);
			layerComboBox.addActionListener(this);
			if (items.length > 0) {
			    layerComboBox.setSelectedIndex(0);
			}
		}
		return layerComboBox;
	}

	/**
	 * This method initializes jComboBox1
	 *
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getFieldsComboBox() {
		if (fieldNameComboBox == null) {
		    fieldNameComboBox = new JComboBox();
		    fieldNameComboBox.addActionListener(this);
		    fieldNameComboBox.setEnabled(false);
		}
		return fieldNameComboBox;
	}


	/**
	 * This method initializes jComboBox2
	 *
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getValuesComboBox() {
		if (fieldValueComboBox == null) {
		    fieldValueComboBox = new JComboBox();
		    fieldValueComboBox.addActionListener(this);
		    fieldValueComboBox.setEnabled(false);
		}
		return fieldValueComboBox;
	}




	/**
	 * This method initializes jButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getGoButton() {
		if (goButton == null) {
		    goButton = new JButton();
		    goButton.setPreferredSize(new Dimension(80, 23));
		    goButton.setText(PluginServices.getText(this,"_Go"));
		    goButton.addActionListener(this);
		}
		return goButton;
	}
	/**
	 * This method initializes jButton1
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getCloseButton() {
		if (closeButton == null) {
		    closeButton = new JButton();
		    closeButton.setPreferredSize(new Dimension(80, 23));
		    closeButton.setText(PluginServices.getText(this,"_Close"));
		    closeButton.addActionListener(this);
		}
		return closeButton;
	}



	public Object getWindowProfile() {
		return WindowInfo.DIALOG_PROFILE;
	}

	class ValueEvaluator implements Evaluator {

		private String fieldName = null;
		private Object value = null;
		private EvaluatorFieldsInfo info = null;
		private boolean nonnull = true;
		private DataType dataType = null;

	    /**
	     * Non null
	     *
	     * @param name
	     */
		public ValueEvaluator(String name) {
		    fieldName = name;
		    info = new EvaluatorFieldsInfo();
		    info.addFieldValue(name);
		    nonnull = true;
		}

		/**
		 * This value
		 * @param name
		 * @param val
		 */
		public ValueEvaluator(String name, Object val, DataType dtype) {

		    fieldName = name;
			info = new EvaluatorFieldsInfo();
			info.addFieldValue(name);
			value = val;
			nonnull = false;
			dataType = dtype;
		}

		public Object evaluate(EvaluatorData data)
				throws EvaluatorException {

			Object obj = data.getDataValue(fieldName);
			if (nonnull) {
			    /*
			     * Accepts non null
			     */
	            if (obj == null) {
	                return Boolean.FALSE;
	            } else {
	                return Boolean.TRUE;
	            }
			} else {
			    return (value == null && obj == null)
			        ||
			        (value != null && obj != null && value.equals(obj));
			}
		}

		public String getSQL() {
		    String resp = null;
		    if (nonnull) {
		        resp = fieldName + " is not null";
		    } else {
		        if (value == null) {
		            resp = fieldName + " is null";
		        } else {
		            String quote = dataType.isNumeric() ? "" : "'";
		            resp = fieldName + " = "  + quote + value.toString() + quote;
		        }
		    }
		    return resp;
		}

		public String getDescription() {
			return "Evaluates if a field is not null";
		}

		public EvaluatorFieldsInfo getFieldsInfo() {
			return this.info;
		}

		public String getName() {
			return this.getClass().getName();
		}

    }

    public void actionPerformed(ActionEvent e) {

        Object src = e.getSource();
        if (src == this.getLayersComboBox()) {

            LayersComboItem litem =
                (LayersComboItem) getLayersComboBox().getSelectedItem();
            if (litem != null) {
                FeatureAttributeDescriptor[] atts = getFields(litem.getLayer());
                if (atts.length > 0) {
                    getFieldsComboBox().setEnabled(true);
                    getFieldsComboBox().removeAllItems();

                    for (int i=0; i<atts.length; i++) {
                        if (atts[i].getType() != DataTypes.GEOMETRY) {
                            getFieldsComboBox().addItem(new FieldsComboItem(atts[i]));
                        }
                    }
                    getFieldsComboBox().setSelectedIndex(0);
                    /*
                     * OK
                     */
                    return;
                }
            }
            /*
             * Not OK
             */
            getFieldsComboBox().setEnabled(false);
            getValuesComboBox().setEnabled(false);
            return;
        }

        // ==========================================================
        // ==========================================================

        if (src == this.getFieldsComboBox()) {

            LayersComboItem litem =
                (LayersComboItem) getLayersComboBox().getSelectedItem();
            FieldsComboItem fatt =
                (FieldsComboItem) getFieldsComboBox().getSelectedItem();
            if (litem != null && fatt != null) {

                Object[] vals = this.getNewValues(litem.getLayer(), fatt.toString());
                if (vals.length > 0) {

                    this.getValuesComboBox().setEnabled(true);
                    this.getValuesComboBox().removeAllItems();
                    DefaultComboBoxModel model = new DefaultComboBoxModel(vals);
                    getValuesComboBox().setModel(model);
                    getValuesComboBox().setSelectedIndex(0);
                    this.getGoButton().setEnabled(true);
                    /*
                     * OK
                     */
                    return;
                }
            }
            /*
             * Not OK
             */
            this.getValuesComboBox().setEnabled(false);
            this.getGoButton().setEnabled(false);
            return;
        }

        // ==========================================================
        // ==========================================================

        if (src == this.getValuesComboBox()) {

            return;
        }

        // ==========================================================
        // ==========================================================

        if (src == this.getCloseButton()) {
            ApplicationLocator.getManager().getUIManager().closeWindow(this);
            return;
        }

        // ==========================================================
        // ==========================================================

        if (src == this.getGoButton()) {

            LayersComboItem litem =
                (LayersComboItem) getLayersComboBox().getSelectedItem();
            FieldsComboItem fatt = (FieldsComboItem)
                getFieldsComboBox().getSelectedItem();
            Object value = this.getValuesComboBox().getSelectedItem();
            if (litem == null || fatt == null || value == null) {
                // Problem with combo boxes
                return;
            }
            FLyrVect lyr = litem.getLayer();
            FeatureStore featureStore;
            FeatureSet featureSet = null;
            DisposableIterator diter = null;
            try {
                featureStore = lyr.getFeatureStore();
                FeatureQuery query = featureStore.createFeatureQuery();
                Evaluator myEvaluator =
                    new ValueEvaluator(fatt.toString(), value, fatt.getType());
                query.setFilter(myEvaluator);
                String geoname =
                    featureStore.getDefaultFeatureType().getDefaultGeometryAttributeName();
                query.setAttributeNames(new String[] { fatt.toString(), geoname });
                featureSet = featureStore.getFeatureSet(query);
                SelectedZoomVisitor visitor = new SelectedZoomVisitor();
                featureSet.accept(visitor);
                Envelope env_data = visitor.getSelectBound();

                if (env_data != null) {
                    if (lyr.getCoordTrans() != null) {
                        env_data = env_data.convert(lyr.getCoordTrans());
                    }
                    if ((env_data.getMaximum(0) - env_data.getMinimum(0) == 0)
                            || (env_data.getMaximum(1) - env_data.getMinimum(1) == 0)) {
                    	Point lowerCorner = env_data.getLowerCorner();
                    	lowerCorner.move(-1000, -1000);
                    	Point upperCorner = env_data.getUpperCorner();
                    	upperCorner.move(+1000, +1000);
                    	env_data.setLowerCorner(lowerCorner);
                    	env_data.setUpperCorner(upperCorner);
                    }
                    this.mapCtxt.getViewPort().setEnvelope(env_data);
                }
            // =======================================

            } catch (Exception exc) {
                logger.error("While zooming to value", exc);
            }
            return;
        }

    }


}
