/*
 * @(#)PropertySheetPage.java
 * Created on 28/04/2004
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.properties;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.custom.TableTree;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.HelpEvent;
import org.eclipse.swt.events.HelpListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchPart;

import org.eclipse.ui.help.WorkbenchHelp;
import org.eclipse.ui.part.CellEditorActionHandler;
import org.eclipse.ui.part.Page;
import org.eclipse.ui.views.properties.IPropertySheetEntry;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.IPropertySourceProvider;

/**
 * <code>PropertySheetPage</code>.
 * <p>
 * @author Kin Wong
 */
abstract public class PropertySheetPage extends Page implements IPropertySheetPage {
	/**
	 * Help context id 
	 * (value <code>"org.eclipse.ui.property_sheet_page_help_context"</code>).
	 */
	public static final String HELP_CONTEXT_PROPERTY_SHEET_PAGE = "org.eclipse.ui.property_sheet_page_help_context"; //$NON-NLS-1$

	private PropertySheetViewer viewer;
	private IPropertySheetEntry rootEntry;
	private IPropertySourceProvider provider;

	private ICellEditorActivationListener cellEditorActivationListener;
	private CellEditorActionHandler cellEditorActionHandler;

	private Clipboard clipboard;
	/**
	 * Creates a new property sheet page.
	 */
	public PropertySheetPage() {
		super();
	}
	/* (non-Javadoc)
	 * Method declared on <code>IPage</code>.
	 */
	public void createControl(Composite parent) {
		// create a new viewer
		
		viewer = new PropertySheetViewer(parent);

		// set the model for the viewer
		if (rootEntry == null) {
			// create a new root
			PropertySheetEntry root = new PropertySheetEntry();
			if (provider != null)
				// set the property source provider
				root.setPropertySourceProvider(provider);
			rootEntry = root;
		}
		viewer.setRootEntry(rootEntry);
		viewer.addActivationListener(getCellEditorActivationListener());
		// add a listener to track when the entry selection changes
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				handleEntrySelection((IStructuredSelection) event.getSelection());
			}
		});
		initDragAndDrop();
		createActions();
		
		// Create the popup menu for the page.
		MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
		contributeToMenu(menuMgr);
		
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);

		// Set help on the viewer 
		viewer.getControl().addHelpListener(new HelpListener() {
			/*
			 * @see HelpListener#helpRequested(HelpEvent)
			 */
			public void helpRequested(HelpEvent e) {
				// Get the context for the selected item
				IStructuredSelection selection = (IStructuredSelection)viewer.getSelection();
				if (!selection.isEmpty()) {
					IPropertySheetEntry entry = (IPropertySheetEntry)selection.getFirstElement();
					Object helpContextId = entry.getHelpContextIds();
					if (helpContextId != null) {
						if (helpContextId instanceof String) {
							WorkbenchHelp.displayHelp((String)helpContextId);
							return;
						}

						// Since 2.0 the only valid type for helpContextIds
						// is a String (a single id).
						// However for backward compatibility we have to handle
						// and array of contexts (Strings and/or IContexts) 
						// or a context computer.
						
//						Object[] contexts = null;
//						if (helpContextId instanceof IContextComputer) {
//							// get local contexts
//							contexts = ((IContextComputer)helpContextId).getLocalContexts(e);
//						} else {
//							contexts = (Object[])helpContextId;
//						}
//						// Ignore all but the first element in the array
//						if (contexts[0] instanceof IContext) 
//							WorkbenchHelp.displayHelp((IContext)contexts[0]);
//						else
//							WorkbenchHelp.displayHelp((String)contexts[0]);
//						return;
					}
				}
				
				// No help for the selection so show page help
				WorkbenchHelp.displayHelp(HELP_CONTEXT_PROPERTY_SHEET_PAGE);
			}
		});
	}
	
	public PropertySheetViewer getViewer() {
		return viewer;
	}
	
	/**
	 * The <code>PropertySheetPage</code> implementation of this <code>IPage</code> method 
	 * disposes of this page's entries.
	 */
	public void dispose() {
		super.dispose();
		if (rootEntry != null) {
			rootEntry.dispose();
			rootEntry = null;
		}
		if (clipboard != null) {
			clipboard.dispose();
			clipboard = null;
		}			
	}
	/**
	 * Returns the cell editor activation listener for this page
	 */
	private ICellEditorActivationListener getCellEditorActivationListener() {
		if (cellEditorActivationListener == null) {
			cellEditorActivationListener = new ICellEditorActivationListener() {
				public void cellEditorActivated(CellEditor cellEditor) {
					if (cellEditorActionHandler != null)
						cellEditorActionHandler.addCellEditor(cellEditor);
				}
				public void cellEditorDeactivated(CellEditor cellEditor) {
					if (cellEditorActionHandler != null)
						cellEditorActionHandler.removeCellEditor(cellEditor);
				}
			};
		}
		return cellEditorActivationListener;
	}
	/* (non-Javadoc)
	 * Method declared on IPage (and Page).
	 */
	public Control getControl() {
		if (viewer == null)
			return null;
		return viewer.getControl();
	}
	
	/**
	 * Returns the image descriptor with the given relative path.
	 */
	/*
	private ImageDescriptor getImageDescriptor(String relativePath) {
		String iconPath = "icons/full/"; //$NON-NLS-1$

		try {
			Bundle bundle =  Platform.getBundle(PlatformUI.PLUGIN_ID);
			URL installURL = bundle.getEntry("/");
		
			URL url = new URL(installURL, iconPath + relativePath);
			return ImageDescriptor.createFromURL(url);
		} catch (MalformedURLException e) {
			// Should not happen
			return null;
		}
	}
	*/
	
	/**
	 * Handles a selection change in the entry table.
	 *
	 * @param selection the new selection
	 */
	public void handleEntrySelection(ISelection selection) {
		updateActions();
	}
	
	protected boolean isCurrentEditable() {
		return viewer.getActiveCellEditor() != null;
	}
	
	/**
	 * Adds drag and drop support.
	 */
	protected void initDragAndDrop() {
		int operations = DND.DROP_COPY;
		Transfer[] transferTypes = new Transfer[]{
			TextTransfer.getInstance()};
		DragSourceListener listener = new DragSourceAdapter() {
			public void dragSetData(DragSourceEvent event){
				performDragSetData(event);
			}
			public void dragFinished(DragSourceEvent event){
			}
		};
		DragSource dragSource = new DragSource(((TableTree)viewer.getControl()).getTable(), operations);
		dragSource.setTransfer(transferTypes);
		dragSource.addDragListener(listener);
	}
	/**
	 * The user is attempting to drag.  Add the appropriate
	 * data to the event.
	 */
	void performDragSetData(DragSourceEvent event) {
		// Get the selected property
		IStructuredSelection selection = (IStructuredSelection)viewer.getSelection();
		if (selection.isEmpty()) 
			return;
		// Assume single selection
		IPropertySheetEntry entry = (IPropertySheetEntry)selection.getFirstElement();

		// Place text as the data
		StringBuffer buffer = new StringBuffer();
		buffer.append(entry.getDisplayName());
		buffer.append("\t"); //$NON-NLS-1$
		buffer.append(entry.getValueAsString());
		
		event.data = buffer.toString();			
	}
	/**
	 * Make action objects.
	 */
	abstract protected void createActions();
	abstract protected void updateActions();
	
	protected void contributeToMenu(IMenuManager menu) {
		
	}
	protected void contributeToToolBar(IToolBarManager toolBar) {
		
	}
	
	protected void contributeToStatusLine(IStatusLineManager statusLineManager) {
	}
	
	
	/* (non-Javadoc)
	 * Method declared on IPage (and Page).
	 */
	public void makeContributions(IMenuManager menuManager, IToolBarManager toolBarManager, IStatusLineManager statusLineManager) {

		// add actions to the tool bar
		contributeToToolBar(toolBarManager);
		contributeToMenu(menuManager);

		contributeToStatusLine(statusLineManager);
		viewer.setStatusLineManager(statusLineManager);
	}
	/**
	 * Updates the model for the viewer.
	 * <p>
	 * Note that this means ensuring that the model reflects the state
	 * of the current viewer input. 
	 * </p>
	 */
	public void refresh() {
		if (viewer == null)
			return;
		// calling setInput on the viewer will cause the model to refresh
		viewer.setInput(viewer.getInput());
	}
	/* (non-Javadoc)
	 * Method declared on ISelectionListener.
	 */
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		if (viewer == null)
			return;

		// change the viewer input since the workbench selection has changed.
		if (selection instanceof IStructuredSelection) {
			viewer.setInput(((IStructuredSelection) selection).toArray());
		}
	}
	/**
	 * The <code>PropertySheetPage</code> implementation of this <code>IPage</code> method
	 * calls <code>makeContributions</code> for backwards compatibility with
	 * previous versions of <code>IPage</code>. 
	 * <p>
	 * Subclasses may reimplement.
	 * </p>
	 */
	public void setActionBars(IActionBars actionBars) {
		super.setActionBars(actionBars);
		cellEditorActionHandler = new CellEditorActionHandler(actionBars);
	}
	/**
	 * Sets focus to a part in the page.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}
	/**
	 * Sets the given property source provider as
	 * the property source provider.
	 * <p>
	 * Calling this method is only valid if you are using
	 * this page's default root entry.
	 * </p>
	 * @param newProvider the property source provider
	 */
	public void setPropertySourceProvider(IPropertySourceProvider newProvider) {
		provider = newProvider;
		if (rootEntry instanceof PropertySheetEntry) {
			((PropertySheetEntry) rootEntry).setPropertySourceProvider(provider);
			// the following will trigger an update
			viewer.setRootEntry(rootEntry);
		}
	}
	/**
	 * Sets the given entry as the model for the page.
	 *
	 * @param entry the root entry
	 */
	public void setRootEntry(IPropertySheetEntry entry) {
		rootEntry = entry;
		if (viewer != null)
			// the following will trigger an update
			viewer.setRootEntry(rootEntry);
	}
}
