package es.icarto.gvsig.sixhiara.forms;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Set;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;

import com.iver.cit.gvsig.fmap.layers.LayerFactory;
import com.jeta.forms.components.panel.FormPanel;

import es.icarto.gvsig.navtableforms.IValidatableForm;
import es.icarto.gvsig.navtableforms.ormlite.ORMLite;
import es.icarto.gvsig.navtableforms.ormlite.ORMLiteAppDomain;
import es.icarto.gvsig.navtableforms.ormlite.XMLSAXParser;
import es.icarto.gvsig.navtableforms.ormlite.domainvalidator.rules.ValidationRule;
import es.icarto.gvsig.navtableforms.ormlite.domainvalues.DomainValues;
import es.icarto.gvsig.navtableforms.utils.AbeilleParser;

public abstract class CommonMethodsForTestForms {

    private ORMLiteAppDomain ado;
    private FormPanel formPanel;
    private HashMap<String, JComponent> widgets;
    IValidatableForm form = null;

    @BeforeClass
    public static void doSetupBeforeClass() {
	try {
	    initializegvSIGDrivers();
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    private static void initializegvSIGDrivers() throws Exception {
	final String fwAndamiDriverPath = "../_fwAndami/gvSIG/extensiones/com.iver.cit.gvsig/drivers";
	final File baseDriversPath = new File(fwAndamiDriverPath);
	if (!baseDriversPath.exists()) {
	    throw new Exception("Can't find drivers path: "
		    + fwAndamiDriverPath);
	}

	LayerFactory.setDriversPath(baseDriversPath.getAbsolutePath());
	if (LayerFactory.getDM().getDriverNames().length < 1) {
	    throw new Exception("Can't find drivers in path: "
		    + fwAndamiDriverPath);
	}
    }

    @Before
    public void doSetup() {
	ORMLite ormLite = new ORMLite(getMetadataFile());
	ado = ormLite.getAppDomain();
	try {
	    InputStream file = new FileInputStream(getUIFile());
	    formPanel = new FormPanel(file);
	} catch (Exception e) {
	    e.printStackTrace();
	}
	widgets = AbeilleParser.getWidgetsFromContainer(formPanel);
    }

    @Test
    public void testXMLIsValid() throws SAXException {
	boolean thrown = false;
	File file = new File(getMetadataFile());
	assertTrue("File not exists: " + getMetadataFile(), file.exists());
	try {
	    new XMLSAXParser(getMetadataFile());
	} catch (ParserConfigurationException e) {
	    thrown = true;
	} catch (SAXException e) {
	    thrown = true;
	} catch (IOException e) {
	    thrown = true;
	}
	assertFalse(thrown);
    }

    // protected abstract ConnectionParameters getConnectionParameters();

    // TODO: abeille-xml files must be in the classpath, new
    // FormPanel(abeillePath) does't work in other case. To handle this: Run
    // Configuration -> TestTaludesForm -> Classpath -> Advanced -> add
    // extGIA/forms
    // see
    // http://java.net/nonav/projects/abeille/lists/users/archive/2005-06/message/7
    @Test
    public void test_allWidgetsHaveName() {

	for (final JComponent widget : this.widgets.values()) {
	    assertNotNull(widget.getName());
	    assertTrue(widget.getName().trim().length() > 0);
	}
    }

    @Test
    public void test_domainValuesMatchComboBoxesNames() throws Exception {

	final HashMap<String, DomainValues> domainValues = ado
		.getDomainValues();

	for (final String domainValue : domainValues.keySet()) {
	    JComponent cb = this.widgets.get(domainValue);
	    if (!(cb instanceof JComboBox)) {
		fail(domainValue);
	    }
	}
	assertTrue(true);
    }

    @Test
    public void test_validationRulesMatchWidgetNames() throws Exception {

	final HashMap<String, Set<ValidationRule>> validationRules = new HashMap<String, Set<ValidationRule>>();
	for (String key : ado.getDomainValidators().keySet()) {
	    validationRules.put(key, ado.getDomainValidators().get(key)
		    .getRules());
	}
	for (final String validationRule : validationRules.keySet()) {
	    assertNotNull(validationRule, this.widgets.get(validationRule));
	}
    }

    protected abstract String getUIFile();

    protected abstract String getMetadataFile();

}
