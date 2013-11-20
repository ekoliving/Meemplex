package org.openmaji.implementation.common.group;

import org.openmaji.implementation.common.BinaryWedge;
import org.openmaji.meem.definition.MeemDefinition;
import org.openmaji.meem.definition.MeemDefinitionFactory;
import org.openmaji.meem.definition.MeemDefinitionProvider;
import org.openmaji.meem.definition.MeemDefinitionUtility;

public class BinaryGroupMeem implements MeemDefinitionProvider {
	public static final String FACETID_GROUP_INPUT = "groupBinaryInput";
	public static final String FACETID_GROUP_OUTPUT = "groupBinaryOutput";

	public MeemDefinition getMeemDefinition() {
		Class<?>[] wedges = new Class[] { 
				BinaryWedge.class, 
				BinaryGroupWedge.class
			};
		MeemDefinition meemDefinition = MeemDefinitionFactory.spi.create().createMeemDefinition(wedges);
		
		MeemDefinitionUtility.renameFacetIdentifier(meemDefinition, "BinaryWedge", "binary", "binaryInput");
		MeemDefinitionUtility.renameFacetIdentifier(meemDefinition, "BinaryWedge", "binaryClient", "binaryOutput");
		MeemDefinitionUtility.renameFacetIdentifier(meemDefinition, "BinaryGroupWedge", "binary", FACETID_GROUP_INPUT);
		MeemDefinitionUtility.renameFacetIdentifier(meemDefinition, "BinaryGroupWedge", "binaryOut", FACETID_GROUP_OUTPUT);

		return meemDefinition;
	}
}
