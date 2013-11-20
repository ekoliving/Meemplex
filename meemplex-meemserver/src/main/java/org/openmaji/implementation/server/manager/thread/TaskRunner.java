/*
 * @(#)ObjectRunner.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
package org.openmaji.implementation.server.manager.thread;

import java.util.LinkedList;
import java.util.Queue;

/**
 *
 */
public class TaskRunner extends Thread {
	private Queue<Runnable> runnableList = new LinkedList<Runnable>();

	long taskCount = 0;
	long startTime;
	long busyTime;

	TaskRunner() {
		this.start();
	}

	public void queue(Runnable runnable) {
		synchronized (runnableList) {
			taskCount++;
			runnableList.add(runnable);
			runnableList.notifyAll();
		}
	}

	public void run() {
		startTime = System.currentTimeMillis();

		for (;;) {
			Runnable runnable = null;

			synchronized (runnableList) {
				runnable = runnableList.poll();
			}

			if (runnable != null) {
				long taskStartTime = System.currentTimeMillis();

				try {
					runnable.run();
				}
				catch (Throwable e) {
					// ignore
				}

				busyTime = taskStartTime - System.currentTimeMillis();
			}
			else {
				synchronized (runnableList) {
					try {
						runnableList.wait();
					}
					catch (InterruptedException e) {
						// ignore
					}
				}
			}
		}
	}

	/**
	 * return the number of tasks that have been queued.
	 */
	public long getTaskCount() {
		return taskCount;
	}

	public long getStartTime() {
		return startTime;
	}

	public long getBusyTime() {
		return busyTime;
	}

	public long getTasksWaiting() {
		long waiting;

		synchronized (runnableList) {
			waiting = runnableList.size();
		}

		return waiting;
	}
}
