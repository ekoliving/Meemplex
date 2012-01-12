/*
 * @(#)MeemPathLeasedResource.java
 *
 * Copyright 2004 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */
package org.openmaji.implementation.server.meem.wedge.remote;

import org.openmaji.meem.MeemPath;

import net.jini.id.Uuid;
import net.jini.id.UuidFactory;

import com.sun.jini.landlord.LeasedResource;

/**
 * @author mg
 */
public class MeemPathLeasedResource implements LeasedResource {	
	
	private final Uuid cookie;
	//private final MeemPath meemPath;
	private long expiration = 0;
	
	public MeemPathLeasedResource(MeemPath meemPath) {
		//this.meemPath = meemPath;
		cookie = UuidFactory.generate();
	}
	
	public Uuid getCookie() {
		return cookie;
	}
	
	public long getExpiration() {
		return expiration;
	}
	
	public void setExpiration(long newExpiration) {
		expiration = newExpiration;
	}
}
