/*
 * @(#)MeemBuilder.java
 * Created on 19/05/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic.builders;


import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

import org.openmaji.implementation.tool.eclipse.client.MetaMeemStub;
import org.openmaji.implementation.tool.eclipse.editor.common.model.Element;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.EntryOf;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.Meem;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.Wedge;
import org.openmaji.meem.definition.WedgeAttribute;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClient;
import org.openmaji.meem.wedge.lifecycle.LifeCycleState;
import org.openmaji.meem.wedge.lifecycle.LifeCycleTransition;
import org.openmaji.system.meem.definition.MeemStructure;
import org.openmaji.system.meem.definition.MetaMeem;



/**
 * <code>MeemBuilder</code> builds the view model meem based on system facet 
 * <code>MetaMeem</code>. It listens to changes made to wedges contained in the
 * meem and update the view model accordingly.
 * <p>
 * @author Kin Wong
 */
public class MeemBuilder extends ElementContainerBuilder {
	//=== Internal LifeCycle Implementation ======================================
	private LifeCycleClient lifeCycleClient = new LifeCycleClient() {
		public void lifeCycleStateChanging(LifeCycleTransition transition) {
			//don't care
		}
		public void lifeCycleStateChanged(LifeCycleTransition transition) {
			setLifeCycleState(transition.getCurrentState());
			if(transition.equals(LifeCycleTransition.LOADED_DORMANT) || transition.equals(LifeCycleTransition.DORMANT_LOADED)) {
				rebuildParent();
			}
		}
	};

	//=== Internal MetaMeemClient Implementation =================================
	private MetaMeem metaMeemClient = new MetaMeemStub() {
		public void addWedgeAttribute(WedgeAttribute wedgeAttribute) {
			MeemBuilder.this.addWedgeAttribute(wedgeAttribute);
		}
		
		public void updateWedgeAttribute(WedgeAttribute wedgeAttribute) {
			MeemBuilder.this.updateWedgeAttribute(wedgeAttribute);
		}
		
		public void removeWedgeAttribute(Object wedgeKey) {
			MeemBuilder.this.removeWedgeAttribute(wedgeKey);
		}
	};

	/**
	 * Constructs an instance of <code>MeemBuilder</code>.
	 * <p>
	 * @param meem The meem model to be built.
	 */
	public MeemBuilder(Meem meem) {
		super(meem);
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.kinetic.builders.ElementBuilder#getConnectableKey()
	 */
	protected Object getConnectableKey() {
		return getMeem().getMeemPath();
	}

	/**
	 * Gets the meem associates with this meem builder.
	 * <p>
	 * @return Meem The meem associates with this meem builder.
	 */
	protected Meem getMeem() {
		return (Meem)getModel();
	}
	
	/**
	 * Activates this meem builder by connecting its implementation of 
	 * MetaMeem as a client to the MetaMeem facet proxy.
	 * <p>
	 * @see org.openmaji.implementation.tool.eclipse.editor.kinetic.builders.ElementBuilder#activate()
	 */	
	public void activate() {
		getMeem().getProxy().getLifeCycle().addClient(lifeCycleClient);
//		getMeem().getProxy().getMetaMeem().addClient(metaMeemClient);
	}
	
	/**
	 * Deactivates this meem builder by disconnecting its implementation of 
	 * MetaMeem as a client from the MetaMeem facet proxy.
	 * <p>
	 * @see org.openmaji.implementation.tool.eclipse.editor.kinetic.builders.ElementBuilder#deactivate()
	 */
	public void deactivate() {
//		getMeem().getProxy().getMetaMeem().removeClient(metaMeemClient);
		getMeem().getProxy().getLifeCycle().removeClient(lifeCycleClient);
	}
	
	/**
	 * Gets the meem structure used for building the meem.
	 * <p>
	 * @return MeemStructure The meem attribute used for building the meem.
	 */
	protected MeemStructure getStructure() {
		return getMeem().getProxy().getMetaMeem().getStructure();
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.kinetic.builders.ElementBuilder#buildContents()
	 */
	protected void refreshContents() {
		// Build a local map of what wedges are in the view model now.
		Map tempWedges = createChildElementMap();	// Maps attribute keys to wedges
	
		// Going through all wedge attributes in the MetaMeem	
		MeemStructure structure = getStructure();
		synchronized (structure) {
			Iterator<Serializable> itWedgeAttributeKey = structure.getWedgeAttributeKeys().iterator();
			while(itWedgeAttributeKey.hasNext()) {
				Serializable wedgeAttributeKey = itWedgeAttributeKey.next();
				WedgeAttribute wedgeAttribute = getStructure().getWedgeAttribute(wedgeAttributeKey);
				Wedge wedge = (Wedge)tempWedges.get(wedgeAttributeKey);
				
				if(wedge == null) {
					// Wedge Attribute is NOT found, create a new wedge
					addWedgeAttribute(wedgeAttribute);
				}
				if(wedge != null) {
					// Wedge Attribute is found, update the Wedge
					getChildBuilder(wedge.getId()).refresh();
					tempWedges.remove(wedge.getId());
				}
			}
		}

		// Removing all orphan wedges
		Iterator itRemovingWedge = tempWedges.keySet().iterator();
		while(itRemovingWedge.hasNext()) removeChild(itRemovingWedge.next());
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.kinetic.builders.ElementBuilder#clearContents()
	 */
	protected void clearContents() {
		// Going through all wedges in the view model		
		Element[] children = createChildElementArray();
		for(int i = 0; i < children.length; i++) {
			Wedge wedge = (Wedge)children[i];
			removeWedgeAttribute(wedge.getAttributeIdentifier());
		}
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.kinetic.builders.ElementBuilder#refreshConnections()
	 */
	protected void refreshConnections() {
	}

	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.kinetic.builders.ElementBuilder#clearConnections()
	 */
	protected void clearConnections() {
		EntryOf[] entryOfs = 
			(EntryOf[])getMeem().getEntriesToCategory().toArray(new EntryOf[0]);
			
		for(int i = 0; i < entryOfs.length; i++) {
			EntryOf entryOf = entryOfs[i];
			getRoot().registerUnresolvedConnection(entryOf);

			getRoot().removeConnection(entryOf);
			entryOf.detach();
			entryOf.setCategory(null);
			entryOf.setMeem(null);
			entryOf.attach();
		}
	}
	
	protected void rebuildParent() {
		if(!(getParent() instanceof DiagramBuilder)) return;
		DiagramBuilder diagramBuilder = (DiagramBuilder)getParent();
		diagramBuilder.refreshCategoryEntry(getMeem().getMeemPath());
	}


	protected void setLifeCycleState(LifeCycleState state) {}
	
	/**
	 * Adds a wedge view model from a <code>WedgeAttribute</code>.
	 * <p>
	 * @param wedgeAttribute The wedge attribute the wedge view model represents.
	 */
	protected void addWedgeAttribute(WedgeAttribute wedgeAttribute) {
		Wedge wedge = (Wedge)getMeem().findElement(wedgeAttribute.getIdentifier());

		if(wedge == null) {
			wedge = new Wedge(wedgeAttribute);
			getContainerModel().addChild(wedge);
		}
		
		ElementBuilder builder = getChildBuilder(wedge.getId());
		if(builder == null) {
			addChildBuilder(new WedgeBuilder(wedge));
		}
		if(!loadVariable(wedge)) addVariable(wedge);
	}

	/**
	 * Updates a wedge view model from a <code>WedgeAttribute</code>.
	 * <p>
	 * @param wedgeAttribute The wedge attribute the wedge view model represents.
	 */
	protected void updateWedgeAttribute(WedgeAttribute wedgeAttribute) {
		Element wedge = getContainerModel().findElement(wedgeAttribute.getIdentifier());
		if(wedge != null) getContainerModel().refreshChild(wedge);
	}

	/**
	 * Removes a wedge view model identified by a wedge key.
	 * <p>
	 * @param wedgeKey The key that identifies wedge view model.
	 */
	protected void removeWedgeAttribute(Object wedgeKey) {
		Wedge wedge = (Wedge)getMeem().findElement(wedgeKey);
		if(wedge != null) removeChild(wedge.getId());
	}
	
	/**
	 * @see org.openmaji.implementation.tool.eclipse.editor.kinetic.builders.ElementBuilder#setParent(org.openmaji.implementation.tool.eclipse.editor.kinetic.builders.ElementBuilder)
	 */
	protected void setParent(ElementBuilder parent) {
		super.setParent(parent);
		if (getParent() != null) {
			// connect metameem proxy			
			getMeem().getProxy().getMetaMeem().addClient(metaMeemClient);
		} else {
			// disconnect metameem proxy		
			getMeem().getProxy().getMetaMeem().removeClient(metaMeemClient);
		}
	}
}
