/*
 * @(#)KeyReceiver.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
package org.openmaji.server.meem.wedge.security;

import java.security.Key;

import org.openmaji.meem.Facet;


/**
 *
 */
public interface KeyReceiver extends Facet
{
	public void processSessionKey(int sequenceNumber, Key sessionKey);
}
