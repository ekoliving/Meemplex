/*
 * @(#)MajiPerspective.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.tool.eclipse.plugin.perspective;

import org.eclipse.ui.*;
import org.openmaji.implementation.tool.eclipse.browser.relationship.space.SpaceBrowserView;


/**
 * <p>
 * ...
 * </p>
 * @author  mg
 * @version 1.0
 */
public class MajiPerspective implements IPerspectiveFactory {

	/**
	 * @see org.eclipse.ui.IPerspectiveFactory#createInitialLayout(org.eclipse.ui.IPageLayout)
	 */
	public void createInitialLayout(IPageLayout layout) {
		// Get the editor area.
		String editorArea = layout.getEditorArea();

		// Top left: space, meemstore ,lcm, registry
		IFolderLayout topLeft = layout.createFolder("topLeft", IPageLayout.LEFT, 0.25f, editorArea);
		
		topLeft.addView("org.openmaji.implementation.tool.eclipse.browser.relationship.space.SpaceBrowserView");
		topLeft.addView("org.openmaji.implementation.tool.eclipse.browser.relationship.deployment.DeploymentView");
//		topLeft.addView("org.openmaji.implementation.tool.eclipse.browser.meemstore.views.MeemStoreBrowserView");
//		topLeft.addView("org.openmaji.implementation.tool.eclipse.browser.lifecyclemanager.views.LifeCycleManagerView");
//		topLeft.addView("org.openmaji.implementation.tool.eclipse.browser.meemregistry.views.MeemRegistryView");

		// Bottom left: Toolkit
		IFolderLayout bottomLeft = layout.createFolder("bottomLeft", IPageLayout.BOTTOM, 0.50f, "topLeft");
		bottomLeft.addView("org.openmaji.implementation.tool.eclipse.browser.patterns.toolkit.MeemView");
		bottomLeft.addView("org.openmaji.implementation.tool.eclipse.browser.patterns.toolkit.WedgeView");

		// Bottom right: prop sheet, log, beanshell
		IFolderLayout bottomRight = layout.createFolder("bottomRight", IPageLayout.BOTTOM, 0.66f, editorArea);
		bottomRight.addView(IPageLayout.ID_PROP_SHEET);
		bottomRight.addView("org.openmaji.implementation.tool.eclipse.ui.view.log.MajiLogView");
		//bottomRight.addView("org.openmaji.implementation.tool.eclipse.views.beanshell.BeanshellView");

//		IWorkbenchPage page = MajiPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage();
//
//		page.hideActionSet("org.eclipse.ui.NavigateActionSet");	
//		page.hideActionSet("org.eclipse.ui.externaltools.ExternalToolsSet");
//		page.hideActionSet("org.eclipse.ui.edit.text.actionSet.navigation");
//		page.hideActionSet("org.eclipse.update.ui.softwareUpdates");
//		page.hideActionSet("org.eclipse.debug.ui.debugActionSet");
//		page.hideActionSet("org.eclipse.search.searchActionSet");
//		page.hideActionSet("org.eclipse.team.cvs.ui.CVSActionSet");

		

	}

}
