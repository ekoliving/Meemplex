/*
 * @(#)MeemStoreWedge.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.server.space.meemstore;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openmaji.implementation.server.space.meemstore.content.MeemStoreContentStore;
import org.openmaji.implementation.server.space.meemstore.definition.MeemStoreDefinitionStore;


import org.openmaji.meem.*;
import org.openmaji.meem.definition.*;
import org.openmaji.meem.filter.*;
import org.openmaji.meem.space.Space;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClient;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClientAdapter;
import org.openmaji.system.meem.definition.MeemContent;
import org.openmaji.system.meem.wedge.reference.ContentProvider;
import org.openmaji.system.space.meemstore.MeemContentClient;
import org.openmaji.system.space.meemstore.MeemDefinitionClient;
import org.openmaji.system.space.meemstore.MeemStore;
import org.openmaji.system.space.meemstore.MeemStoreClient;

public class MeemStoreWedge
  implements MeemStore, MeemDefinitionProvider, Wedge, FilterChecker {

	private static final Logger logger = Logger.getAnonymousLogger();

	private static final boolean DEBUG = false;
	
	public static final String DEFINITION_STORE_CLASS = "org.openmaji.space.meemstore.definitionstore.class";
	
	public static final String CONTENT_STORE_CLASS = "org.openmaji.space.meemstore.contentstore.class";
	
	public static final String MEEMSTORE_LOCATION = "org.openmaji.space.meemstore.location";


	/* ------------------------------- outbound facets ------------------------------- */
	
	/**
	 * MeemStoreClient (out-bound Facet)
	 */
	public MeemStoreClient meemStoreClient;
	public final ContentProvider meemStoreClientProvider = new ContentProvider() {
		public void sendContent(Object target, Filter filter) throws IllegalArgumentException {

			MeemStoreClient meemStoreClient = (MeemStoreClient) target;
			Collection<MeemPath> valueSet;
			
			synchronized (meemPaths) {
				valueSet = meemPaths.values();
	
				if (filter == null) {
					// send paths of all stored meems
					for (MeemPath meemPath : valueSet) {
						meemStoreClient.meemStored(meemPath);
					}
				}
				else if (filter instanceof ExactMatchFilter) {
					// determine whether the meem is stored in this MeemStore
					MeemPath meemPath = (MeemPath) ((ExactMatchFilter) filter).getTemplate();
					if (valueSet.contains(meemPath)) {
						meemStoreClient.meemStored(meemPath);
					}
				}
			}
		}
	};

	/**
	 * MeemContentClient (out-bound Facet)
	 */
	public MeemContentClient meemContentClient;
	public final ContentProvider meemContentClientProvider = new ContentProvider() {
		public void sendContent(Object target, Filter filter) throws IllegalArgumentException {

			MeemContentClient meemContentClient = (MeemContentClient) target;
			Collection<MeemPath> valueSet;
			synchronized (meemPaths) {
				valueSet = meemPaths.values();
	
				if (filter == null) {
					logger.info("!!!! No filter for meem content client");
					
					for (MeemPath meemPath : valueSet) {
						meemContentClient.meemContentChanged(meemPath, contentStore.load(meemPath));
					}
				}
				else if (filter instanceof ExactMatchFilter) {
					/*
					 * If it is an exact match filter, grab the MeemPath out of it. 
					 * If that MeemPath isn't in the list, send back an empty MeemContent
					 */
					Object filterObject = ((ExactMatchFilter) filter).getTemplate();
					if (filterObject instanceof MeemPath) {
						MeemPath filterPath = (MeemPath) filterObject;
						if (valueSet.contains(filterPath)) {
							meemContentClient.meemContentChanged(filterPath, contentStore.load(filterPath));
						}
						else {
							meemContentClient.meemContentChanged(filterPath, new MeemContent());
						}
					}
				}
			}
		}
	};

	/**
	 * MeemDefinitionClient (out-bound Facet)
	 */
	public MeemDefinitionClient meemDefinitionClient;
	public final ContentProvider meemDefinitionClientProvider = new ContentProvider() {
		public void sendContent(Object target, Filter filter) throws IllegalArgumentException {

			MeemDefinitionClient meemDefinitionClient = (MeemDefinitionClient) target;
			Collection<MeemPath> valueSet;
			synchronized (meemPaths) {
				valueSet = meemPaths.values();

				if (filter == null) {
					logger.info("!!!! No filter for meem definition client");
					for (MeemPath meemPath : valueSet) {
						meemDefinitionClient.meemDefinitionChanged(meemPath, definitionStore.load(meemPath));
					}
				}
				else if (filter instanceof ExactMatchFilter) {
					Object filterObject = ((ExactMatchFilter) filter).getTemplate();
					if (filterObject instanceof MeemPath) {
						MeemPath filterPath = (MeemPath) filterObject;
						if (valueSet.contains(filterPath)) {
							meemDefinitionClient.meemDefinitionChanged(filterPath, definitionStore.load(filterPath));
						}
						else {
							meemDefinitionClient.meemDefinitionChanged(filterPath, null);
						}
					}
				}
			}
		}
	};


	/* -------------------------------- conduits ------------------------------- */

	public LifeCycleClient lifeCycleClientConduit = new LifeCycleClientAdapter(this);


	/* ----------------------------- private members --------------------------- */
	
	private MeemStoreContentStore contentStore;
	private MeemStoreDefinitionStore definitionStore;

	// TODO[peter] I think the way this collection is used may be a scalability issue
	private Map<String, MeemPath> meemPaths = new HashMap<String, MeemPath>();


	/**
	 * Constructor
	 */
	public MeemStoreWedge() {
		//commence();
	}


	/* ------------------------------- lifecycle methods -------------------------- */
	
	public void commence() {
		Properties properties = System.getProperties();

		// TODO put this in a commence() method, and close stores on conclude().
		startStores(properties);

		// grab the content and definition paths
		// it is possible to have a content stored without a definition and vice versa

		// -mg- not sure this is really necessary. should just hand off to store to try and
		// load and fail silently if path cannot be found
		HashSet<MeemPath> paths = new HashSet<MeemPath>();
		paths.addAll(contentStore.getAllPaths());
		paths.addAll(definitionStore.getAllPaths());
		
		for (MeemPath meemPath : paths) {
			meemPaths.put(meemPath.getLocation(), meemPath);
		}
	}
	
	public void conclude() {
		stopStores();
		meemPaths.clear();
	}


	/* ---------------------------- MeemStore interface ------------------------------- */
	
	public void destroyMeem(MeemPath meemPath) {

		String location = meemPath.getLocation();
		MeemPath removePath;

		synchronized (meemPaths) {
			removePath = (MeemPath) meemPaths.remove(location);

			if (removePath != null) {
	
				contentStore.remove(meemPath);
				definitionStore.remove(meemPath);
	
				meemStoreClient.meemDestroyed(meemPath);
				meemDefinitionClient.meemDefinitionChanged(meemPath, null);
				meemContentClient.meemContentChanged(meemPath, null);
			}
		}
	}

	public void storeMeemContent(MeemPath meemPath, MeemContent meemContent) {
		if (DEBUG) {
			logger.info("storing meem content: " + meemPath);
		}

		// only store meemstore paths
		if (meemPath.getSpace().equals(Space.MEEMSTORE)) {
			contentStore.store(meemPath, meemContent);

			String location = meemPath.getLocation();
			boolean stored = false;

			synchronized (meemPaths) {
				if (!meemPaths.containsKey(location)) {
					meemPaths.put(location, meemPath);
					stored = true;
				}
				
				if (stored) {
					meemStoreClient.meemStored(meemPath);
				}

				// TODO[peter] Should this be called if the meem was just stored?
				meemContentClient.meemContentChanged(meemPath, meemContent);
			}

		}

	}

	public void storeMeemDefinition(MeemPath meemPath, MeemDefinition meemDefinition) {
		if (DEBUG) {
			logger.info("storing meem def: " + meemPath);
		}

		// only store meemstore paths
		if (meemPath.getSpace().equals(Space.MEEMSTORE)) {
			int meemVersion = meemDefinition.getMeemAttribute().getVersion();
			if (meemVersion > definitionStore.getVersion(meemPath)) {
				definitionStore.store(meemPath, meemDefinition);

				synchronized (meemPaths) {
					if (!meemPaths.containsKey(meemPath.getLocation())) {
						meemPaths.put(meemPath.getLocation(), meemPath);
						meemStoreClient.meemStored(meemPath);
					}
					meemDefinitionClient.meemDefinitionChanged(meemPath, meemDefinition);
					meemContentClient.meemContentChanged(meemPath, null);
				}
			} else {
				//definition version number is same or smaller than the persisted one
				//LogTools.warn(logger, "Request to persist MeemDefinition with lower version number than most recent version");
			}
		}
	}

	/**
	 * @see org.openmaji.meem.filter.FilterChecker#invokeMethodCheck(org.openmaji.meem.filter.Filter, java.lang.String, java.lang.Object[])
	 */
	public boolean invokeMethodCheck(Filter filter, String methodName, Object[] args) throws IllegalFilterException {
		if (!(filter instanceof ExactMatchFilter)) {
			throw new IllegalFilterException("Can't check filter: " + filter);
		}

		if (methodName.equals("meemDefinitionChanged") || methodName.equals("meemContentChanged") || methodName.equals("meemDestroyed")) {

			MeemPath meemPath = (MeemPath) args[0];
			if (meemPath != null) {
				ExactMatchFilter exactMatchFilter = (ExactMatchFilter) filter;
				return meemPath.equals(exactMatchFilter.getTemplate());
			}
		}

		return false;
	}


	/* ---------------------------- private methods ------------------------------ */
	
	private void startStores(Properties properties) {
		String definitionStoreClassName = properties.getProperty(DEFINITION_STORE_CLASS);
		String contentStoreClassName = properties.getProperty(CONTENT_STORE_CLASS);

		// start definition store
		try {
			Class<?> definitionStoreClass = Class.forName(definitionStoreClassName);

			definitionStore = (MeemStoreDefinitionStore) definitionStoreClass.newInstance();
		} catch (Exception e) {
			logger.log(Level.INFO, "Exception while starting MeemStoreDefinitionStore", e);
		}

		if (definitionStore != null)
			definitionStore.configure(this, properties);

		//	start content store
		try {
			Class<?> contentStoreClass = Class.forName(contentStoreClassName);

			contentStore = (MeemStoreContentStore) contentStoreClass.newInstance();
		} catch (Exception e) {
			logger.log(Level.INFO, "Exception while starting MeemStoreContentStore", e);
		}

		if (contentStore != null)
			contentStore.configure(this, properties);
	}

	private void stopStores() {
		//definitionStore.close();
		//contentStore.close();
	}
	
/* ---------- MeemDefinitionProvider method(s) ----------------------------- */

  private MeemDefinition meemDefinition = null;

  public MeemDefinition getMeemDefinition() {
    if (meemDefinition == null) {
      meemDefinition = MeemDefinitionFactory.spi.create().createMeemDefinition(
        new Class[] {this.getClass()}
      );
    }
    
      return(meemDefinition);
    }
}