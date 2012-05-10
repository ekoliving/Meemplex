/*
 * @(#)NamedMeemRequest.java
 * Created on 1/03/2004
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.common.dnd.requests;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.requests.LocationRequest;
import org.openmaji.implementation.tool.eclipse.ui.dnd.NamedMeem;


/**
 * <code>NamedMeemRequest</code>.
 * <p>
 * @author Kin Wong
 */
abstract public class NamedMeemRequest extends LocationRequest {
	private NamedMeem[] namedMeems;
	private List newObjects;
		
	protected NamedMeemRequest(Object type) {
		super(type);
	}
	
	public int getSize() {
		// TODO[Kin] The next version of GEF will support returning the object count before the Drop happened.
		if(namedMeems == null) return 1;
		return namedMeems.length;
	}
	
	public NamedMeem[] getNamedMeems() {
		return namedMeems;
	}

	public void setData(Object data) {
		namedMeems = (NamedMeem[])data;
	}
	
	public List getNewObjects() {
		if(newObjects == null) {
			newObjects = createNewsObjects();
		} 
		return newObjects;
	}
	
	protected List createNewsObjects() {
		ArrayList newObjects = new ArrayList();
		NamedMeem[] namedMeems = getNamedMeems();
		if(namedMeems == null) return newObjects;
		
		for (int i = 0; i < namedMeems.length; i++) {
			NamedMeem namedMeem = namedMeems[i];
			newObjects.add(createNewObject(namedMeem));
		}
		return newObjects;
	}
	
	abstract protected Object createNewObject(NamedMeem namedMeem);
}
