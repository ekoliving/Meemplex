/*
 * @(#)MeemStoreBrowserView.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 */
package org.openmaji.implementation.tool.eclipse.browser.meemstore;

import java.util.Iterator;


import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.*;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.internal.dialogs.DialogUtil;
import org.openmaji.implementation.tool.eclipse.client.security.SecurityManager;
import org.openmaji.implementation.tool.eclipse.icon.Icon;
import org.openmaji.implementation.tool.eclipse.ui.dnd.MeemPathTransfer;
import org.openmaji.implementation.tool.eclipse.ui.view.SecurityView;
import org.openmaji.implementation.tool.eclipse.util.MeemEditorInput;
import org.openmaji.meem.Meem;
import org.openmaji.meem.MeemPath;
import org.openmaji.meem.definition.MeemDefinition;
import org.openmaji.meem.definition.Scope;
import org.openmaji.meem.space.Space;
import org.openmaji.system.meemserver.MeemServer;
import org.openmaji.system.space.meemstore.MeemStore;


/**
 * This is the eclipse view that will show all the meems in MeemStore <br>
 * @author mg
 * Created on 20/01/2003
 */
public class MeemStoreBrowserView extends SecurityView implements IDoubleClickListener {
	ListViewer viewer = null;
	Action addMeemAction, deleteMeemAction;
	
	MeemStore meemStore = null;

	protected void createLoginView(Composite parent) {
		viewer = new ListViewer(parent);
		viewer.setContentProvider(new MeemStoreContentProvider());
		viewer.setLabelProvider(new MeemStoreLabelProvider());

		viewer.setInput(SecurityManager.getInstance().getGateway().getMeem(MeemPath.spi.create(Space.HYPERSPACE, "/deployment/meemServer_01/essential/meemStore")));

		viewer.addDoubleClickListener(this);

		createContextMenu();
		
		initDragAndDrop(viewer);
	}

	protected void clearLoginView(Composite parent) {
		viewer = null;

		super.clearLoginView(parent);
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPart#setFocus()
	 */
	public void setFocus() {
		if (viewer != null) {
			viewer.getControl().setFocus();
		}
		else {
			super.setFocus();
		}
	}

	protected void handleLogin() {
		super.handleLogin();
		addMeemAction.setEnabled(true);
		deleteMeemAction.setEnabled(true);
	}

	protected void handleLogout() {
		addMeemAction.setEnabled(false);
		deleteMeemAction.setEnabled(false);
		super.handleLogout();
	}

	protected void createActions() {

		super.createActions();

		addMeemAction = new Action("Add new Meem") {
			public void run() {
				addMeem();
			}
		};
		addMeemAction.setImageDescriptor(ImageDescriptor.createFromFile(Icon.class, "plus.gif"));
		addMeemAction.setToolTipText("Add new Meem");

		deleteMeemAction = new Action("Delete Meems") {
			public void run() {
				deleteMeem();
			}
		};
		deleteMeemAction.setImageDescriptor(ImageDescriptor.createFromFile(Icon.class, "delete.gif"));
		deleteMeemAction.setToolTipText("Delete Meem");

	}

	protected void fillActionBars(IToolBarManager toolBar) {
		toolBar.add(addMeemAction);
		toolBar.add(deleteMeemAction);
	}

	private void addMeem() {
		String id = promptForValue("Enter id:", null);
		if (id != null) {
			MeemPath meemPath = MeemPath.spi.create(Space.MEEMSTORE, id);
			MeemDefinition meemDefinition = new MeemDefinition();
			meemDefinition.getMeemAttribute().setVersion(1);
			meemDefinition.getMeemAttribute().setScope(Scope.LOCAL);

			try {
				getMeemStore().storeMeemDefinition(meemPath, meemDefinition);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void deleteMeem() {
		if (viewer.getSelection() instanceof IStructuredSelection) {
			IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
			for (Iterator i = selection.iterator(); i.hasNext();) {
				MeemPath meemPath = (MeemPath) i.next();
				try {
					getMeemStore().destroyMeem(meemPath);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void createContextMenu() {
		MenuManager menuManager = new MenuManager();
		menuManager.setRemoveAllWhenShown(true);
		menuManager.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				fillContextMenu(manager);
			}
		});

		Menu menu = menuManager.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuManager, viewer);
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(addMeemAction);
		manager.add(deleteMeemAction);
		manager.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
	}
	
	private String promptForValue(String text, String oldValue) {
		InputDialog dlg = new InputDialog(getSite().getShell(), "List View", text, oldValue, null);
		if (dlg.open() != Window.OK)
			return null;
		return dlg.getValue();
	}

	/**
	 * @see org.eclipse.jface.viewers.IDoubleClickListener#doubleClick(org.eclipse.jface.viewers.DoubleClickEvent)
	 */
	public void doubleClick(DoubleClickEvent event) {
		if (event.getSource().equals(viewer)) {
			if (event.getSelection() instanceof IStructuredSelection) {
				MeemPath meemPath = (MeemPath) ((IStructuredSelection) event.getSelection()).getFirstElement();
				openEditor(meemPath);
			}
		}
	}

	private void openEditor(MeemPath meemPath) {
		IWorkbenchPage page = getViewSite().getWorkbenchWindow().getActivePage();
		try {
			page.openEditor(new MeemEditorInput(meemPath), "Meem Definition Editor");
		} catch (PartInitException e) {
			DialogUtil.openError(page.getWorkbenchWindow().getShell(), "editor not opened", e.getMessage(), e);
		}
	}

	private void initDragAndDrop(ListViewer viewer) {
		int ops = DND.DROP_COPY;
		Transfer[] dragTransfers = new Transfer[] { MeemPathTransfer.getInstance()};
		viewer.addDragSupport(ops, dragTransfers, new MeemStoreDragAdapter(viewer));
	}
	
	private MeemStore getMeemStore() {
		if (meemStore == null) {
			Meem meemStoreMeem = SecurityManager.getInstance().getGateway().getMeem(MeemPath.spi.create(Space.HYPERSPACE, MeemServer.spi.getEssentialMeemsCategoryLocation() + "/meemStore"));
			meemStore = (MeemStore) SecurityManager.getInstance().getGateway().getTarget(meemStoreMeem, "meemStore", MeemStore.class);
		}
		return meemStore;
	}

}
