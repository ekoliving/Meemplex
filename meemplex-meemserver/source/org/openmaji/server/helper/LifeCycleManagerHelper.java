/*
 * @(#)LifeCycleManagerHelper.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - Move MeemUtility into org.openmaji.utility.MeemUtility ?
 * - Determine how to make a "data driven" synchronous Helper method.
 * - Provide a "batch Meem creation" method to reduce the overhead of
 *     creating a new Reference and Filter for each Meem creation.
 *     Best to avoid "contentRequired" and perform local filtering.
 */

package org.openmaji.server.helper;

import java.util.*;


import org.openmaji.implementation.server.manager.gateway.GatewayManagerWedge;
import org.openmaji.implementation.server.manager.lifecycle.transitory.TransientLifeCycleManagerMeem;
import org.openmaji.meem.Facet;
import org.openmaji.meem.Meem;
import org.openmaji.meem.MeemPath;
import org.openmaji.meem.definition.MeemDefinition;
import org.openmaji.meem.filter.Filter;
import org.openmaji.meem.space.Space;
import org.openmaji.meem.wedge.lifecycle.LifeCycleLimit;
import org.openmaji.meem.wedge.lifecycle.LifeCycleState;
import org.openmaji.meem.wedge.reference.Reference;
import org.openmaji.server.utility.PigeonHole;
import org.openmaji.server.utility.TimeoutException;
import org.openmaji.system.manager.lifecycle.CreateMeemFilter;
import org.openmaji.system.manager.lifecycle.EssentialLifeCycleManager;
import org.openmaji.system.manager.lifecycle.LifeCycleManager;
import org.openmaji.system.manager.lifecycle.LifeCycleManagerClient;
import org.openmaji.system.meem.definition.DefinitionFactory;
import org.openmaji.system.meem.wedge.lifecycle.LifeCycleManagementClient;
import org.openmaji.system.meem.wedge.reference.ContentClient;
import org.openmaji.system.space.Category;


import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * <p>
 * LifeCycleManagerHelper ...
 * </p>
 * <p>
 * Note: Implementation thread safe = Not considered yet
 * </p>
 * @author  Andy Gelme
 * @version 1.0
 */

public class LifeCycleManagerHelper {

	/**
	 * Reference to the Transient LifeCycleManager (local per-JVM instance)
	 */

	private static Meem transientLifeCycleManagerMeem = null;

	public static synchronized void setTransientLCM(Meem lifeCycleManagerMeem) {

		if (transientLifeCycleManagerMeem == null) {
			transientLifeCycleManagerMeem = lifeCycleManagerMeem;
		}
	}

    public static Meem getTransientLCM()
    {
        if (transientLifeCycleManagerMeem == null) {
            return EssentialMeemHelper.getEssentialMeem(TransientLifeCycleManagerMeem.spi.getIdentifier());
        }
        
        return transientLifeCycleManagerMeem;
    }
    
	public static Meem createMeem(MeemDefinition meemDefinition, MeemPath meemPath) throws RuntimeException {
		return createMeem(meemDefinition, meemPath, LifeCycleState.READY);
	}

	/**
	 * 
	 * @param meemDefinition
	 * @param meemPath
	 * 	The full path to the Meem to be created.  Do not reate a new one if a Meem already exists at this path.
	 *  
	 * @param initialState
	 * @return
	 * @throws RuntimeException
	 */
	public static Meem createMeemAtPath(MeemDefinition meemDefinition, MeemPath meemPath, LifeCycleState initialState) throws RuntimeException {
		Meem resolvedMeem = MeemPathResolverHelper.getInstance().resolveMeemPath(meemPath);
		if (resolvedMeem != null) {		// Meem at this path already created.  Assume it's the one we want to create.  Return this meem.
			//logger.log(10, "Meem already exists at " + meemPath + ". Not creating a new one, but returning that meem.");
			return resolvedMeem;
		}
		else {
			return createMeem(meemDefinition, meemPath, initialState);
		}
	}

	/**
	 * MeemPath points to a Category or a LCM. If meempath is of the form hyperspace:/cat1/cat2/meem then the 
	 * LCM monitoring cat2 will be used to create a new meem and add it to cat2 with an entryname of 'meem'. If the
	 * MeemPath is of the form meemstore:/uid, the meem with that path is checked to see if it is a LCM. If it is, then
	 * it is used to create a new meem and add it to its startup category. If it isn't an LCM, it is checked to see if
	 * it had a LifeCycleManagerMonitored facet. If it does, then the LCM that is monitoring the meem is used to create
	 * the new meem. If the MeemPath is a hyperspace path, then it must end in the new entry name.
	 */
	public static Meem createMeem(MeemDefinition meemDefinition, MeemPath meemPath, LifeCycleState initialState) throws RuntimeException {

		Meem resolvedMeem = MeemPathResolverHelper.getInstance().resolveMeemPath(meemPath);

		// if the meempath cannot be resolved, handle it depending on the type of space the request is for
		// for meemspace or transient throw an error
		// for hyperspace try and see if the next to last path entry can be resolved

		if (resolvedMeem != null) {
			// assume use resolveMeem as LCM or resolved Meeem's LCM.
			return doCreateMeem(meemDefinition, resolvedMeem, initialState);
		}
		else if (!meemPath.getSpace().equals(Space.HYPERSPACE)) {
			// not found
			throw new RuntimeException("MeemPath could not be resolved: " + meemPath);
		}

		Category category = null;
		String newCategoryEntryName = null;

		List<String> paths = new LinkedList<String>();
		String location = meemPath.getLocation();
		StringTokenizer tok = new StringTokenizer(location, "/");
		while (tok.hasMoreTokens()) {
			paths.add(tok.nextToken());
		}

		if (paths.size() == 0) {
			// not found
			throw new RuntimeException("HyperSpace MeemPath does not end in category entry name");
		}

		StringBuffer pathBuffer = new StringBuffer();
		if (paths.size() == 1) {
			pathBuffer.append("/");
		}
		else {
			for (int i = 0; i < paths.size() - 1; i++) {
				pathBuffer.append("/");
				pathBuffer.append((String) paths.get(i));
			}
		}

		meemPath = MeemPath.spi.create(Space.HYPERSPACE, pathBuffer.toString());
		resolvedMeem = MeemPathResolverHelper.getInstance().resolveMeemPath(meemPath);

		if (resolvedMeem == null) {
			// not found
			throw new RuntimeException("MeemPath could not be resolved: " + meemPath);
		}

		//check to see if its a category
		category = (Category) ReferenceHelper.getTarget(resolvedMeem, "category", Category.class);

		// if it is and we were given a HS path, then we need to rename the new entry in this category
		// as the LCM will make the category entry with the meems UID as the name by default

		newCategoryEntryName = (String) paths.get(paths.size() - 1);

		if (category != null) {
			Meem createdMeem = doCreateMeem(meemDefinition, resolvedMeem, initialState);
			category.addEntry(newCategoryEntryName, createdMeem);
			return createdMeem;
		}
		
		throw new RuntimeException("cannot create category entry");
	}

	public static Meem doCreateMeem(MeemDefinition meemDefinition, Meem resolvedMeem, LifeCycleState initialState) throws RuntimeException {

		// check to see if the resolved meem is a LifeCycleManager
		LifeCycleManager lifeCycleManager = ReferenceHelper.getTarget(resolvedMeem, "lifeCycleManager", LifeCycleManager.class);

		if (lifeCycleManager == null) {
			// get the LCM from the LifeCycleManagement facet
			PigeonHole<LifeCycleManager> pigeonHole = new PigeonHole<LifeCycleManager>();
			LifeCycleManagementClientImpl client = new LifeCycleManagementClientImpl(pigeonHole);
			Facet proxy = GatewayManagerWedge.getTargetFor(client, LifeCycleManagementClient.class); 

			Reference lcmManagementReference = Reference.spi.create("lifeCycleManagementClient", proxy, true);

			resolvedMeem.addOutboundReference(lcmManagementReference, true);

			try {
				lifeCycleManager = pigeonHole.get(timeout);
			}
			catch (TimeoutException ex) {
				logger.log(Level.INFO, "Timeout waiting for LifecycleManager", ex);
				lifeCycleManager = null;
			}
			finally {
				GatewayManagerWedge.revokeTarget(proxy, client);
			}

			if (lifeCycleManager == null) {
				throw new RuntimeException("Unable to obtain LifeCycleManager for Meem: " + resolvedMeem);
			}

			resolvedMeem = (Meem) lifeCycleManager;
//			resolvedMeem = client.getMeem();
		}

//		return createMeem(meemDefinition, resolvedMeem, lifeCycleManager, initialState);
		return createMeem(meemDefinition, resolvedMeem, initialState);
	}

	public static Meem createTransientMeem(MeemDefinition meemDefinition) {

		if (transientLifeCycleManagerMeem == null) {
			transientLifeCycleManagerMeem =
				EssentialMeemHelper.getEssentialMeem(EssentialLifeCycleManager.spi.getIdentifier());
		}

		return (createMeem(meemDefinition, transientLifeCycleManagerMeem));
	}

	public static Meem createTransientMeem(MeemDefinition meemDefinition, LifeCycleState lifeCycleState) {

		if (transientLifeCycleManagerMeem == null) {
			transientLifeCycleManagerMeem =
				EssentialMeemHelper.getEssentialMeem(EssentialLifeCycleManager.spi.getIdentifier());
		}

		return (createMeem(meemDefinition, transientLifeCycleManagerMeem, lifeCycleState));
	}

	public static Meem createMeem(MeemDefinition meemDefinition, Meem lifeCycleManagerMeem) {
		return createMeem(meemDefinition, lifeCycleManagerMeem, LifeCycleState.READY);
	}

//	public static Meem createMeem(MeemDefinition meemDefinition, final Meem lifeCycleManagerMeem,
//		final LifeCycleState initialState) {
//
//		LifeCycleManager lifeCycleManager = (LifeCycleManager) ReferenceHelper.getTarget(
//			lifeCycleManagerMeem, "lifeCycleManager", LifeCycleManager.class);
//
//		return createMeem(meemDefinition, lifeCycleManagerMeem, lifeCycleManager, initialState);			
//	}

	public static Meem createMeem(MeemDefinition meemDefinition, final Meem lifeCycleManagerMeem, /*final LifeCycleManager lifeCycleManager,*/ final LifeCycleState initialState) {

		PigeonHole<Meem> pigeonHole = new PigeonHole<Meem>();
		LifeCycleManagerClient lifeCycleManagerClient = new CreateMeemTask(pigeonHole);
		Facet proxy = GatewayManagerWedge.getTargetFor(lifeCycleManagerClient, LifeCycleManagerClient.class);
		Filter filter = new CreateMeemFilter(meemDefinition, initialState);
		Reference reference = Reference.spi.create("lifeCycleManagerClient", proxy, true, filter);

		lifeCycleManagerMeem.addOutboundReference(reference, true);

		try {
			return pigeonHole.get(timeout);
		} catch (TimeoutException ex) {
			logger.log(Level.INFO, "Timeout waiting for CreateMeemTask", ex);
			return null;
		}
		finally {
			GatewayManagerWedge.revokeTarget(proxy, lifeCycleManagerClient);
		}
	}

	public static Meem assembleMeem(Class<?>[] wedgeClasses, LifeCycleState lifeCycleState, LifeCycleState lifeCycleStateLimit, String path) {

		DefinitionFactory definitionFactory = DefinitionFactory.spi.create();

		Iterator<?> wedgeIterator = Arrays.asList(wedgeClasses).iterator();

		MeemDefinition meemDefinition = definitionFactory.createMeemDefinition(wedgeIterator);

		Meem meem = null;

		if (path == null) {
			meem = createTransientMeem(meemDefinition, lifeCycleState);
		} else {
			MeemPath meemPath = MeemPath.spi.create(Space.HYPERSPACE, path);
			meem = createMeem(meemDefinition, meemPath, lifeCycleState);
		}

		LifeCycleLimit lifeCycleLimit = (LifeCycleLimit) ReferenceHelper.getTarget(meem, "lifeCycleLimit", LifeCycleLimit.class);

		lifeCycleLimit.limitLifeCycleState(lifeCycleStateLimit);

		return meem;
	}

	private static final long timeout = 60000;

	/** Logger for the class */
	private static final Logger logger = Logger.getAnonymousLogger();

	
	public static class LifeCycleManagerClientImpl implements LifeCycleManagerClient {

		private PigeonHole<Meem> meemHole = new PigeonHole<Meem>();
		private long timeout = 60000;
	
		public Meem getMeem() {
			try {
				return ((Meem) meemHole.get(timeout));
			} catch (TimeoutException ex) {
				logger.log(Level.INFO, "Timeout waiting for LifecycleManager", ex);
				return null;
			}
		}

		public void meemCreated(Meem meem, String identifier) {
			meemHole.put(meem);
		
		}

		public void meemDestroyed(Meem meem) {
		}

		public void meemTransferred(Meem meem, LifeCycleManager targetLifeCycleManager) {
			// Don't care
		}

		/** Logger for the class */
		private static final Logger logger = Logger.getAnonymousLogger();
	}
	
	public static class LifeCycleManagementClientImpl implements LifeCycleManagementClient {

		public LifeCycleManagementClientImpl(PigeonHole<LifeCycleManager> pigeonHole) {
			this.pigeonHole = pigeonHole;
		}
		
		private PigeonHole<LifeCycleManager> pigeonHole;
		private Meem meem = null;

		public Meem getMeem() {
			return meem;
		}

		public void parentLifeCycleManagerChanged(Meem meem, LifeCycleManager lifeCycleManager) {
			if (pigeonHole != null)
			{
				this.meem = meem;
				pigeonHole.put(lifeCycleManager);
				pigeonHole = null;
			}
		}
	}

	public static class CreateMeemTask implements LifeCycleManagerClient, ContentClient {
		public CreateMeemTask(PigeonHole<Meem> pigeonHole) {
			this.pigeonHole = pigeonHole;
		}

		public void meemCreated(Meem meem, String identifier) {
			this.meem = meem;
		}

		public void meemDestroyed(Meem meem) {
		}

		public void meemTransferred(Meem meem, LifeCycleManager lifeCycleManager) {
		}

		public void contentSent() {
			if (pigeonHole != null) {
				pigeonHole.put(meem);
				pigeonHole = null;
			}
		}

		public void contentFailed(String arg0) {
			if (pigeonHole != null) {
				pigeonHole.put(null);
				pigeonHole = null;
			}
		}

		private PigeonHole<Meem> pigeonHole;
		private Meem meem = null;
	}
}
