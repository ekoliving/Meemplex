/*
 * @(#)LifeCycleManagerDefinitionFactory.java
 *
 *  Copyright 2004 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 *  This software is the proprietary information of EkoLiving Pty Ltd.
 *  Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.system.manager.lifecycle;

import org.openmaji.meem.definition.MeemDefinition;
import org.openmaji.spi.MajiSPI;


/**
 * <p>
 * Factory for creating MeemDefinitions for LifeCycleManagers.
 * </p>
 * eg:
 * <pre>
 * LifeCycleManagerDefinitionFactory.spi.get().createTransient(
 *   new Class[] { ApplicationWedge.class });
 * </pre>
 *
 * @author  mg
 * @version 1.0
 */
public interface LifeCycleManagerDefinitionFactory {
	
	/**
	 * Create a MeemDefinition for a persisting LifeCycleManager with the passed
	 * in wedges added to it.
	 * @param wedges Array of Wedge classes to be added to the LifeCycleManager MeemDefinition
	 * @return MeemDefinition for persisting LifeCycleManager
	 */
	public MeemDefinition createPersisting(Class<?>[] wedges);
	
	/**
	 * Create a MeemDefinition for a transient LifeCycleManager with the passed
	 * in wedges added to it.
	 * @param wedges Array of Wedge classes to be added to the LifeCycleManager MeemDefinition
	 * @return MeemDefinition for transient LifeCycleManager
	 */
	public MeemDefinition createTransient(Class<?>[] wedges);
	
	/**
	 * Access point to service provider.
	 */
	public static class spi
	{
		/**
		 * Return a MeemUtility object.
		 * 
		 * @return a MeemUtility object.
		 */
		public static synchronized LifeCycleManagerDefinitionFactory get() {
		  return  (LifeCycleManagerDefinitionFactory) MajiSPI.provider().create(LifeCycleManagerDefinitionFactory.class);
		}
	}

}
