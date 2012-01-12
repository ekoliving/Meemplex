/* Copyright 2005 by Majitek Limited.  All Rights Reserved.
*
* This software is the proprietary information of Majitek Limited.
* Use is subject to license terms.
*/
package org.openmaji.implementation.common;
import org.swzoo.log2.core.LogFactory;
import org.swzoo.log2.core.LogTools;
import org.swzoo.log2.core.Logger;

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
/**
 * UnaryMultistateAdapterWedge is used to provide a flexible way to transfer Unary
 * input to Multistate output
 * 
 * @author  Diana Huang
 * 
 */
public class UnaryMultistateAdapterWedge implements Unary, Wedge, WedgeDefinitionProvider{
	private static Logger logger = LogFactory.getLogger();
	/**
	 * Outbound Multistate facet
	 */
	public Multistate multistateClient;
	/**
	 * Outbound Multistate conduit
	 */
	public Multistate multistateControlConduit;
	/**
	 * Current state maintained by this wedge
	 */
	public State state;
	/**
	 * The conduit through which this Wedge alerts errors in configuration changes
	 */
	public ConfigurationClient configurationClientConduit = new ConfigurationClientAdapter(this);
	/**
	 * The conduit through which we are alerted to life cycle changes
	 */
	public LifeCycleClient lifeCycleClientConduit = new LifeCycleClientAdapter(this);
	public String stateString;
	public transient ConfigurationSpecification stateStringSpecification = new ConfigurationSpecification("Multistate state string");
	
	/* ---------- ConfigurationChangeHandler listener ------------------------- */

	public void setStateString (String string){
  		if ( DebugFlag.TRACE ) LogTools.trace(logger,20,"setStateString() - invoked");
		this.stateString = string;
	}
	/* -------------- validation ----------------------------------------------- */
	public void validate() throws WedgeValidationException{
		if ( DebugFlag.TRACE ) LogTools.trace(logger,20,"validate() - invoked");
		
		if(stateString==null){
			throw new WedgeValidationException("can't go ready because stateString is null");
		}
		
	}
	/**
	 * Unary inbound implementation
	 */
	public void valueChanged() {
		state.setState(stateString);
		multistateClient.stateChanged(state);
		multistateControlConduit.stateChanged(state);
	}
	  
	/* ---------- WedgeDefinitionProvider method(s) ---------------------------- */

	public WedgeDefinition getWedgeDefinition() {
		WedgeDefinition wedgeDefinition =WedgeDefinitionFactory.spi.create().inspectWedgeDefinition(this.getClass());
		WedgeDefinitionUtility.renameFacetIdentifier(wedgeDefinition, "unary", "unaryInput");
		WedgeDefinitionUtility.renameFacetIdentifier(wedgeDefinition,"multistateClient","multistateOutput");
	    return(wedgeDefinition);
	}  
}
