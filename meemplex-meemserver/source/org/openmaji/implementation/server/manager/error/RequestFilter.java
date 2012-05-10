/*
 * @(#)RequestFilter.java
 *
 * Copyright 2004 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
package org.openmaji.implementation.server.manager.error;

import java.io.Serializable;

import org.openmaji.implementation.server.request.Request;
import org.openmaji.meem.filter.Filter;



/**
 * @author mg
 */
public class RequestFilter implements Filter, Serializable {
	private static final long serialVersionUID = 958244484334L;

	private final Request request;
	
	public RequestFilter(Request request) {
		this.request = request;
	}
	
	public Request getRequest() {
		return request;
	}
	
	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return request.hashCode();
	}
	
	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object object) {
		if (object == this) return(true);

    if ((object instanceof RequestFilter) == false) return(false);

    RequestFilter thatFilter = (RequestFilter) object;

    if (request.equals(thatFilter.request) == false) {
      return(false);
    }
    
    return true;
	}
}
