/*
 * @(#)PersistenceHandlerWedge.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - on second save, only grab content from previous persist
 */
package org.openmaji.implementation.server.meem.wedge.persistence;


import java.util.logging.Level;
import java.util.logging.Logger;

import org.openmaji.implementation.server.meem.wedge.lifecycle.SystemLifeCycleClientAdapter;
import org.openmaji.meem.Meem;
import org.openmaji.meem.MeemPath;
import org.openmaji.meem.Wedge;
import org.openmaji.meem.conduit.Persistence;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClient;
import org.openmaji.meem.wedge.reference.Reference;
import org.openmaji.system.meem.core.MeemCore;
import org.openmaji.system.meem.definition.MeemContent;
import org.openmaji.system.meem.wedge.persistence.ManagedPersistenceClient;
import org.openmaji.system.meem.wedge.persistence.ManagedPersistenceHandler;
import org.openmaji.system.meem.wedge.reference.MeemClientCallback;
import org.openmaji.system.meem.wedge.reference.MeemClientConduit;
import org.openmaji.system.space.meemstore.MeemStore;


/**
 * <p>
 * ...
 * </p>
 * @author  mg
 * @version 1.0
 */
public class PersistenceHandlerWedge implements ManagedPersistenceHandler, Wedge {

	/**
	 * Internal reference to MeemCore
	 */
	public MeemCore meemCore;

	/**
	 * ManagedPersistenceClient (out-bound Facet)
	 */
	public ManagedPersistenceClient 		managedPersistenceClient;
	
	public Persistence 								persistenceConduit;
	public ManagedPersistenceClient 		managedPersistenceClientConduit;
	public ManagedPersistenceHandler	managedPersistenceHandlerConduit = new PersistenceHandlerConduit(this);
	
	public Meem 										meemConduit;
	public MeemClientConduit 			meemClientConduit;

	public LifeCycleClient lifeCycleClientConduit = new SystemLifeCycleClientAdapter(this);

	/**
	 */
	public void persist() 
	{
		if (DEBUG) {
			logger.log(Level.INFO, "persisting meem: " + meemCore.getMeemPath());
		}
		if (meemCore.getMeemStore() == null) 
		{
			// we don't have a meem store, so we can't persist
			return;
		}

		meemClientConduit.provideReference(meemCore.getMeemStore(), "meemStore", MeemStore.class, new ReferenceCallbackImpl());
	}
	

	/* (non-Javadoc)
	 * @see org.openmaji.meem.conduit.Persistence#load()
	 */
	public void restore()
	{
		if (DEBUG) {
			logger.log(Level.INFO, "restoring meem: " + meemCore.getMeemPath());
		}
		persistenceConduit.restore();
	}

	/**
	 */
	public synchronized void restore(MeemContent meemContent) {
		MeemPath	meemPath = meemCore.getMeemPath();
		
		meemCore.restoreContent(meemContent);
		
		managedPersistenceClient.restored(meemPath);
		managedPersistenceClientConduit.restored(meemPath);
		
		persistenceConduit.restore();
	}

	private final class ReferenceCallbackImpl
		implements MeemClientCallback
	{
        public void referenceProvided(Reference reference)
        {
        	if (reference == null)
        	{
				logger.log(Level.INFO, "no meemStore reference found can't persist!");
				return;
        	}
        	
			MeemStore 		meemStore = (MeemStore)reference.getTarget();
			MeemPath			meemPath = meemCore.getMeemPath();
			MeemContent 	meemContent = meemCore.getContent();
             
			meemStore.storeMeemContent(meemPath, meemContent);

			managedPersistenceClient.meemContentChanged(meemPath, meemContent);
			
			persistenceConduit.persist();
        }
	}

	private final class PersistenceHandlerConduit implements ManagedPersistenceHandler {
		
		ManagedPersistenceHandler	handler;
		
		PersistenceHandlerConduit(
			ManagedPersistenceHandler handler)
		{
			this.handler = handler;
		}
		
		/**
		 * @see org.openmaji.system.meem.wedge.persistence.ManagedPersistenceHandler#persist()
		 */
		public void persist() {
			handler.persist();
		}
		
		/**
		 * @see org.openmaji.system.meem.wedge.persistence.ManagedPersistenceHandler#restore(org.openmaji.system.meem.definition.MeemContent)
		 */
		public void restore(MeemContent meemContent) {
			handler.restore(meemContent);
		}

        /* (non-Javadoc)
         * @see org.openmaji.meem.conduit.Persistence#load()
         */
        public void restore()
        {
            handler.restore();
        }

	}
	
	public void commence() {
	}

	public void conclude() {
		persist();
	}

	/* ---------- Logging fields ----------------------------------------------- */

	private static final boolean DEBUG = false;
	private static final Logger logger = Logger.getAnonymousLogger();
}
