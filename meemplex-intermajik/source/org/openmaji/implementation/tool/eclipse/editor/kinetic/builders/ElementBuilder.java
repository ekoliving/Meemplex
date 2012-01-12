/*
 * @(#)ElementBuilder.java
 * Created on 9/10/2003
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic.builders;

import java.util.HashMap;
import java.util.Map;

import org.openmaji.implementation.intermajik.model.ElementPath;
import org.openmaji.implementation.tool.eclipse.editor.common.model.Element;


/**
 * <code>ElementBuilder</code> provides based implementation of a view model 
 * building logic for both full and incremental construction of the view model
 * based on Maji meem.
 * <p>
 * <code>ElementBuilder</code> connects to a Maji concept and construct the 
 * corresponding view model accordingly. This construction can be full or 
 * incremental, meaning it should be the place to handle both distributed 
 * persistence and distributed update.
 * <p>
 * @see org.openmaji.implementation.tool.eclipse.editor.common.model.ElementContainer
 * @author Kin Wong
 */
abstract public class ElementBuilder {
	protected ConfigurationBuilder root;
	private Element model;
	private ElementBuilder parent;
	private Map childBuilders;
	
	/**
	 * Constructs an instance of <code>ElementBuilder</code>.
	 * <p>
	 */
	protected ElementBuilder() {
	}
	
	/**
	 * Gets the id of the builder that uniquely identifies this build in its 
	 * parent builder, which must be the id of the model.
	 * @return Object The id of the builder.
	 */
	protected Object getId() {
		return getModel().getId();
	}
	
	/**
	 * Gets the key that uniquely identifies this builder in the configuration.
	 */	
	protected Object getConnectableKey() {
		return null;	// Null indicates not connectable.
	}
	
	/**
	 * Gets the model (build target) of this builder.
	 * <p>
	 * @return Element The model associates with this builder.
	 */
	protected Element getModel() {
		return model;
	}
	
	/**
	 * Sets the parent builder of this builder.
	 * <p>
	 * @param parent The parent builder of this builder.
	 */
	protected void setParent(ElementBuilder parent) {
		this.parent = parent;
		this.root = (parent != null)? parent.getRoot() : null;
	}
	
	/**
	 * Gets the parent builder of this builder.
	 * <p>
	 * @return ElementBuilder The parent builder of this builder.
	 */
	protected ElementBuilder getParent() {
		return parent;
	}
	
	/**
	 * Gets the root builder of this builder.
	 * @return ElementBuilder The root builder of this builder.
	 */
	protected ConfigurationBuilder getRoot() {
		return root;
	}
	
	/**
	 * Sets the model (build target) of this builder.
	 * <p>
	 * @param model The model associates with this builder.
	 */
	protected void setModel(Element model) {
		if(this.model == model) return;
		if(this.model != null) {
				deactivate();
			}
		this.model= model;
	}
	
	/**
	 * Adds a child builder the builder map of this builder.
	 * <p>
	 * @param childBuilder A child builder to be added to the builder map of this
	 * builder.
	 */
	protected void addChildBuilder(ElementBuilder childBuilder) {
		if(childBuilders == null) childBuilders = new HashMap();
		childBuilders.put(childBuilder.getId(), childBuilder);
		childBuilder.setParent(this);
		childBuilder.activate();
		getRoot().registerConnectableBuilder(childBuilder);
		childBuilder.refresh();
	}

	/**
	 * Removes a previously added child builder from the builder map of this 
	 * builder.
	 * <p>
	 * @param id The id that identifies the child builder.
	 */
	protected boolean removeChildBuilder(Object id) {
		if(childBuilders == null) return false; // No child builder exists.
		
		ElementBuilder childBuilder = (ElementBuilder)childBuilders.get(id);
		if(childBuilder == null) return false;
		
		getRoot().deregisterConnectableBuilder(childBuilder);
		childBuilder.removeAllChildBuilder();
		childBuilder.clear();
		childBuilder.deactivate();
		childBuilder.setParent(null);
		
		childBuilders.remove(id);
		if(childBuilders.isEmpty()) childBuilders = null;
		return true;
	}
	
	private void removeAllChildBuilder() {
		if(childBuilders == null) return;
		ElementBuilder[] children = 
			(ElementBuilder[])childBuilders.values().toArray(new ElementBuilder[0]);
		for(int i = 0; i < children.length; i++) {
			removeChildBuilder(children[i].getId());
		}
	}

//	static private void deactivateChildren(ElementBuilder builder) {
//		if(builder.childBuilders == null) return;
//		ElementBuilder[] builders = 
//			(ElementBuilder[])builder.childBuilders.values().toArray(new ElementBuilder[0]);
//			
//		for(int i = 0; i < builders.length; i++){
//			ElementBuilder childBuilder = builders[i];
//			deactivateChildren(childBuilder);
//			
//			childBuilder.clear();
//			childBuilder.deactivate();
//			childBuilder.setParent(null);
//			builder.getRoot().deregisterBuilder(childBuilder);
//		}
//	}

	/**
	 * Gets the child builder identified by the id.
	 * <p>
	 * @param id The id that identifies the child builder.
	 * @return ElementBuilder The child builder identified by the id.
	 */
	protected ElementBuilder getChildBuilder(Object id) {
		if(childBuilders == null) return null;
		return (ElementBuilder)childBuilders.get(id);
	}
	
	/**
	 * Refreshes the model (build target) according to the current states from Maji.
	 * The build process should be incremental and minimal. 
	 * <p>
	 */
	public void refresh()	{
		refreshContents();
		refreshConnections();
	}
	
	/**
	 * Clears the model (build target) of this builder.
	 * <p>
	 */
	public void clear() {
		clearConnections();
		clearContents();
	}
	
	/*
	static void refreshChildConnections(ElementBuilder builder) {
		builder.refreshConnections();

		Map childBuilders = builder.childBuilders;
		if(childBuilders == null) return;
		Iterator it = builder.childBuilders.values().iterator();
		while(it.hasNext()) {
			ElementBuilder childBuilder = (ElementBuilder)it.next();
			refreshChildConnections(childBuilder);
		}
	}
	*/
	
	/**
	 * Refreshes the model contents according to the current states from Maji.
	 * The refresh process should be incremental and minimal. 
	 * <p>
	 * The contents of model is defined as all elements logically contained in
	 * the element together with the state of itself, excluding all 
	 * interconnections.
	 */
	abstract protected void refreshContents();
	
	abstract protected void clearContents();

	/**
	 * Refreshes the connection associates with the model (target) according to 
	 * the current states from Maji. The default implementation does nothing.
	 * <p>
	 * Similar to refreshContents(), the refresh process should be incremental and 
	 * minimal. 
	 * <p>
	 */
	abstract protected void refreshConnections();
	
	abstract protected void clearConnections();

	/**
	 * Activates this builder by connecting all client interfaces to the target 
	 * for incremental building.
	 * <p>
	 */	
	public abstract void activate();

	/**
	 * Deactivates this builder by disconnecting all client interfaces from the
	 * target to stop incremental building.
	 * <p>
	 */
	public abstract void deactivate();

	/**
	 * Loads the variable defined for the given element.
	 * <p>
	 * @param element The element which the states are to be loaded.
	 * @return boolean true is the 
	 */
	protected boolean loadVariable(Element element) {
		return getRoot().loadVariable(element);
	}
	
	/**
	 * Adds (defines) the variable for the given element.
	 * @param element The element which the variable is declared for.
	 */
	protected void addVariable(Element element) {
		getRoot().addVariable(element);
	}
	
	protected void deleteVariable(ElementPath path) {
		//getRoot().deleteVariable(path);
	}
}
