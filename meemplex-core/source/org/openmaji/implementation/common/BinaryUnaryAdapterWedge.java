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
 * The Wedge is used to operate a Unary based on the 
 * value of Binary Input.
 * 
 * @author  Diana Huang
 * 
 */
public class BinaryUnaryAdapterWedge implements Binary, Wedge, WedgeDefinitionProvider {
	private static Logger logger = LogFactory.getLogger();
	/**
	 * Outbound Unary facet
	 */
	public Unary unaryClient;
	/**
	 * Outbound Unary conduit
	 */
	public Unary unaryControlConduit;
	/**
	 * Inbound Binary conduit
	 */
	public Binary binaryStateConduit=new BinaryStateConduit();
	
	/**
	 * The conduit through which this Wedge alerts errors in configuration changes
	 */
	public ConfigurationClient configurationClientConduit = new ConfigurationClientAdapter(this);
	/**
	 * The conduit through which we are alerted to life cycle changes
	 */
	public LifeCycleClient lifeCycleClientConduit = new LifeCycleClientAdapter(this);
	/**
	 * A configuration property to specify which Binary input value the wedge is looking after
	 */
	public String binaryValue="true";
	public transient ConfigurationSpecification binaryValueSpecification=new ConfigurationSpecification("true|false|both");
	
	/* ---------- ConfigurationChangeHandler listener ------------------------- */

	public void setBinaryValue (String string){
  		if ( DebugFlag.TRACE ) LogTools.trace(logger,20,"setBinaryValue() - invoked");
		this.binaryValue = string;
	}
	/*public void validate() throws WedgeValidationException{
		if ( DebugFlag.TRACE ) LogTools.trace(logger,20,"validate() - invoked");
		
		if(binaryValue==null){
			throw new WedgeValidationException("can't go ready because binaryValue is null");
		}
		
	}*/
	/**
	 * Inbound Binary facet Implementation
	 */
	public void valueChanged(boolean newValue) {
		if ( DebugFlag.TRACE ) LogTools.trace(logger,20,"valueChanged() - invoked on inbound facet");
		String binaryString=binaryValue.trim();
		if(newValue){
			if(binaryString.equalsIgnoreCase("true")){
				unaryControlConduit.valueChanged();
			    unaryClient.valueChanged();
			}else if (binaryString.equalsIgnoreCase("both")){
				unaryControlConduit.valueChanged();
			    unaryClient.valueChanged();
			}
			
		}else{
			if(binaryString.equalsIgnoreCase("false")){
				unaryControlConduit.valueChanged();
			    unaryClient.valueChanged();
			}else if(binaryString.equalsIgnoreCase("both")){
				unaryControlConduit.valueChanged();
			    unaryClient.valueChanged();
			}
		}
		
	         
	}
	  
	/* ---------- WedgeDefinitionProvider method(s) ---------------------------- */
	public WedgeDefinition getWedgeDefinition() {
		WedgeDefinition wedgeDefinition =WedgeDefinitionFactory.spi.create().inspectWedgeDefinition(this.getClass());
		WedgeDefinitionUtility.renameFacetIdentifier(wedgeDefinition, "binary", "binaryInput");
		WedgeDefinitionUtility.renameFacetIdentifier(wedgeDefinition,"unaryClient","unaryOutput");
	    return(wedgeDefinition);
	}
	
	class BinaryStateConduit implements Binary {
		public synchronized void valueChanged (boolean newValue){
			String binaryString=binaryValue.trim();
			if(newValue){
				if(binaryString.equalsIgnoreCase("true")){
					unaryControlConduit.valueChanged();
				    unaryClient.valueChanged();
				}else if (binaryString.equalsIgnoreCase("both")){
					unaryControlConduit.valueChanged();
				    unaryClient.valueChanged();
				}
				
			}else{
				if(binaryString.equalsIgnoreCase("false")){
					unaryControlConduit.valueChanged();
				    unaryClient.valueChanged();
				}else if(binaryString.equalsIgnoreCase("both")){
					unaryControlConduit.valueChanged();
				    unaryClient.valueChanged();
				}
			}
		}
	}

}
