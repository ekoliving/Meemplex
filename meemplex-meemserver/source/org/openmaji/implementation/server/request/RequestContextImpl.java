/*
 * @(#)RequestContextImpl.java
 *
 * Copyright 2004 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
package org.openmaji.implementation.server.request;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.openmaji.implementation.server.manager.error.ErrorRepository;
import org.openmaji.implementation.server.manager.error.RequestFilter;
import org.openmaji.meem.Meem;
import org.openmaji.meem.wedge.error.ErrorHandler;
import org.openmaji.meem.wedge.reference.Reference;
import org.openmaji.server.helper.EssentialMeemHelper;
import org.openmaji.system.meem.core.MeemCore;
import org.openmaji.system.meemserver.MeemServer;
import org.openmaji.system.request.RequestContext;
import org.openmaji.system.request.RequestCreationException;
import org.openmaji.system.request.RequestSuspendedException;
import org.openmaji.system.request.RequestTimeoutException;
import org.openmaji.system.request.SuspendedRequest;



/**
 * @author mg
 */
public class RequestContextImpl implements RequestContext {

	private final Serializable uniqueIdentifier;
	private MeemCore meemCore;
	
	private ErrorHandler requestErrorConduit; // outbound conduit
	private ErrorHandler errorClientConduit = new ErrorHandlerImpl(); // inbound conduit
	
	private Meem errorRepositoryMeem = null;
	
	private static Timer timer = new Timer(true);
	
	private Map<Request, Object> requestContexts = new HashMap<Request, Object>();
	private Map<Request, TimerTask> requestTimerTasks = new HashMap<Request, TimerTask>();
	private Map<Request, Reference> requestErrorReferences = new HashMap<Request, Reference>();
	
	public RequestContextImpl(MeemCore meemCore, Serializable uniqueIdentifier) {
		this.meemCore = meemCore;
		this.uniqueIdentifier = uniqueIdentifier;
		
		// connect to requestErrorConduit
		requestErrorConduit = (ErrorHandler) meemCore.getConduitSource("requestError", ErrorHandler.class);
		
		// connect to errorClientConduit
		meemCore.addConduitTarget("errorClient", ErrorHandler.class, errorClientConduit);
	}
	
	/**
	 * @see org.openmaji.system.request.RequestContext#begin(long, java.lang.Object)
	 */
	public synchronized void begin(long timeout, Object context) throws RequestCreationException {
		if (timeout < 1) {
			throw new RequestCreationException("Timeout value must be greater than zero");
		}
		
		if (context == null) {
			throw new RequestCreationException("Context must not be null");
		}
		
		RequestStack currentRequestStack = RequestTracker.getRequestStack();
		currentRequestStack = (RequestStack) currentRequestStack.clone();
		
		Request newRequest = generateNewRequest();
		
		requestContexts.put(newRequest, context);
		
		currentRequestStack.pushRequest(newRequest);
		
		connectToErrorRepository(newRequest);
		
//		System.err.println(uniqueIdentifier + " : " + currentRequestStack);
		
		RequestTracker.setRequestStack(currentRequestStack);
		
		// start timer
		
		RequestTimerTask timerTask = new RequestTimerTask(timeout, currentRequestStack);
		
		requestTimerTasks.put(newRequest, timerTask);
		
		timer.schedule(timerTask, timeout);
		
	}
	/**
	 * @see org.openmaji.system.request.RequestContext#end()
	 */
	public synchronized void end() {
		RequestStack currentRequestStack = (RequestStack) RequestTracker.getRequestStack().clone();
		Request request = currentRequestStack.matchLatestRequest(requestContexts.keySet());
		
		TimerTask timerTask = (TimerTask) requestTimerTasks.remove(request); 
		if (timerTask != null) {
			timerTask.cancel();
					
			requestContexts.remove(request);
			
			currentRequestStack.popRequest(request);
			
			RequestTracker.setRequestStack(currentRequestStack);
		}

	}
	
	/**
	 * @see org.openmaji.system.request.RequestContext#get()
	 */
	public synchronized Object get() {
		RequestStack rs = RequestTracker.getRequestStack();
		Request request = rs.matchLatestRequest(requestContexts.keySet());
		if (request != null) {
			return requestContexts.get(request);
		}
		return null;
	}
	/**
	 * @see org.openmaji.system.request.RequestContext#suspend()
	 */
	public SuspendedRequest suspend() throws RequestSuspendedException {
		return new SuspendedRequestImpl(uniqueIdentifier);
	}
	
	private Request generateNewRequest() {
		return new Request(MeemServer.spi.getName(), getErrorRepositoryMeem().getMeemPath(), uniqueIdentifier);
	}
	
	private Meem getErrorRepositoryMeem() {
		if (errorRepositoryMeem == null) {
			errorRepositoryMeem = EssentialMeemHelper.getEssentialMeem(ErrorRepository.spi.getIdentifier());
		}
		return errorRepositoryMeem;
	}
	
	private void connectToErrorRepository(Request request) {
		Reference reference = Reference.spi.create("errorRepositoryClient", meemCore.getTarget("errorHandler"), false, new RequestFilter(request));
		
		requestErrorReferences.put(request, reference);
		
		getErrorRepositoryMeem().addOutboundReference(reference, false);
	}
	
	private void disconnectErrorRepository(Request request) {
		Reference reference = (Reference) requestErrorReferences.remove(request);
		if (reference != null) {
			getErrorRepositoryMeem().removeOutboundReference(reference);
		}
	}
	
	class RequestTimerTask extends TimerTask {
		
		private final RequestStack requestStack;
		private final long timeout;
		
		public RequestTimerTask(long timeout, RequestStack requestStack) {
			this.requestStack = requestStack;
			this.timeout = timeout;
		}
		
		/**
		 * @see java.lang.Runnable#run()
		 */
		public void run() {			
			// remove error reference
			disconnectErrorRepository(requestStack.peekRequest());
			
			RequestStack currentRequestStack = (RequestStack) RequestTracker.getRequestStack();
			
//			System.err.println("TIMERTASK RUN: " + requestStack);
			
			RequestTimeoutException exception;
			
			RequestTracker.setRequestStack(requestStack);
			
			Request request = requestStack.peekRequest();
			
			// if the request was suspended remotely, then the cached stack won't know about it but there is nothing we can do about it at the moment
			
			if (request != null) {
				if (request.suspended != null) {
					exception = new RequestTimeoutException("Request timed out. (Timeout " + timeout + " msec)", new RequestSuspendedException("Request suspended by " + request.suspended + " not resumed"));
				} else {
					exception = new RequestTimeoutException("Request timed out. (Timeout " + timeout + " msec)");
				}
			} else {
				exception = new RequestTimeoutException("Request timed out. (Timeout " + timeout + " msec)");
			}
			
			errorClientConduit.thrown(exception);
			
			RequestTracker.setRequestStack(currentRequestStack);
		}
	}
	
	class ErrorHandlerImpl implements ErrorHandler {
		
		/**
		 * @see org.openmaji.meem.wedge.error.ErrorHandler#thrown(java.lang.Throwable)
		 */
		public void thrown(Throwable throwable) {			
			RequestStack currentRequestStack = (RequestStack) RequestTracker.getRequestStack().clone();
			Request request = currentRequestStack.peekRequest();
			
			TimerTask timerTask = (TimerTask) requestTimerTasks.remove(request); 
			if (timerTask != null) {
				timerTask.cancel();

				// remove error reference
				disconnectErrorRepository(request);
				
				requestErrorConduit.thrown(throwable);
								
				requestContexts.remove(request);
				
				currentRequestStack.popRequest(request);
				
				RequestTracker.setRequestStack(currentRequestStack);
			}
		}
	}
	
}

