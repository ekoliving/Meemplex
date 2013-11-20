package org.openmaji.implementation.server.config.ui;

import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import java.awt.GridBagConstraints;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JButton;

public class PropertiesPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private String serverName;
	private String propertiesPath;	
	private DefaultTableModel tableModel = null;  //  @jve:decl-index=0:
	private JScrollPane propertiesScrollPane = null;
	private JTable propertiesTable = null;

	private JPanel titlePanel = null;

	private JLabel titleLabel = null;

	private JPanel actionPanel = null;

	private JButton stopServerButton = null;

	private JButton startServerButton = null;

	private JLabel statusLabel = null;

	/**
	 * This is the default constructor
	 */
	public PropertiesPanel() {
		super();
		initialize();
	}
	
	public void setServerName(String name) {
		this.serverName = name;
	}
	
	public String getServerName() {
		return serverName;
	}
	
	public void setPropertiesPath(String path) {
		this.propertiesPath = path;
		
		// TODO load properties file and populate table
		Properties properties = new Properties();
		
		try {
			InputStream inStream = new FileInputStream(path);
			properties.load(inStream);
			setProperties(properties);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String getPropertiesPath() {
		return propertiesPath;
	}


	public void setProperties(Properties properties) {
		// disregard existing rows
		getTableModel().setRowCount(0);
		
		Enumeration keyEnum = properties.keys();
		TreeSet sortedSet = new TreeSet();
		sortedSet.addAll(properties.keySet());

		while (keyEnum.hasMoreElements()) {
			String key = (String) keyEnum.nextElement();
			String value = properties.getProperty(key);
			getTableModel().addRow( new Object[] { key, value } );
		}
	}
	
	public Properties getProperties() {
		Properties properties = new Properties();
		Vector rows = getTableModel().getDataVector();
		Iterator rowIter = rows.iterator();
		while (rowIter.hasNext()) {
			Vector row = (Vector) rowIter.next();
			String key = (String) row.get(0);
			String value = (String) row.get(1);
			properties.setProperty(key, value);
		}
		
		return properties;
	}

	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
		this.setSize(300, 200);
		this.setLayout(new BorderLayout());
		this.add(getPropertiesScrollPane(), BorderLayout.CENTER);
		this.add(getTitlePanel(), BorderLayout.NORTH);
		this.add(getActionPanel(), BorderLayout.SOUTH);
	}

	/**
	 * This method initializes propertiesScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getPropertiesScrollPane() {
		if (propertiesScrollPane == null) {
			propertiesScrollPane = new JScrollPane();
			propertiesScrollPane.setViewportView(getPropertiesTable());
		}
		return propertiesScrollPane;
	}

	/**
	 * This method initializes propertiesTable	
	 * 	
	 * @return javax.swing.JTable	
	 */
	private JTable getPropertiesTable() {
		if (propertiesTable == null) {
			propertiesTable = new JTable();
			propertiesTable.setModel(getTableModel());
		}
		return propertiesTable;
	}
	
	private DefaultTableModel getTableModel() {
		if (tableModel == null) {
			tableModel = new PropertiesTableModel();
			tableModel.addRow(new Object[] { "server.name", "unknown" } );
		}
		return tableModel;
	}

	/**
	 * This method initializes titlePanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getTitlePanel() {
		if (titlePanel == null) {
			titleLabel = new JLabel();
			titleLabel.setText("server");
			titlePanel = new JPanel();
			titlePanel.setLayout(new GridBagLayout());
			titlePanel.setVisible(false);
			titlePanel.add(titleLabel, new GridBagConstraints());
		}
		return titlePanel;
	}

	/**
	 * This method initializes actionPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getActionPanel() {
		if (actionPanel == null) {
			statusLabel = new JLabel();
			statusLabel.setText("status");
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 2;
			gridBagConstraints.gridy = 0;
			actionPanel = new JPanel();
			actionPanel.setLayout(new GridBagLayout());
			actionPanel.add(statusLabel, new GridBagConstraints());
			actionPanel.add(getStopServerButton(), new GridBagConstraints());
			actionPanel.add(getStartServerButton(), gridBagConstraints);
		}
		return actionPanel;
	}

	/**
	 * This method initializes stopServerButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getStopServerButton() {
		if (stopServerButton == null) {
			stopServerButton = new JButton();
			stopServerButton.setText("stop server");
		}
		return stopServerButton;
	}

	/**
	 * This method initializes startServerButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getStartServerButton() {
		if (startServerButton == null) {
			startServerButton = new JButton();
			startServerButton.setText("start server");
		}
		return startServerButton;
	}

}
