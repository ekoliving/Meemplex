/*
 * @(#)SpaceBrowserLabelProvider.java
 * Created on 25/05/2004
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.browser.relationship.space;

import org.openmaji.implementation.tool.eclipse.client.ConfigurationHandlerProxy;
import org.openmaji.implementation.tool.eclipse.hierarchy.MeemNodeLabelProvider;
import org.openmaji.implementation.tool.eclipse.hierarchy.nodes.MeemNode;

/**
 * <code>SpaceBrowserLabelProvider</code>.
 * <p>
 * @author Kin Wong
 */
public class SpaceBrowserLabelProvider extends MeemNodeLabelProvider {
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.hierarchy.NodeLabelProvider#getText(java.lang.Object)
	 */
	public String getText(Object element) {
		if(!(element instanceof MeemNode)) return super.getText(element);
		MeemNode meemNode = (MeemNode)element;
		
		String label = meemNode.getText();
		if(label == null) label = "";
		if(label.startsWith("<HyperSpace> ")) return label;
		
		ConfigurationHandlerProxy config = 
			meemNode.getProxy().getConfigurationHandler();
		String meemIdentifier = (String)config.
			getValue(ConfigurationHandlerProxy.ID_MEEM_IDENTIFIER);

		if((meemIdentifier != null) && (!label.equals(meemIdentifier))) {
			meemIdentifier = " (" + meemIdentifier + ")";
		}
		else {
			meemIdentifier = "";
		}
		return label + meemIdentifier;
	}
}
