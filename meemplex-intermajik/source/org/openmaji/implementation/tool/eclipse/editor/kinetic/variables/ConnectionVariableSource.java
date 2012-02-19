/*
 * @(#)ConnectionVariableSource.java
 * Created on 5/10/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic.variables;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Vector;

import org.openmaji.implementation.intermajik.model.ValueBag;
import org.openmaji.implementation.tool.eclipse.client.variables.IVariableSource;
import org.openmaji.implementation.tool.eclipse.editor.common.model.ConnectionElement;


/**
 * <code>ConnectionVariableSource</code>.
 * <p>
 * @author Kin Wong
 */
public class ConnectionVariableSource implements IVariableSource {
	private ConnectionElement connection;
	
	public ConnectionVariableSource(ConnectionElement connection) {
		this.connection = connection;
	}
	
	protected ConnectionElement getConnection() {
		return  connection;
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.client.IVariableSource#extract()
	 */
	public ValueBag extractAll() {
		ValueBag bag = new ValueBag();
		extract(ConnectionElement.ID_BENDPOINT, bag);
		return bag;
	}

	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.client.IVariableSource#merge(org.openmaji.implementation.tool.eclipse.client.ValueBag)
	 */
	public boolean merge(ValueBag bag) {
		boolean merged = false;
		Iterator<Serializable> it = bag.getIds();
		while(it.hasNext()) {
			Serializable id = it.next();
			if(ConnectionElement.ID_BENDPOINT.equals(id)) {
				Vector bendpoints = (Vector)bag.get(id);
				getConnection().setBendpoints(bendpoints);
				merged = true;
			}
		}
		return merged;
	}

	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.client.variables.IVariableSource#extract(java.lang.Object)
	 */
	public boolean extract(Object key, ValueBag bag) {
		if(ConnectionElement.ID_BENDPOINT.equals(key)) {
			Vector bendpoints = new Vector(getConnection().getBendpoints());
			bag.add(ConnectionElement.ID_BENDPOINT, bendpoints);
			return true;
		}
		else
		return false;
	}
}
