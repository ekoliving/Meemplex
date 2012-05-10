/*
 * @(#)VariableSourceFactory.java
 * Created on 5/10/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic.variables;

import org.openmaji.implementation.tool.eclipse.client.variables.IVariableSource;
import org.openmaji.implementation.tool.eclipse.client.variables.IVariableSourceFactory;
import org.openmaji.implementation.tool.eclipse.editor.common.model.ConnectionElement;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.Facet;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.Meem;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.Wedge;

/**
 * <code>VariableSourceFactory</code>.
 * <p>
 * @author Kin Wong
 */
public class VariableSourceFactory implements IVariableSourceFactory {
	static VariableSourceFactory factory = new VariableSourceFactory();
	
	static public VariableSourceFactory getInstance() {
		return factory;
	}

	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.client.variables.IVariableSourceFactory#createVariableSource(java.lang.Object)
	 */
	public IVariableSource createVariableSource(Object model) {
		if(model instanceof Meem) {
			return new MeemVariableSource((Meem)model);
		}
		else
		if(model instanceof Wedge) {
			return new WedgeVariableSource((Wedge)model);
		}
		else
		if(model instanceof Facet) {
			return new FacetVariableSource((Facet)model);
		}
		else
		if(model instanceof ConnectionElement) {
			return new ConnectionVariableSource((ConnectionElement)model);
		}
		else
		return null;
	}
}
