/*
 * @(#)WorksheetWedge.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.intermajik.worksheet;

import org.openmaji.meem.MeemPath;
import org.openmaji.meem.Wedge;
import org.openmaji.meem.filter.Filter;
import org.openmaji.system.meem.wedge.reference.ContentProvider;

/**
 * <p>
 * ...
 * </p>
 * @author  mg
 * @version 1.0
 */
public class WorksheetWedge implements Wedge, Worksheet {

    public Worksheet worksheetClient;
    public final ContentProvider worksheetClientProvider = new ContentProvider() {
        public void sendContent(Object target, Filter filter) {
            Worksheet client = (Worksheet) target;
            client.lifeCycleManagerChanged(lifeCycleManagerPath);
        }
    };

	private MeemPath lifeCycleManagerPath = null;

	/**
	 * @see org.openmaji.implementation.intermajik.worksheet.Worksheet#lifeCycleManagerChanged(org.openmaji.meem.MeemPath)
	 */
	public void lifeCycleManagerChanged(MeemPath lifeCycleManagerPath) {
		this.lifeCycleManagerPath = lifeCycleManagerPath;
		worksheetClient.lifeCycleManagerChanged(lifeCycleManagerPath);
	}


}
