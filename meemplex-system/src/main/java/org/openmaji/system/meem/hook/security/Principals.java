/*
 * @(#)Principals.java
 *
 * Copyright 2004 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
package org.openmaji.system.meem.hook.security;

/**
 * Standard Principals.
 */
public interface Principals
{
	public static final GroupPrincipal OTHER = new GroupPrincipal("other");
	public static final GroupPrincipal SYSTEM = new GroupPrincipal("system");
	public static final GroupPrincipal USER = new GroupPrincipal("user");
}
