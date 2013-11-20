/*
 * Copyright 2005 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
package org.openmaji.implementation.common;

import org.openmaji.common.StringValue;
import org.openmaji.common.Unary;
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
 */
public class UnaryVariableAdapterWedge implements Wedge {


	/* --------------------------- conduits ---------------------------- */
	
	public Variable variableControlConduit = new Variable() {
		public void valueChanged(Value value) {
			if ( UnaryVariableAdapterWedge.this.value.equals(value) ) {
				unaryStateConduit.valueChanged();
			}
		};
	};

	public Variable variableStateConduit;

	public Unary unaryControlConduit = new Unary() {
		public void valueChanged() {
			variableStateConduit.valueChanged(value);
		}
	};

	public Unary unaryStateConduit ;

	/**
	 * The conduit through which this Wedge alerts errors in configuration
	 * changes
	 */
	public ConfigurationClient configurationClientConduit = new ConfigurationClientAdapter(this);

	/**
	 * The conduit through which we are alerted to life cycle changes
	 */
	public LifeCycleClient lifeCycleClientConduit = new LifeCycleClientAdapter(this);


	/* --------------------------- persisted values -------------------------- */
	
	/**
	 * A configuration property to specify which Variable value the wedge will send
	 */
	public Value value = new StringValue("");

	
	/* ---------------------- configuration specification --------------------- */ 
	
	public transient ConfigurationSpecification binaryValueSpecification = new ConfigurationSpecification("true|false");


	/* ---------- ConfigurationChangeHandler listener ------------------------- */

	public void setValue(String string) {
		this.value = new StringValue(string);
	}

	public String getValue() {
		return value.toString();
	}

}
