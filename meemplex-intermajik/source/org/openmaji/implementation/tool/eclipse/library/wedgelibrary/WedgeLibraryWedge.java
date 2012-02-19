/*
 * @(#)WedgeLibraryWedge.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.tool.eclipse.library.wedgelibrary;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


import org.openmaji.implementation.tool.eclipse.Common;
import org.openmaji.implementation.tool.eclipse.client.security.SecurityManager;
import org.openmaji.implementation.tool.eclipse.library.classlibrary.*;
import org.openmaji.meem.Meem;
import org.openmaji.meem.MeemPath;
import org.openmaji.meem.Wedge;
import org.openmaji.meem.filter.Filter;
import org.openmaji.meem.space.Space;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClient;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClientAdapter;
import org.openmaji.meem.wedge.reference.Reference;
import org.openmaji.system.meem.core.MeemCore;
import org.openmaji.system.meem.wedge.reference.ContentProvider;
import org.openmaji.system.meem.wedge.reference.MeemClientConduit;
import org.openmaji.system.space.hyperspace.StandardHyperSpaceCategory;
import org.openmaji.system.spi.MajiSystemProvider;
import org.openmaji.system.spi.SpecificationType;

/**
 * <p>
 * ...
 * </p>
 * @author  mg
 * @version 1.0
 */
public class WedgeLibraryWedge implements WedgeLibrary, Wedge {

	static private final Logger logger = Logger.getAnonymousLogger();

	private static final String IDENTITY_MANAGER_WEDGE = "org.openmaji.implementation.server.manager.identity.IdentityManagerWedge";
	
	/**
	 * The conduit through which we are alerted to life cycle changes
	 */
	public LifeCycleClient lifeCycleClientConduit = new LifeCycleClientAdapter(this);

	public WedgeLibraryClient wedgeLibraryClient;
    public final ContentProvider wedgeLibraryClientProvider = new ContentProvider() {
        public void sendContent(Object target, Filter filter) {

            WedgeLibraryClient wedgeLibraryClient = (WedgeLibraryClient) target;
            Iterator iter = wedgeMap.values().iterator();
			while (iter.hasNext()) {
				wedgeLibraryClient.wedgeAdded((ClassDescriptor) iter.next());
			}
        }
    };




	public MeemClientConduit	meemClientConduit;
	
	private Map<String, ClassDescriptor> wedgeMap = Collections.synchronizedMap(new HashMap<String, ClassDescriptor>());
	private boolean connected = false;

	private Reference reference = Reference.spi.create(
			"classLibraryClient",
			SecurityManager.getInstance().getGateway().getTargetFor(new ClassLibraryClientImpl(), ClassLibraryClient.class),
			true,
			new InterfaceListFilter("org.openmaji.meem.Wedge")
		);

	private Meem classLibraryMeem = null;

	// persisted list of seen wedges
	public Vector seenWedges;

	private Vector<String> systemWedges = new Vector<String>();

	public MeemCore meemCore;
	
	public Meem wedgeLibraryMeemFacet;

	public Meem meemConduit;

	public WedgeLibraryWedge() {
		seenWedges = new Vector();
	}
	
	/**
	 * @see org.openmaji.implementation.tool.eclipse.library.wedgelibrary.WedgeLibrary#reset()
	 */
	public void reset() {
		seenWedges.clear();
		
		Reference clientMeem = Reference.spi.create("wedgeLibraryMeemFacet", classLibraryMeem, false, null);

		meemConduit.addOutboundReference(clientMeem, false);
		
		wedgeLibraryMeemFacet.removeOutboundReference(reference);
		
		wedgeLibraryMeemFacet.addOutboundReference(reference, false);
		
		meemConduit.removeOutboundReference(clientMeem);
	}

	private void addWedge(ClassDescriptor classDescriptor) {
		String className = classDescriptor.getClassName();

		// if this is a system wedge, ignore it
		if (systemWedges.contains(className)) {
			return;
		}
		
      if (className.startsWith("org.openmajik.implementation.server.nursery")) {
        if ( Common.TRACE_ENABLED && Common.TRACE_WEDGE_LIBRARY )
  			logger.log(Level.FINE, "Ignoring nursery wedge");
			return;
		}

		wedgeMap.put(className, classDescriptor);

        if (!seenWedges.contains(className)) {
          if ( Common.TRACE_ENABLED && Common.TRACE_WEDGE_LIBRARY )
        	  logger.log(Level.FINE, "Creating new wedgekit meem for wedge: " + className);
            throw new RuntimeException("this is not meant to be used!!!");
			// create new entry in unfiled
            //TODO[ben] Remove string literal, confirm this is the correct hyperspace path
//			MeemPath meemPath = MeemPath.spi.create(Space.HYPERSPACE, StandardHyperSpaceCategory.MAJI_CONFIGURATION_TOOLKIT_WEDGE_UNFILED + className);
//
//			Meem meem = MeemFactory.spi.get(SecurityManager.getInstance().getGateway()).create(createDefinition(className), meemPath, LifeCycleState.LOADED);
//
//			LifeCycleLimit lifeCycleLimit = (LifeCycleLimit) 
//				MeemUtility.spi.get(SecurityManager.getInstance().getGateway()).getTarget(meem, "lifeCycleLimit", LifeCycleLimit.class);
//			lifeCycleLimit.limitLifeCycleState(LifeCycleState.LOADED);
//
//			seenWedges.add(className);
//			
//			meemClientConduit.provideReference(meem, "managedPersistenceHandler", ManagedPersistenceHandler.class, new ReferenceCallbackImpl());
		}

		wedgeLibraryClient.wedgeAdded(classDescriptor);
	}
/*
	private MeemDefinition createDefinition(String className) {

		MeemAttribute meemAttribute = new MeemAttribute();
		meemAttribute.setScope(Scope.LOCAL);
		meemAttribute.setIdentifier(className);
		meemAttribute.setVersion(1);

		MeemDefinition meemDefinition = new MeemDefinition(meemAttribute);

		try {
			Class wedgeClass = Class.forName(className);

			WedgeDefinition wedgeDefinition = WedgeIntrospector.getWedgeDefinition(wedgeClass);

			meemDefinition.addWedgeDefinition(wedgeDefinition);

		} catch (WedgeIntrospectorException e) {
			LogTools.error(logger, "Exception while introspecting class: " + className, e);
		} catch (ClassNotFoundException e) {
			LogTools.error(logger, "Class not found: " + className, e);
		}

		return meemDefinition;
	}
*/
/*
	private class ReferenceCallbackImpl
		implements MeemClientCallback
	{
		public void referenceProvided(Reference reference)
		{
			if (reference == null)
			{
				LogTools.error(logger, "no managedPersistenceHandler found can't persist!");
				return;
			}
			
			((ManagedPersistenceHandler)reference.getTarget()).persist();
			
			// persist self.
			
			((ManagedPersistenceHandler)meemCore.getTarget("managedPersistenceHandler")).persist();
		}
	}
*/
	private final class ClassLibraryClientImpl implements ClassLibraryClient {
		/**
		 */
		public void classAdded(ClassDescriptor classDescriptor) {
			addWedge(classDescriptor);
		}

		/**
		 */
		public void jarAdded(String jarFileName) {
			// don't care
		}
	}

    private static MajiSystemProvider majiSystemProvider = null;
	
		public void commence() {
			if (!connected) {
        if (majiSystemProvider == null) {
          majiSystemProvider = MajiSystemProvider.systemProvider();
        } 
               
				//		find system wedges
        
        Collection<Class> systemWedgeSpecifications = new ArrayList<Class>();
        systemWedgeSpecifications.addAll(majiSystemProvider.getSpecifications(SpecificationType.SYSTEM_WEDGE));
        systemWedgeSpecifications.addAll(majiSystemProvider.getSpecifications(SpecificationType.SYSTEM_HOOK));
        
        Iterator<Class> systemWedgeIter = systemWedgeSpecifications.iterator();
      
        while (systemWedgeIter.hasNext()) {
          Class specification = systemWedgeIter.next();
        
          String implementationClassName = majiSystemProvider.getSpecificationEntry(specification).getImplementation().getName();

          systemWedges.add(implementationClassName);
          
			if ( Common.TRACE_ENABLED && Common.TRACE_WEDGE_LIBRARY )
				logger.log(Level.FINE, "SYSTEM: " + implementationClassName);
				}

				// add security manager as it isn't in the list

				systemWedges.add(IDENTITY_MANAGER_WEDGE);

				// connect to the ClassLibrary
				if (classLibraryMeem == null) {
					classLibraryMeem =
					    SecurityManager.getInstance().getGateway().getMeem(
                    MeemPath.spi.create(Space.HYPERSPACE, StandardHyperSpaceCategory.MAJI_SYSTEM_CLASS_LIBRARY));
				}
			
			
				Reference clientMeem = Reference.spi.create("wedgeLibraryMeemFacet", classLibraryMeem, false, null);

				meemConduit.addOutboundReference(clientMeem, false);
			
				wedgeLibraryMeemFacet.addOutboundReference(reference, false);
			
				meemConduit.removeOutboundReference(clientMeem);

				connected = true;
			}

		}

}
