/*
 * Copyright 2005 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.deployment;

public class SimpleProgress implements Progress {
	private int endpoint;

	private int progressPoint;

	public SimpleProgress() {
    }
	
	public SimpleProgress(int progress, int endpoint) {
		setCompletionPoint(endpoint);
		setProgressPoint(progress);
    }
	
	public void setCompletionPoint(int endpoint) {
		this.endpoint = endpoint;
	}

	public int getCompletionPoint() {
		return endpoint;
	}

	public void setProgressPoint(int progressPoint) {
		this.progressPoint = progressPoint;
	}

	public int getProgressPoint() {
		return progressPoint;
	}
}
