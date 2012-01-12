/*
 * @(#)MeemSetProxy.java
 * Created on 12/05/2004
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.client;

import java.util.*;

import org.openmaji.meem.Meem;


/**
 * <code>MeemSetProxy</code>.
 * <p>
 * @author Kin Wong
 */
abstract public class MeemSetProxy extends FacetProxy {
	private Map meemMap = null;
	
	/**
	 * Constructs an instance of <code>MeemSetProxy</code>.
	 * <p>
	 * @param meemClientProxy
	 * @param specs
	 */
	protected MeemSetProxy(
		MeemClientProxy meemClientProxy,
		FacetSpecificationPair specs) {
		super(meemClientProxy, specs);
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.client.FacetProxy#clearContent()
	 */
	protected void clearContent() {
		meemMap = null;
	}

	public Meem[] getMeems() {

		if(meemMap == null) return new Meem[0];
		return (Meem[])meemMap.values().toArray(new Meem[0]);
	}
	
	public int getSetSize() {
		if(meemMap == null) return 0;
		return meemMap.size();
	}
	
	public boolean isSetEmpty() {
		if(meemMap == null) return true;
		return meemMap.isEmpty();
	}
	
	protected void addMeem(Meem meem) {
		if(meemMap == null) meemMap = new HashMap();
		meemMap.put(meem.getMeemPath(), meem);
	}
	
	protected boolean removeMeem(Meem meem) {
		if(meemMap == null) return false;
		if (meemMap.remove(meem.getMeemPath()) != null) {
			return true;
		}
		return false;
	}
}
