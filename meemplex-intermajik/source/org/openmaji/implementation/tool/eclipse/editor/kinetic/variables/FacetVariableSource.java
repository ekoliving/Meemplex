/*
 * @(#)FacetVariableSource.java
 * Created on 5/10/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic.variables;

import org.openmaji.implementation.intermajik.model.ValueBag;
import org.openmaji.implementation.tool.eclipse.client.variables.IVariableSource;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.Facet;


/**
 * <code>FacetVariableSource</code>.
 * <p>
 * @author Kin Wong
 */
public class FacetVariableSource implements IVariableSource {
	//private Facet facet;
	
	public FacetVariableSource(Facet facet) {
		//this.facet = facet;
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.client.IVariableSource#extract()
	 */
	public ValueBag extractAll() {
		ValueBag bag = new ValueBag();
		return bag;
	}

	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.client.IVariableSource#merge(org.openmaji.implementation.tool.eclipse.client.ValueBag)
	 */
	public boolean merge(ValueBag bag) {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.client.variables.IVariableSource#extract(java.lang.Object)
	 */
	public boolean extract(Object key, ValueBag bag) {
		return false;	
	}
}
