/*
 * @(#)InvocationContext.java
 *
 * Copyright 2005 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */
package org.openmaji.implementation.server.meem.invocation;

import java.io.*;
import java.util.HashMap;

/**
 * @author mg
 */
public class InvocationContext implements Serializable {
	
	private static final long serialVersionUID = 7259658962775320606L;

	public static final String CURRENT_MEEM_PATH = "currentMeemPath";
	public static final String CALLING_MEEM_PATH = "callingMeemPath";	
	
	private final HashMap<Serializable, Serializable> map = new HashMap<Serializable, Serializable>();

	public void put(String key, Serializable value) {
		synchronized (map) {
			map.put(key, value);
		}
	}

	public Serializable get(String key) {
		synchronized (map) {
			return (Serializable) map.get(key);
		}
	}

	public InvocationContext copy() {

		InvocationContext context = new InvocationContext();
		synchronized (map) {
			context.map.putAll(this.map);
		}
		return context;
	}
	
	public boolean isEmpty() {
		return map.size() == 0;
	}
	
	public String toString() {
		return "InvocationContext[" + map +"]";
	}

}