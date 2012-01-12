/*
 * @(#)CategoryIconicEditPart.java
 * Created on 10/04/2003
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic.edit;


import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.Ellipse;
import org.eclipse.draw2d.Figure;
import org.eclipse.gef.EditPolicy;
import org.eclipse.swt.graphics.Image;
import org.openmaji.implementation.tool.eclipse.client.VariableMapProxy;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.Category;
import org.openmaji.implementation.tool.eclipse.images.Images;


/**
 * <code>CategoryIconicEditPart</code>.
 * <p>
 * @author Kin Wong
 */
public class CategoryIconicEditPart extends MeemIconicEditPart {
	static private Image DEFAULT_IMAGE = Images.getIcon("category48.gif");
	static private Image TEMPLATE_IMAGE = Images.getIcon("category_template48.gif");

	/**
	 * Constructs an instance of <code>CategoryIconicEditPart</code>.
	 * <p>
	 * @param category The <code>Category</code> associates with this edit part
	 * as model.
	 */
	public CategoryIconicEditPart(Category category, VariableMapProxy variableMapProxy) {
		super(category, variableMapProxy);
	}
	
	/**
	 * Gets the model as a <code>Category</code>.
	 * @return Category The model as a <code>Category</code>.
	 */
	protected Category getCategoryModel() {
		return (Category)getModel();
	}

	protected Image getDefaultImage() {
		return DEFAULT_IMAGE;
	}
	
	protected Image getTemplateImage() {
		return TEMPLATE_IMAGE;
	}
	
	/**
	 * Overridden to install policy for creating entry-of connection to 
	 * category.
	 * @see org.openmaji.implementation.tool.eclipse.editor.kinetic.edit.MeemIconicEditPart#createEditPolicies()
	 */
	protected void createEditPolicies() {
		super.createEditPolicies();
		installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE, new CategoryNodeEditPolicy());
	}
	
	/**
	 * Overridden to handle change of cateogry entry.
	 * @see org.openmaji.implementation.tool.eclipse.editor.kinetic.edit.MeemIconicEditPart#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		String prop = evt.getPropertyName();
		if(	Category.ID_CATEGORY_ENTRY.equals(prop) ||
				Category.ID_DEPENDENCY_TARGET.equals(prop)) {
			// The Category entry has been changed.
			refreshTargetConnections();
		}
		else
		super.propertyChange(evt);
	}

	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.kinetic.edit.MeemIconicEditPart#createLifeCycleStateFigure()
	 */
	protected Figure createLifeCycleStateFigure() {
		Ellipse meem = new Ellipse(){
			public boolean containsPoint(int x, int y) {return false;}
		};
		meem.setFill(false);
		meem.setSize(LIFE_CYCLE_STATE_SIZE);
		meem.setLineWidth(LIFE_CYCLE_STATE_WIDTH);
		return meem;
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
}
