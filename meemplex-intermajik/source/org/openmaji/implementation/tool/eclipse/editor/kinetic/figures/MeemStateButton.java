/*
 * @(#)MeemStateButton.java
 * Created on 14/11/2003
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic.figures;


import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.ImageFigure;
import org.eclipse.draw2d.Toggle;
import org.eclipse.swt.graphics.Image;
import org.openmaji.implementation.tool.eclipse.images.Images;
import org.openmaji.meem.wedge.lifecycle.LifeCycleState;


/**
 * <code>MeemStateButton</code>.
 * <p>
 * @author Kin Wong
 */
public class MeemStateButton extends Toggle {
//	static private final int MARGIN = 1;

	private LifeCycleState state = LifeCycleState.ABSENT;
	static private Image meemStateAbsent = Images.ICON_MEEM_STATE_ABSENT.createImage();
	static private Image meemStateDormant = Images.ICON_MEEM_STATE_DORMANT.createImage();
	static private Image meemStateLoaded = Images.ICON_MEEM_STATE_LOADED.createImage();
	static private Image meemStatePending = Images.ICON_MEEM_STATE_PENDING.createImage();
	static private Image meemStateReady = Images.ICON_MEEM_STATE_READY.createImage();
	
	public MeemStateButton() {
		setOpaque(true);
		setStyle(STYLE_TOGGLE);
		
		setRequestFocusEnabled(false);
		setFocusTraversable(false);
		setRolloverEnabled(true);
		setContents(createStateFigure(LifeCycleState.ABSENT));
		setSize(-1,-1);
	}
	
	
	public void setState(LifeCycleState state) {
		if(this.state.equals(state)) return;
		this.state = state;
		setContents(createStateFigure(state));
	}

	static Figure createStateFigure(LifeCycleState state) {
		if(state.equals(LifeCycleState.READY)) {
			return new ImageFigure(meemStateReady);
		}
		else
		if(state.equals(LifeCycleState.LOADED)) {
			return new ImageFigure(meemStateLoaded);
		}
		else
		if(state.equals(LifeCycleState.ABSENT)) {
			return new ImageFigure(meemStateAbsent);
		}
		else
		if(state.equals(LifeCycleState.DORMANT)) {
			return new ImageFigure(meemStateDormant);
		}
		else
		if(state.equals(LifeCycleState.PENDING)) {
			return new ImageFigure(meemStatePending);
		}
		return new Figure();
	}
}
