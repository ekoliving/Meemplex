/*
 * @(#)WedgeAddDropEditPolicy.java
 * Created on 30/04/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.common.dnd;

import java.io.Serializable;
import java.util.Iterator;


import org.eclipse.draw2d.*;
import org.eclipse.draw2d.geometry.Point;
//import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.*;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.editpolicies.GraphicalEditPolicy;
import org.eclipse.gef.requests.LocationRequest;
import org.openmaji.implementation.tool.eclipse.editor.common.dnd.requests.WedgeAddRequest;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.commands.FacetAddCommand;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.commands.WedgeAddCommand;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.figures.MeemPlexShapeFigure;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.Meem;
import org.openmaji.implementation.tool.eclipse.ui.dnd.WedgeTransfer;
import org.openmaji.meem.definition.FacetAttribute;
import org.openmaji.meem.definition.WedgeAttribute;
import org.openmaji.system.meem.definition.MeemStructure;


/**
 * <code>WedgeAddDropEditPolicy</code>.
 * <p>
 * @author Kin Wong
 * @author mg
 */
public class WedgeAddDropEditPolicy extends GraphicalEditPolicy {
	private IFigure feedback;

	/**
	 * Overridden to intrepret <code>WedgeAddRequest</code> 
	 * @see org.eclipse.gef.EditPolicy#getCommand(org.eclipse.gef.Request)
	 */
	public Command getCommand(Request request) {
		if (WedgeAddRequest.REQ_WEDGE_ADD.equals(request.getType()))
			return getWedgeAddCommand((WedgeAddRequest) request);

		return super.getCommand(request);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.AbstractEditPolicy#showTargetFeedback(org.eclipse.gef.Request)
	 */
	public void showTargetFeedback(Request request) {
		if ((!(request instanceof WedgeAddRequest)) ) 
			return;
			
		LocationRequest dropRequest = (LocationRequest) request;
		if (feedback == null) {
			// Use a ghost rectangle for feedback
			RectangleFigure r = new RectangleFigure();
			FigureUtilities.makeGhostShape(r);
			r.setLineStyle(Graphics.LINE_DASHDOT);
			r.setForegroundColor(ColorConstants.white);
			r.setSize(MeemPlexShapeFigure.DEFAULT_WIDTH, MeemPlexShapeFigure.DEFAULT_HEIGHT); //-mg- change this
			addFeedback(r);
			feedback = r;
		}
		
		if (feedback != null)
			feedback.setLocation(getPointFromRequest(dropRequest));
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.AbstractEditPolicy#deactivate()
	 */
	public void deactivate() {
		if (feedback != null) {
			removeFeedback(feedback);
			feedback = null;
		}
		super.deactivate();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.AbstractEditPolicy#eraseTargetFeedback(org.eclipse.gef.Request)
	 */
	public void eraseTargetFeedback(Request request) {
		if (feedback == null)
			return;
		removeFeedback(feedback);
		feedback = null;
	}
	
	/**
	 * 
	 * @param request
	 * @return Command
	 */
	protected Command getWedgeAddCommand(WedgeAddRequest request) {	
		CompoundCommand commands = new CompoundCommand();
		Point location = getPointFromRequest(request);

		WedgeTransfer[] wedgeTransfers = (WedgeTransfer[])request.getData();
		if(wedgeTransfers == null) return null;
		
		for(int i = 0; i < wedgeTransfers.length; i++) {
			
			WedgeTransfer wedgeTransfer = wedgeTransfers[i];
      setUniqueIdentifiers(wedgeTransfer);
			WedgeAttribute wedgeAttribute = wedgeTransfer.getWedgeAttribute();
			Command command = getWedgeAddCommand(wedgeAttribute, location);
			if(command == null) {
				commands = null;
				break;
			} 
			commands.add(command);
			
			Iterator facetIterator = wedgeTransfer.getFacetAttributes().iterator();
			while (facetIterator.hasNext()) {
        FacetAttribute facetAttribute = (FacetAttribute) facetIterator.next();
				command = getFacetAddCommand(wedgeAttribute.getIdentifier(), facetAttribute, location);
				if(command == null) {
					commands = null;
					break;
				} 
				commands.add(command);
			}
		}
		
		return commands;
	}
  
  private void setUniqueIdentifiers(WedgeTransfer wedgeTransfer)
  {
    WedgeAttribute wedgeAttribute = wedgeTransfer.getWedgeAttribute();
    String originalIdentifier = wedgeAttribute.getIdentifier();
    MeemStructure meemStructure = getMeem().getProxy().getMetaMeem().getStructure();

    int count=1;
    String newIdentifier = originalIdentifier;
    synchronized(meemStructure) {
    	Iterator<Serializable> iterator = meemStructure.getWedgeAttributeKeys().iterator();
	    while ( iterator.hasNext() )
	    {
	      Serializable key = iterator.next();
	      WedgeAttribute existingWedgeAttribute = meemStructure.getWedgeAttribute(key);
	      if ( existingWedgeAttribute.getIdentifier().equals(newIdentifier) )
	      {
	        newIdentifier = originalIdentifier + (++count);
	      }
	    }
    }

    if ( count == 1 ) return;

    wedgeAttribute.setIdentifier(newIdentifier);

    Iterator facetIterator = wedgeTransfer.getFacetAttributes().iterator();
    while ( facetIterator.hasNext() )
    {
      FacetAttribute facetAttribute = (FacetAttribute) facetIterator.next();
      newIdentifier = facetAttribute.getIdentifier() + count;
      facetAttribute.setIdentifier(newIdentifier);
    }
  }
	
	protected Command getWedgeAddCommand(WedgeAttribute wedgeAttribute, Point location) {
//		Rectangle bounds = new Rectangle(location.x, location.y , -1, -1);
		return 
			new WedgeAddCommand(
				getMeem().getProxy().getMetaMeem(),
				wedgeAttribute);
	}
	
	protected Command getFacetAddCommand(Serializable wedgeKey, FacetAttribute facetAttribute, Point location) {
//			Rectangle bounds = new Rectangle(location.x, location.y , -1, -1);
    return 
				new FacetAddCommand(
					getMeem().getProxy().getMetaMeem(),
					wedgeKey,
					facetAttribute);
		}
	
	protected Meem getMeem() {
		return (Meem)getHost().getModel();
	}

	protected Point getPointFromRequest(LocationRequest request) {
		GraphicalEditPart editPart = (GraphicalEditPart) getHost();
		Point point = request.getLocation().getCopy();
		getHostFigure().translateToRelative(point);
	
		GraphicalViewer viewer = (GraphicalViewer) editPart.getRoot().getViewer();
		org.eclipse.swt.graphics.Point point2 = viewer.getControl().toControl(point.x, point.y);
	
		point2.x -= (MeemPlexShapeFigure.DEFAULT_WIDTH >> 1);
		point2.y -= (MeemPlexShapeFigure.DEFAULT_HEIGHT >> 1);
		return new Point(point2.x, point2.y);
	}
	
	/**
	 * Overridden to check if the request is of type 
	 * <code>WedgeAddRequest</code> and return the host editpart if so.
	 * @see org.eclipse.gef.EditPolicy#getTargetEditPart(org.eclipse.gef.Request)
	 */
	public EditPart getTargetEditPart(Request request) {
		if ( 	(WedgeAddRequest.REQ_WEDGE_ADD.equals(request.getType())) )
			return getHost();
		return super.getTargetEditPart(request);
	}
	
	protected WedgeAttribute getWedgeAttributeFromDropData(Object requestData) {
		if (requestData instanceof WedgeAttribute[]) {
			return ((WedgeAttribute[]) requestData)[0];
		}
		return null;
	}
}
