/*
 * @(#)MeemStructureImpl.java
 * Created on 10/07/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - Methods for add(), update() and remove() should throw an
 *   IllegalArgumentException, rather than return a boolean.
 *   This will allow for better diagnostic messages !
 *
 * - Produce an implementation that doesn't clone() for performance.
 *   Extend that version for the "safe" clone() implementation.
 *
 * - Consider whether linked-list (chain) "MeemStructure next" should be
 *   external to the MeemStructure.
 */

package org.openmaji.implementation.server.meem.definition;

import java.io.Serializable;
import java.util.*;

import org.swzoo.log2.core.LogFactory;
import org.swzoo.log2.core.LogTools;
import org.swzoo.log2.core.Logger;

import org.openmaji.meem.definition.*;
import org.openmaji.system.meem.definition.MeemStructure;
import org.openmaji.system.meem.definition.MeemStructureListener;

/**
 * <code>MeemStructureImpl</code>.
 * <p>
 * @author Kin Wong
 */
public class MeemStructureImpl implements MeemStructure {
	private static final Logger logger = LogFactory.getLogger();
	private static final boolean DEBUG = false;

	/** 
	 * Meem attribute
	 */
	private MeemAttribute meem;
	
	/** 
	 * Maps wedge id to WedgeEntry 
	 */
	private final Map<Serializable, WedgeEntry> wedges = new HashMap<Serializable, WedgeEntry>();
	
	/** 
	 * Maps facet id to FacetEntry
	 */
	private final Map<Serializable, FacetEntry> facets = new HashMap<Serializable, FacetEntry>();		

	/** 
	 * Maps dependency id to DependencyEntry.
	 * 
	 * Dependencies should not be in MeemStructure, so do not persist 
	 * dependencies. 
	 */
	private transient Map<Serializable, DependencyEntry> dependencies = new HashMap<Serializable, DependencyEntry>();	

	private MeemStructure next;

	private MeemStructureListener meemStructureListener = null;
	
	/**
	 * Constructs an instance of <code>MeemStructureBase</code>.
	 * <p>
	 */
	public MeemStructureImpl() {
	}
	
	public void setChain(MeemStructure next) {
		this.next = next;
	}

	public void setMeemStructureListener(MeemStructureListener meemStructureListener) {
		this.meemStructureListener = meemStructureListener;
	}

	public void clear() {
		if (DEBUG) {
			LogTools.info(logger, "clear()");
		}
		meem = null;
		wedges.clear();
		facets.clear();
		getDependencies().clear();
	}
	
	public MeemAttribute getMeemAttribute() {
		if (meem == null) {
			meem = new MeemAttribute();
		}
		return (MeemAttribute)meem.clone();
	}

	/**
	 * 
	 */

	public boolean set(MeemAttribute meemAttribute) {
    
		if (meem == null) {
			meem = new MeemAttribute();
		}
		
		MeemAttribute.copyPreservingImmutableAttributes(this.meem, meemAttribute);

		if (meemStructureListener != null) {
			meemStructureListener.meemAttributeChanged(meemAttribute);
		}

		if (next != null) {
			next.set(meemAttribute);
		}
    
		return(true);
	}

	/**
	 * 
	 */
	public boolean add(WedgeAttribute wedge) {
		if(wedges.containsKey(wedge.getIdentifier())) {
			return false;	// Already exist
		}
		
		WedgeEntry wedgeEntry = new WedgeEntry(wedge);
		wedges.put(wedgeEntry.getWedge().getIdentifier(), wedgeEntry);
		
		if (meemStructureListener != null) {
			meemStructureListener.wedgeAttributeAdded(wedge);
		}

		if(next != null) {
			next.add(wedge);
		}
		return true;
	}

	public boolean add(WedgeAttribute wedge, FacetAttribute facet) {
		if(facets.containsKey(facet.getIdentifier())) {
			return false; // Already exists
		}

		WedgeEntry wedgeEntry = (WedgeEntry)wedges.get(wedge.getIdentifier());
		if(wedgeEntry == null) {
			return false;	// Wedge is not found
		}
    
		FacetEntry facetEntry =  new FacetEntry(wedge.getIdentifier(), facet);
		facets.put(facetEntry.getFacet().getIdentifier(), facetEntry);
		wedgeEntry.addFacetId(facetEntry.getFacet().getIdentifier());
		if (meemStructureListener != null) {
			meemStructureListener.facetAttributeAdded(wedge, facet);
		}

		if(next != null) {
			next.add(wedge, facet);
		}
		return true;
	}
	
	/**
	 * 
	 */
	public boolean add(FacetAttribute facet, DependencyAttribute dependency) {
		if (DEBUG) {
			LogTools.info(logger, "adding dependency: " + facet.getIdentifier() + "->" + dependency);
		}
		
		
		FacetEntry facetEntry = (FacetEntry)facets.get(facet.getIdentifier());
		if(facetEntry == null) {
			if (DEBUG) {
				LogTools.info(logger, "Facet has not been defined: " + facet.getIdentifier());
			}
			return false; // Facet has not been defined
		}
		
/* TODO remove this section */
		if(getDependencies().containsKey(dependency.getKey())) {
			if (DEBUG) {
				LogTools.info(logger, "Dependency already exists: " + dependency.getKey() + " => " + getDependencies().get(dependency.getKey()));
			}
			return false;	// Dependency already exists
		}
		
		DependencyEntry dependencyEntry = 
			new DependencyEntry(facetEntry.getFacet().getIdentifier(), dependency);

		getDependencies().put(dependencyEntry.getDependency().getKey(), dependencyEntry);
		facetEntry.setDependencyId(dependencyEntry.getDependency().getKey());
/* end of section to remove */
		if (meemStructureListener != null) {
			meemStructureListener.dependencyAttributeAdded(facet.getIdentifier(), dependency);
		}

		if(next != null) {
			next.add(facet, dependency);
		}
		return true;
	}

	public boolean update(MeemAttribute meemAttribute) {
    
		if (this.meem == null) {
			return false;
		}
    
		if (this.meem.getKey().equals(meemAttribute.getKey()) == false) {
	      return(false);
	    }

		MeemAttribute.copyPreservingImmutableAttributes(this.meem, meemAttribute);
    
		if (meemStructureListener != null) {
			meemStructureListener.meemAttributeChanged(meemAttribute);
		}

		if (next != null) {
			next.update(meemAttribute);
		}
    
		return(true);
	}
	
	public boolean update(WedgeAttribute wedge) {

		WedgeEntry wedgeEntry = (WedgeEntry)wedges.get(wedge.getIdentifier());
		if(wedgeEntry == null) {
			return false;
		}
		wedgeEntry.updateWedge(wedge);
		if (meemStructureListener != null) {
			meemStructureListener.wedgeAttributeChanged(wedge);
		}

		if(next != null) {
			next.update(wedge);
		}
		return true;
	}
	
	public boolean update(FacetAttribute facet) {

		FacetEntry facetEntry = (FacetEntry)facets.get(facet.getIdentifier());
		if(facetEntry == null) {
			return false;
		}
		facetEntry.updateFacet(facet);
		if (meemStructureListener != null) {
			meemStructureListener.facetAttributeChanged(facet);
		}

		if(next != null) {
			next.update(facet);
		}
		return true;
	}
	
	/**
	 * 
	 */
	public boolean update(DependencyAttribute dependency) {
		if (DEBUG) {
			LogTools.info(logger, "updating dependency: " + dependency);
		}
		
/* TODO Remove this seciton */
		DependencyEntry dependencyEntry = 
			(DependencyEntry)getDependencies().get(dependency.getKey());
			
		if(dependencyEntry == null) {
			// still notify listener
			if (meemStructureListener != null) {
				meemStructureListener.dependencyAttributeChanged(dependency);
			}
			return false;
		}
		dependencyEntry.updateDependency(dependency);
/* end of section to remove */		
		if (meemStructureListener != null) {
			meemStructureListener.dependencyAttributeChanged(dependency);
		}

		if(next != null) {
			next.update(dependency);
		}
		return true;
	}


	public boolean remove(WedgeAttribute wedge) {

		WedgeEntry wedgeEntry = (WedgeEntry)wedges.get(wedge.getIdentifier());
		if(wedgeEntry == null) {
			return false;
		}
		
		// Remove all facets of this wedge
		String[] facetIds = wedgeEntry.getFacetIdArray();
		for(int i=0; i < facetIds.length; i++) {
			remove(getFacetAttribute(facetIds[i]));
		}
		
		wedges.remove(wedgeEntry.getWedge().getIdentifier());
		if (meemStructureListener != null) {
			meemStructureListener.wedgeAttributeRemoved(wedge);
		}
		
		if(next != null) {
			next.remove(wedge);
		}
		return true;
	}
	
	public boolean remove(FacetAttribute facet) {

		FacetEntry facetEntry = (FacetEntry)facets.get(facet.getIdentifier());
		if(facetEntry == null) {
			return false;
		}
		WedgeEntry wedgeEntry = (WedgeEntry)wedges.get(facetEntry.getWedgeId());
		if(wedgeEntry != null) {
			wedgeEntry.removeFacetId(facetEntry.getFacet().getIdentifier());
		}
		facets.remove(facetEntry.getFacet().getIdentifier());
		if (meemStructureListener != null) {
			meemStructureListener.facetAttributeRemoved(facet);
		}

		if(next != null) {
			next.remove(facet);
		}
		return true;
	}
	
	/**
	 * 
	 */
	public boolean remove(DependencyAttribute dependency) {
		if (DEBUG) {
			LogTools.info(logger, "removing dependency: " + dependency);
		}
		
/* TODO remove this section  */
		DependencyEntry dependencyEntry = (DependencyEntry)getDependencies().get(dependency.getKey());
		if(dependencyEntry == null) {
			// still notify listener
			if (meemStructureListener != null) {
				meemStructureListener.dependencyAttributeRemoved(dependency);
			}
			return false;
		}
		
		FacetEntry facetEntry =  (FacetEntry)facets.get(dependencyEntry.getFacetId());
		if(facetEntry != null) {
			facetEntry.setDependencyId(null);
		}
		getDependencies().remove(dependencyEntry.getDependency().getKey());
/* end of seciton to remove */
		
		if (meemStructureListener != null) {
			meemStructureListener.dependencyAttributeRemoved(dependency);
		}

		if(next != null) {
			next.remove(dependency);
		}
		return true;
	}

	public DependencyAttribute getDependencyAttribute(Serializable dependencyId) {
		if (DEBUG) {
			LogTools.info(logger, "getDependencyAttribute: " + dependencyId);
		}
		
		DependencyEntry entry = (DependencyEntry)getDependencies().get(dependencyId);
		if(entry == null) {
			if (DEBUG) {
				LogTools.info(logger, "could not located dependencyAttribute for " + dependencyId);
			}
			return null;
		}
		return (DependencyAttribute)entry.getDependency().clone();
	}

	public Collection<Serializable> getDependencyAttributeKeys() {
		if (DEBUG) {
			LogTools.info(logger, "getDependencyAttributeKeys");
		}
		
		return getDependencies().keySet();
	}

	/**
	 * 
	 */
	public Serializable getDependencyKeyFromFacetKey(String facetId) {
		
		FacetEntry entry = facets.get(facetId);
		if(entry == null) {
			return null;
		}
		return entry.getDependencyId();
	}

	/**
	 * 
	 */
	public FacetAttribute getFacetAttribute(String facetId) {

		FacetEntry entry = facets.get(facetId);
		if(entry == null) {
			return null;
		}
		return (FacetAttribute)entry.getFacet().clone();
	}

	/**
	 * 
	 */
	public Collection<String> getFacetAttributeKeys(Serializable wedgeId) {

		WedgeEntry entry = wedges.get(wedgeId);
		if(entry == null) {
			return Collections.EMPTY_SET;
		}
		return entry.getFacetIds();
	}

	public String getFacetKeyFromDependencyKey(Serializable dependencyId) {

		DependencyEntry entry = getDependencies().get(dependencyId);
		if(entry == null) {
			return null;
		}
		return entry.getFacetId();
	}

	/**
	 * 
	 */
	public WedgeAttribute getWedgeAttribute(Serializable wedgeId) {
		WedgeEntry entry = (WedgeEntry)wedges.get(wedgeId);
		if(entry == null) {
			return null;
		}
		return entry.getWedge();
	}

	/**
	 * 
	 */
	public Collection<Serializable> getWedgeAttributeKeys() {
		return wedges.keySet();
	}

	/* ----------------- private members ---------------------- */
	
	private Map<Serializable, DependencyEntry> getDependencies() {
		if (dependencies == null) {
			dependencies = new HashMap<Serializable, DependencyEntry>();
		}
		return dependencies;
	}
	
	/* --------------- inner classes -------------------- */
	
	
	/**
	 * <code>WedgeEntry</code> is an internal class used in 
	 * <code>MeemStructureImpl</code> to maintain wedge entry.
	 * <p>
	 * @author Kin Wong
	 */
	private final class WedgeEntry implements Serializable {
		private static final long serialVersionUID = 19292030340L;
		
		private WedgeAttribute wedge;
		private final Set<String> facetIds = new HashSet<String>();
			
		public WedgeEntry(WedgeAttribute wedge) {
			updateWedge(wedge);
		}
			
		public WedgeAttribute getWedge() {
			return wedge;
		}

		public void updateWedge(WedgeAttribute wedge) {
			this.wedge = (WedgeAttribute)wedge.clone();
		}
			
		public void addFacetId(String facetId) {
			facetIds.add(facetId);
		}
		
		public String[] getFacetIdArray() {
			return facetIds.toArray(new String[]{});
		}
			
		public boolean removeFacetId(String facetId) {
			if(facetIds == null) {
				return false;
			}
			return facetIds.remove(facetId);
		}
			
		public Collection<String> getFacetIds() {
			return facetIds;
		}
	}
	
	/**
	 * <code>FacetEntry</code> is an internal class used in 
	 * <code>MeemStructureImpl</code> to maintain facet entry.
	 * <p>
	 * @author Kin Wong
	 */
	private final class FacetEntry {
		private FacetAttribute facet;
		private Serializable wedgeId;
		private Serializable dependencyId;
		
		public FacetEntry(Serializable wedgeId, FacetAttribute facet) {
			this.wedgeId = wedgeId;
			updateFacet(facet);
		}
		
		public FacetAttribute getFacet() {
//			return (FacetAttribute)facet.clone();
			return facet;
		}
		
		public void updateFacet(FacetAttribute facet) {
			this.facet = (FacetAttribute)facet.clone();
		}
		
		public Serializable getWedgeId() {
			return wedgeId;
		}
		
		public Serializable getDependencyId() {
			return dependencyId;
		}
		
		public void setDependencyId(Serializable dependencyId) {
			this.dependencyId = dependencyId;
		}
	}
		
	/**
	 * <code>DependencyEntry</code> is an internal class used in 
	 * <code>MeemStructureImpl</code> to maintain dependency entry.
	 * <p>
	 * @author Kin Wong
	 */
	private final class DependencyEntry {
		private DependencyAttribute dependency;
		private String facetId;
		
		public DependencyEntry(String facetId, DependencyAttribute dependency) {
			setFacetId(facetId);
			updateDependency(dependency);
		}
		
		public DependencyAttribute getDependency() {
//			return (DependencyAttribute)dependency.clone();
			return dependency;
		}
		
		public String getFacetId() {
			return facetId;
		}
		
		public void setFacetId(String facetId) {
			this.facetId = facetId;
		}
	
		public void updateDependency(DependencyAttribute dependency) {
			this.dependency = (DependencyAttribute)dependency.clone();
		}
	}
}