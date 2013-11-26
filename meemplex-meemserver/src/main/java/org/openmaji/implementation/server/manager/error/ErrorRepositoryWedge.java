/*
 * @(#)ErrorRepositoryWedge.java
 *
 * Copyright 2004 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
package org.openmaji.implementation.server.manager.error;

import java.util.*;
import java.util.Map.Entry;

import org.openmaji.implementation.server.request.Request;
import org.openmaji.implementation.server.request.RequestStack;
import org.openmaji.implementation.server.request.RequestTracker;



import java.util.logging.Level;
import java.util.logging.Logger;

import org.openmaji.meem.*;
import org.openmaji.meem.definition.*;
import org.openmaji.meem.filter.Filter;
import org.openmaji.meem.filter.FilterChecker;
import org.openmaji.meem.filter.IllegalFilterException;
import org.openmaji.meem.wedge.configuration.ConfigurationClient;
import org.openmaji.meem.wedge.configuration.ConfigurationClientAdapter;
import org.openmaji.meem.wedge.configuration.ConfigurationSpecification;
import org.openmaji.meem.wedge.error.ErrorHandler;
import org.openmaji.meem.wedge.lifecycle.LifeCycleState;
import org.openmaji.meem.wedge.reference.Reference;
import org.openmaji.system.meem.wedge.reference.ContentProvider;
import org.openmaji.system.meem.wedge.reference.MeemClientCallback;
import org.openmaji.system.meem.wedge.reference.MeemClientConduit;


/**
 * @author mg
 */
public class ErrorRepositoryWedge implements ErrorHandler, Wedge, FilterChecker, MeemDefinitionProvider {

	private static final Logger logger = Logger.getAnonymousLogger();
	
	public MeemContext meemContext;
	
	public ErrorHandler errorRepositoryClient;
	
	public MeemClientConduit meemClientConduit;

	public final ContentProvider<ErrorHandler> errorRepositoryClientProvider = new ContentProvider<ErrorHandler>() {
		public synchronized void sendContent(ErrorHandler client, Filter filter) {

			if (filter == null) {
				RequestStack currentRequestStack = RequestTracker.getRequestStack();
				for (Map.Entry<RequestStack, Throwable> entry : errorMap.entrySet()) {
					RequestTracker.setRequestStack(entry.getKey());
					client.thrown(entry.getValue());
				}
				RequestTracker.setRequestStack(currentRequestStack);
			}
			else if (filter instanceof RequestFilter) {
				RequestStack currentRequestStack = RequestTracker.getRequestStack();
				Request request = ((RequestFilter) filter).getRequest();

				for (RequestStack requestStack : errorMap.keySet()) {
					if (requestStack.peekRequest().equals(request)) {
						RequestTracker.setRequestStack(requestStack);
						client.thrown(errorMap.get(requestStack));
						break;
					}
				}
				RequestTracker.setRequestStack(currentRequestStack);
			}
		}
	};

	private MaximumSizeMap<RequestStack, Throwable> errorMap = new MaximumSizeMap<RequestStack, Throwable>();

	private static Timer timer = new Timer(true);

	public Integer numberOfErrors = new Integer(100);

	public Integer errorLifeTime = new Integer(300000);

	public ConfigurationClient configurationClientConduit = new ConfigurationClientAdapter(this);

	public transient ConfigurationSpecification<Integer> numberOfErrorsSpecification = ConfigurationSpecification.create(
			"Number of errors to keep", Integer.class, LifeCycleState.READY);

	public transient ConfigurationSpecification<Integer> errorLifeTimeSpecification = ConfigurationSpecification.create(
			"Maximum time to keep error (msec)", Integer.class, LifeCycleState.READY);

	public void setNumberOfErrors(Integer numberOfErrors) {
		this.numberOfErrors = numberOfErrors;
	}

	public void setErrorLifeTime(Integer errorLifeTime) {
		this.errorLifeTime = errorLifeTime;
	}

	/*
	 * Inbound ErrorHandler methods
	 * 
	 * @see org.openmaji.meem.wedge.error.ErrorHandler#thrown(java.lang.Throwable)
	 */
	public void thrown(Throwable throwable) {
		// log it
		logger.log(Level.WARNING, "Error thrown", throwable);
		
		// get the current request stack
		RequestStack requestStack = (RequestStack) RequestTracker.getRequestStack().clone();

		// check to see if we need to pass this error to a different repository
		Request request = requestStack.peekRequest();
		if (request != null) {
			MeemPath errorRepositoryPath = request.getErrorRepository();
			if (!meemContext.getSelf().getMeemPath().equals(errorRepositoryPath)) {
				// hand this error off to correct repository
				
				meemClientConduit.provideReference(Meem.spi.get(errorRepositoryPath), "errorRepository", ErrorHandler.class, new ErrorCallback(throwable));
			}
		}		
		
		// add new throwable into the map
		errorMap.put(requestStack, throwable);

		// start timer
		timer.schedule(new ErrorTimerTask(requestStack), errorLifeTime.intValue());

		// notify clients
		errorRepositoryClient.thrown(throwable);
		
		
	}
	
	/*
	 * FilterChecker methods
	 * 
	 * @see org.openmaji.meem.filter.FilterChecker#invokeMethodCheck(org.openmaji.meem.filter.Filter, java.lang.String, java.lang.Object[])
	 */
	public boolean invokeMethodCheck(Filter filter, String facetName, String methodName, Object[] args) throws IllegalFilterException {
		if (!(filter instanceof RequestFilter)) {
      throw new IllegalFilterException("Can't check filter: " + filter);
		}
		
		Request request = ((RequestFilter)filter).getRequest();
		RequestStack requestStack = RequestTracker.getRequestStack();
		if (requestStack.peekRequest() != null && requestStack.peekRequest().equals(request)) {
			return true;
		}
		return false;
	}

	class MaximumSizeMap<K, V> extends LinkedHashMap<K, V> {
		private static final long serialVersionUID = 958238884334L;
		
		/**
		 * @see java.util.LinkedHashMap#removeEldestEntry(java.util.Map.Entry)
		 */
		protected boolean removeEldestEntry(Entry<K, V> eldest) {
			if (size() > numberOfErrors.intValue()) {
				return true;
			}
			return super.removeEldestEntry(eldest);
		}
	}

	class ErrorTimerTask extends TimerTask {

		private final RequestStack requestStack;

		public ErrorTimerTask(RequestStack requestStack) {
			this.requestStack = requestStack;
		}

		/**
		 * @see java.lang.Runnable#run()
		 */
		public void run() {
			errorMap.remove(requestStack);
		}
	}
	
	/*
	 * @see org.openmaji.meem.definition.MeemDefinitionProvider#getMeemDefinition()
	 */
	public MeemDefinition getMeemDefinition() {
		Class<?>[] wedges = new Class[] {
				ErrorRepositoryWedge.class
			};
		MeemDefinition meemDefinition = MeemDefinitionFactory.spi.create().createMeemDefinition(wedges);
		MeemDefinitionUtility.renameFacetIdentifier(meemDefinition,"ErrorRepositoryWedge","errorHandler","errorRepository");
    
		return meemDefinition;
	}
	
	private class ErrorCallback implements MeemClientCallback<ErrorHandler> {
		private final Throwable throwable;

		public ErrorCallback(Throwable throwable) {
			this.throwable = throwable;
		}

		/*
		 * @see org.openmaji.system.meem.wedge.reference.MeemClientCallback#referenceProvided(org.openmaji.meem.wedge.reference.Reference)
		 */
		public void referenceProvided(Reference<ErrorHandler> reference) {
			if (reference != null) {
				ErrorHandler errorRepository = reference.getTarget();
				errorRepository.thrown(throwable);
			}
		}
	}
}