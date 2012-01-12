/*
 * @(#)WedgeVariableSource.java
 * Created on 5/10/2003
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic.variables;

import java.io.Serializable;
import java.util.Iterator;

import org.openmaji.implementation.intermajik.model.ValueBag;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.Wedge;


/**
 * <code>WedgeVariableSource</code>.
 * <p>
 * @author Kin Wong
 */
public class WedgeVariableSource extends ElementContainerVariableSource {
	private Wedge wedge;
	
	public WedgeVariableSource(Wedge wedge) {
		super(wedge);
		this.wedge = wedge;
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.client.IVariableSource#extract()
	 */
	public ValueBag extractAll() {
		ValueBag bag = new ValueBag();
		extract(Wedge.ID_COLLAPSED, bag);
		extract(Wedge.ID_CHILD_ORDERS, bag);
		return bag;
	}

	public boolean extract(Object key, ValueBag bag) {
		if(key.equals(Wedge.ID_COLLAPSED)) {
			bag.add(Wedge.ID_COLLAPSED, new Boolean(wedge.isCollapsed()));
			return true;
		}
		else {
			return super.extract(key, bag);
		}
	}


	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.client.IVariableSource#merge(org.openmaji.implementation.tool.eclipse.client.ValueBag)
	 */
	public boolean merge(ValueBag bag) {
		boolean changed = false;
		Iterator<Serializable> it = bag.getIds();
		while(it.hasNext()) {
			Serializable id = it.next();
			Serializable value = bag.get(id);
			
			if(Wedge.ID_COLLAPSED.equals(id)) {
				Boolean collapsed = (Boolean)value;
				if(wedge.isCollapsed() != collapsed.booleanValue()) {
					wedge.setCollapse(collapsed.booleanValue());
					changed = true;
				} 
			}
			else
			if(mergeChildOrder(id, value)) {
				changed = true;
			}
		}
		return changed;
	}
}
