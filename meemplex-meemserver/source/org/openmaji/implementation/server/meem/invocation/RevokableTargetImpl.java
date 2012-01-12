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
public class RevokableTargetImpl implements RevokableTarget
{
	public RevokableTargetImpl(Facet target, Revokable revokable)
	{
		this.target = target;
		this.revokable = revokable;
	}

	public Facet getTarget()
	{
		return target;
	}

	public void revoke()
	{
		if (revokable != null)
		{
			revokable.revoke();
			revokable = null;
		}
	}

	private final Facet target;
	private Revokable revokable;
}
