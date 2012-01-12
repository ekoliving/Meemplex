package org.openmaji.implementation.common.group;

import org.openmaji.implementation.common.BinaryWedge;
import org.openmaji.meem.definition.MeemDefinition;
import org.openmaji.meem.definition.MeemDefinitionFactory;
import org.openmaji.meem.definition.MeemDefinitionProvider;
import org.openmaji.meem.definition.MeemDefinitionUtility;

public class BinaryLinearGroupMeem implements MeemDefinitionProvider {
	public static final String FACETID_GROUP_INPUT = "groupLinearInput";
	public static final String FACETID_GROUP_OUTPUT = "groupLinearOutput";
	
	public MeemDefinition getMeemDefinition() {
		Class<?>[] wedges = new Class[] { 
				BinaryWedge.class,
				BinaryLinearGroupWedge.class
			};
		MeemDefinition meemDefinition = MeemDefinitionFactory.spi.create().createMeemDefinition(wedges);
		
		MeemDefinitionUtility.renameFacetIdentifier(meemDefinition, "BinaryWedge", "binary", "binaryInput");
		MeemDefinitionUtility.renameFacetIdentifier(meemDefinition, "BinaryWedge", "binaryClient", "binaryOutput");
		MeemDefinitionUtility.renameFacetIdentifier(meemDefinition, "BinaryLinearGroupWedge", "linear", FACETID_GROUP_INPUT);
		MeemDefinitionUtility.renameFacetIdentifier(meemDefinition, "BinaryLinearGroupWedge", "linearOut", FACETID_GROUP_OUTPUT);

		return meemDefinition;
	}
}
