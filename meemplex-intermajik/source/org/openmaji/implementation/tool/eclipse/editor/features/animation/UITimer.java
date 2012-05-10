/*
 * @(#)UITimer.java
 * Created on 27/04/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.features.animation;

import org.openmaji.implementation.tool.eclipse.client.SWTClientSynchronizer;

/**
 * <code>UITimer</code>.
 * <p>
 * @author Kin Wong
 */
public class UITimer extends Timer {
	protected void performStep(){
		SWTClientSynchronizer.getDefault().execute(getRunnable());
	}
}