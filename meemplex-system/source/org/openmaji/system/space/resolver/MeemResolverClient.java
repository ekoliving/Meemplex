/*
 * @(#)MeemResolverClient.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.system.space.resolver;

import org.openmaji.meem.Facet;
import org.openmaji.meem.Meem;
import org.openmaji.meem.MeemPath;

/**
 * <p>
 * This <code>Facet</code> is used to monitor which <code>Meem</code>, if any is located at a
 * given <code>MeemPath</code>. Resolver meems typically have an outbound facet of this type, which
 * clients connect to, using a filter of type <code>ExactMatchFilter</code> specifying a
 * <code>MeemPath</code> to query and/or monitor.
 * </p>
 * @author  mg
 * @version 1.0
 * @see org.openmaji.meem.filter.ExactMatchFilter
 */
public interface MeemResolverClient extends Facet {

	/**
	 * @param meemPath The <code>MeemPath</code> which was being monitored.
	 * @param meem The <code>Meem</code> that is at <code>meemPath</code> or null if not resolved.
	 */
	public void meemResolved(MeemPath meemPath, Meem meem);
}
