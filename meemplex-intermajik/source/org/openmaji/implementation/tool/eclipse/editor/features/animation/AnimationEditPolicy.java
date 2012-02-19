/*
 * @(#)AnimationEditPolicy.java
 * Created on 9/06/2004
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.features.animation;

import org.eclipse.gef.editpolicies.AbstractEditPolicy;
import org.eclipse.ui.IEditorPart;
import org.openmaji.implementation.tool.eclipse.editor.features.util.EditPartHelper;


/**
 * <code>AnimationEditPolicy</code>.<p>
 * @author Kin Wong
 */
abstract public class AnimationEditPolicy extends AbstractEditPolicy implements IAnimatable {
	private AnimatableManager animManager;

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.AbstractEditPolicy#activate()
	 */
	public void activate() {
		super.activate();
		IEditorPart editorPart = EditPartHelper.getEditorPart(getHost());
		animManager = 
			(AnimatableManager)editorPart.getAdapter(AnimatableManager.class);
		if(animManager == null) return;
		animManager.addAnimatable(this);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.AbstractEditPolicy#deactivate()
	 */
	public void deactivate() {
		if(animManager != null) animManager.removeAnimatable(this);
		super.deactivate();
	}
	
	
}
