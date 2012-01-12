/*
 * Created on 26/03/2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.openmaji.implementation.tool.eclipse.editor.kinetic.layout.deathman;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.GraphicalEditPart;
import org.openmaji.implementation.tool.eclipse.editor.common.layout.Edge;
import org.openmaji.implementation.tool.eclipse.editor.common.layout.Node;
import org.openmaji.implementation.tool.eclipse.editor.common.layout.Vector2D;
import org.openmaji.implementation.tool.eclipse.editor.common.model.BoundsObject;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.Dependency;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.EntryOf;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.Meem;


/**
 * @author Kin Wong
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class ConfigurationLayoutObjectFactory implements ILayoutObjectFactory {
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.kinetic.layout.ILayoutObjectFactory#createNode(org.eclipse.gef.EditPart)
	 */
	public Node createNode(GraphicalEditPart editPart)
	{
		Object model = editPart.getModel();

		if (model instanceof BoundsObject)
		{
			Rectangle figureBounds = editPart.getFigure().getBounds();
			boolean locked = false;
			
			if (model instanceof Meem)
			{
//				Meem meem = (Meem) model;
//				locked = !meem.isCollapsed();
			}

			return createBoundsObjectNode(figureBounds, locked);
		}

		return null;
	}

	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.kinetic.layout.ILayoutObjectFactory#edgeEditPart(org.openmaji.implementation.tool.eclipse.editor.common.layout.Node, org.openmaji.implementation.tool.eclipse.editor.common.layout.Node, org.eclipse.gef.ConnectionEditPart)
	 */
	public Edge createEdge(
		Node source,
		Node target,
		ConnectionEditPart editPart)
	{
		double strength = Edge.DEFAULT_STRENGTH;
		double magnetism = 0.0;

		// TODO[peter] Consider using a multiple of the radii here
		Object model = editPart.getModel();

		if (model instanceof Dependency) {
//			Dependency dependencyModel = (Dependency)editPart.getModel();

//			Object strengthAttribute = dependencyModel.getAttribute() (Dependency.ID_STRENGTH);
			boolean isWeak = false;

			if (isWeak)
			{
				magnetism = 2.5;
			}
			else
			{
				magnetism = 10.0;
				//strength *= 2.0;
			}
		}
		else if (model instanceof EntryOf)
		{
			magnetism = -2.5;
		}

//		double length =
//			Edge.DEFAULT_LENGTH;// + source.getRadius() + target.getRadius();
		double length =
			Edge.DEFAULT_LENGTH + (source.getWidth() + target.getWidth()) / 2.0;

		Vector2D sourceAnchor = new Vector2D(source.getWidth() / 2.0, 0.0);//Edge.DEFAULT_ANCHOR
		Vector2D targetAnchor = new Vector2D(-target.getWidth() / 2.0, 0.0);//Edge.DEFAULT_ANCHOR
//		Vector2D sourceAnchor = new Vector2D(0.0, 0.0);//Edge.DEFAULT_ANCHOR
//		Vector2D targetAnchor = new Vector2D(0.0, 0.0);//Edge.DEFAULT_ANCHOR

		return new Edge(source, target, sourceAnchor, targetAnchor, length, strength, magnetism);
	}

	/**
	 *  (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.kinetic.layout.ILayoutObjectFactory#update(org.openmaji.implementation.tool.eclipse.editor.common.layout.Node, org.eclipse.gef.GraphicalEditPart)
	 */
	public void update(Node node, GraphicalEditPart editPart)
	{
		Object model = editPart.getModel();

		if (model instanceof BoundsObject)
		{
			Rectangle figureBounds = editPart.getFigure().getBounds();
			updateBoundsObject(node, (BoundsObject) model, figureBounds);
		}
	}

	private Node createBoundsObjectNode(Rectangle figureBounds, boolean locked)
	{
		Vector2D location =
			new Vector2D(
				0.5 * figureBounds.width + figureBounds.x,
				0.5 * figureBounds.height + figureBounds.y);

		double area = figureBounds.width * figureBounds.height;
		double factor = Math.sqrt(area) / 16.0 + 1.0;
		factor = Math.max(1.0, Math.sqrt(factor));
		//factor = Math.max(1.0, factor);

		Node node = new Node(
			location,
			figureBounds.height,
			figureBounds.width,
			Node.DEFAULT_MASS * factor,
			Node.DEFAULT_CHARGE * factor);

		node.setLocking(locked);

		return node;
	}

	private void updateBoundsObject(
		Node node,
		BoundsObject boundsObject,
		Rectangle figureBounds)
	{
		Vector2D location = node.getLocation();
		Rectangle bounds = boundsObject.getBounds();
		Rectangle newBounds =
			new Rectangle(
				(int) (location.getX() - 0.5 * figureBounds.width),
				(int) (location.getY() - 0.5 * figureBounds.height),
				bounds.width,
				bounds.height);

		boundsObject.setBounds(newBounds);
	}
}
