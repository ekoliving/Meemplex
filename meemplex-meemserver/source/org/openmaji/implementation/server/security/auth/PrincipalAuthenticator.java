/*
 * @(#)PrincipalAuthenticator.java
 *
 * Copyright 2004 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */
package org.openmaji.implementation.server.security.auth;

import java.security.Principal;

/**
 * System authenticator for a Principal
 */
public interface PrincipalAuthenticator
{
	public Principal getPrincipal();
	
	public boolean isValid();
	
	public void revoke();
}
