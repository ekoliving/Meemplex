/*
 * @(#)DecoupledInvocationProcessor.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
package org.openmaji.implementation.server.meem.invocation;

import java.io.Serializable;
import java.util.*;

import org.openmaji.implementation.server.manager.thread.PoolingThreadManagerWedge;
import org.openmaji.system.manager.thread.Task;
import org.openmaji.system.manager.thread.ThreadManager;

/**
 * Basic class that handles calls with thread decoupling.
 */
public class DecoupledInvocationProcessor implements ThreadManager, Runnable, Serializable {
	private static final long serialVersionUID = 8367446748900311992L;

	private final LinkedList<Runnable> queue = new LinkedList<Runnable>();

	private boolean running = false;

	public void run() {
		getRunnable().run();

		requeue();
	}

	public synchronized void queue(Runnable runnable) {
		queue.addLast(runnable);

		if (!running) {
			running = true;

			PoolingThreadManagerWedge.queueRunnable(this);
		}
	}

	public Task queue(final Runnable runnable, long absoluteTime) {
		Runnable delayedRunnable = new Runnable() {
			public void run() {
				queue(runnable);
			}
		};

		return PoolingThreadManagerWedge.queueRunnable(delayedRunnable, absoluteTime);
	}

	public void cancel(Runnable runnable) {
	}

	private synchronized Runnable getRunnable() {
		return (Runnable) queue.removeFirst();
	}

	private synchronized void requeue() {
		if (queue.isEmpty()) {
			running = false;
		}
		else {
			// TODO[peter] This will run with the same Subject as previous invocation
			// The Subject should perhaps be bundled with the invocation somehow
			PoolingThreadManagerWedge.queueRunnable(this);
		}
	}
}
