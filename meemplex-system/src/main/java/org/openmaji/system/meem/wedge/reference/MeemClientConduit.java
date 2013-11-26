/*
 * @(#)MeemClientConduit.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
package org.openmaji.system.meem.wedge.reference;

import org.openmaji.meem.Facet;
import org.openmaji.meem.Meem;

/**
 * Interface for meemClientConduit, a system provided conduit that can be used to
 * request inbound facets on a meem external to the one the conduit is in.
 */
public interface MeemClientConduit
{
	/**
	 * Request a reference on the passed in meem. The reference will be passed back
	 * via the referenceCallback. If no reference is found a null will be returned to the
	 * call back.
	 * 
	 * @param meem the meem of interest.
	 * @param inboundFacetIdentifier the identifier for the inbound facet of interest.
	 * @param specification the interface the inbound facet is expected to implement.
	 * @param referenceCallback the call back to pass the reference back on.
	 */
    public <T extends Facet> void provideReference(Meem meem, String inboundFacetIdentifier,
        Class<T> specification, MeemClientCallback<T> referenceCallback);
}
