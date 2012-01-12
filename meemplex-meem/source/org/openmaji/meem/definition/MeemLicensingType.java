/*
 * Copyright 2004 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.meem.definition;

import java.io.Serializable;

/**
 * <p>Stong type class for the MeemDefinition property "MeemLicensingType".
 * This property is read from the meemkit descriptor, and is used to designate
 * which license the Meem requires upon activation.</p>
 */

public class MeemLicensingType
    implements Serializable
{
	private static final long serialVersionUID = -1178365040590887015L;

	private String mLicensingType;
	
	/**
	 * Setter for the licensing type identifier
	 * @param type The licensing type identifer.
	 */
	
	public void setLicensingType(String type) {
		mLicensingType = type;
	}
	
	/**
	 * Getter for the licensing type identifier 
	 * @return The licensing type identifer for the meem
	 */
	
	public String getLicensingType() {
		return mLicensingType;
	}
	
}
