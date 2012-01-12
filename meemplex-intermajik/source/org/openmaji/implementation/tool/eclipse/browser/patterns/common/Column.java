/*
 * @(#)Column.java
 * Created on 14/05/2004
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.browser.patterns.common;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.openmaji.implementation.tool.eclipse.hierarchy.nodes.Node;


/**
 * <code>Column</code>.
 * <p>
 * @author Kin Wong
 */
abstract public class Column {
	private Object id;
	private String name;
	private int style;
	private int width = -1;
	
	/**
	 * Constructs an instance of <code>Column</code>.
	 * <p>
	 * 
	 */
	public Column(Object id, String name, int width, int style) {
		this.id = id;
		this.name = name;
		this.width = width;
		this.style = style;
	}
	
	public Column(Object id, String name, int width) {
		this(id, name, width, SWT.LEFT);
	}
	
	public Object getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public int getStyle() {
		return style;
	}
	
	public int getWidth() {
		return width;
	}
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return id.hashCode();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if(obj == null) return false;
		if(!(obj instanceof Column)) return false;
		Column that = (Column)obj;
		return getId().equals(that.getId());
	}
	
	abstract protected String getText(Node node);
	protected Image getImage(Node node) {
		return null;
	}
}
