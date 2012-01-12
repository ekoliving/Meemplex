/*
 * @(#)ColorTransformer.java
 * Created on 19/03/2003
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */
package org.openmaji.implementation.tool.eclipse.editor.common.util;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

/**
 * <code>ColorTransformer</code> provides utility function to manipulate color.
 * <p>
 * @author Kin Wong
 */
public class ColorTransformer {
	static private float DEFAULT_DARKEN = 0.25f;
	static private float DEFAULT_BRIGHTEN = 0.25f;
	
	/**
	 * Darkens the color by the default amount.
	 * @param color The color to be darkened.
	 * @return Color The darkened color.
	 */
	static public Color darken(Color color) {
		return darken(color, DEFAULT_DARKEN);
	}
	/**
	 * Darkens the color by the change amount from 0.0 to 1.0.
	 * @param color The color to be darkened.
	 * @param change The amount of change from 0.0 to 1.0.
	 * @return Color The darkened color.
	 */
	static public Color darken(Color color, float change) {
		int r = (int)((float)color.getRed() * (1.0f - change));
		int g = (int)((float)color.getGreen() * (1.0f - change));
		int b = (int)((float)color.getBlue() * (1.0f - change));
		return new Color(Display.getDefault(), r, g, b);
	}
	/**
	 * Brightens the color by the default amount.
	 * @param color The color to be brightened.
	 * @return Color The brightened color.
	 */
	static public Color brighten(Color color) {
		return brighten(color, DEFAULT_BRIGHTEN);
	}
	/**
	 * Brightens the color by the change amount from 0.0 to 1.0.
	 * @param color The color to be brightened.
	 * @param change The amount of change from 0.0 to 1.0.
	 * @return Color The brightened color.
	 */
	static public Color brighten(Color color, float change) {
		int r = (int)((float)(255 - color.getRed()) * change);
		int g = (int)((float)(255 - color.getGreen()) * change);
		int b = (int)((float)(255 - color.getBlue()) * change);
		return new Color(	Display.getDefault(), 
							color.getRed() + r, 
							color.getGreen() + g, 
							color.getBlue() + b);
	}
}
