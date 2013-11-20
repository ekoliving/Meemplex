/*
 * @(#)SmartProxyRemoteReference.java
 *
 * Copyright 2004 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
package org.openmaji.implementation.server.meem.wedge.remote;

import org.openmaji.meem.Facet;
import org.openmaji.meem.MeemPath;
import org.openmaji.meem.wedge.reference.Reference;


/**
 * @author mg
 */
public class SmartProxyRemoteReference extends RemoteReference {
	private static final long serialVersionUID = 3292989782897L;

	public SmartProxyRemoteReference(Reference reference, MeemPath meemPath) {
		super(reference, meemPath);
		queueing = false;
	}
	
	public boolean isValid() {
		// always return true. The remote invocation handler will automatically remove
		// this ref if a remote exception occurs.
		return true;
	}
	
	public Object readResolve() {
		return this;
	}
	
	public Facet getTarget() {
		return reference.getTarget();
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return getClass().getName() + "[" +
	    "facetIdentifier="   + getFacetIdentifier() +
	    ", target="          + getTarget()          +
	    ", contentRequired=" + isContentRequired() +
	    ", filter="          + getFilter()+
	    ", valid="           + isValid()+
	    "]";
	}

}
