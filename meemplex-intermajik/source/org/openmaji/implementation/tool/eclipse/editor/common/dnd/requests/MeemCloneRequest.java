/*
 * @(#)MeemCloneRequest.java
 * Created on 29/04/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.common.dnd.requests;

import org.openmaji.implementation.intermajik.model.ViewModeConstants;
import org.openmaji.implementation.tool.eclipse.client.*;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.Meem;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.MeemModelFactory;
import org.openmaji.implementation.tool.eclipse.plugin.MajiPlugin;
import org.openmaji.implementation.tool.eclipse.ui.dnd.NamedMeem;
import org.openmaji.meem.wedge.lifecycle.LifeCycleState;



/**
 * <code>MeemCloneRequest</code>.
 * <p>
 * @author mg
 */
public class MeemCloneRequest extends NamedMeemRequest {
	public static final String REQ_MEEM_CLONE = "Meem Clone Request";

	public MeemCloneRequest() {
		super(REQ_MEEM_CLONE);
	}

	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.common.dnd.requests.NamedMeemRequest#createNewObject(org.openmaji.implementation.tool.eclipse.ui.dnd.NamedMeem)
	 */
	protected Object createNewObject(NamedMeem namedMeem) {
		org.openmaji.meem.Meem lcmMeem = null;
		MeemClientProxy worksheetProxy = MajiPlugin.getDefault().getActiveWorksheetProxy();
		if (worksheetProxy != null) {
			LifeCycleManagementClientProxy proxy = worksheetProxy.getLifeCycleManagementClient();
			
			while (!proxy.isContentInitialized()) {
				try {
					Thread.sleep(250);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			lcmMeem = (org.openmaji.meem.Meem) proxy.getParentLifeCycleManager();
		}
		
		final MeemClientProxy proxy = 
			InterMajikClientProxyFactory.getInstance().clone(namedMeem.getMeemPath(), 
			LifeCycleState.LOADED,
			lcmMeem);
		
		Runnable runnable = new Runnable() {
			public void run() {
				LifeCycleLimitProxy lifeCycleLimitProxy = proxy.getLifeCycleLimit();
				while (!lifeCycleLimitProxy.isConnected()) {
					try {
						Thread.sleep(250);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				lifeCycleLimitProxy.limitLifeCycleState(LifeCycleState.READY);
			}
		};
		
		new Thread(runnable).start();
		
		Meem meem = MeemModelFactory.create(proxy);
		meem.setCollapse(false);
		meem.setName(namedMeem.getName());
		meem.setViewMode(ViewModeConstants.VIEW_MODE_DETAILED);
		return meem;
	}
}

