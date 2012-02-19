/*
 * @(#)Latte.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.tool.eclipse.util.log;

import java.util.Properties;

import org.swzoo.log2.component.util.ReentrantCounterSource;
import org.swzoo.log2.topology.common.AbstractTopology;
import org.swzoo.log2.topology.common.BackEnd;

/**
 * Maji logging topology for logging to eclipse
 * @author  mg
 * @version 1.0
 */
public class Latte extends AbstractTopology {
	private static final long serialVersionUID = 6424227717462161145L;

  /** Name for this topology. */
  static final String TOPOLOGY_NAME = "Latte";

  /** Our fully qualified class name. */
  public static final String CLASS_NAME = Latte.class.getName();

  /**
   * Properties prefix for "LATTE"
   */
  public static final String PREFIX_LATTE = CLASS_NAME;

  public Latte() {
//  System.out.println("latte started");
  }

  public Latte(Properties config) {
    super(config);
//  System.out.println("latte config started");
  }

  /* (non-Javadoc)
   * @see org.swzoo.log2.topology.common.AbstractTopology#createBackEnd(org.swzoo.log2.component.util.ReentrantCounterSource)
   */
  protected BackEnd createBackEnd(ReentrantCounterSource counterSource) {
    return new LatteBackEnd(counterSource);
  }

  /* (non-Javadoc)
   * @see org.swzoo.log2.topology.common.AbstractTopology#getConfigurationKeyPrefix()
   */
  public String getConfigurationKeyPrefix() {
    return PREFIX_LATTE;
  }

  /* (non-Javadoc)
   * @see org.swzoo.log2.core.LogProvider#getName()
   */
  public String getName() {
    return TOPOLOGY_NAME;
  }

  /* (non-Javadoc)
   * @see org.swzoo.log2.topology.common.AbstractTopology#getPackageName()
   */
  protected String getPackageName() {
    return getClass().getPackage().getName();
  }
}
