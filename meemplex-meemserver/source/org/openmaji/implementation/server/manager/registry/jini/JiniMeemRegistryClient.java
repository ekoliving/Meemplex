/*
 * @(#)JiniMeemRegistryClient.java
 *
 * Copyright 2004 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.server.manager.registry.jini;

import java.rmi.Remote;
import java.rmi.RemoteException;

import org.openmaji.meem.Facet;
import org.openmaji.meem.Meem;


/**
 * <p>
 * ...
 * </p>
 *
 * @author  mg
 * @version 1.0
 */
public interface JiniMeemRegistryClient extends Facet, Remote {
	public void meemRegisteredRemote(Meem meem) throws RemoteException;

	public void meemDeregisteredRemote(Meem meem) throws RemoteException;
	
	public void contentSent() throws RemoteException;
	public void contentFailed(String reason) throws RemoteException;
}
