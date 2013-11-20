package org.openmaji.implementation.server.config.ui;

import javax.swing.JPanel;
import javax.swing.JButton;
import java.awt.FlowLayout;

public class ToolPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private JButton addServerButton = null;
	private JButton removeServerButton = null;

	/**
	 * This is the default constructor
	 */
	public ToolPanel() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 */
	private void initialize() {
		this.setSize(300, 118);
		this.setLayout(new FlowLayout());
		this.add(getAddServerButton(), null);
		this.add(getRemoveServerButton(), null);
	}

	/**
	 * This method initializes addServerButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getAddServerButton() {
		if (addServerButton == null) {
			addServerButton = new JButton();
			addServerButton.setText("add server");
		}
		return addServerButton;
	}

	/**
	 * This method initializes removeServerButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getRemoveServerButton() {
		if (removeServerButton == null) {
			removeServerButton = new JButton();
			removeServerButton.setText("remove server");
		}
		return removeServerButton;
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
