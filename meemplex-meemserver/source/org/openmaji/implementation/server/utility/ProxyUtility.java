/*
 * @(#)ProxyUtility.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

/* ToDo:
 * - Should this be moved to "org.openmaji.meem.invocation", perhaps into
 *   the InvocationProcessor class (keep everything together) ?
 */

package org.openmaji.implementation.server.utility;

import java.lang.reflect.*;

/**
 * <p>
 * ProxyUtility is a collection of methods for managing Proxys.
 * </p>
 * <p>
 * Note: Implementation thread safe = Single-threaded (2003-04-15)
 * </p>
 * @author  Andy Gelme
 * @version 1.0
 */

public final class ProxyUtility {

  /**
   * Perform special handling for Object method calls.
   *
   * @param object Object instance that the method is to be invoked upon
   * @param method Method to be invoked
   * @param args   Array of Objects containing the values of the arguments
   * @return       Value to return from the Object method invocation
   * @exception InternalError Thrown if an unexpected Object method is invoked
   */

  public static final Object handleObjectMethods(
    Object   object,
    Method   method,
    Object[] args)
    throws   InternalError {

    if (method.getDeclaringClass() == Object.class) {
      String methodName = method.getName();

      if (methodName.equals("equals")) {
        if (args.length > 0  &&  args[0] != null) {
					if(object.equals(args[0])) {
						return(Boolean.TRUE);
					} else {
	        	// check to see if the args[0] object is a proxy.
	        	// if it is, get the invocationHandler out of it and use that in the equals
	        	boolean isProxy = Proxy.isProxyClass(args[0].getClass());
	        	if (isProxy) {
							Object invocationHandler = Proxy.getInvocationHandler(args[0]);
							if(object.equals(invocationHandler)) {
								return(Boolean.TRUE);
							} else {
								return(Boolean.FALSE);
							}
	        	} else {
	        		return(Boolean.FALSE);
	        	}
					}

        }
        else {
          return(Boolean.FALSE);
        }
      }

      if (methodName.equals("hashCode")) {
        return(new Integer(object.hashCode()));
      }

      if (methodName.equals("toString")) {
        return(object.toString());
      }

      throw new InternalError(
        "Unexpected Object method invocation: " + method
      );
    }

    return(null);
  }
}
