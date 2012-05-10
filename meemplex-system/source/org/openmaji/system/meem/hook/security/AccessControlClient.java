/*
 * @(#)AccessControlClient.java
 *
 * Copyright 2004 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
package org.openmaji.system.meem.hook.security;

import java.security.Principal;

import org.openmaji.meem.Facet;


/**
 * Client interface for monitoring access control changes on a meem.
 */
public interface AccessControlClient
	extends Facet
{
	/**
	 * Signal the addition of access privileges for the given principal.
	 * 
	 * @param principal the principal affected
	 * @param level the new access level of the subject
	 */
	public void accessAdded(Principal principal, AccessLevel level);

	/**
	 * Signal the removal of a subject at a given access level.
	 * 
	 * @param principal the principal affected
	 * @param level the previous access level of the subject
	 */
	public void accessRemoved(Principal principal, AccessLevel level);
}
