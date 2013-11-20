package org.openmaji.implementation.automation.loopback;

import org.openmaji.meem.definition.MeemDefinition;
import org.openmaji.meem.definition.MeemDefinitionFactory;
import org.openmaji.meem.definition.MeemDefinitionProvider;
import org.openmaji.meem.definition.MeemDefinitionUtility;

import org.openmaji.implementation.automation.common.DeviceWedge;
import org.openmaji.implementation.common.CategoryEntryConsumerWedge;

public class LoopbackCategoryEntryConsumerMeem implements MeemDefinitionProvider{
	  private MeemDefinition meemDefinition = null;

	  public MeemDefinition getMeemDefinition(){
			if ( meemDefinition == null ){
				Class[] wedges = new Class[3];
				wedges[0] = CategoryEntryConsumerWedge.class;
				wedges[1] = DeviceWedge.class;
				wedges[2] = LoopbackCategoryEntryConsumerWedge.class;
				meemDefinition = MeemDefinitionFactory.spi.create().createMeemDefinition(wedges);
				meemDefinition.getMeemAttribute().setIdentifier("LoopbackCategoryEntryConsumer");
				MeemDefinitionUtility.renameFacetIdentifier(meemDefinition,"DeviceWedge","device","deviceInput");
				MeemDefinitionUtility.renameFacetIdentifier(meemDefinition,"CategoryEntryConsumerWedge","categoryEntryConsumer","entryConsumerInput");
				MeemDefinitionUtility.renameFacetIdentifier(meemDefinition,"CategoryEntryConsumerWedge","entryConsumerClient","entryConsumerOutput");
			}

			return meemDefinition;
	  }
}

