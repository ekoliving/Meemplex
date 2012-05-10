/*
 * @(#)MeemServiceItemFilter.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
package org.openmaji.implementation.server.manager.registry;

import java.io.Serializable;

import net.jini.core.lookup.ServiceID;
import net.jini.core.lookup.ServiceItem;
import net.jini.lookup.ServiceItemFilter;

/**
 * Basic filter for looking up meems
 */
public class MeemServiceItemFilter
	implements ServiceItemFilter, Serializable
{
	private static final long serialVersionUID = -833434909459L;
	
	private ServiceID serviceID = null;

	public MeemServiceItemFilter(
		ServiceID	meemServiceID)
	{
		this.serviceID = meemServiceID;
	}
	
    /* (non-Javadoc)
     * @see net.jini.lookup.ServiceItemFilter#check(net.jini.core.lookup.ServiceItem)
     */
    public boolean check(ServiceItem serviceItem)
    {
		return(serviceID.equals(serviceItem.serviceID));
    }
}
