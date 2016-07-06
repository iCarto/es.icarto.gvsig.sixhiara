package es.icarto.gvsig.sixhiara.forms;


import org.gvsig.fmap.mapcontext.layers.vectorial.FLyrVect;

import es.icarto.gvsig.navtableforms.AbstractForm;
import es.icarto.gvsig.navtableforms.gui.tables.AbstractSubForm;
import es.icarto.gvsig.navtableforms.utils.DBConnectionBaseFormFactory;
import es.icarto.gvsig.navtableforms.utils.FormFactory;
import es.icarto.gvsig.sixhiara.preferences.DBNames;

public class SixhiaraFormFactory extends DBConnectionBaseFormFactory {

    private static SixhiaraFormFactory instance = null;

    static {
	instance = new SixhiaraFormFactory();
    }

    public static void registerFormFactory() {
	FormFactory.registerFormFactory(instance);
    }

    public static SixhiaraFormFactory getInstance() {
	return instance;
    }

    @Override
    public AbstractForm createForm(FLyrVect layer) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public AbstractForm createSingletonForm(FLyrVect layer) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public AbstractForm createForm(String layerName) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public AbstractForm createSingletonForm(String layerName) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public AbstractSubForm createSubForm(String tableName) {
	if (tableName != null) {
	    if (tableName.equals(FountainsAnalyticalSubForm.TABLENAME)) {
		return new FountainsAnalyticalSubForm();
	    } else if (tableName
		    .equals(FountainsFlowrateGaugingSubForm.TABLENAME)) {
		return new FountainsFlowrateGaugingSubForm();
	    } else if (tableName
		    .equals(StationsPluviometricDataSubForm.TABLENAME)) {
		return new StationsPluviometricDataSubForm();
	    } else if (tableName
		    .equals(StationsHydrometricDataSubForm.TABLENAME)) {
		return new StationsHydrometricDataSubForm();
	    }
	}
	return null;
    }

    @Override
    public boolean hasMainForm(String layerName) {
	// TODO Auto-generated method stub
	return false;
    }

    @Override
    public boolean allLayersLoaded() {
	// TODO Auto-generated method stub
	return false;
    }

    

    @Override
    public void loadLayer(String layerName) {
	// TODO Auto-generated method stub

    }

	@Override
	public void loadTable(String tableName) {
		/*
		 * As nt formfactory only allows register one factory we should do this
		 * ugly if instead of have a factory for each project
		 */
		loadTable(tableName, DBNames.SCHEMA);
	    }
}
