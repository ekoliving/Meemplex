package org.openmaji.implementation.rpc.test.gui;

import java.util.HashSet;

import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.openmaji.common.IntegerPosition;
import org.openmaji.common.Linear;
import org.openmaji.common.Position;

public class LinearSlider extends JSlider implements Linear {
	private static final long serialVersionUID = 0L;

	private Linear linearOutput = new Linear() {
		public void valueChanged(Position position) {
			Object[] listeners = linearListeners.toArray();
			for (int i=0; i<listeners.length; i++) {
				Linear listener = (Linear) listeners[i];
				listener.valueChanged(position);
			}
		};
	};
	
	private HashSet<Linear> linearListeners = new HashSet<Linear>();
	
	public LinearSlider() {
		addChangeListener(
				new ChangeListener() {
					public void stateChanged(ChangeEvent e) {
						IntegerPosition position = new IntegerPosition(getValue(), getMinorTickSpacing(), getMinimum(), getMaximum());
						linearOutput.valueChanged(position);
					}
				}
			);
	}
	
	public void addListener(Linear listener) {
		linearListeners.add(listener);
	}
	
	public void removeListener(Linear listener) {
		linearListeners.remove(listener);
	}
	
	public void valueChanged(Position position) {
		if (position.intValue() != getValue()) {
			setMinimum(position.getMinimumAsInt());
			setMaximum(position.getMaximumAsInt());
			setValue(position.intValue());
		}
	}
}
