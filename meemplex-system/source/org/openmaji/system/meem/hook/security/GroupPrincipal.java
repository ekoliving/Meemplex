/*
 * @(#)GroupPrincipal.java
 *
 * Copyright 2004 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */
package org.openmaji.system.meem.hook.security;

import java.io.Serializable;
import java.security.Principal;

/**
 * Principal identifier for a group.
 */
public class GroupPrincipal implements Principal, Serializable {
	private static final long serialVersionUID = 2489153323493391229L;

	private String name;

	public GroupPrincipal(String name) {
		this.name = name;
	}

	/* (non-Javadoc)
	 * @see java.security.Principal#getName()
	 */
	public String getName() {
		return name;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		
		//new Exception("Group Equals Trace").printStackTrace();
		if (obj instanceof GroupPrincipal) {
			GroupPrincipal other = (GroupPrincipal) obj;

			return other.name.equals(this.name);
		}

		return false;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return (name == null) ? 36787 : name.hashCode();
	}

	public String toString() {
		return "[GroupPrincipal: " + name + "]";
	}
}
