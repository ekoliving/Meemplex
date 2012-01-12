/*
 * Created on 11/03/2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.openmaji.implementation.tool.eclipse.editor.features.dynamicshapes;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Shape;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.editpolicies.NonResizableEditPolicy;
import org.eclipse.gef.handles.NonResizableHandleKit;


/**
 * <p>
 * @author Kin Wong
 */
public class NonResizableShapeEditPolicy extends NonResizableEditPolicy {
	boolean useRealtimeFeedback = false;
	
	protected IFigure createDragSourceFeedbackFigure() {
		if (useRealtimeFeedback) {
			// Use the actual figure for feedback
			return ((GraphicalEditPart)getHost()).getFigure();
		} 
		else {
			IShapeProvider shapeProvider = getShapeProvider();
			if(shapeProvider != null) {
				// Use a ghost ellipse for feedback
				Shape shape = shapeProvider.createFeedbackShape();
				FigureUtilities.makeGhostShape(shape);
				addFeedback(shape);
				return shape;						
			}
			else
			return super.createDragSourceFeedbackFigure();			
		}
	}
	/**
	 * Returns the <code>IShapeProvider</code>of the host.
	 * @return IShapeProvider The IShapeProvider of the host, null if it has not 
	 * been implemented.
	 */
	protected IShapeProvider getShapeProvider() {
		if(getHost() instanceof IShapeProvider) 
		return (IShapeProvider)getHost();
		return (IShapeProvider)getHost().getAdapter(IShapeProvider.class);
	}
	/**
	 * Gets the bounds of the host figure.
	 * @return Rectangle The bounds of the host figure.
	 */
	protected Rectangle getBounds() {
		return ((GraphicalEditPart)getHost()).getFigure().getBounds();
	}
	/**
	 * Overridden to create a ShapeMoveHandle and 4 non-resize handles.
	 */
	protected List createSelectionHandles() {
		IShapeProvider shapeProvider = getShapeProvider();
		if(shapeProvider == null) return super.createSelectionHandles();
		
		List list = new ArrayList();
		list.add(new ShapeMoveHandle((GraphicalEditPart)getHost(), shapeProvider));
		NonResizableHandleKit.addCornerHandles((GraphicalEditPart)getHost(), list);
		return list;
	}
}
