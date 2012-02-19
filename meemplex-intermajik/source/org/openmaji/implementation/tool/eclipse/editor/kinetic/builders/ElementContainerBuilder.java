/*
 * @(#)ElementContainerBuilder.java
 * Created on 16/10/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic.builders;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.openmaji.implementation.tool.eclipse.editor.common.model.Element;
import org.openmaji.implementation.tool.eclipse.editor.common.model.ElementContainer;


/**
 * <code>ElementContainerBuilder</code>.
 * <p>
 * @author Kin Wong
 */
abstract public class ElementContainerBuilder extends ElementBuilder {
	
	protected ElementContainerBuilder(ElementContainer container) {
		setModel(container);
	}
	
	protected ElementContainer getContainerModel() {
		return (ElementContainer)getModel();
	}
	
	/**
	 * Removes a child object from the model (build target) together with its 
	 * corresponding child builder.
	 * <p>
	 * @param id The id that identifies the child.
	 */
	protected void removeChild(Object id) {
		removeChildBuilder(id);
		Element element = getContainerModel().findElement(id);
		if(element != null) {
			getContainerModel().removeChild(element);
		}
	}
	
	/**
	 * Creates a map that maps element ids to elements.
	 * <p>
	 * @return Map A map that maps element ids to elements.
	 */
	protected Map createChildElementMap() {
		// Build a temporary map, another left must be removed at the end.
		HashMap tempChildren = new HashMap();
		Iterator it = getContainerModel().getChildren().iterator();
		while(it.hasNext()) {
			Element element = (Element)it.next();
			tempChildren.put(element.getId(), element);
		}
		return tempChildren;
	}
	
	protected Element[] createChildElementArray() {
		return (Element[]) getContainerModel().getChildren().toArray(new Element[0]);
	}
}
