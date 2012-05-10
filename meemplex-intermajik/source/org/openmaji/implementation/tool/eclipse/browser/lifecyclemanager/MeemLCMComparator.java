/*
 * @(#)MeemLCMComparator.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.tool.eclipse.browser.lifecyclemanager;

import java.util.Comparator;

import org.openmaji.meem.Meem;


/**
 * <p>
 * ...
 * </p>
 * @author  mg
 * @version 1.0
 */
public class MeemLCMComparator implements Comparator {

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(Object o1, Object o2) {
		if ((o1 instanceof Meem) && (o2 instanceof Meem)) {
			String meemPath1 = ((Meem)o1).getMeemPath().toString();
			String meemPath2 = ((Meem)o2).getMeemPath().toString();
			
			return meemPath1.compareTo(meemPath2);
		}
		if ((o1 instanceof LifeCycleManagerClientImpl) && (o2 instanceof LifeCycleManagerClientImpl)) {
			String meemPath1 = ((LifeCycleManagerClientImpl) o1).getMeemPath().toString();
			String meemPath2 = ((LifeCycleManagerClientImpl) o2).getMeemPath().toString();

			return meemPath1.compareTo(meemPath2);
		}
		if ((o1 instanceof Meem) && (o2 instanceof LifeCycleManagerClientImpl)) {
			String meemPath1 = ((Meem) o1).getMeemPath().toString();
			String meemPath2 = ((LifeCycleManagerClientImpl) o2).getMeemPath().toString();

			return meemPath1.compareTo(meemPath2);
		}
		if ((o1 instanceof LifeCycleManagerClientImpl) && (o2 instanceof Meem)) {
			String meemPath1 = ((LifeCycleManagerClientImpl) o1).getMeemPath().toString();
			String meemPath2 = ((Meem) o2).getMeemPath().toString();

			return meemPath1.compareTo(meemPath2);
		}
		throw new ClassCastException();
	}
}
