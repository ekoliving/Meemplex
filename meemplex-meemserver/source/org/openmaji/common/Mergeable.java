/*
 * @(#)Mergeable.java
 * Created on 13/10/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
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
