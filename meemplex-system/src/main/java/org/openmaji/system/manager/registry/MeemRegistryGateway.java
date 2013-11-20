/*
 * @(#)MeemRegistryGateway.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
package org.openmaji.system.manager.registry;

import org.openmaji.spi.MajiSPI;

/**
 * The MeemRegistryGateway is the master registry for the meem server. All requests
 * to locate meems go through the MeemRegistryGateway.
 * <p>
 * In general developers will probably not need to go via the gateway to find a meem,
 * it is easier to use Meem.spi.get().
 * </p>
 * 
 * @see org.openmaji.meem.Meem
 */
public interface MeemRegistryGateway {

	public class Singleton {
		private static MeemRegistryGateway meemRegistryGateway = null;

		public static MeemRegistryGateway get() {
			
			if (meemRegistryGateway == null) {
				throw new RuntimeException("Singleton MeemRegistryGateway not yet assigned");
			}
			
			return meemRegistryGateway;
		}
		
		public static void set(MeemRegistryGateway newMeemRegistryGateway) {
			if (meemRegistryGateway != null) {
				throw new RuntimeException("Singleton MeemRegistryGateway already assigned");
			} 
			
			meemRegistryGateway = newMeemRegistryGateway;			
		}
		
	}

	/**
	 * Nested class for service provider.
	 * 
	 * @see org.openmaji.spi.MajiSPI
     */
  public class spi {
    public static MeemRegistryGateway create() {
      return(
        (MeemRegistryGateway) MajiSPI.provider().create(
          MeemRegistryGateway.class
        )
      );
    }

    public static String getIdentifier() {
      return("meemRegistryGateway");
    };
  }
}