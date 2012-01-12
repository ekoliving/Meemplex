/*
 * @(#)RevokableTarget.java
 *
 * Copyright 2004 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */
package org.openmaji.implementation.server.meem.invocation;

import org.openmaji.meem.Facet;

/**
 * @author Peter
 */
public interface RevokableTarget extends Revokable
{
	Facet getTarget();
}
