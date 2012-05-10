/*
 * @(#)MeemDefinitionPreferencePage.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.tool.eclipse.maji;

import org.eclipse.jface.preference.*;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.openmaji.implementation.tool.eclipse.plugin.MajiPlugin;


/**
 * This class represents a preference page that
 * is contributed to the Preferences dialog. By 
 * subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows
 * us to create a page that is small and knows how to 
 * save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They
 * are stored in the preference store that belongs to
 * the main plug-in class. That way, preferences can
 * be accessed directly via the preference store.
 */

public class MajiSystemPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
	
	public static final String MAJI_LAUNCH_PROPERTIES = "maji_launch_properties";
	public static final String INTERMAJIK_LAUNCH_PROPERTIES = "intermajik_launch_properties";
	public static final String MAJI_USERNAME_PROPERTIES = "maji_username_properties";

	public MajiSystemPreferencePage() {
		super(GRID);
		setPreferenceStore(MajiPlugin.getDefault().getPreferenceStore());
		//setDescription("A demonstration of a preference page implementation");
		initializeDefaults();
	}
	/**
	 * Sets the default values of the preferences.
	 */
	private void initializeDefaults() {
		IPreferenceStore store = getPreferenceStore();
		store.setDefault(MAJI_LAUNCH_PROPERTIES, "");
		store.setDefault(INTERMAJIK_LAUNCH_PROPERTIES, "");
		store.setDefault(MAJI_USERNAME_PROPERTIES, "");
	}

	/**
	 * Creates the field editors. Field editors are abstractions of
	 * the common GUI blocks needed to manipulate various types
	 * of preferences. Each field editor knows how to save and
	 * restore itself.
	 */

	public void createFieldEditors() {
		addField(new FileFieldEditor(MAJI_LAUNCH_PROPERTIES, "Maji configuration file:", true, getFieldEditorParent()));
		addField(new FileFieldEditor(INTERMAJIK_LAUNCH_PROPERTIES, "Intermajik user keystore file:", true, getFieldEditorParent()));
	}

	public void init(IWorkbench workbench) {
	}

	public boolean performOk() {
		if (super.performOk()) {
			MajiPlugin.getDefault().savePluginPreferences();
			return true;
		}
		return false;
	}
}