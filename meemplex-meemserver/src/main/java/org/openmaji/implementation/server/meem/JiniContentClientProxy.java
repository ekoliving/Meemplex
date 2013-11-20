/*
 * @(#)JiniContentClientProxy.java
 *
 * Copyright 2004 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
package org.openmaji.implementation.server.meem;

import java.rmi.RemoteException;

import org.openmaji.implementation.server.manager.registry.jini.JiniMeemRegistryClient;
import org.openmaji.system.meem.wedge.reference.ContentClient;



/**
 * @author Peter
 */
public class JiniContentClientProxy implements ContentClient
{
	public JiniContentClientProxy(JiniMeemRegistryClient client)
	{
		this.client = client;
	}

	public void contentSent()
	{
		try
		{
			client.contentSent();
		}
		catch (RemoteException e)
		{
			e.printStackTrace();
		}
	}

	public void contentFailed(String reason)
	{
		try
		{
			client.contentFailed(reason);
		}
		catch (RemoteException e)
		{
			e.printStackTrace();
		}
	}

	private final JiniMeemRegistryClient client;
}
