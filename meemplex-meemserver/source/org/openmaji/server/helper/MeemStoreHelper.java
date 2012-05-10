/*
 * @(#)MeemStoreHelper.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.server.helper;

import org.openmaji.meem.Meem;
import org.openmaji.system.space.meemstore.MeemStore;


/**
 * @author mg
 * Created on 20/01/2003
 */
public class MeemStoreHelper {
	private static MeemStore meemStore = null;
	private static MeemStoreHelper instance = new MeemStoreHelper();

	private MeemStoreHelper() {
	}

	public static MeemStoreHelper getInstance() {
		return instance;
	}

	public MeemStore getMeemStore() {
		if (meemStore == null) {

			Meem meem = EssentialMeemHelper.getEssentialMeem(MeemStore.spi.getIdentifier());
			meemStore = (MeemStore) org.openmaji.server.helper.ReferenceHelper.getTarget(meem, "meemStore", MeemStore.class);
		}
		return meemStore;
	}

}
