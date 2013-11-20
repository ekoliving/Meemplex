/*
 * @(#)MeemIconicPresentation.java
 * Created on 3/03/2004
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.system.presentation;

import java.io.Serializable;

/**
 * <code>MeemIconicPresentation</code> represents iconic presentation for a 
 * Meem. It is currently defined 2 sets of icon, one containing images of size 
 * 16 x 16 and this is refered as the small icon set. The other contains images 
 * of size 32 x 32 and is named large icon set.
 * <p>
 * @author Kin Wong
 */
public class MeemIconicPresentation implements Serializable, Cloneable {
	private static final long serialVersionUID = 7381625865736823251L;
	
	private byte[] icon16;
	private byte[] icon32; 
	
	/**
	 * Constructs an instance of <code>MeemIconicPresentation</code>.
	 * <p>
	 * @param icon16 The small icon set.
   * @param icon32 The large icon set.
	 */
	public MeemIconicPresentation(byte[] icon16, byte[] icon32) {
		this.icon16 = icon16;
		this.icon32 = icon32;
	}
	
	/**
	 * Constructs an instance of <code>MeemIconicPresentation</code>.
	 * <p>
	 */	
	public MeemIconicPresentation() {
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public Object clone() {
		try {
			return super.clone();
		}
		catch(CloneNotSupportedException e) {
			// Should never get to here.
			return null;			
		}
	}
	
	/**
	 * Gets the small icon.
	 * <p>
	 * @return A byte array that contains the small icon in the native format.
	 */
	public byte[] getSmallIcon() {
		return icon16;
	}

	/**
	 * Gets the large icon.
	 * <p>
	 * @return A byte array that contains the large icon in the native format.
	 */
	public byte[] getLargeIcon() {
		return icon32;
	}
	
	/**
	 * 
	 * @param iconData
	 */
	public void setSmallIcon(byte[] iconData) {
		icon16 = iconData;
	}
	
	/**
	 * 
	 * @param iconData
	 */
	public void setLargeIcon(byte[] iconData) {
		icon32 = iconData;
	}
	
	static public String getLargeIconName(String smallName) {
		int index = smallName.lastIndexOf("16");
		if(index == -1) return smallName;
		String largeName = smallName.substring(0, index);
		largeName += "32" + smallName.substring(index + 2);
		return largeName;		
	}
}
