/*
 * @(#)DeploymentDropAdapter.java
 * Created on 12/05/2004
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.browser.relationship.deployment.dnd;

import java.security.PrivilegedAction;

import javax.security.auth.Subject;


import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.dnd.TransferData;
import org.openmaji.implementation.tool.eclipse.browser.common.ViewerDropAdapter;
import org.openmaji.implementation.tool.eclipse.browser.relationship.deployment.nodes.SubsystemFactoryNode;
import org.openmaji.implementation.tool.eclipse.browser.relationship.deployment.nodes.SubsystemNode;
import org.openmaji.implementation.tool.eclipse.client.InterMajikClientProxyFactory;
import org.openmaji.implementation.tool.eclipse.client.LifeCycleManagerProxy;
import org.openmaji.implementation.tool.eclipse.client.MeemClientProxy;
import org.openmaji.implementation.tool.eclipse.client.security.SecurityManager;
import org.openmaji.implementation.tool.eclipse.ui.dnd.MeemNodeTransfer;
import org.openmaji.meem.Meem;
import org.openmaji.meem.MeemPath;
import org.openmaji.system.manager.lifecycle.LifeCycleManager;
import org.openmaji.system.manager.lifecycle.subsystem.Subsystem;


/**
 * <code>DeploymentDropAdapter</code>.
 * <p>
 * @author Kin Wong
 */
public class DeploymentDropAdapter extends ViewerDropAdapter {
	/**
	 * Constructs an instance of <code>DeploymentDropTargetListener</code>.
	 * <p>
	 * @param viewer
	 */
	public DeploymentDropAdapter(Viewer viewer) {
		super(viewer);
	}


	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.browser.common.ViewerDropAdapter#validateDrop(java.lang.Object, int, org.eclipse.swt.dnd.TransferData, java.lang.Object)
	 */
	public boolean validateDrop(Object target, int operation, TransferData transferType, Object data) {
		if(target == null) return false;
		
		if(target instanceof SubsystemNode) {
			SubsystemNode subsystem = (SubsystemNode)target;
			if(subsystem.getSubsystem().isReadOnly()) return false;
		
			if(MeemNodeTransfer.getInstance().isSupportedType(transferType))
			return isValid(subsystem, (TransferDrag[])data);
		}
		else
		if(target instanceof SubsystemFactoryNode) {
			SubsystemFactoryNode subsystemFactory = (SubsystemFactoryNode)target;
			if(subsystemFactory.getSubsystemFactory().isReadOnly()) return false;
			
			if(MeemNodeTransfer.getInstance().isSupportedType(transferType))
			return isValid(subsystemFactory, (TransferDrag[])data);
		}
	
		return false;
	}

	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.browser.common.ViewerDropAdapter#performDrop(org.eclipse.swt.dnd.TransferData, java.lang.Object)
	 */
	public boolean performDrop(TransferData transferType, Object data) {
		if(!validateDrop(getCurrentTarget(), getCurrentOperation(), transferType, data))
		return false;

		if(data instanceof TransferDrag[]) {
			TransferDrag[] transferDragObjects = (TransferDrag[]) data;
			Object target = getCurrentTarget();
			if(target instanceof SubsystemNode) {
				LifeCycleManager lcm = ((SubsystemNode)getCurrentTarget()).getLifeCycleManagerProxy().getLifeCycleManager();

				dropMeemPaths(lcm, transferDragObjects);
				return true;			
			}
			else
			if(target instanceof SubsystemFactoryNode) {
				
				LifeCycleManager lcm = ((SubsystemFactoryNode)getCurrentTarget()).getLifeCycleManagerProxy().getLifeCycleManager();
				dropMeemPaths(lcm, transferDragObjects);
				return true;
			}
		}
		return false;
	}

	private boolean isValid(SubsystemNode subsystem, TransferDrag[] transferDragObjects) {
		if(transferDragObjects == null) return true;	// Assume it to be true

		MeemPath targetPath = subsystem.getMeemPath();

		for (int i = 0; i < transferDragObjects.length; i++) {
			if(targetPath.equals(transferDragObjects[i].getMeemPath())) {
				return false;
			} 
			MeemClientProxy proxy = InterMajikClientProxyFactory.getInstance().locate(transferDragObjects[i].getMeemPath());
			if (proxy == null) return false;
			if(proxy.isA(Subsystem.class)) return false;
		}
		return true;
	}
	
	private boolean isValid(SubsystemFactoryNode subsystemManager, TransferDrag[] transferDragObjects) {
		if(transferDragObjects == null) return true;
		// Only Subsystem is valid
		for (int i = 0; i < transferDragObjects.length; i++) {
			MeemClientProxy proxy = InterMajikClientProxyFactory.getInstance().locate(transferDragObjects[i].getMeemPath());
			if (proxy == null) return false;
			if(!proxy.isA(Subsystem.class)) return false;
		}
		return true;
		
	}
	
	private void dropMeemPaths(final LifeCycleManager lifeCycleManager, final TransferDrag[] transferDragObjects) {
		for (int i = 0; i < transferDragObjects.length; ++i) {
		    
			MeemClientProxy parentProxy = InterMajikClientProxyFactory.getInstance().locate(transferDragObjects[i].getParentMeemPath());
			final LifeCycleManager currentLCM = ((LifeCycleManagerProxy)parentProxy.getFacetProxy(LifeCycleManagerProxy.class)).getLifeCycleManager();
			
			final int index = i;
			Subject.doAs(SecurityManager.getInstance().getSubject(), new PrivilegedAction() {
				public Object run() {
					currentLCM.transferMeem(Meem.spi.get(transferDragObjects[index].getMeemPath()), lifeCycleManager);

					return null;
				}
			});
			
		}
	}

}
