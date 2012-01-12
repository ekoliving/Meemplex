/*
 * @(#)ExplorerView.java
 * Created on 26/02/2004
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.browser.patterns.common;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.util.Assert;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.openmaji.implementation.tool.eclipse.hierarchy.controllers.Controller;
import org.openmaji.implementation.tool.eclipse.hierarchy.controllers.ControllerFactory;
import org.openmaji.implementation.tool.eclipse.hierarchy.nodes.Node;
import org.openmaji.implementation.tool.eclipse.hierarchy.nodes.NodeTreeContentProvider;
import org.openmaji.implementation.tool.eclipse.images.Images;
import org.openmaji.implementation.tool.eclipse.plugin.MajiPlugin;
import org.openmaji.implementation.tool.eclipse.ui.view.SecurityView;
import org.openmaji.implementation.tool.eclipse.ui.view.SelectionChangeAdaptor;



/**
 * <code>ExplorerView</code>.
 * <p>
 * @author Kin Wong
 */
abstract public class ExplorerView extends SecurityView  {
	private static ITableLabelProvider defaultContentLabelProvider = 
		new NullTablelLabelProvider();
		
	private static final String VERTICAL_VIEW_ORIENTATION = "Vertical View Orientation";
	private static final String HORIZONTAL_VIEW_ORIENTATION = "Horizontal View Orientation";
	private static final String CONTENT_VIEW_ONLY = "Content View Only";
	private static final String HIDE_INFO_PANE = "Hide Info Pane";
	
	public static final int VERTICAL_ORIENTATION_VIEW = 0;
	public static final int HORIZONTAL_ORIENTATION_VIEW = 1;
	public static final int CONTENT_ONLY_VIEW = 2;
	
	private String VIEW_ORIENTATION = getClass().getName() + "__ViewOrientation";
	private String INFO_PANE = getClass().getName() + "__InfoPane";
	
	private static final String VIEW_GROUP = "View Group";
	
	private Action verticalOrientationAction;
	private Action horizontalOrientationAction;
	private Action contentViewOnlyAction;
	private Action hideInfoPaneAction;
	
	//private int view = VERTICAL_ORIENTATION_VIEW;

	private SashForm outerSash;
	private SashForm sash;
	private TreeViewer treeViewer;	// The tree viewer that presents the hierarchy.
	private TableViewer contentViewer;// The table viewer that presents the content.
	private TextViewer infoViewer;	// The Text viewer that presents selection information.

	private IContentNodeFactory contentNodeFactory;
	private ControllerFactory controllerFactory;
	private MenuManager treeContextMenuManager;
	private MenuManager contentContextMenuManager;
	private List controllers;
	
	public ExplorerView() {
		treeContextMenuManager = new MenuManager();
		treeContextMenuManager.setRemoveAllWhenShown(true);
		treeContextMenuManager.addMenuListener(new IMenuListener() {
					public void menuAboutToShow(IMenuManager manager) {
						showContextMenu(manager, getTreeViewer());
					}
		});

		contentContextMenuManager = new MenuManager();
		contentContextMenuManager.setRemoveAllWhenShown(true);
		contentContextMenuManager.addMenuListener(new IMenuListener() {
					public void menuAboutToShow(IMenuManager manager) {
						showContextMenu(manager, getContentViewer());
					}
		});
	}
	
	/**
	 * Gets the hierarchy viewer from this pattern view.
	 * @return The hierarchy viewer from this pattern view
	 */
	public TreeViewer getTreeViewer() {
		return treeViewer;
	}
	
	/**
	 * Sets the content node factory.<p>
	 * Given a selected node in the hierarchy view, the factory will create a
	 * node used in the content view.
	 * @param factory
	 */
	public void setContentNodeFactory(IContentNodeFactory factory) {
		contentNodeFactory = factory;
	}
	
	public void setControllerFactory(ControllerFactory factory) {
		controllerFactory = factory;
	}

	/**
	 * Gets the content viewer of thie pattern view.
	 * @return The content viewer of thie pattern view
	 */
	public TableViewer getContentViewer() {
		return contentViewer;
	}

	/**
	 * Gets the Info viewer of this pattern view.
	 * @return The Info viewer of this pattern view.
	 */
	public TextViewer getInfoViewer() {
		return infoViewer;
	}

	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.ui.view.SecurityView#createLoginView(org.eclipse.swt.widgets.Composite)
	 */
	protected void createLoginView(Composite parent) {
		parent.setLayout(new FillLayout());
		outerSash = new SashForm(parent, SWT.NONE|SWT.VERTICAL);
		
		// Create splitter between tree and content pane.
		sash = new SashForm(outerSash, SWT.NONE);
		sash.setOrientation(SWT.HORIZONTAL);
		treeViewer = createTreeViewer(sash);
		contentViewer = createContentViewer(sash);		
		setContentLabelProvider(null);
		sash.setWeights(new int[]{30, 70});
		
		infoViewer = createInfoViewer(outerSash);
		outerSash.setWeights(new int[]{85, 15});
		initialiseDragAndDrop();
		int orientation = VERTICAL_ORIENTATION_VIEW;
		String persistedView = getState(VIEW_ORIENTATION);
		if (persistedView != null && !persistedView.equals("")) {
			orientation = Integer.valueOf(persistedView).intValue();
		}
		setView(orientation);

		Menu menuTree = 
			treeContextMenuManager.createContextMenu(treeViewer.getControl());
		treeViewer.getControl().setMenu(menuTree);

		Menu menuContent = 
			contentContextMenuManager.createContextMenu(contentViewer.getControl());
		contentViewer.getControl().setMenu(menuContent);
		
		String showInfoView = getState(INFO_PANE);
		if (showInfoView != null&& !showInfoView.equals("")) {
			showInfoPane(Boolean.valueOf(showInfoView).booleanValue());
		}
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.ui.view.SecurityView#clearLoginView(org.eclipse.swt.widgets.Composite)
	 */
	protected void clearLoginView(Composite parent) {
		if (contentViewer != null) { 
			contentViewer.getTable().removeAll();
		}
		if (treeViewer != null) {
			treeViewer.getTree().removeAll();
		}
		deactivateControllers();
		super.clearLoginView(parent);
	}

	/**
	 * Initialises Drag and Drop support.
	 */
	protected void initialiseDragAndDrop() {
	}

	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.ui.view.SecurityView#createActions()
	 */
	protected void createActions() {
		super.createActions();
		
		//=== Vertical Orientation Action ===
		verticalOrientationAction = 
		new Action(VERTICAL_VIEW_ORIENTATION, IAction.AS_RADIO_BUTTON) {
			public void run() {
				setView(VERTICAL_ORIENTATION_VIEW);
			}
		};
		verticalOrientationAction.setDescription(VERTICAL_VIEW_ORIENTATION);
		verticalOrientationAction.setToolTipText(VERTICAL_VIEW_ORIENTATION);
		verticalOrientationAction.setImageDescriptor(Images.ICON_VIEW_VERITICAL_ORIENTATION);
	
		//=== Horizontal Orientation Action	===
		horizontalOrientationAction = 
		new Action(HORIZONTAL_VIEW_ORIENTATION, IAction.AS_RADIO_BUTTON) {
			public void run() {
				setView(HORIZONTAL_ORIENTATION_VIEW);
			}
		};
		horizontalOrientationAction.setDescription(HORIZONTAL_VIEW_ORIENTATION);
		horizontalOrientationAction.setToolTipText(HORIZONTAL_VIEW_ORIENTATION);
		horizontalOrientationAction.setImageDescriptor(Images.ICON_VIEW_HORIZONTAL_ORIENTATION);
		
		//=== Content-View-Only Action ===
		contentViewOnlyAction = 
		new Action(CONTENT_VIEW_ONLY, IAction.AS_RADIO_BUTTON) {
			public void run() {
				setView(CONTENT_ONLY_VIEW);
			}
		};
		contentViewOnlyAction.setDescription(CONTENT_VIEW_ONLY);
		contentViewOnlyAction.setToolTipText(CONTENT_VIEW_ONLY);
		contentViewOnlyAction.setImageDescriptor(Images.ICON_VIEW_CONTENT_ONLY);
		
		//=== Hide Info Pane Action ===
		hideInfoPaneAction = 
		new Action(HIDE_INFO_PANE, IAction.AS_CHECK_BOX) {
			public void run() {
				toggleInfoPane();
			}
		};
		hideInfoPaneAction.setDescription(HIDE_INFO_PANE);
		hideInfoPaneAction.setToolTipText(HIDE_INFO_PANE);
		hideInfoPaneAction.setImageDescriptor(Images.ICON_HIDE_INFO);
	}

	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.ui.view.SecurityView#fillMenu(org.eclipse.jface.action.IMenuManager)
	 */
	protected void fillMenu(IMenuManager menu) {
		super.fillMenu(menu);
		menu.add(new Separator());
		GroupMarker viewGroup = new GroupMarker(VIEW_GROUP);
		menu.add(viewGroup);
		menu.appendToGroup(VIEW_GROUP, verticalOrientationAction);
		menu.appendToGroup(VIEW_GROUP, horizontalOrientationAction);
		menu.appendToGroup(VIEW_GROUP, contentViewOnlyAction);
		menu.add(new Separator());
		menu.add(hideInfoPaneAction);
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.ui.view.SecurityView#fillActionBars(org.eclipse.jface.action.IToolBarManager)
	 */
	protected void fillActionBars(IToolBarManager toolBar) {
		super.fillActionBars(toolBar);
		GroupMarker viewGroup = new GroupMarker(VIEW_GROUP);
		toolBar.add(viewGroup);
		toolBar.appendToGroup(VIEW_GROUP, verticalOrientationAction);
		toolBar.appendToGroup(VIEW_GROUP, horizontalOrientationAction);
		toolBar.appendToGroup(VIEW_GROUP, contentViewOnlyAction);
		toolBar.add(new Separator());
		toolBar.add(hideInfoPaneAction);
	}
	
	/**
	 * Creates the tree viewer that is hosted in tree pane.
	 * <p>
	 * @param parent The parent composite that hosts the Tree Viewer.
	 * @return A TreeViewer represents the hierarchy View.
	 */
	protected TreeViewer createTreeViewer(Composite parent) {
		TreeViewer viewer = new TreeViewer(parent, SWT.SINGLE | SWT.BORDER);
		viewer.getControl().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		viewer.addSelectionChangedListener(new SelectionChangeAdaptor() {
			public void selectionChanged(SelectionChangedEvent event, Object[] selectedObject) {
				handleTreeSelectionChanged(selectedObject);
			}
		}
		);
		return viewer;
	}
	
	/**
	 * Creates the Content Viewer for this pattern view.
	 * <p>
	 * @param parent The parent composite that hosts this content viewer.
	 * @return A TableViewer that represents as Content Viewer.
	 */
	protected TableViewer createContentViewer(Composite parent) {
		TableViewer tableViewer = new TableViewer(parent);
		Table table = tableViewer.getTable();
		table.setLayoutData(new GridData(GridData.FILL_BOTH));
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		tableViewer.setContentProvider(new NodeTreeContentProvider());

		tableViewer.addSelectionChangedListener(new SelectionChangeAdaptor() {
			public void selectionChanged(SelectionChangedEvent event, Object[] selectedObject) {
				handleContentSelectionChanged(selectedObject);
			}
		});
		return tableViewer;
	}
	
	protected void activateControllers(StructuredViewer viewer) {
		Assert.isNotNull(viewer);
		deactivateControllers();
		
		controllers = new ArrayList();
		if((viewer == null) || (controllerFactory == null)) return;
		
		IStructuredSelection selection = 
			(IStructuredSelection)viewer.getSelection();
			
		Object[] selectedObjects = selection.toArray();
		for (int i = 0; i < selectedObjects.length; i++) {
			Object selectedObject = selectedObjects[i];
			if(selectedObject instanceof Node) {
				Controller controller = controllerFactory.create((Node)selectedObject);
				if(controller != null) {
					controllers.add(controller);
					controller.activate(viewer, this, getSite());
				}
			}
		}
	}
	
	protected void deactivateControllers() {
		if(controllers == null) return;
		Iterator it = controllers.iterator();
		while(it.hasNext()) {
			Controller controller = (Controller)it.next();
			controller.deactivate();
		}
		controllers = null;
	}
	
	void showContextMenu(IMenuManager manager, StructuredViewer viewer) {
		activateControllers(viewer);
		if(controllers.isEmpty()) return;
		Controller controller = (Controller)controllers.get(0);
		controller.fillMenu(manager);
	}
	
	
	private void setContentLabelProvider(Node node) {
		ITableLabelProvider labelProvider = createContentLabelProvider(node);
		if(labelProvider == null) labelProvider = defaultContentLabelProvider;

		if(labelProvider != getContentViewer().getLabelProvider()) {
			clearColumns(getContentViewer().getTable());

			getContentViewer().setLabelProvider(labelProvider);
			if(labelProvider instanceof ITableColumnProvider) {
				ITableColumnProvider columnProvider = 
					(ITableColumnProvider)labelProvider;
				columnProvider.provideColumns(contentViewer.getTable());
			}
		}
	}
	
	/**
	 * Creates the Info Viewer for this pattern view.
	 * @param parent The parent that hosts the Info Viewer.
	 * @return A TextViewer as Info Viewer.
	 */
	protected TextViewer createInfoViewer(Composite parent) {
		TextViewer infoViewer = 
			new TextViewer(parent, SWT.BORDER | SWT.LEFT | SWT.TOP | SWT.WRAP);
		infoViewer.setEditable(false);
		infoViewer.setDocument(new Document());
		return infoViewer;
	}
	
	/**
	 * Gets the documentation displaying in the Info View from the selection.
	 * @param document The document that contains the documentation.
	 * @param selection The selection in which documentation is for.
	 */
	protected boolean getDocumentation(
		Document document,
		IStructuredSelection selection) {
		return false;
	}
	
	/**
	 * Sets the current view mode of this pattern view.
	 * @param view The view mode of this pattern view.
	 */
	public void setView(int view) {
		//this.view = view;
		
		if(sash != null)
		switch(view) {
			case CONTENT_ONLY_VIEW:
			sash.setMaximizedControl(contentViewer.getControl());
			verticalOrientationAction.setChecked(false);
			horizontalOrientationAction.setChecked(false);
			contentViewOnlyAction.setChecked(true);
			break;
			
			case HORIZONTAL_ORIENTATION_VIEW:
			sash.setMaximizedControl(null);
			sash.setOrientation(SWT.HORIZONTAL);
			verticalOrientationAction.setChecked(false);
			contentViewOnlyAction.setChecked(false);
			horizontalOrientationAction.setChecked(true);
			break;

			case VERTICAL_ORIENTATION_VIEW:
			default:
			sash.setMaximizedControl(null);
			sash.setOrientation(SWT.VERTICAL);
			horizontalOrientationAction.setChecked(false);
			contentViewOnlyAction.setChecked(false);
			verticalOrientationAction.setChecked(true);
			break;
		}
		setState(VIEW_ORIENTATION, String.valueOf(view));
	}
	
	/**
	 * Returns whether the info pane is currently shown.<p>
	 * @return true if the info pane is currently shown, false otherwise.
	 */
	public boolean isInfoPaneShown() {
		return (outerSash.getMaximizedControl() != null);
	}
	
	/**
	 * Shows or hides the info pane.<p>
	 * @param show Shows or hides the info pane.
	 */
	public void showInfoPane(boolean show) {
		if(show) {
			if(!isInfoPaneShown()) {
				outerSash.setMaximizedControl(sash);
				hideInfoPaneAction.setChecked(true);
			}
		}
		else {
			if(isInfoPaneShown()) {
				outerSash.setMaximizedControl(null);
				hideInfoPaneAction.setChecked(false);
			}
		}
		setState(INFO_PANE, String.valueOf(show));
	}
	/**
	 * Toggles the Info Pane visiblity state.
	 */
	public void toggleInfoPane() {
		showInfoPane(!isInfoPaneShown());
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.ui.view.SecurityView#handleLogin()
	 */
	protected void handleLogin() {
		super.handleLogin();
		verticalOrientationAction.setEnabled(true);
		horizontalOrientationAction.setEnabled(true);
		contentViewOnlyAction.setEnabled(true);
		hideInfoPaneAction.setEnabled(true);
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.ui.view.SecurityView#handleLogout()
	 */
	protected void handleLogout() {
		hideInfoPaneAction.setEnabled(false);
		verticalOrientationAction.setEnabled(false);
		horizontalOrientationAction.setEnabled(false);
		contentViewOnlyAction.setEnabled(false);
		super.handleLogout();
	}
	
	/**
	 * Handles hierarchy selection change.
	 * @param selectedObjects All selected objects.
	 */
	protected void handleTreeSelectionChanged(Object[] selectedObjects) {
		// Support single selection for tree view ONLY
		Object selected = selectedObjects[0];

		Node node = null;
		Node contentNode = null;
				
		if(selected instanceof Node) {
			// Valid Node selected in the tree
			node = (Node)selected;
			contentNode = createTableContentProviderNode(node);
		}
		
//		final Node fcn = contentNode;
//		Display.getCurrent().asyncExec(
//				new Runnable() {
//					public void run() {
						contentViewer.getTable().setRedraw(false);
						setContentLabelProvider(contentNode);
						contentViewer.setInput(contentNode);
						contentViewer.getTable().setRedraw(true);
//					}
//				});
	}
	
	private void clearColumns(Table table) {
		table.removeAll();
		TableColumn[] columns = table.getColumns();
		for (int i = 0; i < columns.length; i++) {
			columns[i].dispose();
		}
	}
	/**
	 * Handles Content selection change.
	 * @param selectedObjects All selected objects.
	 * content selection change.
	 */
	protected void handleContentSelectionChanged(Object[] selectedObjects) {
		Document doc = new Document();
		IStructuredSelection selection = (IStructuredSelection)contentViewer.getSelection();
		if(getDocumentation(doc, selection)) {
		}
		else {
			doc.set("");
		}
		infoViewer.setDocument(doc);
	}
	
	protected Node createTableContentProviderNode(Node templateNode) {
		if(contentNodeFactory != null) {
			return contentNodeFactory.createContentNode(templateNode);
		}
		return null;
	}
	
	/**
	 * Creates the content provider of the content view for a node.<p>
	 * @param node A node that represents the selected object in the hierarchy.
	 * @return An <code>IStructuredContentProvider</code> that will be bound to 
	 * the content view.
	 */
	/*
	protected IStructuredContentProvider createTableContentProvider(Node node) {
		return new NodeTreeContentProvider();
	}
	*/

	protected ITableLabelProvider createContentLabelProvider(Node node) {
		return null;
	}
	
	private void setState(String setting, String value) {
		MajiPlugin.getDefault().getPluginPreferences().setValue(setting, value);
		MajiPlugin.getDefault().savePluginPreferences();
	}
	
	private String getState(String setting) {
		return MajiPlugin.getDefault().getPluginPreferences().getString(setting);
	}
}
