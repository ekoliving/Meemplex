/*
 * @(#)RequestStateProvider.java
 * Created on 4/06/2004
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.common.edit;

/**
 * <code>RequestStateProvider</code>.
 * <p>
 * @author Kin Wong
 */
public interface RequestStateProvider {
	Object getRequestState(Object type);
}
