/*
 * @(#)MeemStructureListener.java
 *
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
 */

package org.openmaji.system.meem.definition;

import org.openmaji.meem.definition.DependencyAttribute;
import org.openmaji.meem.definition.FacetAttribute;
import org.openmaji.meem.definition.MeemAttribute;
import org.openmaji.meem.definition.WedgeAttribute;

/**
 * Reports changes on a <code>MeemStructure</code>
 * 
 * @author  Andy Gelme
 * @version 1.0
 * @see org.openmaji.meem.definition.DependencyAttribute
 * @see org.openmaji.meem.definition.FacetAttribute
 * @see org.openmaji.meem.definition.MeemAttribute
 * @see org.openmaji.meem.definition.WedgeAttribute
 */

public interface MeemStructureListener {

  void meemAttributeChanged(
    MeemAttribute meemAttribute);

  void wedgeAttributeAdded(
    WedgeAttribute wedgeAttribute);

  void wedgeAttributeChanged(
    WedgeAttribute wedgeAttribute);

  void wedgeAttributeRemoved(
    WedgeAttribute wedgeAttribute);

  void facetAttributeAdded(
    WedgeAttribute wedgeAttribute,
    FacetAttribute facetAttribute);

  void facetAttributeChanged(
    FacetAttribute facetAttribute);

  void facetAttributeRemoved(
    FacetAttribute facetAttribute);

  /**
   * @deprecated Dependencies should not be in MeemStructure 
   */
  void dependencyAttributeAdded(
    String      facetId,
    DependencyAttribute dependencyAttribute);

  /**
   * @deprecated Dependencies should not be in MeemStructure 
   */
  void dependencyAttributeChanged(
    DependencyAttribute dependencyAttribute);

  /**
   * @deprecated Dependencies should not be in MeemStructure 
   */
  void dependencyAttributeRemoved(
    DependencyAttribute dependencyAttribute);

}
