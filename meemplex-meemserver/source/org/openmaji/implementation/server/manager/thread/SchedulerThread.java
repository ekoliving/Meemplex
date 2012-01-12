/*
 * @(#)SchedulerThread.java
 *
 * Copyright 2004 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */
package org.openmaji.implementation.server.manager.thread;

import java.util.*;

import org.openmaji.system.manager.thread.ThreadManager;

/**
 * TODO replace bubble stuff with a SortedTree.
 * 
 * @author Peter
 */
public class SchedulerThread extends Thread {
	/**
	 * The ThreadManager to use to queue Jobs for immediate execution.
	 */
	private ThreadManager threadManager;

	/**
	 * The queue of jobs to be run.
	 */
	private final SortedSet<SchedulerEntry> jobQueue = new TreeSet<SchedulerEntry>(new JobComparator());

	/**
	 * Constructor
	 * 
	 * @param threadManager
	 */
	public SchedulerThread(ThreadManager threadManager) {
		this.threadManager = threadManager;
	}

	public void run() {
		for (;;) {
			try {
				SchedulerEntry readyEntry = null;
				synchronized (jobQueue) {
					if (jobQueue.isEmpty()) {
						jobQueue.wait();
					}
					else {
						SchedulerEntry nextEntry = jobQueue.first();

						// SchedulerEntry nextEntry = (SchedulerEntry) priorityQueue.get(0);

						long now = System.currentTimeMillis();
						long delay = nextEntry.absoluteTime - now;

						if (delay > 0) {
							jobQueue.wait(delay);
						}
						else {
							readyEntry = nextEntry;
							// removeFirstSchedulerEntry();
							jobQueue.remove(nextEntry);
						}
					}
				}

				if (readyEntry != null) {
					if (threadManager == null) {
						readyEntry.runnable.run();
					}
					else {
						// queue for running immediately
						threadManager.queue(readyEntry.runnable);
					}
				}
			}
			catch (InterruptedException e) {
			}
		}
	}

	protected void queue(Runnable runnable, long absoluteTime) {
		SchedulerEntry schedulerEntry = new SchedulerEntry(runnable, absoluteTime);

		synchronized (jobQueue) {
			addSchedulerEntry(schedulerEntry);
			jobQueue.notify();
		}
	}

	protected void cancel(Runnable runnable) {
		synchronized (jobQueue) {
			removeEntry(runnable);
			jobQueue.notify();
		}
	}

	private void addSchedulerEntry(SchedulerEntry schedulerEntry) {
		jobQueue.add(schedulerEntry);
	}

	private boolean removeEntry(Runnable runnable) {
		boolean result = false;
		synchronized (jobQueue) {
			for (SchedulerEntry job : jobQueue) {
				if (job.runnable.equals(runnable)) {
					result = jobQueue.remove(job);
					break;
				}
			}
		}
		return result;
	}

	/**
	 * A Job
	 */
	private static class SchedulerEntry {
		public SchedulerEntry(Runnable runnable, long absoluteTime) {
			this.runnable = runnable;
			this.absoluteTime = absoluteTime;
		}

		/**
		 * The runnable job
		 */
		private final Runnable runnable;
		
		/**
		 * When to run the job.
		 */
		private final long absoluteTime;
	};

	/**
	 * A comparator used for sorting jobs
	 */
	private static class JobComparator implements Comparator<SchedulerEntry> {
		public int compare(SchedulerEntry o1, SchedulerEntry o2) {
			long result = o1.absoluteTime - o2.absoluteTime;
			if (result == 0) {
				result = o1.hashCode() - o2.hashCode();
			}
			return (int) result;
		}
	}

	
	

	/**
	 * Tester
	 */
	public static void main(String[] args) {
		ThreadManager tm = new ThreadManager() {
			public void queue(Runnable runnable) {
				runnable.run();
			}

			public void queue(Runnable runnable, long absoluteTime) {
				runnable.run();
			}

			public void cancel(Runnable runnable) {
			}
		};

		SchedulerThread st = new SchedulerThread(tm);

		for (long i = 0; i < 100; ++i) {
			final long id = i;
			final long time = 1000L * ((i * 7) % 37) + System.currentTimeMillis();

			Runnable runnable = new Runnable() {
				public void run() {
					System.err.println(id + " : " + new Date(time));
				}
			};

			SchedulerEntry schedulerEntry = new SchedulerEntry(runnable, time);

			st.addSchedulerEntry(schedulerEntry);
		}

		st.run();
	}
}
