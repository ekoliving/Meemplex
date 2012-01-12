/*
 * @(#)RemoteInvocationHandler.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */
package org.openmaji.implementation.server.meem.invocation;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import org.openmaji.implementation.server.manager.gateway.GatewayManagerWedge;
import org.openmaji.implementation.server.meem.MeemUnbound;
import org.openmaji.implementation.server.meem.WedgeImpl;
import org.openmaji.implementation.server.meem.core.MeemCoreImpl;
import org.openmaji.implementation.server.meem.wedge.remote.SmartProxyMeem;
import org.openmaji.implementation.server.nursery.diagnostic.DiagnosticLog;
import org.openmaji.implementation.server.nursery.diagnostic.events.invocation.RemoteOutboundInvocationEvent;
import org.openmaji.meem.Meem;
import org.openmaji.meem.MeemClient;
import org.openmaji.meem.MeemPath;
import org.openmaji.meem.filter.ExactMatchFilter;
import org.openmaji.meem.filter.FacetDescriptor;
import org.openmaji.meem.filter.Filter;
import org.openmaji.meem.wedge.reference.Reference;
import org.openmaji.server.helper.EssentialMeemHelper;
import org.openmaji.system.meem.core.MeemCore;
import org.openmaji.system.meem.wedge.remote.RemoteMeem;
import org.openmaji.system.space.resolver.MeemResolver;
import org.openmaji.system.space.resolver.MeemResolverClient;
import org.swzoo.log2.core.LogFactory;
import org.swzoo.log2.core.LogTools;
import org.swzoo.log2.core.Logger;



/**
 * Handler for dealing with remote invocations. Each time a meem shows up
 * we request its proxy and extract the RMI object responsible for sending across
 * the invocation.
 */
public class RemoteInvocationHandler {

	private static Map boundMeems = new HashMap();

	public static void invokeRemoteMeem(MeemPath meemPath, ReflectionInvocation reflectionInvocation) {

		BindTask bindTask;
		synchronized (boundMeems) {
			bindTask = (BindTask) boundMeems.get(meemPath);
			if (bindTask == null) {
				bindTask = new BindTask(meemPath);
				boundMeems.put(meemPath, bindTask);
			}
		}

		bindTask.addInvocation(reflectionInvocation);
	}

	public static void dropMeem(MeemPath meemPath) {
		BindTask bindTask;
		synchronized (boundMeems) {
			bindTask = (BindTask) boundMeems.remove(meemPath);
		}
		if (bindTask != null) {
			bindTask.dispose();
		}
	}
	
	/**
	 * Task for resolving an unbound meem - we do this in a seperate thread so we don't affect 
	 * anything going on in the meem world.
	 */
	public static final class BindTask implements MeemResolverClient, MeemClient {

		private static final int MAX_FAILURE_COUNT = 3;
		
		private final MeemPath meemPath;
		private final LinkedList queue = new LinkedList();
		private boolean bound = false;
		private Meem boundMeem = null;
		private RemoteMeem remoteMeem = null;
		private Reference resolverReference = null;
		private boolean resolving = false;
		
		protected boolean tryAgain = false;

		public BindTask(MeemPath meemPath) {
			this.meemPath = meemPath;
		}
		
		public synchronized void dispose() {
//			System.err.println("BindTask.dispose(): " + meemPath);
			queue.clear();
			Meem resolverMeem = EssentialMeemHelper.getEssentialMeem(MeemResolver.spi.getIdentifier());
			resolverMeem.removeOutboundReference(resolverReference);
			resolverReference = null;
			bound = false;
			boundMeem = null;
			remoteMeem = null;
			resolving = false;
		}

		public synchronized void meemResolved(MeemPath meemPath, Meem meem) {

//			System.err.println("*** meemResolved: " + meemPath + " : " + meem + " : " + this);
			if (!meemPath.equals(this.meemPath))
			{
				LogTools.error(
						logger, 
						"BindTask.meemResolved() listening for: " + this.meemPath + " got " + meemPath);
				return;
			}

			if (meem != boundMeem)
			{
				boundMeem = meem;

				if (meem == null)
				{
					bound = false;
					// the only time this should happen is if the meem cannot be found remotely
					// so lets not try and find it any more. If it turns up later, all well and good
	
					// should we also trash the entry out of the map?
				}
				else if (meem instanceof MeemUnbound) {
					bound = false;
					MeemClient meemClient = (MeemClient) GatewayManagerWedge.getTargetFor(this, MeemClient.class);
					Filter filter = new FacetDescriptor("meem", Meem.class);
					Reference reference = Reference.spi.create("meemClientFacet", meemClient, true, filter);

					meem.addOutboundReference(reference, true);
				}
				else
				{
					InvocationHandler ih = Proxy.getInvocationHandler(meem);
	
					if (ih instanceof SmartProxyMeem) {
						remoteMeem = ((SmartProxyMeem) ih).getRemoteMeem();
					}
					else {
						remoteMeem = findRemote(meem);
					}

					bound = true;
					processQueue();
				}
			}
		}

		public synchronized void addInvocation(ReflectionInvocation reflectionInvocation) {
			if (!resolving) {
				resolve();
			}

			queue.add(reflectionInvocation);

			if (bound) {
				processQueue();
			}
		}

		public synchronized void referenceAdded(Reference reference) {
//			System.err.println("*** referenceAdded: " + meemPath + " : " + reference + " : " + this);
			remoteMeem = findRemote((Meem) reference.getTarget());
			bound = true;
			processQueue();
		}

		public synchronized void referenceRemoved(Reference reference) {
			// TODO [dgh] Auto-generated method stub
		}

		private void resolve() {
//			System.err.println("*** resolve() called: " + meemPath + " : " + this);
			resolving = true;
			Meem resolverMeem = EssentialMeemHelper.getEssentialMeem(MeemResolver.spi.getIdentifier());
			MeemResolverClient meemResolverClient = (MeemResolverClient) GatewayManagerWedge.getTargetFor(this, MeemResolverClient.class);
			Filter filter = new ExactMatchFilter(meemPath);

			resolverReference = Reference.spi.create("meemResolverClient", meemResolverClient, true, filter);

			resolverMeem.addOutboundReference(resolverReference, false);
//			resolverMeem.addOutboundReference(resolverReference, true);
		}
		
		private void processQueue() {
			while (queue.size() > 0) {
				ReflectionInvocation reflectionInvocation = (ReflectionInvocation) queue.getFirst();

				if (remoteMeem == null) {
					// TODO[peter] contentFailed if necessary?
					reflectionInvocation.fail();
					
					tryAgain = false;
					LogTools.info(logger, "remoteMeem is null");
					break;
				}
				else {
					Method remoteMethod = reflectionInvocation.getMethod();
//					System.err.println("RemoteInvocationHandler.majikInvocation: "+ System.currentTimeMillis()+ " rs = " + reflectionInvocation.getRequestStack() + " : " + getDescription(remoteMethod, reflectionInvocation.getArgs()) + " : " + Thread.currentThread().getName());
	
					try {
						MeemPath sourceMeemPath = (MeemPath) reflectionInvocation.getInvocationContext().get(InvocationContext.CALLING_MEEM_PATH);
						if (DiagnosticLog.DIAGNOSE) {
							DiagnosticLog.log(new RemoteOutboundInvocationEvent(sourceMeemPath, meemPath, remoteMethod, reflectionInvocation.getArgs()));
						}
						
						Object[] rargs = reflectionInvocation.getArgs();
						
						Serializable[] sargs = null;
						if (rargs != null) {
							sargs = new Serializable[rargs.length];
							System.arraycopy(rargs, 0, sargs, 0, sargs.length);
						}
						
						remoteMeem.majikInvocation(
							reflectionInvocation.getFacetIdentifier(),
							remoteMethod.getName(),
							remoteMethod.getParameterTypes(),
							sargs,
							reflectionInvocation.getInvocationContext()
						);
						
						queue.removeFirst();
						tryAgain = false;
					}
					catch (RemoteException e) {
//						System.err.println(e.getMessage());
//						System.err.println("(" + reflectionInvocation.getDescription(true) + ")");
//						remoteMeem = null;

						StringBuffer types = new StringBuffer();
						StringBuffer args = new StringBuffer();
						for (int i=0; i<reflectionInvocation.getArgs().length; i++) {
							if (i>0) {
								args.append(",");
								types.append(",");
							}
							args.append(reflectionInvocation.getArgs()[i]);
							types.append(reflectionInvocation.getArgs()[i].getClass());
						}

						LogTools.info(
								logger, 
								"RemoteException while invoking majikInvocation: " + 
									reflectionInvocation.getFacetIdentifier() + "." + 
									remoteMethod.getName() + 
									"(" + args + ")\n" +
									"{" + types + "}",
								e
							);
						
						// TODO[peter] contentFailed if necessary?
						reflectionInvocation.fail();
						
						if (reflectionInvocation.getFailureCount() > MAX_FAILURE_COUNT) {
							queue.remove(reflectionInvocation);
						}
						else {
							tryAgain = true;
						}
						
						// if a remote exception occurs, stop processing the queue
						// -mg- should start a timer or something here to make another attempt in case no methods are called for a while
						break;
					}
					catch (Throwable t) {
						t.printStackTrace();
						break;
					}
				}
			}
//			if (queue.size() > 0) {
//				System.err.println("BindTask.processQueue(): " + meemPath + " : " + queue.size());
//			}
		}

		private static RemoteMeem findRemote(Meem meem) {
			//
			// we have a local meem that has wandered back to us.
			//
			
			Object object = Proxy.getInvocationHandler(meem);
    	
			MeemInvocationTarget target = null;

			if (object instanceof MeemInvocationTarget) {
				target = (MeemInvocationTarget) object;
			} 
			else {
				// ???
			}

			MeemCore meemCore = target.getMeemCore();

			//
			// TODO [dgh] this bit really needs to be rewritten...
			//
			RemoteMeem rMeem = null;
			for (WedgeImpl w : ((MeemCoreImpl) meemCore).getWedgeImpls()) {
				if (w.getImplementation() instanceof RemoteMeem) {
					rMeem = (RemoteMeem) w.getImplementation();
					break;
				}
			}

			return rMeem;
		}
	}
	
	/**
	 * Create the per-class Software Zoo Logging V2 reference.
	 */
	private static final Logger logger = LogFactory.getLogger();
}
