/*
 * @(#)SessionMeemFactoryWedge.java
 *
 * Copyright 2004 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */
package org.openmaji.implementation.server.meem.session;

import java.util.*;

import org.openmaji.implementation.server.Common;

import org.openmaji.meem.*;
import org.openmaji.meem.definition.*;
import org.openmaji.meem.wedge.lifecycle.*;
import org.openmaji.system.manager.lifecycle.LifeCycleManager;
import org.openmaji.system.manager.lifecycle.LifeCycleManagerClient;
import org.openmaji.system.meem.session.SessionMeemFactory;
import org.openmaji.system.meem.session.SessionMeemFactoryClient;
import org.swzoo.log2.core.*;


/**
 *
 */
public abstract class SessionMeemFactoryWedge implements Wedge, SessionMeemFactory {

	private static final Logger logger = LogFactory.getLogger();
	/** Outbound client facet for returning the newly created session meem 
	 * instance and associated errors */
	
	public SessionMeemFactoryClient fSessionClient;
		
	/** Outbound conduit for creating/destroying meems via the lifecycle manager */
	
	public LifeCycleManager lifeCycleManagerConduit;
	
	/** Inbound client conduit from the lifecycle manager to recieve notification
	 * of newly created and destroyed meems. */
	
	public LifeCycleManagerClient lifeCycleManagerClientConduit = new LifeCycleManagerClientImpl();
	
	/** The conduit through which we are alerted to life cycle changes */
	  
	public LifeCycleClient lifeCycleClientConduit = new LifeCycleClientAdapter(this);

	/** The conduit through which the wedge votes on going to a ready lifecycle state  */
		
	public Vote lifeCycleControlConduit;
	
	/** Counter for sequentially incrementing the identifier on each newly
	 * created session meem. */
	
	private long mSessionMeemIdentfierCounter;
	
	/** A working list of all session meems managed by the factory. Upon
	 * conluding this wedge, all will be destroyed by the lifecycle manager.*/
	
	private List mManagedMeems = new Vector();
	
	
	/* -------------------- inbound facets ---------------------------*/
	
	/*
	 * @see org.openmaji.system.meem.session
	 */
	
	public void createSessionMeem() {
		
		if (Common.TRACE_ENABLED && Common.TRACE_LICENSING) {
			LogTools.trace(logger, Common.getLogLevel(), "Request recieved to create session meem...");		
		}

		// grab the meem definition from the abstract provider
		MeemDefinition meemdef = getSessionMeemDefinition();
		
		// change the definition, say with sequential identifiers
		
		String ident = meemdef.getMeemAttribute().getIdentifier();
		
		if ((ident == null) || (ident == "")) {
			
			LogTools.error(logger, "No base identifier provided in meem definition");		
			
			// TODO: throw a CreateException on the error facet
			
		}
		
		mSessionMeemIdentfierCounter++;
		String id = ident + (new Long(mSessionMeemIdentfierCounter)).toString();
		meemdef.getMeemAttribute().setIdentifier(ident);
				
		if (Common.TRACE_ENABLED && Common.TRACE_LICENSING) {
			LogTools.trace(logger, Common.getLogLevel(), "Creating session meeem " + id);		
		}
		lifeCycleManagerConduit.createMeem(meemdef, LifeCycleState.READY);
		
	}

	/*
	 * @see org.openmaji.system.meem.session
	 */
	
	public void destroySessionMeem(MeemPath mp) {
		
		if (Common.TRACE_ENABLED && Common.TRACE_LICENSING) {
			LogTools.trace(logger, Common.getLogLevel(), "Request recieved to destroy session meem...");	
		}
		
		Vector mManagedMeemsList = new Vector();
		mManagedMeemsList.copyInto(mManagedMeems.toArray());
		
		Iterator it = mManagedMeemsList.iterator();
		
		while (it.hasNext()) {
		
			Meem m = (Meem) it.next();
			if (m.getMeemPath().equals(mp)) {
				if (Common.TRACE_ENABLED && Common.TRACE_LICENSING) {
					LogTools.trace(logger, Common.getLogLevel(), "Destroying session meeem...");
				}
				lifeCycleManagerConduit.destroyMeem(m);
			}
		}
	
	}
	
	/* --------------------- inbound conduits -----------------------*/
	
	/**
	 * Inner class implementation of the inbound client conduit from the
	 * lifecycle manager.
	 */
	
	public class LifeCycleManagerClientImpl implements LifeCycleManagerClient {
		
		/*
		 * @see org.openmaji.system.manager.lifecycle.LifeCycleManagerClient
		 */
		
		public void meemCreated(Meem meem, String identifier) {
			
			// Add the newly created meem to a list of meems currently
			// held by the subsystem.
			
			if (Common.TRACE_ENABLED && Common.TRACE_LICENSING) {
				LogTools.trace(logger, Common.getLogLevel(), "Created session meem...");		
			}
			mManagedMeems.add(meem);		
			fSessionClient.sessionMeemCreated(meem.getMeemPath());
		}

		/*
		 * @see org.openmaji.system.manager.lifecycle.LifeCycleManagerClient
		 */
		
		public void meemDestroyed(Meem meem) {

			if (Common.TRACE_ENABLED && Common.TRACE_LICENSING) {
				LogTools.trace(logger, Common.getLogLevel(), "Destroyed session meem...");		
			}
			mManagedMeems.remove(meem);		
			fSessionClient.sessionMeemDestroyed(meem.getMeemPath());
			
		}
		
		/*
		 * @see org.openmaji.system.manager.lifecycle.LifeCycleManagerClient
		 */

		public void meemTransferred(Meem meem, LifeCycleManager targetLifeCycleManager) {
			
		}
		
	}
		
	
	/* --------------------- lifecycle methods ----------------------- */
	
	/**
	 * <p>When starting up, ensures the neighbouring subsystem is started and
	 * commissioned, and therefore able to create new meems.</p>
	 */
	
	public void commence() {
				
		mManagedMeems = new Vector();
		
		// initialise the meemattribute counter 
		
		mSessionMeemIdentfierCounter = 0;
		
	}
	
	/**
	 * <p>When shutting down, dereferences all existing session meems which
	 * have been created during the factory lifetime.</p>
	 */
	
	public void conclude() {
		
		Iterator it = new ArrayList(mManagedMeems).iterator();
		
		while (it.hasNext()) {
			Meem m = (Meem) it.next();
			lifeCycleManagerConduit.destroyMeem(m);
		}
		mManagedMeems = new Vector();
	}
	
	
	/**
	 * <p>Abstract method which should be overriden by each factory implementation
	 * to return the definition of the session meem which should be instantiated by
	 * the factory.</p>
	 */
	
	protected abstract MeemDefinition getSessionMeemDefinition();
}
