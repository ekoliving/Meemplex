/*
 * @(#)ScopedMeemPath.java
 *
 * Copyright 2004 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.server.manager.registry;

import java.io.Serializable;

import org.openmaji.meem.MeemPath;
import org.openmaji.meem.definition.Scope;


/**
 * <p>
 * ...
 * </p>
 *
 * @author  mg
 * @version 1.0
 */
public class ScopedMeemPath implements Serializable {
	private static final long serialVersionUID = 0L;
	
	private MeemPath meemPath;
	private Scope scope;
	
	public ScopedMeemPath(MeemPath meemPath, Scope scope) {
		this.meemPath = meemPath;
		this.scope = scope;
	}

	public MeemPath getMeemPath() {
		return meemPath;
	}

	public Scope getScope() {
		return scope;
	}
	
	public int hashCode() {
		return meemPath.hashCode() ^ scope.hashCode();
	}
	
	public boolean equals(Object obj) {
		if (!(obj instanceof ScopedMeemPath)) return false;
		ScopedMeemPath other = (ScopedMeemPath) obj;
		return other.meemPath.equals(this.meemPath) && other.scope.equals(this.scope);
	}

	public String toString() {
		return "ScopedMeemPath [" + meemPath + ", " + scope + "]";
	}
	
}
