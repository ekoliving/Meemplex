package org.openmaji.implementation.server.config.ui;

import javax.swing.table.DefaultTableModel;

public class PropertiesTableModel extends DefaultTableModel {

	private static final long serialVersionUID = 0L;
	
	PropertiesTableModel() {
		super( new Object[] { "property", "value" }, 0 );
	}
	
	public boolean isCellEditable(int row, int column) {
		return column > 0;
	}
}
