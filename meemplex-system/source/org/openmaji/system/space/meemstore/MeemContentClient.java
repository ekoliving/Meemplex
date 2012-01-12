/*
 * @(#)MeemContentClient.java
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
import org.openmaji.system.meem.definition.MeemContent;


/**
 * <p>
 * Client facet for picking up changes in a meem's content.
 * </p>
 * <p>
 * Note: this class is almost definitely subject to change.
 * </p>
 * @author  mg
 * @version 1.0
 */
public interface MeemContentClient extends Facet {

	//-mg- this should be put back in once unbound meems are going 
	//public void meemContentChanged(Meem meem, MeemContent meemContent);
	/**
	 * Signal that the content at a given meem path has changed.
	 * 
	 * @param meemPath the meem path for the meem that was affected.
	 * @param meemContent the new content for the meem.
	 */
	public void meemContentChanged(MeemPath meemPath, MeemContent meemContent);
	
}
