/*
 * Copyright 2007 by GSDEC.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
package org.openmaji.implementation.common;

import org.openmaji.common.Binary;
import org.openmaji.common.StringValue;
import org.openmaji.common.Value;
import org.openmaji.common.Variable;
import org.openmaji.meem.Wedge;
import org.openmaji.meem.wedge.configuration.ConfigurationClient;
import org.openmaji.meem.wedge.configuration.ConfigurationClientAdapter;
import org.openmaji.meem.wedge.configuration.ConfigurationSpecification;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClient;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClientAdapter;


/**
 * 
 * @author Warren Bloomer
 * 
 */
public class BinaryVariableAdapterWedge implements Wedge {

	
	/* ---------------------------------- conduits --------------------------------- */
	
	public Binary binaryStateConduit;

	public Binary binaryControlConduit = new Binary() {
		public void valueChanged(boolean value) {
			String str = value ? trueValue : falseValue;
			variableStateConduit.valueChanged(new StringValue(str));
			binaryStateConduit.valueChanged(value);
		};
	};
	
	public Variable variableStateConduit;
	
	public Variable variableControlConduit = new Variable() {
		public void valueChanged(Value value) {
			String str = value.toString();
			if (trueValue.equals(str)) {
				binaryStateConduit.valueChanged(true);			
				variableStateConduit.valueChanged(value);
			}
			else if (falseValue.equals(str)) {
				binaryStateConduit.valueChanged(false);			
				variableStateConduit.valueChanged(value);
			}
			else {
				// do nothing
			}
		};
	};
	
	/**
	 * The conduit through which this Wedge alerts errors in configuration
	 * changes
	 */
	public ConfigurationClient configurationClientConduit = new ConfigurationClientAdapter(this);

	/**
	 * The conduit through which we are alerted to life cycle changes
	 */
	public LifeCycleClient lifeCycleClientConduit = new LifeCycleClientAdapter(this);

	
	/* ------------------------------- persisted properties ------------------------------- */
	
	/**
	 * A configuration property to specify which Binary input value the wedge is
	 * looking after
	 */
	public String trueValue = "true";
	
	public String falseValue = "false";

	public transient ConfigurationSpecification trueValueSpecification = new ConfigurationSpecification("The value that represents \"true\"");
	
	public transient ConfigurationSpecification falseValueSpecification = new ConfigurationSpecification("The value that represents \"false\"");

	
	/* ------------------------- configuration methods ----------------------------------- */

	public void setTrueValue(String string) {
		this.trueValue = string;
	}
	
	public String getTrueValue() {
		return this.trueValue ;
	}
	
	public void setFalseValue(String string) {
		this.falseValue = string;
	}
	
	public String getFalseValue() {
		return this.falseValue;
	}
}
