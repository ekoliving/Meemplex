/*
 * @(#)WedgeAddRequest.java
 * Created on 29/04/2003
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.common.dnd.requests;

import org.eclipse.gef.requests.LocationRequest;

/**
 * <code>MeemCloneRequest</code>.
 * <p>
 * @author mg
 */
public class WedgeAddRequest extends LocationRequest {
	public static final String REQ_WEDGE_ADD = "Wedge Add Request";

	private Object data;
	public WedgeAddRequest() {
		super(REQ_WEDGE_ADD);
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

}
