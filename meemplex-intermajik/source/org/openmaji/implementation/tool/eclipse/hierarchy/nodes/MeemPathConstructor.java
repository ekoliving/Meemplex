/*
 * @(#)MeemPathConstructor.java
 * Created on 24/02/2004
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.hierarchy.nodes;

/**
 * <code>MeemPathConstructor</code>.
 * <p>
 * @author Kin Wong
 */
public class MeemPathConstructor {
	static public String getPath(Node node) {
		StringBuffer path = new StringBuffer();

		if (node != null) {
			Node parent = node.getParent();

			if (parent != null) {
				while (parent.getParent() != null) {
					path.insert(0, "/" + node.getText());
					node = parent;  
					parent = node.getParent();
				}
			}
		}

		if (path.length() < 1) path.append("/");

		path.insert(0, "hyperspace:");

		return path.toString();
	}
}
