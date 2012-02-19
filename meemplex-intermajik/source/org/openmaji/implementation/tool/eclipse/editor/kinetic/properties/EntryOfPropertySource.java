/*
 * @(#)EntryOfPropertySource.java
 * Created on 24/09/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic.properties;

import org.eclipse.gef.commands.Command;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.Messages;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.commands.CategoryEntryRenameCommand;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.Category;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.EntryOf;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.Meem;


/**
 * <code>EntryOfPropertySource</code>.
 * <p>
 * @author Kin Wong
 */
public class EntryOfPropertySource implements IPropertySource {
	static private PropertyDescriptor[] descriptors;
	static {
		descriptors = new PropertyDescriptor[] {
			new TextPropertyDescriptor(EntryOf.ID_NAME, Messages.Property_EntryOf_EntryName_Label)
		};
	}
	private EntryOf entryOf;
	
	/**
	 * Constructs an instance of <code>EntryOfPropertySource</code>.
	 * <p>
	 * @param entryOf An entry-of object to be associated with this property 
	 * source.
	 */
	public EntryOfPropertySource(EntryOf entryOf) {
		this.entryOf = entryOf;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#getEditableValue()
	 */
	public Object getEditableValue() {
		return this;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyDescriptors()
	 */
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return descriptors;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyValue(java.lang.Object)
	 */
	public Object getPropertyValue(Object id) {
		if(EntryOf.ID_NAME.equals(id)) {
			return entryOf.getName();
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#isPropertySet(java.lang.Object)
	 */
	public boolean isPropertySet(Object id) {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#resetPropertyValue(java.lang.Object)
	 */
	public void resetPropertyValue(Object id) {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#setPropertyValue(java.lang.Object, java.lang.Object)
	 */
	public void setPropertyValue(Object id, Object value) {
		if(EntryOf.ID_NAME.equals(id)) {
			Category category = entryOf.getCategory();
			Meem meem = entryOf.getMeem();
			if((category != null) && (meem != null)) {
				String entryName = (String)value;
				Command command = 
					new CategoryEntryRenameCommand(
						category.getProxy().getCategoryProxy(), 
						meem.getMeemPath(), 
						entryOf.getName(),
						entryName);
				entryOf.setName(entryName);
				command.execute();
			}
		}
	}
}
