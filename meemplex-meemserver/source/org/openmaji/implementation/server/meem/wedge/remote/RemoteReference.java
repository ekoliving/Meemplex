/*
 * @(#)RemoteReference.java
 *
 * Copyright 2004 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
package org.openmaji.implementation.server.meem.wedge.remote;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Vector;

import org.openmaji.implementation.server.classloader.SystemExportList;
import org.openmaji.implementation.server.manager.gateway.GatewayManagerWedge;
import org.openmaji.implementation.server.meem.invocation.ReflectionInvocation;
import org.openmaji.implementation.server.meem.invocation.RemoteInvocationHandler;
import org.openmaji.meem.Facet;
import org.openmaji.meem.Meem;
import org.openmaji.meem.MeemPath;
import org.openmaji.meem.filter.ExactMatchFilter;
import org.openmaji.meem.filter.Filter;
import org.openmaji.meem.wedge.reference.Reference;
import org.openmaji.server.helper.EssentialMeemHelper;
import org.openmaji.system.manager.registry.MeemRegistryClient;
import org.openmaji.system.manager.registry.MeemRegistryGateway;
import org.openmaji.system.meem.hook.invoke.Invocation;
import org.openmaji.system.meem.wedge.reference.ContentClient;
import org.openmaji.system.meemserver.MeemServer;



/**
 * @author mg
 */
public class RemoteReference implements Reference {
	
	private static final long serialVersionUID = 1506849712030951908L;

	protected final Reference reference;
	protected final MeemPath meemPath;
	private transient WeakReference weakReference = null;
	private boolean local = false;
	protected boolean queueing = true;
	private transient Vector queue = null;	
	private transient Facet targetProxy = null;
	private transient Reference registryReference = null;
	
	public RemoteReference(Reference reference, MeemPath meemPath) {
		this.reference = reference;
		this.meemPath = meemPath;
	}
	
	public String getFacetIdentifier() {
		return reference.getFacetIdentifier();
	}
	
	public Filter getFilter() {
		return reference.getFilter();
	}
	
	public Facet getTarget() {
		return getTargetProxy();
	}
	
	public boolean isContentRequired() {
		return reference.isContentRequired();
	}
	
	private Facet getTargetProxy() {
		if (targetProxy == null) {
			Facet facet = reference.getTarget();
			Class[] interfaces = facet.getClass().getInterfaces();
			
			ClassLoader classLoader = SystemExportList.getInstance().getClassLoaderFor(getSpecification(interfaces));
			targetProxy = (Facet) Proxy.newProxyInstance(classLoader, interfaces, new RemoteReferenceInvocationHandler(facet));
		}
		return targetProxy;
	}
	
	private String getSpecification(Class[] interfaces) {
  	for (int i = 0; i < interfaces.length; i++) {
  		Class iface = interfaces[i];
  		if (Facet.class.isAssignableFrom(iface)&& !iface.equals(Meem.class)) {
  			return iface.getName();
  		}
  	}
  	return Meem.class.getName();
  }
	
	public Object readResolve() {
		// If not deserialized in a Meemserver, return this without trying to obtain weak reference
		if (MeemServer.spi.getName() == null) {
			return this;
		}
		
		queue = new Vector();
		
		obtainWeakReference();
		
		return this;
	}
	
	private synchronized void obtainWeakReference() {
		synchronized (SmartProxyMeem.smartProxyMeemMap) {
			SmartProxyMeem smartProxyMeem = (SmartProxyMeem) SmartProxyMeem.smartProxyMeemMap.get(meemPath);
		
			if (smartProxyMeem != null) {
				weakReference = smartProxyMeem.obtainWeakReference();
				runQueuedInvocations();
			} else {
				
				// there is no SPM for this meempath running in this vm
				// kick off a locate
				
				MeemRegistryClientImpl meemRegistryClient = new MeemRegistryClientImpl();
				Facet proxy = GatewayManagerWedge.getTargetFor(meemRegistryClient, MeemRegistryClient.class);

				Filter filter = new ExactMatchFilter(meemPath);

				registryReference = Reference.spi.create("meemRegistryClient", proxy, true, filter);

				Meem meemRegistryGateway = EssentialMeemHelper.getEssentialMeem(MeemRegistryGateway.spi.getIdentifier());

				meemRegistryGateway.addOutboundReference(registryReference, false);
			}
		}
	}

	public boolean isValid() {
		if (local || queueing) {
			return true;
		}
		if (weakReference == null || weakReference.get() == null) {
			obtainWeakReference();
		}
		boolean valid = (weakReference != null && weakReference.get() != null) ? true : false; 
		if (!valid) {
			// tell the RemoteInvocationHandler to trash the queue for the target meem.
			RemoteInvocationHandler.dropMeem(meemPath);
			targetProxy = null;
		}
		return valid;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		return reference.equals(obj);
	}
	
	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return reference.hashCode();
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return getClass().getName() + "[" +
	    "facetIdentifier=" + getFacetIdentifier() +
	    ", target="          + getTarget() +
	    ", contentRequired=" + isContentRequired() +
	    ", filter="          + getFilter() +
	    ", valid="           + isValid() +
	    ", weakReference="   + weakReference +
	    "]";
	}
	
	private void runQueuedInvocations() {
		for (int i = 0; i < queue.size(); i++) {
			Invocation invocation = (Invocation) queue.get(i);
			invocation.invoke(reference.getTarget(), null);
		}
		queueing = false;
		queue.clear();
	}
	
	private final class MeemRegistryClientImpl implements MeemRegistryClient, ContentClient {
		//private Meem meem = null;
		public boolean contentSent = false;

		public MeemRegistryClientImpl() {
		}
		
		public void meemRegistered(Meem meem) {
			if (registryReference == null) {
				return;
			}
			
			Meem meemRegistryGateway = EssentialMeemHelper.getEssentialMeem(MeemRegistryGateway.spi.getIdentifier());
			meemRegistryGateway.removeOutboundReference(registryReference);
			registryReference = null;
			
			//this.meem = meem;
			
			if (!(Proxy.getInvocationHandler(meem) instanceof SmartProxyMeem)) {
				local = true;
				runQueuedInvocations();
				return;
			}
		
			obtainWeakReference();
		}

		public void meemDeregistered(Meem meem) {
			//this.meem = null;
		}
		
		public void contentSent() {
//			System.err.println("contentSent: " + meemPath);
		}
		
		public void contentFailed(String reason) {
//			System.err.println("contentFailed: " + meemPath + " : " + reason);
		}

	}
	
	public final class RemoteReferenceInvocationHandler implements InvocationHandler {
		
		private final Facet facet;
		
		public RemoteReferenceInvocationHandler(Facet facet) {
			this.facet = facet;
		}
		
		/**
		 * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object, java.lang.reflect.Method, java.lang.Object[])
		 */
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			if (method.getDeclaringClass() == Object.class)
			{
				if (args == null)
				{
					if (method.getReturnType() == Integer.TYPE)
					{
						// Object.hashCode
						return new Integer(this.hashCode());
					}

					// Object.toString
					return "RemoteReferenceInvocationHandler[" + meemPath.getLocation() + "]";
				}

				// Object.equals
				Object other = args[0];
				boolean result = false;

				if (other == proxy)
				{
					result = true;
				}
				else if (Proxy.isProxyClass(other.getClass()))
				{
					InvocationHandler ih = Proxy.getInvocationHandler(other);

					if (ih == this)
					{
						result = true;
					}
					else if (ih != null)
					{
						result = this.equals(ih);
					}
				} 

				return Boolean.valueOf(result);
			}
			
			final Invocation invocation = new ReflectionInvocation("", method, args);
			if (queueing) {
				queue.add(invocation);
			} else {
				// invoke				
				invocation.invoke(facet, null);
			}
			return null;
		}
		
		public Facet getFacet() {
			return facet;
		}
	}

}
