/*
 * @(#)DefinitionFactory.java
 * Created on 26/09/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.client;

import org.openmaji.meem.definition.MeemDefinition;
import org.openmaji.meem.definition.MeemDefinitionFactory;
import org.openmaji.meem.definition.Scope;
import org.openmaji.system.space.Category;
import org.openmaji.utility.uid.UID;


/**
 * <code>DefinitionFactory</code>.
 * <p>
 * @author Kin Wong
 */
public class DefinitionFactory {

	//private static WedgeDefinition categoryDefinition;

	/**
	 * Creates a meem definition contains only system wedges.
	 * @return MeemDefinition A meem definition contains only system wedges.
	 */
	static public MeemDefinition createMeem() {
		UID uid = UID.spi.create();
		String id = "Meem " + uid.getUIDString();
		
		MeemDefinition definition = new MeemDefinition();//getFactory().createMeemDefinition(id);
		definition.getMeemAttribute().setIdentifier(id);
		definition.getMeemAttribute().setScope(Scope.LOCAL);
		definition.getMeemAttribute().setVersion(1);
		return definition;
	}
	
	/**
	 * Creates a meem definition contains system wedges and category wedge.
	 * @return MeemDefinition A meem definition contains system wedges and 
	 * category wedge.
	 */
	static public MeemDefinition createCategory() {
		MeemDefinition definition = MeemDefinitionFactory.spi.create().createMeemDefinition(Category.class);

		UID uid = UID.spi.create();
		String id = "Category " + uid.getUIDString();
		definition.getMeemAttribute().setIdentifier(id);
		
		return definition;
	}
	
	/**
	 * Creates a meem definition contains system wedges and worksheet wedge.
	 * @return MeemDefinition A meem definition contains system wedges and 
	 * worksheet wedge.
	 */	
	static public MeemDefinition createWorksheet() {
		MeemDefinition definition = createMeem();
		return definition;
	}
	
	/**
	 * Creates a meem Definition contains system wedges and MeemPlex wedge.
	 * @return MeemDefinition A meem definition contains system wedges and 
	 * MeemPlex wedge.
	 */
	static public MeemDefinition createMeemPlex() {
		MeemDefinition definition = createMeem();
		return definition;
	}
}
