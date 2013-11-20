/*
 * @(#)MeemCore.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
package org.openmaji.system.meem.core;

import org.openmaji.meem.Facet;
import org.openmaji.meem.Meem;
import org.openmaji.meem.MeemContext;
import org.openmaji.meem.MeemPath;
import org.openmaji.system.meem.definition.MeemContent;
import org.openmaji.system.meem.definition.MeemStructure;


/**
 * <p>
 * The system version of the MeemContext. In general application developers should
 * use the MeemContext class.
 * </p>
 * <p>
 * Note: Implementation thread safe = Not applicable
 * </p>
 * @author  Andy Gelme
 * @version 1.0
 * @see org.openmaji.meem.Meem
 * @see org.openmaji.meem.definition.MeemDefinition
 * @see org.openmaji.system.meem.definition.MeemStructure
 * @see org.openmaji.system.meem.hook.invoke.InvocationList
 */
public interface MeemCore 
	extends MeemContext
{
    /**
     * Add a target to the conduit specified by the given identifier and class.
     * <p>
     * Note: in this case it is unnecessary to put the string "Conduit" on the end of the
     * identifier. For example, in a wedge where you would define "lifeCycleClientConduit"
     * as the name of the field holding the conduit, in the case of using the MeemCore
     * methods you would just use the real identifier "lifeCycleClient".
     * </p>
     * @param conduitIdentifier name of the conduit.
     * @param specification the interface the conduit implements.
     * @param implementation the target implementation.
     */
    public void addConduitTarget(String conduitIdentifier, Class<? extends Facet>  specification, Object implementation);
  
    /**
     * Return the conduit source object for the passed in identifier. The source object is
     * the object used to populate wedges. When methods on it are invoked it will propogate 
     * the call to all the conduits targets.
     * <p>
     * Note: in this case it is unnecessary to put the string "Conduit" on the end of the
     * identifier. For example, in a wedge where you would define "lifeCycleClientConduit"
     * as the name of the field holding the conduit, in the case of using the MeemCore
     * methods you would just use the real identifier "lifeCycleClient".
     * </p>
     * @param conduitIdentifier the name of the conduit of interest.
     * @param specification the interface the source implements.
     * @return the source object for the conduit.
     */
    public Object getConduitSource(String conduitIdentifier, Class<? extends Facet>  specification);

    /**
     * Remove the passed in implementation from the conduit's list of targets.
     * <p>
     * Note: in this case it is unnecessary to put the string "Conduit" on the end of the
     * identifier. For example, in a wedge where you would define "lifeCycleClientConduit"
     * as the name of the field holding the conduit, in the case of using the MeemCore
     * methods you would just use the real identifier "lifeCycleClient".
     * </p>
     * @param conduitIdentifier the name of the conduit affected.
     * @param implementation the target object to be removed.
     */
    public void removeConduitTarget(String conduitIdentifier, Facet implementation);
  
    /**
     * Remove the conduit associated with the given identifier.
     * <p>
     * Note: in this case it is unnecessary to put the string "Conduit" on the end of the
     * identifier. For example, in a wedge where you would define "lifeCycleClientConduit"
     * as the name of the field holding the conduit, in the case of using the MeemCore
     * methods you would just use the real identifier "lifeCycleClient".
     * </p>
     * @param conduitIdentifier the name of the conduit to be removed.
     */
    public void removeConduit(String conduitIdentifier);
  
    /**
     * Return the life cycle manager associated with this meem.
     * 
     * @return this meem's life cycle manager.
     */
    public Meem getLifeCycleManager();

	/**
	 * Return the life cycle manager associated with this meem.
	 * 
	 * @return this meem's life cycle manager.
	 */
    public Meem getMeemRegistry();

	/**
	 * Return the thread manager associated with this meem.
	 * 
	 * @return this meem's thread manager.
	 */
    public Meem getThreadManager();

	/**
	 * Return the meem store associated with this meem.
	 * 
	 * @return this meem's meem store.
	 */
    public Meem getMeemStore();
  
	/**
	 * Return the flight recorder meem associated with this meem.
	 * <p>
	 * Note: this is currently not implemented by the server and may be deprecated.
	 * </p>
	 * @return this meem's associated flight recorder meem.
	 */
    public Meem getFlightRecorder();

    /**
     * Return true if the meem represented by this meemCore has an inbound facet 
     * implementing the passed in class.
     * 
     * @param specification type we are looking for.
     * @return true if there is a match, false otherwise.
     */
    public boolean isA(Class<? extends Facet> specification);

    /**
     * Return this meem's meem path.
     * 
     * @return the containing meem's meem path.
     */
	public MeemPath getMeemPath();

    /**
     * Return this meem's underlying meem structure.
     * 
     * @return the containing meem's meem structure.
     */
	public MeemStructure getMeemStructure();
	
	/**
	 * Get our total meem content.
	 * 
	 * @return the content for the meem.
	 */
	public MeemContent getContent();
	
	/**
	 * Restore our content from the passed in meem content.
	 * 
	 * @param meemContent the meem content we wish to restore from.
	 */
	public void restoreContent(MeemContent meemContent);
}
