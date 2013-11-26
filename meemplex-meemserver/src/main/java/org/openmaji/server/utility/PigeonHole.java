/*
 * @(#)PigeonHole.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - Use a boolean condition variable, rather than overloading "null" pigeon.
 *
 * - Refactor get() to use get(timeout) and avoid code duplication.
 * - Provide get(long timeout) and put(long timeout) variants.
 * - Provide reset() method, so that the PigeonHole can be reused.
 * - Provide a Vector, so that multiple put()s can occur.
 *
 * - Significant Design Consideration:
 *   PigeonHole was originally intended to enabled code designed for
 *   multi-threaded, asynchronous, void methods to be used during the
 *   single-threaded bootstrap Genesis of a MajiServer.
 *   However, synchronous Helpers that utilize the PigeonHole may also be
 *   invoked when the MajiServer is running multi-threaded with thread
 *   decoupling enabled.  This means that potentially numerous threads
 *   could enter a PigeonHole that is already in use.  This is due to
 *   the way that Helpers currently use a single static PigeonHole.
 *   The problem is to ensure that use of a given PigeonHole is
 *   either single-threaded or protected from multiple simulaneous
 *   access.  Either way, it would be better if the solution is
 *   provided as part of the PigeonHole design, rather than worrying
 *   about every synchronous Helper getting it right.
 */

package org.openmaji.server.utility;

import java.util.ArrayList;
import java.util.List;

import org.openmaji.system.gateway.AsyncCallback;
import org.openmaji.system.manager.thread.Task;
import org.openmaji.system.manager.thread.ThreadManager;
import org.openmaji.system.meem.wedge.reference.ContentException;

/**
 * <p>
 * A PigeonHole is a mutex protected holder for a single Object.
 * </p>
 * <p>
 * This allows one thread to asynchronously hand off an Object to another thread. Only one Object may be placed in the PigeonHole at a time.
 * </p>
 * <p>
 * If the "Object consuming thread" attempts to read from an empty PigeonHole, then it will wait until the PigeonHole is filled. If the "Object producing thread" attempts to write
 * to a filled PigeonHole, then it will wait until the PigeonHole is emptied.
 * </p>
 * <p>
 * A PigeonHole is particularly useful for creating "synchronous helper classes", which provide a method call that will block, hiding an underlying asynchronous request and
 * response pair. Note: Synchronous helper classes should only be used in special circumstances, e.g bootstrap phase. In general, the goal is to produce as lively (asynchronous) a
 * system as possible.
 * </p>
 * <p>
 * Note: Implementation thread safe = Single-threaded (1996-03-01)
 * </p>
 * 
 * @author Andy Gelme
 * @version 1.0
 */

public final class PigeonHole<T> {
	
	public static final String PROPERTY_TIMEOUT = "org.openmaji.server.pigeonhole.timeout";
	
	/**
	 * PigeonHole Timeout value
	 */
	private static final long timeout = Long.parseLong(System.getProperty(PigeonHole.PROPERTY_TIMEOUT, "60000"));

	/**
	 * Object that is to be passed between asynchronous threads
	 */
	private T pigeon = null;

	private Exception exception = null;

	private boolean received = false;

	private int waitingThreads = 0;
	
	private List<AsyncCallback<T>> callbacks = new ArrayList<AsyncCallback<T>>();

	/**
	 * 
	 * @param callback
	 */
	public synchronized void get(final AsyncCallback<T> callback) {
		
		if (pigeon != null) {
			callback.result(pigeon);
		}
		else if (exception != null) {
			callback.exception(exception);
		}
		else {
			this.callbacks.add(callback);

			// check for timeouts
			final Task timeoutTask = ThreadManager.spi.create().queue(new Runnable() {
				public void run() {
					synchronized (PigeonHole.this) {
						callbacks.remove(callback);
						callback.exception(new TimeoutException());
					}
				}
			}, System.currentTimeMillis() + timeout);
			if (timeoutTask != null) {
				this.callbacks.add(new AsyncCallback<T>() {
					public void result(T result) {
						timeoutTask.cancel();
					}
					public void exception(Exception e) {
						timeoutTask.cancel();
					}
				});
			}
		}
	}

	public synchronized T get() throws ContentException, TimeoutException {
		return get(timeout);
	}

	/**
	 * Retrieve an Object from the PigeonHole. If the PigeonHole is empty, then wait until it is filled.
	 * 
	 * @param timeout
	 *            maximum time in milliseconds to wait for a result
	 * @return Object stored in the PigeonHole
	 * @throws TimeoutException
	 *             When the time to wait has expired
	 */

	public synchronized T get(long timeout) throws ContentException, TimeoutException {
		if (timeout < 1) {
			throw new IllegalArgumentException("Timeout must be greater than zero");
		}

		if (!received) {
			if (++waitingThreads > 1) {
				System.err.println("WARNING: More than one getter in a single PigeonHole");
			}

			long end = System.currentTimeMillis() + timeout;
			long remaining = timeout;

			do {
				try {
					this.wait(remaining);
					if (exception != null) {
						if (exception instanceof TimeoutException) {
							throw (TimeoutException) exception;
						}
						else {
							throw new ContentException(exception);
						}
					}
				}
				catch (InterruptedException interruptedException) {
					throw new ContentException(exception);
				}
				remaining = end - System.currentTimeMillis();
			} while (!received && remaining > 0);

			--waitingThreads;

			this.notify();

			if (!received) {
				// TODO[peter] There is a race condition if the pigeon hole times out
				// after the put() but before the notify()
				throw new TimeoutException("Timeout of " + ((float) timeout / 1000.0) + " seconds exceeded while waiting for the pigeon");
			}
		}

		return pigeon;
	}

	/**
	 * Place an Object into the PigeonHole. If the PigeonHole is full, then wait until it is emptied.
	 * 
	 * @param pigeon
	 *            Object to be placed in the PigeonHole
	 */
	public synchronized void put(T pigeon) {
		if (received) {
			throw new RuntimeException("Pigeons may only be used once!");
		}

		this.received = true;
		this.pigeon = pigeon;
		for (AsyncCallback<T> callback : callbacks) {
			callback.result(pigeon);
		}
		this.notify();
	}
	
	public synchronized void exception(Exception e) {
		this.exception = e;
		for (AsyncCallback<T> callback : callbacks) {
			callback.exception(exception);
		}
		notifyAll();
	}
}
