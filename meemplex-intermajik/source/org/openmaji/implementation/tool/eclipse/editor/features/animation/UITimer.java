/*
 * @(#)UITimer.java
 * Created on 27/04/2003
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
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