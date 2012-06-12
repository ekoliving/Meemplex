/*
 * @(#)RemoteMeemWedge.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - Improve the performance of majikInvocation() by being able to get a
 *   specific "invocationReference" from the MeemCore, rather than iterate.
 */

package org.openmaji.implementation.server.meem.wedge.remote;

import java.io.Serializable;
import java.lang.reflect.*;
import java.rmi.RemoteException;
import java.util.ArrayList;

import net.jini.core.lease.LeaseDeniedException;

import org.openmaji.implementation.server.manager.lifecycle.meemkit.MeemkitLifeCycleManager;
import org.openmaji.implementation.server.manager.registry.jini.JiniMeemRegistry;
import org.openmaji.implementation.server.meem.invocation.InvocationContext;
import org.openmaji.implementation.server.meem.invocation.InvocationContextTracker;
import org.openmaji.implementation.server.meem.invocation.MeemInvocationTarget;
import org.openmaji.implementation.server.nursery.diagnostic.DiagnosticLog;
import org.openmaji.implementation.server.nursery.diagnostic.events.invocation.RemoteInboundInvocationEvent;
import org.openmaji.implementation.server.request.RequestStack;
import org.openmaji.implementation.server.request.RequestTracker;
import org.openmaji.implementation.server.space.meemstore.remote.MeemStoreProxy;



import org.openmaji.meem.*;
import org.openmaji.meem.definition.Direction;
import org.openmaji.meem.filter.Filter;
import org.openmaji.meem.wedge.error.ErrorHandler;
import org.openmaji.server.helper.EssentialMeemHelper;
import org.openmaji.system.meem.FacetItem;
import org.openmaji.system.meem.core.MeemCore;
import org.openmaji.system.meem.wedge.reference.ContentProvider;
import org.openmaji.system.meem.wedge.remote.Lease;
import org.openmaji.system.meem.wedge.remote.RemoteMeem;
import org.openmaji.system.meem.wedge.remote.RemoteMeemClient;
import org.openmaji.system.meemserver.MeemServer;
import org.openmaji.system.space.hyperspace.HyperSpace;
import org.openmaji.system.space.meemstore.MeemStore;
import java.util.logging.Level;
import java.util.logging.Logger;

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

public class RemoteMeemWedge implements Wedge, RemoteMeem {

	public static final String SMART_PROXY_LEASE_TIME = "org.openmaji.remote.leasetime";
	
  /**
   * Internal reference to MeemCore
   */

  public MeemCore meemCore;

  /**
   * RemoteMeemClient (out-bound Facet)
   */

  public RemoteMeemClient remoteMeemClientFacet;
  
  public ErrorHandler errorHandlerConduit;
  
  private static transient MeemLandlord meemLandlord = new MeemLandlord();
  private static long leaseTime;
   
  static {
  	String sLeaseTime = System.getProperty(SMART_PROXY_LEASE_TIME, "30000");
  	try {
  		leaseTime = Long.valueOf(sLeaseTime).longValue();
  	} catch (NumberFormatException e) {
  		leaseTime = 30000L;
  	}
  }
  
  public final ContentProvider remoteMeemClientFacetProvider =
    new ContentProvider() {
      public synchronized void sendContent(
        Object target,
        Filter filter) {

// [TODO] MUST REPLACE this WITH THE MeemInvocationTarget PROXY FOR THIS WEDGE
        doRemoteMeemChanged((RemoteMeemClient) target);
      }
    };

  private FacetItem[] facetItems = null;

  private void doRemoteMeemChanged(
    RemoteMeemClient target) {
	
	ArrayList	items = new ArrayList();
	
    if (facetItems == null) {
      if (meemCore.isA(HyperSpace.class)) {
        items.add(
          new FacetItem(
            "hyperSpace", HyperSpace.class.getName(), Direction.INBOUND
          ));
      }
      if (meemCore.isA(MeemStore.class) && !meemCore.isA(MeemStoreProxy.class) && meemCore.getSelf().equals(EssentialMeemHelper.getEssentialMeem(MeemStore.spi.getIdentifier()))) {
        items.add(
          new FacetItem(
            "meemStore", MeemStore.class.getName(), Direction.INBOUND
          ));
      }
		  if (meemCore.isA(JiniMeemRegistry.class)) {
				items.add(
				  new FacetItem(
				 		"jiniMeemRegistry", JiniMeemRegistry.class.getName(), Direction.INBOUND
				  ));
		  }
      if (meemCore.isA(MeemkitLifeCycleManager.class)) {
        items.add(
            new FacetItem(
              MeemkitLifeCycleManager.spi.getIdentifier(), MeemkitLifeCycleManager.class.getName(), Direction.INBOUND
            ));
      }
		  if (meemCore.isA(MeemServer.class)) {
				items.add(
				  new FacetItem(
				 		"meemServer", MeemServer.class.getName(), Direction.INBOUND
				  ));
		  }
      items.add(
        new FacetItem(
          "meem", Meem.class.getName(), Direction.INBOUND
        ));
    }
    
//    if (items.size() > 1) {
//    	System.err.println("remoteMeemChanged: " + items);
//    }

    target.remoteMeemChanged(meemCore.getSelf(), this, (FacetItem[])items.toArray(new FacetItem[items.size()]));
  }

  /**
   * Remote Invocation.
   */
  public void majikInvocation(
    String       facetIdentifier,
    String       methodName,
    Class[]      argsClasses,
    Serializable[]     args,
    Serializable request)
    throws   RemoteException 
  {

    Object target = meemCore.getTarget(facetIdentifier);

    if (target == null) {
        String msg = "majikInvocation(" + methodName + "): No such facetIdentifier=" + facetIdentifier + "\n\t" + meemCore.getMeemPath() + "\n\t" + getDescription(methodName, args);
      logger.log(Level.WARNING,
         
        msg
      );

      return;
//      throw new RemoteException(
//        "majikInvocation(): No such facetIdentifier=" + facetIdentifier
//      );
    }

    try {
    	Object object = Proxy.getInvocationHandler(target);
    	
    	MeemInvocationTarget meemInvocationTarget = null;
    	
    	if (object instanceof MeemInvocationTarget) {
    		meemInvocationTarget = (MeemInvocationTarget) object;
    	} 
    	else {
    		// ???
    		return;
//          throw new RemoteException(
//          "majikInvocation(): No such facetIdentifier=" + facetIdentifier
//        );
    	}
    
      Facet implementation = meemInvocationTarget.getImplementation(facetIdentifier);

      if (implementation == null)
      {
//      	System.err.println("Call to facet [" + facetIdentifier + "." + methodName + "] on revoked proxy");
//		throw new RemoteException("majikInvocation(): Proxy has been revoked");
      }
      else
      {
      	InvocationContextTracker.setInvocationContext((InvocationContext) request);
      	InvocationContext invocationContext = InvocationContextTracker.getInvocationContext();
      	if (invocationContext == null) {
      		invocationContext = new InvocationContext();
      	}
      	RequestStack rs = (RequestStack) invocationContext.get(RequestStack.REQUEST_STACK);
      	if (rs == null) {
      		rs = new RequestStack();
      	}
      	
      	RequestTracker.setRequestStack(rs);
      	      	
        Class targetClass = implementation.getClass();

        Method method = targetClass.getMethod(methodName, argsClasses);
        
//        System.err.println("Remote: rs = " + request + " : " + getDescription(method, args));

        MeemPath sourceMeemPath = (MeemPath) InvocationContextTracker.getInvocationContext().get(InvocationContext.CALLING_MEEM_PATH);
		if (DiagnosticLog.DIAGNOSE) {
			DiagnosticLog.log(new RemoteInboundInvocationEvent(sourceMeemPath, meemCore.getMeemPath(), method, args));
		}
        
        meemInvocationTarget.invoke(target, method, args);
	  }
    }
    catch (NoSuchMethodException noSuchMethodException) {
      logger.log(Level.WARNING, "majikInvocation(): No such method=" + methodName);

      throw new RemoteException(
        "majikInvocation(): No such method=" + methodName
      );
    }
    catch (Throwable throwable) {
      logger.log(Level.WARNING, "majikInvocation(): Exception: ", throwable);

      throw new RemoteException("majikInvocation(): Exception: " + throwable);
    }
  }
  
  public String getDescription(Method method, Object[] args)
	{
		StringBuffer result = new StringBuffer();
		result.append("METHOD: ");
		
		String className = method.getDeclaringClass().getName();
		result.append(className.substring(className.lastIndexOf(".") + 1));
		result.append(".");
		result.append(method.getName());

		if (true)
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
  
  public String getDescription(String methodName, Object[] args)
	{
		StringBuffer result = new StringBuffer();
		result.append("METHOD: ");
		
		result.append(methodName);

		if (true)
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
	 * @see org.openmaji.system.meem.wedge.remote.RemoteMeem#obtainLease()
	 */
	public Lease obtainLease() throws RemoteException {
		try {
			return meemLandlord.generateLease(meemCore.getMeemPath(), leaseTime);
		} catch (LeaseDeniedException e) {
			errorHandlerConduit.thrown(e);
		}
		return null;
	}

  /* ---------- Logging fields ----------------------------------------------- */

  /**
   * Create the per-class Software Zoo Logging V2 reference.
   */

  private static final Logger logger = Logger.getAnonymousLogger();
}
