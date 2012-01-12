/*
 * @(#)ErrorHandlerWedge.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.server.meem.wedge.error;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.openmaji.implementation.server.manager.error.ErrorRepository;
import org.openmaji.meem.Meem;
import org.openmaji.meem.Wedge;
import org.openmaji.meem.filter.Filter;
import org.openmaji.meem.wedge.error.ErrorHandler;
import org.openmaji.meem.wedge.reference.Reference;
import org.openmaji.server.helper.EssentialMeemHelper;
import org.openmaji.system.meem.wedge.reference.ContentProvider;
import org.openmaji.system.meem.wedge.reference.MeemClientCallback;
import org.openmaji.system.meem.wedge.reference.MeemClientConduit;



/**
 * <p>
 * ...
 * </p>
 * 
 * @author mg
 * @version 1.0
 */
public class ErrorHandlerWedge implements ErrorHandler, Wedge {

	private static final Logger logger = Logger.getLogger(ErrorHandler.class.getCanonicalName());
	
	private Meem errorRepositoryMeem = null;

	public MeemClientConduit meemClientConduit;

	private Throwable lastThrowable = null;

	public ErrorHandler errorHandlerClient;

	public final ContentProvider errorHandlerClientProvider = new ContentProvider() {
		public void sendContent(Object target, Filter filter) {
			((ErrorHandler) target).thrown(lastThrowable);
		}
	};

	public ErrorHandler errorHandlerConduit = new ErrorHandlerImpl();

	public ErrorHandler errorClientConduit;

	/*
	 * Inbound facet method
	 * 
	 * @see org.openmaji.meem.aspect.wedge.error.ErrorHandler#thrown(java.lang.Throwable)
	 */
	public void thrown(Throwable throwable) {
		errorClientConduit.thrown(throwable);
	}

	/**
	 * errorHandlerConduit implementation
	 */
	private class ErrorHandlerImpl implements ErrorHandler {

		/**
		 * @see org.openmaji.meem.wedge.error.ErrorHandler#thrown(java.lang.Throwable)
		 */
		public void thrown(Throwable throwable) {
			logger.log(Level.INFO, "Thrown: " + throwable,throwable);;
			
			if (isLooped(throwable)) {
				// log it
				logger.log(Level.WARNING, "Recursive Error thrown. Something has probably gone wrong communicating with the error repository.", throwable);
				
				return;
			}
			
			// store it
			lastThrowable = throwable;

			// hand it off
			errorHandlerClient.thrown(throwable);

			// send it to ErrorRepository
			reportError(throwable);
		}
	}

	private void reportError(Throwable throwable) {
		if (errorRepositoryMeem == null) {
			errorRepositoryMeem = EssentialMeemHelper.getEssentialMeem(ErrorRepository.spi.getIdentifier());
		}
		// commented out for debugging 14th January 2005
//		meemClientConduit.provideReference(errorRepositoryMeem, "errorRepository", ErrorHandler.class, new ErrorCallback(throwable));
	}

	class ErrorCallback implements MeemClientCallback {
		private final Throwable throwable;

		public ErrorCallback(Throwable throwable) {
			this.throwable = throwable;
		}

		/*
		 * @see org.openmaji.system.meem.wedge.reference.MeemClientCallback#referenceProvided(org.openmaji.meem.wedge.reference.Reference)
		 */
		public void referenceProvided(Reference reference) {
			if (reference != null) {
				ErrorHandler errorRepository = (ErrorHandler) reference.getTarget();

				errorRepository.thrown(throwable);
			}
		}
	}
	
	/**
	 * Checks to see if the org.openmaji.meem.aspect.wedge.error.ErrorHandlerWedge.thrown method 
	 * appears in the stack trace more than once. If it does, then we have a looping 
	 * error so we'll drop it. 
	 * (An example of this is to add a reference to the client facet that doesn't implement
	 * ErrorHandler)
	 * @param throwable
	 * @return True if stack trace has loop
	 */
	private boolean isLooped(Throwable throwable) {
		boolean methodFound = false;
		
		StackTraceElement[] stackTraceElements = throwable.getStackTrace();
		
		for (int i = 0; i < stackTraceElements.length; i++) {
			StackTraceElement element = stackTraceElements[i];
			
			if ((element.getClassName().equals(ErrorHandlerWedge.class.getName()) || element.getClassName().equals(ErrorHandlerWedge.ErrorHandlerImpl.class.getName())) && element.getMethodName().equals("thrown")) {
				if (methodFound) {
					return true;
				}
				else
					methodFound = true;
			}				
		}
		
		return false;
	}
}