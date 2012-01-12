/*
 * @(#)ConnectionGroupSummarizer.java
 * Created on 16/04/2003
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.features.collapsible;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * <code>ConnectionGroupSummarizer</code>.
 * <p>
 * @author Kin Wong
 */
public class ConnectionGroupSummarizer {
	public List summarizeConnections(Map connectionGroups) {
		List summarizedConnections = new ArrayList();
		Iterator it = connectionGroups.values().iterator();
		while(it.hasNext()) {
			Object group = it.next();
			if(!(group instanceof Collection)) continue;
			Collection innerConnections = (Collection)group; 
			createSummarizedConnection(summarizedConnections, innerConnections);
		}
		return summarizedConnections;
	}
	
	protected void createSummarizedConnection(
		List summarizedConnections, 
		Collection innerConnections) {
			summarizedConnections.addAll(innerConnections);
		}
}
