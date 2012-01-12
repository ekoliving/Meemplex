package org.openmaji.implementation.tool.eclipse.editor.features.collapsible;

import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.geometry.Point;

/**
 * @author Kin Wong
 */
public interface IConnectionAnchorProvider {
	ConnectionAnchor connectionAnchorAt(Point p);
	
	ConnectionAnchor getSourceConnectionAnchor(String id);
	ConnectionAnchor getSourceConnectionAnchorAt(Point p);
	
	public ConnectionAnchor getTargetConnectionAnchorAt(Point p);
	ConnectionAnchor getTargetConnectionAnchor(String id);
}
