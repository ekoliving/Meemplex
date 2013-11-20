/*
 * @(#)RevokableTarget.java
 *
 * Copyright 2004 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
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
