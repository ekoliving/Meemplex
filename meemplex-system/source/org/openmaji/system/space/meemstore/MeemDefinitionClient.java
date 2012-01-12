/*
 * @(#)MeemDefinitionClient.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.system.space.meemstore;

import org.openmaji.meem.Facet;
import org.openmaji.meem.MeemPath;
import org.openmaji.meem.definition.MeemDefinition;

/**
 * <p>
 * Client facet for picking up changes in a meem's definition.
 * </p>
 * <p>
 * Note: this class is almost definitely subject to change.
 * </p>
 * @author  mg
 * @version 1.0
 */
public interface MeemDefinitionClient extends Facet {
	
	// -mg- this should be put back in once unbound meems are going 
	//public void meemDefinitionChanged(Meem meem, MeemDefinition meemDefinition);
	
	/**
	 * Signal that a meem at a given meemPath has changed definition.
	 * 
	 * @param meemPath the meem path of the meem affected.
	 * @param meemDefinition the new definition for the meem.
	 */
	public void meemDefinitionChanged(MeemPath meemPath, MeemDefinition meemDefinition);
	
}
