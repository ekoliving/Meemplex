/*
 * @(#)FacetBuilder.java
 * Created on 16/10/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic.builders;

import java.io.Serializable;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.security.auth.Subject;

import org.openmaji.implementation.tool.eclipse.client.MeemClientProxy;
import org.openmaji.implementation.tool.eclipse.client.MetaMeemStub;
import org.openmaji.implementation.tool.eclipse.client.security.SecurityManager;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.Category;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.Dependency;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.Facet;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.Meem;
import org.openmaji.implementation.tool.eclipse.util.Pair;
import org.openmaji.meem.MeemPath;
import org.openmaji.meem.definition.DependencyAttribute;
import org.openmaji.meem.definition.DependencyType;
import org.openmaji.server.helper.MeemPathResolverHelper;
import org.openmaji.system.meem.definition.MeemStructure;
import org.openmaji.system.meem.definition.MetaMeem;



/**
 * <code>FacetBuilder</code>.
 * <p>
 * @author Kin Wong
 */
public class FacetBuilder extends ElementBuilder {
	//=== Internal MetaMeemClient Implementation =================================
	private MetaMeem metaMeemClient = new MetaMeemStub() {
	
		public void addDependencyAttribute(String facetKey, DependencyAttribute dependencyAttribute) {
			if(!getFacet().getAttributeIdentifier().equals(facetKey)) return;
			updateFacet();
			FacetBuilder.this.addDependencyAttribute(dependencyAttribute);
		}
		
		public void updateDependencyAttribute(DependencyAttribute dependencyAttribute) {
			updateFacet();
			FacetBuilder.this.updateDependencyAttribute(dependencyAttribute);
		}
		
		public void removeDependencyAttribute(Serializable dependencyKey) {
			updateFacet();
			FacetBuilder.this.removeDependencyAttribute(dependencyKey);
		}
	};
	
	/**
	 * Constructs an instance of <code>FacetBuilder</code>.
	 * <p>
	 */
	public FacetBuilder(Facet facet) {
		setModel(facet);
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.kinetic.builders.ElementBuilder#getConnectableKey()
	 */
	protected Object getConnectableKey() {
		MeemPath meemPath = getMeem().getMeemPath(); 
		return new Pair(meemPath, getFacet().getAttribute().getIdentifier());
	}
	
	/**
	 * Gets the model associates with this facet builder as facet.
	 * @return Facet The model associates with this facet builder as facet.
	 */
	protected Facet getFacet() {
		return (Facet)getModel();
	}

	protected Meem getMeem() {
		return getFacet().getMeem();
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.kinetic.builders.ElementBuilder#activate()
	 */
	public void activate() {
		getMeem().getProxy().getMetaMeem().addClient(metaMeemClient);
	}

	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.kinetic.builders.ElementBuilder#deactivate()
	 */
	public void deactivate() {
		getMeem().getProxy().getMetaMeem().removeClient(metaMeemClient);
	}

	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.kinetic.builders.ElementBuilder#buildContents()
	 */
	protected void refreshContents() {
		updateFacet();
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.kinetic.builders.ElementBuilder#clearContents()
	 */
	protected void clearContents() {
	}

	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.kinetic.builders.ElementBuilder#buildConnections()
	 */
	protected void refreshConnections() {
		Map tempDependencies = createDependencies();
	
		// Going through all the dependencies in MetaMeem.
		MeemStructure structure = getMeem().getProxy().getMetaMeem().getStructure();

		Serializable dependencyKey = structure.getDependencyKeyFromFacetKey(getFacet().getAttributeIdentifier());
		if(dependencyKey != null) {
			Dependency dependency = (Dependency)tempDependencies.get(dependencyKey);
			if(dependency == null) {
				// Add this new dependency
				DependencyAttribute attribute = structure.getDependencyAttribute(dependencyKey);
				if(attribute != null) {
					addDependencyAttribute(attribute);
				}
			}
			else {
				// remove dependency from the temporary map.
				tempDependencies.remove(dependencyKey);
			}
		}

		// Remove all the meem in the view model but not in the category.
		Iterator it = tempDependencies.values().iterator();
		while(it.hasNext()) {
			Dependency dependencyRemoving = (Dependency)it.next();
			removeDependencyAttribute(dependencyRemoving.getAttributekey());
		}
		updateFacet();
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.kinetic.builders.ElementBuilder#clearConnections()
	 */
	protected void clearConnections() {
		// Get all the dependency which this is the source
		Dependency[] dependencies = 
			(Dependency[])getFacet().getSourceConnections().toArray(new Dependency[0]);
		for(int i = 0; i < dependencies.length; i++) {
			removeDependencyAttribute(dependencies[i].getAttributekey());
		}
		
		// Get all the dependency which this is the target
		dependencies = (Dependency[])getFacet().getTargetConnections().toArray(new Dependency[0]);
		
		for(int i = 0; i < dependencies.length; i++) {
			getRoot().registerUnresolvedConnection(dependencies[i]);
			removeDependencyAttribute(dependencies[i].getAttributekey());
		}
	}


	/**
	 * Creates a map that maps element ids to elements.
	 * <p>
	 * @return Map A map that maps element ids to elements.
	 */
	protected Map createDependencies() {
		return new HashMap(getFacet().getSourceConnectionsMap());
	}

	/**
	 * Adds a dependency to the view model.
	 * @param dependencyAttribute The new dependency based on the dependency 
	 * attribute.
	 */
	protected void addDependencyAttribute(DependencyAttribute dependencyAttribute) {
		DependencyType type = dependencyAttribute.getDependencyType();
		
		if(	type.equals(DependencyType.STRONG) ||
				type.equals(DependencyType.WEAK)) {
				// Add single Dependency
				addSingleDependencyAttribute(dependencyAttribute);
		}
		else {
			// Add many dependency
			addManyDependencyAttribute(dependencyAttribute);
		}
	}
	
	/**
	 * Adds a "single" dependency to the view model based on the given dependency 
	 * attribute.
	 * <p>
	 * @param dependencyAttribute The dependency attribute for the new "single" 
	 * dependency.
	 */
	private void addSingleDependencyAttribute(DependencyAttribute dependencyAttribute) {
		Dependency dependency = 
			(Dependency)getRoot().findConnection(dependencyAttribute.getKey());
		if(dependency != null) return;
		
		MeemPath meemPath = getDefinitiveMeemPath(dependencyAttribute.getMeemPath());
		
		// Unable to find the dependency view model, create a new one.
		Pair targetKey = new Pair(	meemPath,
																dependencyAttribute.getFacetIdentifier());
																			
		ElementBuilder targetBuilder = getRoot().findConnectableBuilder(targetKey);
		if((targetBuilder == null) || 
		(!(targetBuilder.getModel() instanceof Facet))) {
			getRoot().registerUnresolvedConnection(
				getConnectableKey(), 
				targetKey, 
				dependencyAttribute.getKey());
			return;
		}
		
		Facet targetFacet = (Facet)targetBuilder.getModel();
		dependency = new Dependency(dependencyAttribute);
		dependency.setSourceFacet(getFacet());
		dependency.setTarget(targetFacet);
		dependency.attach();

		getRoot().addConnection(this, dependency, targetKey);
		if(!loadVariable(dependency)) addVariable(dependency);
	}

	private void updateFacet() {
		MeemClientProxy proxy = getMeem().getProxy();
		Facet facet = getFacet();

		Serializable dependencyKey =
			proxy.getMetaMeem().getStructure().
			getDependencyKeyFromFacetKey(getFacet().getAttributeIdentifier());
			facet.setDependencyKey(dependencyKey);
	}
	
	/**
	 * Adds a "many" dependency to the view model based on the given dependency 
	 * attribute.
	 * <p>
	 * @param dependencyAttribute The dependency attribute for the new "many" 
	 * dependency.
	 */
	private void addManyDependencyAttribute(DependencyAttribute dependencyAttribute) {
		Dependency dependency = 
			(Dependency)getRoot().findConnection(dependencyAttribute.getKey());
		if(dependency != null) return;
		
		MeemPath meemPath = getDefinitiveMeemPath(dependencyAttribute.getMeemPath());

		// Unable to find the dependency view model, create a new one.
		Meem meem = getRoot().findMeem(meemPath);
		if((meem == null) || (!(meem instanceof Category))) {
			getRoot().
				registerUnresolvedConnection(	getConnectableKey(), 
																			meemPath,
																			dependencyAttribute.getKey());
			return;
		}

		Category category = (Category)meem;
		dependency = new Dependency(dependencyAttribute);
		dependency.setSourceFacet(getFacet());
		dependency.setTarget(category);
		dependency.attach();

		getRoot().addConnection(this, dependency, meemPath);
		if(!loadVariable(dependency)) addVariable(dependency);
	}
	
	protected void updateDependencyAttribute(DependencyAttribute dependencyAttribute){
		Dependency dependency = 
			(Dependency)getRoot().findConnection(dependencyAttribute.getKey());
		if(dependency == null) return;
		dependency.updateAttribute(dependencyAttribute);
	}

	protected void removeDependencyAttribute(Object dependencyKey) {
		Dependency dependency = 
			(Dependency)getRoot().findConnection(dependencyKey);
		if(dependency == null) return;
		
		deleteVariable(dependency.getPath());
		getRoot().removeConnection(dependency);
		dependency.detach();
		dependency.setTarget(null);
		dependency.setSource(null);
		dependency.attach();
	}
	
	private MeemPath getDefinitiveMeemPath(final MeemPath meemPath) {
		if (!meemPath.isDefinitive()) {
			PrivilegedAction priv = new PrivilegedAction() {
					public Object run() {
						return MeemPathResolverHelper.getInstance().resolveMeemPath(meemPath);
					}
			};
			org.openmaji.meem.Meem resolvedMeem = (org.openmaji.meem.Meem) Subject.doAs(SecurityManager.getInstance().getSubject(), priv);
			if (resolvedMeem != null) {
						return resolvedMeem.getMeemPath();
					} else {
						return meemPath;
					}
		} else {
			return meemPath;
		}
	}
}
