/*
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.network;

import org.openmaji.meem.MeemContext;
import org.openmaji.meem.Wedge;
import org.openmaji.meem.wedge.configuration.ConfigurationClient;
import org.openmaji.meem.wedge.configuration.ConfigurationClientAdapter;
import org.openmaji.meem.wedge.configuration.ConfigurationSpecification;
import org.openmaji.meem.wedge.lifecycle.*;


/**
 * TODO complete this.
 */
public class WakeOnLanWedge implements Wedge
{
	public MeemContext meemContext;

	
	/* ----------------- conduits ------------------ */

	public Vote lifeCycleControlConduit = null;

	public ConfigurationClient configurationClientConduit = new ConfigurationClientAdapter(this);

	public LifeCycleClient lifeCycleClientConduit = new LifeCycleClientAdapter(this);

	
	/* ------------ persisted fields ---------------- */

	public String broadcastAddress = "192.168.1.255";

	public String[] macAddresses = null;

	public long waitTime = 10000;
	

	/* -------------- private members --------------- */
	
	WakeOnLan wakeOnLan = new WakeOnLan();


	/* ------------- configuration specifications ----------------------- */

	public transient ConfigurationSpecification broadcastAddressSpecification = ConfigurationSpecification.create("Broadcast address");

	public transient ConfigurationSpecification addressesSpecification = ConfigurationSpecification.create("MAC addresses separated by spaces");

	public transient ConfigurationSpecification waitTimeSpecification = ConfigurationSpecification.create("Time, in seconds, between sending \"Wake On LAN\" messages");

	public void setBroadcastAddress(String address) {
		this.broadcastAddress = address;
	}
	
	public String getBroadcastAddress() {
		return broadcastAddress;
	}
	
	public void setAddresses(String addresses) {
		String[] strs = addresses.split("\\s");
		this.macAddresses = strs;
	}
	
	public String getAddresses() {
		StringBuffer sb = new StringBuffer();
		if (macAddresses != null) {
			for (int i=0; i<macAddresses.length; i++) {
				if (i>0) {
					sb.append(" ");
				}
				sb.append(macAddresses[i]);
			}
		}
		return sb.toString();
	}
  
	public void setWaitTime(String secondsString) {
		this.waitTime = Long.parseLong(secondsString) * 1000;
	}
	
	public String getWaitTime() {
		return Long.toString(waitTime/1000);
	}
  
	/* ------- Meem functionality ------------------------------------- */
	
	/**
	 * 
	 */
	public void commence() {
		wakeOnLan.setBroadcastAddress(broadcastAddress);
		wakeOnLan.setMacAddresses(macAddresses);
		wakeOnLan.setWaitTime(waitTime);
		wakeOnLan.start();
	}

	/**
	 * 
	 */
	public void conclude() {
		wakeOnLan.stop();
	}
  
	
	/* -------------- private methods ------------ */
	

}

