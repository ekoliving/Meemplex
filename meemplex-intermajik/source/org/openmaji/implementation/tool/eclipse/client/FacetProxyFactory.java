/*
 * @(#)FacetProxyFactory.java
 * Created on 5/05/2004
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.client;

import java.lang.reflect.Constructor;

/**
 * <code>FacetProxyFactory</code> provides a consistent means of creating
 * <code>FacetProxy</code> in <code>MeemClientProxy</code>.
 * <p>
 * @author Kin Wong
 * @see org.openmaji.implementation.tool.eclipse.client.MeemClientProxy
 * @see org.openmaji.implementation.tool.eclipse.client.FacetProxy
 */
public class FacetProxyFactory {
	static private FacetProxyFactory _instance = new FacetProxyFactory();
	
	static public FacetProxyFactory getInstance() {
		return _instance;
	}
	
	//private Map classToFacetProxy = new HashMap();
	private FacetProxyFactory() {
	}

	public FacetProxy create(MeemClientProxy clientProxy, Class type) {
		if(!FacetProxy.class.isAssignableFrom(type)) return null;
		try {
			Constructor constructor = 
				type.getDeclaredConstructor(new Class[]{ MeemClientProxy.class });
			return (FacetProxy) constructor.newInstance(new Object[]{clientProxy});
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
