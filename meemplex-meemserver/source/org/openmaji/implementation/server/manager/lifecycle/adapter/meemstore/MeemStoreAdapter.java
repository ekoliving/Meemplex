/*
 * @(#)MeemStoreAdapter.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.server.manager.lifecycle.adapter.meemstore;

import org.openmaji.meem.MeemPath;
import org.openmaji.system.space.meemstore.MeemStore;


/**
 * <p>
 * ...
 * </p>
 * @author  mg
 * @version 1.0
 */
public interface MeemStoreAdapter extends MeemStore {

	public void load(MeemPath meemPath);
}
