/*
 * @(#)HyperSpaceLifeCycleManagerWedge.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.server.manager.lifecycle.hyperspace;

import org.openmaji.meem.Meem;
import org.openmaji.meem.MeemPath;
import org.openmaji.meem.Wedge;
import org.openmaji.meem.wedge.lifecycle.LifeCycle;
import org.openmaji.meem.wedge.lifecycle.LifeCycleState;
import org.openmaji.server.helper.HyperSpaceHelper;
import org.openmaji.system.meem.core.MeemCore;
import org.openmaji.system.meem.definition.MeemContent;
import org.openmaji.system.meem.wedge.persistence.ManagedPersistenceClient;
import org.openmaji.system.space.Category;
import org.openmaji.system.space.hyperspace.HyperSpace;


/**
 * <p>
 * ...
 * </p>
 * @author  mg
 * @version 1.0
 */
public class HyperSpaceLifeCycleManagerWedge implements HyperSpace, Wedge {

	public MeemCore meemCore;
	
	private boolean hyperSpaceMeemSet = false;
	
	/* ------------------------ conduits --------------------- */
	
	public Category categoryConduit = null;
	
	public ManagedPersistenceClient managedPersistenceClientConduit = new PersistenceClientImpl(); //inbound

	/*--------------------- Category methods ----------------- */

	/* These are for the HyperSpace root category, not the LifeCycleManagement category */

	/**
	 * @see org.openmaji.system.space.Category#addEntry(java.lang.String, org.openmaji.meem.Meem)
	 */
	public void addEntry(String entryName, Meem meem) {
		categoryConduit.addEntry(entryName, meem);
	}

	/**
	 * @see org.openmaji.system.space.Category#removeEntry(java.lang.String)
	 */
	public void removeEntry(String entryName) {
		categoryConduit.removeEntry(entryName);
	}

	/**
	 * @see org.openmaji.system.space.Category#renameEntry(java.lang.String, java.lang.String)
	 */
	public void renameEntry(String oldEntryName, String newEntryName) {
		categoryConduit.renameEntry(oldEntryName, newEntryName);
	}
	

	/* ---------------------- ManagedPersistenceClient conduit ----------------- */

	private final class PersistenceClientImpl implements ManagedPersistenceClient {

		/**
		 * @see org.openmaji.system.meem.wedge.persistence.ManagedPersistenceClient#restored(org.openmaji.meem.MeemPath)
		 */
		public void restored(MeemPath meemPath) {

			if (meemPath.equals(meemCore.getMeemPath())) {

				// make ourself READY which will start meems
				LifeCycle lifeCycleFacet = (LifeCycle) meemCore.getTarget("lifeCycle");

				lifeCycleFacet.changeLifeCycleState(LifeCycleState.READY);	
				
				if (!hyperSpaceMeemSet) {
					hyperSpaceMeemSet = true;
					HyperSpaceHelper.getInstance().setHyperSpaceMeem(meemCore.getSelf());
				}
			}
		}

		/**
		 * 
		 */
		public void meemContentChanged(MeemPath meemPath, MeemContent meemContent) {
			// Don't care
		}

	}
}
