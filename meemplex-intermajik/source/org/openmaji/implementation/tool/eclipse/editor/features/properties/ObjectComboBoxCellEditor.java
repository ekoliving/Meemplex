/*
 * @(#)ObjectComboBoxCellEditor.java
 * Created on 16/09/2003
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.features.properties;

import java.text.MessageFormat;

import org.eclipse.jface.util.Assert;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * <code>ObjectComboBoxCellEditor</code>.
 * <p>
 * @author Kin Wong
 */
public class ObjectComboBoxCellEditor extends CellEditor {
	/**
	 * The list of items to present in the combo box.
	 */
	private Object[] items;

	/**
	 * The zero-based index of the selected item.
	 */
	private int selection;

	/**
	 * The custom combo box control.
	 */
	private CCombo comboBox;

	/**
	 * Default ComboBoxCellEditor style
	 */
	private static final int defaultStyle = SWT.NONE;

/**
 * Creates a new cell editor with no control and no  st of choices. Initially,
 * the cell editor has no cell validator.
 * 
 * @since 2.1
 */
public ObjectComboBoxCellEditor() {
	setStyle(defaultStyle);
}

/**
 * Creates a new cell editor with a combo containing the given 
 * list of choices and parented under the given control. The cell
 * editor value is the zero-based index of the selected item.
 * Initially, the cell editor has no cell validator and
 * the first item in the list is selected. 
 *
 * @param parent the parent control
 * @param items the list of strings for the combo box
 */
public ObjectComboBoxCellEditor(Composite parent, Object[] items) {
	this(parent, items, defaultStyle);
}

/**
 * Creates a new cell editor with a combo containing the given 
 * list of choices and parented under the given control. The cell
 * editor value is the zero-based index of the selected item.
 * Initially, the cell editor has no cell validator and
 * the first item in the list is selected. 
 *
 * @param parent the parent control
 * @param items the list of strings for the combo box
 * @param style the style bits
 * @since 2.1
 */
public ObjectComboBoxCellEditor(Composite parent, Object[] items, int style) {
	super(parent, style);
	setItems(items);
}

/**
 * Returns the list of choices for the combo box
 *
 * @return the list of choices for the combo box
 */
public Object[] getItems() {
	return this.items;
}

/**
 * Sets the list of choices for the combo box
 *
 * @param items the list of choices for the combo box
 */
public void setItems(Object[] items) {
	Assert.isNotNull(items);
	this.items = items;
	populateComboBoxItems();
}

/* (non-Javadoc)
 * Method declared on CellEditor.
 */
protected Control createControl(Composite parent) {
	
	comboBox = new CCombo(parent, getStyle());
	comboBox.setFont(parent.getFont());

	comboBox.addKeyListener(new KeyAdapter() {
		// hook key pressed - see PR 14201  
		public void keyPressed(KeyEvent e) {
			keyReleaseOccured(e);
		}
	});

	comboBox.addSelectionListener(new SelectionAdapter() {
		public void widgetDefaultSelected(SelectionEvent event) {
			// must set the selection before getting value
			selection = comboBox.getSelectionIndex();
			Object newValue = doGetValue();
			markDirty();
			boolean isValid = isCorrect(newValue);
			setValueValid(isValid);
			if (!isValid) {
				// try to insert the current value into the error message.
				setErrorMessage(
					MessageFormat.format(getErrorMessage(), new Object[] {items[selection]})); 
			}
			fireApplyEditorValue();
		}
	});

	comboBox.addTraverseListener(new TraverseListener() {
		public void keyTraversed(TraverseEvent e) {
			if (e.detail == SWT.TRAVERSE_ESCAPE || e.detail == SWT.TRAVERSE_RETURN) {
				e.doit = false;
			}
		}
	});

	return comboBox;
}

/**
 * The <code>ComboBoxCellEditor</code> implementation of
 * this <code>CellEditor</code> framework method returns
 * the zero-based index of the current selection.
 *
 * @return the zero-based index of the current selection wrapped
 *  as an <code>Integer</code>
 */
protected Object doGetValue() {
	return items[selection];
}

/* (non-Javadoc)
 * Method declared on CellEditor.
 */
protected void doSetFocus() {
	comboBox.setFocus();
}

/**
 * The <code>ComboBoxCellEditor</code> implementation of
 * this <code>CellEditor</code> framework method sets the 
 * minimum width of the cell.  The minimum width is 10 characters
 * if <code>comboBox</code> is not <code>null</code> or <code>disposed</code>
 * eles it is 60 pixels to make sure the arrow button and some text is visible.
 * The list of CCombo will be wide enough to show its longest item.
 */
public LayoutData getLayoutData() {
	LayoutData layoutData = super.getLayoutData();
	if ((comboBox == null) || comboBox.isDisposed())
		layoutData.minimumWidth = 60;
	else {
		// make the comboBox 10 characters wide
		GC gc = new GC(comboBox);
		layoutData.minimumWidth = (gc.getFontMetrics().getAverageCharWidth() * 10) + 10;
		gc.dispose();
	}
	return layoutData;
}

/**
 * The <code>ComboBoxCellEditor</code> implementation of
 * this <code>CellEditor</code> framework method
 * accepts a zero-based index of a selection.
 *
 * @param value the zero-based index of the selection wrapped
 *   as an <code>Integer</code>
 */
protected void doSetValue(Object value) {
	Assert.isTrue(comboBox != null);
	selection = -1;
	for(int i = 0; i < items.length; i++) {
		if(items[i].equals(value)) {
			selection = i;
			break;
		}
	}
	if(selection == -1) {
		System.err.println("Unable to find Item: " + value);
	}
	comboBox.select(selection);
}

/**
 * Updates the list of choices for the combo box for the current control.
 */
private void populateComboBoxItems() {
	if (comboBox != null && items != null) {
		comboBox.removeAll();
		for (int i = 0; i < items.length; i++)
			comboBox.add(items[i].toString(), i);

		setValueValid(true);
		selection = 0;
	}
}

}
