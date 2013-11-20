/*
 * @(#)MeemRegistryJiniUtility.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */

package org.openmaji.implementation.server.manager.registry;

import org.openmaji.meem.MeemPath;

import net.jini.core.lookup.ServiceID;


/**
 * <p>
 * ...
 * </p>
 * <p>
 * Note: Implementation thread safe = Yes (2004-01-03)
 * </p>
 * @author  Andy Gelme
 * @version 1.0
 */

public class MeemRegistryJiniUtility {

  /**
   * Create a UID from a storage meem path.
   * 
   * @param meemPath
   */
  public static ServiceID createServiceID(
	MeemPath	meemPath) {

	String	location = meemPath.getLocation();
	String	uidString = location.substring(location.indexOf('/') +1);
	
	long part1 = Long.parseLong(uidString.substring( 0,  8), 16);
	long part2 = Long.parseLong(uidString.substring( 9, 13), 16);
	long part3 = Long.parseLong(uidString.substring(14, 18), 16);
	long part4 = Long.parseLong(uidString.substring(19, 23), 16);
	long part5 = Long.parseLong(uidString.substring(24, 36), 16);

	long mostSignificantBits  = (part1 << 32) + (part2 << 16) + part3;
	long leastSignificantBits = (part4 << 48) +  part5;

	return(new ServiceID(mostSignificantBits, leastSignificantBits));
  }
  
  public static ServiceID createServiceID(
    String uidString) {

    long part1 = Long.parseLong(uidString.substring( 0,  8), 16);
    long part2 = Long.parseLong(uidString.substring( 9, 13), 16);
    long part3 = Long.parseLong(uidString.substring(14, 18), 16);
    long part4 = Long.parseLong(uidString.substring(19, 23), 16);
    long part5 = Long.parseLong(uidString.substring(24, 36), 16);

    long mostSignificantBits  = (part1 << 32) + (part2 << 16) + part3;
    long leastSignificantBits = (part4 << 48) +  part5;

    return(new ServiceID(mostSignificantBits, leastSignificantBits));
  }
  
  // -mg- I don't think this is really the correct place for this, but will do for now
  public static final String JINI_CONFIGURATION_FILE = "org.openmaji.server.jini.configuration";
}
