/*
 * @(#)Engine.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
package org.openmaji.implementation.tool.eclipse.editor.common.layout;

import java.util.Random;

/**
 * @author Peter
 */
public class Engine
{
	public static void main(String[] args)
	{
		Node nodeA = new Node(new Vector2D(100.0, 100.0));
		Node nodeB =
			new Node(
				new Vector2D(200.0, 100.0),
				Node.DEFAULT_HEIGHT,
				Node.DEFAULT_WIDTH,
				Node.DEFAULT_MASS * 5.0,
				Node.DEFAULT_CHARGE * 5.0);
		Node nodeC = new Node(new Vector2D(100.0, 200.0));
		Node nodeD = new Node(new Vector2D(200.0, 200.0));
		Node nodeE = new Node(new Vector2D(250.0, 150.0));
		Edge edgeAB = new Edge(nodeA, nodeB);
		Edge edgeAC = new Edge(nodeA, nodeC);
		Edge edgeAD =
			new Edge(
				nodeA,
				nodeD,
				Edge.DEFAULT_ANCHOR,
				Edge.DEFAULT_ANCHOR,
				Edge.DEFAULT_LENGTH,
				Edge.DEFAULT_STRENGTH * 2.0,
				Edge.DEFAULT_MAGNETISM);

		System.err.println("NodeA: " + nodeA.getLocation());
		System.err.println("NodeB: " + nodeB.getLocation());
		System.err.println("NodeC: " + nodeC.getLocation());
		System.err.println("NodeD: " + nodeD.getLocation());
		System.err.println("NodeE: " + nodeE.getLocation());

		Graph graph = new Graph();
		graph.addNode(nodeA);
		graph.addNode(nodeB);
		graph.addNode(nodeC);
		graph.addNode(nodeD);
		//graph.addNode(nodeE);
		graph.addEdge(edgeAB);
		graph.addEdge(edgeAC);
		graph.addEdge(edgeAD);

		Engine engine = new Engine();
		engine.layout(graph, 100, 0);

		System.err.println("NodeA: " + nodeA.getLocation());
		System.err.println("NodeB: " + nodeB.getLocation());
		System.err.println("NodeC: " + nodeC.getLocation());
		System.err.println("NodeD: " + nodeD.getLocation());
		System.err.println("NodeE: " + nodeE.getLocation());

		// TODO [peter] Test locking (by locking at least two nodes!)
	}

/*
	// TODO[peter] This method should do a very quick and dirty stab at initial placement of nodes
	public void place(Graph graph)
	{
		Iterator iter = graph.getNodes().iterator();
		while (iter.hasNext())
		{
			Node node = (Node) iter.next();

			
		}
	}
*/

	public void layout(Graph graph, int numIterations, long seed)
	{
		Object[] nodes = graph.getNodes().toArray();
		Object[] edges = graph.getEdges().toArray();

		// TODO [peter] See simulated annealing/temperature measurement for another phase

		Random random = new Random(seed);

		perturbNodes(random, nodes, 1.0);

		for (int i = 0; i < numIterations; ++i)
		{
			calculateGravity(nodes);
			calculateRepulsion(nodes);
			calculateSprings(edges);

			applyForces(nodes);
		}

		packNodes(nodes);
	}

	// TODO[peter] Can relate size of perturbation to "temperature"?
	private void perturbNodes(Random random, Object[] nodes, double mag)
	{
		int i = 0;
		while (i < nodes.length)
		{
			Node node = (Node) nodes[i++];

			if (node.locked) continue;

/*
			double theta = 2 * Math.PI * Math.random();
			offset.x = Math.cos(theta);
			offset.y = Math.sin(theta);
			offset.scale(mag);

			//node.addLocation(offset);
			node.location.x += offset.x;
			node.location.y += offset.y;
*/

//			double jitter = Math.random();
			double jitter = random.nextDouble();

			if (jitter < 0.5)
			{
				node.location.x += jitter - 0.25;
			}
			else
			{
				node.location.y += jitter - 0.75;
			}
		}
	}

	// Not really gravitational, but a constant attractive force towards centroid
	private void calculateGravity(Object[] nodes)
	{
		Vector2D centroid = new Vector2D(0.0, 0.0);
		double centroidMass = 0.0;

		int i = 0;
		while (i < nodes.length)
		{
			Node node = (Node) nodes[i++];

			centroid.x += node.location.x * node.mass;
			centroid.y += node.location.y * node.mass;

			centroidMass += node.mass;
		}

		double scale = 1.0 / centroidMass;
		centroid.x *= scale;
		centroid.y *= scale;

		Vector2D diff = new Vector2D();

		i = 0;
		while (i < nodes.length)
		{
			Node node = (Node) nodes[i++];

			diff.x = centroid.x;
			diff.y = centroid.y;

			Vector2D location = node.location;
			diff.x -= location.x;
			diff.y -= location.y;

			double mag2 = diff.x * diff.x + diff.y * diff.y + OFFSET_MAG2;
			double mag = Math.sqrt(mag2);

			double f = node.mass * GRAVITATION_STRENGTH;
			scale = f / mag;
			diff.x *= scale;
			diff.y *= scale;

			node.force.x += diff.x;
			node.force.y += diff.y;
		}
	}

	// An electric repulsive force 1/SQR(D)
	private void calculateRepulsion(Object[] nodes)
	{
		Vector2D diff = new Vector2D();

		int left = 0;
		while (left < nodes.length)
		{
			Node leftNode = (Node) nodes[left++];

			int right = left;
			while (right < nodes.length)
			{
				Node rightNode = (Node) nodes[right++];

				Vector2D rightLocation = rightNode.location;
				diff.x = rightLocation.x;
				diff.y = rightLocation.y;

				Vector2D leftLocation = leftNode.location;
				diff.x -= leftLocation.x;
				diff.y -= leftLocation.y;

				double mag2 = diff.x * diff.x + diff.y * diff.y + OFFSET_MAG2;
				double mag = Math.sqrt(mag2);

//				mag -= rightNode.radius;
//				mag -= leftNode.radius;
//
//				mag = Math.max(mag, MINIMUM_MAG);

				double scale =
					leftNode.charge
						* rightNode.charge
						* REPULSION_STRENGTH
						/ (mag * mag * mag);

				diff.x *= scale;
				diff.y *= scale;

				rightNode.force.x += diff.x;
				rightNode.force.y += diff.y;
				leftNode.force.x -= diff.x;
				leftNode.force.y -= diff.y;
			}
		}
	}

	// Each edge is treated as a spring, and a restoring force is applied
	private void calculateSprings(Object[] edges)
	{
		Vector2D diff = new Vector2D();

		for (int i = 0; i < edges.length; ++i)
		{
			Edge edge = (Edge) edges[i];
			Node sourceNode = edge.sourceNode;
			Node targetNode = edge.targetNode;

			// TODO[peter] Figure out this magnetic thing properly
			sourceNode.force.x -= edge.magnetism;
			targetNode.force.x += edge.magnetism;

			Vector2D targetLocation = targetNode.location;
			diff.x = targetLocation.x;
			diff.y = targetLocation.y;

			Vector2D targetAnchor = edge.targetAnchor;
			diff.x += targetAnchor.x;
			diff.y += targetAnchor.y;

			Vector2D sourceLocation = sourceNode.location;
			diff.x -= sourceLocation.x;
			diff.y -= sourceLocation.y;

			Vector2D sourceAnchor = edge.sourceAnchor;
			diff.x -= sourceAnchor.x;
			diff.y -= sourceAnchor.y;


			double mag2 = diff.x * diff.x + diff.y * diff.y + OFFSET_MAG2;
			double mag = Math.sqrt(mag2);
//			mag = Math.max(mag, MINIMUM_MAG);

			// Hookes law F = -K(X - X0)
			double scale = (edge.length - mag) * edge.strength / mag;

			diff.x *= scale;
			diff.y *= scale;
			
			targetNode.force.x += diff.x;
			targetNode.force.y += diff.y;
			sourceNode.force.x -= diff.x;
			sourceNode.force.y -= diff.y;
		}
	}

	private void applyForces(Object[] nodes)
	{
		for (int i = 0; i < nodes.length; ++i)
		{
			Node node = (Node) nodes[i];

			if (!node.locked)
			{
				node.location.x += node.force.x / node.mass;
				node.location.y += node.force.y / node.mass;
			}

			node.force.x = node.force.y = 0.0;
		}
	}

	// Loop through all nodes to find minimums and then adjust all nodes according to desired margin
	private void packNodes(Object[] nodes)
	{
		if (nodes.length < 1) return;
		
		Node node = (Node) nodes[0];
		Vector2D location = node.location;

		double minX = location.x;
		double minY = location.y;

		int i = 1;
		while (i < nodes.length)
		{
			node = (Node) nodes[i++];

			if (node.getMinX() < minX)
				minX = node.getMinX();

			if (node.getMinY() < minY)
				minY = node.getMinY();
		}

		double adjustX = PACKING_MARGIN - minX;
		double adjustY = PACKING_MARGIN - minY;

		i = 0;
		while (i < nodes.length)
		{
			node = (Node) nodes[i++];

			node.location.x += adjustX;
			node.location.y += adjustY;
		}
	}

	private static final double GRAVITATION_STRENGTH = 25.0;
	private static final double OFFSET_MAG = 20.0;
	private static final double OFFSET_MAG2 = OFFSET_MAG * OFFSET_MAG;
	private static final double PACKING_MARGIN = 50.0;
	private static final double REPULSION_STRENGTH = 500000.0;
}
