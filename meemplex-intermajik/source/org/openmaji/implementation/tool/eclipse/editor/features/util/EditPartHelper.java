/*
 * @(#)EditPartHelper.java
 * Created on 30/04/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.features.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.EditDomain;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.ui.IEditorPart;

/**
 * <code>EditPartHelper</code> consists of a collection of helper methods to
 * be used in editpart implementation or access editpart.
 * <p>
 * Implementation of one of these methods were done by reverse engineering of 
 * the GEF source code and may not be compatible with future version of GEF.
 * <p>
 * @author Kin Wong
 */
public class EditPartHelper {
	/**
	 * Gets the editor part as <code>IEditorPart</code> for the given editpart.
	 * @param editPart The editpart exists in the editor.
	 * @return IEditorPart The <code>IEditorPart</code> of the editor, or null 
	 * if unable to retreive it.
	 */
	static public IEditorPart getEditorPart(EditPart editPart) {
		if(editPart == null) return null;
		if(editPart.getRoot() == null) return null;
		EditDomain editDomain = editPart.getRoot().getViewer().getEditDomain();
		if(!(editDomain instanceof DefaultEditDomain)) return null;
		DefaultEditDomain defaultEditDomain = (DefaultEditDomain)editDomain;
		return defaultEditDomain.getEditorPart();
	}
	
	/**
	 * Gets the action registry from the editor of the given editpart.
	 * @param editPart The editpart exists in the editor.
	 * @return ActionRegistry The action registry of the editor, or null if
	 * unable to retreive it.
	 */
	static public ActionRegistry getActionRegistry(EditPart editPart) {
		IEditorPart editorPart = getEditorPart(editPart);
		if(editorPart == null) return null;
		return (ActionRegistry)editorPart.getAdapter(ActionRegistry.class);
	}
	
	/**
	 * Finds a class or interface implemented or exposed with 
	 * <code>getAdaptor()</code> by the editpart or any of its ancestor, start
	 * from the youngest. 
	 * @param editPart The editpart start to find.
	 * @param type The class to match.
	 * @return Object The object of that matches the type, or null if not found.
	 */
	static public Object findAncestor(EditPart editPart, Class type) {
		if(editPart == null) return null;
		if(type.isInstance(editPart)) return editPart;
		Object match = editPart.getAdapter(type);
		if(match != null) return match;
		return findAncestor(editPart.getParent(), type);
	}
	
	/**
	 * Give a list of editParts, constructs a list of editparts that understand
	 * a request by recursively traversing down the editpart hierarchy.
	 * <p>
	 * @param editParts 
	 * @param request
	 */

	static public List 
		filterEditPartsUnderstandingRecursive(List editParts, Object request) {
		ArrayList results = new ArrayList();
		filterEditPartsUnderstandingRecursive(editParts, request, results);
		return results;
	}
	
	static private void 
		filterEditPartsUnderstandingRecursive(
			List editParts, Object request, ArrayList results) {
				
		for (Iterator iter = editParts.iterator(); iter.hasNext();) {
			Object element = iter.next();
			if(!(element instanceof EditPart)) continue;

			EditPart editPart = (EditPart)element;
			filterEditPartsUnderstandingRecursive(
				editPart.getChildren(), request, results);
		//	if(editPart.understandsRequest(request)) results.add(editPart);
		}
	}

	/**
	 * Forces a repaint of the specified EditPart. Not very efficient, but
	 * needed to fix painting on linux.
	 * @param editPart The editpart to repaint
	 */
	static public void forceRepaint(EditPart editPart) {
//  if(PlatformHelper.runningOnLinux())
//    ((GraphicalEditPart)editPart.getRoot().getContents()).getContentPane().getUpdateManager().performUpdate();
	}
}
