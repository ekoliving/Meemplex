/*
 * @(#)MeemSpaceClient.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.tool.eclipse.browser.meemstore;

import java.util.Set;

import org.openmaji.meem.MeemPath;
import org.openmaji.system.space.meemstore.MeemStoreClient;


/**
 * @author mg
 * Created on 21/01/2003
 */
public class MeemStoreBrowserClient implements MeemStoreClient {

	private Set meemPaths;
	private MeemStoreContentProvider provider;

	public MeemStoreBrowserClient(Set meemPaths) {
		this.meemPaths = meemPaths;
	}

	public void setParent(MeemStoreContentProvider provider) {
		this.provider = provider;
	}

	public void meemDestroyed(MeemPath meemPath) {
		meemPaths.remove(meemPath);
		provider.update();
	}

	public void meemStored(MeemPath meemPath) {
		meemPaths.add(meemPath);
		provider.update();
	}

}
