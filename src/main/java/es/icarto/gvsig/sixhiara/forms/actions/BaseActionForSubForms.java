package es.icarto.gvsig.sixhiara.forms.actions;

import java.awt.event.ActionListener;

import org.gvsig.app.project.documents.table.TableDocument;
import org.gvsig.fmap.dal.exception.DataException;
import org.gvsig.fmap.dal.feature.FeatureQuery;
import org.gvsig.fmap.dal.feature.FeatureSet;
import org.gvsig.fmap.dal.feature.FeatureStore;


import es.icarto.gvsig.navtableforms.utils.TOCTableManager;
import es.icarto.gvsig.sixhiara.forms.BasicAbstractForm;

public abstract class BaseActionForSubForms implements ActionListener {
	
	protected final BasicAbstractForm form;
	protected final String subTableName;

	public BaseActionForSubForms(BasicAbstractForm form, String subTableName) {
		this.form = form;
		this.subTableName = subTableName;
	}
	
	protected FeatureSet getSubFormFeatureSet(String orderByField) throws DataException {
		TOCTableManager toc = new TOCTableManager();
		TableDocument tableDocument = toc.getTableDocumentByName(subTableName);
		FeatureStore tableStore = tableDocument.getStore();
		FeatureQuery query = tableStore.createFeatureQuery("", orderByField, true);
		FeatureSet analiseSet = tableStore.getFeatureSet(query);
		return analiseSet;
	}

}
