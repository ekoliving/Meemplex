/*
 * @(#)Request.java
 *
 * Copyright 2004 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */
package org.openmaji.implementation.server.request;

import java.io.Serializable;

import org.openmaji.meem.MeemPath;


/**
 * @author mg
 */
public class Request implements Serializable {
	private static final long serialVersionUID = 534540102928363464L;

	private final String meemServerName;

	private final Serializable uniqueIdentifier;
	
	private final MeemPath errorRepository;

	private final int oneUp;
	
	Object suspended = null;

	public Request(String meemServerName, MeemPath errorRepository, Serializable uniqueIdentifier) {
		this.meemServerName = meemServerName;
		this.uniqueIdentifier = uniqueIdentifier;
		this.oneUp = getNextId();
		this.errorRepository = errorRepository;
	}

	public String toString() {
		return "Request[" + meemServerName + " : " + uniqueIdentifier + " : " + oneUp + "]";
	}

	private static int currentId = 0;

	private static synchronized int getNextId() {
		return currentId++;
	}
	
	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (obj instanceof Request) {
			Request request = (Request) obj;
			if (request.oneUp != this.oneUp) return false;
			if (!request.uniqueIdentifier.equals(this.uniqueIdentifier)) return false;
			
			if (request.meemServerName != null && this.meemServerName != null) {
				if (!request.meemServerName.equals(this.meemServerName)) return false;
			}
			
			if (request.errorRepository != null && this.errorRepository != null) {
				if (!request.errorRepository.equals(this.errorRepository)) return false;
			}
			return true;
		}
		return super.equals(obj);
	}
	
	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		int code = oneUp + uniqueIdentifier.hashCode();
		if (errorRepository != null) {
			code ^= errorRepository.hashCode();
		}
		if (meemServerName != null) {
			code ^= meemServerName.hashCode();
		}
		return code;
	}
	
	public MeemPath getErrorRepository() {
		return errorRepository;
	}
}