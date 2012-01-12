/*
 * @(#)MeemStructure.java
 * Created on 7/07/2003
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

/* ToDo:
 * - Consider renaming all ...
 *     "add*Attribute()"    to "*AttributeAdded()"
 *     "update*Attribute()" to "*AttributeChanged()"
 *     "remove*Attribute()" to "*AttributeRemoved()"
 *   ,,, to be consistent across *all* Maji Facets.
 *
 * - Methods for add(), update() and remove() should throw an
 *   IllegalArgumentException, rather than return a boolean.
 *   This will allow for better diagnostic messages !
 */

package org.openmaji.system.meem.definition;

import java.io.Serializable;
import java.util.Collection;

import org.openmaji.meem.definition.DependencyAttribute;
import org.openmaji.meem.definition.FacetAttribute;
import org.openmaji.meem.definition.MeemAttribute;
import org.openmaji.meem.definition.WedgeAttribute;
import org.openmaji.spi.MajiSPI;

/**
 * <p>
 * <code>MeemStructure</code> defines the contract to construct and access the 
 * inter-relationships amongst definitions, including
 * <code>MeemAttribute</code>, <code>WedgeAttribute</code>, 
 * <code>FacetAttribute</code> and <code>DependencyAttribute</code>.
 * </p>
 * <p>
 * <code>MeemStructure</code> also allows retrieval of definition by its id. 
 * The scope of uniqueness of id is defined by the id implementation used in
 * the definition implementation.
 * </p>
 * @see org.openmaji.meem.definition.MeemAttribute
 * @see org.openmaji.meem.definition.WedgeAttribute
 * @see org.openmaji.meem.definition.FacetAttribute
 * @see org.openmaji.meem.definition.DependencyAttribute
 * @see org.openmaji.system.meem.definition.MeemStructureListener
 * @author Kin Wong
 */
public interface MeemStructure {

	void setMeemStructureListener(MeemStructureListener meemStructureListener);

	/**
	 * Returns the <code>MeemAttribute</code> associates with the 
	 * <code>MeemStructure</code> implementation.
	 * <p>
	 * @return MeemAttribute The <code>MeemAttribute</code> associates with 
	 * the <code>MeemStructure</code> implementation.
	 */
	MeemAttribute getMeemAttribute();
	
	/**
	 * Returns the <code>WedgeAttribute</code> of given wedge key.
	 * <p>
	 * @param wedgeKey The wedge key of the retrieving 
	 * <code>WedgeAttribute</code>.
	 * @return WedgeAttribute The <code>WedgeAttribute</code> of the given 
	 * wedge key.
	 */
	WedgeAttribute getWedgeAttribute(Serializable wedgeKey);
	
	/**
	 * Returns the <code>FacetAttribute</code> of given facet key.
	 *
	 * @param facetkey The facet key of the retrieving <code>FacetAttribute</code>.
	 * @return FacetAttribute The <code>FacetAttribute</code> of the given facet key.
	 */
	FacetAttribute getFacetAttribute(String facetkey);
	
	/**
	 * Returns the <code>DependencyAttribute</code> of given dependency key.
	 *
	 * @param dependencykey The key of the retrieving dependency.
	 * @return DependencyAttribute The <code>DependencyAttribute</code> of the
	 * given dependency key.
	 * @deprecated Dependencies should not be part of MeemStructure
	 */
	DependencyAttribute getDependencyAttribute(Serializable dependencykey);
	
	/**
	 * Returns all <code>WedgeAttribute</code> keys associate with the 
	 * <code>MeemStructure</code> implementation.
	 *
	 * @return Collection A Collection that can be used to iterate through all 
	 * keys of <code>WedgeAttribute</code>s.
	 */
	Collection<Serializable> getWedgeAttributeKeys();
	
	/**
	 * Returns all <code>FacetAttribute</code> keys associate with the
	 * <code>MeemStructure</code> implementation of the given wedge key.
	 *
	 * @param wedgeKey The key of the wedge all <code>FacetAttribute</code>s 
	 * belong to.
	 * @return Collection A Collection that can be used to iteratre through all
	 * keys of <code>FacetAttribute</code>s.
	 */
	Collection<String> getFacetAttributeKeys(Serializable wedgeKey);
	
	/**
	 * Returns all <code>DependencyAttribute</code> keys associate with the
	 * <code>MeemStructure</code> implementation.
	 *
	 * @return Collection A Collection that can be used to iterate through all 
	 * keys of <code>DependencyAttribute</code>s.
	 * @deprecated Dependencies should not be part of MeemStructure
	 */
	Collection<Serializable> getDependencyAttributeKeys();
	
	/**
	 * Given the key of a dependency, returns the key of the facet asscoiates 
	 * with it.
	 *
	 * @param dependencyKey The id of the dependency of the facet.
	 * @return Serializable The key of the facet associates with the dependency.
	 * @deprecated Dependencies should not be part of MeemStructure
	 */	
	String getFacetKeyFromDependencyKey(Serializable dependencyKey);
	
	/**
	 * Given the key of a facet, returns the key of the dependency asscoiates 
	 * with it.
	 *
	 * @param facetKey The key of the facet of the dependency.
	 * @return Serializable The key of the dependency asscoiates with the facet.
	 * @deprecated Dependencies should not be part of MeemStructure
	 */
	Serializable getDependencyKeyFromFacetKey(String facetKey);
	
	/**
	 * Assigns the <code>MeemAttribute</code> to the 
	 * <code>MeemStructure</code> implementation.
   * 
	 * @param meem The <code>MeemAttribute</code> of the
	 * <code>MeemStructure</code> implementation.
	 */		
	boolean set(MeemAttribute meem);
	
	/**
	 * Add a WedgeAttribute
	 * @param wedge
	 */
	boolean add(WedgeAttribute wedge);
	
	/**
	 * 
	 * @param Wedge
	 * @param facet
	 */
	boolean add(WedgeAttribute Wedge, FacetAttribute facet);
	
	/**
	 * 
	 * @param facet
	 * @param dependency
	 * @deprecated Dependencies should not be part of MeemStructure
	 */
	boolean add(FacetAttribute facet, DependencyAttribute dependency);
	
	boolean update(MeemAttribute meem);
	
	boolean update(WedgeAttribute wedge);
	
	boolean update(FacetAttribute facetType);

	/**
	 * 
	 * @param dependency
	 * @deprecated Dependencies should not be part of MeemStructure
	 */
	boolean update(DependencyAttribute dependency);

	boolean remove(WedgeAttribute wedge);
	
	boolean remove(FacetAttribute facet);
	
	/**
	 * 
	 * @param dependency
	 * @deprecated Dependencies should not be part of MeemStructure
	 */
	boolean remove(DependencyAttribute dependency);

	/**
	 * Nested class for service provider.
	 * 
	 * @see org.openmaji.spi.MajiSPI
	 */
      public class spi {
        public static MeemStructure create() {
          return((MeemStructure) MajiSPI.provider().create(MeemStructure.class));
        }
      }
}
