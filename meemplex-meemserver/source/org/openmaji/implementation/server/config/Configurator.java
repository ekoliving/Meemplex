package org.openmaji.implementation.server.config;

import javax.swing.JFrame;

import org.openmaji.implementation.server.config.ui.ConfigPanel;


public class Configurator {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Configurator configurator = new Configurator();
			configurator.doit();
		}
		catch (Throwable t) {
			t.printStackTrace();
		}
	}
	
	
	public void doit() {
		JFrame frame = new JFrame("Edge Configurator");
		ConfigPanel panel = new ConfigPanel();
		frame.getContentPane().add(panel);
		frame.setSize(640, 480);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

}
