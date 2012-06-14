/*
 * @(#)ServerGateway.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
package org.openmaji.system.gateway;

import javax.security.auth.Subject;

import org.openmaji.meem.Facet;
import org.openmaji.meem.Meem;
import org.openmaji.meem.MeemPath;
import org.openmaji.spi.MajiSPI;


/**
 * Basic gateway to the MeemServer for a given subject.
 * <p>
 * Ordinarily a gateway would be created using the spi.create(Subject) method and passing it the Subject you want as
 * an argument. This is the Maji equivalent of logging in. In the event the thread is already executing under 
 * an authorised subject but external to a meem a gateway can be created using the spi.create() method with no
 * parameters and the resulting gateway will adopt the current Subject.
 * <p>
 * Essential meems are accessed via the category with the hyperspace path:
 * <pre>
 * "hyperspace:" + MeemServer.spi.getEssentialMeemsCategoryLocation()
 * </pre>
 * with the name for a primary MeemServer often being <b>meemServer_01</b>
 * </p>
 * <p>
 * The standard paths being as follows:
 * <pre>
 * "hyperspace:" + MeemServer.spi.getEssentialMeemsCategoryLocation() + "/licenseStoreFactory" - the meem licensing factory
 * "hyperspace:" + MeemServer.spi.getEssentialMeemsCategoryLocation() + "/meemRegistry" - the meem server registry
 * "hyperspace:" + MeemServer.spi.getEssentialMeemsCategoryLocation() + "/meemServerController" - the meem server controller
 * "hyperspace:" + MeemServer.spi.getEssentialMeemsCategoryLocation() + "/meemStore" - meem store
 * "hyperspace:" + MeemServer.spi.getEssentialMeemsCategoryLocation() + "/meemkitManager" - the meemkit manager
 * "hyperspace:" + MeemServer.spi.getEssentialMeemsCategoryLocation() + "/persistingLifeCycleManager" - the essential or persisting life cycle manager.
 * "hyperspace:" + MeemServer.spi.getEssentialMeemsCategoryLocation() + "/transientLifeCycleManager" - the transient life cycle manager.
 * "hyperspace:" + MeemServer.spi.getEssentialMeemsCategoryLocation() + "/userManager" - the meem server user manager
 * </pre>
 * </p>
 */
public interface ServerGateway 
{

    /** 
 	 * return the given meem for the passed in MeemPath. 
 	 * "hyperspace:/" will return the highest allowable root for this subject.
 	 * 
     * @param path the hyperspace path for the meem of interest.
 	 * @return the meem for the meem path.
 	 */ 
    Meem getMeem(MeemPath path);
    
//    void getMeem(MeemPath meemPath, AsyncCallback<Meem> callback);
        
    /**
     * Return a suitably wrapped target for passing into the MeemServer's MeemSpace.
     * 
     * @param implementation the internal object that will be called by the target's invocation handler.
     * @param specification the class defining how the proxy should appear externally.
     * @return a wrapped version of the delegate object passed in.
     */
    <T extends Facet> T getTargetFor(T implementation, Class<T> specification);
    
    /**
     * Revoke a generated target so that it will no longer accept calls.
     * 
     * @param proxy the proxy that was generated by getTargetFor
     * @param implementation the internal object which was being called by the target's invocation handler.
     */
    void revokeTarget(Facet proxy, Facet implementation);

    /**
     * Provides a suitably wrapped target for invoking methods on a Facet of a Meem.
     * This Facet target will be provided to the callback object.
     * 
     * @param meem
     * @param facetIdentifier
     * @param specification
     * @param callback
     */
    <T extends Facet>
    void getTarget(Meem meem, String facetIdentifier, Class<T> specification, AsyncCallback<T> callback);

    /**
     * Returns a suitably wrapped target for invoking methods on a Facet of a Meem.
     * 
     * @param meem
     * @param facetIdentifier
     * @param specification
     * @return A Facet target on which invocations can be made
     */
	<T extends Facet> T getTarget(Meem meem, String facetIdentifier, Class<T> specification);

	/**
     * Returns a suitably wrapped target for invoking methods on a Facet of a Meem.
	 * 
	 * @param facet
	 * @param specification
	 * @return returns a Facet target on which invocations can be made
	 */
	<T extends Facet> T getTarget(T facet, Class<T> specification);

    /**
     * Shutdown the MeemServer.
     * 
     * @return true if the shutdown was successful, false otherwise.
     */
    boolean shutdown();

    /**
     * Service provider interface.
     */
    public static class spi 
	{ 
    	/** 
    	 * return a ServerGateway created for the subject the thread is currently executing in.
    	 */ 
    	public static ServerGateway create()
    	{
            if (Subject.getSubject(java.security.AccessController.getContext()) == null)
            {
                throw new IllegalArgumentException("cannot create server gateway with no Subject available.");
            }
            
    		return (ServerGateway)MajiSPI.provider().create(ServerGateway.class);
    	}

    	/** 
    	 * return a ServerGateway created for the passed in subject 
    	 */ 
    	public static ServerGateway create(
    	    Subject subject)
    	{
            if (subject == null)
            {
                throw new IllegalArgumentException("cannot create server gateway with null Subject.");
            }
            
    	    return (ServerGateway)MajiSPI.provider().create(ServerGateway.class, new Object[] { subject });
    	}
    }
}
