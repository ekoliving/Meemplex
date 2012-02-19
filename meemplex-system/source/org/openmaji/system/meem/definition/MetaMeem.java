/*
 * @(#)MetaMeem.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - Consider renaming all ...
 *     "add*Attribute()"    to "*AttributeAdded()"
 *     "update*Attribute()" to "*AttributeChanged()"
 *     "remove*Attribute()" to "*AttributeRemoved()"
 *   ,,, to be consistent across *all* Maji Facets.
 */

package org.openmaji.system.meem.definition;

import java.io.Serializable;

import org.openmaji.meem.Facet;
import org.openmaji.meem.definition.DependencyAttribute;
import org.openmaji.meem.definition.FacetAttribute;
import org.openmaji.meem.definition.MeemAttribute;
import org.openmaji.meem.definition.WedgeAttribute;

/**
 * <p>
 * The MetaMeem Facet is used to both manipulate a Meem's Definition and
 * Attributes, as well as allow others to be informed about changes to a
 * Meem's Definition and Attributes.  It is a symmetrical Facet, that is
 * intended to be used on both the Meem client-side and Meem provider-side.
 * </p>
 * <p>
 * Typically, users of this Facet will have a proxy that listens to this
 * interface and populates the MeemStructure accordingly.  Also, as the
 * local MeemStructure is changed, this Facet can be used to instigate
 * change in a remote Meem's Structure and Attributes.
 * </p>
 * <p>
 * The MetaMeem Facet has been specifically designed for applications
 * such as dynamic and distributed Integrated Development Environment
 * tools, such as InterMajik, that can operate asynchronously with the
 * Maji system.
 * It is an intentional design decision that the granularity for changes
 * occurs just at the Meem, Wedge, Facet and Dependency levels.
 * </p>
 * <p>
 * The use of "keys" by the MetaMeem definition of specific methods
 * allows efficient use over a remote interface (network).  These
 * keys can be used as an index into a MeemStructure, that permits
 * finer granularity of the Attributes transferred via this interface.
 * </p>
 * <p>
 * In constrast, the creation or persistence of a Meem's Definition is
 * usually represented via the Meem, Wedge and Facet Definition object
 * structures, that combine the Meem's complete hierarchy and Attributes.
 * </p>
 * <p>
 * Note: Implementation thread safe = Not applicable
 * </p>
 * @author  Andy Gelme
 * @author  Kin  Wong
 * @version 1.0
 * @see org.openmaji.meem.Facet
 * @see org.openmaji.meem.Meem
 * @see org.openmaji.meem.definition.DependencyAttribute
 * @see org.openmaji.meem.definition.FacetAttribute
 * @see org.openmaji.meem.definition.MeemAttribute
 * @see org.openmaji.system.meem.definition.MeemStructure
 * @see org.openmaji.meem.definition.WedgeAttribute
 */

public interface MetaMeem extends Facet {

  void updateMeemAttribute(MeemAttribute meemAttribute);

  void addWedgeAttribute(WedgeAttribute wedgeAttribute);

  void updateWedgeAttribute(WedgeAttribute wedgeAttribute);

  void removeWedgeAttribute(Serializable wedgeKey);

  void addFacetAttribute(Serializable wedgeKey, FacetAttribute facetAttribute);

  void updateFacetAttribute(FacetAttribute facetAttribute);

  void removeFacetAttribute(String facetKey);

  /**
   * 
   * @param facetKey
   * @param dependencyAttribute
   * @deprecated Use the DependencyHandler wedge
   */
  void addDependencyAttribute(String facetKey, DependencyAttribute dependencyAttribute);

  /**
   * 
   * @param dependencyAttribute
   * @deprecated Use the DependencyHandler wedge
   */
  void updateDependencyAttribute(DependencyAttribute dependencyAttribute);

  /**
   * 
   * @param dependencyKey
   * @deprecated Use the DependencyHandler wedge
   */
  void removeDependencyAttribute(Serializable dependencyKey);
  
	/**
	 * Nested class for service provider.
	 * 
	 * @see org.openmaji.spi.MajiSPI
	 */
  public class spi {
    public static String getIdentifier() {
      return("metaMeem");
    };
  }
}