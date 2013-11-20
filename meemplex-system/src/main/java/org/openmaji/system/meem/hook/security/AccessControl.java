/*
 * @(#)AccessControl.java
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
 * AccessControl Facet - allows the system to authorise a prinicpal with a specific level
 * of access.
 */
public interface AccessControl
	extends Facet
{
	/**
	 * Add access by the principal to the Meem.
	 * 
	 * @param principal the principal.
	 * @param level access level.
	 */
	public void addAccess(Principal principal, AccessLevel level);

	/**
	 * Remove access by the principal to the Meem.
	 * 
	 * @param principal
	 */
	public void removeAccess(Principal principal);
}
