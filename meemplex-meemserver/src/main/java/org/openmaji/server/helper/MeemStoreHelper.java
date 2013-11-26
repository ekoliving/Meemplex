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
import org.openmaji.server.utility.PigeonHole;
import org.openmaji.system.gateway.AsyncCallback;
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
			meemStore = ReferenceHelper.getTarget(meem, "meemStore", MeemStore.class);
		}
		return meemStore;
	}

	public void getMeemStore(AsyncCallback<MeemStore> callback) {
		if (meemStore == null) {
			final PigeonHole<MeemStore> promise = new PigeonHole<MeemStore>();
			Meem meem = EssentialMeemHelper.getEssentialMeem(MeemStore.spi.getIdentifier());
			ReferenceHelper.getTarget(meem, "meemStore", MeemStore.class, new AsyncCallback<MeemStore>() {
				public void result(MeemStore result) {
					MeemStoreHelper.meemStore = result;
					promise.put(result);
				}
				public void exception(Exception e) {
					promise.exception(e);
				}
			});
			promise.get(callback);
		}
		else {
			callback.result(meemStore);
		}
	}
}
