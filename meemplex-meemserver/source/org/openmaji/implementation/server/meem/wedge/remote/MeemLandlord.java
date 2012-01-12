/*
 * @(#)MeemLandlord.java
 *
 * Copyright 2004 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */
package org.openmaji.implementation.server.meem.wedge.remote;

import java.util.HashMap;
import java.util.Map;

import org.openmaji.implementation.server.Common;
import org.openmaji.implementation.server.manager.registry.jini.ExporterHelper;
import org.openmaji.meem.MeemPath;
import org.openmaji.system.meem.wedge.remote.Lease;
import org.swzoo.log2.core.LogFactory;
import org.swzoo.log2.core.LogTools;
import org.swzoo.log2.core.Logger;

import net.jini.core.lease.LeaseDeniedException;
import net.jini.core.lease.UnknownLeaseException;
import net.jini.id.Uuid;
import net.jini.id.UuidFactory;

import com.sun.jini.landlord.*;

/**
 * Landlord implementation<br>
 * from http://pandonia.canberra.edu.au/java/jini/tutorial/Lease.xml
 * @author mg
 */
public class MeemLandlord implements Landlord {

	private static final long MAX_LEASE = net.jini.core.lease.Lease.FOREVER;

	private static final long DEFAULT_LEASE = 1000 * 60 * 5; // 5 min

	private Map leasedResourceMap = new HashMap();

	private LeaseFactory factory;

	private LeasePeriodPolicy policy = new FixedLeasePeriodPolicy(MAX_LEASE, DEFAULT_LEASE);

	private Uuid myUuid = UuidFactory.generate();

	public MeemLandlord() {
		// export
		Landlord proxy = (Landlord) ExporterHelper.export(this);
		factory = new LeaseFactory(proxy, myUuid);
	}

	/**
   * @see com.sun.jini.landlord.Landlord#cancel(net.jini.id.Uuid)
	 */
	public void cancel(Uuid cookie) throws UnknownLeaseException {
		if (leasedResourceMap.remove(cookie) == null) {
			throw new UnknownLeaseException();
		}
	}

	/**
	 * @see com.sun.jini.landlord.Landlord#cancelAll(net.jini.id.Uuid[])
	 */
	public Map cancelAll(Uuid[] cookies) {
		Map failMap = new HashMap();
		for (int n = 0; n < cookies.length; n++) {
			try {
				cancel(cookies[n]);
			} catch (UnknownLeaseException e) {
				failMap.put(cookies[n], e);
			}
		}
		if (failMap.isEmpty()) {
			return null;
		} else {
			return failMap;
		}
	}

	/**
	 * @see com.sun.jini.landlord.Landlord#renew(net.jini.id.Uuid, long)
	 */
	public long renew(Uuid cookie, long extension) throws LeaseDeniedException, UnknownLeaseException {
		LeasedResource resource = (LeasedResource) leasedResourceMap.get(cookie);
		LeasePeriodPolicy.Result result = null;
		if (resource != null) {
			result = policy.renew(resource, extension);
		} else {
			throw new UnknownLeaseException();
		}
		if (Common.TRACE_ENABLED && Common.TRACE_LEASING) {
			LogTools.trace(logger, Common.getLogLevelVerbose(), "renew() " + cookie + " : " + extension + " : " + result.duration);
		}
		return result.duration;
	}

	/**
	 * @see com.sun.jini.landlord.Landlord#renewAll(net.jini.id.Uuid[], long[])
	 */
	public Landlord.RenewResults renewAll(Uuid[] cookies, long[] durations) {
		long[] granted = new long[cookies.length];
		Exception[] denied = new Exception[cookies.length];
		boolean wasDenied = false;

		for (int n = 0; n < cookies.length; n++) {
			try {
				granted[n] = renew(cookies[n], durations[n]);
			} catch (UnknownLeaseException e) {
				granted[n] = -1;
				denied[n] = e;
				wasDenied = true;
			} catch (LeaseDeniedException e) {
				granted[n] = -1;
				denied[n] = e;
				wasDenied = true;
			}
		}
		if (wasDenied) {
			return new Landlord.RenewResults(granted, denied);
		} else {
			return new Landlord.RenewResults(granted, null);
		}
	}

	private LeasePeriodPolicy.Result grant(LeasedResource resource, long requestedDuration) throws LeaseDeniedException {
		Uuid cookie = resource.getCookie();
		try {
			leasedResourceMap.put(cookie, resource);
		} catch (Exception e) {
			throw new LeaseDeniedException(e.toString());
		}
		return policy.grant(resource, requestedDuration);
	}

	public Lease generateLease(MeemPath meemPath, long duration) throws LeaseDeniedException {
		MeemPathLeasedResource resource = new MeemPathLeasedResource(meemPath);
		Uuid cookie = resource.getCookie();

		LeasePeriodPolicy.Result result = grant(resource, duration);
		long expiration = result.expiration;
		resource.setExpiration(expiration);

		net.jini.core.lease.Lease lease = factory.newLease(cookie, expiration);
		JiniLeaseWrapper leaseImpl= new JiniLeaseWrapper(lease);
		if (Common.TRACE_ENABLED && Common.TRACE_LEASING) {
			LogTools.trace(logger, Common.getLogLevelVerbose(), "generateLease() " + meemPath + " : " + cookie);
		}
		return leaseImpl;
	}
	
	/* ---------- Logging fields ----------------------------------------------- */

  /**
   * Create the per-class Software Zoo Logging V2 reference.
   */

  private static final Logger logger = LogFactory.getLogger();

}