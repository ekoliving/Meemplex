/*
 * @(#)PropertySheetCategory.java
 * Created on 28/04/2004
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.properties;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.ui.views.properties.IPropertySheetEntry;

/**
 * <code>PropertySheetCategory</code>.
 * <p>
 * @author Kin Wong
 */
public class PropertySheetCategory {
	private String categoryName;
	private List entries = new ArrayList();
	private boolean shouldAutoExpand = true;
/**
 * Create a PropertySheet category with name.
 */
public PropertySheetCategory(String name) {
	categoryName = name;
}
/**
 * Add an <code>IPropertySheetEntry</code> to the list
 * of entries in this category. 
 */
public void addEntry(IPropertySheetEntry entry) {
	entries.add(entry);
}
/**
 * Return the category name.
 */
public String getCategoryName() {
	return categoryName;
}
/**
 * Returns <code>true</code> if this category should be automatically 
 * expanded. The default value is <code>true</code>.
 * 
 * @return <code>true</code> if this category should be automatically 
 * expanded, <code>false</code> otherwise
 */
public boolean getAutoExpand() {
	return shouldAutoExpand;
}
/**
 * Sets if this category should be automatically 
 * expanded.
 */
public void setAutoExpand(boolean autoExpand) {
	shouldAutoExpand = autoExpand;
}
/**
 * Returns the entries in this category.
 *
 * @return the entries in this category
 */
public IPropertySheetEntry[] getChildEntries() {
	return (IPropertySheetEntry[])entries.toArray(new IPropertySheetEntry[entries.size()]);
}
/**
 * Removes all of the entries in this category.
 * Doing so allows us to reuse this category entry.
 */
public void removeAllEntries() {
	entries = new ArrayList();
}
}
