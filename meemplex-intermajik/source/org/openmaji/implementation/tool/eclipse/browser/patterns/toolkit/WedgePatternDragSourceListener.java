/*
 * @(#)WedgePatternDragSourceListener.java
 * Created on 2/03/2004
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.browser.patterns.toolkit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;


import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.Transfer;
import org.openmaji.implementation.common.VariableMapWedge;
import org.openmaji.implementation.server.nursery.pattern.MeemPatternWedge;
import org.openmaji.implementation.tool.eclipse.hierarchy.MeemNodeDragSourceListener;
import org.openmaji.implementation.tool.eclipse.hierarchy.nodes.MeemNode;
import org.openmaji.implementation.tool.eclipse.ui.dnd.WedgeTransfer;
import org.openmaji.meem.definition.FacetAttribute;
import org.openmaji.meem.definition.WedgeAttribute;
import org.openmaji.system.meem.definition.MeemStructure;


/**
 * <code>WedgePatternDragSourceListener</code>.
 * <p>
 * @author Kin Wong
 */
public class WedgePatternDragSourceListener
	extends MeemNodeDragSourceListener {

	/**
	 * Constructs an instance of <code>WedgePatternDragSourceListener</code>.
	 * <p>
	 * @param selectionProvider
	 * @param transfer
	 */
	public WedgePatternDragSourceListener(ISelectionProvider selectionProvider, Transfer transfer) {
		super(selectionProvider, transfer);
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.hierarchy.MeemNodeDragSourceListener#dragSetData(org.eclipse.swt.dnd.DragSourceEvent)
	 */
	public void dragSetData(DragSourceEvent event) {
		ArrayList<WedgeTransfer> wedgeTransfers = new ArrayList<WedgeTransfer>();
		Iterator itNode = getSelection().iterator();
		while (itNode.hasNext()) {
			MeemNode node = (MeemNode)itNode.next();

			MeemStructure meemStructure = node.getProxy().getMetaMeem().getStructure();
			synchronized (meemStructure) {
				Iterator<Serializable> wedgeKeyIterator = meemStructure.getWedgeAttributeKeys().iterator();
	
				while (wedgeKeyIterator.hasNext()) {
					Serializable wedgeKey = wedgeKeyIterator.next();
					WedgeAttribute wedgeAttribute = meemStructure.getWedgeAttribute(wedgeKey);
	
					if (wedgeAttribute.isSystemWedge()) continue;
					if (wedgeAttribute.getImplementationClassName().equals(VariableMapWedge.class.getName())) continue;
					if (wedgeAttribute.getImplementationClassName().equals(MeemPatternWedge.class.getName())) continue;
	
					WedgeTransfer wedgeTransfer = new WedgeTransfer();
					wedgeTransfer.setWedgeAttribute((WedgeAttribute) wedgeAttribute.clone());
	
					Iterator<String> facetKeyIterator = meemStructure.getFacetAttributeKeys(wedgeKey).iterator();
					while (facetKeyIterator.hasNext()) {
						String facetKey = facetKeyIterator.next();
						FacetAttribute facetAttribute = meemStructure.getFacetAttribute(facetKey);
						wedgeTransfer.addFacetAttribute((FacetAttribute)facetAttribute.clone());
					}
					wedgeTransfers.add(wedgeTransfer);
				}
			}
		}
		event.data = (WedgeTransfer[]) 
			wedgeTransfers.toArray(new WedgeTransfer[0]);
	}
}
