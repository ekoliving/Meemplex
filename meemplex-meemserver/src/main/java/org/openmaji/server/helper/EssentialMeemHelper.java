/*
 * @(#)EssentialMeemHelper.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.server.helper;

import java.util.HashMap;
import java.util.Map;

import org.openmaji.meem.Meem;


/**
 * <p>
 * This is a quick workaround for the changing of essential Meems MeemPath format.
 * This should really be replaced by TypeSpace checks when TypeSpace is going.
 * </p>
 * @author  mg
 * @version 1.0
 */
public class EssentialMeemHelper {

	private static Map<String, Meem>  essentialMeems = new HashMap<String, Meem>();
	private static boolean locked = false;

	/**
	 * Used to stop modifications to the list of essential meems. Set once
	 * the Essential LCM has created all the essential Meems.
	 */
	public static void lock() {
		locked = true;
	}

	public static void setEssentialMeem(String meemIdentifier, Meem meem) {
		//System.out.println("set essential meem: " + meemIdentifier + " : " + meem.getMeemPath() + " : locked=" + locked);
		if (!locked) {
			essentialMeems.put(meemIdentifier, meem);
		}		
	}
	
	/**
	 * Used to obtain the MeemPath of essential Meems
	 * @param meemIdentifier The identifer specified in the Meems interface
	 * @return The MeemPath to the requested Meem
	 */
	public static Meem getEssentialMeem(String meemIdentifier) {
		//System.out.println("getting essential meem: " + meemIdentifier + " : " + essentialMeems.get(meemIdentifier));
		return essentialMeems.get(meemIdentifier);		
	}
}
