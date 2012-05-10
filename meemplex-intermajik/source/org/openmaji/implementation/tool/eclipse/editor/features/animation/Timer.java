/*
 * @(#)Timer.java
 * Created on 27/04/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.features.animation;

/**
 * <code>Timer</code>.
 * <p>
 * @author Kin Wong
 */
public class Timer implements Runnable {
	private Thread thread;
	private Runnable run;
	private int period;
	private boolean stopped;

	public void start(){
		getThread().start();
	}
	public void stop() {
		stopped = true;
	}

	public Thread getThread(){
		if (thread == null){
			thread = new Thread(this);
			thread.setPriority(Thread.MIN_PRIORITY);
			thread.setDaemon(true);
		}
		return thread;
	}

	protected Runnable getRunnable(){
		return run;
	}

	protected void performStep(){
		if(getRunnable()!= null) getRunnable().run();
	}

	public synchronized void run(){
		while(!stopped) {
			try {
				wait(period);
			}
			catch (InterruptedException exc){}
			if(stopped) return;
			performStep();
		}
	}
	public void queue(Runnable runnable, int period){
		run = runnable;
		this.period = period;
	}
}