/*
 * @(#)MeemClientProxy.java
 * Created on 27/04/2003
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.client;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;


import org.eclipse.jface.util.Assert;
import org.openmaji.meem.Facet;
import org.openmaji.meem.Meem;
import org.openmaji.meem.MeemPath;
import org.openmaji.meem.definition.DependencyAttribute;
import org.openmaji.meem.definition.LifeTime;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClient;
import org.openmaji.meem.wedge.lifecycle.LifeCycleState;
import org.openmaji.meem.wedge.lifecycle.LifeCycleTransition;
import org.openmaji.meem.wedge.reference.Reference;
import org.openmaji.system.meem.definition.MetaMeem;

/**
 * <code>MeemClientProxy</code> represents a proxy of a Maji meem.
 * <p>
 * <code>MeemClientProxy</code> allows application built with conventional 
 * programming paradigm to interact Maji Meem.
 * <p>
 * @author Kin Wong
 * @see org.openmaji.implementation.tool.eclipse.client.FacetProxy
 */

public class MeemClientProxy implements Meem {	

	private Meem meem;
	private MeemPath meemPath;
	private ClientSynchronizer synchronizer;
	private Map facetProxyMap = new HashMap();	// Maps class to facet proxy instance.
	private LinkedHashSet resetListeners;
	
	private LifeCycleProxy lifeCycleProxy;
	private MetaMeemProxy metaMeemProxy;
	
	private Boolean connecting = Boolean.FALSE;
	
	//=== Internal LifeCycleClient Implementation ================================	
	private LifeCycleClient lifeCycleClient = new LifeCycleClient() {
		public void lifeCycleStateChanging(LifeCycleTransition transition) {
		}
		public void lifeCycleStateChanged(LifeCycleTransition transition) {
			synchronized(connecting) {
				if (transition.equals(LifeCycleTransition.LOADED_DORMANT)) {		
					connecting = Boolean.FALSE;
					disconnectAll();
				} else 
				if (!connecting.booleanValue()) {
					int currentStateIndex = LifeCycleState.STATES.indexOf(transition.getCurrentState());
					
					if (currentStateIndex >= LifeCycleState.STATES.indexOf(LifeCycleState.LOADED)) {
						connecting = Boolean.TRUE;
						connectAll();
					}
				}
			}
		}
	};
		
	/**
	 * Constructs an instance of <code>MeemClientProxy</code> with no meem 
	 * attached. This constructor is intended to be used in derived class.
	 * <p>
	 */
	protected MeemClientProxy(ClientSynchronizer synchronizer, Meem meem) {
		Assert.isNotNull(meem);
		
		this.meem = meem;
		this.synchronizer = synchronizer;
		this.meemPath = meem.getMeemPath();
		
		// Listen to LifeCycle
		getLifeCycle().addClient(lifeCycleClient);	
	}
	
	/**
	 * Checks whether the underlying meem is active.<p>
	 * Active is defined as a state above DORMANT.<p>
	 * @return true if the underlying meem is active, false otherwise.
	 */
	protected boolean isActive() {
		LifeCycleState state = getLifeCycleState();
		return 
		(!state.equals(LifeCycleState.DORMANT)) &&
		(!state.equals(LifeCycleState.ABSENT));
	}
	
	/**
	 * Gets the life cycle state of this meem client proxy.<p>
	 * @return The <code>LifeCycleState</code> of this proxy.
	 */
	public LifeCycleState getLifeCycleState() {
		return getLifeCycle().getState();
	}
	
	/**
	 * Adds a <code>ResetListener</code> to this client proxy.<p>
	 * @param listener A <code>ResetListener</code> that listen to reset 
	 * notification.
	 */	
	public void addResetListener(ResetListener listener) {
		if(resetListeners == null) resetListeners = new LinkedHashSet();
		if(resetListeners.contains(listener)) return;
		resetListeners.add(listener);
	}
	
	/**
	 * Removes a <code>ResetListener</code> from this client proxy.<p>
	 * @param listener A <code>ResetListener</code> to be removed from this client 
	 * proxy.
	 */
	public void removeResetListener(ResetListener listener) {
		if(resetListeners == null) return;
		resetListeners.remove(listener);
	}
	
	/**
	 * Gets the UI synchronizer associates with this client proxy.
	 * <p>
	 * @return ClientSynchronizer The UI synchronizer associates with this client 
	 * proxy.
	 */
	public ClientSynchronizer getSynchronizer() {
		return synchronizer;
	}
	
	/**
	 * Gets the underlying meem that this proxy is representing.
	 * <p>
	 * @return The underlying meem that this proxy is representing, or null if
	 * there the meem is not resolved.
	 */
	public Meem getUnderlyingMeem() {
		return meem;
	}
	
	/**
	 * Gets the facet proxy of a particalar type.<p>
	 * If the facet proxy has been created it will return the cached one, 
	 * otherwise a new one is dynamically created and optionally connected.
	 * <p>
	 * @param type The type of facet proxys and it must be derived from 
	 * <code>FacetProxy</code>.
	 * @return A <code>FacetProxy</code> the facet proxy of the desired type, 
	 * or null if it can not be instantiated.
	 */
	public FacetProxy getFacetProxy(Class type) {
		if (type == LifeCycleClient.class) return getLifeCycle();
		
		if (type == MetaMeem.class) return getMetaMeem();

		FacetProxy proxy = (FacetProxy)facetProxyMap.get(type);
		if(proxy != null) return proxy;
		
		proxy = FacetProxyFactory.getInstance().create(this, type);
		if(proxy == null) return null;

		facetProxyMap.put(type, proxy);
		if(isActive()) {
			connectProxy(proxy);
		}
		return proxy;
	}
	
	/**
	 * Removes the facet proxy previously create by 
	 * <code>getFacetProxy()</code>.<p>
	 * @param facetProxy The facet proxy to be removed.
	 * @return true if the facet proxy has been removed, false otherwise.
	 */
	protected boolean removeFacetProxy(FacetProxy facetProxy) {
		Assert.isNotNull(facetProxy);
		if(facetProxy == lifeCycleProxy) return false; // Can't remove LifeCycle

		FacetProxy proxy = (FacetProxy)facetProxyMap.get(facetProxy.getClass());
		if(facetProxy !=  proxy) return false;

		facetProxyMap.remove(facetProxy.getClass());
//		if(isActive()) {
			disconnectProxy(proxy);
//		}
		return true;
	}
	
	/**
	 * Connects to the facet proxy with security context.<p>
	 * @param facetProxy The facet proxy to be connected.
	 */
	private void connectProxy(final FacetProxy facetProxy) {
		if(facetProxy.isConnected()) return;

		facetProxy.connect();
	}
	
	/**
	 * Disconnect the facet proxy with the security context.<p>
	 * @param facetProxy The facet proxy to be disconnected.
	 */
	private void disconnectProxy(final FacetProxy facetProxy) {
		if(!facetProxy.isConnected()) return;

		facetProxy.disconnect();
	}
	
	/**
	 * Gets the <code>LifeCycleProxy</code> of this meem proxy.<p>
	 * @return LifeCycleProxy The <code>LifeCycleProxy</code> associates with this
	 * meem proxy.
	 * <p>
	 * @see LifeCycleProxy
	 */	
	public LifeCycleProxy getLifeCycle() {
		if (lifeCycleProxy == null) {
			lifeCycleProxy = new LifeCycleProxy(this);
			connectProxy(lifeCycleProxy);
		}
		return lifeCycleProxy;
	}
	
	/**
	 * Gets the <code>MetaMeemProxy</code> of this meem proxy.<p>
	 * @return MetaMeemProxy The <code>MetaMeemProxy</code> associates with this
	 * meem proxy.
	 * @see MetaMeemProxy
	 */
	public MetaMeemProxy getMetaMeem() {
		if (metaMeemProxy == null) {
			metaMeemProxy = new MetaMeemProxy(this);
			connectProxy(metaMeemProxy);
		}
		
		return metaMeemProxy;
	}
	
	/**
	 * Gets the <code>LifeCycleLimitProxy</code> of this meem proxy.
	 * <p>
	 * @return The <code>LifeCycleLimitProxy</code> associates with this
	 * meem proxy.
	 * @see LifeCycleLimitProxy
	 */	
	public LifeCycleLimitProxy getLifeCycleLimit() {
		return (LifeCycleLimitProxy)getFacetProxy(LifeCycleLimitProxy.class);
	}
	
	/**
	 * Gets the <code>ErrorHandlerProxy</code> of this meem proxy.
	 * <p>
	 * @return The <code>ErrorHandlerProxy</code> associates 
	 * with this meem proxy.
	 * @see ErrorHandlerProxy
	 */
	public ErrorHandlerProxy getErrorHandler() {
		return (ErrorHandlerProxy)getFacetProxy(ErrorHandlerProxy.class);
	}
	
	/**
	 * Gets the <code>VariableMapProxy</code> of this meem proxy.
	 * <p>
	 * @return A  <code>VariableMapProxy</code> associates with this meem proxy.
	 * @see VariableMapProxy
	 */
	public VariableMapProxy getVariableMapProxy() {
		return (VariableMapProxy)getFacetProxy(VariableMapProxy.class);
	}
	
	/**
	 * Gets the <code>CategoryProxy</code> of this meem client proxy.
	 * @return CategoryProxy The <code>CategoryProxy</code> associates with this
	 * meem client proxy.
	 */
	public CategoryProxy getCategoryProxy() {
		return (CategoryProxy)getFacetProxy(CategoryProxy.class);
	}

	/**
	 * Gets the <code>CategoryProxy</code> of this meem client proxy.
	 * @return CategoryProxy The <code>CategoryProxy</code> associates with this
	 * meem client proxy.
	 */
	public SubsystemFactoryProxy getSubsystemFactoryProxy() {
		return (SubsystemFactoryProxy)getFacetProxy(SubsystemFactoryProxy.class);
	}

	/**
	 * Gets the <code>LCMCategoryProxy</code> of this meem proxy.
	 * <p>
	 * @return The <code>LCMCategoryProxy</code> associates with this meem proxy.
	 * @see LCMCategoryProxy
	 */
	public LCMCategoryProxy getLCMCategoryProxy() {
		return (LCMCategoryProxy)getFacetProxy(LCMCategoryProxy.class);
	}

	/**
	 * Gets the <code>ConfigurationHandlerProxy</code> of this meem proxy.
	 * <p>
	 * @return A <code>ConfigurationHandlerProxy</code> associates with this meem 
	 * proxy.
	 * @see ConfigurationHandlerProxy
	 */
	public ConfigurationHandlerProxy getConfigurationHandler() {
		return (ConfigurationHandlerProxy)
			getFacetProxy(ConfigurationHandlerProxy.class);
	}
	
	/**
	 * Gets the <code>SubsystemProxy</code> of this meem proxy.
	 * <p>
	 * @return A <code>SubsystemProxy</code> associates with this meem 
	 * proxy.
	 * @see SubsystemProxy
	 */
	public SubsystemProxy getSubsystem() {
		return (SubsystemProxy)
			getFacetProxy(SubsystemProxy.class);
	}
	
	public LifeCycleManagementClientProxy getLifeCycleManagementClient() {
		return (LifeCycleManagementClientProxy)
			getFacetProxy(LifeCycleManagementClientProxy.class);
	}
	
	/**
	 * @param specification
	 */
	public boolean isA(final Class specification) {
		return getMetaMeem().hasFacet(specification);
	}
	
	//=== Meem Implementation ==================================================
	/**
	 * @see org.openmaji.meem.Meem#getMeemPath()
	 */
	public MeemPath getMeemPath() {
		return meemPath;
		}
	
	public void addDependency(Facet facet, DependencyAttribute dependencyAttribute, LifeTime lifeTime) {
		meem.addDependency(facet, dependencyAttribute, lifeTime);
	}
	
	public void addDependency(String facetIdentifier, DependencyAttribute dependencyAttribute, LifeTime lifeTime) {
		meem.addDependency(facetIdentifier, dependencyAttribute, lifeTime);
	}
	
	public void removeDependency(DependencyAttribute dependencyAttribute) {
		meem.removeDependency(dependencyAttribute);
	}

	public void updateDependency(DependencyAttribute dependencyAttribute) {
		meem.updateDependency(dependencyAttribute);
	}
	
	
	/**
	 */
	public void addOutboundReference(final Reference reference, final boolean automaticRemove) {
		meem.addOutboundReference(reference, automaticRemove);
	}

	/**
	 */
	public void removeOutboundReference(final Reference reference) {
		meem.removeOutboundReference(reference);
	}
	
	private void connectAll() {
		getLifeCycle().connect();
		
		// wait until lifecycle is connected, then connect everything else
		
		Runnable runnable = new Runnable() {
			public void run() {
				while (!getLifeCycle().isConnected()) {
					try {
						Thread.sleep(250);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				getMetaMeem().connect();
				
				while (!getMetaMeem().isContentInitialized()) {
					try {
						Thread.sleep(250);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				final FacetProxy[] facetProxies = (FacetProxy[])facetProxyMap.values().toArray(new FacetProxy[0]);

				for(int i = 0; i < facetProxies.length; i++) {
					facetProxies[i].connect();
				}
			}
		};
		
		new Thread(runnable).start();	
		
	}
	
	/**
	 * Disconnects all facet proxies.<p>
	 */
	protected void disconnectAll() {
//		System.err.println("MeemClientProxy.disconnectAll(): " + meemPath);
		
		final FacetProxy[] 
		facetProxies = (FacetProxy[])facetProxyMap.values().toArray(new FacetProxy[0]);
		for(int i = 0; i < facetProxies.length; i++) {
//			System.err.println("disconnecting: " + facetProxies[i] + " : " + this);
			facetProxies[i].disconnect();
		}
		getMetaMeem().disconnect();
		getLifeCycle().disconnect();
		
	}
}
