/*
 * Copyright 2005 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
package org.openmaji.implementation.common;

import org.openmaji.common.Multistate;
import org.openmaji.common.State;
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
import org.openmaji.meem.wedge.lifecycle.WedgeValidationException;



import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * MultistateUnaryAdapterWedge is used to provide a flexible way to transfer Multistate
 * input to Unary output
 * 
 * @author  Diana Huang
 * 
 */
public class MultistateUnaryAdapterWedge implements Multistate, Wedge, WedgeDefinitionProvider {
	private static Logger logger = Logger.getAnonymousLogger();
	/**
	 * Outbound Unary facet
	 */
	public Unary unaryClient;
	/**
	 * Outbound Unary conduit
	 */
	public Unary unaryControlConduit;
	/**
	 * The conduit through which this Wedge alerts errors in configuration changes
	 */
	public ConfigurationClient configurationClientConduit = new ConfigurationClientAdapter(this);
	/**
	 * The conduit through which we are alerted to life cycle changes
	 */
	public LifeCycleClient lifeCycleClientConduit = new LifeCycleClientAdapter(this);
	/**
	 * A configuration property to specify what output string to generate when it receives Binary input value
	 */
	public String stateString;
	public transient ConfigurationSpecification stateStringSpecification = new ConfigurationSpecification("Multistate state string");
	
	/* ---------- ConfigurationChangeHandler listener ------------------------- */

	public void setStateString (String string){
  		if ( DebugFlag.TRACE ) logger.log(Level.FINE, "setStateString() - invoked");
		this.stateString = string;
	}
	
	public void validate() throws WedgeValidationException{
		if ( DebugFlag.TRACE ) logger.log(Level.FINE, "validate() - invoked");
		
		if(stateString==null ){
			throw new WedgeValidationException("can't go ready because stateString is null");
		}
		
	}
	/**
	 * Inbound Multistate facet Implementation
	 */
	public void stateChanged(State state) {
		String stateValue=state.getState();
		
		if (stateValue.equals(stateString.trim())){
			unaryClient.valueChanged();
			unaryControlConduit.valueChanged();
		}
	}
	  
	/* ---------- WedgeDefinitionProvider method(s) ---------------------------- */

	  public WedgeDefinition getWedgeDefinition() {
		  WedgeDefinition wedgeDefinition =WedgeDefinitionFactory.spi.create().inspectWedgeDefinition(this.getClass());
		  WedgeDefinitionUtility.renameFacetIdentifier(wedgeDefinition, "multistate", "multistateInput");
		  WedgeDefinitionUtility.renameFacetIdentifier(wedgeDefinition,"unaryClient","unaryOutput");
	      return(wedgeDefinition);
	  }  
}
