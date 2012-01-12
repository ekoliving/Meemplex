/*
 * @(#)LifeCycleManagerClientImpl.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.tool.eclipse.browser.lifecyclemanager;

import java.util.*;

import org.eclipse.swt.widgets.Display;
import org.openmaji.implementation.server.manager.gateway.GatewayManager;
import org.openmaji.implementation.tool.eclipse.client.SWTClientSynchronizer;
import org.openmaji.implementation.tool.eclipse.client.security.SecurityManager;
import org.openmaji.implementation.tool.eclipse.util.MeemComparator;
import org.openmaji.meem.Meem;
import org.openmaji.meem.MeemPath;
import org.openmaji.meem.wedge.reference.Reference;
import org.openmaji.system.manager.lifecycle.LifeCycleManager;
import org.openmaji.system.manager.lifecycle.LifeCycleManagerClient;
import org.openmaji.system.utility.MeemUtility;




/**
 * <p>
 * ...
 * </p>
 * @author  mg
 * @version 1.0
 */
public class LifeCycleManagerClientImpl implements LifeCycleManagerClient {
	private final Set entries = new TreeSet(new MeemComparator());
	private final Map lcms = new TreeMap(new MeemLCMComparator());

	private final Reference lcmClientReference;
	private final Meem lcmMeem;
	private final LifeCycleManagersContentProvider provider;

	private final MeemPath meemPath;

	public LifeCycleManagerClientImpl(LifeCycleManagersContentProvider provider, Meem meemLCM) {
		this.provider = provider;
		this.meemPath = meemLCM.getMeemPath();
		this.lcmMeem = meemLCM;
		this.lcmClientReference = Reference.spi.create("lifeCycleManagerClient",
			SecurityManager.getInstance().getGateway().getTargetFor(this, LifeCycleManagerClient.class), true);

		lcmMeem.addOutboundReference(lcmClientReference, false);
	}
	
	/**
	 * @see org.openmaji.system.manager.lifecycle.LifeCycleManagerClient#meemCreated(org.openmaji.meem.Meem, java.lang.String)
	 */
	public void meemCreated(final Meem meem, final String identifier) {
		Runnable runnable = new Runnable() {
			public void run() {

				GatewayManager.spi.create();
				GatewayManager gw;
				if (meem.getMeemPath() != meemPath) {
					if (MeemUtility.spi.get().isA(meem, LifeCycleManager.class)) {
						lcms.put(meem, new LifeCycleManagerClientImpl(provider, meem));
					}
					else {
						entries.add(meem);
					}
				}

				provider.update();
			}
		};

		SWTClientSynchronizer.getDefault().execute(runnable);
	}
	
	/**
	 * @see org.openmaji.system.manager.lifecycle.LifeCycleManagerClient#meemDestroyed(org.openmaji.meem.Meem)
	 */
	public void meemDestroyed(final Meem meem) {
		Runnable runnable = new Runnable() {
			public void run() {
				if (!entries.remove(meem)) {
					LifeCycleManagerClientImpl LCMi = (LifeCycleManagerClientImpl) lcms.remove(meem);

					if (LCMi != null) {
						LCMi.dispose();
					}
				}
				provider.update();
			}
		};

		SWTClientSynchronizer.getDefault().execute(runnable);
	}

	/**
	 */
	public void meemTransferred(Meem meem, LifeCycleManager targetLifeCycleManager) {
		// -mg- Implement this
	}

	public Object[] getChildren() {
		Set tempSet = new LinkedHashSet();
		tempSet.addAll(lcms.values());
		tempSet.addAll(entries);
		return tempSet.toArray();
	}

	public boolean hasChildren() {
		return (entries.size() > 0) || (lcms.size() > 0);
	}

	public void dispose() {
		if (lcmMeem != null && lcmClientReference != null) {
			lcmMeem.removeOutboundReference(lcmClientReference);
		}
	}

	public MeemPath getMeemPath() {
		return meemPath;
	}
}
