/*
 * @(#)SmartProxyMeem.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

/* ToDo:
 * - Only references and usage of MeemPath is a temporary hack to support
 *   the synchronous version of Meem.getMeemPath().  This should be removed
 *   as soon as possible.
 *
 * - Attempt to make the list of Interfaces visible via a Jini Lookup Service
 *   browser to be the minimal set, without duplicate of "Remote" and "Facet".
 *
 * - Provide JoinAdmin support.
 */

package org.openmaji.implementation.server.meem.wedge.remote;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.rmi.RemoteException;
import java.util.*;

import org.openmaji.implementation.server.manager.gateway.GatewayManagerWedge;
import org.openmaji.implementation.server.meem.MeemSystemWedge;
import org.openmaji.implementation.server.meem.invocation.InvocationContext;
import org.openmaji.implementation.server.meem.invocation.InvocationContextTracker;
import org.openmaji.implementation.server.meem.invocation.MeemInvocationTarget;
import org.openmaji.implementation.server.nursery.diagnostic.DiagnosticLog;
import org.openmaji.implementation.server.nursery.diagnostic.events.invocation.RemoteOutboundInvocationEvent;
import org.openmaji.implementation.server.request.RequestStack;
import org.openmaji.implementation.server.request.RequestTracker;
import org.swzoo.log2.core.LogFactory;
import org.swzoo.log2.core.LogTools;
import org.swzoo.log2.core.Logger;

import net.jini.core.lease.UnknownLeaseException;
import net.jini.lease.LeaseListener;
import net.jini.lease.LeaseRenewalEvent;
import net.jini.lease.LeaseRenewalManager;

import org.openmaji.meem.*;
import org.openmaji.meem.definition.Direction;
import org.openmaji.meem.filter.FacetFilter;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClient;
import org.openmaji.meem.wedge.lifecycle.LifeCycleState;
import org.openmaji.meem.wedge.lifecycle.LifeCycleTransition;
import org.openmaji.meem.wedge.reference.Reference;
import org.openmaji.system.meem.wedge.reference.ContentClient;
import org.openmaji.system.meem.wedge.remote.Lease;
import org.openmaji.system.meem.wedge.remote.RemoteMeem;
import org.openmaji.system.meemserver.MeemServer;


/**
 * <p>
 * ...
 * </p>
 * <p>
 * Note: Implementation thread safe = Not considered yet
 * </p>
 * @author  Andy Gelme
 * @version 1.0
 */
public class SmartProxyMeem implements InvocationHandler, Serializable, WeakReferenceProvider
{
	static final long serialVersionUID = -5040929670711536181L;
	
	private static String REPLACE_WITH_NEWEST = "org.openmaji.server.smartproxymeem.replaceWithNewest";
	private static String LIFECYCLESTATE_PROXYCACHING = "org.openmaji.server.smartproxymeem.lifeCycleStateProxyCache";
	private static String LEASING_ENABLED = "org.openmaji.server.smartproxymeem.leasingEnabled";

//	private transient static Class[] systemInboundFacets = null;

	private transient Meem smartProxyMeem = null;

	private final RemoteMeem remoteMeem;
	private final MeemPath meemPath;

	private final Date timeStamp;

	private boolean disposed = false;
	private boolean renewingLease = false;
	private transient TimerTask renewTask = null;
	private transient Object waitObject = null;
	
	private transient WeakReference weakReference = null;
	private transient static Timer timer = new Timer(true);
	
	private transient Lease lease;
	
	private LifeCycleTransition lastTransition = LifeCycleTransition.ABSENT_DORMANT;
	private transient Set lifeCycleClientReferences = null;
	private transient LifeCycleConnector lifeCycleConnector;
	private transient long leaseRetryTime;
	
	private transient Set meemClientReferences = null;
		
	protected static HashMap smartProxyMeemMap = new HashMap();
	private static LeaseRenewalManager leaseRenewalManager = new LeaseRenewalManager();
	
	static private final Logger logger = LogFactory.getLogger();
	
	private static boolean replaceWithNewest;
	private static boolean proxyAndCacheLCS;
	private static boolean leasingEnabled;
	
	static {
		replaceWithNewest = Boolean.valueOf(System.getProperty(REPLACE_WITH_NEWEST, "true")).booleanValue();
		proxyAndCacheLCS = Boolean.valueOf(System.getProperty(LIFECYCLESTATE_PROXYCACHING, "true")).booleanValue();
		leasingEnabled = Boolean.valueOf(System.getProperty(LEASING_ENABLED, "true")).booleanValue();
		
		if (!replaceWithNewest) {
			LogTools.warn(logger, "Replace with newest SmartProxyMeem disabled. Will always use the first one discovered.");
		}
		if (!proxyAndCacheLCS) {
			LogTools.warn(logger, "Proxying and caching of LifeCycleState in SmartProxyMeem disabled.");
		}
		if (!leasingEnabled) {
			LogTools.warn(logger, "SmartProxyMeem leasing disabled.");
		}
	}
	
	public Object readResolve() {
		// If not deserialized in a Meemserver, return this without creating leases etc
		if (MeemServer.spi.getName() == null) {
			return this;
		}
		
		// call this to initialize the lifecycle references set
		getLifeCycleClientReferences();
		
		synchronized (smartProxyMeemMap) {
			SmartProxyMeem spm = (SmartProxyMeem) smartProxyMeemMap.get(meemPath);
			if (spm != null) {
				if (replaceWithNewest) {
					if (spm.timeStamp.equals(this.timeStamp) || spm.timeStamp.after(this.timeStamp)) {
						return spm;
					} else {
						// shutdown existing spm and use this new one
						this.lifeCycleClientReferences = spm.lifeCycleClientReferences;
						spm.privateDispose();
					}
				} else {
					return spm;
				}
			}
			
			smartProxyMeemMap.put(meemPath, this);
			
			if (leasingEnabled) {
				connectLease();
			} else {
				if (proxyAndCacheLCS) {
					connectLifeCycleClient();
				}
			}
						
			return this;
		}
	}
	
	public SmartProxyMeem(RemoteMeem remoteMeem, MeemPath meemPath)
	{
		this.remoteMeem = remoteMeem;
		this.meemPath = meemPath;

		this.timeStamp = new Date();
	}

	public Meem getSmartProxyMeem()
	{
		if (remoteMeem != null && smartProxyMeem == null)
		{
			try
			{
				smartProxyMeem = (Meem) Proxy.newProxyInstance(
					getClass().getClassLoader(),
					new Class[] { org.openmaji.meem.Meem.class },
					this);
			}
			catch (Exception exception)
			{
				exception.printStackTrace();
			}
		}

		return smartProxyMeem;
	}

	public RemoteMeem getRemoteMeem()
	{
		return remoteMeem;
	}

	/**
	 * <p>
	 * </p>
	 * @param proxy  Proxy instance that the method was invoked upon
	 * @param method Corresponds to the interface method invoked on the proxy
	 * @param args   Array of objects containing the values of the arguments
	 * @return       Value to return from the method invocation on the proxy
	 * @exception Throwable Thrown from the actual method invocation
	 */
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
	{
		final Class decClass = method.getDeclaringClass();

		if (decClass == Object.class)
		{
			if (args == null)
			{
				if (method.getReturnType() == Integer.TYPE)
				{
					// Object.hashCode
					return new Integer(this.hashCode());
				}

				// Object.toString
				return this.toString();
			}

			// Object.equals
			boolean result = false;
			Object other = args[0];

			if (other == proxy)
			{
				result = true;
			}
			else if (other instanceof Meem)
			{
				result = ((Meem) other).getMeemPath().equals(this.meemPath);
			}

			return Boolean.valueOf(result);
		}

		if (args == null)
		{
			// Meem.getMeemPath
			return this.meemPath;
		}
		
		
		Reference reference = (Reference) args[0];
		Boolean automaticRemove = null;
		
		if (proxyAndCacheLCS) {
			if (method.getName().equals("addOutboundReference")) {
				
				automaticRemove = (Boolean) args[1];
				 
				String facetIdentifier = reference.getFacetIdentifier();
				if (facetIdentifier.equals("meemClientFacet")) {
					if (reference.getFilter() != null && reference.getFilter() instanceof FacetFilter) {
						FacetFilter filter = (FacetFilter) reference.getFilter();
						if (filter.match("lifeCycleClient", LifeCycleClient.class, Direction.OUTBOUND)) {
							// if its an exact match, then we don't need to hand it through to the real meem
							addMeemClientReference(reference, automaticRemove.booleanValue());
							return null;
						}
					}
					if (reference.getFilter() == null) {
						// if there isn't a filter
						// then we always send it across the wire. We also need to keep it though
						// so we can send any lifecycleclient addRefs.
						addMeemClientReference(reference, automaticRemove.booleanValue());
					}
				}
				if (facetIdentifier.equals("lifeCycleClient")) {
					addOutboundLifeCycleReference(reference, automaticRemove.booleanValue());
					return null;
				}
			}
			
			if (method.getName().equals("removeOutboundReference")) {
				
				String facetIdentifier = reference.getFacetIdentifier();
				if (facetIdentifier.equals("meemClientFacet")) {
					if (reference.getFilter() != null && reference.getFilter() instanceof FacetFilter) {
						FacetFilter filter = (FacetFilter) reference.getFilter();
						if (filter.match("lifeCycleClient", LifeCycleClient.class, Direction.OUTBOUND)) {
							removeMeemClientReference(reference);
							return null;
						}
					}
					if (reference.getFilter() == null) {
						removeMeemClientReference(reference);
					}
				}
				if (facetIdentifier.equals("lifeCycleClient")) {
					removeOutboundLifeCycleReference(reference);
					return null;
				}
			}
		}
		
		if (leasingEnabled) {
			MeemPath targetMeemPath = null;
			if (Proxy.getInvocationHandler(reference.getTarget()) instanceof MeemInvocationTarget) {
				MeemInvocationTarget mit = (MeemInvocationTarget)Proxy.getInvocationHandler(reference.getTarget());
				targetMeemPath = mit.getMeemPath();
			}
			if (targetMeemPath == null) {
				reference = new SmartProxyRemoteReference(reference, targetMeemPath);
			} else {
				reference = new RemoteReference(reference, targetMeemPath);
//				System.err.println("** " + meemPath + " : " + reference);
			}
			args[0] = reference;
		}
		
		
		try
		{
			if (DiagnosticLog.DIAGNOSE) {
				MeemPath sourceMeemPath = (MeemPath) InvocationContextTracker.getInvocationContext().get(InvocationContext.CALLING_MEEM_PATH);
				DiagnosticLog.log(new RemoteOutboundInvocationEvent(sourceMeemPath, meemPath, method, args));
			}
			Serializable[] sargs = null;
			if (args != null) {
				sargs = new Serializable[args.length];
				System.arraycopy(args, 0, sargs, 0, sargs.length);
			}

			InvocationContext invocationContext = InvocationContextTracker.getInvocationContext();
//			invocationContext.put(RequestStack.REQUEST_STACK, null);
			remoteMeem.majikInvocation("meem", method.getName(), method.getParameterTypes(), sargs, invocationContext);
			
		}
		catch (Throwable t)
		{
			//new ReflectionInvocation("meem", method, args, RequestTracker.getRequestStack()).fail();
			//System.err.println("SmartProxyMeem.invoke(): " + lease + " : " + meemPath + " : " + t.getMessage());
		}

		return null;
	}

	public String toString()
	{
		return "SmartProxyMeem " + meemPath;
	}
	
	/**
	 * @see org.openmaji.implementation.server.meem.wedge.remote.WeakReferenceProvider#obtainWeakReference()
	 */
	public WeakReference obtainWeakReference() {
		// if we haven't yet obtained a lease, lets wait a little bit
		// we will be notified if the lease turns up while we are waiting
		if (weakReference == null) {
			//long start =  System.currentTimeMillis();
			try {
				synchronized(waitObject) {
					waitObject.wait(2000);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
//			System.err.println("notify: " + meemPath + " : " + (System.currentTimeMillis() - start));
		}
		return weakReference;
	}
	
	public void dispose() {
		privateDispose();
		
		synchronized (smartProxyMeemMap) {
			SmartProxyMeem spm = (SmartProxyMeem) smartProxyMeemMap.get(meemPath);
			if (spm != null && spm != this) {
				spm.privateDispose();
			}
		}
	}
		
	private void privateDispose() {
		if (!disposed) {
//			System.err.println("SmartProxyMeem.dispose(): " + meemPath);
			disposed = true;
			
			if (weakReference != null) {
				weakReference.clear();
			}
			
			if (renewTask != null) {
				renewTask.cancel();
			}
			
			if (lease != null) {
				try {
					leaseRenewalManager.remove(((JiniLeaseWrapper)lease).getJiniLease());
				} catch (UnknownLeaseException e) {
				}
				lease = null;
			}			
			
			if (lifeCycleConnector != null) {
				lifeCycleConnector.disconnect();
				lifeCycleConnector.lifeCycleClient.lifeCycleStateChanged(LifeCycleTransition.LOADED_DORMANT);
			}
		} else {
//			System.err.println("SmartProxyMeem.dispose(): " + meemPath + " Already disposed");
		}
	}
	
	private synchronized Set getLifeCycleClientReferences()	{
		if (lifeCycleClientReferences == null) {
			lifeCycleClientReferences = new HashSet();
		}

		return lifeCycleClientReferences;
	}
	
	private synchronized Set getMeemClientReferences()	{
		if (meemClientReferences == null) {
			meemClientReferences = new HashSet();
		}

		return meemClientReferences;
	}

	private void notifyLastTransition(LifeCycleTransition transition) {
		synchronized (lifeCycleClientReferences) {
			Iterator iterator = getLifeCycleClientReferences().iterator();
			while (iterator.hasNext()) {
				Reference reference = (Reference) iterator.next();
				LifeCycleClient client = (LifeCycleClient) reference.getTarget();
				
				client.lifeCycleStateChanging(transition);
				client.lifeCycleStateChanged(transition); 
			}
		}
	}
	
	private void connectLease() {
		// connect LC facet		
		connectLifeCycleClient();

		waitObject = new Object();
		
		leaseRetryTime = 100;
		timer.schedule(new RenewLeaseTimerTask(), 1);
	}
	
	private final class LeaseListenerImpl implements LeaseListener {
		/**
		 * @see net.jini.lease.LeaseListener#notify(net.jini.lease.LeaseRenewalEvent)
		 */
		public void notify(LeaseRenewalEvent event) {
			renewingLease = true;
			lease = null;
			lifeCycleConnector.lifeCycleClient.lifeCycleStateChanged(LifeCycleTransition.LOADED_DORMANT);
			weakReference.clear();
			leaseRetryTime = 5000;
			attemptLeaseRenewal();
		}		
	}
	
	private void attemptLeaseRenewal() {
//		System.err.println("attemptLeaseRenewal(): " + meemPath + " : "+ leaseRetryTime);
		renewTask = new RenewLeaseTimerTask();
	  timer.schedule(renewTask, leaseRetryTime);		
	}
	
	private final class RenewLeaseTimerTask extends TimerTask {
		
		public void run() {
			if (disposed) {
				return;
			}
			try {
				lease = remoteMeem.obtainLease();
			} catch (RemoteException e) {
//				System.err.println("attemptLeaseRenewal: " + meemPath + " : "+ leaseRetryTime + "\n" +e.getMessage());
				// double lease retry time
				leaseRetryTime += leaseRetryTime;
				attemptLeaseRenewal();
				return;
			}
			
			synchronized (waitObject) {
//				System.err.println("notifying: " + meemPath);
				waitObject.notifyAll();
			}
			
			weakReference = new WeakReference(SmartProxyMeem.this);
			leaseRenewalManager.renewUntil(((JiniLeaseWrapper)lease).getJiniLease(), net.jini.core.lease.Lease.FOREVER, 30000L, new LeaseListenerImpl());
			renewingLease = false;			
//			System.err.println("SmartProxyMeem.connectLease(): " + meemPath + " : "+ lease + " : " + weakReference);
			lifeCycleConnector.reconnect();			
		}
	}
	
	private void addOutboundLifeCycleReference(Reference reference, boolean automaticRemove) {
		synchronized (lifeCycleClientReferences) {
			Facet target = reference.getTarget();
			ContentClient contentClient = MeemSystemWedge.getContentClientFromTarget(target);
	
			if (target instanceof LifeCycleClient) {
				if (reference.isContentRequired()) {
					LifeCycleClient lifeCycleClient = (LifeCycleClient) target;
					lifeCycleClient.lifeCycleStateChanged(lastTransition);
				}
	
				if (contentClient != null) {
					contentClient.contentSent();
				}
	
				if (!automaticRemove) {
					getLifeCycleClientReferences().add(reference);
					notifyReferenceChange(reference, true);
				}
			} else {
				if (contentClient != null) {
					contentClient.contentFailed("Target must be of type: " + LifeCycleClient.class);
				}
			}
		}
	}
	
	private synchronized void removeOutboundLifeCycleReference(Reference reference) {
		synchronized (lifeCycleClientReferences) {
			getLifeCycleClientReferences().remove(reference);
			notifyReferenceChange(reference, false);
		}
	}
	
	private void connectLifeCycleClient() {
		lifeCycleConnector = new LifeCycleConnector();
		lifeCycleConnector.connect();
	}
	
	private synchronized void addMeemClientReference(Reference reference, boolean automaticRemove) {
		Facet target = reference.getTarget();
		ContentClient contentClient = MeemSystemWedge.getContentClientFromTarget(target);

		if (target instanceof MeemClient) {
			if (reference.isContentRequired()) {
				MeemClient meemClient = (MeemClient) target;
				Iterator iterator = getLifeCycleClientReferences().iterator();
				while (iterator.hasNext()) {
					Reference ref = (Reference) iterator.next();
					meemClient.referenceAdded(ref);
				}
			}

			if (contentClient != null) {
				contentClient.contentSent();
			}

			if (!automaticRemove) {
				getMeemClientReferences().add(reference);
			}
		} else {
			if (contentClient != null) {
				contentClient.contentFailed("Target must be of type: " + MeemClient.class);
			}
		}
	}

	private synchronized void removeMeemClientReference(Reference reference) {
		getMeemClientReferences().remove(reference);
		notifyReferenceChange(reference, false);
	}
	
	private void notifyReferenceChange(Reference reference, boolean added) {
		Iterator iterator = getMeemClientReferences().iterator();
		while (iterator.hasNext()) {
			Reference ref = (Reference) iterator.next();
			MeemClient client = (MeemClient) ref.getTarget();

			if (added) {
				client.referenceAdded(reference);
			} else {
				client.referenceRemoved(reference);
			}
		}
	}
	
	private final class LifeCycleConnector {
		private boolean disposed = false;
		private boolean referenceRemoved = false;
		private Reference reference;
		public LifeCycleClient lifeCycleClient;

		public LifeCycleConnector() {
			lifeCycleClient = (LifeCycleClient) GatewayManagerWedge.getTargetFor(new LifeCycleClientImpl(), LifeCycleClient.class);
			reference = Reference.spi.create("lifeCycleClient", lifeCycleClient, true);
			reference = new SmartProxyRemoteReference(reference, meemPath);
		}
		
		public void connect() {
			disposed = false;
//			LogTools.warn(logger, "connect(): " + meemPath + " : " + lifeCycleClient + " : " + this);
			
			Serializable[] args = new Serializable[] {reference, Boolean.FALSE}; 
			
			try
			{
				// if we have just been unmarshalled, there will not be a request stack, so we set one in the IC
				// if there is already one, then it will be used
				InvocationContextTracker.getInvocationContext().put(RequestStack.REQUEST_STACK, RequestTracker.getRequestStack());
				InvocationContextTracker.getInvocationContext().put(InvocationContext.CALLING_MEEM_PATH, meemPath);
				
				Method method = Meem.class.getMethod("addOutboundReference", new Class[]{ Reference.class, boolean.class });
				
				if (DiagnosticLog.DIAGNOSE) {
					DiagnosticLog.log(new RemoteOutboundInvocationEvent(meemPath, meemPath, method, args));
				}
				
				InvocationContext invocationContext = InvocationContextTracker.getInvocationContext();
//				invocationContext.put(RequestStack.REQUEST_STACK, null);
				remoteMeem.majikInvocation("meem", "addOutboundReference", new Class[] {Reference.class, boolean.class}, args, invocationContext);
				
				referenceRemoved = false;
			}
			catch (Throwable t)
			{
				// -mg- work out what to do here.
				LogTools.info(logger, "connect exception: " + meemPath + ".", t);
			}
		}

		public void disconnect() {
			disposed = true;
			
			// only remove the reference if the lease is still valid. If it is invalid, then there is no connection. The SPMRemoteReference will clean up the reference.
			if (!renewingLease && !referenceRemoved) {
				try
				{
					referenceRemoved = true;
					
					InvocationContextTracker.getInvocationContext().put(InvocationContext.CALLING_MEEM_PATH, meemPath);
					InvocationContextTracker.getInvocationContext().put(RequestStack.REQUEST_STACK, RequestTracker.getRequestStack());
					
					Method method = Meem.class.getMethod("removeOutboundReference", new Class[]{ Reference.class });
					
					if (DiagnosticLog.DIAGNOSE) {
						DiagnosticLog.log(new RemoteOutboundInvocationEvent(meemPath, meemPath, method, new Object[] {reference}));
					}
					remoteMeem.majikInvocation("meem", "removeOutboundReference", new Class[] {Reference.class}, new Serializable[] {reference}, InvocationContextTracker.getInvocationContext());
				}
				catch (Throwable t)
				{
					// -mg- work out what to do here.
					LogTools.info(logger, "problem while disconnecting", t);
				}
			}
			
		}
		
		public void reconnect() {
			disconnect();
			connect();
		}

		public class LifeCycleClientImpl implements LifeCycleClient {
			public void lifeCycleStateChanging(LifeCycleTransition transition) {
			}
	
			public void lifeCycleStateChanged(LifeCycleTransition transition) {
//				LogTools.warn(logger, "lifeCycleStateChanged(): " + meemPath + " : " + disposed +  " : " + lifeCycleClient + " : " + transition);
				if (!disposed) {
					LifeCycleState actualCurrentState = lastTransition.getCurrentState();
					if (!transition.getCurrentState().equals(actualCurrentState)) {
						LifeCycleState expectedCurrentState = transition.getPreviousState();
	
						if (!expectedCurrentState.equals(actualCurrentState)) {
							int expectedIndex = LifeCycleState.STATES.indexOf(expectedCurrentState);
							int actualIndex = LifeCycleState.STATES.indexOf(actualCurrentState);
	
							int increment = actualIndex < expectedIndex ? 1 : -1;
	
							do {
								int nextIndex = actualIndex + increment;
	
								LifeCycleTransition nextTransition = new LifeCycleTransition((LifeCycleState) LifeCycleState.STATES
										.get(actualIndex), (LifeCycleState) LifeCycleState.STATES.get(nextIndex));
	
								notifyLastTransition(nextTransition);
	
								actualIndex = nextIndex;
							} while (actualIndex != expectedIndex);
						}
	
						notifyLastTransition(transition);
						
						lastTransition = transition;
	
						if (!renewingLease && lastTransition.equals(LifeCycleTransition.LOADED_DORMANT)) {
							privateDispose();
						}
					}
				}
			}
		}
	};
	
	
}
