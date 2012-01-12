/*
 * @(#)Mergeable.java
 * Created on 13/10/2003
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.common;

/**
 * This Interface is implemented by classes to indicate that they
 * are mergeable.
 * 
 * @author Kin Wong
 */
public interface Mergeable {
	public boolean merge(Object delta);
}
