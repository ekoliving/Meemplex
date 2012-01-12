/*
 * @(#)JiniLeaseWrapper.java
 *
 * Copyright 2004 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */
package org.openmaji.implementation.server.meem.wedge.remote;

import java.io.Serializable;
import java.rmi.RemoteException;

import org.openmaji.system.meem.wedge.remote.Lease;

import net.jini.core.lease.LeaseDeniedException;
import net.jini.core.lease.LeaseMap;
import net.jini.core.lease.UnknownLeaseException;

/**
 * @author mg
 */
public class JiniLeaseWrapper implements Lease, net.jini.core.lease.Lease, Serializable {
	private static final long serialVersionUID = 9089034902390838L;

	private final net.jini.core.lease.Lease lease; 
	
	protected JiniLeaseWrapper(net.jini.core.lease.Lease lease) {
		this.lease = lease;
	}
	
	/**
	 * @see org.openmaji.system.meem.wedge.remote.Lease#cancel()
	 */
	public void cancel() {
		try {
			lease.cancel();
		} catch (UnknownLeaseException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @see org.openmaji.system.meem.wedge.remote.Lease#getExpiration()
	 */
	public long getExpiration() {
		return lease.getExpiration();
	}
	
	/**
	 * @see org.openmaji.system.meem.wedge.remote.Lease#renew(long)
	 */
	public void renew(long duration) {
		try {
			lease.renew(duration);
		} catch (LeaseDeniedException e) {
			e.printStackTrace();
		} catch (UnknownLeaseException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
		
	/**
	 * @see net.jini.core.lease.Lease#canBatch(net.jini.core.lease.Lease)
	 */
	public boolean canBatch(net.jini.core.lease.Lease otherLease) {
		return lease.canBatch(otherLease);
	}

	/**
	 * @see net.jini.core.lease.Lease#createLeaseMap(long)
	 */
	public LeaseMap createLeaseMap(long duration) {
		return lease.createLeaseMap(duration);
	}

	/**
	 * @see net.jini.core.lease.Lease#getSerialFormat()
	 */
	public int getSerialFormat() {
		return lease.getSerialFormat();
	}
	
	/**
	 * @see net.jini.core.lease.Lease#setSerialFormat(int)
	 */
	public void setSerialFormat(int format) {
		lease.setSerialFormat(format);
	}
	
	public net.jini.core.lease.Lease getJiniLease() {
		return lease;
	}
	
}
