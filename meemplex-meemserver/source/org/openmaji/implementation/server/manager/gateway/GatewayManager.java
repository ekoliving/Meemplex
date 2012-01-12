/*
 * @(#)GatewayManagerManager.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.server.manager.gateway;

import org.openmaji.meem.Facet;
import org.openmaji.spi.MajiSPI;


/**
 * Basic facet gateway manager - this exists to allow delegate classes from outside
 * the "meem" world to be turned into facets.
 */
public interface GatewayManager extends Facet {

	/**
	 * Nested class for service provider.
	 * 
	 * @see org.openmaji.spi.MajiSPI
	 */
	public class spi {
		
	    public static synchronized GatewayManager create() {
	      return  (GatewayManager) MajiSPI.provider().create(GatewayManager.class);
	    }
		
	    public static String getIdentifier() {
	      return("gatewayManager");
	    };
	}
}