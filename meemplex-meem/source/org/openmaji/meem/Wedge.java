/*
 * @(#)Wedge.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.meem;


/**
 * <p>
 * Wedge is a marker that can be used to indicate that a Java class
 * should be considered to be a Maji Wedge.
 * </p>
 * <p>
 * Whilst the Maji framework does not mandate that a developer marks their
 * Wedges using this marker, it is useful in two ways.
 * </p>
 * <p>
 * 1) Allows Definition builders to use reflection to take an arbitary Java
 *    class and dynamically build simple MeemDefinitions or WedgeDefinitions
 *    on-the-fly.
 * </p>
 * <p>
 * 2) Allows software development tools, such as InterMajik, to produce a
 *    list of Wedges for selection by a developer, whilst ignoring all
 *    other regular Java classes.
 * </p>
 * @author  Andy Gelme
 * @version 1.0
 * @see org.openmaji.meem.Meem
 * @see org.openmaji.meem.Facet
 * @see org.openmaji.meem.definition.WedgeDefinition
 */

public interface Wedge {
}
