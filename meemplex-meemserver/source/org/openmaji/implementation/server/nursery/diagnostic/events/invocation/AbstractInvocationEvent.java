/*
 * @(#)AbstractInvocationEvent.java
 *
 * Copyright 2005 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
package org.openmaji.implementation.server.nursery.diagnostic.events.invocation;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Method;

import org.openmaji.implementation.server.nursery.diagnostic.DiagnosticLog;
import org.openmaji.implementation.server.nursery.diagnostic.events.thread.PoolThreadTracker;
import org.openmaji.implementation.server.request.RequestStack;
import org.openmaji.implementation.server.request.RequestTracker;
import org.openmaji.meem.MeemPath;



/**
 * @author mg
 */
public abstract class AbstractInvocationEvent implements InvocationEvent {

	private long timeStamp;
		
	private MeemPath sourceMeemPath;
	private MeemPath targetMeemPath;
	
	private String targetMethodName;
	private Object[] targetMethodArgs;
	
	private RequestStack requestStack;
	private String threadName;
	private long threadID;
	
	private long eventID;
	
	protected AbstractInvocationEvent(MeemPath sourceMeemPath, MeemPath targetMeemPath, Method targetMethod, Object[] targetMethodArgs) {
		this.eventID = DiagnosticLog.getEventID();
		
		this.timeStamp = System.currentTimeMillis();
		this.threadName = Thread.currentThread().getName();
		this.threadID = PoolThreadTracker.getThreadID();		
		
		this.sourceMeemPath = sourceMeemPath;
		this.targetMeemPath = targetMeemPath;
		
		this.targetMethodName = targetMethod.getDeclaringClass().getName() + "." + targetMethod.getName();
		this.targetMethodArgs = targetMethodArgs;
		
		this.requestStack = RequestTracker.getRequestStack();
	}
	
	public long getEventID() {
		return eventID;
	}
	
	public long getThreadID() {
		return threadID;
	}
 
	public MeemPath getSourceMeemPath() {
		return sourceMeemPath;
	}
	
	public MeemPath getTargetMeemPath() {
		return targetMeemPath;
	}
	
	public Object[] getTargetMethodArgs() {
		return targetMethodArgs;
	}
	
	public String getTargetMethodName() {
		return targetMethodName;
	}
	
	public RequestStack getRequestStack() {
		return requestStack;
	}
	
	public long getTimeStamp() {
		return timeStamp;
	}
	
	public String getThreadName() {
		return threadName;
	}
	
	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		out.writeLong(eventID);
		out.writeLong(timeStamp);
		out.writeObject(threadName);
		out.writeLong(threadID);
		out.writeObject(sourceMeemPath);
		out.writeObject(targetMeemPath);
		out.writeObject(targetMethodName);
		if (targetMethodArgs != null && targetMethodArgs.length > 0) {
			out.writeInt(targetMethodArgs.length);
			// any objects that are not serializable - just turn into strings
			for (int i = 0; i < targetMethodArgs.length; i++) {
				if (targetMethodArgs[i] instanceof Serializable || targetMethodArgs[i] == null) {
					out.writeObject(targetMethodArgs[i]);
				} else {
					out.writeObject(targetMethodArgs[i].toString());
				}
			}			
		}	else {
			out.writeInt(0);
		}
		out.writeObject(requestStack);
	}
	
	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
		eventID = in.readLong();
		timeStamp = in.readLong();
		threadName = (String) in.readObject();
		threadID = in.readLong();
		sourceMeemPath = (MeemPath) in.readObject();
		targetMeemPath = (MeemPath) in.readObject();
		targetMethodName = (String) in.readObject();
		
		int argsSize = in.readInt();
		targetMethodArgs = new Object[argsSize];
		for (int i = 0; i < argsSize; i++) {
			targetMethodArgs[i] = in.readObject();
		}
		
		requestStack = (RequestStack) in.readObject();
	}
}
