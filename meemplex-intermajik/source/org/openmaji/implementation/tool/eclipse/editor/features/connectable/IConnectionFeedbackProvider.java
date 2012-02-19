/*
 * @(#)IConnectionFeedbackProvider.java
 * Created on 21/04/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.features.connectable;

import org.eclipse.draw2d.Connection;

/**
 * <code>IConnectionFeedbackProvider</code>.
 * <p>
 * @author Kin Wong
 */
public interface IConnectionFeedbackProvider {
	void update(IConnection model, Connection figure);
}
