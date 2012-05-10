/*
 * @(#)PropertiesMessages.java
 * Created on 28/04/2004
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.properties;

import org.eclipse.osgi.util.NLS;

/**
 * <code>PropertiesMessages</code>.
 * <p>
 * @author Kin Wong
 */
public class PropertiesMessages extends NLS {
	private static final String BUNDLE_NAME= "org.openmaji.implementation.tool.eclipse.properties.messages"; //$NON-NLS-1$

	public static String PropertyViewer_property;
	public static String PropertyViewer_value;
	public static String PropertyViewer_misc;
	
	static {
		NLS.initializeMessages(BUNDLE_NAME, PropertiesMessages.class);
	}

	private PropertiesMessages() {
		// prevent instantiation of class
	}
}
