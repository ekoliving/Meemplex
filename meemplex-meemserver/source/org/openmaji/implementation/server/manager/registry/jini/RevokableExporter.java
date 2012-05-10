/*
 * @(#)RevokableExporter.java
 *
 * Copyright 2004 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
package org.openmaji.implementation.server.manager.registry.jini;

import org.openmaji.implementation.server.meem.invocation.Revokable;

import net.jini.export.Exporter;

/**
 * @author Peter
 */
public class RevokableExporter implements Revokable
{
	public RevokableExporter(Exporter exporter)
	{
		this.exporter = exporter;
	}

	public void revoke()
	{
//		System.err.println("Unexporting");
		exporter.unexport(true);
	}

	private final Exporter exporter;
}
