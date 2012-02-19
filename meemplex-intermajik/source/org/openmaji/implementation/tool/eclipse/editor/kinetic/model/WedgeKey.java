/*
 * @(#)WedgeKey.java
 * Created on 23/03/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic.model;

import java.io.Serializable;

/**
 * The <code>WedgeKey</code> uniquely identifies a wedge in the system.
 * <p>
 * @author Kin Wong
 */
public class WedgeKey implements Serializable, Cloneable {
	
	private static final long serialVersionUID = 6424227717462161145L;

	private Object meemAttributeKey;
	private Object wedgeAttributeKey;
	
	/**
	 * Constructs an instance of <code>WedgeKey</code>.
	 * <p>
	 * @param meemAttributeKey The meem attribute key.
	 * @param wedgeAttributeKey The wedge attribute key.
	 */
	public WedgeKey(Object meemAttributeKey, Object wedgeAttributeKey) {
		this.meemAttributeKey = meemAttributeKey;
		this.wedgeAttributeKey = wedgeAttributeKey;
	}
	
	/**
	 * Gets the meem attribute key of this wedge key.
	 * @return Object The meem attribute key of this wedge key.
	 */
	public Object getMeemAttributeKey() {
		return meemAttributeKey;
	}

	/**
	 * Gets the wedge attribute key of this wedge key.
	 * @return Object The wedge attribute key of this wedge key.
	 */
	public Object getWedgeAttributeKey() {
		return wedgeAttributeKey;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public Object clone() {
			try{
				return super.clone();
			}
			catch(CloneNotSupportedException e) {
				return null;
			}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object that) {
		if(that == null) return false;
		if(!(that instanceof WedgeKey)) return false;
		
		WedgeKey thatKey = (WedgeKey)that;
		return 	wedgeAttributeKey.equals(thatKey.wedgeAttributeKey) && 
						meemAttributeKey.equals(thatKey.meemAttributeKey);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode(){
		return meemAttributeKey.hashCode() + wedgeAttributeKey.hashCode();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return meemAttributeKey.toString() + "," + wedgeAttributeKey.toString();
	}
}
