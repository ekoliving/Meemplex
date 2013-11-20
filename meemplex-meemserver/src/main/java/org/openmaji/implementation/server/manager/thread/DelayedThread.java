/*
 * @(#)DelayedThread.java
 *
 * Copyright 2004 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
package org.openmaji.implementation.server.manager.thread;

class DelayedThread extends Thread {
	private final Runnable runnable;

	private final long absoluteTime;

	public DelayedThread(Runnable runnable, long absoluteTime) {
		this.runnable = runnable;
		this.absoluteTime = absoluteTime;
	}

	public void run() {
		long delta = absoluteTime - System.currentTimeMillis();
		try {
			if (delta > 0) {
				Thread.sleep(delta);
			}
			runnable.run();
		}
		catch (InterruptedException e) {
			// ignore
		}
	}
}