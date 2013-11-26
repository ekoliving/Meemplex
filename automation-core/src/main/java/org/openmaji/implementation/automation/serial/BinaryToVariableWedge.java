package org.openmaji.implementation.automation.serial;

import org.openmaji.meem.Wedge;
import org.openmaji.meem.wedge.configuration.ConfigurationClient;
import org.openmaji.meem.wedge.configuration.ConfigurationClientAdapter;
import org.openmaji.meem.wedge.configuration.ConfigurationSpecification;
import org.openmaji.meem.wedge.lifecycle.LifeCycleState;
import org.openmaji.common.Binary;
import org.openmaji.common.StringValue;
import org.openmaji.common.Value;
import org.openmaji.common.Variable;

public class BinaryToVariableWedge implements Wedge, Binary {
	
	
	/* -------------- facets ------------ */
	
	public Binary binaryOutput;
	

	/* ----------- conduits ------------ */
	
	/**
	 * 
	 */
	public Variable variableControlConduit;

	/**
	 * 
	 */
	public Variable variableStateConduit = new VariableStateConduit();

	/**
	 * 
	 */
	public Binary binaryControlConduit = new BinaryControlConduit();
	
	/**
	 * 
	 */
	public Binary binaryStateConduit;
	
	/**
	 * Conduit to allow this wedge to be configured. 
	 */
	public ConfigurationClient configurationClientConduit = new ConfigurationClientAdapter(this);

	
	/* ---------- persisted properties ------------ */
	
	public String onString = "X01\r\n";
	public String offString = "X02\r\n";

	public transient ConfigurationSpecification onStringSpecification  = 
		ConfigurationSpecification.create("The string value to send when the binary is switched off", String.class, LifeCycleState.READY);
	
	public transient ConfigurationSpecification offStringSpecification  = 
		ConfigurationSpecification.create("The string value to send when the binary is switched on", String.class, LifeCycleState.READY);
	
	/* --------------- property methods ----------- */

	public void setOnString(String on) {
		this.onString = on;
	}

	public void setOffString(String off) {
		this.offString = off;
	}
	
	/* -------------- Binary interface ------------ */
	
	public void valueChanged(boolean value) {
		String str = value ? onString : offString;
		variableControlConduit.valueChanged(new StringValue(str));
	}

	
	/* ---------------- inner classes ------------- */
	
	final private class BinaryControlConduit implements Binary {
		public void valueChanged(boolean value) {
			BinaryToVariableWedge.this.valueChanged(value);
		}
	}
	
    /**
     * 
     */
    final private class VariableStateConduit implements Variable {
    	public void valueChanged(Value value) {
    		String str = value.toString();
    		if (onString.equals(str)) {
    			binaryOutput.valueChanged(true);
    			binaryStateConduit.valueChanged(true);
    		}
    		else if (offString.equals(str)) {
    			binaryOutput.valueChanged(false);
    			binaryStateConduit.valueChanged(false);
    		}
    		else {
    			// ignore
    		}
    	}
    }
}
