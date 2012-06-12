/*
 * @(#)PoolingThreadManagerWedge.java
 *
 * Copyright 2004 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 * 
 */

package org.openmaji.implementation.server.manager.thread;

import java.security.AccessController;
import java.util.*;

import javax.security.auth.Subject;

import org.openmaji.implementation.server.nursery.diagnostic.DiagnosticLog;
import org.openmaji.implementation.server.nursery.diagnostic.events.thread.CreatePoolThreadEvent;
import org.openmaji.implementation.server.nursery.diagnostic.events.thread.PoolThreadEndTaskEvent;
import org.openmaji.implementation.server.nursery.diagnostic.events.thread.PoolThreadStartTaskEvent;

import org.openmaji.meem.Wedge;
import org.openmaji.meem.definition.*;
import org.openmaji.meem.wedge.error.ErrorHandler;
import org.openmaji.system.manager.thread.ThreadManager;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Peter
 */

public class PoolingThreadManagerWedge implements ThreadManager, MeemDefinitionProvider, Wedge {

	public ErrorHandler errorHandlerConduit;

	private static final Logger logger = Logger.getAnonymousLogger();

	private static final ThreadGroup poolThreadGroup = new ThreadGroup("Maji Thread Pool");

	private static final SchedulerThread schedulerThread = new SchedulerThread(new ThreadManager() {
		public void queue(Runnable runnable) {
			PoolingThreadManagerWedge.queueRunnable(runnable);
		}

		public void queue(Runnable runnable, long absoluteTime) {
		}

		public void cancel(Runnable runnable) {
		}
	});

	private static final LinkedList<Runnable> readyRunnables = new LinkedList<Runnable>();

	private static int ACTIVE_THREADS = 5;

	private static int MINIMUM_POOL_THREADS = 10;

	private static boolean shouldExit = false;

	private static int totalThreadCount;

	private static int activeThreadCount;

	private static int availableThreadCount;

//	private static class ExitContinuation extends ThreadLocal<PigeonHole> {
//		public PigeonHole initialValue() {
//			return null;
//		}
//	}

	//private static ExitContinuation exitContinuation = new ExitContinuation();

	public static void startup() {
		String activeThreadProperty = System.getProperty(ThreadManager.PROPERTY_THREADMANAGER_ACTIVE_THREADS);

		if (activeThreadProperty != null) {
			Integer activeThread = Integer.valueOf(activeThreadProperty);

			ACTIVE_THREADS = Math.max(1, activeThread.intValue());
			MINIMUM_POOL_THREADS = Math.max(ACTIVE_THREADS + 5, ACTIVE_THREADS * 2);
		}

		logger.log(Level.INFO, "Initialising " + poolThreadGroup.getName() + " with " + ACTIVE_THREADS + " active thread(s)");

		synchronized (readyRunnables) {
			totalThreadCount = MINIMUM_POOL_THREADS;
			activeThreadCount = MINIMUM_POOL_THREADS;
			availableThreadCount = MINIMUM_POOL_THREADS - ACTIVE_THREADS;
		}

		poolThreadGroup.setDaemon(false);

		schedulerThread.start();

		for (int i = 0; i < MINIMUM_POOL_THREADS; ++i) {
			createPoolThread();
		}
	}

	public static void shutdown() {
		synchronized (readyRunnables) {
			shouldExit = true;
			readyRunnables.notify();
		}

		for (;;) {
			synchronized (readyRunnables) {
				if (totalThreadCount < 1) {
					break;
				}
			}

			try {
				Thread.sleep(1000);
			}
			catch (InterruptedException e) {
			}
		}
	}

	private static void doQueuedRunnable(Runnable runnable) {
		synchronized (readyRunnables) {
			readyRunnables.add(runnable);

			if (activeThreadCount < ACTIVE_THREADS) {
				readyRunnables.notify();
			}
		}
	}

	public static void queueRunnable(Runnable runnable) {
		Subject subject = Subject.getSubject(AccessController.getContext());
		final Runnable secureRunnable = new TaskNode(subject, runnable);

		doQueuedRunnable(secureRunnable);
	}

	public static void queueRunnable(final Runnable runnable, long absoluteTime) {
		if (absoluteTime < System.currentTimeMillis()) {
			queueRunnable(runnable);
		}
		else {
			Subject subject = Subject.getSubject(AccessController.getContext());
			final Runnable secureRunnable = new TaskNode(subject, runnable);

			Runnable delayedRunnable = new Runnable() {
				public void run() {
					doQueuedRunnable(secureRunnable);
				}
			};

			schedulerThread.queue(delayedRunnable, absoluteTime);
		}
	}

	public synchronized void queue(Runnable runnable) {
		try {
			queueRunnable(runnable);
		}
		catch (Throwable e) {
			errorHandlerConduit.thrown(e);
		}
	}

	/**
	 * Queue a Runnable to be started no earlier than the passed in time
	 */
	public synchronized void queue(final Runnable runnable, long absoluteTime) {
		if (absoluteTime < System.currentTimeMillis()) {
			queue(runnable);
		}
		else {
			Subject subject = Subject.getSubject(AccessController.getContext());
			final Runnable secureRunnable = new TaskNode(subject, runnable);

			Runnable delayedRunnable = new Runnable() {
				public void run() {
					doQueuedRunnable(secureRunnable);
				}
			};

			schedulerThread.queue(delayedRunnable, absoluteTime);
		}
	}

	public void cancel(Runnable runnable) {
		// TODO cancel the queued job
	}

//	public static void exitContinuation(final PigeonHole pigeonHole) {
//		Runnable runnable = new Runnable() {
//			public void run() {
//				exitContinuation.set(pigeonHole);
//			}
//		};
//
//		// NB: Don't need to secure this Runnable
//		doQueuedRunnable(runnable);
//	}

	public static boolean isMajiThread() {
		return Thread.currentThread().getThreadGroup() == poolThreadGroup;
	}

	private static final Runnable poolThreadFunc = new Runnable() {
		public void run() {
			for (;;) {
				
//				PigeonHole pigeonHole = exitContinuation.get();
//				if (pigeonHole != null) {
//					exitContinuation.set(null);
//					synchronized (pigeonHole) {
//						pigeonHole.notify();
//					}
//				}

				Runnable runnable = null;

				synchronized (readyRunnables) {
//					if (pigeonHole == null) {
						--activeThreadCount;
//					}
//					else 
//					if (totalThreadCount <= MINIMUM_POOL_THREADS) {
//						++availableThreadCount;
//					}
//					else {
//						--totalThreadCount;
//						break;
//					}

					while (activeThreadCount >= ACTIVE_THREADS || readyRunnables.isEmpty()) {
						if (shouldExit && activeThreadCount == 0) {
							--totalThreadCount;
							readyRunnables.notify();
							return;
						}

						try {
							readyRunnables.wait();
						}
						catch (InterruptedException e) {
						}
					}

					++activeThreadCount;

					runnable = (Runnable) readyRunnables.removeFirst();
				}

				if (DiagnosticLog.DIAGNOSE) {
					DiagnosticLog.log(new PoolThreadStartTaskEvent());
				}

				try {
					runnable.run();
				}
				catch (Throwable t) {
					t.printStackTrace();
				}

				if (DiagnosticLog.DIAGNOSE) {
					DiagnosticLog.log(new PoolThreadEndTaskEvent());
				}
			}
		}
	};

	public static void allocatePoolThread() {
		synchronized (readyRunnables) {
			if (availableThreadCount > 0) {
				--activeThreadCount;
				--availableThreadCount;
				readyRunnables.notify();
				return;
			}

			++totalThreadCount;
		}

		createPoolThread();
	}

	private static void createPoolThread() {
		Thread t = new Thread(poolThreadGroup, poolThreadFunc);
		t.setName("Pool " + t.getName());
		t.setDaemon(false);

		if (DiagnosticLog.DIAGNOSE) {
			DiagnosticLog.log(new CreatePoolThreadEvent(t.getName()));
		}

		t.start();
	}

	/* ---------- MeemDefinitionProvider method(s) ----------------------------- */

	private MeemDefinition meemDefinition = null;

	public MeemDefinition getMeemDefinition() {
		if (meemDefinition == null) {
			meemDefinition = MeemDefinitionFactory.spi.create().createMeemDefinition(new Class[] { this.getClass() });
		}

		return (meemDefinition);
	}
}