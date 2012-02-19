/*
 * @(#)ManagedPersistenceClient.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.system.meem.wedge.persistence;

import org.openmaji.meem.Facet;
import org.openmaji.meem.MeemPath;
import org.openmaji.system.meem.definition.MeemContent;


/**
 * <p>
 * ...
 * </p>
 * @author  mg
 * @version 1.0
 */
public interface ManagedPersistenceClient extends Facet {

	public void meemContentChanged(MeemPath meemPath, MeemContent meemContent);
	public void restored(MeemPath meemPath);
	
}
