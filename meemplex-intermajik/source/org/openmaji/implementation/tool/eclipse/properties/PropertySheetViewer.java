/*
 * @(#)PropertySheetViewer.java
 * Created on 28/04/2004
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.properties;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.util.ListenerList;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLayoutData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ICellEditorListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableTree;
import org.eclipse.swt.custom.TableTreeEditor;
import org.eclipse.swt.custom.TableTreeItem;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TreeEvent;
import org.eclipse.swt.events.TreeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.views.properties.IPropertySheetEntry;
import org.eclipse.ui.views.properties.IPropertySheetEntryListener;

/**
 * <code>PropertySheetViewer</code>.
 * <p>
 * @author Kin Wong
 */
public class PropertySheetViewer extends Viewer {
	
	// The input objects for the viewer
	private Object[] input;

	// The root entry of the viewer 
	private IPropertySheetEntry rootEntry;

	// The current categories
	private PropertySheetCategory[] categories;

	// SWT widgets
	private TableTree tableTree;
	private TableTreeEditor tableTreeEditor;
	private static String[] columnLabels = { PropertiesMessages.PropertyViewer_property, PropertiesMessages.PropertyViewer_value };
	private static String MISCELLANEOUS_CATEGORY_NAME = PropertiesMessages.PropertyViewer_misc;

	// Cell editor support.
	private int columnToEdit = 1;
	private CellEditor cellEditor;
	private IPropertySheetEntryListener entryListener;
	private ICellEditorListener editorListener;

	// Flag to indicate if categories (if any) should be shown
	private boolean isShowingCategories = false;

	// The status line manager for showing messages
	private IStatusLineManager statusLineManager;

	// Cell editor activation listeners
	private ListenerList activationListeners = new ListenerList(3);
	
	private Set filters;
	/**
	 * Creates a property sheet viewer on a newly-created table tree control
	 * under the given parent. The viewer has no input, and no root entry.
	 *
	 * @param parent the parent control
	 */
	public PropertySheetViewer(Composite parent) {
		tableTree = new TableTree(parent, SWT.FULL_SELECTION | SWT.SINGLE | SWT.HIDE_SELECTION);

		// configure the widget
		Table table = tableTree.getTable();
		table.setLinesVisible(true);
		table.setHeaderVisible(true);

		// configure the columns
		addColumns();

		// add our listeners to the widget
		hookControl();

		// create a new table tree editor	
		tableTreeEditor = new TableTreeEditor(tableTree);

		// create the entry and editor listener
		createEntryListener();
		createEditorListener();
	}
	
	protected boolean isCellEditorActivated() {
		return cellEditor != null;
	}
	/**
	 * Activate a cell editor for the given selected table tree item.
	 *
	 * @param item the selected table tree item
	 */
	private void activateCellEditor(TableTreeItem item) {
		// ensure the cell editor is visible
		tableTree.showSelection();

		// Get the entry for this item
		IPropertySheetEntry activeEntry = (IPropertySheetEntry) item.getData();

		// Get the cell editor for the entry.
		// Note that the editor parent must be the Table control 
		// that is underneath the TableTree
		cellEditor = activeEntry.getEditor(tableTree.getTable());

		if (cellEditor == null)
			// unable to create the editor
			return;

		// activate the cell editor
		cellEditor.activate();

		// if the cell editor has no control we can stop now
		Control control = cellEditor.getControl();
		if (control == null) {
			cellEditor.deactivate();
			cellEditor = null;
			return;
		}

		// add our editor listener
		cellEditor.addListener(editorListener);

		// set the layout of the table tree editor to match the cell editor
		CellEditor.LayoutData layout = cellEditor.getLayoutData();
		tableTreeEditor.horizontalAlignment = layout.horizontalAlignment;
		tableTreeEditor.grabHorizontal = layout.grabHorizontal;
		tableTreeEditor.minimumWidth = layout.minimumWidth;
		tableTreeEditor.setEditor(control, item, columnToEdit);

		// set the error text from the cel editor
		setErrorMessage(cellEditor.getErrorMessage());

		// give focus to the cell editor
		cellEditor.setFocus();

		// notify of activation	
		fireCellEditorActivated(cellEditor);
	}

	public boolean hasFilters() {
		return filters != null;
	}
	
	public void addFilter(Object filter) {
		if(filter == null) return;
		if(filters == null) filters = new HashSet();
		filters.add(filter);
		refresh();
	}
	
	public boolean removeFilter(Object filter) {
		if(filters == null) return false;
		if(filters.remove(filter)) {
			refresh();
			return true;
		}
		return false;
	}
	
	public void removeAllFilters() {
		filters = null;
		refresh();
	}
	
	/**
	 * Adds a cell editor activation listener.
	 * Has no effect if an identical activation listener 
	 * is already registered.
	 *
	 * @param listener a cell editor activation listener
	 */
	/*package*/
	void addActivationListener(ICellEditorActivationListener listener) {
		activationListeners.add(listener);
	}
	/**
	 * Add columns to the table tree and 
	 * set up the layout manager accordingly.
	 */
	private void addColumns() {
		Table table = tableTree.getTable();

		// create the columns
		TableColumn[] columns = table.getColumns();
		for (int i = 0; i < columnLabels.length; i++) {
			String string = columnLabels[i];
			if (string != null) {
				TableColumn column;
				if (i < columns.length)
					column = columns[i];
				else
					column = new TableColumn(table, 0);
				column.setText(string);
			}
		}

		// property column
		ColumnLayoutData c1Layout = new ColumnWeightData(40, false);

		// value column
		ColumnLayoutData c2Layout = new ColumnWeightData(60, true);

		// set columns in Table layout
		TableLayout layout = new TableLayout();
		layout.addColumnData(c1Layout);
		layout.addColumnData(c2Layout);
		table.setLayout(layout);
	}
	/**
	 * Asks the entry currently being edited to apply its
	 * current cell editor value.
	 */
	private void applyEditorValue() {
		TableTreeItem treeItem = tableTreeEditor.getItem();
		// treeItem can be null when view is opened
		if (treeItem == null || treeItem.isDisposed())
			return;
		IPropertySheetEntry entry = (IPropertySheetEntry) treeItem.getData();
		entry.applyEditorValue();
	}
	/**
	 * Creates the child items for the given widget (item or table tree).
	 * This method is called when the item is expanded for the first time or
	 * when an item is assigned as the root of the table tree.
	 */
	private void createChildren(Widget widget) {
		// get the current child items
		TableTreeItem[] childItems;
		if (widget == tableTree)
			childItems = tableTree.getItems();
		else {
			childItems = ((TableTreeItem) widget).getItems();
		}

		if (childItems.length > 0) {
			Object data = childItems[0].getData();
			if (data != null)
				// children already there!
				return;
			else
				// remove the dummy
				childItems[0].dispose();
		}

		// get the children and create their table tree items
		Object node = widget.getData();
		List children = getChildren(node);
		if (children.isEmpty())
			// this item does't actually have any children
			return;
		for (int i = 0; i < children.size(); i++) {
			// create a new table tree item
			createItem(children.get(i), widget, i);
		}
	}
	/** 
	 * Creates a new cell editor listener.
	 */
	private void createEditorListener() {
		editorListener = new ICellEditorListener() {
			public void cancelEditor() {
				deactivateCellEditor();
			}
			public void editorValueChanged(boolean oldValidState, boolean newValidState) {
			}
			public void applyEditorValue() {
			}
		};
	}
	/** 
	 * Creates a new property sheet entry listener.
	 */
	private void createEntryListener() {
		entryListener = new IPropertySheetEntryListener() {
			public void childEntriesChanged(IPropertySheetEntry entry) {
					// update the children of the given entry
	if (entry == rootEntry)
					updateChildrenOf(entry, tableTree);
				else {
					TableTreeItem item = findItem(entry);
					if (item != null)
						updateChildrenOf(entry, item);
				}
			}
			public void valueChanged(IPropertySheetEntry entry) {
				// update the given entry
				TableTreeItem item = findItem(entry);
				if (item != null)
					updateEntry(entry, item);
			}
			public void errorMessageChanged(IPropertySheetEntry entry) {
				// update the error message
				setErrorMessage(entry.getErrorText());
			}
		};
	}
	/**
	 * Creates a new table tree item, sets the given entry or
	 * category (node)in its user data field, and adds a listener to
	 * the node if it is an entry.
	 *
	 * @param node the entry or category associated with this item
	 * @param parent the parent widget
	 * @param index indicates the position to insert the item 
	 *    into its parent
	 */
	private void createItem(Object node, Widget parent, int i) {
		// create the item
		TableTreeItem item;
		if (parent instanceof TableTreeItem)
			item = new TableTreeItem((TableTreeItem) parent, SWT.NONE, i);
		else
			item = new TableTreeItem((TableTree) parent, SWT.NONE, i);

		// set the user data field	
		item.setData(node);

		// add our listener
		if (node instanceof IPropertySheetEntry)
			 ((IPropertySheetEntry) node).addPropertySheetEntryListener(entryListener);

		// update the visual presentation	
		if (node instanceof IPropertySheetEntry)
			updateEntry((IPropertySheetEntry) node, item);
		else
			updateCategory((PropertySheetCategory) node, item);
	}
	/**
	 * Deactivate the currently active cell editor.
	 */
	/*package*/
	void deactivateCellEditor() {
		tableTreeEditor.setEditor(null, null, columnToEdit);
		if (cellEditor != null) {
			cellEditor.deactivate();
			fireCellEditorDeactivated(cellEditor);
			cellEditor.removeListener(editorListener);
			cellEditor = null;
		}
		// clear any error message from the editor
		setErrorMessage(null);
	}
	/**
	 * Sends out a selection changed event for the entry table to all 
	 * registered listeners.
	 */
	private void entrySelectionChanged() {
		SelectionChangedEvent changeEvent = new SelectionChangedEvent(this, getSelection());
		fireSelectionChanged(changeEvent);
	}
	/**
	 * Return a table tree item in the property sheet that has 
	 * the same entry in its user data field as the supplied
	 * entry. Return <code>null</code> if there is no such item.
	 *
	 * @param entry the entry to serach for
	 */
	private TableTreeItem findItem(IPropertySheetEntry entry) {
		// Iterate through tableTreeItems to find item
		TableTreeItem[] items = tableTree.getItems();
		for (int i = 0; i < items.length; i++) {
			TableTreeItem item = items[i];
			TableTreeItem findItem = findItem(entry, item);
			if (findItem != null)
				return findItem;
		}
		return null;
	}
	/**
	 * Return a table tree item in the property sheet that has 
	 * the same entry in its user data field as the supplied
	 * entry. Return <code>null</code> if there is no such item.
	 *
	 * @param entry the entry to search for
	 * @param item the item look in
	 */
	private TableTreeItem findItem(IPropertySheetEntry entry, TableTreeItem item) {
		// compare with current item
		if (entry == item.getData())
			return item;

		// recurse over children
		TableTreeItem[] items = item.getItems();
		for (int i = 0; i < items.length; i++) {
			TableTreeItem childItem = items[i];
			TableTreeItem findItem = findItem(entry, childItem);
			if (findItem != null)
				return findItem;
		}
		return null;
	}
	/**
	 * Notifies all registered cell editor activation listeners
	 * of a cell editor activation.
	 *
	 * @param cellEditor the activated cell editor
	 */
	private void fireCellEditorActivated(CellEditor cellEditor) {
		Object[] listeners = activationListeners.getListeners();
		for (int i = 0; i < listeners.length; ++i) {
			((ICellEditorActivationListener) listeners[i]).cellEditorActivated(cellEditor);
		}
	}
	/**
	 * Notifies all registered cell editor activation listeners
	 * of a cell editor deactivation.
	 *
	 * @param cellEditor the deactivated cell editor
	 */
	private void fireCellEditorDeactivated(CellEditor cellEditor) {
		Object[] listeners = activationListeners.getListeners();
		for (int i = 0; i < listeners.length; ++i) {
			((ICellEditorActivationListener) listeners[i]).cellEditorDeactivated(cellEditor);
		}
	}
	/**
	 * Returns the active cell editor of this property sheet viewer
	 * or <code>null</code> if no cell editor is active.
	 *
	 * @return the active cell editor
	 */
	public CellEditor getActiveCellEditor() {
		return cellEditor;
	}
	/**
	 * Returns the children of the given category or entry
	 *
	 * @node a category or entry
	 * @return the children of the given category or entry
	 *  (element type <code>IPropertySheetEntry</code> or 
	 *  <code>PropertySheetCategory</code>)
	 */
	private List getChildren(Object node) {
		// cast the entry or category	
		IPropertySheetEntry entry = null;
		PropertySheetCategory category = null;
		if (node instanceof IPropertySheetEntry)
			entry = (IPropertySheetEntry) node;
		else
			category = (PropertySheetCategory) node;

		// get the child entries or categories
		List children;
		if (category == null)
			children = getChildren(entry);
		else
			children = getChildren(category);

		return children;
	}
	/**
	 * Returns the child entries of the given entry
	 *
	 * @return the children of the given entry
	 *  (element type <code>IPropertySheetEntry</code>) 
	 */
	private List getChildren(IPropertySheetEntry entry) {
		// if the entry is the root and we are showing categories, and we have more than the
		// defualt category, return the categories
		if (entry == rootEntry && isShowingCategories) {
			if (categories.length > 1 || 
				(categories.length == 1 && 
					!categories[0].getCategoryName().equals(MISCELLANEOUS_CATEGORY_NAME)))
						return Arrays.asList(categories);
		}

		// return the filtered child entries
		return getFilteredEntries(entry.getChildEntries());
	}
	/**
	 * Returns the child entries of the given category
	 *
	 * @return the children of the given category
	 *  (element type <code>IPropertySheetEntry</code>)
	 */
	private List getChildren(PropertySheetCategory category) {
		return getFilteredEntries(category.getChildEntries());
	}
	/* (non-Javadoc)
	 * Method declared on Viewer.
	 */
	public Control getControl() {
		return tableTree;
	}
	
	
	/**
	 * Returns the entries which match the current filter.
	 *
	 * @entries the entries to filter
	 * @return the entries which match the current filter
	 *  (element type <code>IPropertySheetEntry</code>)
	 */
	private List getFilteredEntries(IPropertySheetEntry[] entries) {
		// if no filter just return all entries
		if (filters == null)
			return Arrays.asList(entries);
		
		// check each entry for the filter
		List filteredEntries = new ArrayList(entries.length);
		for (int i = 0; i < entries.length; i++) {
			IPropertySheetEntry entry = entries[i];
			if(includeEntry(entry)) filteredEntries.add(entry);
		}
		return filteredEntries;
	}
	
	protected boolean includeEntry(IPropertySheetEntry entry) {
		String[] entryFilters = entry.getFilters();
		if(entryFilters == null) return true;
		
		for (int j = 0; j < entryFilters.length; j++) {
			String entryFilter = entryFilters[j];
			if (filters.contains(entryFilter)) return true;
		}
		return false;
	}
	
	/**
	 * The <code>PropertySheetViewer</code> implementation of this
	 * method declared on <code>IInputProvider</code> returns the 
	 * objects for which the viewer is currently showing properties. 
	 * It returns an <code>Object[]</code> or <code>null</code>.
	 */
	public Object getInput() {
		return input;
	}
	/**
	 * Returns the root entry for this property sheet viewer.
	 * The root entry is not visible in the viewer.
	 * 
	 * @return the root entry or <code>null</code>.
	 */
	public IPropertySheetEntry getRootEntry() {
		return rootEntry;
	}
	/**
	 * The <code>PropertySheetViewer</code> implementation of this 
	 * <code>ISelectionProvider</code> method
	 * returns the result as a <code>StructuredSelection</code>.
	 * <p>
	 * Note that this method only includes <code>IPropertySheetEntry</code>
	 * in the selection (no categories).
	 * </p>
	 */
	public ISelection getSelection() {
		if (tableTree.getSelectionCount() == 0)
			return StructuredSelection.EMPTY;
		TableTreeItem[] sel = tableTree.getSelection();
		List entries = new ArrayList(sel.length);
		for (int i = 0; i < sel.length; i++) {
			TableTreeItem ti = sel[i];
			Object data = ti.getData();
			if (data instanceof IPropertySheetEntry)
				entries.add(data);
		}
		return new StructuredSelection(entries);
	}
	/**
	 * Selection in the viewer occurred. 
	 * Check if there is an active cell editor. 
	 * If yes, deactivate it and check if a new cell editor must be activated.
	 *
	 * @param selection the TableTreeItem that is selected
	 */
	private void handleSelect(TableTreeItem selection) {
		// deactivate the current cell editor
		if (cellEditor != null) {
			applyEditorValue();
			deactivateCellEditor();
		}

		// get the new selection
		TableTreeItem[] sel = new TableTreeItem[] {selection};
		if (sel.length == 0) {
			setMessage(null);
			setErrorMessage(null);
		} else {
			Object object = sel[0].getData(); // assume single selection
			if (object instanceof IPropertySheetEntry) {
				// get the entry for this item
				IPropertySheetEntry activeEntry = (IPropertySheetEntry) object;

				// display the description for the item
				setMessage(activeEntry.getDescription());

				// activate a cell editor on the selection
				activateCellEditor(sel[0]);
			}
		}
		entrySelectionChanged();
	}
	/**
	 * The expand icon for a node in this viewer has been
	 * selected to collapse a subtree. Deactivate the cell editor
	 *
	 * @param event the SWT tree event
	 */
	private void handleTreeCollapse(TreeEvent event) {
		if (cellEditor != null) {
			applyEditorValue();
			deactivateCellEditor();
		}
	}
	/**
	 * The expand icon for a node in this viewer has been
	 * selected to expand the subtree. Create the children 
	 * 1 level deep.
	 * <p>
	 * Note that we use a "dummy" item (no user data) to show a "+"
	 * icon beside an item which has children before the item is expanded
	 * now that it is being expanded we have to create the real child
	 * items
	 * </p>
	 *
	 * @param event the SWT tree event
	 */
	private void handleTreeExpand(TreeEvent event) {
		createChildren(event.item);
	}
	/**
	 * Hides the categories.
	 */
	/*package*/
	void hideCategories() {
		isShowingCategories = false;
		categories = null;
		refresh();
	}

	/**
	 * Establish this viewer as a listener on the control
	 */
	private void hookControl() {
		// Handle selections in the TableTree
		// Part1: Double click only (allow traversal via keyboard without
		// activation
		tableTree.addSelectionListener(new SelectionAdapter() {
			public void widgetDefaultSelected(SelectionEvent e) {
				handleSelect((TableTreeItem)e.item);
			}
		});
		// Part2: handle single click activation of cell editor
		tableTree.getTable().addMouseListener(new MouseAdapter() {
			public void mouseDown(MouseEvent event) {
				// only activate if there is a cell editor
				Point pt = new Point (event.x, event.y);
				TableTreeItem item = tableTree.getItem(pt);
				if (item != null) {
					handleSelect(item);
				}				
			}
		});
		
		// Add a tree listener to expand and collapse which
		// allows for lazy creation of children
		tableTree.addTreeListener(new TreeListener() {
			public void treeExpanded(final TreeEvent event) {
				handleTreeExpand(event);
			}
			public void treeCollapsed(final TreeEvent event) {
				handleTreeCollapse(event);
			}
		});

		// Refresh the table when F5 pressed
		tableTree.getTable().addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				if (e.character == SWT.ESC)
					deactivateCellEditor();
				else if (e.keyCode == SWT.F5)
					// The following will simulate a reselect
					setInput(getInput());
			}
		});
	}
	/**
	 * Updates all of the items in the tree.
	 * <p>
	 * Note that this means ensuring that the tree items reflect the state
	 * of the model (entry tree) it does not mean telling the model to update 
	 * itself.
	 * </p>
	 */
	public void refresh() {
		if(isCellEditorActivated()) deactivateCellEditor();
		if (rootEntry != null) {
			tableTree.setRedraw(false);
			updateChildrenOf(rootEntry, tableTree);
			tableTree.setRedraw(true);
		}
	}
	/**
	 * Removes the given cell editor activation listener 
	 * from this viewer.
	 * Has no effect if an identical activation listener 
	 * is not registered.
	 *
	 * @param listener a cell editor activation listener
	 */
	/*package*/
	void removeActivationListener(ICellEditorActivationListener listener) {
		activationListeners.remove(listener);
	}
	/**
	 * Remove the given item from the table tree.
	 * Remove our listener if the item's user data is a
	 * an entry then set the user data to null
	 *
	 * @param item the item to remove
	 */
	private void removeItem(TableTreeItem item) {
		Object data = item.getData();
		if (data instanceof IPropertySheetEntry)
			 ((IPropertySheetEntry) data).removePropertySheetEntryListener(entryListener);
		item.setData(null);
		item.dispose();
	}
	/**
	 * Reset the selected properties to their default values.
	 */
	public void resetProperties() {
		// Determine the selection
		IStructuredSelection selection = (IStructuredSelection) getSelection();

		// Iterate over entries and reset them
		Iterator enumerator = selection.iterator();
		while (enumerator.hasNext())
			 ((IPropertySheetEntry) enumerator.next()).resetPropertyValue();
	}
	/**
	 * Sets the error message to be displayed in the status line.
	 *
	 * @param errorMessage the message to be displayed, or <code>null</code>
	 */
	private void setErrorMessage(String errorMessage) {
		// show the error message
		if (statusLineManager != null)
			statusLineManager.setErrorMessage(errorMessage);
	}
	/**
	 * The <code>PropertySheetViewer</code> implementation of this
	 * method declared on <code>Viewer</code> method sets the objects 
	 * for which the viewer is currently showing properties. 
	 * <p>
	 * The input must be an <code>Object[]</code> or <code>null</code>.
	 * </p>
	 * @param newInput the input of this viewer, or <code>null</code> if none
	 */
	public void setInput(Object newInput) {
		// need to save any changed value when user clicks elsewhere
		applyEditorValue();
		// deactivate our cell editor
		deactivateCellEditor();

		// set the new input to the root entry
		input = (Object[]) newInput;
		if (input == null)
			input = new Object[0];

		if (rootEntry != null) {
			tableTree.setRedraw(false);
			rootEntry.setValues(input);
			// ensure first level children are visible
			updateChildrenOf(rootEntry, tableTree);
			tableTree.setRedraw(true);
		}
	}
	/**
	 * Sets the message to be displayed in the status line. This message
	 * is displayed when there is no error message.
	 *
	 * @param message the message to be displayed, or <code>null</code>
	 */
	private void setMessage(String message) {
		// show the message
		if (statusLineManager != null)
			statusLineManager.setMessage(message);
	}
	/**
	 * Sets the root entry for this property sheet viewer.
	 * The root entry is not visible in the viewer.
	 * 
	 * @param root the root entry
	 */
	public void setRootEntry(IPropertySheetEntry root) {
		// If we have a root entry, remove our entry listener
		if (rootEntry != null)
			rootEntry.removePropertySheetEntryListener(entryListener);

		rootEntry = root;

		// Set the root as user data on the tableTree
		tableTree.setData(rootEntry);

		// Add an IPropertySheetEntryListener to listen for entry change notifications
		rootEntry.addPropertySheetEntryListener(entryListener);

		// Pass our input to the root, this will trigger entry change
		// callbacks to update this viewer
		setInput(input);
	}
	/**
	 * The <code>PropertySheetViewer</code> implementation of this 
	 * <code>Viewer</code> method does nothing.
	 */
	public void setSelection(ISelection selection, boolean reveal) {
	}
	/**
	 * Sets the status line manager this view will use to show messages.
	 *
	 * @param manager the status line manager
	 */
	public void setStatusLineManager(IStatusLineManager manager) {
		statusLineManager = manager;
	}
	/**
	 * Shows the categories.
	 */
	/*package*/
	void showCategories() {
		isShowingCategories = true;
		refresh();
	}
	/**
	 * Updates the categories.
	 * Reuses old categories if possible.
	 */
	private void updateCategories() {
		// lazy initialize
		if (categories == null)
			categories = new PropertySheetCategory[0];

		// get all the filtered child entries of the root
		List childEntries = getFilteredEntries(rootEntry.getChildEntries());

		// if the list is empty, just set an empty categories array
		if (childEntries.size() == 0) {
			categories = new PropertySheetCategory[0];
			return;
		}

		// cache old categories by their descriptor name
		Map categoryCache = new HashMap(categories.length * 2 + 1);
		for (int i = 0; i < categories.length; i++) {
			categories[i].removeAllEntries();
			categoryCache.put(categories[i].getCategoryName(), categories[i]);
		}

		// create a list of categories to get rid of
		List categoriesToRemove = new ArrayList(Arrays.asList(categories));

		// Determine the categories
		PropertySheetCategory misc = (PropertySheetCategory) categoryCache.get(MISCELLANEOUS_CATEGORY_NAME);
		if (misc == null)
			misc = new PropertySheetCategory(MISCELLANEOUS_CATEGORY_NAME);
		boolean addMisc = false;

		for (int i = 0; i < childEntries.size(); i++) {
			IPropertySheetEntry childEntry = (IPropertySheetEntry) childEntries.get(i);
			String categoryName = childEntry.getCategory();
			if (categoryName == null) {
				misc.addEntry(childEntry);
				addMisc = true;
				categoriesToRemove.remove(misc);
			} else {
				PropertySheetCategory category = (PropertySheetCategory) categoryCache.get(categoryName);
				if (category == null) {
					category = new PropertySheetCategory(categoryName);
					categoryCache.put(categoryName, category);
				} else {
					categoriesToRemove.remove(category);
				}
				category.addEntry(childEntry);
			}
		}

		// Add the PSE_MISC category if it has entries
		if (addMisc)
			categoryCache.put(MISCELLANEOUS_CATEGORY_NAME, misc);

		// Sort the categories	
		List list = new ArrayList(categoryCache.values());
		for (int i = 0; i < categoriesToRemove.size(); i++)
			list.remove(categoriesToRemove.get(i));
		Collections.sort(list, new Comparator() {
			Collator coll = Collator.getInstance(Locale.getDefault());
			public int compare(Object a, Object b) {
				PropertySheetCategory c1, c2;
				String dname1, dname2;
				c1 = (PropertySheetCategory) a;
				dname1 = c1.getCategoryName();
				c2 = (PropertySheetCategory) b;
				dname2 = c2.getCategoryName();
				return coll.compare(dname1, dname2);
			}
		});

		categories = (PropertySheetCategory[]) list.toArray(new PropertySheetCategory[list.size()]);
	}
	/**
	 * Update the category (but not its parent or children).
	 *
	 * @param node the category to update
	 * @param item the tree item for the given entry
	 */
	private void updateCategory(PropertySheetCategory category, TableTreeItem item) {
		// ensure that backpointer is correct
		item.setData(category);

		// Update the name and value columns
		item.setText(0, category.getCategoryName());
		item.setText(1, ""); //$NON-NLS-1$

		// update the "+" icon	
		if (category.getAutoExpand()) {
			// we auto expand categories when they first appear
			createChildren(item);
			item.setExpanded(true);
			category.setAutoExpand(false);
		} else {
			// we do not want to auto expand categories if the user has collpased them
			updatePlus(category, item);
		}
	}
	/**
	 * Update the child entries or categories of the given entry or category.
	 * If the given node is the root entry and we are showing categories 
	 * then the child entries are categories, otherwise they are entries.
	 *
	 * @param node the entry or category whose children we will update
	 * @param widget the widget for the given entry, either a
	 *  <code>TableTree</code> if the node is the root node or a
	 *  <code>TableTreeItem</code> otherwise.
	 */
	private void updateChildrenOf(Object node, Widget widget) {
		// cast the entry or category	
		IPropertySheetEntry entry = null;
		PropertySheetCategory category = null;
		if (node instanceof IPropertySheetEntry)
			entry = (IPropertySheetEntry) node;
		else
			category = (PropertySheetCategory) node;

		// get the current child table tree items
		TableTreeItem item = null;
		TableTreeItem[] childItems;
		if (node == rootEntry) {
			childItems = tableTree.getItems();
		} else {
			item = (TableTreeItem) widget;
			childItems = item.getItems();
		}

		// optimization! prune collapsed subtrees
		if (item != null && !item.getExpanded()) {
			// remove all children
			for (int i = 0; i < childItems.length; i++) {
				if (childItems[i].getData() != null) {
					removeItem(childItems[i]);
				}
			}

			// append a dummy if necessary
			if ((category != null || entry.hasChildEntries()) && childItems.length == 0) // may already have a dummy
				// its is either a category (which always has at least one child)
				// or an entry with chidren.
				// Note that this test is not perfect, if we have filtering on
				// then there in fact may be no entires to show when the user
				// presses the "+" expand icon. But this is an acceptable compromise.
				new TableTreeItem(item, SWT.NULL);

			return;
		}

		// get the child entries or categories
		if (node == rootEntry && isShowingCategories)
			// update the categories
			updateCategories();
		List children = getChildren(node);

		// remove items
		Set set = new HashSet(childItems.length * 2 + 1);

		for (int i = 0; i < childItems.length; i++) {
			Object data = childItems[i].getData();
			if (data != null) {
				Object e = data;
				int ix = children.indexOf(e);
				if (ix < 0) { // not found
					removeItem(childItems[i]);
				} else { // found
					set.add(e);
				}
			} else if (data == null) { // the dummy
				item.dispose();
			}
		}

		// WORKAROUND
		int oldCnt = -1;
		if (widget == tableTree)
			oldCnt = tableTree.getItemCount();

		// add new items
		int newSize = children.size();
		for (int i = 0; i < newSize; i++) {
			Object el = children.get(i);
			if (!set.contains(el))
				createItem(el, widget, i);
		}

		// WORKAROUND
		if (widget == tableTree && oldCnt == 0 && tableTree.getItemCount() == 1) {
			tableTree.setRedraw(false);
			tableTree.setRedraw(true);
		}

		// get the child table tree items after our changes
		if (entry == rootEntry)
			childItems = tableTree.getItems();
		else
			childItems = item.getItems();

		// update the child items
		// This ensures that the children are in the correct order
		// are showing the correct values.
		for (int i = 0; i < newSize; i++) {
			Object el = children.get(i);
			if (el instanceof IPropertySheetEntry)
				updateEntry((IPropertySheetEntry) el, childItems[i]);
			else {
				updateCategory((PropertySheetCategory) el, childItems[i]);
				updateChildrenOf((PropertySheetCategory) el, childItems[i]);
			}
		}
		// The tree's original selection may no longer apply after the update,
		// so fire the selection changed event.
		entrySelectionChanged();
	}
	/**
	 * Update the given entry (but not its children or parent)
	 *
	 * @param node the entry we will update
	 * @param item the tree item for the given entry
	 */
	private void updateEntry(IPropertySheetEntry entry, TableTreeItem item) {
		// ensure that backpointer is correct
		item.setData(entry);

		// update the name and value columns
		item.setText(0, entry.getDisplayName());
		item.setText(1, entry.getValueAsString());
		Image image = entry.getImage();
		if (image != null)
			item.setImage(1, image);

		// update the "+" icon	
		updatePlus(entry, item);
	}
	/**
	 * Updates the "+"/"-" icon of the tree item from the given entry
	 * or category.
	 *
	 * @parem node the entry or category
	 * @param item the table tree item being updated
	 */
	private void updatePlus(Object node, TableTreeItem item) {
		// cast the entry or category	
		IPropertySheetEntry entry = null;
		PropertySheetCategory category = null;
		if (node instanceof IPropertySheetEntry)
			entry = (IPropertySheetEntry) node;
		else
			category = (PropertySheetCategory) node;

		boolean hasPlus = item.getItemCount() > 0;
		boolean needsPlus = category != null || entry.hasChildEntries();
		boolean removeAll = false;
		boolean addDummy = false;

		if (hasPlus != needsPlus) {
			if (needsPlus) {
				addDummy = true;
			} else {
				removeAll = true;
			}
		}
		if (removeAll) {
			// remove all children
			TableTreeItem[] items = item.getItems();
			for (int i = 0; i < items.length; i++) {
				removeItem(items[i]);
			}
		}

		if (addDummy) {
			new TableTreeItem(item, SWT.NULL); // append a dummy to create the plus sign
		}
	}
}
