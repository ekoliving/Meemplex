/*
 * @(#)SpacePersistenceDelegate.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.server.nursery.space.meemstore.beanxml;

import java.beans.*;

import org.openmaji.meem.space.Space;


/**
 * <p>
 * ...
 * </p>
 * @author  mg
 * @version 1.0
 */
public class SpacePersistenceDelegate extends DefaultPersistenceDelegate {
	
	protected Expression instantiate(Object oldInstance, Encoder out) {
		Space space = (Space)oldInstance;
		
		return new Expression(oldInstance, oldInstance.getClass(), "new", new Object[] { space.getType(), new Boolean(space.isStorage())});
	}
	
}
