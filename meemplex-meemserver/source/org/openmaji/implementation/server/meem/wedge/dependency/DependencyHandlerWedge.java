/*
 * @(#)DependencyHandlerWedge.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.server.meem.wedge.dependency;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.*;

import org.openmaji.implementation.server.Common;
import org.openmaji.implementation.server.meem.FacetImpl;
import org.openmaji.implementation.server.meem.core.MeemCoreImpl;
import org.openmaji.implementation.server.meem.invocation.MeemInvocationSource;
import org.openmaji.implementation.server.meem.invocation.MeemInvocationTarget;

import org.openmaji.meem.*;
import org.openmaji.meem.definition.*;
import org.openmaji.meem.filter.Filter;
import org.openmaji.meem.wedge.dependency.*;
import org.openmaji.meem.wedge.lifecycle.*;
import org.openmaji.system.meem.core.MeemCore;
import org.openmaji.system.meem.wedge.reference.*;
import org.swzoo.log2.core.LogFactory;
import org.swzoo.log2.core.LogTools;
import org.swzoo.log2.core.Logger;


/**
 * <p>
 * This doesn't implement DependencyHandler as it isn't a facet.
 * What type of filters do we want for this?
 * </p>
 * @author  mg
 * @version 1.0
 */
public class DependencyHandlerWedge implements DependencyHandler, Wedge {

	private static final int LOG_LEVEL = Common.getLogLevelVerbose();
	private static final Logger logger = LogFactory.getLogger();
	private static final boolean DEBUG = false;

	public MeemCore meemCore;
	public MeemContext meemContext;


	/* ------------------- outbound facets ------------------ */
	
    public DependencyClient dependencyClient;
    public final ContentProvider dependencyClientProvider = new ContentProvider() {
        public void sendContent(Object target, Filter filter) {
            DependencyClient dependencyClientTarget = (DependencyClient) target;

            sendDependencyContent(dependencyClientTarget);
        }
    };


	/* -----------------------  conduits ------------------------ */

	/** 
	 * inbound - DependencyHandler - add/remove deps 
	 */
	public DependencyHandler dependencyHandlerConduit = new DependencyHandlerTarget();

	/** 
	 * outbound - DependencyClient - dep connected/disconnected 
	 */
	public DependencyClient dependencyClientConduit;

	/** 
	 * A conduit for adding and removing references to this meem.
	 * Used by the SingleForwardDependency.
	 */
	public Meem meemConduit;
	
	/** 
	 * A conduit for requesting references to facets on other meems.
	 * Used by the SingleForwardDependency.
	 */
	public MeemClientConduit meemClientConduit;

	/** lifecycle - go ready voting */
	public Vote lifeCycleControlConduit;

	/** 
	 * note: we use the application wedge cycle here 
	 */
	public LifeCycleClient lifeCycleClientConduit = new LifeCycleClientConduit();


	/* --------------- persisted properties ---------------- */
	
	/** 
	 * persistent dependendencies that have been persisted.
	 * This is a map from DependencyAttribute to DependencyDescriptor.
	 */
	public Map<DependencyAttribute, DependencyDescriptor> persistedDependencies = new HashMap<DependencyAttribute, DependencyDescriptor>();

	
	/* --------------- private properties ------------------ */

	/** 
	 * Dependencies that are transient, i.e. are lost after the meem has gone
	 * down to a loaded state.
	 * A map of DependencyAttributes to FacetConnectables ('LifecycleMonitors').
	 */
	private Map<DependencyAttribute, FacetConnectable> transientDependencies = new HashMap<DependencyAttribute, FacetConnectable>();
	
	/** 
	 * Dependencies to persist.
	 * A map of dependencyAttributes to FacetConnectables  ('LifecycleMonitors').
	 */
	private Map<DependencyAttribute, FacetConnectable> persistentDependencies = new HashMap<DependencyAttribute, FacetConnectable>();
	
	/** dependencies that make this meem's lifecycle state dependent on another meem */
	private Set<DependencyAttribute> strongDependencies = new HashSet<DependencyAttribute>();

	/** the dependencies that have been connected */
	private Set<DependencyAttribute> connectedDependencies = new HashSet<DependencyAttribute>();
	
	/** a mapping of dependency keys to dependency attributes */
	private Map<Serializable, DependencyAttribute> dependencyKeyAttributes = new HashMap<Serializable, DependencyAttribute>();

	/** the number of strong dependencies that are 'connected' */
	private int connectedStrongCount = 0;
	
	/** 
	 * A cache of the current voting status of the wedge, used to determine
	 * whether to change the lifecycle voting status. 
	 */
	private boolean currentVote = true;
	
	/**
	 * A map of Meem to MeemLifeCycleStateMonitor.
	 */
	private HashMap<Meem, MeemLifeCycleStateMonitor> lifecycleMonitors = new HashMap<Meem, MeemLifeCycleStateMonitor>();

	
	/* ---------------------- DependencyHandler interface ----------------------- */

	/**
	 * 
	 */
	public void addDependency(String facetIdentifier, DependencyAttribute dependencyAttribute, LifeTime lifeTime) {
		dependencyHandlerConduit.addDependency(facetIdentifier, dependencyAttribute, lifeTime);
	}
	
	/**
	 * @see org.openmaji.meem.wedge.dependency.DependencyHandler#addDependency(org.openmaji.meem.Facet, org.openmaji.meem.definition.DependencyAttribute, org.openmaji.meem.definition.LifeTime)
	 */
	public void addDependency(Facet facet, DependencyAttribute dependencyAttribute, LifeTime lifeTime) {
		dependencyHandlerConduit.addDependency(facet, dependencyAttribute, lifeTime);
	}

	/**
	 * 
	 */
	public void removeDependency(DependencyAttribute dependencyAttribute) {
		dependencyHandlerConduit.removeDependency(dependencyAttribute);
	}

	/**
	 * 
	 */
	public void updateDependency(DependencyAttribute dependencyAttribute) {
		dependencyHandlerConduit.updateDependency(dependencyAttribute);
	}

	/* ----------------------- private methods --------------------- */
	
	protected  void doAddDependency(String sourceFacetIdentifier, final DependencyAttribute dependencyAttribute, LifeTime lifeTime) {
		doAddDependency(sourceFacetIdentifier, null, dependencyAttribute, lifeTime, true);
	}
	
	protected  void doAddDependency(Facet sourceFacet, final DependencyAttribute dependencyAttribute, LifeTime lifeTime) {
		doAddDependency(null, sourceFacet, dependencyAttribute, lifeTime, true);
	}
	
	protected synchronized void doAddDependency(String sourceFacetIdentifier, Facet sourceFacet, final DependencyAttribute dependencyAttribute, LifeTime lifeTime, boolean notify) {

		DependencyDescriptor dependencyDescriptor;
		if (sourceFacetIdentifier != null) {
			dependencyDescriptor = new DependencyDescriptor(
					sourceFacetIdentifier,
					dependencyAttribute,
					lifeTime);			
		}
		else {
			dependencyDescriptor = new DependencyDescriptor(
					sourceFacet,
					dependencyAttribute,
					lifeTime);			
		}
		
		// check to see if we already have this dependency attribute
		if (persistentDependencies.containsKey(dependencyAttribute) || transientDependencies.containsKey(dependencyAttribute)) {
			// don't need to do anything
			return;
		}
		
		dependencyKeyAttributes.put(dependencyAttribute.getKey(), dependencyAttribute);

		// if a permanent dependency, add to set of persisted dependencies
		if (lifeTime == LifeTime.PERMANENT) {
			persistedDependencies.put(dependencyAttribute, dependencyDescriptor);
		}

		boolean many = dependencyAttribute.getDependencyType().equals(DependencyType.STRONG_MANY)
			||	dependencyAttribute.getDependencyType().equals(DependencyType.WEAK_MANY);


		final String targetFacetIdentifier = dependencyAttribute.getFacetIdentifier();

		Meem dependencyMeem = dependencyAttribute.getMeem();
		final MeemPath dependencyMeemPath = (dependencyMeem != null)
			?	dependencyMeem.getMeemPath()
			:	dependencyAttribute.getMeemPath();

		if (dependencyMeem == null) {
			dependencyMeem = Meem.spi.get(dependencyMeemPath);
		}

		DependencyType dependencyType = dependencyAttribute.getDependencyType();

		if (many) {
			dependencyType = dependencyType.equals(DependencyType.STRONG_MANY) ? DependencyType.STRONG : DependencyType.WEAK;
		}

		final boolean isStrong = dependencyType.equals(DependencyType.STRONG);

		Connectable client = new BasicConnectable(dependencyAttribute, isStrong);

		boolean isInbound = false;
		Class specification = null;
		
		if (sourceFacetIdentifier != null) {
			FacetImpl facetImpl = getFacetImpl(sourceFacetIdentifier);
	
			if (facetImpl == null) {
				LogTools.warn(logger, "No facet with identifier \"" + sourceFacetIdentifier + "\". Unable to create dependency.");
				return;
			}
	
			FacetAttribute facetAttribute = facetImpl.getFacetAttribute();
			isInbound = facetAttribute.isDirection(Direction.INBOUND);
			if (!isInbound) {
				specification = facetImpl.getSpecification();
			}
		}
		else {
			if (sourceFacet != null && Proxy.isProxyClass(sourceFacet.getClass())) {
				InvocationHandler ih = Proxy.getInvocationHandler(sourceFacet);

				if (ih != null) {
					if (ih instanceof MeemInvocationTarget) {
						sourceFacetIdentifier = ((MeemInvocationTarget) ih).getFacetIdentifier();
						isInbound = true;
					}
					else if (ih instanceof MeemInvocationSource) {
						isInbound = false;
						sourceFacetIdentifier = ((MeemInvocationSource) ih).getFacetAttribute().getIdentifier();
						specification = ((MeemInvocationSource) ih).getSpecification();
					}
				}
			}
		}

		boolean isSystem = false;
		FacetConnectable meemClient;
		
		if (many) {
			meemClient = new ManyDependency(
					this,
					dependencyMeem, 
					dependencyDescriptor,
					dependencyType,
					isSystem,
					client);
		}
		else {
			isSystem = isSystemFacet(targetFacetIdentifier);

			if (isInbound) {
				meemClient = new SingleReverseDependency(
						this,
						dependencyMeem,
						dependencyDescriptor,
						isSystem,
						client);
			}
			else {
				meemClient = new SingleForwardDependency(
						this,
						dependencyMeem,
						dependencyDescriptor,
						isSystem,
						specification, 
						client);
			}
		}

		// Monitor the lifecycle state of the Meem
		addToMonitor(meemClient);

		if (lifeTime.equals(LifeTime.PERMANENT)) {
			persistentDependencies.put(dependencyAttribute, meemClient);
		}
		else {
			transientDependencies.put(dependencyAttribute, meemClient);
		}

		if (isStrong) {
			strongDependencies.add(dependencyAttribute);
			checkStrongDependenciesResolved();
		}

		if (notify) {
			dependencyClient.dependencyAdded(sourceFacetIdentifier, dependencyAttribute);
			dependencyClientConduit.dependencyAdded(sourceFacetIdentifier, dependencyAttribute);
		}

		if (Common.TRACE_ENABLED && Common.TRACE_DEPENDENCY) {
			LogTools.trace(logger, LOG_LEVEL,
				"dependencyAdded() SourceMeem = " + meemCore.getMeemPath()
					+ " FacetId: " + sourceFacetIdentifier
					+ " DepAttr: " + dependencyAttribute
					+ " LifeTime: " + lifeTime);
		}
	}

	/**
	 * 
	 * @param dependencyAttribute
	 */
	protected synchronized void doRemoveDependency(final DependencyAttribute dependencyAttribute, boolean permanent) {

		if (DEBUG) {
			LogTools.info(logger, "Removing dependency (" + permanent + "): " + dependencyAttribute);
		}
		
		boolean removed = false;
		
		if (dependencyAttribute == null) {
			return;
		}
		
		if (permanent) {
			// is a proper removal. remove from persisted dependencies (if it's persistend)
			DependencyDescriptor descriptor = (DependencyDescriptor) persistedDependencies.remove(dependencyAttribute);
			removed = (descriptor != null);
		}
		
		DependencyAttribute oldDependencyAttribute = (DependencyAttribute) dependencyKeyAttributes.remove(dependencyAttribute.getKey());
		if (oldDependencyAttribute != null) {
			removed = true;
			FacetConnectable client = (FacetConnectable) transientDependencies.remove(oldDependencyAttribute);
			if (client == null) {
				client = (FacetConnectable) persistentDependencies.remove(oldDependencyAttribute);
			}
			if (client == null) {
				// something is weird. we don't know about the DependencyAttribute. Lets just return
				LogTools.info(logger, "Problem removing dependency.  DependencyAttribute is not in transient or persistent collection.");
				return;
			}

			removeFromMonitor(client);

			if (oldDependencyAttribute.getDependencyType().equals(DependencyType.STRONG)
				||	oldDependencyAttribute.getDependencyType().equals(DependencyType.STRONG_MANY))
			{
				strongDependencies.remove(oldDependencyAttribute);
				checkStrongDependenciesResolved();
			}
		}

		if (permanent && removed) {
			// if dependency has been removed, notify clients
			if (oldDependencyAttribute == null) {
				oldDependencyAttribute = dependencyAttribute;
			}
			dependencyClient.dependencyRemoved(oldDependencyAttribute);
			dependencyClientConduit.dependencyRemoved(oldDependencyAttribute);
		}

		if (Common.TRACE_ENABLED && Common.TRACE_DEPENDENCY) {
			LogTools.trace(logger, LOG_LEVEL, "dependencyRemoved() DepAttr: " + oldDependencyAttribute);
		}
	}
	
	private void doUpdateDependency(DependencyAttribute dependencyAttribute) {
		
		if (DEBUG) {
			LogTools.info(logger, "doUpdateDependency: " + dependencyAttribute);
		}

		FacetConnectable desc = (FacetConnectable) transientDependencies.get(dependencyAttribute);
		if (desc == null) {
			desc = (FacetConnectable) persistentDependencies.get(dependencyAttribute);
		}
		if (desc == null) {
			LogTools.info(logger, "Can not update dependency. Not found");
			return;
		}
		
		
		DependencyAttribute oldDependencyAttribute = (DependencyAttribute) dependencyKeyAttributes.put(dependencyAttribute.getKey(), dependencyAttribute);

		// remove old attribute and add new one silently
		doRemoveDependency(oldDependencyAttribute, false);
		doAddDependency(desc.getLocalFacetId(), null, dependencyAttribute, desc.getLifeTime(), false);		
		
		dependencyClient.dependencyUpdated(dependencyAttribute);
		dependencyClientConduit.dependencyUpdated(dependencyAttribute);
	}

	private void checkStrongDependenciesResolved() {
		boolean allDependenciesResolved = (strongDependencies.size() == connectedStrongCount);

		if (currentVote != allDependenciesResolved) {
			currentVote = allDependenciesResolved;

			if (Common.TRACE_ENABLED && Common.TRACE_DEPENDENCY) {
				LogTools.trace(logger, LOG_LEVEL, "checkStrongDependenciesResolved() voting : "
					+ allDependenciesResolved + " : " + meemCore.getMeemPath());
			}

			lifeCycleControlConduit.vote(meemContext.getWedgeIdentifier(), allDependenciesResolved);
		}
	}

	private boolean isSystemFacet(String facetIdentifier) {
		FacetImpl facetImpl = getFacetImpl(facetIdentifier);

		return facetImpl == null ? false : facetImpl.getWedgeImpl().isSystemWedge();
	}

	private FacetImpl getFacetImpl(String facetIdentifier)
	{
		FacetImpl facetImpl = ((MeemCoreImpl) meemCore).getInboundFacetImpl(facetIdentifier);

		if (facetImpl == null) {
			facetImpl = ((MeemCoreImpl) meemCore).getOutboundFacetImpl(facetIdentifier);
		}

		return facetImpl;
	}
	
	/**
	 * Add the connectable client to the appropriate LyfcycleState monitor.
	 * 
	 * @param meem The meem whose LC state to monitor
	 * @param isSytem whether the monitored Facet is a system facet or not. 
	 * @param client
	 */
	private void addToMonitor(FacetConnectable client) {
		MeemLifeCycleStateMonitor monitor;
		
		synchronized(lifecycleMonitors) {
			Meem meem = client.getMeem();
			monitor = (MeemLifeCycleStateMonitor) lifecycleMonitors.get(meem);
			if (monitor == null) {
				monitor = new MeemLifeCycleStateMonitor(meemCore, meem);
				lifecycleMonitors.put(meem, monitor);
			}
			monitor.addConnectable(client);
		}
		// connect the monitor, this will connect the client when appropriate
		monitor.connect();
	}
	
	/**
	 * 
	 * @param meem
	 * @param client
	 */
	private void removeFromMonitor(FacetConnectable client) {
		synchronized(lifecycleMonitors) {
			Meem meem = client.getMeem();
			MeemLifeCycleStateMonitor monitor = (MeemLifeCycleStateMonitor) lifecycleMonitors.get(meem);
			if (monitor != null) {
				monitor.removeConnectable(client);
				if (monitor.connectables() == 0) {
					monitor.disconnect();
					lifecycleMonitors.remove(meem);
				}
			}
		}
		client.disconnect();
	}
	
	/**
	 * Send content to a DependencyClient
	 * @param client
	 */
	private void sendDependencyContent(DependencyClient client) {
        Iterator addedIterator = persistedDependencies.keySet().iterator();
        while (addedIterator.hasNext()) {
        	DependencyAttribute dependencyAttribute = (DependencyAttribute) addedIterator.next();
        	DependencyDescriptor descriptor = (DependencyDescriptor) persistedDependencies.get(dependencyAttribute);
        	client.dependencyAdded(descriptor.getFacetIdentifier(), dependencyAttribute);
        }

		Iterator connectedIterator = connectedDependencies.iterator();
		while (connectedIterator.hasNext()) {
			DependencyAttribute dependencyAttribute = (DependencyAttribute) connectedIterator.next();
			client.dependencyConnected(dependencyAttribute);
		}

	}
	
	/* --------------- DependencyHandler conduit ------------ */

	private final class DependencyHandlerTarget implements DependencyHandler {
		/**
		 * 
		 */
		public void addDependency(String facetIdentifier, DependencyAttribute dependencyAttribute, LifeTime lifeTime) {
			
			doAddDependency(facetIdentifier, dependencyAttribute, lifeTime);
		}
		
		/**
		 * @see org.openmaji.meem.wedge.dependency.DependencyHandler#addDependency(org.openmaji.meem.Facet, org.openmaji.meem.definition.DependencyAttribute, org.openmaji.meem.definition.LifeTime)
		 */
		public void addDependency(Facet facet, DependencyAttribute dependencyAttribute, LifeTime lifeTime) {
			
			doAddDependency(facet, dependencyAttribute, lifeTime);
		}

		/**
		 * 
		 */
		public void removeDependency(DependencyAttribute dependencyAttribute) {

			doRemoveDependency(dependencyAttribute, true);
		}
		
		public void updateDependency(DependencyAttribute dependencyAttribute) {
			doUpdateDependency(dependencyAttribute);
		}
	}

	/* ---------- LifecycleClient conduit ------------ */
	
	private final class LifeCycleClientConduit implements LifeCycleClient 
	{
		public void lifeCycleStateChanged(LifeCycleTransition transition) {}

        public void lifeCycleStateChanging(LifeCycleTransition transition)
        {
        	// old-style dependencies are contained in MeemStructure
			//MeemStructure	meemStructure  = meemCore.getMeemStructure();
 
			if (transition.equals(LifeCycleTransition.LOADED_PENDING)) {
				// send dependency info to dependencyClientConduit
				sendDependencyContent(dependencyClientConduit);
				
				// add persisted dependencies.
				
				/* TODO remove this 
				  Iterator depIterator = meemStructure.getDependencyAttributeKeys();
				  while (depIterator.hasNext())  {
					  Object dependencyAttributeKey = depIterator.next();
					  DependencyAttribute dependencyAttribute = meemStructure.getDependencyAttribute(dependencyAttributeKey);
					  FacetAttribute facetAttribute =  meemStructure.getFacetAttribute(meemStructure.getFacetKeyFromDependencyKey(dependencyAttributeKey));
	
					  doAddDependency(facetAttribute.getIdentifier(), dependencyAttribute, LifeTime.PERMANENT);
				  }
				 end section */

				// iterate through the dependencies that are persisted, and add them to this meem
				Iterator iterator = persistedDependencies.keySet().iterator();
				while (iterator.hasNext())  {
					DependencyAttribute attribute = (DependencyAttribute) iterator.next();
					DependencyDescriptor descriptor = (DependencyDescriptor) persistedDependencies.get(attribute);
					doAddDependency(descriptor.getFacetIdentifier(), attribute, LifeTime.PERMANENT);
				}
			}
			else if (transition.equals(LifeCycleTransition.PENDING_LOADED))  {
				//
				// remove dependencies.
				//
				
				/* TOOD remove this section. Dependencies should not be in meem structure 
				Iterator iterator = meemStructure.getDependencyAttributeKeys();
				while (iterator.hasNext()) {
					Object dependencyAttributeKey = iterator.next();
					DependencyAttribute dependencyAttribute = meemStructure.getDependencyAttribute(dependencyAttributeKey);
					if (dependencyAttribute != null) {
						doRemoveDependency(dependencyAttribute, false);
					}
				}
				 end section */
				

				// remove the transient dependencies
				Object[] keys = transientDependencies.keySet().toArray();
				for (int i = 0; i < keys.length; i++) {
					DependencyAttribute dependencyAttribute = (DependencyAttribute) keys[i];
					doRemoveDependency(dependencyAttribute, true);
				}		
				
				// remove the persistent dependences
				keys = persistentDependencies.keySet().toArray();
				for (int i = 0; i < keys.length; i++) {
					DependencyAttribute dependencyAttribute = (DependencyAttribute) keys[i];
					doRemoveDependency(dependencyAttribute, false);
				}
			}
        }
	};
	
	/**
	 * 
	 */
	private final class BasicConnectable implements Connectable {
		final DependencyAttribute dependencyAttribute;
		final boolean isStrong;
		
		public BasicConnectable(DependencyAttribute dependencyAttribute, boolean isStrong) {
			this.dependencyAttribute = dependencyAttribute;
			this.isStrong = isStrong;
		}

		public void connect() {
			if ((transientDependencies.containsKey(dependencyAttribute) || 
		      persistentDependencies.containsKey(dependencyAttribute)) 
		      && connectedDependencies.add(dependencyAttribute))
			{
				if (isStrong) {
					connectedStrongCount++;
					checkStrongDependenciesResolved();
				}

				// TODO[peter] Set the attribute's meem?

				dependencyClient.dependencyConnected(dependencyAttribute);
				dependencyClientConduit.dependencyConnected(dependencyAttribute);
			}
		}

		public void disconnect() {
			if (connectedDependencies.remove(dependencyAttribute)) {
				if (isStrong) {
					connectedStrongCount--;
					checkStrongDependenciesResolved();
				}

				// TODO[peter] Set the attribute's meem?
				
				dependencyClient.dependencyDisconnected(dependencyAttribute);
				dependencyClientConduit.dependencyDisconnected(dependencyAttribute);
			}
		}
	}
	


}
