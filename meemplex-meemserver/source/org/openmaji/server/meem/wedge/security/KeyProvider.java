/*
 * @(#)KeyExtractorWedge.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
package org.openmaji.server.meem.wedge.security;

import org.openmaji.meem.Facet;
import org.openmaji.meem.MeemPath;


/**
 * basic facet for sending keys.
 */
public interface KeyProvider extends Facet
{
	public void requestSessionKey(int requestNumber, MeemPath sourceMeemPath, byte[] encSessionKey);
}
