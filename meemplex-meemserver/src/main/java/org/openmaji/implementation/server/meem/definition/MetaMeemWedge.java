/*
 * @(#)MetaMeemWedge.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - Consider using delegation to MetaMeemStructureAdapter, rather than an
 *   inheritance based approach.
 * 
 * - Consider moving "MetaMeem2MeemStructure.metaMeemToMeemStructure" into
 *   MetaMeemStructureAdapter ?
 *
 * - Implement FilterChecker that excludes System WedgeDefinitions.
 *
 * - Improve error handling ...
 *   - MeemStructureImpl should throw Exceptions, not return boolean.
 *   - MetaMeemStructureAdapter should throw Exceptions.
 *   - Send error messages via the Error out-bound Facet.
 *
 * - Destructively test, using "broken" Definitions ... shouldn't crash system.
 */

package org.openmaji.implementation.server.meem.definition;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;

import org.openmaji.implementation.server.meem.wedge.lifecycle.SystemLifeCycleClientAdapter;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.openmaji.meem.Wedge;
import org.openmaji.meem.definition.*;
import org.openmaji.meem.filter.Filter;
import org.openmaji.meem.space.Space;
import org.openmaji.meem.wedge.dependency.DependencyClient;
import org.openmaji.meem.wedge.dependency.DependencyHandler;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClient;
import org.openmaji.meem.wedge.reference.Reference;
import org.openmaji.system.meem.core.MeemCore;
import org.openmaji.system.meem.definition.MeemStructureListener;
import org.openmaji.system.meem.definition.MetaMeem;
import org.openmaji.system.meem.wedge.reference.ContentProvider;
import org.openmaji.system.meem.wedge.reference.MeemClientCallback;
import org.openmaji.system.meem.wedge.reference.MeemClientConduit;
import org.openmaji.system.space.meemstore.MeemStore;

/**
 * <p>
 * ...
 * </p>
 * <p>
 * Note: Implementation thread safe = Not considered yet
 * </p>
 * 
 * @author Andy Gelme
 * @author Kin Wong
 * @version 1.0
 * @see org.openmaji.meem.definition.DependencyAttribute
 * @see org.openmaji.meem.definition.FacetAttribute
 * @see org.openmaji.meem.definition.MeemAttribute
 * @see org.openmaji.meem.definition.WedgeAttribute
 */

public class MetaMeemWedge extends MetaMeemStructureAdapter implements MetaMeem, Wedge {

	private static final Logger logger = Logger.getAnonymousLogger();

	private static final boolean DEBUG = false;

	/** Internal reference to MeemCore */
	public MeemCore meemCore;

	/*
	 * ------------------------------ outbound facets
	 * -------------------------------
	 */

	/**
	 * MetaMeem (out-bound Facet)
	 */
	public MetaMeem metaMeemClient;

	public final ContentProvider<MetaMeem> metaMeemClientProvider = new ContentProvider<MetaMeem>() {
		public synchronized void sendContent(MetaMeem metaMeemClient, Filter filter) {
			commence();

			metaMeemClient.updateMeemAttribute(meemStructure.getMeemAttribute());

			for (Serializable wedgeKey : meemStructure.getWedgeAttributeKeys()) {
				metaMeemClient.addWedgeAttribute(meemStructure.getWedgeAttribute(wedgeKey));
				for (String facetKey : meemStructure.getFacetAttributeKeys(wedgeKey)) {
					metaMeemClient.addFacetAttribute(wedgeKey, meemStructure.getFacetAttribute(facetKey));
				}

				// send cached dependencies received from the
				// dependencyHandlerConduit
				// TODO remove the dependencies from MetaMeem

				// logger.log(Level.INFO, "sending dependency attributes...");
				for (String facetId : facetDep.keySet()) {
					DependencyAttribute dependencyAttribute = (DependencyAttribute) facetDep.get(facetId);
					// logger.log(Level.INFO,
					// "sending dependency attribute with meem: " +
					// dependencyAttribute.getMeem());
					metaMeemClient.addDependencyAttribute(facetId, dependencyAttribute);
				}

			}
		}
	};

	/*
	 * -------------------------------- conduits
	 * --------------------------------
	 */

	public LifeCycleClient lifeCycleClientConduit = new SystemLifeCycleClientAdapter(this);

	public MeemClientConduit meemClientConduit;

	public DependencyHandler dependencyHandlerConduit;

	public DependencyClient dependencyClientConduit = new DependencyClientConduit();

	/*
	 * ----------------------------- private members
	 * ----------------------------
	 */

	// private boolean commenced = false;

	private MeemStructureListener meemStructureListener = new MeemStructureListenerImpl();

	/**
	 * used for caching dependencies received from the dependencyHandler conduit
	 * it is a map from facetId to DependencyAttribute
	 */
	private HashMap<String, DependencyAttribute> facetDep = new HashMap<String, DependencyAttribute>();

	/**
	 * Mapping of dependency id to facetId
	 */
	private HashMap<Serializable, String> depFacet = new HashMap<Serializable, String>();

	/**
	 * Map of dependency keys to DependencyAttributes
	 */
	private HashMap<Serializable, DependencyAttribute> dependencies = new HashMap<Serializable, DependencyAttribute>();

	/*
	 * --------------------------------------------------------------------------
	 */

	/**
	 * Constructor
	 */
	public MetaMeemWedge() {
		super(null);
	}

	/* ---------- MetaMeemStructureAdapter method(s) --------------------------- */

	/**
	 * 
	 */
	public void commence() {
		if (meemStructure == null) {
			meemStructure = meemCore.getMeemStructure();
			meemStructure.setMeemStructureListener(meemStructureListener);
			// this.structure = meemStructure;
			// commenced = true;
		}
	}

	/* ----------------------- override some MetaMeem methods ------------------ */

	/*
	 * TODO enable these public void addDependencyAttribute(Object facetId,
	 * DependencyAttribute dependencyAttribute) { if (DEBUG) {
	 * logger.log(Level.INFO, "addDependencyAttribute: " + dependencyAttribute);
	 * }
	 * 
	 * // assume permanent
	 * dependencyHandlerConduit.addDependency((String)facetId,
	 * dependencyAttribute, LifeTime.PERMANENT); }
	 */

	public void removeDependencyAttribute(Serializable dependencyId) {

		if (DEBUG) {
			logger.log(Level.INFO, "removeDependencyAttribute( " + dependencyId + ")");
		}

		DependencyAttribute dependencyAttribute;

		synchronized (dependencies) {
			dependencyAttribute = (DependencyAttribute) dependencies.get(dependencyId);
		}

		if (dependencyAttribute == null) {
			logger.log(Level.INFO, "Unknown dependency for: " + dependencyId);
			return;
		}

		super.removeDependency(dependencyAttribute);
	}

	/*
	 * public void updateDependencyAttribute(DependencyAttribute
	 * dependencyAttribute) {
	 * 
	 * if (DEBUG) { logger.log(Level.INFO, "updateDependencyAttribute: " +
	 * dependencyAttribute); }
	 * 
	 * dependencyHandlerConduit.updateDependency(dependencyAttribute); }
	 */

	/* ---------------------------- private members ---------------------------- */

	/**
	 * Persist the current MeemDefinition in MeemStore
	 */
	private void persistDefinition() {
		if (!meemCore.getMeemPath().getSpace().equals(Space.MEEMSTORE)) {
			return;
		}

		boolean hasApplicationWedges = false;

		/*
		 * Generate MeemDefinition This is different to
		 * MeemDefinitionStructureAdapter.getMeemDefinition() in that we only
		 * want application wedges
		 */
		MeemDefinition meemDefinition = new MeemDefinition(meemStructure.getMeemAttribute());

		synchronized (meemStructure) {
			Iterator<Serializable> wedgeAttributeKeys = meemStructure.getWedgeAttributeKeys().iterator();

			while (wedgeAttributeKeys.hasNext()) {
				Serializable wedgeKey = wedgeAttributeKeys.next();

				WedgeAttribute wedgeAttribute = meemStructure.getWedgeAttribute(wedgeKey);
				if (!wedgeAttribute.isSystemWedge()) {
					hasApplicationWedges = true;

					WedgeDefinition wedgeDefinition = new WedgeDefinition(wedgeAttribute);

					meemDefinition.addWedgeDefinition(wedgeDefinition);

					Iterator<String> facetAttributeKeys = meemStructure.getFacetAttributeKeys(wedgeKey).iterator();

					while (facetAttributeKeys.hasNext()) {
						String facetKey = facetAttributeKeys.next();

						FacetDefinition facetDefinition = new FacetDefinition(meemStructure.getFacetAttribute(facetKey));

						wedgeDefinition.addFacetDefinition(facetDefinition);

					}
				}
			}
		}

		if (hasApplicationWedges) {
			MeemAttribute meemAttribute = meemStructure.getMeemAttribute();

			meemAttribute.setVersion(meemAttribute.getVersion() + 1);

			// bump the MeemDefinition version number
			meemDefinition.getMeemAttribute().setVersion(meemAttribute.getVersion());

			// and the one in the MeemStructure

			// stop listening
			meemStructure.setMeemStructureListener(null);

			meemStructure.update(meemAttribute);

			// start listening
			meemStructure.setMeemStructureListener(meemStructureListener);

			// tell MeemStore to persist it
			meemClientConduit.provideReference(meemCore.getMeemStore(), "meemStore", MeemStore.class, new ReferenceCallbackImpl(meemDefinition));
		}
	}

	/* --------------------------- inner classes ------------------------ */

	/**
	 * 
	 */
	private final class ReferenceCallbackImpl implements MeemClientCallback<MeemStore> {
		MeemDefinition meemDefinition;

		ReferenceCallbackImpl(MeemDefinition meemDefinition) {
			this.meemDefinition = meemDefinition;
		}

		public void referenceProvided(Reference<MeemStore> reference) {
			if (reference == null) {
				logger.log(Level.WARNING, "no meemStore reference found can't persist meemDefinition!");
				return;
			}

			MeemStore meemStore = reference.getTarget();

			meemStore.storeMeemDefinition(meemCore.getMeemPath(), meemDefinition);
		}
	}

	/**
	 * 
	 */
	final private class MeemStructureListenerImpl implements MeemStructureListener {

		public void meemAttributeChanged(MeemAttribute meemAttribute) {

			persistDefinition();

			metaMeemClient.updateMeemAttribute(meemAttribute);
		}

		public void wedgeAttributeAdded(WedgeAttribute wedgeAttribute) {

			persistDefinition();

			metaMeemClient.addWedgeAttribute(wedgeAttribute);
		}

		public void wedgeAttributeChanged(WedgeAttribute wedgeAttribute) {

			persistDefinition();

			metaMeemClient.updateWedgeAttribute(wedgeAttribute);
		}

		public void wedgeAttributeRemoved(WedgeAttribute wedgeAttribute) {

			persistDefinition();

			metaMeemClient.removeWedgeAttribute(wedgeAttribute.getIdentifier());
		}

		public void facetAttributeAdded(WedgeAttribute wedgeAttribute, FacetAttribute facetAttribute) {

			persistDefinition();

			metaMeemClient.addFacetAttribute(wedgeAttribute.getIdentifier(), facetAttribute);
		}

		public void facetAttributeChanged(FacetAttribute facetAttribute) {

			persistDefinition();

			metaMeemClient.updateFacetAttribute(facetAttribute);
		}

		public void facetAttributeRemoved(FacetAttribute facetAttribute) {

			persistDefinition();

			metaMeemClient.removeFacetAttribute(facetAttribute.getIdentifier());
		}

		public void dependencyAttributeAdded(String facetId, DependencyAttribute dependencyAttribute) {
			if (DEBUG) {
				logger.log(Level.INFO, "dependencyAttributeAdded: " + dependencyAttribute);
			}

			// add dependency via the dependencyHandlerConduit
			dependencyHandlerConduit.addDependency(facetId, dependencyAttribute, LifeTime.PERMANENT);
		}

		public void dependencyAttributeChanged(DependencyAttribute dependencyAttribute) {
			if (DEBUG) {
				logger.log(Level.INFO, "dependencyAttributeChanged: " + dependencyAttribute);
			}

			// update dependency via the dependencyHandlerConduit
			dependencyHandlerConduit.updateDependency(dependencyAttribute);
		}

		public void dependencyAttributeRemoved(DependencyAttribute dependencyAttribute) {

			if (DEBUG) {
				logger.log(Level.INFO, "dependencyAttributeRemoved: " + dependencyAttribute);
			}

			// remove dependency via the dependencyHandlerConduit
			dependencyHandlerConduit.removeDependency(dependencyAttribute);
		}
	}

	/**
	 * class to receive dependencyclientconduit messages
	 */
	private final class DependencyClientConduit implements DependencyClient {

		public void dependencyAdded(String facetId, DependencyAttribute dependencyAttribute) {
			if (DEBUG) {
				logger.log(Level.INFO, "dependencyAdded: " + facetId + " - " + dependencyAttribute);
			}
			synchronized (dependencies) {
				facetDep.put(facetId, dependencyAttribute);
				depFacet.put(dependencyAttribute.getKey(), facetId);
				dependencies.put(dependencyAttribute.getKey(), dependencyAttribute);
			}

			// meemStructure.add(facet, dependency);

			metaMeemClient.addDependencyAttribute(facetId, dependencyAttribute);
		}

		public void dependencyRemoved(DependencyAttribute dependencyAttribute) {
			if (DEBUG) {
				logger.log(Level.INFO, "dependencyRemoved: " + dependencyAttribute);
			}
			// remove the dependency attribute from dependencies cache
			synchronized (dependencies) {
				Object facetId = depFacet.remove(dependencyAttribute.getKey());
				if (facetId != null) {
					facetDep.remove(facetId);
				}
				dependencies.remove(dependencyAttribute.getKey());
			}

			metaMeemClient.removeDependencyAttribute(dependencyAttribute.getKey());
		}

		public void dependencyUpdated(DependencyAttribute dependencyAttribute) {
			if (DEBUG) {
				logger.log(Level.INFO, "dependencyUpdated: " + dependencyAttribute);
			}

			// update the dependency attribute in dependencies cache
			synchronized (facetDep) {
				String facetId = depFacet.get(dependencyAttribute.getKey());
				if (facetId != null) {
					facetDep.put(facetId, dependencyAttribute);
				}
				dependencies.put(dependencyAttribute.getKey(), dependencyAttribute);
			}

			metaMeemClient.updateDependencyAttribute(dependencyAttribute);
		}

		public void dependencyConnected(DependencyAttribute dependencyAttribute) {
		}

		public void dependencyDisconnected(DependencyAttribute dependencyAttribute) {
		}
	}

}
/*
 * final class MetaMeem2MeemStructure { static public void
 * metaMeemToMeemStructure(MetaMeem metaMeem, MeemStructure meemStructure) {
 * 
 * metaMeem.updateMeemAttribute(meemStructure.getMeemAttribute());
 * 
 * synchronized (meemStructure) { Iterator wedgeAttributeKeys =
 * meemStructure.getWedgeAttributeKeys();
 * 
 * while (wedgeAttributeKeys.hasNext()) { Object wedgeKey =
 * wedgeAttributeKeys.next();
 * 
 * WedgeAttribute wedgeAttribute = meemStructure.getWedgeAttribute(wedgeKey);
 * metaMeem.addWedgeAttribute(wedgeAttribute);
 * 
 * Iterator facetAttributeKeys = meemStructure.getFacetAttributeKeys(wedgeKey);
 * 
 * while (facetAttributeKeys.hasNext()) { Object facetKey =
 * facetAttributeKeys.next();
 * 
 * metaMeem.addFacetAttribute(wedgeKey,
 * meemStructure.getFacetAttribute(facetKey));
 * 
 * Object dependencyKey = meemStructure.getDependencyKeyFromFacetKey(facetKey);
 * 
 * if (dependencyKey != null) { metaMeem.addDependencyAttribute(facetKey,
 * meemStructure.getDependencyAttribute(dependencyKey)); } } } } } }
 */
