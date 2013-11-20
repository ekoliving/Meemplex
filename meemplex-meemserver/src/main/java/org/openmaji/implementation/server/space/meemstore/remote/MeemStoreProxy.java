/*
 * @(#)MeemStoreProxy.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.server.space.meemstore.remote;

import org.openmaji.meem.Facet;
import org.openmaji.meem.Meem;


/**
 * <p>
 * ...
 * </p>
 *
 * @author  mg
 * @version 1.0
 */
public interface MeemStoreProxy extends Facet {
	
	public void setMeemStoreMeem(Meem meemStoreMeem);
	
}
