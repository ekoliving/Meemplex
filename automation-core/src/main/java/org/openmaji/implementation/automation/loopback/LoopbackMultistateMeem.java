/* Copyright 2005 by EkoLiving Pty Ltd.  All Rights Reserved.
*
* This software is the proprietary information of EkoLiving Pty Ltd.
* Use is subject to license terms.
*/
package org.openmaji.implementation.automation.loopback;

import org.openmaji.implementation.common.MultistateWedge;

import org.openmaji.meem.definition.MeemDefinition;
import org.openmaji.meem.definition.MeemDefinitionFactory;
import org.openmaji.meem.definition.MeemDefinitionProvider;
import org.openmaji.meem.definition.MeemDefinitionUtility;

/**
 * The LoopbackMultistateMeem has a device inbound facet, a multistate
 * inbound facet and a multistate outbound facet
 * 
 * @author  Diana Huang
 */
public class LoopbackMultistateMeem implements MeemDefinitionProvider{
	  private MeemDefinition meemDefinition = null;

	  public MeemDefinition getMeemDefinition(){
	    if ( meemDefinition == null ){
	      Class[] wedges = new Class[]{MultistateWedge.class,LoopbackMultistateWedge.class};
	      meemDefinition = MeemDefinitionFactory.spi.create().createMeemDefinition(wedges);
        meemDefinition.getMeemAttribute().setIdentifier("LoopbackMultistate");
	      //MeemDefinitionUtility.renameFacetIdentifier(meemDefinition,"DeviceWedge","device","deviceInput");
	      MeemDefinitionUtility.renameFacetIdentifier(meemDefinition,"MultistateWedge","multistate","multistateInput");
	      MeemDefinitionUtility.renameFacetIdentifier(meemDefinition,"MultistateWedge","multistateClient","multistateOutput");
	    }

	    return meemDefinition;
	  }
}

