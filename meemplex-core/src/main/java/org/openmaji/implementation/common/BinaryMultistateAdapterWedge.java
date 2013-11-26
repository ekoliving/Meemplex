/*
 * Copyright 2005 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
package org.openmaji.implementation.common;



import java.util.logging.Level;
import java.util.logging.Logger;

import org.openmaji.common.Binary;
import org.openmaji.common.Multistate;
import org.openmaji.common.State;
import org.openmaji.common.StringState;
import org.openmaji.implementation.common.DebugFlag;
import org.openmaji.meem.Wedge;
import org.openmaji.meem.definition.WedgeDefinition;
import org.openmaji.meem.definition.WedgeDefinitionFactory;
import org.openmaji.meem.definition.WedgeDefinitionProvider;
import org.openmaji.meem.definition.WedgeDefinitionUtility;
import org.openmaji.meem.wedge.configuration.ConfigurationClient;
import org.openmaji.meem.wedge.configuration.ConfigurationClientAdapter;
import org.openmaji.meem.wedge.configuration.ConfigurationSpecification;
import org.openmaji.meem.wedge.lifecycle.WedgeValidationException;
/**
 * BinaryMultistateAdapterWedge is used to provide a flexible way to transfer Binary
 * input to Multistate output
 * 
 * @author  Diana Huang
 * 
 */
public class BinaryMultistateAdapterWedge implements Binary, Wedge, WedgeDefinitionProvider {
	private static Logger logger = Logger.getAnonymousLogger();
	/**
	 * Outbound Multistate facet
	 */
	public Multistate multistateClient;
	/**
	 * Outbound Multistate conduit
	 */
	public Multistate multistateControlConduit;
	/**
	 * Inbound Binary conduit
	 */
	public Binary binaryStateConduit=new BinaryStateConduit();
	/**
	 * Current state value maintained by this wedge
	 */
	public State state;
	/**
	 * The conduit through which this Wedge alerts errors in configuration changes
	 */
	public ConfigurationClient configurationClientConduit = new ConfigurationClientAdapter(this);
	
	/**
	 * A configuration property to specify which output State for true Binary value
	 */
	public String onValueState;
	public transient ConfigurationSpecification onValueStateSpecification = ConfigurationSpecification.create("Multistate state string");
	/**
	 * A configuration property to specify which output State for false Binary value
	 */
	public String offValueState;
	public transient ConfigurationSpecification offValueStateSpecification=ConfigurationSpecification.create("Multistate state string");
	
	/* ---------- ConfigurationChangeHandler listener ------------------------- */

	public void setOnValueState (String string){
  		if ( DebugFlag.TRACE ) logger.log(Level.FINE, "setOnValueState() - invoked");
		this.onValueState = string;
	}
	public void setOffValueState(String string){
		if ( DebugFlag.TRACE ) logger.log(Level.FINE, "setOffValueState() - invoked");
		this.offValueState=string;
	}
	public void validate() throws WedgeValidationException{
		if ( DebugFlag.TRACE ) logger.log(Level.FINE, "validate() - invoked");
		
		if(onValueState==null && offValueState==null){
			throw new WedgeValidationException("can't go ready because neither onValueState nor offValueState is set");
		}
		
		String[] availableStates = new String[] { onValueState, offValueState };
		this.state = new StringState("", availableStates);
	}
  
	/**
	 * Inbound Binary facet Implementation
	 */
	public void valueChanged(boolean newValue) {
		if(newValue){
			state.setState(onValueState.trim());
			multistateClient.stateChanged(state);
			multistateControlConduit.stateChanged(state);
		}else{
			state.setState(offValueState.trim());
			multistateClient.stateChanged(state);
			multistateControlConduit.stateChanged(state);
		}
         
	}
	  
	/* ---------- WedgeDefinitionProvider method(s) ---------------------------- */

	  public WedgeDefinition getWedgeDefinition() {
		  WedgeDefinition wedgeDefinition =WedgeDefinitionFactory.spi.create().inspectWedgeDefinition(this.getClass());
		  WedgeDefinitionUtility.renameFacetIdentifier(wedgeDefinition, "binary", "binaryInput");
		  WedgeDefinitionUtility.renameFacetIdentifier(wedgeDefinition,"multistateClient","multistateOutput");
	      return(wedgeDefinition);
	  }  
	  
	class BinaryStateConduit implements Binary {
		public synchronized void valueChanged (boolean newValue){
			BinaryMultistateAdapterWedge.this.valueChanged(newValue);
		}
	}
}
