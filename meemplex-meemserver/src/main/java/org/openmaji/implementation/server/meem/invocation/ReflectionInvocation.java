/*
 * @(#)Invocation.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - Implement getImplementation() and getMethod() methods, such that if the
 *   implementation is null and/or the method is null, then those transient
 *   fields are automagically reconstituted in the new environment.
 *
 * - Consider whether to extract an interface from this class and place it in
 *   "org.openmaji.meem.invocation" or to just move the whole class there.
 */

package org.openmaji.implementation.server.meem.invocation;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.openmaji.implementation.server.meem.MeemSystemWedge;
import org.openmaji.implementation.server.request.RequestStack;
import org.openmaji.implementation.server.request.RequestTracker;
import org.openmaji.meem.Meem;
import org.openmaji.meem.wedge.error.ErrorHandler;
import org.openmaji.meem.wedge.reference.Reference;
import org.openmaji.system.meem.hook.invoke.Invocation;
import org.openmaji.system.meem.wedge.reference.ContentClient;


import java.util.logging.Level;
import java.util.logging.Logger;



/**
 * <p>
 * An Invocation contains the information required to invoke a method with
 * arguments on a given implementation Object.
 * </p>
 * Within the context of a local JVM, the Invocation maintains the actual
 * reference to the local Object implementation and the Method to be invoked.
 * <p>
 * </p>
 * When the Invocation information is transferred between JVMs, then the
 * implementation Object and the Method to be invoked, must be reinstated
 * when received within the remote JVM.
 * <p>
 * </p>
 * <p>
 * Note: Implementation thread safe = Yes (2003-08-21)
 * </p>
 * @author  Andy Gelme
 * @author  Warren Bloomer
 * @version 1.0
 */

public class ReflectionInvocation implements Invocation, Serializable
{
	private static final long serialVersionUID = 5348329329139613L;

	/**
	 * @param facetIdentifier 	the facet this invocation is on.
	 * @param method         	Method to invoke on the given implementation
	 * @param args           		Arguments to use with the method to be invoked
	 */
	public ReflectionInvocation(String facetIdentifier, Method method, Object[] args)
	{
		this.facetIdentifier = facetIdentifier;
		this.method = method;
		this.args = args;
		this.invocationContext = InvocationContextTracker.getInvocationContext().copy();
		this.requestStack = (RequestStack) RequestTracker.getRequestStack().clone();
		
		this.invocationContext.put(RequestStack.REQUEST_STACK, requestStack);
	}

	public void invoke(Object target, ErrorHandler errorHandler)
	{
		InvocationContextTracker.setInvocationContext(invocationContext);
		RequestTracker.setRequestStack(requestStack);
		
		try
		{
			method.invoke(target, args);
		}
		catch (InvocationTargetException invocationTargetException)
		{
			if (errorHandler == null) {
				StringBuffer msg = new StringBuffer();
				
				msg.append("Got unhandled InvocationTargetException: ");
				msg.append(facetIdentifier);
				msg.append(".");
				msg.append(method.getName());
				msg.append("(");
				for (int i=0; i<args.length; i++) {
					if (i>0) {
						msg.append(",");
					}
					msg.append(args[i]);
				}
				msg.append(")");
				
				logger.log(Level.INFO, msg.toString(), invocationTargetException);
			}
			else {
				errorHandler.thrown(invocationTargetException.getTargetException());
			}
		}
		catch (Throwable throwable)
		{
			if (errorHandler == null) {
				logger.log(Level.INFO, "Got unhandled Throwable", throwable);
			}
			else {
				errorHandler.thrown(throwable);
			}
		}
	}

	public void fail()
	{
		failureCount++;
		
		Class decClass = method.getDeclaringClass();

		if (decClass == Meem.class && method.getName().equals("addOutboundReference"))
		{
			Reference reference = (Reference) args[0];
			ContentClient contentClient = MeemSystemWedge.getContentClientFromTarget(reference.getTarget());
			contentClient.contentFailed("Proxy has been revoked");
		}
	}

	public String getFacetIdentifier()
	{
		return facetIdentifier;
	}

	public String getDescription(boolean detailed)
	{
		StringBuffer result = new StringBuffer();
		result.append("METHOD: ");
		
		String className = method.getDeclaringClass().getName();
		result.append(className.substring(className.lastIndexOf(".") + 1));
		result.append(".");
		result.append(method.getName());

		if (detailed)
		{
			result.append("(");

			if (args != null && args.length > 0)
			{
				int index = 0;
				result.append(args[index++]);

				while (index < args.length)
				{
					result.append(", ");
					result.append(args[index++]);
				}
			}

			result.append(")");
		}

		return result.toString();
	}

	/**
	 * Provides a String representation of <code>Invocation</code>.
	 *
	 * @return String representation of Invocation
	 */
	public synchronized String toString()
	{
		return getClass().getName() + "[" + "method=" + method.getName() + ", arguments=" + args + "]";
	}

	public Object[] getArgs()
	{
		return args;
	}
	
	public Method getMethod()
	{
		return method;
	}
	
	public int getFailureCount() {
		return failureCount;
	}
	
	/**
	 * Method to invoke on the given implementation.
	 */
	private final transient Method method;

	/**
	 * Arguments to use with the method to be invoked.
	 */
	private final Object[] args;
	
	/**
	 * facet identifier we are called on.
	 */
	private final String facetIdentifier;	
	
	private static final Logger logger = Logger.getAnonymousLogger();
	
	private final InvocationContext invocationContext;
	private final RequestStack requestStack;
	
	private int failureCount = 0;

	public InvocationContext getInvocationContext() {
		return (InvocationContext) invocationContext;
	}
}
