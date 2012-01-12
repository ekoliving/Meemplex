/*
 * @(#)ITableColumnProvider.java
 * Created on 14/05/2004
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.browser.patterns.common;

import org.eclipse.swt.widgets.Table;

/**
 * <code>ITableColumnProvider</code>.
 * <p>
 * @author Kin Wong
 */
public interface ITableColumnProvider {
	void provideColumns(Table table);
}
