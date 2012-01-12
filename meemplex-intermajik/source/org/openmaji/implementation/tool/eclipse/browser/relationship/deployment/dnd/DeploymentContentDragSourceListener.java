/*
 * @(#)DeploymentContentDragSourceListener.java
 * Created on 19/05/2004
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.browser.relationship.deployment.dnd;

import java.util.List;

import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.Transfer;
import org.openmaji.implementation.tool.eclipse.client.ConfigurationHandlerProxy;
import org.openmaji.implementation.tool.eclipse.hierarchy.MeemNodeDragSourceListener;
import org.openmaji.implementation.tool.eclipse.hierarchy.nodes.MeemNode;
import org.openmaji.implementation.tool.eclipse.ui.dnd.NamedMeem;


/**
 * <code>DeploymentContentDragSourceListener</code>.
 * <p>
 * @author Kin Wong
 */
public class DeploymentContentDragSourceListener extends MeemNodeDragSourceListener {

	/**
	 * Constructs an instance of <code>DeploymentContentListener</code>.
	 * <p>
	 * @param selectionProvider
	 * @param transfer
	 */
	public DeploymentContentDragSourceListener(
		ISelectionProvider selectionProvider,
		Transfer transfer) {
		super(selectionProvider, transfer);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.dnd.DragSourceListener#dragSetData(org.eclipse.swt.dnd.DragSourceEvent)
	 */
	public void dragSetData(DragSourceEvent event) {
		if(!getTransfer().isSupportedType(event.dataType)) return;
		List meemNodes = getMeemNodes();
		NamedMeem[] namedMeems = new NamedMeem[meemNodes.size()];
		for(int i = 0; i < namedMeems.length; i++) {
			MeemNode meemNode = (MeemNode)meemNodes.get(i);
			ConfigurationHandlerProxy config = 
				meemNode.getProxy().getConfigurationHandler();
			
			String identifier = meemNode.getText();
			if(identifier.length() == 0) {
				Object identifierValue = 
					config.getValue(ConfigurationHandlerProxy.ID_MEEM_IDENTIFIER);
				if(identifierValue instanceof String) {
					identifier = (String)identifierValue;
				}
			}
			namedMeems[i] = new NamedMeem(identifier, meemNode.getMeemPath());
		}
		event.data = namedMeems;
	}
}
