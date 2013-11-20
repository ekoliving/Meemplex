package org.openmaji.implementation.automation.loopback;

import org.openmaji.implementation.common.UnaryWedge;

import org.openmaji.meem.definition.MeemDefinition;
import org.openmaji.meem.definition.MeemDefinitionFactory;
import org.openmaji.meem.definition.MeemDefinitionProvider;
import org.openmaji.meem.definition.MeemDefinitionUtility;

public class LoopbackUnaryMeem implements MeemDefinitionProvider{
	  private MeemDefinition meemDefinition = null;

	  public MeemDefinition getMeemDefinition(){
	    if ( meemDefinition == null ){
	      Class[] wedges = new Class[]{UnaryWedge.class,LoopbackUnaryWedge.class};
	      meemDefinition = MeemDefinitionFactory.spi.create().createMeemDefinition(wedges);
        meemDefinition.getMeemAttribute().setIdentifier("LoopbackUnary");
	      //MeemDefinitionUtility.renameFacetIdentifier(meemDefinition,"DeviceWedge","device","deviceInput");
	      MeemDefinitionUtility.renameFacetIdentifier(meemDefinition,"UnaryWedge","unary","unaryInput");
	      MeemDefinitionUtility.renameFacetIdentifier(meemDefinition,"UnaryWedge","unaryClient","unaryOutput");
	    }

	    return meemDefinition;
	  }
}
