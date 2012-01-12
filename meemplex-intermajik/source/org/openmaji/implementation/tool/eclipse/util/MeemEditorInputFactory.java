/*
 * @(#)MeemEditorInputFactory.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.tool.eclipse.util;


import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.IElementFactory;
import org.eclipse.ui.IMemento;
import org.openmaji.meem.MeemPath;
import org.openmaji.meem.space.Space;

/**
 * <p>
 * ...
 * </p>
 * @author  mg
 * @version 1.0
 */
public class MeemEditorInputFactory implements IElementFactory {

	private static final String ID_FACTORY = "org.openmaji.implementation.tool.eclipse.util.MeemEditorInputFactory";

	private static final String TAG_PATH = "location";

	/**
	 * @see org.eclipse.ui.IElementFactory#createElement(org.eclipse.ui.IMemento)
	 */
	public IAdaptable createElement(IMemento memento) {
		// Get the file name.
		String location = memento.getString(TAG_PATH);
		if (location == null)
			return null;

		return new MeemEditorInput(MeemPath.spi.create(Space.MEEMSTORE, location));
	}

	public static String getFactoryId() {
		return ID_FACTORY;
	}
	
	public static void saveState(IMemento memento, MeemEditorInput input) {
		MeemPath meemPath = input.getMeemPath();
		memento.putString(TAG_PATH, meemPath.getLocation());
	}

}
