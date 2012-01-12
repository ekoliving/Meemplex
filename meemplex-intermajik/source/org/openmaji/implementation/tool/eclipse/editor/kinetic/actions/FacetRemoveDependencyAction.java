/*
 * @(#)FacetRemoveDependencyAction.java
 * Created on 17/06/2004
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic.actions;

import org.eclipse.gef.Request;
import org.eclipse.ui.IWorkbenchPart;
import org.openmaji.implementation.tool.eclipse.editor.common.actions.RequestAction;


/**
 * <code>FacetRemoveDependencyAction</code>.<p>
 * @author Kin Wong
 */
public class FacetRemoveDependencyAction extends RequestAction {
	static public String ID_REMOVE_DEPENDENCY = 
		FacetRemoveDependencyAction.class.getName();
	static public Object REQ_REMOVE_DEPENDENCY = ID_REMOVE_DEPENDENCY + ".request";

	static private Request REQUEST = new Request(REQ_REMOVE_DEPENDENCY);

	public FacetRemoveDependencyAction(IWorkbenchPart workbenchPart) {
		super(workbenchPart);
		setId(ID_REMOVE_DEPENDENCY);
		setText("Remove Dependency");
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.common.actions.RequestAction#getRequest()
	 */
	protected Request getRequest() {
		return REQUEST;
	}
}
