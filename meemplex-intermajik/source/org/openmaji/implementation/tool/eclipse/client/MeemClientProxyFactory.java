/*
 * @(#)MeemClientProxyFactory.java
 * Created on 25/04/2003
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.client;

import java.io.Serializable;
import java.security.PrivilegedAction;
import java.util.*;

import org.openmaji.implementation.common.VariableMapWedge;
import org.openmaji.implementation.server.nursery.pattern.MeemPatternWedge;
import org.openmaji.implementation.tool.eclipse.client.security.SecurityManager;
import org.openmaji.implementation.tool.eclipse.plugin.MajiPlugin;

import org.openmaji.meem.Meem;
import org.openmaji.meem.MeemPath;
import org.openmaji.meem.definition.*;
import org.openmaji.meem.wedge.lifecycle.LifeCycleState;
import org.openmaji.system.meem.MeemFactory;
import org.openmaji.system.meem.definition.MeemStructure;
import org.openmaji.system.space.Category;
import org.openmaji.system.spi.MajiSystemProvider;
import org.openmaji.system.spi.SpecificationType;


/**
 * <code>MeemClientProxyFactory</code> represents an abstract implementation of
 * factory for <code>MeemClientProxy</code>. It provides a standard caching 
 * machanism for and application
 * <p>
 * @author Kin Wong
 */
abstract public class MeemClientProxyFactory {
	Vector<String> systemWedgeClassNames = null;
	MeemClientProxyCache cache = new MeemClientProxyCache();
		
	/**
	 * Creates a <code>MeemClientProxy</code> for a Meem.<p>
	 * @param meem The meem which the newly created 
	 * <code>MeemClientProxy</code> represents.
	 * @return A new <code>MeemClientProxy</code> representing the meem.
	 */
	public MeemClientProxy create(final Meem meem) {
		// Should never relocate the meem again!!!!!
		MeemPath meemPath = meem.getMeemPath();
		
		MeemClientProxy proxy = checkCache(meemPath);
		if(proxy != null) {
			return proxy;
		}

		// not in cache - lock on meempath
		synchronized(meemPath.toString().intern()) {
			proxy = checkCache(meemPath);
			if(proxy != null) {
				return proxy;
			}
			Meem meemLocated = SecurityManager.getInstance().getGateway().getMeem(meemPath);
			proxy = new MeemClientProxy(createSynchronizer(), meemLocated);
			synchronized (cache) {
				if (checkCache(meemPath) != null) {
					System.err.println("ALREADY IN CACHE: " + meemPath);
				}
				cache.add(proxy);
			}
		}
		return proxy;
	}
	
	private MeemClientProxy checkCache(MeemPath meemPath) {
		synchronized (cache) {
			return cache.find(meemPath);
		}
	}

	/**
	 * Override this to return a ClientSynchronizer.<p>
	 * @param meemPath
	 */
	abstract protected ClientSynchronizer createSynchronizer();

	/**
	 * Locates a MeemClientProxy from its path.
	 * @param meemPath The path of the meem.
	 * @return MeemClientProxy The proxy that represents the meem at the location.
	 */
	public MeemClientProxy locate(final MeemPath meemPath) {
		return create(Meem.spi.get(meemPath));
	}
	
	protected class CreateAction implements PrivilegedAction
	{
		LifeCycleState	initialState;
		MeemDefinition definition;
		Meem lifeCycleManager;
		
		CreateAction(MeemDefinition definition, LifeCycleState	initialState, Meem lifeCycleManager) {
			this.initialState = initialState;
			this.definition = definition;
			this.lifeCycleManager = lifeCycleManager;
		}
		
		public Object run() {
			Meem meem = null;
			try {
				meem = createMeem(definition, initialState, lifeCycleManager);
			} 
			catch (Exception e) {
				e.printStackTrace();
			}
			return create(meem);
		}
	}
	
	/**
	 * Creates a Meem Client Proxy.<p>
	 * @return MeemClientProxy 
	 */
	public MeemClientProxy create(LifeCycleState initialState) {
		return (MeemClientProxy)new CreateAction(DefinitionFactory.createMeem(), initialState, MajiPlugin.getWorksheetLifeCycleManager().getUnderlyingMeem()).run();
	}
	
	/**
	 * Creates a MeemClientProxy together and its underlying meem.<p>
	 * @param definition
	 * @param initialState
	 */
	public MeemClientProxy create(
		MeemDefinition definition, LifeCycleState initialState) {
		return (MeemClientProxy)new CreateAction(definition, initialState, MajiPlugin.getWorksheetLifeCycleManager().getUnderlyingMeem()).run();
	}
	
	public MeemClientProxy create(
		MeemDefinition definition, LifeCycleState initialState, Meem lifeCycleManager) {
		return (MeemClientProxy)new CreateAction(definition, initialState, lifeCycleManager).run();
	}

	/**
	 * Creates a category proxy.
	 * @return CategoryClientProxy
	 */
	public MeemClientProxy createCategory(LifeCycleState initialState) {
		MeemDefinition definition = MeemDefinitionFactory.spi.create().createMeemDefinition(Category.class);
		Meem category = (Meem)new CreateAction(definition, initialState, MajiPlugin.getWorksheetLifeCycleManager().getUnderlyingMeem()).run();
		return create(category);
	}
	
	/**
	 * Clones the meem located by the meempath and return a meem client Proxy.
	 * @param meemPath
	 * @return MeemClientProxy
	 */
	public MeemClientProxy clone(MeemPath meemPath, LifeCycleState initialState, Meem lifeCycleManager) {
		MeemClientProxy proxy = locate(meemPath);
		MeemStructure meemStructure = proxy.getMetaMeem().getStructure();
		MeemDefinition definition = getMeemDefinition(meemStructure);
		if (lifeCycleManager == null) {
			return create(definition, initialState);
		} else {
			return create(definition, initialState, lifeCycleManager);
		}
	}

	private static Meem createMeem(MeemDefinition definition, LifeCycleState initialState, Meem lifeCycleManager) {
		Meem meem = MeemFactory.spi.get().
			create(		definition, 
							lifeCycleManager,
							initialState);
		return meem;
	}

  private MajiSystemProvider majiSystemProvider = null;

	// this is a bit of a hack until MetaMeem can give us application wedges only
	private MeemDefinition getMeemDefinition(MeemStructure meemStructure) {
		if (systemWedgeClassNames == null) {
      if (majiSystemProvider == null) {
        majiSystemProvider = MajiSystemProvider.systemProvider();
      } 
      
			systemWedgeClassNames = new Vector<String>();
	
			Collection<Class> systemWedgeSpecifications = new ArrayList<Class>();
			systemWedgeSpecifications.addAll(majiSystemProvider.getSpecifications(SpecificationType.SYSTEM_WEDGE));
			systemWedgeSpecifications.addAll(majiSystemProvider.getSpecifications(SpecificationType.SYSTEM_HOOK));
			Iterator systemWedgeIter = systemWedgeSpecifications.iterator();
      
			while (systemWedgeIter.hasNext()) {
        Class specification = (Class) systemWedgeIter.next();
        
        systemWedgeClassNames.add(majiSystemProvider.getSpecificationEntry(specification).getImplementation().getName());
			}
		}
		
		MeemDefinition meemDefinition = new MeemDefinition(meemStructure.getMeemAttribute());

		synchronized(meemStructure) {
			Iterator<Serializable> wedgeAttributeKeys = meemStructure.getWedgeAttributeKeys().iterator();
	
			while (wedgeAttributeKeys.hasNext()) {
				Serializable wedgeKey = wedgeAttributeKeys.next();
	
				WedgeDefinition wedgeDefinition = new WedgeDefinition(meemStructure.getWedgeAttribute(wedgeKey));
				
				if (systemWedgeClassNames.contains(wedgeDefinition.getWedgeAttribute().getImplementationClassName())) 
					continue;
					
				if(wedgeDefinition.getWedgeAttribute().getImplementationClassName().equals(VariableMapWedge.class.getName()))
					continue;
				
				if(wedgeDefinition.getWedgeAttribute().getImplementationClassName().equals(MeemPatternWedge.class.getName()))
					continue;
	
				meemDefinition.addWedgeDefinition(wedgeDefinition);
	
				Iterator<String> facetAttributeKeys = meemStructure.getFacetAttributeKeys(wedgeKey).iterator();
	
				while (facetAttributeKeys.hasNext()) {
					String facetKey = facetAttributeKeys.next();
	
					FacetDefinition facetDefinition = new FacetDefinition(meemStructure.getFacetAttribute(facetKey));
	
					wedgeDefinition.addFacetDefinition(facetDefinition);
				}
			}
		}

		return (meemDefinition);
	}
}
