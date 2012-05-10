/*
 * @(#)PersistenceHandlerAdapter.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.server.manager.lifecycle.adapter.persistence;

import org.openmaji.meem.Meem;
import org.openmaji.system.meem.definition.MeemContent;


/**
 * <p>
 * ...
 * </p>
 * @author  mg
 * @version 1.0
 */
public interface PersistenceHandlerAdapter {
	
	public void persist(Meem meem);
	public void restore(Meem meem, MeemContent meemContent);
	
}
