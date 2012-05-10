/*
 * Copyright 2004 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.system.meem.licensing;

import org.openmaji.meem.definition.MeemLicensingType;

/**
 * <p>With the introduction of licensing, there is a requirement to allow 
 * Wedges to know the MeemLicensingType identifier in which they are 
 * running, and can therefore elect whether they elect to run inside that 
 * Meem. Thus, a new interface has be added to the Meem API, which is to be 
 * implemented on all Wedges that wish to enforce licensing.</p>
 * 
 * <p>The LicensingValidationProvider interface returns an array of 
 * <code>MeemLicensingType</code> objects, each of which refers to a 
 * <code>MeemLicensingType</code> identifier in which the Wedge is allowed 
 * to run.</p>
 * 
 * <p>On construction of the Meem, the MeemCore will check all wedges to 
 * see if they implement this interface, and if so, retrieve the Wedge 
 * <code>MeemLicensingType</code> identifiers and check to see if there is a match to 
 * the MeemLicensingType held in the Meem definition. If there is a match, 
 * the Meem is constructed. If not, the Meem construction will throw an error.</p>
 */

public interface LicensingValidationProvider {

	/**
	 * <p>This method returns an array of MeemLicensingType objects, each of which 
	 * refers to a <code>MeemLicensingType</code> identifier in which the Wedge is 
	 * allowed to run.<p>
	 * 
	 * @return An array of of <code>MeemLicenseType</code> objects. If null is returned, 
	 * the implementing wedge will not be auble to run inside any Meems.
	 */
	
	public MeemLicensingType[] getValidLicensingTypes();
	
}
