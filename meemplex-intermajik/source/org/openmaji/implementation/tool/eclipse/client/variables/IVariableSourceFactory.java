/*
 * @(#)IVariableSourceFactory.java
 * Created on 6/10/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.client.variables;


/**
 * <code>IVariableSourceFactory</code>.
 * <p>
 * @author Kin Wong
 */
public interface IVariableSourceFactory {
	IVariableSource createVariableSource(Object model);
}
