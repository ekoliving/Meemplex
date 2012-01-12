/*
 * @(#)PropertyChangeToVariableMapElementEditPolicy.java
 * Created on 22/10/2003
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic.edit;


import org.openmaji.common.VariableMap;
import org.openmaji.implementation.tool.eclipse.client.variables.*;
import org.openmaji.implementation.tool.eclipse.editor.common.model.ElementContainer;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.Diagram;


/**
 * <code>PropertyChangeToVariableMapElementEditPolicy</code>.
 * <p>
 * @author Kin Wong
 */
public class PropertyChangeToVariableMapElementEditPolicy
	extends PropertyChangeToVariableMapEditPolicy {

	/**
	 * Constructs an instance of <code>PropertyChangeToVariableMapElementEditPolicy</code>.
	 * <p>
	 * @param source
	 */
	public PropertyChangeToVariableMapElementEditPolicy(IVariableSource source) {
		super(source);
	}

	public VariableMap getVariableMap() {
		ElementContainer root = getElementModel().getRoot();
		if(root instanceof Diagram) {
			return ((Diagram)root).getProxy().getVariableMapProxy();
		}
		return null;
	}

}
