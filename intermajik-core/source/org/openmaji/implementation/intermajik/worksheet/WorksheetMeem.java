/*
 * @(#)WorksheetMeem.java
 *
 * Copyright 2004 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
package org.openmaji.implementation.intermajik.worksheet;

import org.openmaji.implementation.common.VariableMapWedge;

import org.openmaji.meem.definition.MeemDefinition;
import org.openmaji.meem.definition.MeemDefinitionFactory;
import org.openmaji.meem.definition.MeemDefinitionProvider;
import org.openmaji.system.space.Category;

/**
 * @author mg
 */
public class WorksheetMeem implements MeemDefinitionProvider {
	private MeemDefinition meemDefinition;
	
	/**
	 * @see org.openmaji.meem.definition.MeemDefinitionProvider#getMeemDefinition()
	 */
	public MeemDefinition getMeemDefinition() {
		if (meemDefinition == null) {
			MeemDefinitionFactory meemDefinitionFactory = MeemDefinitionFactory.spi.create();
			
			Class[] wedgeClasses = new Class[] {
					WorksheetWedge.class,
					VariableMapWedge.class,
					Category.class
				};
			
			meemDefinition = meemDefinitionFactory.createMeemDefinition(wedgeClasses);
      meemDefinition.getMeemAttribute().setIdentifier("Worksheet");
		}
		
		return meemDefinition;
	}
}
