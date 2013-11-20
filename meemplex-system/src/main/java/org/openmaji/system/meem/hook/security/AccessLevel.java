/*
 * @(#)AccessLevel.java
 *
 * Copyright 2004 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
package org.openmaji.system.meem.hook.security;

import java.io.Serializable;

/**
 * AccessControl levels for a meem.
 */
public class AccessLevel
	implements Serializable
{
	private static final long serialVersionUID = -7746262680928062727L;
	
	public static final AccessLevel	DENY = new AccessLevel(0);
	public static final AccessLevel	READ = new AccessLevel(1);
	public static final AccessLevel	WRITE = new AccessLevel(2);
	public static final AccessLevel READ_WRITE = new AccessLevel(3);
	public static final AccessLevel	CONFIGURE = new AccessLevel(4);
	public static final AccessLevel	ADMINISTER = new AccessLevel(5);
	
	private int level;
	
	private AccessLevel(
		int	level)
	{
		this.level = level;
	}
	
    /**
     * Return the ordinal value of the access level.
     * 
     * @return levels ordinal value
     */
	public int level()
	{
		return level;
	}

    /**
     * Return true if the level passed in implies an access level that this applies to,
     * false otherwise.
     * 
     * @param level
     * @return true if the level passed in is implied, false otherwise.
     */
    public boolean isGrantedBy(AccessLevel level)
    {
    	if (level == null) {
    		return false;
    	}
    	
    	if (this.level == DENY.level()) {
    		return false;
    	}

    	if (this.level() == READ.level() && level.level() == WRITE.level()) {
    		return false;
    	}
    	
    	return (level.level() >= this.level());
    }
    
    public boolean equals(Object object) {
    	return 
			object != null && 
			object instanceof AccessLevel && 
			((AccessLevel)object).level == this.level;
    }
    
    public String toString()
    {	
    	return "AccessLevel: " + this.level();
    }
}
