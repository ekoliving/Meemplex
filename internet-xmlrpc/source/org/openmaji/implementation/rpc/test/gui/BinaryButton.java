package org.openmaji.implementation.rpc.test.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.logging.Logger;

import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;

import org.openmaji.common.Binary;

/**
 * A button that sends and receives Binary facet messages.
 *  
 * @author stormboy
 *
 */
public class BinaryButton extends JToggleButton implements Binary {

	private static final long serialVersionUID = 0L;
	
	private Binary binaryOutput = new Binary() {
		public void valueChanged(boolean value) {
			Object[] listeners = binaryListeners.toArray();
			for (int i=0; i<listeners.length; i++) {
				Binary listener = (Binary) listeners[i];
				listener.valueChanged(value);
			}
		};		
	};

	private HashSet<Binary> binaryListeners = new HashSet<Binary>();

	public BinaryButton() {
//		addChangeListener(
//				new ChangeListener() {
//					public void stateChanged(ChangeEvent e) {
//						Logger.getAnonymousLogger().info(getName() + ": state change: " + e);
//						binaryOutput.valueChanged(isSelected());
//					}
//				}
//				);
		
		addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						Logger.getAnonymousLogger().info(getName() + ": action: " + e);
						binaryOutput.valueChanged(isSelected());
					}
				}			
			);
		
//		addPropertyChangeListener(
//				new PropertyChangeListener() {
//					public void propertyChange(PropertyChangeEvent evt) {
//						Logger.getAnonymousLogger().info("property change: " + evt);
//					}
//				}
//			);
//		addVetoableChangeListener(
//				new VetoableChangeListener() {
//					public void vetoableChange(PropertyChangeEvent evt) throws PropertyVetoException {
//						Logger.getAnonymousLogger().info("property: " + evt.getPropertyName());
//						//throw new PropertyVetoException("", evt);
//					}
//				}
//				);
	}
		
	public void addListener(Binary listener) {
		binaryListeners.add(listener);
	}
	
	public void removeListener(Binary listener) {
		binaryListeners.remove(listener);
	}
	
	public void valueChanged(final boolean value) {
		Logger.getAnonymousLogger().info(getName() + ": got binary: " + value);
		if (value != isSelected()) {
			//Logger.getAnonymousLogger().info(getName() + ": got binary: " + value);
			SwingUtilities.invokeLater(
					new Runnable() {
						public void run() {
							setSelected(value);
						}
					}
					);
		}
	}
	

}
