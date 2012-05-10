/*
 * @(#)FacetClientCallback.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
package org.openmaji.system.meem;

/**
 * Call back for use with the facetClientConduit.
 */
public interface FacetClientCallback
{
	public void facetExists(boolean facetExists);
}
