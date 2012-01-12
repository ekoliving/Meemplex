/*
 * @(#)EssentialMeemHelper.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.server.helper;

import java.util.Hashtable;

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

	private static Hashtable<String, Meem>  essentialMeems = new Hashtable<String, Meem>();
	private static boolean locked = false;

	/**
	 * Used to stop modifications to the list of essential meems. Set once
	 * the Essential LCM has created all the essential Meems.
	 */
	public static void lock() {
		locked = true;
	}

	public static void setEssentialMeem(String meemIdentifier, Meem meem) {
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
		return (Meem)essentialMeems.get(meemIdentifier);		
	}
}
