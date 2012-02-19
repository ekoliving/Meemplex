/*
 * @(#)RemoteMeemClient.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */

package org.openmaji.system.meem.wedge.remote;

import org.openmaji.meem.Facet;
import org.openmaji.meem.Meem;
import org.openmaji.system.meem.FacetItem;


/**
 * <p>
 * The RemoteMeemClient ...
 * </p>
 * <p>
 * Note: Implementation thread safe = Not applicable
 * </p>
 * @author  Andy Gelme
 * @version 1.0
 * @see org.openmaji.meem.Facet
 * @see org.openmaji.system.meem.wedge.remote.RemoteMeem
 */

public interface RemoteMeemClient extends Facet {

  public void remoteMeemChanged(
    Meem        meem,
    RemoteMeem  remoteMeem,
    FacetItem[] facetItems);
}
