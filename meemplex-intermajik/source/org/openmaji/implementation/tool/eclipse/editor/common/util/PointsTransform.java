/*
 * @(#)PointsTransform.java
 * Created on 6/05/2003
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.common.util;

import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Transform;

/**
 * <code>PointsTransform</code>.
 * <p>
 * @author Kin Wong
 */
public class PointsTransform extends Transform {
	public PointList transform(PointList points) {
		PointList pointsTransformed = new PointList(points.size());
		for(int p = 0; p < points.size(); p++)
		pointsTransformed.addPoint(getTransformed(points.getPoint(p)));
		
		return pointsTransformed;
	}
}
