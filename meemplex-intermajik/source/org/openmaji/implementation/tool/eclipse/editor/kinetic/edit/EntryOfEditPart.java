/*
 * @(#)EntryOfEditPart.java
 * Created on 17/04/2003
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic.edit;


import java.beans.PropertyChangeEvent;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.gef.EditPolicy;
import org.eclipse.ui.views.properties.IPropertySource;
import org.openmaji.implementation.tool.eclipse.editor.common.figures.FigureScheme;
import org.openmaji.implementation.tool.eclipse.editor.features.labels.ConnectionLabel;
import org.openmaji.implementation.tool.eclipse.editor.features.labels.LayerConstants;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.figures.EntryOfConnection;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.infotips.EntryOfInfoTip;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.EntryOf;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.properties.EntryOfPropertySource;


/**
 * <code>EntryOfEditPart</code>.
 * <p>
 * @author Kin Wong
 */
public class EntryOfEditPart extends SchemeConnectionEditPart {
	private ConnectionLabel label;
	
	/**
	 * Constructs an instance of <code>EntryOfEditPart</code>.
	 * <p>
	 * @param entryOf The entry-of connection model associates with this edit 
	 * part as model.
	 */
	public EntryOfEditPart(EntryOf entryOf) {
		super(entryOf);
	}
	
	public EntryOf getEntryOfModel() {
		return (EntryOf)getModel();
	}
	
	private void createLabel() {
		PolylineConnection connection = (PolylineConnection)(getFigure());
		label = new ConnectionLabel(connection, ConnectionLabel.SOURCE);
		label.setForegroundColor(ColorConstants.white);
		getLayer(LayerConstants.LABEL_LAYER).add(label);
		refreshLabel();
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.common.edit.ConnectionElementEditPart#activate()
	 */
	public void activate() {
		super.activate();
		createLabel();
	}
	
	protected void refreshLabel() {
		EntryOf entryOf = getEntryOfModel();
		label.setText(entryOf.getName());
	}

	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.common.edit.ConnectionElementEditPart#deactivate()
	 */
	public void deactivate() {
		if(label != null) {
			getLayer(LayerConstants.LABEL_LAYER).remove(label);
			label.setOwner(null);
			label = null;
		}
		super.deactivate();
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.common.edit.ConnectionElementEditPart#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		String propName = evt.getPropertyName();
		if(EntryOf.ID_NAME.equals(propName)) {
			refreshLabel();
		}
		else
		super.propertyChange(evt);
	}


	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractConnectionEditPart#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class adapter) {
		if(IPropertySource.class.equals(adapter)) {
			return new EntryOfPropertySource(getEntryOfModel());
		} 
		else
		return super.getAdapter(adapter);
	}

	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.kinetic.edit.SchemeConnectionEditPart#createFigure()
	 */
	protected IFigure createFigure() {
		EntryOfConnection figure = new EntryOfConnection();
		FigureScheme scheme = getScheme(this);
		scheme.applyColors(figure);
		return figure;
	}
	/*
	 *  (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
	 */
	protected void createEditPolicies() {
		super.createEditPolicies();
		//installEditPolicy(	EditPolicy.CONNECTION_ENDPOINTS_ROLE, 
		//										new ConnectionEndpointEditPolicy());

		installEditPolicy(	EditPolicy.CONNECTION_ROLE, 
												new EntryOfCategoryConnectionEditPolicy());
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.common.edit.ConnectionElementEditPart#refreshVisuals()
	 */
	protected void refreshVisuals() {
		super.refreshVisuals();
		getFigure().setToolTip(new EntryOfInfoTip(getEntryOfModel()));
	}
}
