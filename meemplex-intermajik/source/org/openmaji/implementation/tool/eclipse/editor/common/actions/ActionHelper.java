/*
 * @(#)ActionHelper.java
 * Created on 4/06/2004
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.common.actions;

import org.eclipse.jface.action.Action;

/**
 * <code>ActionHelper</code>.
 * <p>
 * @author Kin Wong
 */
public class ActionHelper {
	static public void copyDetails(Action source, Action target) {
		if(source == null) {
			target.setText("");
			target.setDescription("");
			target.setImageDescriptor(null);
			target.setHoverImageDescriptor(null);
			target.setToolTipText(null);
		}
		else {
			target.setText(source.getText());
			target.setDescription(source.getDescription());
			target.setImageDescriptor(source.getImageDescriptor());
			target.setHoverImageDescriptor(source.getHoverImageDescriptor());
			target.setToolTipText(source.getToolTipText());
		}
	}
}
