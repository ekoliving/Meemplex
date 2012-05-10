/*
 * @(#)ViewModeAction.java
 * Created on 10/04/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic.actions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.tools.ToolUtilities;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IWorkbenchPart;
import org.openmaji.implementation.intermajik.model.ViewModeConstants;
import org.openmaji.implementation.tool.eclipse.editor.common.actions.SelectionAction;
import org.openmaji.implementation.tool.eclipse.editor.features.multiview.IViewModeProvider;
import org.openmaji.implementation.tool.eclipse.editor.features.multiview.ViewModeRequest;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.Messages;
import org.openmaji.implementation.tool.eclipse.images.Images;


public class ViewModeAction extends SelectionAction implements ViewModeConstants {
	static public String ID_VIEW_MODE = ViewModeAction.class + ".ViewMode";

	static private final ViewModeRequest requestDetailed = new ViewModeRequest(VIEW_MODE_DETAILED);
	static private final ViewModeRequest requestIconic = new ViewModeRequest(VIEW_MODE_ICONIC);
	static private final ViewModeRequest requestDevice 	= new ViewModeRequest(VIEW_MODE_DEVICE);
	static private final ViewModeRequest viewModeRequests[] = {
		requestDetailed, requestIconic, requestDevice
	};
	
	private MenuManager menuManager;
	private ViewModeRequest lastRequest = requestIconic;
	private ViewModeRequest currentRequest = requestIconic;

	private List currentViewModeRequests = new ArrayList();
	private Set viewModeChecked = new HashSet();
	
	private IMenuCreator viewModeMenuCreator = new IMenuCreator() {
		public void dispose() {}
		public Menu getMenu(Control parent) {
			return menuManager.createContextMenu(parent);
		}

		public Menu getMenu(Menu parent) {
			Menu menu = new Menu(parent);
			menuManager.fill(menu, -1);
			return menu;
		}
	};

	class ViewAction extends Action {
		private ViewModeRequest request;
		public ViewAction(ViewModeRequest request) {
			super("", IAction.AS_RADIO_BUTTON);
			setChecked(false);
			this.request = request;
			updateActionDetails(this, request);
		}
		
		/* (non-Javadoc)
		 * @see org.eclipse.jface.action.Action#isEnabled()
		 */
		public boolean isEnabled() {
			return true;
		}
		
		public void run() {
			currentRequest = request;
			lastRequest = request;
			ViewModeAction.this.run();
		}
	}
	
	/**
	 * Constructs an instance of <code>ViewModeAction</code>.
	 * @param part The workbench part.
	 */
	public ViewModeAction(IWorkbenchPart part) {
		super(part);

		setMenuCreator(viewModeMenuCreator);
		initializeAction();
		update();
	}

	/**
	 * Initializes this ViewModeAction.
	 */
	protected void initializeAction() {
		setId(ID_VIEW_MODE);
		update();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.Action#getMenuCreator()
	 */
	public IMenuCreator getMenuCreator() {
		return viewModeMenuCreator;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.ui.actions.EditorPartAction#calculateEnabled()
	 */
	protected boolean calculateEnabled() {
		List editparts = getSelectedObjectClone();
		ToolUtilities.filterEditPartsUnderstanding(editparts, lastRequest);
		return !editparts.isEmpty();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.ui.actions.SelectionAction#update()
	 */
	public void update() {
		super.update();
		updateMenu();
		updateActionsDetails();
		setText("");
	}
	
	private void updateActionsDetails() {
		boolean applyLastViewMode = true;
		if(viewModeChecked.size() == 1) {
			// All selected objects currently use same view mode
			applyLastViewMode = false;
			Iterator it = viewModeChecked.iterator();
			currentRequest = getNextRequest(it.next());
		}
		else {
			currentRequest = lastRequest;
		}
		updateActionDetails(this, currentRequest);
		if(applyLastViewMode) {
			setToolTipText("Apply Last: " + getToolTipText());
		}
	}
	
	ViewModeRequest getNextRequest(Object viewMode) {
		if(viewMode.equals(VIEW_MODE_ICONIC)) {
			return requestDetailed;
		}
		else {
			return requestIconic;
		}
	}
	
	static private void updateActionDetails(IAction action, Object request) {
		if(request.equals(requestDevice)) {
			// Device View
			action.setText(Messages.ViewModeDevice_Label);
			action.setToolTipText(Messages.ViewModeDevice_Tooltip);
			action.setImageDescriptor(Images.ICON_DEVICE_VIEW);
			action.setHoverImageDescriptor(Images.ICON_DEVICE_VIEW);
		}
		else
		if(request.equals(requestIconic)) {
			// Iconic View
			action.setText(Messages.ViewModeIconic_Label);
			action.setToolTipText(Messages.ViewModeIconic_Tooltip);
			action.setImageDescriptor(Images.ICON_ICONIC_VIEW);
			action.setHoverImageDescriptor(Images.ICON_ICONIC_VIEW);
		}
		else {
			// Detailed View
			action.setText(Messages.ViewModeDetailed_Label);
			action.setToolTipText(Messages.ViewModeDetailed_Tooltip);
			action.setImageDescriptor(Images.ICON_DETAILED_VIEW);
			action.setHoverImageDescriptor(Images.ICON_DETAILED_VIEW);
		}
	}
	
	private void updateMenu() {
		menuManager = new MenuManager("Views");
		currentViewModeRequests.clear();
		viewModeChecked = getCheckedViewModes(getSelectedObjects());
		
		for(int i = 0; i < viewModeRequests.length; i++) {
			ViewModeRequest request = viewModeRequests[i];
			
			List editParts = getSelectedObjectClone();
			ToolUtilities.filterEditPartsUnderstanding(editParts, request);
			if(!editParts.isEmpty()) {
				Action action = new ViewAction(request);
				action.setChecked(viewModeChecked.contains(request.getViewMode()));
				menuManager.add(action);
				currentViewModeRequests.add(request);
			}
		}
	}
	
	public void appendToGroup(MenuManager menu, String groupName)	 {
		IContributionItem[] items = menuManager.getItems();
		for (int i = 0; i < items.length; i++) {
			menu.appendToGroup(groupName, items[i]);
		}
	}
	
	protected Command getCommand(ViewModeRequest request) {
		//System.out.println("getCommand(" + request.getViewMode() + ")");
		
		CompoundCommand compoundCommand = new CompoundCommand();
		compoundCommand.setLabel(request.getViewMode().toString());
		Iterator it = getSelectedObjects().iterator();
		while(it.hasNext()) {
			EditPart part = (EditPart) it.next();
			compoundCommand.add(part.getCommand(request));
		}
		return compoundCommand.unwrap();
	}
	
	protected Set getCheckedViewModes(List editParts) {
		Set checked = new HashSet();
		for (Iterator iter = editParts.iterator(); iter.hasNext();) {
			Object selected = iter.next();
			if(!(selected instanceof EditPart)) continue;
			
			EditPart editPart = (EditPart)selected;
			if(editPart.getModel() != null) {
				if(editPart.getModel() instanceof IViewModeProvider) {
					IViewModeProvider provider = (IViewModeProvider)editPart.getModel();
					checked.add(provider.getViewMode());
				}
			}
		}
		return checked;
	}

	public void run() {
		execute(getCommand(currentRequest));
		update();
	}
	
}

