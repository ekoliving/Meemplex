/*
 * @(#)MeemInvocationTarget.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet !
 */

package org.openmaji.implementation.server.meem.invocation;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.security.GeneralSecurityException;
import java.util.logging.Level;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.security.auth.Subject;

import org.openmaji.implementation.server.Common;
import org.openmaji.implementation.server.manager.thread.PoolingThreadManagerWedge;
import org.openmaji.implementation.server.meem.core.MeemCoreImpl;
import org.openmaji.implementation.server.meem.wedge.remote.RemoteReference;
import org.openmaji.implementation.server.nursery.diagnostic.DiagnosticLog;
import org.openmaji.implementation.server.nursery.diagnostic.events.invocation.InboundInvocationEvent;
import org.openmaji.implementation.server.request.RequestStack;
import org.openmaji.implementation.server.request.RequestTracker;
import org.openmaji.meem.Facet;
import org.openmaji.meem.Meem;
import org.openmaji.meem.MeemPath;
import org.openmaji.meem.wedge.error.ErrorHandler;
import org.openmaji.system.meem.core.MeemCore;
import org.openmaji.system.meem.hook.Hook;
import org.openmaji.system.meem.hook.HookProcessor;
import org.openmaji.system.meem.hook.invoke.InvocationIterator;
import org.openmaji.system.meem.hook.invoke.InvocationList;
import org.openmaji.system.meem.hook.invoke.InvocationListProvider;
import org.openmaji.system.meem.wedge.reference.ContentClient;


/**
 * <p>
 * ...
 * </p>
 * <p>
 * Note: Implementation thread safe = Not considered yet
 * </p>
 * @author  Andy Gelme
 * @version 1.0
 * 
 * TODO [dgh] - things to watch out for:
 *    1. when invocations are queued the args array should probably be serialised, otherwise people
 * will get unpleasant surprises.
 *    2. invocations should probably also keep track of what subject they are using.
 *    3. the bind task needs some way of dealing with errors.
 *    4. we are assuming the smart proxy decouples.
 */

public class MeemInvocationTarget implements InvocationHandler, Serializable
{
	private static final long serialVersionUID = 4943111306652941572L;
	
	private static final Logger logger = Logger.getAnonymousLogger();
	
	private transient Facet implementation;
	private transient ContentClient contentClient;
	private transient MeemCoreImpl meemCoreImpl;
	private transient ErrorHandler errorHandlerConduit;

	private final MeemPath meemPath;
	private final String facetIdentifier;
	
	private boolean nonBlocking = false;

	public MeemInvocationTarget(
			MeemCoreImpl meemCoreImpl,
			String facetIdentifer,
	    	Facet implementation,
	    	ContentClient contentClient,
			boolean nonBlocking)
	{			
		this.meemCoreImpl = meemCoreImpl;
		this.facetIdentifier = facetIdentifer;
		this.implementation = implementation;
		this.contentClient = contentClient;
		this.meemPath = meemCoreImpl.getMeemPath();
		this.nonBlocking = nonBlocking;

		errorHandlerConduit = (ErrorHandler) meemCoreImpl.getConduitSource("errorHandler", ErrorHandler.class);
	}

	public String getFacetIdentifier()
	{
		return facetIdentifier;
	}

	public void revoke()
	{
		this.implementation = null;

		if (this.contentClient != null)
		{
			// TODO[peter] Why does this break things?
//			contentClient.contentFailed("Proxy revoked");
			this.contentClient = null;
		}
	}

	public boolean revoke(Facet implementation)
	{
		boolean allowed = (implementation == this.implementation);

		if (allowed)		
		{
			revoke();
		}

		return allowed;		
	}

	public boolean isValid() {
		return implementation != null;
	}

	public MeemPath getMeemPath()
	{
		return meemPath;
	}

	protected MeemCore getMeemCore()
	{
		return meemCoreImpl;
	}

	public Facet getImplementation(String facetIdentifier)
	{
		if (facetIdentifier == null || facetIdentifier.equals(this.facetIdentifier))
		{
			return (implementation);
		}

		return (meemCoreImpl.getSystemImplementation());
	}

	/**
	 * <p>
	 * Invoke an in-bound Facet method on a system Wedge implementation.
	 * </p>
	 * <p>
	 * The Dynamic Proxy Object invocation on the Wedge implementation allows
	 * the Feature invocation mechanism to intercept every in-bound Facet
	 * method call.
	 * </p>
	 * @param proxy  Proxy instance that the method was invoked upon
	 * @param method Corresponds to the interface method invoked on the proxy
	 * @param args   Array of objects containing the values of the arguments
	 * @return       Value to return from the method invocation on the proxy
	 * @exception Throwable Thrown from the actual method invocation
	 */

	public Object invoke(Object proxy, final Method method, final Object[] args) throws Throwable
	{
		final Class<?> decClass = method.getDeclaringClass();
		
		if (decClass == Object.class)
		{
			// call to methods declared in Object class
			return handleObjectMethods(proxy, method, args);
		}

//		if (Common.TRACE_ENABLED) {
//			Object meemId = meemCoreImpl.getMeemStructure().getMeemAttribute().getIdentifier();
//			meemId = meemId == null || meemId.toString().isEmpty() ? meemPath : meemId;
//			logger.log(Level.INFO, "<-- in:  " + meemId + " : " + facetIdentifier + " . " + method.getName());
//		}

		if (decClass == Meem.class && args == null)
		{
			// must be a call to Meem.getMeemPath()
			return this.meemPath;
		}

		Subject currentSubject = Subject.getSubject(java.security.AccessController.getContext());
		if (currentSubject == null)
		{
			errorHandlerConduit.thrown(new GeneralSecurityException("attempt to invoke target with null subject."));
			return null;
		}

		//
		// if this is the case we are remote, have to go through a remote meem, pass on
		// to the remote invocation handler.
		//
		final boolean isMeem = decClass.isAssignableFrom(Meem.class);
		final String facetId = isMeem ? "meem" : facetIdentifier;
		
		// change the meempath values. current is now the caller and we are now the current
		
		InvocationContext invocationContext = InvocationContextTracker.getInvocationContext();
		
		Serializable currentMeemPath = invocationContext.get(InvocationContext.CURRENT_MEEM_PATH);
		Serializable callerMeemPath = invocationContext.get(InvocationContext.CALLING_MEEM_PATH);
		
		invocationContext.put(InvocationContext.CURRENT_MEEM_PATH, meemPath);
		invocationContext.put(InvocationContext.CALLING_MEEM_PATH, currentMeemPath);
		
		final ReflectionInvocation invocation = new ReflectionInvocation(facetId, method, args);
		
		// reset the meempaths back
		
		invocationContext.put(InvocationContext.CURRENT_MEEM_PATH, currentMeemPath);
		invocationContext.put(InvocationContext.CALLING_MEEM_PATH, callerMeemPath);

		if (meemCoreImpl == null)
		{
			RemoteInvocationHandler.invokeRemoteMeem(meemPath, invocation);
		}
		else if (implementation == null)
		{
			invocation.fail();
		}
		else
		{
			Runnable runnable = new InvocationTask(currentSubject, decClass, invocation);

			if (nonBlocking) {
				// queue non-blocked task on thread manager queue
				PoolingThreadManagerWedge.queueRunnable(runnable);
			}
			else {
				// queue blocked task on the DecoupledInvocationProcessor
				meemCoreImpl.getBlockingDip().queue(runnable);
			}
		}

		return null;
	}
    
	public boolean equals(Object other)
	{
		if (other == this) return true;
		if (!(other instanceof MeemInvocationTarget)) return false;

		MeemInvocationTarget mit = (MeemInvocationTarget) other;
		
		if (this.meemCoreImpl != null && mit.meemCoreImpl != null) {
			if (facetIdentifier != mit.facetIdentifier && !facetIdentifier.equals(mit.facetIdentifier)) return false;
		}

		return this.meemPath.equals(mit.meemPath); 
	}

	public int hashCode()
	{
		int facetIdentifierHashCode = facetIdentifier.hashCode();
		int meemPathHashCode = this.meemPath.hashCode();

		return facetIdentifierHashCode ^ meemPathHashCode;
	}
	
	/**
	 * Handle method calls to methods declare in Object.
	 * 
	 * @param proxy the proxy.
	 * @param method the method to invoke.
	 * @param args arguments to the method call.
	 * @return result from the method call.
	 */
	private Object handleObjectMethods(Object proxy, final Method method, final Object[] args) {
		if (args == null)
		{
			if (method.getReturnType() == Integer.TYPE)
			{
				// Object.hashCode
				return new Integer(this.hashCode());
			}

			// Object.toString
			String boundString = meemCoreImpl == null ? "remote" : "local";

			return "MeemInvocationTarget[" + this.meemPath.getLocation() + ", " + boundString + "] " + super.hashCode();
		}

		// Object.equals
		Object other = args[0];
		boolean result = false;

		if (other == proxy)
		{
			result = true;
		}
		else if (Proxy.isProxyClass(other.getClass()))
		{
			InvocationHandler ih = Proxy.getInvocationHandler(other);

			if (ih == this)
			{
				result = true;
			}
			else if (ih != null)
			{
				result = this.equals(ih);
				if (result == false) {
					if (ih instanceof RemoteReference.RemoteReferenceInvocationHandler) {
						RemoteReference.RemoteReferenceInvocationHandler rrih = (RemoteReference.RemoteReferenceInvocationHandler)ih;
						ih = Proxy.getInvocationHandler(rrih.getFacet());
						result = this.equals(ih);
					}
				}
			}
		} 
		else if (other instanceof Meem)
		{
			result = ((Meem) other).getMeemPath().equals(this.meemPath);
		}

		return Boolean.valueOf(result);
	}

	/**
	 * An invocation task that is queued up to be executed on the Meem.
	 * The Subject is also stored so that execution can be made in context
	 * of that Subject.
	 */
	private final class InvocationTask implements Runnable, HookProcessor
	{
		private final ReflectionInvocation invocation;
        private final Class decClass;
        //private final Subject subject;
        
        InvocationIterator invocationIterator;

		InvocationTask(Subject subject, Class decClass, ReflectionInvocation invocation)
		{
			//this.subject = subject;
			
			if (subject == null) {
				throw new IllegalArgumentException("attempt to create invocation with null Subject");
			}
			
			this.decClass = decClass;
			this.invocation = invocation;

			InvocationListProvider invocationListProvider = meemCoreImpl.getInvocationListGenerator();
			InvocationList invocationList = invocationListProvider.generate();
			this.invocationIterator = invocationList.invocationIterator();
		}

		/**
		 * May be called by ReentrantHooks in order to put more context on Thread.
		 * 
		 * @throws Throwable
		 */
		public boolean processHooks() 
			throws Throwable
		{
			boolean continueProcessing = true;
			while (continueProcessing && invocationIterator.hasNext()) {
				Hook nextHook = invocationIterator.nextHook();
				continueProcessing = nextHook.process(invocation, this);
			}
			return continueProcessing;
		}
		
		public void run()
		{
			if (implementation == null) {
				invocation.fail();
			}
			else {
				try {
					// process Hooks
					if (!processHooks()) {
						return;
					}

					// execute the invocation
					Object target;
					if (decClass.isAssignableFrom(Meem.class)) {
						target = meemCoreImpl.getSystemImplementation();
					}
					else if (decClass.isAssignableFrom(ContentClient.class)) {
						target = contentClient;

						// TODO[peter] If we know that this was a "snapshot" reference, then
						// we can automatically revoke ourselves at this point
					}
					else {
						target = implementation;
					}
	
					if (target != null) {
						InvocationContext invocationContext = invocation.getInvocationContext();
						MeemPath sourceMeemPath = (MeemPath) invocationContext.get(InvocationContext.CALLING_MEEM_PATH);
						RequestTracker.setRequestStack((RequestStack) invocationContext.get(RequestStack.REQUEST_STACK));
						if (DiagnosticLog.DIAGNOSE) {
							DiagnosticLog.log(new InboundInvocationEvent(sourceMeemPath, meemPath, invocation.getMethod(), invocation.getArgs()));
						}
//						if (Common.TRACE_ENABLED) {
//							Object meemId = meemCoreImpl.getMeemStructure().getMeemAttribute().getIdentifier();
//							meemId = meemId == null || meemId.toString().isEmpty() ? meemPath : meemId;
//							logger.log(Level.INFO, "<-- in:  " + meemId + " : " + facetIdentifier + " . " + invocation.getMethod().getName());
//						}
						
						invocation.invoke(target, errorHandlerConduit);
					}
				}
				catch (Throwable t) {
					logger.log(Level.INFO, "got error invoking Target");
					errorHandlerConduit.thrown(t);
				}					
			}
		}

//		public void run()
//		{
//			if (implementation == null) {
//				invocation.fail();
//			}
//			else {
//				PrivilegedAction action = new PrivilegedAction()
//				{
//					public Object run()
//					{
//						try {
//							// process Hooks
//							if (!processHooks()) {
//								return null;
//							}
//
//							// execute the invocation
//							Object target;
//							if (decClass.isAssignableFrom(Meem.class)) {
//								target = meemCoreImpl.getSystemImplementation();
//							}
//							else if (decClass.isAssignableFrom(ContentClient.class)) {
//								target = contentClient;
//
//								// TODO[peter] If we know that this was a "snapshot" reference, then
//								// we can automatically revoke ourselves at this point
//							}
//							else {
//								target = implementation;
//							}
//			
//							if (target != null) {
//								invocation.invoke(target, errorHandlerConduit);
//							}
//						}
//						catch (Throwable t) {
//							errorHandlerConduit.thrown(t);
//						}
//					
//						return null;
//					}
//				};
//
//				Subject.doAsPrivileged(subject, action, null);
//			}
//		}
	}
	
//	private static final Logger logger = Logger.getAnonymousLogger();
}
