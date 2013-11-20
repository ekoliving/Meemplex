package org.openmaji.implementation.server.config.ui;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;

public class ServersPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private JTabbedPane serversTabbedPane = null;
	private PropertiesPanel primaryPanel = null;
	private PropertiesPanel secondaryPanel = null;
	private PropertiesPanel intermajikPanel = null;

	/**
	 * This is the default constructor
	 */
	public ServersPanel() {
		super();
		initialize();
	}
	
	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
		this.setSize(300, 200);
		this.setLayout(new BorderLayout());
		this.add(getServersTabbedPane(), BorderLayout.CENTER);
	}

	/**
	 * This method initializes serversTabbedPane	
	 * 	
	 * @return javax.swing.JTabbedPane	
	 */
	private JTabbedPane getServersTabbedPane() {
		if (serversTabbedPane == null) {
			serversTabbedPane = new JTabbedPane();
			serversTabbedPane.setName("");
			serversTabbedPane.addTab("primary", null, getPrimaryPanel(), null);
			serversTabbedPane.addTab("secondary", null, getSecondaryPanel(), null);
			serversTabbedPane.addTab("intermajik", null, getIntermajikPanel(), null);
		}
		return serversTabbedPane;
	}

	/**
	 * This method initializes primaryPanel	
	 * 	
	 * @return org.openmaji.implementation.server.config.ui.PropertiesPanel	
	 */
	private PropertiesPanel getPrimaryPanel() {
		if (primaryPanel == null) {
			primaryPanel = new PropertiesPanel();
			primaryPanel.setPropertiesPath("/Applications/Majitek/EdgeSystem/EdgeServer/primary/scripts/sysconfig/maji-edgeServer-primary.properties");
		}
		return primaryPanel;
	}

	/**
	 * This method initializes secondaryPanel	
	 * 	
	 * @return org.openmaji.implementation.server.config.ui.PropertiesPanel	
	 */
	private PropertiesPanel getSecondaryPanel() {
		if (secondaryPanel == null) {
			secondaryPanel = new PropertiesPanel();
			secondaryPanel.setPropertiesPath("/Applications/Majitek/EdgeSystem/EdgeServer/primary/scripts/sysconfig/maji-edgeServer-secondary.properties");
		}
		return secondaryPanel;
	}

	/**
	 * This method initializes intermajikPanel	
	 * 	
	 * @return org.openmaji.implementation.server.config.ui.PropertiesPanel	
	 */
	private PropertiesPanel getIntermajikPanel() {
		if (intermajikPanel == null) {
			intermajikPanel = new PropertiesPanel();
			intermajikPanel.setPropertiesPath("/Applications/Majitek/EdgeSystem/InterMajik/scripts/sysconfig/maji-edgeServer-primary.properties");

		}
		return intermajikPanel;
	}

}
