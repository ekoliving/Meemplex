/*
 * Created on 25/03/2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.openmaji.implementation.tool.eclipse.editor.kinetic.layout.deathman;

import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.NodeEditPart;
import org.openmaji.implementation.tool.eclipse.editor.common.layout.Edge;
import org.openmaji.implementation.tool.eclipse.editor.common.layout.Engine;
import org.openmaji.implementation.tool.eclipse.editor.common.layout.Graph;
import org.openmaji.implementation.tool.eclipse.editor.common.layout.Node;


/**
 * @author Kin Wong
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class Layout {
	private int iteration = 1000;
	private Engine engine = new Engine();
	private Graph graph = new Graph();
	private ILayoutObjectFactory factory;
	
	private IdentityHashMap nodesToEditParts = new IdentityHashMap();
	private IdentityHashMap editPartsToNodes = new IdentityHashMap();
	
	public Layout(ILayoutObjectFactory factory) {
		this.factory = factory;
	}
	
	public void setIteration(int iteration) {
		this.iteration = iteration;
	}
	public int getIteration() {
		return iteration;
	}
	public void build(GraphicalEditPart editPart) {
		clear();
		collect(editPart);
	}
	public void clear() {
		graph.clear();
		nodesToEditParts.clear();
		editPartsToNodes.clear();
	}
	
	public void layout(long seed) {
		engine.layout(graph, iteration, seed);
		update();
	}

	/**
	 * Creates a node (or cluster) base on an edit part.
	 * @param editPart The edit part in which the newly created node or cluster is based on.
	 * @return Node The newly created node.
	 */
	protected Node createNode(GraphicalEditPart editPart) {
		Node node = factory.createNode(editPart);
		if(node != null) {
			nodesToEditParts.put(node, editPart);
			editPartsToNodes.put(editPart, node);
		}
		return node;
	}
	/**
	 * Creates an edge that represents a connection edit part.
	 * @param editPart The connection edit part the new edge represents.
	 * @return Edge The new edge that represents the connection edit part.
	 */
	protected Edge createEdge(ConnectionEditPart editPart) {
		Node source = findAncestorNode(editPart.getSource());
		Node target = findAncestorNode(editPart.getTarget());
		if(source == null || target == null) return null;
		return factory.createEdge(source, target, editPart);
	}

	/**
	 * Collects all the objects in the edit part that will be relocated by
	 * the layout engine.<p>
	 * @param editPart The root edit part of the collection.
	 */
	protected void collect(GraphicalEditPart editPart) {
		//System.out.println("===== Start Preparing layout ======");
		List children = editPart.getChildren();
		Iterator it = children.iterator();
		while(it.hasNext()) {
			GraphicalEditPart editPartChild = (GraphicalEditPart)it.next();
			Node node = createNode(editPartChild);
			if(node != null) {
				graph.addNode(node);  
				//System.out.println("Adding Node for " + editPartChild);
			}
			else {
				System.out.println(editPartChild);
			}
		}
		collectEdges(editPart);
		//System.out.println("===== End Preparing layout ======");
	}
	/**
	 * Collects all the edges in the edit part and its children.
	 * @param editPart The root edit part for the collection.
	 */
	protected void collectEdges(GraphicalEditPart editPart) {
		List children = editPart.getChildren();
		if(children.isEmpty()) return;
		
		Iterator it = children.iterator();
		while(it.hasNext()) {
			GraphicalEditPart editPartChild = (GraphicalEditPart)it.next();
			if(editPartChild instanceof NodeEditPart) {
				NodeEditPart nodeEditPart = (NodeEditPart)editPartChild;
				Iterator itConnection = nodeEditPart.getSourceConnections().iterator();
				
				while(itConnection.hasNext()) {
					ConnectionEditPart connectionEditPart = 
						(ConnectionEditPart)itConnection.next();
					Edge edge = createEdge(connectionEditPart);
					if(edge != null) {
						graph.addEdge(edge);						
						//System.out.println("Add Edge for " + connectionEditPart);
					}
				}
			}
			// Collect all the edges in the child.
			collectEdges(editPartChild);
		}
	}
	/**
	 * Finds an node that corresponds to an ancestor edit part.
	 * @param editPart The child editpart.
	 * @return Node The ancestor node.
	 */
	private Node findAncestorNode(EditPart editPart) {
		while(editPart != null){
			Node node = (Node)editPartsToNodes.get(editPart);
			if(node != null) return node;
			editPart = editPart.getParent();
		}
		return null;
	}
	/**
	 * Updates all editparts from changed nodes.
	 */
	public void update() {
		Iterator it = graph.getNodes().iterator();
		while(it.hasNext()) {
			Node node = (Node)it.next();
			GraphicalEditPart editPart = 
				(GraphicalEditPart)nodesToEditParts.get(node);
			factory.update(node, editPart);
		}
	}
}
