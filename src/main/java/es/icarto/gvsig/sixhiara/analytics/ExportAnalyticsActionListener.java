package es.icarto.gvsig.sixhiara.analytics;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.icarto.gvsig.commons.db.ConnectionWrapper;
import es.icarto.gvsig.commons.gui.SaveFileDialog;
import es.icarto.gvsig.sixhiara.forms.BasicAbstractForm;
import es.icarto.gvsig.sixhiara.forms.actions.BaseActionForSubForms;
import es.udc.cartolab.gvsig.users.utils.DBSession;

public class ExportAnalyticsActionListener extends BaseActionForSubForms {

	private static final Logger logger = LoggerFactory.getLogger(ExportAnalyticsActionListener.class);

	public ExportAnalyticsActionListener(BasicAbstractForm form, String subTableName) {
		super(form, subTableName);
	}

	private void doIt(File file) throws IOException {
		final DefaultTableModel data = getData();
		final XLSReport xlsReport = new XLSReport();
		xlsReport.insertSheet("Analise", data, null);
		xlsReport.writeToDisk(file);
	}

	private DefaultTableModel getData() {
		final ConnectionWrapper con = new ConnectionWrapper(DBSession.getCurrentSession().getJavaConnection());
		String query = "";
		if (this.subTableName == "fontes_analise") {
			query = "select f.cadastro \"C�digo Fonte/Cadastro\", f.tip_fonte \"Tipo de Fonte\", f.tipo_agua \"Tipo de �gua\", f.provincia \"Provincia\", f.distrito \"Distrito\", f.posto_adm \"Posto administrativo\", f.nucleo \"Bairro\", f.loc_unidad \"Unidade\", f.bacia \"Bacia\", f.subacia \"Sub-bacia\", a.tipo_aqui \"Tipo de aqu�fero\", a.data_anal \"Data an�lise\", a.temperat \"Temperatura (�C)\", a.turbidez \"Turva��o (NTU)\", a.conductiv \"Conductividade (�S/cm)\", a.ph \"PH\", a.carbonato \"Carbonato (mg/l)\", a.dureza \"Dureza total (mg/l)\", a.oxigeno_d \"Oxig�nio dissol. (mg/l)\", a.sol_suspe \"Solidos suspen��o (mg/l)\", a.nitratos \"Nitratos (mg/l)\", a.nitritos \"Nitritos (mg/l)\", a.coli_feca \"Coliformes fec. (NMP/100ml)\", a.coli_tot \"Coliformes T (NMP/100ml)\", a.cloruros \"Cloretos (mg/l)\", a.ca \"Ca (mg/l)\", a.mg \"Mg (mg/l)\", a.k \"K (mg/l)\", a.na \"Na (mg/l)\", a.sulfatos \"Sulfatos (mg/l)\" from inventario.fontes f JOIN inventario.fontes_analise a ON f.cadastro = a.cadastro ORDER BY f.cadastro, a.data_anal;";
		} else {
			query = "SELECT e.cod_estac \"C�digo esta��o\", e.tip_estac \"Tipo de esta��o\", e.sac \"Pertence ao SAC\", e.provincia \"Provincia\", e.distrito \"Distrito\", e.posto_adm \"Posto administrativo\", e.nucleo \"Bairro\", e.bacia \"Bacia\", e.subacia \"Sub-bacia\", data_med \"Data medi��o\", temperat \"Temperatura (�C)\", cor \"Cor (mg Pt-Co/l)\", turbidez \"Turva��o (NTU)\", conductiv \"Conductividade (�S/cm)\", ph \"PH\", alcalin_f \"Alcalinidade fe. (mg/l)\", alcalinid \"Alcalinidade T (mg/l)\", carbonato \"Carbonato (mg/l)\", bicarbona \"Bicabornato (mg/l)\", dureza \" Dureza total (mg/l)\", oxigeno_d \"Oxig�nio dissol. (mg/l)\", mo \"MO (mg/l)\", sol_suspe \"Solidos suspen��o (mg/l)\", sol_disol \"Solidos dissolv. (mg/l)\", nitratos \"Nitratos (mg/l)\", nitritos \"Nitritos (mg/l)\", coli_feca \"Coliformes fec. (NMP/100ml)\", coli_tot \"Coliformes T (NMP/100ml)\", cloruros \"Cloretos (mg/l)\", fosfatos \"Fosfatos (mg/l)\", sulfatos \"Sulfatos (mg/l)\", ca \"Ca (mg/l)\", mg \"Mg (mg/l)\", amonio \"Amoniaco (mg/l)\", k \"K (mg/l)\", na \"Na (mg/l)\", fe \"Fe (mg/l)\", alt_hidro \"Altura hidrom�trica (m)\", oxigeno_c \"Oxig�nio consumido (mg/l)\", dioxido_l \"CO2 livre (mgCO2/l)\", silica \"Silica (mgSiCO2/l)\", res_seco \"Residuo Seco (18%-mg/l)\", deposito \"Dep�sito\" FROM inventario.estacoes e join inventario.estacoes_analise a on e.cod_estac = a.cod_estac ORDER BY e.cod_estac, a.data_med;";
		}

		final DefaultTableModel data = con.execute(query);
		return data;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		final SaveFileDialog dialog = new SaveFileDialog("Ficheiros Excel", "xls");
		dialog.setAskForOverwrite(true);
		final File file = dialog.showDialog();
		if (file == null) {
			return;
		}
		try {
			doIt(file);
		} catch (final IOException e1) {
			JOptionPane.showMessageDialog(null, "Error exportando datos", "", JOptionPane.ERROR_MESSAGE);
			logger.error(e1.getMessage(), e1);
		}
	}

}
