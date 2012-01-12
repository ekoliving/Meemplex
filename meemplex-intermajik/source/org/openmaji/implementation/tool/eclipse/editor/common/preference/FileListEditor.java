/*
 * @(#)FileListEditor.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.tool.eclipse.editor.common.preference;

import java.io.File;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.eclipse.jface.preference.ListEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;

/**
 * @author mg
 * Created on 14/01/2003
 */
public class FileListEditor extends ListEditor {

	/**
	 * The last path, or <code>null</code> if none.
	 */
	private String lastPath;

	/**
	 * Creates a new file list field editor 
	 */
	protected FileListEditor() {
	}
	
	/**
	 * Creates a file field editor.
	 * 
	 * @param name the name of the preference this field editor works on
	 * @param labelText the label text of the field editor
	 * @param parent the parent of the field editor's control
	 */
	public FileListEditor(String name, String labelText,  Composite parent) {
		init(name, labelText);
		createControl(parent);
	}
	
	/* (non-Javadoc)
	 * Method declared on ListEditor.
	 * Creates a single string from the given array by separating each
	 * string with the appropriate OS-specific path separator.
	 */
	protected String createList(String[] items) {
		StringBuffer path = new StringBuffer(""); 

		for (int i = 0; i < items.length; i++) {
			path.append(items[i]);
			path.append(File.pathSeparator);
		}
		return path.toString();
	}
	
	protected String getNewInputObject() {

		FileDialog dialog = new FileDialog(getShell());
		
		if (lastPath != null) {
			if (new File(lastPath).exists())
				dialog.setFilterPath(lastPath);
		}
		
		dialog.setFilterExtensions(new String[]{"*.jar"});
		
		String file = dialog.open();
		if (file != null) {
			
			file = file.trim();
			
			if (file.length() == 0)
				return null;
			
			lastPath = file;
		}
		return file;
	}
	
	/* (non-Javadoc)
	 * Method declared on ListEditor.
	 */
	protected String[] parseString(String stringList) {
		StringTokenizer st = new StringTokenizer(stringList, File.pathSeparator + "\n\r");
		ArrayList v = new ArrayList();
		while (st.hasMoreElements()) {
			v.add(st.nextElement());
		}
		return (String[]) v.toArray(new String[v.size()]);
	}
}
