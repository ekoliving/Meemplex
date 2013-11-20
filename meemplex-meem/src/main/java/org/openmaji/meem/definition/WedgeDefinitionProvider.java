/*
 * @(#)WedgeDefinitionProvider.java
 *
 * Copyright 2004 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
package org.openmaji.meem.definition;

/**
 * <p>
 * Interface that a wedge definition provider must conform to.
 * </p>
 * @author  Andy Gelme
 * @version 1.0
 */
public interface WedgeDefinitionProvider {

  /**
   * Return a wedge definition.
   * 
   * @return a WedgeDefinition.
   */
  public WedgeDefinition getWedgeDefinition();
}