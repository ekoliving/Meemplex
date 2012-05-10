/*
 * @(#)SecurityListener.java
 * Created on 30/01/2004
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.client.security;

/**
 * <code>SecurityListener</code>.
 * <p>
 * @author Kin Wong
 */
public interface SecurityListener {
	/**
	 * This is Invoked when the security manager has been logged in.
	 * <p>
	 * @param manager The security manager that has been logged in.
	 */
	void onLogin(SecurityManager manager);

	/**
	 * This is invoked when the security manager has been logged out.
	 * <p>
	 * @param manager The security manager that has been logged out.
	 */
	void onLogout(SecurityManager manager);
}
