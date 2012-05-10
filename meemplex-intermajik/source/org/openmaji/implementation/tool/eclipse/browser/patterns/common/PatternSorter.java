/*
 * @(#)PatternSorter.java
 *
 * Copyright 2004 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
package org.openmaji.implementation.tool.eclipse.browser.patterns.common;


import org.eclipse.jface.viewers.*;
import org.openmaji.implementation.tool.eclipse.hierarchy.nodes.MeemNode;
import org.openmaji.implementation.tool.eclipse.hierarchy.nodes.Node;
import org.openmaji.system.meempool.metadata.Abstract;
import org.openmaji.system.presentation.InterMajik;


/**
 * @author Peter
 */
public class PatternSorter extends ViewerSorter
{
	public int compare(Viewer v, Object a, Object b)
	{
		String textA = "";
		if (a instanceof MeemNode) {
			MeemNode nodeA = (MeemNode) a;
			Abstract abstractA = (Abstract) nodeA.getProxy().getVariableMapProxy().get(InterMajik.ABSTRACT_KEY);
			textA = abstractA == null ? "" : abstractA.getName();
			if (textA.length() < 1) textA = nodeA.getText();
		} else 
		if (a instanceof Node) {
			textA = ((Node)a).getText();
		}
		
		String textB = "";
		if (b instanceof MeemNode) {
			MeemNode nodeB = (MeemNode) b;
			Abstract abstractB = (Abstract) nodeB.getProxy().getVariableMapProxy().get(InterMajik.ABSTRACT_KEY);
			textB = abstractB == null ? "" : abstractB.getName();
			if (textB.length() < 1) textB = nodeB.getText();
		} else 
		if (b instanceof Node) {
			textB = ((Node)b).getText();
		}
		
		return textA.compareTo(textB);
	}
}
