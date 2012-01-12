/*
 * @(#)MeemDefinitionProvider.java
 *
 * Copyright 2004 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */
package org.openmaji.meem.definition;

/**
 * <p>
 * Interface that a MeemDefinition provider must follow.
 * </p>
 * @author  Andy Gelme
 * @version 1.0
 */
public interface MeemDefinitionProvider {

  /**
   * Return a MeemDefinition.
   * 
   * @return the MeemDefinition.
   */
  public MeemDefinition getMeemDefinition();
}