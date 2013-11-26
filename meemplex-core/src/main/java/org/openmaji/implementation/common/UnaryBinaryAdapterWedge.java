/*
 * Copyright 2005 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
package org.openmaji.implementation.common;

import org.openmaji.common.Binary;
import org.openmaji.common.Unary;
import org.openmaji.meem.Wedge;
import org.openmaji.meem.definition.WedgeDefinition;
import org.openmaji.meem.definition.WedgeDefinitionFactory;
import org.openmaji.meem.definition.WedgeDefinitionProvider;
import org.openmaji.meem.definition.WedgeDefinitionUtility;
import org.openmaji.meem.wedge.configuration.ConfigurationClient;
import org.openmaji.meem.wedge.configuration.ConfigurationClientAdapter;
import org.openmaji.meem.wedge.configuration.ConfigurationSpecification;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClient;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClientAdapter;


/**
 * The Wedge is used to receive a Unary command and then invoke a Binary
 * outbound upon a configuration property.
 * 
 * @author Diana Huang
 * 
 */
public class UnaryBinaryAdapterWedge implements Unary, Wedge, WedgeDefinitionProvider {

	
	/* ---------------------- outbound facets -------------------------- */
	/**
	 * Outbound Binary facet
	 */
	public Binary binaryClient;


	/* --------------------------- conduits ---------------------------- */
	
	/**
	 * Outbound Binary conduit
	 */
	public Binary binaryControlConduit;

	/**
	 * Inbound Unary conduit
	 */
	public Unary unaryStateConduit = new UnaryStateConduit();

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
	 * A configuration property to specify which Binary input value the wedge is
	 * looking after
	 */
	public boolean binaryValue = true;

	
	/* ---------------------- configuration specification --------------------- */ 
	
	public transient ConfigurationSpecification binaryValueSpecification = ConfigurationSpecification.create("true|false");


	/* ---------- ConfigurationChangeHandler listener ------------------------- */

	public void setBinaryValue(String string) {
		this.binaryValue = Boolean.parseBoolean(string);
	}

	public String getBinaryValue() {
		return Boolean.toString(binaryValue);
	}


	/*
	 * public void validate() throws WedgeValidationException{ if (
	 * DebugFlag.TRACE ) logger.log(Level.FINE, "validate() - invoked");
	 * 
	 * if(binaryValue==null){ throw new WedgeValidationException("can't go ready
	 * because binaryValue is null"); }
	 *  }
	 */

	public void valueChanged() {
		binaryClient.valueChanged(binaryValue);
		binaryControlConduit.valueChanged(binaryValue);
	}

	/* ---------- WedgeDefinitionProvider method(s) ---------------------------- */
	public WedgeDefinition getWedgeDefinition() {
		WedgeDefinition wedgeDefinition = WedgeDefinitionFactory.spi.create().inspectWedgeDefinition(this.getClass());
		WedgeDefinitionUtility.renameFacetIdentifier(wedgeDefinition, "unary", "unaryInput");
		WedgeDefinitionUtility.renameFacetIdentifier(wedgeDefinition, "binaryClient", "binaryOutput");
		return (wedgeDefinition);
	}

	private class UnaryStateConduit implements Unary {
		public void valueChanged() {
			binaryClient.valueChanged(binaryValue);
			binaryControlConduit.valueChanged(binaryValue);
		}
	}

}
