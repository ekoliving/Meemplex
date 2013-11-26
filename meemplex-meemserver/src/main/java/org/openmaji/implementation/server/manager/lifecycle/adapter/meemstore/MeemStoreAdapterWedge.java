/*
 * @(#)MeemStoreAdapterWedge.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.server.manager.lifecycle.adapter.meemstore;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openmaji.implementation.server.meem.wedge.lifecycle.SystemLifeCycleClientAdapter;


import org.openmaji.meem.MeemPath;
import org.openmaji.meem.Wedge;
import org.openmaji.meem.definition.*;
import org.openmaji.meem.filter.ExactMatchFilter;
import org.openmaji.meem.wedge.dependency.DependencyClient;
import org.openmaji.meem.wedge.dependency.DependencyHandler;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClient;
import org.openmaji.system.meem.core.MeemCore;
import org.openmaji.system.meem.definition.MeemContent;
import org.openmaji.system.space.meemstore.MeemContentClient;
import org.openmaji.system.space.meemstore.MeemDefinitionClient;
import org.openmaji.system.space.meemstore.MeemStore;

/**
 * <p>
 * ...
 * </p>
 * @author  mg
 * @version 1.0
 */
public class MeemStoreAdapterWedge implements Wedge, MeemDefinitionClient, MeemContentClient {
	
	private static final Logger logger = Logger.getAnonymousLogger();

	public MeemCore meemCore;
	public MeemStore meemStore;

	// conduits
	public MeemStoreAdapter meemStoreAdapterConduit = new MeemStoreAdapterConduit(); // inbound
	public LifeCycleClient lifeCycleClientConduit = new SystemLifeCycleClientAdapter(this);
	public DependencyClient dependencyClientConduit = new DependencyClientConduit();
	
	public MeemDefinitionClient meemDefinitionClientConduit; // outbound
	public MeemContentClient meemContentClientConduit; // outbound
	public DependencyHandler dependencyHandlerConduit; // outbound

	// dependency attributes
	private DependencyAttribute meemStoreDependencyAttribute;

	private Map requests = Collections.synchronizedMap(new HashMap());
	private Vector definitionDependencyAttributes = new Vector();
	private Vector contentDependencyAttributes = new Vector();


	/* ------------- MeemDefinitionClient methods ---------- */

	/**
	 * 
	 */
	public void meemDefinitionChanged(MeemPath meemPath, MeemDefinition meemDefinition) {

		synchronized (requests) {
			Request request = (Request) requests.get(meemPath);
			if (request != null) {
				request.setUpdated(true);
				if (request.gotContent()) {
					requests.remove(meemPath);
				}
			}
		}
	
		meemDefinitionClientConduit.meemDefinitionChanged(meemPath, meemDefinition);
		
		DependencyAttribute contentDependencyAttribute =
			new DependencyAttribute( 
				DependencyType.WEAK,
				Scope.LOCAL,
				meemCore.getMeemStore(),
				"meemContentClient",
				ExactMatchFilter.create(meemPath), 
				true);

		contentDependencyAttributes.add(contentDependencyAttribute);

		dependencyHandlerConduit.addDependency("meemContentClient", contentDependencyAttribute, LifeTime.TRANSIENT);

	}

	/* -------------- MeemContentClient methods ------------ */

	/**
	 * 
	 */
	public void meemContentChanged(MeemPath meemPath, MeemContent meemContent) {

		synchronized (requests) {
			Request request = (Request) requests.get(meemPath);
			if (request != null) {
				request.setUpdated(false);
				if (request.gotDefinition()) {
					requests.remove(meemPath);
				}
			}
		}

		meemContentClientConduit.meemContentChanged(meemPath, meemContent);
	}


	/* ----------------------- lifecycle methods --------------------- */
	
	public void commence() {
		// set up meem store dependency
		meemStoreDependencyAttribute = new DependencyAttribute(
				DependencyType.STRONG, 
				Scope.LOCAL,
				meemCore.getMeemStore(), 
				"meemStore", 
				ExactMatchFilter.create(new String()),
				true
			);

		dependencyHandlerConduit.addDependency("meemStore", meemStoreDependencyAttribute, LifeTime.TRANSIENT);
	}

	public void conclude() {
		// remove meem store dependency

		dependencyHandlerConduit.removeDependency(meemStoreDependencyAttribute);
	}

	/* ------------- MeemStoreConduit --------------------- */

	private final class MeemStoreAdapterConduit implements MeemStoreAdapter {

		/**
		 * 
		 */
		public void load(MeemPath meemPath) {
			requests.put(meemPath, new Request());

			DependencyAttribute definitionDependencyAttribute =
				new DependencyAttribute( 
					DependencyType.WEAK,
					Scope.LOCAL,
					meemCore.getMeemStore(),
					"meemDefinitionClient",
					ExactMatchFilter.create(meemPath), 
					true);

			//definitionDependencyAttributes.put(meemPath, definitionDependencyAttribute);
			definitionDependencyAttributes.add(definitionDependencyAttribute);

			dependencyHandlerConduit.addDependency("meemDefinitionClient", definitionDependencyAttribute, LifeTime.TRANSIENT);			
		}

		/**
		 * 
		 */
		public void destroyMeem(MeemPath meemPath) {
			meemStore.destroyMeem(meemPath);
		}

		/**
		 * 
		 */
		public void storeMeemContent(MeemPath meemPath, MeemContent meemContent) {
			logger.log(Level.INFO, "store meem content: " + meemPath);
			
			meemStore.storeMeemContent(meemPath, meemContent);
		}

		/**
		 * 
		 */
		public void storeMeemDefinition(MeemPath meemPath, MeemDefinition meemDefinition) {
			meemStore.storeMeemDefinition(meemPath, meemDefinition);
		}

	}

	private final class Request {
		private boolean gotDefinition = false;
		private boolean gotContent = false;

		public boolean gotContent() {
			return gotContent;
		}

		public boolean gotDefinition() {
			return gotDefinition;
		}

		public void setUpdated(boolean definition) {
			if (definition)
				gotDefinition = true;
			else
				gotContent = true;
		}

	}

	/* ---------------------- DependencyClient conduit ----------------*/

	private final class DependencyClientConduit implements DependencyClient {

		/**
		 * 
		 */
		public void dependencyConnected(DependencyAttribute dependencyAttribute) {
			if (definitionDependencyAttributes.remove(dependencyAttribute) == true) {
				dependencyHandlerConduit.removeDependency(dependencyAttribute);
			}
			
			if (contentDependencyAttributes.remove(dependencyAttribute) == true) {				
				dependencyHandlerConduit.removeDependency(dependencyAttribute);
			}			
		}

		/**
		 * 
		 */
		public void dependencyDisconnected(DependencyAttribute dependencyAttribute) {
		}
		
		/**
		 * @see org.openmaji.meem.wedge.dependency.DependencyClient#dependencyAdded(java.lang.String, org.openmaji.meem.definition.DependencyAttribute)
		 */
		public void dependencyAdded(String facetId, DependencyAttribute dependencyAttribute) {
		}

		/**
		 * @see org.openmaji.meem.wedge.dependency.DependencyClient#dependencyRemoved(org.openmaji.meem.definition.DependencyAttribute)
		 */
		public void dependencyRemoved(DependencyAttribute dependencyAttribute) {
		}
		
		public void dependencyUpdated(DependencyAttribute dependencyAttribute) {
		}
	}
}
