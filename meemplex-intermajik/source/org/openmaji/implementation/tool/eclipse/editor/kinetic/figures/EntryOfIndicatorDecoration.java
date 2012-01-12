/*
 * Created on 25/03/2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.openmaji.implementation.tool.eclipse.editor.kinetic.figures;

import org.eclipse.draw2d.PolylineDecoration;
import org.eclipse.draw2d.geometry.PointList;

/**
 * @author Kin Wong
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class EntryOfIndicatorDecoration extends PolylineDecoration {
	private static final PointList MEMBERINDICATOR = new PointList(4);

	static {
		MEMBERINDICATOR.addPoint(0, 2);
		MEMBERINDICATOR.addPoint(-1, 2);
		MEMBERINDICATOR.addPoint(-1, -2);
		MEMBERINDICATOR.addPoint(0, -2);
	}
	/**
	 * Constructs a EntryOfIndicatorDecoration. 
	 */
	public EntryOfIndicatorDecoration(){
		setTemplate(MEMBERINDICATOR);
		//setScale(0.35,0.25);
		setFill(false);
	}

}
