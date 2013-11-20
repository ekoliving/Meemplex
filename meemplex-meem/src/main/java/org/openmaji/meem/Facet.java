/*
 * @(#)Facet.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.meem;

import java.rmi.Remote;



/**
 * <p>
 * <code>Facet</code> is a marker interface used to indicate that a Java interface
 * should be considered to be a Maji Facet.
 * </p>
 * <p>
 * The Maji framework mandates that a developer marks their facets using this marker;
 * this is useful in several ways.
 * </p>
 * <p>
 * 1) Defines those Facets that will be used between Java Virtual Machines.
 *    If a Facet needs to be used remotely, then it *must* either be marked
 *    as a Facet or directly extend the java.rmi.Remote interface.
 * </p>
 * <p>
 * 2) Allows Definition builders to use reflection to take an arbitary Java
 *    class and dynamically build simple MeemDefinitions or WedgeDefinitions
 *    on-the-fly.
 * </p>
 * <p>
 * 3) Allows software development tools, such as InterMajik, to produce a
 *    list of Facets for selection by a developer, whilst ignoring all
 *    other regular Java interfaces.
 * </p>
 * @author  Andy Gelme
 * @version 1.0
 * @see org.openmaji.meem.Meem
 * @see org.openmaji.meem.Wedge
 * @see org.openmaji.meem.definition.FacetDefinition
 */
public interface Facet extends Remote {
}
