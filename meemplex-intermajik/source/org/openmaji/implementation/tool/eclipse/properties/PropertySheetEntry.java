/*
 * @(#)PropertySheetEntry.java
 * Created on 28/04/2004
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.properties;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellEditorListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySheetEntry;
import org.eclipse.ui.views.properties.IPropertySheetEntryListener;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.IPropertySourceProvider;

/**
 * <code>PropertySheetEntry</code>.
 * <p>
 * @author Kin Wong
 */
public class PropertySheetEntry implements IPropertySheetEntry {

	/**
	* The values we are displaying/editing.
	* These objects repesent the value of one of the
	* properties of the values of our parent entry.
	* Except for the root entry where they represent the
	* input (selected) objects.
	*/
	private Object[] values = new Object[0];
	
	/**
	* The property sources for the values we are displaying/editing.
	*/
	private Map sources = new HashMap(0);

	/**
	 * The value of this entry is defined as the the first object
	 * in its value array or, if that object is an 
	 * <code>IPropertySource</code>, the value it returns when sent
	 * <code>getEditableValue</code>
	 */
	private Object editValue;

	private PropertySheetEntry parent;
	private IPropertySourceProvider propertySourceProvider;
	private IPropertyDescriptor descriptor;
	private CellEditor editor;
	private String errorText;
	private PropertySheetEntry[] childEntries = null;
	private ArrayList listeners = new ArrayList();

	/**
	 * Create the CellEditorListener for this entry. It listens for
	 * value changes in the CellEditor, and cancel and finish requests.
	 */
	private ICellEditorListener cellEditorListener = new ICellEditorListener() {
		public void editorValueChanged(boolean oldValidState, boolean newValidState) {
			if (!newValidState)
				// currently not valid so show an error message
				setErrorText(editor.getErrorMessage());
			else 
				// currently valid 
				setErrorText(null);
		}
		public void cancelEditor() {
			setErrorText(null);
		}
		public void applyEditorValue() {
			PropertySheetEntry.this.applyEditorValue();
		}
	};
/* (non-Javadoc)
 * Method declared on IPropertySheetEntry.
 */
public void addPropertySheetEntryListener(IPropertySheetEntryListener listener) {
	listeners.add(listener);
}
/* (non-Javadoc)
 * Method declared on IPropertySheetEntry.
 */
public void applyEditorValue() {
	if (editor == null) 
		return;
		
	// Check if editor has a valid value
	if (!editor.isValueValid()) {
		setErrorText(editor.getErrorMessage());
		return;
	} else {
		setErrorText(null);
	}
		
	// See if the value changed and if so update
	Object newValue = editor.getValue();
	boolean changed = false;
	if (values.length > 1) {
		changed = true;
	} else if (editValue == null) {
		if (newValue != null)
			changed = true;
	} else if (!editValue.equals(newValue)) 
		changed = true;

	// Set the editor value
	if (changed) 
		setValue(newValue);	
}
/**
 * Return the sorted intersection of all the <code>IPropertyDescriptor</code>s 
 * for the objects.
 */
private List computeMergedPropertyDescriptors() {
	if (values.length == 0)
		return new ArrayList(0);
		
	// get all descriptors from each object
	Map[] propertyDescriptorMaps = new Map[values.length];
	for (int i = 0; i < values.length; i++) {
		Object object = values[i];
		IPropertySource source = getPropertySource(object);
		if (source == null) {
			// if one of the selected items is not a property source
			// then we show no properties
			return new ArrayList(0);
		}
		// get the property descriptors keyed by id
		propertyDescriptorMaps[i] = computePropertyDescriptorsFor(source);
	}

	// intersect
	Map intersection = propertyDescriptorMaps[0];
	for (int i = 1; i < propertyDescriptorMaps.length; i++) {
		// get the current ids
		Object[] ids = intersection.keySet().toArray();
		for (int j = 0; j < ids.length; j++){
			Object object = propertyDescriptorMaps[i].get(ids[j]);
			if (object == null ||
				// see if the descriptors (which have the same id) are compatible
				!((IPropertyDescriptor)intersection.get(ids[j])).isCompatibleWith((IPropertyDescriptor)object))
				intersection.remove(ids[j]);
		}
	}

	// Sort the descriptors	
	List descriptors = new ArrayList(intersection.values());
	Collections.sort(descriptors, new Comparator() {
		Collator coll = Collator.getInstance(Locale.getDefault());
		public int compare(Object a, Object b) {
			IPropertyDescriptor d1, d2;
			String dname1, dname2;
			d1 = (IPropertyDescriptor) a;
			dname1 = d1.getDisplayName();
			d2 = (IPropertyDescriptor) b;
			dname2 = d2.getDisplayName();
			return coll.compare(dname1, dname2);
		}
	});

	return descriptors;
}
/**
 * Returns an map of property descritptors (keyed on id) for the 
 * given property source.
 *
 * @source a property source for which to obtain descriptors
 * @return a table of decriptors keyed on their id
 */
private Map computePropertyDescriptorsFor(IPropertySource source) {
	IPropertyDescriptor[] descriptors = source.getPropertyDescriptors();
	Map result = new HashMap(descriptors.length*2+1);
	for (int i = 0; i < descriptors.length; i++){
		result.put(descriptors[i].getId(), descriptors[i]);
	}
	return result;
}
/**
 * Create our child entries.
 */
private void createChildEntries() {
	// get the current descriptors
	List descriptors = computeMergedPropertyDescriptors();

	// rebuild child entries using old when possible
	childEntries = new PropertySheetEntry[descriptors.size()];
	for (int i = 0; i < descriptors.size(); i++) {
		IPropertyDescriptor d = (IPropertyDescriptor)descriptors.get(i);
		// create new entry
		PropertySheetEntry entry = new PropertySheetEntry();
		entry.setDescriptor(d);
		entry.setParent(this);
		entry.setPropertySourceProvider(propertySourceProvider);
		entry.refreshValues();
		childEntries[i] = entry;
	}
}
/* (non-Javadoc)
 * Method declared on IPropertySheetEntry.
 */
public void dispose() {
	if (editor != null) {
		editor.dispose();
		editor = null;
	}
	// recursive call to dispose children
	if (childEntries != null)
		for (int i = 0; i < childEntries.length; i++) {
			// an error in a property source may cause refreshChildEntries
			// to fail. Since the Workbench handles such errors we
			// can be left in a state where a child entry is null.
			if (childEntries[i] != null)
				childEntries[i].dispose();
		}
}
/**
 * The child entries of this entry have changed 
 * (children added or removed).
 * Notify all listeners of the change.
 */
private void fireChildEntriesChanged() {
	if (listeners == null)
		return;
	Object[] array = listeners.toArray();	
	for (int i = 0; i < array.length; i++) {
		IPropertySheetEntryListener listener = (IPropertySheetEntryListener)array[i];
		listener.childEntriesChanged(this);
	}
}
/**
 * The error message of this entry has changed.
 * Notify all listeners of the change.
 */
private void fireErrorMessageChanged() {
	if (listeners == null)
		return;
	Object[] array = listeners.toArray();	
	for (int i = 0; i < array.length; i++) {
		IPropertySheetEntryListener listener = (IPropertySheetEntryListener)array[i];
		listener.errorMessageChanged(this);
	}
}
/**
 * The values of this entry have changed.
 * Notify all listeners of the change.
 */
private void fireValueChanged() {
	if (listeners == null)
		return;
	Object[] array = listeners.toArray();	
	for (int i = 0; i < array.length; i++) {
		IPropertySheetEntryListener listener = (IPropertySheetEntryListener)array[i];
		listener.valueChanged(this);
	}
}
/* (non-Javadoc)
 * Method declared on IPropertySheetEntry.
 */
public String getCategory() {
	return descriptor.getCategory();
}
/* (non-Javadoc)
 * Method declared on IPropertySheetEntry.
 */
public IPropertySheetEntry[] getChildEntries() {
	if (childEntries == null) 
		createChildEntries();
	return childEntries;
}
/* (non-Javadoc)
 * Method declared on IPropertySheetEntry.
 */
public String getDescription() {
	return descriptor.getDescription();
}
/**
 * Returns the descriptor for this entry.
 */
private IPropertyDescriptor getDescriptor() {
	return descriptor;
}
/* (non-Javadoc)
 * Method declared on IPropertySheetEntry.
 */
public String getDisplayName() {
	return descriptor.getDisplayName();
}
/* (non-Javadoc)
 * Method declared on IPropertySheetEntry.
 */
public CellEditor getEditor(Composite parent) {

	if (editor == null) {
		editor = descriptor.createPropertyEditor(parent);
		if (editor != null) {
			editor.addListener(cellEditorListener);
		}
	}
	if (editor != null) {
		editor.setValue(editValue);
		setErrorText(editor.getErrorMessage());
	}
	return editor;
}
/**
 * Returns the edit value for the object at the given index.
 *
 * @param index the value object index
 * @return the edit value for the object at the given index
 */
protected Object getEditValue(int index) {
	Object value = values[index];
	IPropertySource source = getPropertySource(value);
	if (source != null) {
		value = source.getEditableValue();
	}
	return value;
}
/* (non-Javadoc)
 * Method declared on IPropertySheetEntry.
 */
public String getErrorText() {
	return errorText;
}
/* (non-Javadoc)
 * Method declared on IPropertySheetEntry.
 */
public String getFilters() [] {
	return descriptor.getFilterFlags();
}
/* (non-Javadoc)
 * Method declared on IPropertySheetEntry.
 */
public Object getHelpContextIds() {
	return descriptor.getHelpContextIds();
}
/* (non-Javadoc)
 * Method declared on IPropertySheetEntry.
 */
public Image getImage() {
	ILabelProvider provider = descriptor.getLabelProvider();
	if (provider == null)
		return null;
	return provider.getImage(editValue);
}
/**
 * Returns an property source for the given object.
 *
 * @object an object for which to obtain a property source or
 *  <code>null</code> if a property source is not available
 * @return an property source for the given object
 */
private IPropertySource getPropertySource(Object object) {
	if (sources.containsKey(object))
		return (IPropertySource)sources.get(object);
		
	IPropertySource result = null; 
	if (propertySourceProvider != null)
		result = propertySourceProvider.getPropertySource(object);
	else if (object instanceof IPropertySource) 
		result = (IPropertySource)object;
	else if (object instanceof IAdaptable)
		result = (IPropertySource)((IAdaptable)object).getAdapter(IPropertySource.class);
		
	sources.put(object, result);
	return result;
}
/* (non-Javadoc)
 * Method declared on IPropertySheetEntry.
 */
public String getValueAsString() {
	if (editValue == null)
		return "";//$NON-NLS-1$
	ILabelProvider provider = descriptor.getLabelProvider();
	if (provider == null)
		return editValue.toString();
	return provider.getText(editValue);
}
/**
 * Returns the value objects of this entry.
 */
private Object[] getValues() {
	return values;
}
/* (non-Javadoc)
 * Method declared on IPropertySheetEntry.
 */
public boolean hasChildEntries() {
	if (childEntries != null && childEntries.length > 0)
		return true;
	else
		// see if we could have entires if we were asked
		return computeMergedPropertyDescriptors().size() > 0;
}
/**
 * Update our child entries.
 * This implementation tries to reuse child entries if possible 
 * (if the id of the new descriptor matches the descriptor id of the
 * old entry).
 */
private void refreshChildEntries() {
	if (childEntries == null)
		// no children to refresh
		return;
		
	// get the current descriptors
	List descriptors = computeMergedPropertyDescriptors();

	// cache old entries by their descriptor id
	Map entryCache = new HashMap (childEntries.length*2+1);
	for (int i = 0; i < childEntries.length; i++){
		entryCache.put(childEntries[i].getDescriptor().getId(), childEntries[i]);
	}

	// create a list of entries to dispose
	List entriesToDispose = new ArrayList(Arrays.asList(childEntries));
	
	// rebuild child entries using old when possible
	childEntries = new PropertySheetEntry[descriptors.size()];
	boolean entriesChanged = descriptors.size() != entryCache.size();
	for (int i = 0; i < descriptors.size(); i++) {
		IPropertyDescriptor d = (IPropertyDescriptor)descriptors.get(i);
		// see if we have an entry matching this descriptor
		PropertySheetEntry entry = (PropertySheetEntry)entryCache.get(d.getId());
		if (entry != null) {
			// reuse old entry
				entry.setDescriptor(d);
				entriesToDispose.remove(entry);
		} else {
			// create new entry
			entry = new PropertySheetEntry();
			entry.setDescriptor(d);
			entry.setParent(this);
			entry.setPropertySourceProvider(propertySourceProvider);
			entriesChanged = true;
		}
		entry.refreshValues();
		childEntries[i] = entry;
	}

	if (entriesChanged)
		fireChildEntriesChanged();

	//Dispose of entries which are no longer needed		
	for (int i = 0; i < entriesToDispose.size(); i++) {
		((IPropertySheetEntry)entriesToDispose.get(i)).dispose();		
	}	
}
/**
 * Refresh the entry tree from the root down
 */
private void refreshFromRoot() {
	if (parent == null)
		refreshChildEntries();
	else
		parent.refreshFromRoot();
}
/**
 * Update our value objects.
 * We ask our parent for the property values based on
 * our descriptor.
 */
private void refreshValues() {
	// get our parent's value objects
	Object[] sources = parent.getValues();

	// loop through the objects getting our property value from each
	Object[] newValues = new Object[sources.length];
	for (int i = 0; i < sources.length; i++) {
		IPropertySource source = parent.getPropertySource(sources[i]);
		newValues[i] = source.getPropertyValue(descriptor.getId());
	}

	// set our new values
	setValues(newValues);
}
/* (non-Javadoc)
 * Method declared on IPropertySheetEntry.
 */
public void removePropertySheetEntryListener(IPropertySheetEntryListener listener) {
	listeners.remove(listener);
}
/* (non-Javadoc)
 * Method declared on IPropertySheetEntry.
 */
public void resetPropertyValue() {
	if (parent == null)
		// root does not have a default value
		return;

	//	Use our parent's values to reset our values.
	boolean change = false;
	Object[] objects = parent.getValues();
	for (int i = 0; i < objects.length; i++){
		IPropertySource source = getPropertySource(objects[i]);
		if (source.isPropertySet(descriptor.getId())) {
			source.resetPropertyValue(descriptor.getId());
			change = true;
		}
	}
	if (change)
		refreshFromRoot();
}
/**
 * Set the descriptor.
 */
private void setDescriptor(IPropertyDescriptor newDescriptor) {
	// if our descriptor is changing, we have to get rid
	// of our current editor if there is one
	if (descriptor != newDescriptor && editor != null) {
		editor.dispose();
		editor = null;
	}
	descriptor = newDescriptor;
}
/*
 * Set the error text.  This should be set to null when
 * the current value is valid, otherwise it should be
 * set to a error string
 */
private void setErrorText(String newErrorText) {
	errorText = newErrorText;
	// inform listeners
	fireErrorMessageChanged();
}
/**
 * Sets the parent of the entry. 
 */
private void setParent(PropertySheetEntry p){
	parent = p;
}
/*
 * Sets a property source provider for this entry. 
 * This provider is used to obtain an <code>IPropertySource</code>
 * for each of this entries objects. If no provider is
 * set then a default provider is used.
 */
public void setPropertySourceProvider(IPropertySourceProvider provider) {
	propertySourceProvider = provider;	
}
/**
 * Set the value for this entry.
 * <p>
 * We set the given value as the value for all our value objects.
 * We then call our parent to update the property we represent
 * with the given value.
 * We then trigger a model refresh.
 * <p>
 *
 * @param newValue the new value
 */
private void setValue(Object newValue) {
	// Set the value
	for (int i = 0; i < values.length; i++) {
		values[i] = newValue;
	}

	// Inform our parent
	parent.valueChanged(this);

	// Refresh the model
	refreshFromRoot();	
}
/** 
 * The <code>PropertySheetEntry</code> implmentation of this
 * method declared on<code>IPropertySheetEntry</code> will
 * obtain an editable value for the given objects and update
 * the child entries.
 * <p>
 * Updating the child entries will typically call this method
 * on the child entries and thus the entire entry tree is updated
 * </p>
 * @param objects the new values for this entry
 */
public void setValues(Object[] objects) {
	values = objects;
	sources = new HashMap(values.length * 2 + 1);
	
	if (values.length == 0) 
		editValue = null;
	else {
		// set the first value object as the entry's value
		Object newValue = values[0];

		// see if we should convert the value to an editable value
		IPropertySource source = getPropertySource(newValue);
		if (source != null)
			newValue = source.getEditableValue();
		editValue = newValue;
	}

	// update our child entries
	refreshChildEntries();

	// inform listeners that our value changed
	fireValueChanged();
}
/**
 * The value of the given child entry has changed.
 * Therefore we must set this change into our value objects.
 * <p>
 * We must inform our parent so that it can update its value objects
 * </p>
 * <p>
 * Subclasses may override to set the property value in some
 * custom way.
 * </p>
 */
protected void valueChanged(PropertySheetEntry child) {
	for (int i = 0; i < values.length; i++) {
		IPropertySource source = getPropertySource(values[i]);
		source.setPropertyValue(child.getDescriptor().getId(), child.getEditValue(i));
	}
	
	// inform our parent
	if (parent != null)
		parent.valueChanged(this);
}
}
