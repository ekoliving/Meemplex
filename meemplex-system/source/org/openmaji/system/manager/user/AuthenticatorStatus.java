/*
 * @(#)AuthenticatorStatus.java
 * 
 * Copyright 2005 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
package org.openmaji.system.manager.user;

import org.openmaji.meem.Facet;

/**
 * <p>
 * Notifies the status of the authenticator.
 * </p>
 *
 */
public interface AuthenticatorStatus extends Facet {

	/**
	 * The MajiAuthenticator has been located by Authenticator Lookup.
	 */
	void authenticatorLocated();

	/**
	 * The MajiAuthenticator is no longer available.  Hence authentication
	 * can not be achieved.
	 */
	void authenticatorLost();
}
