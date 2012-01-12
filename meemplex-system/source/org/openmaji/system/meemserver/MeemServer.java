/*
 * @(#)MeemServer.java
 *
 * Copyright 2004 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.system.meemserver;

import java.net.InetAddress;

import org.openmaji.meem.Facet;
import org.openmaji.spi.MajiSPI;


/**
 * <p>
 * ...
 * </p>
 *
 * @author  mg
 * @version 1.0
 */
public interface MeemServer extends Facet {
	
	public static final String PROPERTY_MEEMSERVER_NAME = "org.openmaji.server.meemserver.name";
	
	public class spi {
		private static String name;
		
		public static MeemServer create() {
			return ((MeemServer) MajiSPI.provider().create(MeemServer.class));
		}

		public static String getIdentifier() {
			return ("meemServer");
		};

		public static String getName() {
			if (name == null) {
				name = System.getProperty(PROPERTY_MEEMSERVER_NAME);
				if (name == null) {
					// if no property set, use the hostname as the meemserver name
					try {
						name = InetAddress.getLocalHost().getHostName();
					}
					catch (Exception e) {
					}
				}
			}

			return name;
		};
        
        /**
         * Return the location of the category that contains the essential meems
         * for this meem server.
         * 
         * @return the category name as a string
         */
        public static String getEssentialMeemsCategoryLocation() {
            return "/deployment/" + MeemServer.spi.getName() + "/essential";
        };
	}
}
