package es.icarto.gvsig.sixhiara.navtableforms;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;

import javax.swing.JComponent;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import es.icarto.gvsig.navtableforms.gui.tables.handler.AlphanumericTableHandler;
import es.udc.cartolab.gvsig.navtable.format.DateFormatNT;

public class SortedAlphanumericTableHandler extends AlphanumericTableHandler {

	public SortedAlphanumericTableHandler(String tableName, HashMap<String, JComponent> widgets, String foreignKeyId,
			String[] colNames) {
		super(tableName, widgets, foreignKeyId, colNames);
	}

	public SortedAlphanumericTableHandler(String tableName, HashMap<String, JComponent> widgets, String foreignKeyId,
			String[] colNames, String[] colAliases) {
		super(tableName, widgets, foreignKeyId, colNames, colAliases);
	}

	@Override
	public void fillValues(String foreignKeyValue) {
		super.fillValues(foreignKeyValue);
		TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(jtable.getModel());
		sorter.setSortsOnUpdates(true);
		for (int i = 0; i < colNames.length; i++) {
			if (colNames[i].startsWith("data")) {
				sorter.setComparator(i, new Comparator<String>() {

					@Override
					public int compare(String o1, String o2) {
						Date d1 = DateFormatNT.convertStringToDate(o1);
						Date d2 = DateFormatNT.convertStringToDate(o2);
						if (d1 == null) {
							return 1;
						}
						if (d2 == null) {
							return -1;
						}
						return d1.compareTo(d2);
					}
				});
				javax.swing.RowSorter.SortKey sk = new RowSorter.SortKey(i, SortOrder.ASCENDING);
				sorter.setSortKeys(Arrays.asList(new RowSorter.SortKey[] { sk }));
				break;
			}
		}
		jtable.setRowSorter(sorter);
	}

}
