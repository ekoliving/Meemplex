/*
 * @(#)MeemUtility.java
 *
 * Copyright 2004 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */
package org.openmaji.system.utility;

import org.openmaji.meem.Facet;
import org.openmaji.meem.Meem;
import org.openmaji.meem.definition.Direction;
import org.openmaji.meem.definition.MeemDefinition;
import org.openmaji.spi.MajiSPI;


/**
 * Utility class for investigating meems.
 * <p>
 * This class is designed to allow people outside the MeemServer to do some basic examination of a 
 * meem's properties. Because it allows for synchronous handling of the methods its "under the covers"
 * implementation actually blocks.
 * </p>
 * <p>
 * For this reason, any use of these methods inside a meem must be avoided. To assist with this you will find 
 * examples of the "in-meem" equivalent to the various methods.
 * </p>
 */
public interface MeemUtility
{
	/**
	 * Return true if a meem has an inbound facet implementing the passed-in specification.
     * <p>
     * Within the meem world there are two ways of doing this, the first is to use a one-off
     * target, and the second is to use an inbound facet of your own as the the target. In the
     * case of the one-off target the code might look something like this:
	 * <pre>
     *     
     *     public static class FacetClientImpl
     *         implements FacetClient, ContentClient
     *     {
     *         private boolean found = false;
     *                                                                           
     *         public void hasA(
     *             String facetIdentifer,
     *             Class specification,
     *             Direction direction)
     *         {
     *             found = true;
     *         }     
     *                                                                                     
     *         public void contentSent()
     *         {
     *             if (found)
     *             {
     *                 System.out.println("facet found");
     *             }
     *             else
     *             {   
     *                 System.out.println("facet not found");
     *             }
     *         }
     *    }
     *
     *    ....
     * 
     *    FacetClient client = new FacetClientImpl();
     *    FacetFilter filter = new FacetFilter(specification, Direction.INBOUND);
     * 
     *    Reference ref = Reference.spi.create("facetClientFacet", meemContext.getTargetFor(client, FacetClient.class), true, filter);
     *
     *    meem.addOutboundReference(ref, true);
     * </pre>
     * in the case of your own inbound facets the code might look like
     * <pre>
     *    FacetFilter filter = new FacetFilter(specification, Direction.INBOUND);
     * 
     *    Reference ref = Reference.spi.create("facetClientFacet", meemContext.getTarget("facetClient"), true, filter);
     *
     *    meem.addOutboundReference(ref, true);
     * </pre>
     * where one of the wedges in the meem implements the FacetClient interface.
     * 
	 * @param meem the meem of interest.
	 * @param specification the interface we are looking for.
	 * @return true if the meem implements the specification, false otherwise.
	 */
	public boolean isA(Meem meem, Class specification);

   /**
	* Return true if the meem has a facet that matches the passed-in details. If one of the detail
	* parameters is null the method assumes that any match for that parameter will do.
	* <p>
    * There are two ways of doing this from within a meem. The example below shows
    * how it is done if you are creating a one-off target. You could also use an inbound facet as the
    * target instead. 
    * <pre>
    *     public static class FacetClientImpl
    *         implements FacetClient, ContentClient
    *     {
    *         private boolean found = false;
    *                                                                           
    *         public void hasA(
    *             String facetIdentifer,
    *             Class specification,
    *             Direction direction)
    *         {
    *             found = true;
    *         }     
    *                                                                                     
    *         public void contentSent()
    *         {
    *             if (found)
    *             {
    *                 System.out.println("facet found");
    *             }
    *             else
    *             {   
    *                 System.out.println("facet not found");
    *             }
    *         }
    *    }
    *
    *    ....
    * 
    *    FacetClient client = new FacetClientImpl();
    *    FacetFilter filter = new FacetFilter(facetIdentifier, specification, direction);
    * 
    *    Reference ref = Reference.spi.create("facetClientFacet", meemContext.getTargetFor(client, FacetClient.class), true, filter);
    *
    *    meem.addOutboundReference(ref, true);
    * </pre>
    * 
	* @param meem the meem of interest.
	* @param facetIdentifier the name of the facet we are looking for.
	* @param specification the interface we are looking for.
	* @param direction the direction of the facet - inbound or outbound.
	* @return true if the meem has a facet fitting the passed in details, false otherwise.
	*/
	public boolean hasA(Meem meem, String facetIdentifier, Class specification, Direction direction);
	
	/**
	 * Return the inbound target, if any, matching the passed-in facetIdentifier and specification.
	 * <p>
	 * In a meem you should do this via the meemClientConduit which takes an object that is called
	 * when the target has been obtained from the other meem.
	 * <p>
	 * For example if you were trying to obtain the category facet from a meem, if you are examing
	 * the meem from outside the meem server the code would read:
	 * <pre>
	 *     Category  cat = MeemUtility.getTarget(meem, "category", Category.class);
	 * </pre>
	 * In the case of inside the meem server, where you should avoid blocking a thread at all times
	 * the process is broken up. Firstly you define the call-back and secondly you pass the call-back
	 * to the meemClientConduit. In the category case the call-back might look like:
	 * <pre>
	 * 	   private class MeemClientCallbackImpl
	 *	       implements MeemClientCallback
	 *     {
	 *         public void referenceProvided(Reference reference)
	 *         {
     *  			if (reference == null)
     *  			{
     *  				LogTools.error(logger, "no category found!");
     *  				return;
     *   			}
	 *
     *  			Category cat = (Category)reference.getTarget();
     *              
     *              // do stuff...
     *         }
	 *     }
	 * </pre>
	 * and the above call-back would be passed to the meemClientConduit as follows:
     * <pre>
     *     meemClientConduit.provideReference(Meem meem, "category", Category.class, new MeemClientCallbackImpl());
     * </pre>
	 * @param meem the meem we want to the target for.
	 * @param facetIdentifier the facet name.
	 * @param specification  the interface matching the facet type.
	 * @return the target, or null if there is no target matching.
	 */
	public Facet getTarget(Meem meem, String facetIdentifier, Class specification);
	
	/**
	 * Return the MeemDefinition for the passed in Meem.
	 * 
	 * @param meem The Meem to obtain the MeemDefinition for.
	 * @return The MeemDefinition for the passed in Meem.
	 */
	public MeemDefinition getMeemDefinition(Meem meem);
	
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
		public static synchronized MeemUtility get() 
		{
		    return  (MeemUtility) MajiSPI.provider().create(MeemUtility.class);
		}
	}
}
