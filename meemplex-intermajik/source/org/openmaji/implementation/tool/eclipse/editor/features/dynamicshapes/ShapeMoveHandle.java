/*
 * Created on 11/03/2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.openmaji.implementation.tool.eclipse.editor.features.dynamicshapes;

import org.eclipse.core.runtime.Assert;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Shape;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.handles.MoveHandle;
import org.eclipse.gef.handles.MoveHandleLocator;


/**
 * 
 * @author Kin Wong
 *
 * The <code>ShapeMoveHandle</code> represents a move handle of an owner that 
 * can is a <code>IShapeProvider</code>.
 */
public class ShapeMoveHandle extends MoveHandle {
	private IShapeProvider shapeProvider;
	private Shape shape;

	class ShapeMoveHandleLocator extends MoveHandleLocator {
		private ShapeMoveHandle handle;
		public ShapeMoveHandleLocator(IFigure ref){
			super(ref);
		}

		private void setHandle(ShapeMoveHandle handle) {
			this.handle = handle;
		}
		/**
		 * Sets the handle's bounds to that of its owner figure's
		 * bounds, expanded by the handle's Insets.
		 */
		public void relocate(IFigure target) {
			super.relocate(target);
			handle.realize();
			if(handle.getShape() != null)
			handle.getShape().setLocation(target.getBounds().getLocation());
		}
	}
	/**
	 * Constructs an instance of ShapeMoveHandle.
	 * @param owner The owner editpart of the handle.
	 */
	public ShapeMoveHandle(GraphicalEditPart owner, IShapeProvider shapeProvider) {
		super(owner);
		this.shapeProvider = shapeProvider;
		Assert.isNotNull(shapeProvider, "shapeProvider can not be null");
		realize();
		ShapeMoveHandleLocator locator = new ShapeMoveHandleLocator(owner.getFigure());
		locator.setHandle(this);
		setLocator(locator);
	}
	
	private void realize() {
		if(shape != null) {
			remove(shape);
			shape = null;
		}
		shape = shapeProvider.createSelectionShape(getOwnerFigure());
		add(shape);
	}
	/**
	 * Gets the shape that represents this handle.
	 * @return IFigure The shape that represents this handle.
	 */
	private IFigure getShape() {
		return shape;
	}
}
