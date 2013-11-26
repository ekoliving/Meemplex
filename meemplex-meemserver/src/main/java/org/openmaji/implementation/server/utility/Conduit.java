/*
 * @(#)Conduit.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - In invoke(), pass Exceptions to the Error Facet, so that all Conduit
 *   targets are invoked.
 */

package org.openmaji.implementation.server.utility;

import java.lang.reflect.*;

/**
 * <p>
 * Conduit ...
 * </p>
 * <p>
 * Note: Implementation thread safe = Yes (2003-10-08)
 * </p>
 * @author  Andy Gelme
 * @version 1.0
 */

public interface Conduit<T> extends InvocationHandler {

  public void addTarget(T target)
    throws IllegalArgumentException;

  public String getIdentifier();

  public T getProxy();

  public Class<T> getSpecification();

  public void removeTarget(T target);

  public Object invoke(Object proxy, Method   method, Object[] args)
    throws   Throwable;
}
