/*
 * @(#)CenterLayout.java
 * Created on 11/03/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
package org.openmaji.implementation.tool.eclipse.editor.common.figures;

import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * CenterLayout is a LayoutManager thats lays lot children in the center.
 * <p>
 * @author Kin Wong
 */
public class CenterLayout extends ToolbarLayout {
	private int spacing = 0;
	/**
	 * Constructs an instance of CenterLayout.
	 */
	public CenterLayout() {
		super();
		minorAlignment = ALIGN_CENTER;
	}
	public int getSpacing() {
	 	return spacing;
	}
	/**
	 * Overriddens to reposition
	 */
	public void layout(IFigure parent) {
		List children = parent.getChildren();
		int numChildren = children.size();
		Rectangle clientArea = transposer.t(parent.getClientArea());
		int x = clientArea.x;
		int y = clientArea.y;
		int availableHeight = clientArea.height;

		Dimension prefSizes [] = new Dimension[numChildren];
		Dimension minSizes [] = new Dimension[numChildren];
	
		// Calculate the width and height hints.  If it's a vertical ToolBarLayout,
		// then ignore the height hint (set it to -1); otherwise, ignore the 
		// width hint.  These hints will be passed to the children of the parent
		// figure when getting their preferred size. 
		int wHint = -1;
		int hHint = -1;    
		if (isHorizontal()) {
			hHint = parent.getClientArea(Rectangle.SINGLETON).height;
		} else {
			wHint = parent.getClientArea(Rectangle.SINGLETON).width;
		}

		/*		
		 * Calculate sum of preferred heights of all children(totalHeight). 
		 * Calculate sum of minimum heights of all children(minHeight).
		 * Cache Preferred Sizes and Minimum Sizes of all children.
		 *
		 * totalHeight is the sum of the preferred heights of all children
		 * totalMinHeight is the sum of the minimum heights of all children
		 * prefMinSumHeight is the sum of the difference between all children's
		 * preferred heights and minimum heights. (This is used as a ratio to 
		 * calculate how much each child will shrink). 
		 */
		IFigure child; 
		int totalHeight = 0;
		int totalMinHeight = 0;
		int prefMinSumHeight = 0;

		for (int i = 0; i < numChildren; i++) {
			child = (IFigure)children.get(i);
		
			prefSizes[i] = transposer.t(child.getPreferredSize(wHint, hHint));
			minSizes[i] = transposer.t(child.getMinimumSize(wHint, hHint));
		
			totalHeight += prefSizes[i].height;
			totalMinHeight += minSizes[i].height;
		}
		totalHeight += (numChildren - 1) * getSpacing();
		totalMinHeight += (numChildren - 1) * getSpacing();
		prefMinSumHeight = totalHeight - totalMinHeight;
		/* 
		 * The total amount that the children must be shrunk is the 
		 * sum of the preferred Heights of the children minus  
		 * Max(the available area and the sum of the minimum heights of the children).
		 *
		 * amntShrinkHeight is the combined amount that the children must shrink
		 * amntShrinkCurrentHeight is the amount each child will shrink respectively  
		 */	
		int amntShrinkHeight = totalHeight - Math.max(availableHeight, totalMinHeight);

		if (amntShrinkHeight < 0) {
			amntShrinkHeight = 0;
		}
		if(isHorizontal()) {
			x = clientArea.x;
		}
		else {
			y = clientArea.y + ((availableHeight - totalMinHeight) >> 1);
		}
		for (int i = 0; i < numChildren; i++) {
			int amntShrinkCurrentHeight = 0;
			int prefHeight = prefSizes[i].height;
			int minHeight = minSizes[i].height;
			int prefWidth = prefSizes[i].width;
			int minWidth = minSizes[i].width;
			Rectangle newBounds = new Rectangle(x, y, prefWidth, prefHeight);

			child = (IFigure)children.get(i);
			if (prefMinSumHeight != 0)
				amntShrinkCurrentHeight = (prefHeight - minHeight) * amntShrinkHeight / (prefMinSumHeight);

			int width = Math.min(prefWidth, child.getMaximumSize().width);
			width = Math.max(minWidth, Math.min(clientArea.width, width));
			newBounds.width = width;

			  int adjust = clientArea.width - width;
			switch (minorAlignment) {
			case ALIGN_TOPLEFT: 
				adjust = 0;
				break;
			case ALIGN_CENTER:
				adjust /= 2;
				break;
			case ALIGN_BOTTOMRIGHT:
				break;
			}
			newBounds.x += adjust;
			newBounds.height -= amntShrinkCurrentHeight;
			child.setBounds(transposer.t(newBounds));

			amntShrinkHeight -= amntShrinkCurrentHeight;
			prefMinSumHeight -= (prefHeight - minHeight);
			y += newBounds.height + spacing;
		}
	}

}
