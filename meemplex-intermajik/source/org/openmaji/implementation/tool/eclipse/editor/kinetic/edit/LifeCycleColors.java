/*
 * @(#)LifeCycleColors.java
 * Created on 9/06/2004
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic.edit;

import java.util.HashMap;
import java.util.Map;


import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.openmaji.meem.wedge.lifecycle.LifeCycleState;

/**
 * <code>LifeCycleColors</code>.
 * <p>
 * @author Kin Wong
 */
public class LifeCycleColors {
	static private Color READY = new Color(Display.getDefault(),105, 230, 077);
	static private Color PENDING = new Color(Display.getDefault(),249, 244, 0);
	static private Color LOADED = new Color(Display.getDefault(),250, 166, 19);
	static private Color DORMANT = new Color(Display.getDefault(),223, 0, 41);
	static private Color ABSENT = new Color(Display.getDefault(),223, 0, 41);

	//static private Color PENDING_ALT = new Color(Display.getDefault(),0, 0, 0);

	static Map lcsToColors;
	static {
		lcsToColors = new HashMap();
		lcsToColors.put(LifeCycleState.READY, READY);
		lcsToColors.put(LifeCycleState.PENDING, PENDING);
		lcsToColors.put(LifeCycleState.LOADED, LOADED);
		lcsToColors.put(LifeCycleState.DORMANT, DORMANT);
		lcsToColors.put(LifeCycleState.ABSENT, ABSENT);
	}
	
	static Color getColor(LifeCycleState state) {
		return (Color)lcsToColors.get(state);
	}
	
	static Color getColor(LifeCycleState state, int seed) {
		Color color = getColor(state);
		return color;
	}
}
