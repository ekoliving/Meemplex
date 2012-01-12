/*
 * @(#)SpaceBrowserView.java
 * Created on 28/01/2004
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.browser.relationship.space;

import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.internal.WorkbenchWindow;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.PropertySheetPage;
import org.openmaji.implementation.tool.eclipse.browser.relationship.space.controllers.SpaceControllerFactory;
import org.openmaji.implementation.tool.eclipse.browser.relationship.space.dnd.SpaceBrowserDragSourceListener;
import org.openmaji.implementation.tool.eclipse.browser.relationship.space.dnd.SpaceDropAdapter;
import org.openmaji.implementation.tool.eclipse.client.InterMajikClientProxyFactory;
import org.openmaji.implementation.tool.eclipse.client.MeemClientProxy;
import org.openmaji.implementation.tool.eclipse.client.security.SecurityManager;
import org.openmaji.implementation.tool.eclipse.hierarchy.NodeSorter;
import org.openmaji.implementation.tool.eclipse.hierarchy.controllers.Controller;
import org.openmaji.implementation.tool.eclipse.hierarchy.controllers.ControllerFactory;
import org.openmaji.implementation.tool.eclipse.hierarchy.nodes.CategoryNode;
import org.openmaji.implementation.tool.eclipse.hierarchy.nodes.LabelNode;
import org.openmaji.implementation.tool.eclipse.hierarchy.nodes.MeemPathConstructor;
import org.openmaji.implementation.tool.eclipse.hierarchy.nodes.Node;
import org.openmaji.implementation.tool.eclipse.hierarchy.nodes.NodeTreeContentProvider;
import org.openmaji.implementation.tool.eclipse.plugin.MajiPlugin;
import org.openmaji.implementation.tool.eclipse.ui.dnd.NamedMeemTransfer;
import org.openmaji.implementation.tool.eclipse.ui.view.SecurityView;


/**
 * <code>SpaceBrowserView</code>.
 * <p>
 * @author Kin Wong
 */
public class SpaceBrowserView extends SecurityView implements ISelectionProvider {
	static private String COPY_ENABLED = "org.openmaji.intermajik.spacebrowser.copy_enabled";
	static private String MOVE_ENABLED = "org.openmaji.intermajik.spacebrowser.move_enabled";
	
	private IMemento memento;
	private ControllerFactory factory;
	private Combo combo;
	private TreeViewer viewer;
	private Controller controller;
	
	public SpaceBrowserView() {
		factory = new SpaceControllerFactory();
		
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.ui.view.SecurityView#dispose()
	 */
	public void dispose() {
		//if(controller != null) deactivateController();
		super.dispose();
	}

	/**
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class adapter) {
		if(adapter.equals(IPropertySheetPage.class)) {
			PropertySheetPage page = new PropertySheetPage();
			return page;
		}
		else
		return super.getAdapter(adapter);
	}
	
	
	/**
	 * @see org.openmaji.implementation.tool.eclipse.ui.view.SecurityView#createLoginView(org.eclipse.swt.widgets.Composite)
	 */
	protected void createLoginView(Composite parent) {
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		parent.setLayout(layout);

		Label label = new Label(parent, SWT.HORIZONTAL | SWT.LEFT);
		label.setText("Location");
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));

		combo = new Combo(parent, SWT.DROP_DOWN);
		combo.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));

		combo.setText(MeemPathConstructor.getPath(null));

		combo.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
			}
			public void keyReleased(KeyEvent e) {
			}
		});
		combo.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			public void widgetSelected(SelectionEvent e) {
			}
		});

		IDialogSettings settings = MajiPlugin.getDefault().getDialogSettings();
		String[] comboItems = settings.getArray("ComboItems");

		if (comboItems != null)
			combo.setItems(comboItems);

		viewer = new TreeViewer(parent, SWT.SINGLE | SWT.BORDER);
		viewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));

		MeemClientProxy hyperspaceRoot = InterMajikClientProxyFactory.getInstance().locateHyperSpace();

		String rootName = "<HyperSpace> ";
		rootName += "(" + SecurityManager.getInstance().getUser().getName() + ")";
		Node rootNode = new LabelNode();
		//rootNode.activate(viewer);
		viewer.setContentProvider(new NodeTreeContentProvider());
		viewer.setLabelProvider(new SpaceBrowserLabelProvider());
		viewer.setSorter(new NodeSorter());
		viewer.setInput(rootNode);
		rootNode.addChild(rootName, new CategoryNode(rootName, hyperspaceRoot));
		
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
					handleSelectionChanged();
			}
		});
		
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				handleDoubleClick(event);
			}
		});

		//createContextMenu(viewer);
		initialiseDragAndDrop();

		if (memento != null)
		{
			//restoreState(memento);
			memento = null;
		}

	}
	/**
	 * @see org.openmaji.implementation.tool.eclipse.ui.view.SecurityView#clearLoginView(org.eclipse.swt.widgets.Composite)
	 */
	protected void clearLoginView(Composite parent) {
		super.clearLoginView(parent);
	}

	/**
	 * Initialises Drag and Drop support.
	 */
	private void initialiseDragAndDrop() {
		int ops = 0;

		String copyEnabledString = System.getProperty(COPY_ENABLED);
		String moveEnabledString = System.getProperty(MOVE_ENABLED);

		if(	(copyEnabledString != null)  && 
				(copyEnabledString.equalsIgnoreCase("true"))) ops |= DND.DROP_COPY;
		
		if(	(moveEnabledString != null)  && 
				(moveEnabledString.equalsIgnoreCase("true"))) ops |= DND.DROP_MOVE;
		
		Transfer[] dragTransfers = new Transfer[] { NamedMeemTransfer.getInstance()};
		Transfer[] dropTransfers = new Transfer[] { NamedMeemTransfer.getInstance()};
		
		if(ops != 0) {
			viewer.addDragSupport(
				ops, 
				dragTransfers, 
				new SpaceBrowserDragSourceListener(viewer, ops == DND.DROP_COPY, NamedMeemTransfer.getInstance()));
			viewer.addDropSupport(
				ops, 
				dropTransfers, 
				new SpaceDropAdapter(viewer, ops == DND.DROP_COPY));
		}
	}
	
	private void handleSelectionChanged() {
		IStructuredSelection selection = (IStructuredSelection)viewer.getSelection();
		Object selected = selection.getFirstElement();
		if(controller != null) deactivateController();
		activateController(selected);
		String path = MeemPathConstructor.getPath((Node) selected);
		combo.setText(path);
	}		

	private void handleDoubleClick(DoubleClickEvent e) {
		if(controller == null) return;
		controller.handleDoubleClick(e);
	}
	
	private void activateController(Object object) {
		if(object == null) return;
		
		if(!(object instanceof Node)) return;
		Node node = (Node)object;
		controller = factory.create(node);
		if(controller == null) return;
		controller.activate(viewer, this, getSite());
		
		MenuManager menuManager = new MenuManager();
		menuManager.setRemoveAllWhenShown(true);
		
		menuManager.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				if(controller != null) {
					controller.updateActions();
					controller.fillMenu(manager);
				}
			}
		});
		
		IMenuManager menuMgr = ((WorkbenchWindow)MajiPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow()).getMenuBarManager();
		menuMgr = menuMgr.findMenuUsingPath("InterMajik");
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				removeMergedMenuItems();
				if(controller != null) {
					controller.updateActions();
					controller.fillMainMenu(manager);
				}
			}
		});
		
		//controller.fillMenu(menuManager);
		Menu menu = menuManager.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
	}
	
	protected void deactivateController() {
		controller.deactivate();
		controller = null;
		viewer.getControl().setMenu(null);
		removeMergedMenuItems();
	}
	
	protected void removeMergedMenuItems() {
		IMenuManager menuMgr = ((WorkbenchWindow)MajiPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow()).getMenuBarManager();
		menuMgr = menuMgr.findMenuUsingPath("InterMajik");

		boolean startRemoving = false;
		IContributionItem[] items = menuMgr.getItems();
		for (int i = 0; i < items.length; i++) {
			IContributionItem item = items[i];
			//if(item.getId() == null) continue;
			if(item instanceof GroupMarker) {
				GroupMarker marker = (GroupMarker)item;
				if(marker.getGroupName().equals(MajiPlugin.GROUP_START)) {
					startRemoving = true;
				}
				else 
				if(marker.getGroupName().equals(MajiPlugin.GROUP_END)) {
					break;
				}
			}
			else
			if(startRemoving) {
				menuMgr.remove(item);
			}
		}
	}
	/**
	 * @see org.eclipse.ui.ISelectionListener#selectionChanged(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
	}

	/**
	 * @see org.eclipse.jface.viewers.ISelectionProvider#addSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
	 */
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		System.out.println("addSelectionChangedListener");
	}

	/**
	 * @see org.eclipse.jface.viewers.ISelectionProvider#getSelection()
	 */
	public ISelection getSelection() {
		if(viewer != null) 
		return viewer.getSelection();
		return null;
	}

	/**
	 * @see org.eclipse.jface.viewers.ISelectionProvider#removeSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
	 */
	public void removeSelectionChangedListener(ISelectionChangedListener listener) {
		System.out.println("removeSelectionChangedListener");
	}

	/**
	 * @see org.eclipse.jface.viewers.ISelectionProvider#setSelection(org.eclipse.jface.viewers.ISelection)
	 */
	public void setSelection(ISelection selection) {
	}
	
}
