/*
 * @(#)Lease.java
 *
 * Copyright 2004 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
package org.openmaji.system.meem.wedge.remote;

/**
 * @author mg
 */
public interface Lease {

	/**
   * @return Absolute lease expiry time in milliseconds
	 */
	public long getExpiration();
	
	/**
	 * Cancels the lease with the lease provider
	 */
	public void cancel();
	
	/**
	 * Renews the lease with the lease provider for duration milliseconds
	 * @param duration Time in ms to renew the lease for
	 */
	public void renew(long duration);

}
