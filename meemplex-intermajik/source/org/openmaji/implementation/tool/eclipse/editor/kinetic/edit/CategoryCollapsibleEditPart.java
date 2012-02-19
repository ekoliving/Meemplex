/*
 * @(#)CategoryCollapsibleEditPart.java
 * Created on 25/03/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
 
package org.openmaji.implementation.tool.eclipse.editor.kinetic.edit;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.requests.CreateConnectionRequest;
import org.openmaji.implementation.tool.eclipse.client.VariableMapProxy;
import org.openmaji.implementation.tool.eclipse.editor.common.figures.IFigureSchemeProvider;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.commands.CategoryEntryCreateCommand;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.commands.DependencyCreateCommand;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.figures.CategoryFigureSchemeProvider;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.Category;
import org.openmaji.system.space.CategoryClient;
import org.openmaji.system.space.CategoryEntry;



/**
 * @author Kin Wong
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class CategoryCollapsibleEditPart extends MeemCollapsibleEditPart {
	//=== Internal CategoryClient Implementation =================================
	protected CategoryClient categoryClient = new CategoryClient() {
		public void entriesAdded(CategoryEntry[] newEntries) {
		}
		public void entriesRemoved(CategoryEntry[] removedEntries) {
		}
		public void entryRenamed(CategoryEntry oldEntry, CategoryEntry newEntry) {
		}
	};
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.kinetic.edit.MeemCollapsibleEditPart#createEditPolicies()
	 */
	protected void createEditPolicies() {
		super.createEditPolicies();
		installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE, new CategoryNodeEditPolicy());
	}

	/**
	 * Constructs an instance of <code>CategoryCollapsibleEditPart</code>.
	 * @param category An instance of <code>CategoryCollapsibleEditPart</code>.
	 */
	public CategoryCollapsibleEditPart(Category category, VariableMapProxy variableMapProxy) {
		super(category, variableMapProxy);
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.kinetic.edit.MeemEditPart#activate()
	 */
	public void activate() {
		super.activate();
		getCategoryModel().
			getProxy().getCategoryProxy().addClient(categoryClient);
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.kinetic.edit.MeemEditPart#deactivate()
	 */
	public void deactivate() {
		getCategoryModel().
			getProxy().getCategoryProxy().removeClient(categoryClient);
		super.deactivate();
	}

	/**
	 * Gets the figure scheme provider.
	 * @return IFigureSchemeProvider The figure scheme provider.
	 */
	public IFigureSchemeProvider getSchemeProvider() {
		return CategoryFigureSchemeProvider.getInstance();
	}
	
	/**
	 * Gets the model as category.
	 * @return Category The model as category.
	 */
	protected Category getCategoryModel() {
		return (Category)getModel();
	}
	
	/**
	 * Overridden to handle change of cateogry entry.
	 * @see org.openmaji.implementation.tool.eclipse.editor.kinetic.edit.MeemIconicEditPart#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		String prop = evt.getPropertyName();
		
		if(	Category.ID_DEPENDENCY_TARGET.equals(prop) ||
				Category.ID_CATEGORY_ENTRY.equals(prop)) {
			// The Category entry has been changed.
			refreshTargetConnections();
		}
		else
		super.propertyChange(evt);
	}
	/**
	 * Overridden to return a list that combines connections from the super 
	 * class and entries to this category.
	 * @see org.openmaji.implementation.tool.eclipse.editor.kinetic.edit.MeemIconicEditPart#getModelTargetConnections()
	 */
	protected List getModelTargetConnections() {
		ArrayList all = new ArrayList(super.getModelTargetConnections());
		all.addAll(getCategoryModel().getMeemEntries());
		all.addAll(getCategoryModel().getDependencies());
		return all;
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.kinetic.edit.MeemCollapsibleEditPart#getTargetConnectionAnchor(org.eclipse.gef.Request)
	 */
	public ConnectionAnchor getTargetConnectionAnchor(Request request) {

		if(!(request instanceof CreateConnectionRequest)) return null;
		CreateConnectionRequest createRequest = (CreateConnectionRequest)request;
		
		Command command = createRequest.getStartCommand();
		if(command instanceof CategoryEntryCreateCommand) {
			// The Category object as the category entry target
			return anchor;	
		}
		else
		if(command instanceof DependencyCreateCommand) {
			// The Category object as the dependency target
			return anchor;
		}
		else
		return null;
	}
}
