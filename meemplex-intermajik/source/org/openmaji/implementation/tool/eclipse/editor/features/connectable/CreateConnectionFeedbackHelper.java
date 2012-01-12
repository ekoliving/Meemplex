/*
 * @(#)CreateConnectionFeedbackHelper.java
 * Created on 21/04/2003
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.features.connectable;

import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.editpolicies.FeedbackHelper;

/**
 * <code>CreateConnectionFeedbackHelper</code>.
 * <p>
 * @author Kin Wong
 */
public class CreateConnectionFeedbackHelper extends FeedbackHelper {
	protected IConnection connection;
	protected IConnectionFeedbackProvider feedbackProvider;
	/**
	 * Constructs an instance of <code>CreateConnectionFeedbackHelper</code>.
	 * <p>
	 * @param feedbackProvider
	 */
	CreateConnectionFeedbackHelper(
		IConnection connection, 
		IConnectionFeedbackProvider feedbackProvider) {
		this.connection = connection;
		this.feedbackProvider = feedbackProvider;
		//feedbackProvider.update(connection, getConnection());
	}
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.FeedbackHelper#update(org.eclipse.draw2d.ConnectionAnchor, org.eclipse.draw2d.geometry.Point)
	 */
	public void update(ConnectionAnchor anchor, Point p) {
		//System.out.println("update(" + anchor + ", " + p + ")");
		super.update(anchor, p);
	}
}
