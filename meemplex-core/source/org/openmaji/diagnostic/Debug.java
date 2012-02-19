/*
 * Copyright 2006 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.diagnostic;

import org.openmaji.meem.Facet;

/**
 * The Debug Facet is used to signal changes in the debug value of a Meem.
 * This Facet may be used symmetrically as both the out-bound and in-bound
 * interfaces.
 * 
 * @author  Christos Kakris
 */

public interface Debug extends Facet
{
	/**
	 * Default in-bound Debug Facet identifier
	 */
	public final static String DEFAULT_INBOUND_FACET_IDENTIFER = "debugInput";

	/**
	 * Default out-bound Debug Facet identifier
	 */
	public final static String DEFAULT_OUTBOUND_FACET_IDENTIFIER = "debugOutput";
	
  /**
   * Indicate that the debug level should change or has changed.
   * 
   * @param level Changed debug level
   */

  public void debugLevelChanged(int level);

}
