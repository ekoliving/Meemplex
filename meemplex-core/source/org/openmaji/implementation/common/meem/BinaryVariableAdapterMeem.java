/*
 * Copyright 2007 by GSDEC.  All Rights Reserved.
 *
 * This software is the proprietary information of GSDEC.
 * Use is subject to license terms.
 * 
 */

package org.openmaji.implementation.common.meem;

import org.openmaji.implementation.common.BinaryVariableAdapterWedge;
import org.openmaji.implementation.common.BinaryWedge;
import org.openmaji.implementation.common.VariableWedge;
import org.openmaji.meem.definition.MeemDefinition;
import org.openmaji.meem.definition.MeemDefinitionFactory;
import org.openmaji.meem.definition.MeemDefinitionProvider;
import org.openmaji.meem.definition.MeemDefinitionUtility;


public class BinaryVariableAdapterMeem implements MeemDefinitionProvider {
	private MeemDefinition meemDefinition = null;

	public MeemDefinition getMeemDefinition() {
		if (meemDefinition == null) {
			Class[] wedges = new Class[] { 
					BinaryWedge.class, 
					VariableWedge.class, 
					BinaryVariableAdapterWedge.class, 
				};
			meemDefinition = MeemDefinitionFactory.spi.create().createMeemDefinition(wedges);
			meemDefinition.getMeemAttribute().setIdentifier("BinaryLinearAdapter");
			MeemDefinitionUtility.renameFacetIdentifier(meemDefinition, "BinaryWedge", "binary", "binaryInput");
			MeemDefinitionUtility.renameFacetIdentifier(meemDefinition, "BinaryWedge", "binaryClient", "binaryOutput");
			MeemDefinitionUtility.renameFacetIdentifier(meemDefinition, "VariableWedge", "variable", "variableInput");
			MeemDefinitionUtility.renameFacetIdentifier(meemDefinition, "VariableWedge", "variableClient", "variableOutput");
		}

		return meemDefinition;
	}
}
