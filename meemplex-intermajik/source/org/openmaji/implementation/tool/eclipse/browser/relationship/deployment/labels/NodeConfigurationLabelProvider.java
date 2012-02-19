/*
 * @(#)NodeConfigurationLabelProvider.java
 * Created on 5/05/2004
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.browser.relationship.deployment.labels;

import org.eclipse.swt.graphics.Image;
import org.openmaji.implementation.tool.eclipse.browser.relationship.deployment.nodes.SubsystemFactoryNode;
import org.openmaji.implementation.tool.eclipse.browser.relationship.deployment.nodes.WorksheetManagerNode;
import org.openmaji.implementation.tool.eclipse.client.ConfigurationHandlerProxy;
import org.openmaji.implementation.tool.eclipse.hierarchy.MeemNodeLabelProvider;
import org.openmaji.implementation.tool.eclipse.hierarchy.nodes.MeemNode;
import org.openmaji.implementation.tool.eclipse.images.Images;


/**
 * <code>NodeConfigurationLabelProvider</code>.
 * <p>
 * @author Kin Wong
 */
public class NodeConfigurationLabelProvider extends MeemNodeLabelProvider {
	/**
	 * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
	 */
	public String getText(Object element) {
		if(element instanceof MeemNode) {
			MeemNode meemNode = (MeemNode)element;
			String text = meemNode.getText();
			if(text.length() > 0) return text;
			return getMeemText(meemNode);
		}
		return super.getText(element);
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.hierarchy.NodeLabelProvider#getImage(java.lang.Object)
	 */
	public Image getImage(Object element) {
		if(element instanceof WorksheetManagerNode) {
			return Images.getIcon("worksheets16.gif");
		}
		else
			if(element instanceof SubsystemFactoryNode) {
				return Images.getIcon("deployment16.gif");
			}
			else
		return super.getImage(element);
	}

	protected String getMeemText(MeemNode meem) {
		ConfigurationHandlerProxy config = meem.getProxy().getConfigurationHandler();
		Object value = config.getValue(ConfigurationHandlerProxy.ID_MEEM_IDENTIFIER);
		if(value == null) return meem.getText();
		return value.toString();
	}
}
