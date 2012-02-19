/*
 * @(#)LayoutManager.java
 * Created on 10/12/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.features.layout;

/**
 * <code>LayoutManager</code>.
 * <p>
 * @author Kin Wong
 */
public interface LayoutManager {
	void invalidateModel(Object object);
	void Layout();
}
