/*
 * @(#)MeemTypeFilter.java
 * Created on 27/02/2004
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.filters;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.viewers.ViewerFilter;

/**
 * <code>MeemTypeFilter</code>.
 * <p>
 * @author Kin Wong
 */
abstract public class MeemTypeFilter extends ViewerFilter {
	private Set classes;
	
	public void addFacet(Class clazz) {
		if(classes == null) classes = new HashSet();
		classes.add(clazz);
	}
	
	public boolean removeFacet(Class clazz) {
		if(classes == null) return false;
		return classes.remove(clazz);
	}
	
	protected Set getClasses() {
		return classes;
	}
}
