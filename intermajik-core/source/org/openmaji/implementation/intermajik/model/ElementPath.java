/*
 * @(#)ElementPath.java
 * Created on 5/10/2003
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.intermajik.model;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * <code>ElementPath</code>.
 * <p>
 * @author Kin Wong
 */
public class ElementPath implements Serializable {
	private static final long serialVersionUID = 6424227717462161145L;

	boolean connection = false;
	private LinkedList<Object> ids;
	
	public ElementPath() {
	}
	
	public boolean isConnection() {
		return connection;
	}
	
	public ElementPath(ElementPath that) {
		connection = that.connection;
		if(that.ids != null) {
			this.ids = new LinkedList<Object>(that.ids);
		}
	}
	
	public void setConnection(boolean connection) {
		this.connection = connection;
	}
	
	public int getDepth() {
		if(ids == null) return 0;
		return ids.size();
	}
	
	public Object popHead() {
		if(ids == null) return null;
    if ( ids.isEmpty() ) return null;
		Object id = ids.getFirst();
		ids.removeFirst();
		if(ids.isEmpty()) ids = null;
		return id;
	}
	
	public Object getHead() {
		if(ids == null) return null;
		return ids.getFirst();
	}

	public void pushTail(Object id) {
    if ( id == null ) return;
		if(ids == null) ids = new LinkedList<Object>();
		ids.addLast(id);
	}
	
	public ElementPath append(ElementPath path) {
		if(path.ids == null) return this;
		if(ids == null) ids = new LinkedList<Object>();
		ids.addAll(path.ids);
		return this;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		int code = (isConnection())? 1:0;
		if(ids == null) return code;
		
		Iterator it = ids.iterator();
		while(it.hasNext()) {
			code += it.next().hashCode();
		}
		return code;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if(obj == null) return false;
		if(obj == this) return true;
		if(!(obj instanceof ElementPath)) return false;
		
		ElementPath that = (ElementPath)obj;
		if(isConnection() != that.isConnection()) return false;
		
		if(that.ids == null) return (ids == null);
		if(ids.size() != that.ids.size()) return false;
		
		Iterator itThis = ids.iterator();
		Iterator itThat = that.ids.iterator();	
		while(itThis.hasNext()) {
			if(!itThis.next().equals(itThat.next())) return false;
		}
		return true;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		String text = (isConnection()? "Connection Path: ":"Object Path: ");
		if(ids != null) {
			Iterator it = ids.iterator();
			while(it.hasNext()) {
				text += it.next().toString();
				if(it.hasNext()) text += "/";
			}
		}
		return text;
	}
}
