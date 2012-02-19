/*
 * @(#)RemoteMeem.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */

package org.openmaji.system.meem.wedge.remote;

import java.io.Serializable;
import java.rmi.RemoteException;

import org.openmaji.meem.Facet;


/**
 * <p>
 * The RemoteMeem ...
 * </p>
 * <p>
 * Note: Implementation thread safe = Not applicable
 * </p>
 * @author  Andy Gelme
 * @version 1.0
 * @see org.openmaji.meem.Facet
 * @see org.openmaji.system.meem.wedge.remote.RemoteMeemClient
 */

public interface RemoteMeem extends Facet {

	void majikInvocation(
			String facetIdentifier, 
			String methodName, 
			Class[] argsClasses, 
			Serializable[] args, 
			Serializable request
		) throws RemoteException;

	Lease obtainLease() throws RemoteException;

	/* ---------- Nested class for SPI ----------------------------------------- */

	public class spi {
		public static String getIdentifier() {
			return ("remoteMeem");
		};
	}
}