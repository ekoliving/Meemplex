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

import org.openmaji.implementation.server.manager.lifecycle.transitory.TransientLifeCycleManagerMeem;
import org.openmaji.meem.Meem;
import org.openmaji.meem.MeemPath;
import org.openmaji.meem.definition.MeemDefinition;
import org.openmaji.meem.filter.Filter;
import org.openmaji.meem.space.Space;
import org.openmaji.meem.wedge.lifecycle.LifeCycleLimit;
import org.openmaji.meem.wedge.lifecycle.LifeCycleState;
import org.openmaji.server.utility.FacetCallbackTask;
import org.openmaji.server.utility.PigeonHole;
import org.openmaji.server.utility.TimeoutException;
import org.openmaji.system.gateway.AsyncCallback;
import org.openmaji.system.manager.lifecycle.CreateMeemFilter;
import org.openmaji.system.manager.lifecycle.EssentialLifeCycleManager;
import org.openmaji.system.manager.lifecycle.LifeCycleManager;
import org.openmaji.system.manager.lifecycle.LifeCycleManagerClient;
import org.openmaji.system.meem.definition.DefinitionFactory;
import org.openmaji.system.meem.wedge.lifecycle.LifeCycleManagementClient;
import org.openmaji.system.meem.wedge.reference.ContentException;
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

	private static final String FACET_LCM = "lifeCycleManager"; // LifeCycleManager facet
	
	// outbound facet of lifeCycleManagerMeem
	private static final String FACET_LC_MGR_CLIENT = "lifeCycleManagerClient";
	
	private static final String FACET_LC_MGMT_CLIENT = "lifeCycleManagementClient";
	
	
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
	 * 	The full path to the Meem to be created.  Do not create a new one if a Meem already exists at this path.
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

	/**
	 * 
	 * @param meemDefinition
	 * @param resolvedMeem
	 * 	Meem to use as lifecycle manager
	 * @param initialState
	 * @return
	 * @throws RuntimeException
	 */
	public static Meem doCreateMeem(MeemDefinition meemDefinition, Meem resolvedMeem, LifeCycleState initialState) throws RuntimeException {

		// check to see if the resolved meem is a LifeCycleManager
		LifeCycleManager lifeCycleManager = ReferenceHelper.getTarget(resolvedMeem, FACET_LCM, LifeCycleManager.class);

		if (lifeCycleManager == null) {
			// get the LCM from the LifeCycleManagement facet
			PigeonHole<LifeCycleManager> pigeonHole = new PigeonHole<LifeCycleManager>();
			new LifeCycleManagementClientTask(resolvedMeem, FACET_LC_MGMT_CLIENT, null, pigeonHole);
			try {
				lifeCycleManager = pigeonHole.get(timeout);
			}
			catch (ContentException | TimeoutException ex) {
				logger.log(Level.INFO, "Timeout waiting for LifeCycleManager", ex);
				lifeCycleManager = null;
			}

			if (lifeCycleManager == null) {
				throw new RuntimeException("Unable to obtain LifeCycleManager for Meem: " + resolvedMeem);
			}

			resolvedMeem = (Meem) lifeCycleManager;
		}

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

	/**
	 * Create a transient meem.
	 * 
	 * @param meemDefinition
	 * @param lifeCycleState
	 * @param callback
	 */
	public static void createTransientMeem(MeemDefinition meemDefinition, LifeCycleState lifeCycleState, AsyncCallback<Meem> callback) {
		if (transientLifeCycleManagerMeem == null) {
			transientLifeCycleManagerMeem =
				EssentialMeemHelper.getEssentialMeem(EssentialLifeCycleManager.spi.getIdentifier());
		}

		createMeem(meemDefinition, transientLifeCycleManagerMeem, lifeCycleState, callback);
	}
	
	public static Meem createMeem(MeemDefinition meemDefinition, Meem lifeCycleManagerMeem) {
		return createMeem(meemDefinition, lifeCycleManagerMeem, LifeCycleState.READY);
	}

	/**
	 * Create a meem at the default lifecycle state of READY.
	 * 
	 * @param meemDefinition
	 * @param lifeCycleManagerMeem
	 * @param callback
	 */
	public static void createMeem(MeemDefinition meemDefinition, Meem lifeCycleManagerMeem, AsyncCallback<Meem> callback) {
		createMeem(meemDefinition, lifeCycleManagerMeem, LifeCycleState.READY, callback);
	}
	
	/**
	 * Create a Meem by sending a meemDefinition to a LifeCycleManager.
	 *  
	 * @param meemDefinition
	 * @param lifeCycleManagerMeem
	 * @param initialState
	 * @return
	 */
	public static Meem createMeem(MeemDefinition meemDefinition, final Meem lifeCycleManagerMeem, final LifeCycleState initialState) {

		final PigeonHole<Meem> pigeonHole = new PigeonHole<Meem>();
		
		Filter filter = new CreateMeemFilter(meemDefinition, initialState);
		new MeemCreationTask(lifeCycleManagerMeem, FACET_LC_MGR_CLIENT, filter, pigeonHole);

		try {
			return pigeonHole.get(timeout);
		}
		catch (ContentException ex) {
			logger.log(Level.INFO, "ContentException waiting for CreateMeemTask", ex);
			return null;
		}
		catch (TimeoutException ex) {
			logger.log(Level.INFO, "Timeout waiting for CreateMeemTask", ex);
			return null;
		}
	}

	/**
	 * Create a meem.  Return the meem asynchronously via a callback
	 * 
	 * @param meemDefinition
	 * @param lifeCycleManagerMeem
	 * @param initialState
	 * @param callback
	 */
	public static void createMeem(MeemDefinition meemDefinition, final Meem lifeCycleManagerMeem, final LifeCycleState initialState, AsyncCallback<Meem> callback) {
		Filter filter = new CreateMeemFilter(meemDefinition, initialState);
		new MeemCreationTask(lifeCycleManagerMeem, FACET_LC_MGR_CLIENT, filter, callback);
	}
	
	/**
	 * 
	 * @param wedgeClasses
	 * @param lifeCycleState
	 * @param lifeCycleStateLimit
	 * @param path
	 * @return
	 */
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
	
	/**
	 * Assemble a meem from wedges
	 * 
	 * @param wedgeClasses
	 * @param lifeCycleState
	 * @param lifeCycleStateLimit
	 * @param path
	 * @param callback
	 */
	public static void assembleMeem(Class<?>[] wedgeClasses, LifeCycleState lifeCycleState, final LifeCycleState lifeCycleStateLimit, String path, final AsyncCallback<Meem> callback) {

		DefinitionFactory definitionFactory = DefinitionFactory.spi.create();

		Iterator<?> wedgeIterator = Arrays.asList(wedgeClasses).iterator();

		MeemDefinition meemDefinition = definitionFactory.createMeemDefinition(wedgeIterator);

		Meem meem = null;

		if (path == null) {
			createTransientMeem(meemDefinition, lifeCycleState, new AsyncCallback<Meem>() {
				public void result(Meem newMeem) {
					setLifeCycleStateLimit(newMeem, lifeCycleStateLimit, callback);
				}
				public void exception(Exception e) {
					callback.exception(e);
				}
			});
		}
		else {
			// TODO make this call asynchronous
			
//			MeemPath meemPath = MeemPath.spi.create(Space.HYPERSPACE, path);
//			meem = createMeem(meemDefinition, meemPath, lifeCycleState, new AsyncCallback<Meem>() {
//				public void result(Meem result) {
//					// TODO Auto-generated method stub
//					
//				}
//				public void exception(Exception e) {
//					// TODO Auto-generated method stub
//					
//				}
//			});
			
			MeemPath meemPath = MeemPath.spi.create(Space.HYPERSPACE, path);
			meem = createMeem(meemDefinition, meemPath, lifeCycleState);
			setLifeCycleStateLimit(meem, lifeCycleStateLimit, callback);
		}
	}
	
	/**
	 * Get the lifecycle manager for a Meem.
	 * 
	 * @param resolvedMeem
	 * 		Is either a LCM, or we get the LCM managing this Meem.
	 * 
	 * @param callback
	 * @throws RuntimeException
	 */
	public static void getLifeCycleManagerFor(final Meem meem, final AsyncCallback<LifeCycleManager> callback) throws RuntimeException {

		// check to see if the resolved meem is a LifeCycleManager
		ReferenceHelper.getTarget(meem, FACET_LCM, LifeCycleManager.class, new AsyncCallback<LifeCycleManager>() {
			public void result(LifeCycleManager result) {
				if (result != null) {
					callback.result(result);
				}
				else {
					new LifeCycleManagementClientTask(meem, FACET_LC_MGMT_CLIENT, null, callback);
				}
			}
			public void exception(Exception e) {
				new LifeCycleManagementClientTask(meem, FACET_LC_MGMT_CLIENT, null, callback);
			}
		});
	}

	/**
	 * 
	 * @param meem
	 * @param lifeCycleStateLimit
	 * @param callback
	 */
	private static void setLifeCycleStateLimit(final Meem meem, final LifeCycleState lifeCycleStateLimit, final AsyncCallback<Meem> callback) {
//		LifeCycleLimit lifeCycleLimit = ReferenceHelper.getTarget(meem, "lifeCycleLimit", LifeCycleLimit.class);
//		lifeCycleLimit.limitLifeCycleState(lifeCycleStateLimit);
//		callback.result(meem);

		ReferenceHelper.getTarget(meem, "lifeCycleLimit", LifeCycleLimit.class, new AsyncCallback<LifeCycleLimit>() {
			public void result(LifeCycleLimit lifeCycleLimit) {
				lifeCycleLimit.limitLifeCycleState(lifeCycleStateLimit);
				callback.result(meem);
			}
			public void exception(Exception e) {
				callback.exception(e);
			}
		});
	}

	private static final long timeout = Long.parseLong(System.getProperty(PigeonHole.PROPERTY_TIMEOUT, "60000"));

	/** Logger for the class */
	private static final Logger logger = Logger.getAnonymousLogger();

	/**
	 * 
	 */
	private static class LifeCycleManagementClientTask extends FacetCallbackTask<LifeCycleManagementClient, LifeCycleManager> {
		public LifeCycleManagementClientTask(Meem meem, String facetName, Filter filter, AsyncCallback<LifeCycleManager> callback) {
			super(meem, facetName, filter, callback);
		}
		
		public LifeCycleManagementClientTask(Meem meem, String facetName, Filter filter, PigeonHole<LifeCycleManager> pigeonHole) {
			super(meem, facetName, filter, pigeonHole);
		}

		@Override
		protected LifeCycleManagementClient getFacetListener() {
			return new LifeCycleManagerClientListener();
		}
		
		private class LifeCycleManagerClientListener extends FacetListener implements LifeCycleManagementClient {
			public void parentLifeCycleManagerChanged(Meem meem, LifeCycleManager lifeCycleManager) {
				setResult(lifeCycleManager);
			}
		}
	}	

	/**
	 * 
	 */
	public static class MeemCreationTask extends FacetCallbackTask<LifeCycleManagerClient, Meem> {
		public MeemCreationTask(Meem meem, String facetName, Filter filter, AsyncCallback<Meem> callback) {
			super(meem, facetName, filter, callback);
		}
		
		public MeemCreationTask(Meem meem, String facetName, Filter filter, PigeonHole<Meem> pigeonHole) {
			super(meem, facetName, filter, pigeonHole);
		}
		
		@Override
		protected LifeCycleManagerClient getFacetListener() {
			return new LifeCycleManagerClientImpl();
		}
		
		class LifeCycleManagerClientImpl extends FacetListener implements LifeCycleManagerClient {
			public void meemCreated(Meem meem, String identifier) {
				///logger.info("=== Meem created: " + meem + " " + identifier);
				setResult(meem);
			}
			public void meemDestroyed(Meem meem) {
			}
			public void meemTransferred(Meem meem, LifeCycleManager targetLifeCycleManager) {
			}
		}
	}
	
}
