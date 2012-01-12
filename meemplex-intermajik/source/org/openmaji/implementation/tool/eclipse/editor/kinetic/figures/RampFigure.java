/*
 * @(#)RampFigure.java
 * Created on 17/04/2003
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic.figures;

import org.eclipse.draw2d.BorderLayout;
import org.eclipse.draw2d.Button;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.CompoundBorder;
import org.eclipse.draw2d.Ellipse;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.ImageFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.Panel;
import org.eclipse.draw2d.ScrollPane;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.openmaji.implementation.tool.eclipse.editor.features.flat.FlatScrollBar;


/**
 * <code>RampFigure</code>.
 * <p>
 * @author Kin Wong
 */
public class RampFigure extends Figure {
	FlatScrollBar ramp;
	Label label;
	Button button;
	ImageFigure image;
	
	public FlatScrollBar getRamp() {
		return ramp;
	}
	public Label getLabel() {
		return label;
	}
	
	public RampFigure() {
		setBorder(new CompoundBorder(new LineBorder(ColorConstants.black), new MarginBorder(4)));
		label = new Label();
		ramp = new FlatScrollBar();
		button = new Button("Test device");
		
		Panel statusPanel = new Panel();
		statusPanel.setLayoutManager(new ToolbarLayout(true));
		
		ScrollPane content = new ScrollPane();
		content.setPreferredSize(new Dimension(150, 100));
		
		//image = new ImageFigure(new Image())
		ramp.setPreferredSize(new Dimension(150,-1));
		ramp.setHorizontal(true);
		ramp.getRangeModel().setMinimum(0);
		ramp.getRangeModel().setMinimum(100);
		ramp.getRangeModel().setValue(70);
		
		//ramp.getRangeModel().setExtent(1);
		
		setLayoutManager(new BorderLayout());
		add(label);
		statusPanel.add(ramp);
		statusPanel.add(button);
		add(statusPanel);
		add(content);
		content.setBorder(new LineBorder(1));
		
		setConstraint(label, BorderLayout.TOP);
		setConstraint(content, BorderLayout.CENTER);
		setConstraint(statusPanel, BorderLayout.BOTTOM);
		
		for(int a = 0; a < 50; a++) {
			Ellipse circle = new Ellipse();
			int size = (int)(Math.random() * 40.0) + 10;
			int x = (int)(Math.random() * 150);
			int y = (int)(Math.random() * 150);
			circle.setFill(true);
			circle.setBackgroundColor(	new Color(Display.getDefault(), 
										(int)(Math.random() * 256.0), 
										(int)(Math.random() * 256.0), 
										(int)(Math.random() * 256.0)));
			circle.setBounds(new Rectangle(x,y,size,size));
			content.add(circle);
		}
	}
}
