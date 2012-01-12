/*
 * @(#)MeemCoreStructure.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

/* ToDo:
 * - Provide mode to prevent tinkering with system Wedge/Facet/Dependency.
 *
 * - Provide mode to prevent tinkering with application Wedge/Facet/Dependency.
 *
 * - Ensure thread safety.
 *
 * - Create inter-Wedge references.
 *   - Devise inter-Wedge Listener pattern.
 *   - Should Wedges have a weak reference to the Meem itself ?
 *   - Can Wedges reference the LifeCycleManager via the LifeCycle Wedge ?
 *   - Destroy inter-Wedge references, if Wedge removed.
 *
 * - Ensure that Wedges implement all required in-bound Facet methods.
 *
 * - Perform some action, e.g. generate error, if updating the attributes of
 *   a Definition is invalid, e.g. WedgeDefinition implementationClassName or
 *   FacetDefinition identifier, interfaceName or direction.
 *
 * - Significant Design Consideration:
 *   Consider how to incorporate immutable Attribute object structures
 *   that are safe from external modification and don't require clone().
 *   For performance and neater design, consider wrapping Attributes using
 *   a Dynamic Proxy Object and an immutable interface, rather than using
 *   shallowCopy() everywhere.  Potentially create a Copy-On-Write proxy,
 *   which when it is handed out, makes a copy of itself only when a set*()
 *   method is called.  Even the copy can be handed out again as COW.
 */

package org.openmaji.implementation.server.meem.core;

import java.io.Serializable;
import java.util.*;
import java.util.logging.Logger;

import org.openmaji.implementation.server.meem.FacetImpl;
import org.openmaji.implementation.server.meem.InboundFacetImpl;
import org.openmaji.implementation.server.meem.MeemSystemWedge;
import org.openmaji.implementation.server.meem.OutboundFacetImpl;
import org.openmaji.implementation.server.meem.WedgeImpl;

import org.openmaji.meem.Meem;
import org.openmaji.meem.definition.*;
import org.openmaji.system.meem.definition.MeemStructure;
import org.openmaji.system.meem.definition.MeemStructureListener;

/**
 * <p>
 * ...
 * </p>
 * <p>
 * Note: Implementation thread safe = Not considered yet
 * </p>
 * 
 * @author Andy Gelme
 * @version 1.0
 * @see org.openmaji.meem.Meem
 * @see org.openmaji.meem.definition.DependencyAttribute
 * @see org.openmaji.meem.definition.FacetAttribute
 * @see org.openmaji.meem.definition.MeemAttribute
 * @see org.openmaji.meem.definition.WedgeAttribute
 */

public class MeemCoreStructure implements MeemStructure {
	
	private static final Logger logger = Logger.getAnonymousLogger();

	/**
	 * Internal Meem core object structures
	 */

	private MeemCoreImpl meemCoreImpl;

	/**
	 * Meem Definition Structure and Attributes
	 */

	private MeemStructure meemStructure;

	/**
	 * Collection of Wedges
	 */

	private HashMap<String, WedgeImpl> wedges = new HashMap<String, WedgeImpl>();

	/**
	 * Collection of Inbound Facets
	 */

	private Map<String, InboundFacetImpl> inboundFacets = new HashMap<String, InboundFacetImpl>();

	/**
	 * Collection of Outbound Facets
	 */

	private Map<String, OutboundFacetImpl> outboundFacets = new HashMap<String, OutboundFacetImpl>();

	/**
	 * Collection of References to in-bound Facet Wedge invocationTargets
	 */

	// private Vector invocationReferences = null;
	private WedgeImpl baseImpl = null;

	/**
	 * MeemCoreStructure depends upon the MeemStructure implementation to correctly clone() all incoming and outgoing Attribute object structures, so that other objects in the same
	 * Java Virtual Machine can't sneakily alter the underlying Meem Attributes.
	 */

	public MeemCoreStructure(MeemCoreImpl meemCoreImpl, MeemStructure meemStructure) {
		this.meemCoreImpl = meemCoreImpl;
		this.meemStructure = meemStructure;
	}

	/* ---------- MeemCoreStructure method(s) ---------------------------------- */

	private FacetImpl getFacetImpl(String facetIdentifier) {
		FacetImpl facetImpl = (FacetImpl) inboundFacets.get(facetIdentifier);

		if (facetImpl == null) {
			facetImpl = (FacetImpl) outboundFacets.get(facetIdentifier);
		}

		return facetImpl;
	}

	public InboundFacetImpl getInboundFacetImpl(String inboundFacetIdentifier) {
		return (InboundFacetImpl) inboundFacets.get(inboundFacetIdentifier);
	}

	public OutboundFacetImpl getOutboundFacetImpl(String outboundFacetIdentifier) {
		return (OutboundFacetImpl) outboundFacets.get(outboundFacetIdentifier);
	}

	// public synchronized Iterator getInvocationReferences()
	// {
	// if (invocationReferences == null)
	// {
	// invocationReferences = CollectionUtility.createVector();
	//
	// Iterator facetIterator = inboundFacets.values().iterator();
	// while (facetIterator.hasNext())
	// {
	// InboundFacetImpl inboundFacetImpl = (InboundFacetImpl) facetIterator.next();
	// Reference inboundReference = Reference.spi.create(inboundFacetImpl.getIdentifier(),
	// inboundFacetImpl.getProxy(), inboundFacetImpl.isContentRequired());
	//
	// invocationReferences.add(inboundReference);
	// }
	// }
	//
	// return (invocationReferences.iterator());
	// }

	public Meem getSystemImplementation() {
		return (Meem) baseImpl.getImplementation();
	}

	public Collection<WedgeImpl> getWedgeImpls() {
		return new ArrayList<WedgeImpl>(wedges.values());
	}

	/* ---------- MeemStructure method(s) -------------------------------------- */

	public void setMeemStructureListener(MeemStructureListener meemStructureListener) {

		meemStructure.setMeemStructureListener(meemStructureListener);
	}

	public boolean set(MeemAttribute meemAttribute) {

		return (meemStructure.set(meemAttribute));
	}

	public boolean add(WedgeAttribute wedgeAttribute) {

		return (add(wedgeAttribute, false, null));
	}

	/**
	 * <p>
	 * This operation invalidates the commencableWedges and concludableWedges.
	 * </p>
	 */

	public boolean add(WedgeAttribute wedgeAttribute, boolean systemWedgeFlag, Object existingImplementation) {

		// -----------------------------------------
		// Wedge identifier must be unique

		String identifier = wedgeAttribute.getIdentifier();

		WedgeImpl wedgeImpl = (WedgeImpl) wedges.get(identifier);

		if (wedgeImpl != null) {
			throw new IllegalArgumentException("Wedge with identifier '" + identifier + "' already exists");
		}

		// ------------------
		// Create a new Wedge
		if (existingImplementation == null) {
			try {
				wedgeImpl = new WedgeImpl(meemCoreImpl, wedgeAttribute, systemWedgeFlag);
			}
			catch (ClassNotFoundException e) {
				logger.info("Could not locate class: " + e.getLocalizedMessage());
			}
		}
		else {
			wedgeImpl = new WedgeImpl(meemCoreImpl, wedgeAttribute, systemWedgeFlag, existingImplementation);
		}

		wedges.put(identifier, wedgeImpl);

		return meemStructure.add(wedgeAttribute);
	}

	/**
	 * <p>
	 * This operation invalidates the invocationReferences.
	 * </p>
	 * 
	 * @exception IllegalArgumentException
	 *                Unknown Wedge Definition
	 * @exception IllegalArgumentException
	 *                Facet identifier used in another Wedge
	 */

	public boolean add(WedgeAttribute wedgeAttribute, FacetAttribute facetAttribute) {

		// -------------------------------
		// Wedge implementation must exist

		String identifier = wedgeAttribute.getIdentifier();
		WedgeImpl wedgeImpl = (WedgeImpl) wedges.get(identifier);

		if (wedgeImpl == null) {
			throw new IllegalArgumentException("Unknown WedgeAttribute: " + wedgeAttribute);
		}

		// -------------------------------
		// Facet identifier must be unique

		String facetIdentifier = facetAttribute.getIdentifier();

		if (getFacetImpl(facetIdentifier) != null) {
			String message = "Wedge '" + identifier + "' declares Facet identifier '" + facetIdentifier + "' that is already used by another Facet";
			throw new IllegalArgumentException(message);
		}

		// ------------------
		// Create a new Facet

		try {
			if (facetAttribute.isDirection(Direction.INBOUND)) {
				InboundFacetImpl inboundFacetImpl = new InboundFacetImpl(meemCoreImpl, wedgeImpl, (FacetInboundAttribute) facetAttribute);
	
				// ------------------------------------------------------------------
				// Update "invocation mapping" table for system Wedge in-bound Facets
				if (wedgeImpl.isSystemWedge()) {
					if (wedgeImpl.getImplementation() instanceof MeemSystemWedge) {
						if (baseImpl != null && baseImpl != wedgeImpl) {
							throw new IllegalStateException("meem has two implementations of the MeemSystemWedge!");
						}
						baseImpl = wedgeImpl;
					}
				}
	
				wedgeImpl.addInboundFacet(facetIdentifier, inboundFacetImpl);
	
				inboundFacets.put(facetIdentifier, inboundFacetImpl);
	
				// Invalidate References to in-bound Facet Wedge invocationTargets
				// invocationReferences = null;
			}
			else {
				OutboundFacetImpl outboundFacetImpl = new OutboundFacetImpl(wedgeImpl, (FacetOutboundAttribute) facetAttribute);
	
				wedgeImpl.addOutboundFacet(facetIdentifier, outboundFacetImpl);
	
				outboundFacets.put(facetIdentifier, outboundFacetImpl);
			}
		}
		catch (ClassNotFoundException e) {
			throw new IllegalArgumentException("Class not found", e);
		}
		
		return meemStructure.add(wedgeAttribute, facetAttribute);
	}

	public boolean add(FacetAttribute facetAttribute, DependencyAttribute dependencyAttribute) {

		boolean success = meemStructure.add(facetAttribute, dependencyAttribute);

		return (success);
	}

	public boolean update(MeemAttribute meemAttribute) {

		return (meemStructure.update(meemAttribute));
	}

	/**
	 * <p>
	 * This operation invalidates the commencableWedges and concludableWedges.
	 * </p>
	 */

	public boolean update(WedgeAttribute wedgeAttribute) {

		// Don't allow Implementation class name to change (requires add/remove).
		// Change Persistent fields.

		return (meemStructure.update(wedgeAttribute));
	}

	public boolean update(FacetAttribute facetAttribute) {

		return (meemStructure.update(facetAttribute));
	}

	public boolean update(DependencyAttribute dependencyAttribute) {

		// -mg- fix this to handle dependency attribute updates

		return (meemStructure.update(dependencyAttribute));
	}

	/**
	 * <p>
	 * This operation invalidates the commencableWedges and concludableWedges.
	 * </p>
	 */

	public boolean remove(WedgeAttribute wedgeAttribute) {

		String identifier = wedgeAttribute.getIdentifier();

		WedgeImpl wedgeImpl = (WedgeImpl) wedges.get(identifier);

		if (wedgeImpl != null) {

			wedgeImpl.disconnectConduits();

			// ---------------------------
			// Remove Wedge from this Meem

			wedges.remove(identifier);
		}

		return (meemStructure.remove(wedgeAttribute));
	}

	public boolean remove(FacetAttribute facetAttribute) {

		String facetIdentifier = facetAttribute.getIdentifier();

		FacetImpl facetImpl = getFacetImpl(facetIdentifier);

		if (facetImpl != null) {
			// ----------------------------------------------------
			// Remove only application Wedge Facets from this Meem

			WedgeImpl wedgeImpl = facetImpl.getWedgeImpl();

			if (wedgeImpl.isSystemWedge())
				return (false);

			// ---------------------------
			// Remove Facet from this Meem

			if (facetAttribute.isDirection(Direction.INBOUND)) {
				wedgeImpl.removeInboundFacet(facetIdentifier);
				inboundFacets.remove(facetIdentifier);

				// ---------------------------------------------------------------
				// Invalidate References to in-bound Facet Wedge invocationTargets
				// invocationReferences = null;
			}
			else {
				wedgeImpl.removeOutboundFacet(facetIdentifier);
				outboundFacets.remove(facetIdentifier);
			}
		}

		return meemStructure.remove(facetAttribute);
	}

	public boolean remove(DependencyAttribute dependencyAttribute) {

		boolean success = meemStructure.remove(dependencyAttribute);
		return (success);
	}

	public MeemAttribute getMeemAttribute() {
		return (meemStructure.getMeemAttribute());
	}

	public WedgeAttribute getWedgeAttribute(Serializable wedgeKey) {

		return (meemStructure.getWedgeAttribute(wedgeKey));
	}

	public FacetAttribute getFacetAttribute(String facetKey) {

		return (meemStructure.getFacetAttribute(facetKey));
	}

	public DependencyAttribute getDependencyAttribute(Serializable dependencyKey) {

		return (meemStructure.getDependencyAttribute(dependencyKey));
	}

	public Collection<Serializable> getWedgeAttributeKeys() {
		return (meemStructure.getWedgeAttributeKeys());
	}

	public Collection<String> getFacetAttributeKeys(Serializable wedgeKey) {

		return (meemStructure.getFacetAttributeKeys(wedgeKey));
	}

	public Collection<Serializable> getDependencyAttributeKeys() {
		return (meemStructure.getDependencyAttributeKeys());
	}

	public String getFacetKeyFromDependencyKey(Serializable dependencyKey) {

		return (meemStructure.getFacetKeyFromDependencyKey(dependencyKey));
	}

	public Serializable getDependencyKeyFromFacetKey(String facetKey) {

		return (meemStructure.getDependencyKeyFromFacetKey(facetKey));
	}

}
