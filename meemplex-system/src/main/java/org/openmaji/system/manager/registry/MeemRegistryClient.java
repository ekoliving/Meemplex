/*
 * @(#)MeemRegistryClient.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
package org.openmaji.system.manager.registry;

import org.openmaji.meem.Facet;
import org.openmaji.meem.Meem;

/**
 * <p>
 * Client interface for listening to events occuring in a Meem registry.
 * </p>
 * <p>
 * Note: Implementation thread safe = Not applicable
 * </p>
 * @author  Andy Gelme
 * @version 1.0
 * @see org.openmaji.meem.Facet
 * @see org.openmaji.meem.Meem
 * @see org.openmaji.system.manager.registry.MeemRegistry
 */
public interface MeemRegistryClient extends Facet {

  /**
   * Signal that a meem was registered.
   * 
   * @param meem the meem that was registered.
   */
  public void meemRegistered(
    Meem meem);

  /**
   * Signal that a meem was deregistered.
   * 
   * @param meem the meem that was deregistered.
   */
  public void meemDeregistered(
    Meem meem);
}
