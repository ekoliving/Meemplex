/*
 * @(#)LCMCategoryProxy.java
 * Created on 23/02/2004
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.client;

import java.util.Map;

import org.openmaji.system.manager.lifecycle.LifeCycleManagerCategory;
import org.openmaji.system.manager.lifecycle.LifeCycleManagerCategoryClient;
import org.openmaji.system.space.CategoryClient;


/**
 * <code>LCMCategoryProxy</code>.
 * <p>
 * @author Kin Wong
 */
public class LCMCategoryProxy extends CategoryProxy {
	static private FacetSpecificationPair defaultSpecs = 
	new FacetSpecificationPair(
			new FacetInboundSpecification(LifeCycleManagerCategory.class, "lifeCycleManagerCategory"),
			new FacetOutboundSpecification(LifeCycleManagerCategoryClient.class, "lifeCycleManagerCategoryClient"));
	
	public static class LCMLocalCategoryClient extends LocalCategoryClient implements LifeCycleManagerCategoryClient {

		public LCMLocalCategoryClient(CategoryProxy p, Map nameToEntries) {
			super(p, nameToEntries);
		}
	}
	/**
	 * Constructs an instance of <code>LCMCategoryProxy</code>.
	 * <p>
	 * @param meemClientProxy
	 */
	protected LCMCategoryProxy(MeemClientProxy meemClientProxy) {
		super(meemClientProxy, defaultSpecs);
	}
	
	/**
	 * @see org.openmaji.implementation.tool.eclipse.client.CategoryProxy#createClient(java.util.Map)
	 */
	protected CategoryClient createClient(Map mameToEntries) {
		return new LCMLocalCategoryClient(this, mameToEntries);
	}
	
}
