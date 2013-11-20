/*
 * @(#)TypeSpace.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */

package org.openmaji.system.space.type;

import org.openmaji.meem.Facet;

/**
 * <p>
 * This space is used in a similar way to the Jini Lookup Service. 
 * Given an interface name (eg: org.openmaji.space.HyperSpace),
 * it will return all registered meems that implement that interface.
 * </p>
 * @author  mg
 * @version 1.0
 */

public interface TypeSpace extends Facet {
}