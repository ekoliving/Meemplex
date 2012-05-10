/*
 * @(#)ElementContainerVariableSource.java
 * Created on 8/10/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic.variables;

import java.util.ArrayList;
import java.util.List;

import org.openmaji.implementation.intermajik.model.ValueBag;
import org.openmaji.implementation.tool.eclipse.client.variables.IVariableSource;
import org.openmaji.implementation.tool.eclipse.editor.common.model.Element;
import org.openmaji.implementation.tool.eclipse.editor.common.model.ElementContainer;


/**
 * <code>ElementContainerVariableSource</code>.
 * <p>
 * @author Kin Wong
 */
abstract public class ElementContainerVariableSource implements IVariableSource {
	ElementContainer container;
	
	protected ElementContainerVariableSource(ElementContainer container) {
		this.container = container;
	}

	public boolean extract(Object key, ValueBag value) {
		if(key.equals(ElementContainer.ID_CHILD_ORDERS)) {
			Object[] ids = getChildIds();
			if(ids != null) {
				value.add(ElementContainer.ID_CHILD_ORDERS, ids);
				return true;
			}
		}
		return false;
	}

	protected boolean mergeChildOrder(Object id, Object value) {
		if(!ElementContainer.ID_CHILD_ORDERS.equals(id)) return false;
		Object[] ids = (Object[])value;
		if(!isDifferent(ids)) return false;

		ArrayList elements = new ArrayList();
		for(int i=0; i < ids.length; i++) {
			Element element = container.findElement(ids[i]);
			if(element == null) break;
			elements.add(element);
		}
		container.setSortableItems(elements);
		return true;
	}
	
	private Object[] getChildIds() {
		List children = container.getChildren(); 
		Object[] ids = new Object[children.size()];
		for(int i = 0; i < children.size(); i++) {
			ids[i] = ((Element)children.get(i)).getId();
		}
		return ids;
	}
	
	private boolean isDifferent(Object[] ids) {
		Object[] thisIds = getChildIds();
		if(ids.length != thisIds.length) return true;
		
		for(int i = 0; i < thisIds.length; i++) {
			if(!thisIds[i].equals(ids[i])) return true;
		}
		return false;
	}
}
