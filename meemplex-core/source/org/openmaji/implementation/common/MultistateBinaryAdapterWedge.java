/*
 * Copyright 2005 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
package org.openmaji.implementation.common;

import org.swzoo.log2.core.LogFactory;
import org.swzoo.log2.core.LogTools;
import org.swzoo.log2.core.Logger;

import org.openmaji.common.Binary;
import org.openmaji.common.Multistate;
import org.openmaji.common.State;
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
 * MultistateBinaryAdapterWedge is used to provide a flexible way to transfer Multistate
 * input to Binary output
 * 
 * @author  Diana Huang
 * 
 */
public class MultistateBinaryAdapterWedge implements Multistate, Wedge, WedgeDefinitionProvider {
	private static Logger logger = LogFactory.getLogger();
	/**
	 * Outbound Binary facet
	 */
	public Binary binaryClient;
	/**
	 * Outbound Binary conduit
	 */
	public Binary binaryControlConduit;
	/**
	 * Inbound Multistate conduit
	 */
	public Multistate multistateStateConduit=new MultistateStateConduit();
	/**
	 * The conduit through which this Wedge alerts errors in configuration changes
	 */
	public ConfigurationClient configurationClientConduit = new ConfigurationClientAdapter(this);
	
	/**
	 * A configuration property to specify which state to send a Binary true value
	 */
	public String onValueState;
	public transient ConfigurationSpecification onValueStateSpecification = new ConfigurationSpecification("Multistate state string");
	/**
	 * A configuration property to specify which state to send a Binary false value
	 */
	public String offValueState;
	public transient ConfigurationSpecification offValueStateSpecification=new ConfigurationSpecification("Multistate state string");
	/* ---------- ConfigurationChangeHandler listener ------------------------- */

	public void setOnValueState (String string){
  		if ( DebugFlag.TRACE ) LogTools.trace(logger,20,"setOnValueState() - invoked");
		this.onValueState = string;
	}
	public void setOffValueState(String string){
		if ( DebugFlag.TRACE ) LogTools.trace(logger,20,"setOffValueState() - invoked");
		this.offValueState=string;
	}
	public void validate() throws WedgeValidationException{
		if ( DebugFlag.TRACE ) LogTools.trace(logger,20,"validate() - invoked");
		
		if(onValueState==null && offValueState==null){
			throw new WedgeValidationException("can't go ready because neither onValueState nor offValueState is set");
		}
		
	}
	/**
	 * Inbound Multistate facet Implementation
	 */
	public void stateChanged(State state) {
		String stateValue=state.getState();
		if(stateValue.equalsIgnoreCase(onValueState.trim())){
			binaryClient.valueChanged(true);
			binaryControlConduit.valueChanged(true);
		}else if (stateValue.equalsIgnoreCase(offValueState.trim())){
			binaryClient.valueChanged(false);
			binaryControlConduit.valueChanged(false);
		}
		
	         
	}
	  
	/* ---------- WedgeDefinitionProvider method(s) ---------------------------- */

	public WedgeDefinition getWedgeDefinition() {
		WedgeDefinition wedgeDefinition =WedgeDefinitionFactory.spi.create().inspectWedgeDefinition(this.getClass());
		WedgeDefinitionUtility.renameFacetIdentifier(wedgeDefinition, "multistate", "multistateInput");
		WedgeDefinitionUtility.renameFacetIdentifier(wedgeDefinition,"binaryClient","binaryOutput");
	    return(wedgeDefinition);
	}
	
	class MultistateStateConduit implements Multistate{
		public void stateChanged(State state){
			String stateValue=state.getState();
			if(stateValue.equalsIgnoreCase(onValueState.trim())){
				binaryClient.valueChanged(true);
				binaryControlConduit.valueChanged(true);
			}else if (stateValue.equalsIgnoreCase(offValueState.trim())){
				binaryClient.valueChanged(false);
				binaryControlConduit.valueChanged(false);
			}
		}
	}
}
