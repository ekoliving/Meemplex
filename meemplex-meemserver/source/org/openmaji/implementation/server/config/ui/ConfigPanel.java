package org.openmaji.implementation.server.config.ui;

import javax.swing.JPanel;
import java.awt.BorderLayout;

public class ConfigPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private ServersPanel serversPanel = null;
	private ToolPanel toolPanel = null;

	/**
	 * This is the default constructor
	 */
	public ConfigPanel() {
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
		this.add(getServersPanel(), BorderLayout.CENTER);
		this.add(getToolPanel(), BorderLayout.NORTH);
	}

	/**
	 * This method initializes serversPanel	
	 * 	
	 * @return org.openmaji.implementation.server.config.ui.ServersPanel	
	 */
	private ServersPanel getServersPanel() {
		if (serversPanel == null) {
			serversPanel = new ServersPanel();
		}
		return serversPanel;
	}

	/**
	 * This method initializes toolPanel	
	 * 	
	 * @return org.openmaji.implementation.maji.server.config.ui.ToolPanel	
	 */
	private ToolPanel getToolPanel() {
		if (toolPanel == null) {
			toolPanel = new ToolPanel();
		}
		return toolPanel;
	}

}
