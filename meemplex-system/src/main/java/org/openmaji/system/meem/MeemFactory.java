/*
 * @(#)MeemFactory.java
 *
 * Copyright 2004 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
package org.openmaji.system.meem;

import org.openmaji.meem.Meem;
import org.openmaji.meem.definition.MeemDefinition;
import org.openmaji.meem.wedge.lifecycle.LifeCycleState;
import org.openmaji.spi.MajiSPI;


/**
 * Interface for factories that take MeemDefinitions and create Meems.
 * <p>
 * The MeemFactory is designed to allow people operating outside the MeemServer to create
 * meems from meem definitions, inserting them into categories if necessary.
 * <p>
 * Note: a MeemServer has two standard life cycle managers for creating persistent and transient meems, the
 * MeemPaths and what they represent for the two standard life cycle managers are:
 * <ul>
 * <li>"hyperspace:" + MeemServer.spi.getEssentialMeemsCategoryLocation() + "/persistingLifeCycleManager" - the essential or persisting life cycle manager.</li>
 * <li>"hyperspace:" + MeemServer.spi.getEssentialMeemsCategoryLocation() + "/transientLifeCycleManager" - the transient life cycle manager.</li>
 * </ul>
 */
public interface MeemFactory
{
	/**
	 * Create a persistent, or transient, meem with an initial life cycle state of READY under the control of the passed
	 * in life cycle manager meem. Whether or not the meem is persistent will depend on the life cycle manager passed in.
	 * 
	 * @param meemDefinition definition for the meem to be created.
	 * @param lifeCycleManagerMeem controling life cycle manager.
	 * @return the created meem.
	 */
	public Meem create(MeemDefinition meemDefinition, Meem lifeCycleManagerMeem);

    /**
     * Create a persistent, or transient, meem with an initial life cycle state under control of the passed in life cycle manager meem.
     *  Whether or not the meem is persistent will depend on the life cycle manager passed in.
     * 
     * @param meemDefinition definition for the meem to be created.
     * @param lifeCycleManagerMeem controling life cycle manager.
    * @param initialState initial life cycle state.
    * @return the created meem.
    */
	public Meem create(MeemDefinition meemDefinition, Meem lifeCycleManagerMeem, LifeCycleState initialState);
	
	/**
	 * access point to service provider.
	 */
	public static class spi
	{
        /**
         * Return an object that implements MeemFactory.
         * 
         * @return a MeemFactory object.
         */
        public static MeemFactory get() {
            return (MeemFactory)(MajiSPI.provider().create(MeemFactory.class));
        }
	}
}
