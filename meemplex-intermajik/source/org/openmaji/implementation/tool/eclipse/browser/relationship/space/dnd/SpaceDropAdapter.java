/*
 * @(#)SpaceDropAdapter.java
 * Created on 29/01/2004
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.browser.relationship.space.dnd;


import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TransferData;
import org.openmaji.implementation.tool.eclipse.browser.common.ViewerDropAdapter;
import org.openmaji.implementation.tool.eclipse.client.CategoryEntryNameFactory;
import org.openmaji.implementation.tool.eclipse.client.CategoryProxy;
import org.openmaji.implementation.tool.eclipse.client.security.SecurityManager;
import org.openmaji.implementation.tool.eclipse.hierarchy.nodes.CategoryNode;
import org.openmaji.implementation.tool.eclipse.ui.dnd.NamedMeem;
import org.openmaji.implementation.tool.eclipse.ui.dnd.NamedMeemTransfer;
import org.openmaji.meem.MeemPath;


/**
 * <code>SpaceDropAdapter</code>.
 * <p>
 * @author Kin Wong
 */
public class SpaceDropAdapter extends ViewerDropAdapter {
	private boolean defaultToCopy;
	/**
	 * Constructs an instance of <code>SpaceDropAdapter</code>.
	 * <p>
	 * @param viewer
	 */
	public SpaceDropAdapter(Viewer viewer, boolean defaultToCopy) {
		super(viewer);
		this.defaultToCopy = defaultToCopy;
	}

	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.browser.common.ViewerDropAdapter#validateDrop(java.lang.Object, int, org.eclipse.swt.dnd.TransferData, java.lang.Object)
	 */
	public boolean validateDrop(Object target, int operation, TransferData transferType, Object data) {
		if(target == null) return false;
		if(!(target instanceof CategoryNode)) return false;
		CategoryNode categoryNode = (CategoryNode)target;
		if(categoryNode.getCategory().isReadOnly()) return false;
		
		if(NamedMeemTransfer.getInstance().isSupportedType(transferType))
		return isValid(categoryNode, (NamedMeem[])data);
		
		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.browser.common.ViewerDropAdapter#dragOperationChanged(org.eclipse.swt.dnd.DropTargetEvent)
	 */
	public void dragOperationChanged(DropTargetEvent event) {
		super.dragOperationChanged(event);
		if(defaultToCopy)
		event.detail = DND.DROP_COPY;
	}

	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.browser.common.ViewerDropAdapter#performDrop(org.eclipse.swt.dnd.TransferData, java.lang.Object)
	 */
	public boolean performDrop(TransferData transferType, Object data) {
		if(!validateDrop(getCurrentTarget(), getCurrentOperation(), transferType, data))
		return false;
		
		if(data instanceof NamedMeem[]) {
			NamedMeem[] namedMeems = (NamedMeem[]) data;
			dropNamedMeems(((CategoryNode)getCurrentTarget()).getCategory(), namedMeems);
			return true;			
		}
		return false;
	}
	
	private void dropNamedMeems(CategoryProxy category, NamedMeem[] namedMeems) {
		for (int i = 0; i < namedMeems.length; ++i) {
			NamedMeem namedMeem = namedMeems[i];
			String entryName = CategoryEntryNameFactory.createUniqueEntryName(category, namedMeem.getName());
			category.addEntry(entryName, SecurityManager.getInstance().getGateway().getMeem(namedMeem.getMeemPath()));
		}
	}

	private boolean isValid(CategoryNode category, NamedMeem[] namedMeems) {
		if(namedMeems == null) return true;	// Assume it to be true
		MeemPath targetPath = category.getMeemPath();

		for (int i = 0; i < namedMeems.length; i++) {
			NamedMeem namedMeem = namedMeems[i];
			if(targetPath.equals(namedMeem.getMeemPath())) {
				//System.out.println("TargetPath: " + targetPath.toString() + ", dragPath: " + namedMeem.meemPath);
				return false; 
			} 
		}
		return true;
	}
}
