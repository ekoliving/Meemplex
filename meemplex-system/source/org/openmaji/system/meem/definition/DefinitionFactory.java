/*
 * @(#)DefinitionFactory.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */

package org.openmaji.system.meem.definition;

import java.util.Iterator;

import org.openmaji.meem.definition.MeemDefinition;
import org.openmaji.meem.definition.WedgeDefinition;
import org.openmaji.spi.MajiSPI;

/**
 * <p>
 * A DefinitionFactory provides a convenient means of creating complete
 * definitions for Meems or partial definitions for Wedges.
 * </p>
 * <p>
 * This is particularly useful during the MeemServer bootstrap, because
 * the essential Meems need to be defined and created, and yet, there is
 * no access to a MeemStore (also a Meem), which is the usual place for
 * locating MeemDefinitions.  The DefinitionFactory can also be used to
 * define the system WedgeDefinitions.
 * </p>
 * <p>
 * Note: Implementation thread safe = Not applicable
 * </p>
 * @author  Andy Gelme
 * @version 1.0
 * @see org.openmaji.meem.Meem
 * @see org.openmaji.meem.definition.MeemDefinition
 * @see org.openmaji.meem.definition.WedgeDefinition
 */

public interface DefinitionFactory {

  /**
   * Create a MeemDefinition that is based on the Wedge implementation class
   * that is associated with the Meem's identifier.
   *
   * @param     meemIdentifier Identifier for the Meem
   * @return    MeemDefinition for the Meem specified by the identifier
   * @exception IllegalArgumentException Unknown Meem identifier
   */

  public MeemDefinition createMeemDefinition(
    String meemIdentifier
  );

  /**
   * Create a MeemDefinition that is based on the Wedge implementation class.
   *
   * @param  meemIdentifier     Identifier for the Meem
   * @param  wedgeSpecification Class to be inspected for the MeemDefinition
   * @return MeemDefinition for the Meem specified by the Class
   */

  public MeemDefinition createMeemDefinition(
    String meemIdentifier,
    Class<?>  wedgeSpecification
  );

  /**
   * <p>
   * Create a MeemDefinition that is composed of all the Wedges that are
   * specified by a list of either known Wedge identifiers or Wedge
   * implementation classes.
   * </p>
   * <p>
   * The Wedge iterator can provide a mixture of Wedge identifiers and/or
   * Wedge implementation classes.  This provides a reasonable amount of
   * flexibility for creating MeemDefinitions and, hopefully, isn't too
   * messy.
   * </p>
   * @param     wedgeIterator Specifies which Wedges to define
   * @return    MeemDefinition composed of the specified Wedge identifiers
   * @exception IllegalArgumentException Unknown Wedge identifier
   */

  public MeemDefinition createMeemDefinition(
    Iterator<?> wedgeIterator
  );

  /**
   * Create a WedgeDefinition that is based on the Wedge implementation class
   * that is associated with the Wedge's identifier.
   *
   * @param     wedgeIdentifier Identifier for the Wedge
   * @return    WedgeDefinition for the Wedge specified by the identifier
   * @exception IllegalArgumentException Unknown Wedge identifier
   */

  public WedgeDefinition createWedgeDefinition(
    String wedgeIdentifier
  );

  /**
   * Create a WedgeDefinition that is based on the Wedge implementation class.
   *
   * @param  wedgeSpecification Class to be inspected for the WedgeDefinition
   * @return WedgeDefinition for the Wedge specified by the Class
   */

  public WedgeDefinition createWedgeDefinition(
    Class<?> wedgeSpecification
  );

	/**
	 * Nested class for service provider.
	 * 
	 * @see org.openmaji.spi.MajiSPI
	 */
  public class spi {
    public static DefinitionFactory create() {
      return((DefinitionFactory) MajiSPI.provider().create(DefinitionFactory.class));
    }
  }
}
