/*
 * @(#)MeemInvocationSource.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - getInvocationSource() should only hand out a Weak Reference.
 * - Weak References to meemInvocationSource should be "tracked".
 * - Consider whether this should have a org.openmaji.meem.Facet interface.
 *
 * - The Dynamic Proxy Object invoke() method seems to throw a RuntimeException
 *   when once of the arguments is "null".
 */

package org.openmaji.implementation.server.meem.invocation;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.logging.Logger;

import org.openmaji.implementation.server.Common;
import org.openmaji.implementation.server.meem.FacetImpl;
import org.openmaji.implementation.server.meem.wedge.reference.AsyncContentProvider;
import org.openmaji.implementation.server.meem.wedge.remote.RemoteReference;
import org.openmaji.implementation.server.nursery.diagnostic.DiagnosticLog;
import org.openmaji.implementation.server.nursery.diagnostic.events.invocation.OutboundInvocationEvent;
import org.openmaji.implementation.server.utility.ProxyUtility;
import org.openmaji.meem.MeemPath;
import org.openmaji.meem.definition.FacetAttribute;
import org.openmaji.meem.filter.Filter;
import org.openmaji.meem.filter.FilterChecker;
import org.openmaji.meem.filter.IllegalFilterException;
import org.openmaji.meem.wedge.error.ErrorHandler;
import org.openmaji.meem.wedge.reference.Reference;
import org.openmaji.system.meem.hook.invoke.Invocation;
import org.openmaji.system.meem.wedge.reference.ContentProvider;


/**
 * <p>
 * ...
 * </p>
 * <p>
 * Note: Implementation thread safe = Not considered yet
 * </p>
 * @author  Andy Gelme
 * @version 1.0
 */

public class MeemInvocationSource implements InvocationHandler
{
	private static final Logger logger = Logger.getAnonymousLogger();
	
	public ErrorHandler errorHandlerConduit;

//	private static boolean DEBUG = false;
	
	private HashSet<Reference> references = new HashSet<Reference>();
	private FilterChecker filterChecker = null;

	private final FacetImpl facet;
	public final ContentProvider contentProvider;
	public final AsyncContentProvider asyncContentProvider;

	public MeemInvocationSource(FacetImpl facet, AsyncContentProvider asyncContentProvider,
		ContentProvider contentProvider, FilterChecker filterChecker)
	{
		this.facet = facet;
	    this.asyncContentProvider = asyncContentProvider;
		this.contentProvider = contentProvider;
	    this.filterChecker = filterChecker;
	}

	/**
	 * TODO: check that filter is correct type.
	 */
	public void addReference(final Reference reference)
	{
//		if (DEBUG) {
//			System.err.println("---add reference: " + reference);
//		}
		synchronized (references) {
			references.add(reference);
		}
	}

	/**
	 * 
	 */
	public boolean removeReference(Reference reference)
	{
//		if (DEBUG) {
//			System.err.println("---remove reference: " + reference);
//		}
		synchronized (references) {
			return references.remove(reference);
		}
	}

	/**
	 * 
	 */
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
	{
		// Special handling for Object method calls
		// Must occur before iterating through the out-bound Facet References

		Object result = ProxyUtility.handleObjectMethods(this, method, args);

		if (result != null)
		{
			return result;
		}

//		if (Common.TRACE_ENABLED) {
//			logger.info("--> out: " + facet.getWedgeImpl().getMeemPath() + " : " + facet.getWedgeImpl().getImplementationClassName() + " : " + facet.getIdentifier() + " . " + method.getName());
//		}
		
		synchronized (references) {
			if (!references.isEmpty())
			{
				final Invocation invocation = new ReflectionInvocation(facet.getFacetAttribute().getIdentifier(), method, args);

//				int matchCount = 0;
//				int nullCount = 0;
//				if (DEBUG) {
//					if (references.size() > 200) {
//						StringBuffer msg = new StringBuffer();
//						msg.append("! greater than 200 references for facet (");
//						msg.append(references.size());
//						msg.append("): ");
//						msg.append(facet.getWedgeImpl().getMeemPath());
//						msg.append(".");
//						msg.append(facet.getIdentifier());
//						msg.append('.');
//						msg.append(method.getName());
//						msg.append('(');
//						for (int i=0; i<args.length; i++) {
//							if (i>0) {
//								msg.append(',');
//							}
//							msg.append(args[i]);	
//						}
//						msg.append(')');
//						
//						System.err.println(msg.toString());
//					}
//				}
			
				HashSet<Reference> invalidReferences = new HashSet<Reference>();
				for (Reference reference : references) {
					if (reference instanceof RemoteReference) {
						boolean isValid = ((RemoteReference) reference).isValid();
						if (!isValid) {
							//System.err.println("Non Valid Reference. Removing: " + reference + " : " + meemPath);
//							referenceIterator.remove();
							invalidReferences.add(reference);
							continue;
						}
					}
					
					if (filterChecker != null)
					{
						Filter filter = reference.getFilter();
						if (filter != null)
						{
							try
							{
								if (!filterChecker.invokeMethodCheck(filter, method.getName(), args)) {
									continue;
								}
							}
							catch (IllegalFilterException illegalFilterException)
							{
								System.err.println("Illegal filter:" + illegalFilterException);
								continue;
							}
						}
					}
	//				else if (DEBUG) {
	//					nullCount++;
	//				}
					
					MeemPath targetMeemPath = null;
					Object target = reference.getTarget();
					if (Proxy.isProxyClass(target.getClass())) {
						InvocationHandler ih = Proxy.getInvocationHandler(target);
						if (ih instanceof MeemInvocationTarget) {
							targetMeemPath = ((MeemInvocationTarget)ih).getMeemPath();
						}
					}
	
					if (DiagnosticLog.DIAGNOSE) {
						DiagnosticLog.log(new OutboundInvocationEvent(facet.getWedgeImpl().getMeemPath(), targetMeemPath, method, args));
					}
					
//					if (Common.TRACE_ENABLED) {
//						logger.info("--> out: " + facet.getWedgeImpl().getMeemPath() + " : " + facet.getWedgeImpl().getImplementationClassName() + " : " + facet.getIdentifier() + " . " + method.getName());
//					}
//					if (Common.TRACE_ENABLED) {
//						logger.info("... to: " + targetMeemPath + " : " + facet.getIdentifier() + " . " + method.getName());
//					}
					
					invocation.invoke(reference.getTarget(), errorHandlerConduit);
					
				}
				references.removeAll(invalidReferences);	// remove any invlaid references
			
//				if (DEBUG) {
//					if (references.size() > 200) {
//						System.err.println("\nmatched targets = " + matchCount + ", " + nullCount + " had no filters");
//					}
//				}
			}
		}

		return null;
	}
	
	public FacetAttribute getFacetAttribute() {
		return facet.getFacetAttribute();
	}
	
	public Class getSpecification() {
		return facet.getSpecification();
	}
}
