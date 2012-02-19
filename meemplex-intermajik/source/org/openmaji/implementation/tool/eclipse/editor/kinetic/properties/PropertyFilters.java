/*
 * @(#)PropertyFilters.java
 * Created on 29/04/2004
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic.properties;

/**
 * <code>PropertyFilters</code>.
 * <p>
 * @author Kin Wong
 */
public interface PropertyFilters {
	static final String FILTER_PRESENTATION = "Presentation";
	static final String FILTER_DEFINITION = "Definition";
	static final String FILTER_CONFIGURATION = "Configuration";
	static final String FILTER_DIAGNOSIS = "Diagnosis";
	
	static final String[] STRINGS_FILTER_PRESENTATION = new String[]{FILTER_PRESENTATION};
	static final String[] STRINGS_FILTER_DEFINITION = new String[]{FILTER_DEFINITION};
	static final String[] STRINGS_FILTER_CONFIGURATION = new String[]{FILTER_CONFIGURATION};
	static final String[] STRINGS_FILTER_DIAGNOSIS = new String[]{FILTER_DIAGNOSIS};
}
