/*
 * Created on 26/03/2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.openmaji.implementation.tool.eclipse.editor.kinetic.layout;

import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.GraphicalEditPart;
import org.openmaji.implementation.tool.eclipse.editor.common.layout.Edge;
import org.openmaji.implementation.tool.eclipse.editor.common.layout.Node;


/**
 * @author Kin Wong
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public interface ILayoutObjectFactory {
	/**
	 * Creates a node that represents an edit part.
	 * @param editPart The edit part the creating node represents.
	 * @return Node The Node that corresponds to the edit part.
	 */
	Node createNode(GraphicalEditPart editPart);
	/**
	 * Creates an edge that represents a connection edit part.
	 * @param source The source node of the edge.
	 * @param target The target node of the edge.
	 * @param editPart The connection edit part that the creating edge 
	 * represents.
	 * @return Edge The new edge that represents the connection edit part.
	 */
	Edge createEdge(Node source, Node target, ConnectionEditPart editPart);
	/**
	 * Updates the edit part that corresponds to a node that has been 
	 * repositioned.
	 * @param node The node that has been repositioned.
	 * @param editPart The editpart that corresponds to the node.
	 */
	void update(Node node, GraphicalEditPart editPart);
}
