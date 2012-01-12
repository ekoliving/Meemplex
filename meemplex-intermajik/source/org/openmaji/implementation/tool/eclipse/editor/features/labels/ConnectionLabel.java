/*
 * @(#)ConnectionLabel.java
 * Created on 25/09/2003
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.features.labels;

import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;
import org.openmaji.implementation.tool.eclipse.editor.features.util.FigureHelper;


/**
 * <code>ConnectionLabel</code>.
 * <p>
 * @author Kin Wong
 */
public class ConnectionLabel extends AbstractLabel {
	public static final int SOURCE = 2;
	public static final int TARGET = 3;
	public static final int MIDDLE = 4;
	
	protected int position;
	/**
	 * Constructs an instance of <code>ConnectionLabel</code>.
	 * <p>
	 */
	public ConnectionLabel(PolylineConnection connection, int position) {
		super(connection);
		this.position = position;
	}
	
	public PolylineConnection getConnection() {
		return (PolylineConnection)getOwner();
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.features.labels.AbstractLabel#getVisibility()
	 */
	protected boolean getVisibility() {
		return FigureHelper.isAnchorsVisibleInRootFigure(getConnection());
	}

	protected void update() {
		PolylineConnection connection = getConnection();
		if(connection == null) return;
		if(connection.getTargetAnchor() == null) return;
		if(connection.getSourceAnchor() == null) return;
		
		if((owner != null) && (paint = getVisibility())) {
			Point location = null;
			PointList points = connection.getPoints();
			switch(position) {
				case SOURCE: {
					Point ref = connection.getTargetAnchor().getReferencePoint();
					location = connection.getSourceAnchor().getLocation(ref);
					location = points.getMidpoint().getCopy();
				}
				break;
				
				case TARGET: {
					Point ref = connection.getSourceAnchor().getReferencePoint();
					location = connection.getTargetAnchor().getLocation(ref);
					location = points.getMidpoint().getCopy();
				}
				break;
				
				default:
				location = points.getMidpoint().getCopy();
				break;
			}
			
			connection.translateToAbsolute(location);
			translateToRelative(location);
			
			Dimension size = label.getPreferredSize();
			Rectangle labelBounds = new Rectangle();
			labelBounds.x = location.x - size.width / 2;
			labelBounds.y = location.y + 4;
			labelBounds.setSize(size);
			setBounds(labelBounds);
			labelBounds.setLocation(0,0);
			backdrop.setBounds(labelBounds);
			label.setBounds(labelBounds);
		}
		repaint();
				
	}
}
