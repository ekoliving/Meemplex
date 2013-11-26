/*
 * Copyright 2007 by GSDEC.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
package org.openmaji.implementation.common;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.openmaji.common.Binary;
import org.openmaji.common.FloatPosition;
import org.openmaji.common.Linear;
import org.openmaji.common.Position;
import org.openmaji.meem.Wedge;
import org.openmaji.meem.wedge.configuration.ConfigurationClient;
import org.openmaji.meem.wedge.configuration.ConfigurationClientAdapter;
import org.openmaji.meem.wedge.configuration.ConfigurationSpecification;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClient;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClientAdapter;


/**
 * "0" represents false while any other value is true.
 * 
 * @author Warren Bloomer
 * 
 */
public class BinaryLinearAdapterWedge implements Wedge {

	private static final Logger logger = Logger.getAnonymousLogger();
	
	private static final boolean DEBUG = false;

	/* ---------------------------------- conduits --------------------------------- */
	
	public Binary binaryStateConduit;

	public Binary binaryControlConduit = new Binary() {
		public void valueChanged(boolean value) {
			float f = value ? trueValue : falseValue;
			FloatPosition position = new FloatPosition(f, 1, falseValue, trueValue);
			if (DEBUG) {
				logger.log(Level.INFO, "got binary: " + value + " sending " + position);
			}
			linearStateConduit.valueChanged(position);
		};
	};
	
	public Linear linearStateConduit;
	
	public Linear linearControlConduit = new Linear() {
		public void valueChanged(Position position) {
			boolean b = (position.floatValue() == trueValue);
			if (DEBUG) {
				logger.log(Level.INFO, "got position: " + position + " sending " + b);
			}
			binaryStateConduit.valueChanged(b);
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

	
	/* ------------------- persisted properties -------------------- */
	
	public float trueValue = 1;
	
	public float falseValue = 0;
	
	/* -------------------------- configuration ----------------------- */
	
	public transient ConfigurationSpecification trueValueSpecification = ConfigurationSpecification.create("The value that represents \"true\"");
	
	public transient ConfigurationSpecification falseValueSpecification = ConfigurationSpecification.create("The value that represents \"false\"");

	public void setTrueValue(String string) {
		this.trueValue = Float.parseFloat(string);
	}
	
	public String getTrueValue() {
		return Float.toString(this.trueValue);
	}
	
	public void setFalseValue(String string) {
		this.falseValue = Float.parseFloat(string);
	}
	
	public String getFalseValue() {
		return Float.toString(this.falseValue);
	}

}
