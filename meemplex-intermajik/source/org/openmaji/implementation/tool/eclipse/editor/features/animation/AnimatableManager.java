/*
 * @(#)AnimatableManager.java
 * Created on 23/04/2003
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.features.animation;

import java.util.Iterator;
import java.util.LinkedList;


/**
 * <code>AnimatableManager</code>.
 * <p>
 * @author Kin Wong
 */
public class AnimatableManager {
	LinkedList animatables;
	long startTime = 0;
	long time = 0;
	Timer timer;
	
	protected static final int DELAY = 50;
	
	synchronized public void addAnimatable(IAnimatable animatable) {
		if(animatables == null) animatables = new LinkedList();
		animatables.add(animatable);
	}
	
	synchronized public void removeAnimatable(IAnimatable animatable) {
		if(animatables == null) return;
		if(animatables.remove(animatable)) {
			if(animatables.size() == 0) animatables = null;
		}
	}
	private Runnable updateAction = new Runnable() {
		public void run() {
			fireUpdate();
		}
	};
	public void start() {
		//System.out.println("AnimatableManager.start()");
		startTime = System.currentTimeMillis();
		timer = new UITimer();
		timer.queue(updateAction, DELAY);
		timer.start();
	}

	public void stop() {
		//System.out.println("AnimatableManager.stop()");
		suspend();
	}	
	public void resume() {
		//System.out.println("AnimatableManager.resume()");
		timer = new Timer();
		timer.queue(updateAction, DELAY);
		timer.start();
	}
	
	public void suspend() {
		//System.out.println("AnimatableManager.suspend()");
		if (timer == null) return;
		timer.stop();
		timer = null;
	}
	protected void fireUpdate() {
		long time = System.currentTimeMillis() - startTime;
		//System.out.println("update: " + time);
		if(animatables != null) {
			synchronized(animatables) {
				Iterator it = animatables.iterator();
				while(it.hasNext()) {
					IAnimatable animatable = (IAnimatable)it.next();
					animatable.updateAnimation(time);
				}
			}
		}
	}
}
