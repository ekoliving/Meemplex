/*
 * @(#)PropertyChangeToVariableMapConnectionEditPolicy.java
 * Created on 22/10/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic.edit;

import org.openmaji.common.VariableMap;
import org.openmaji.implementation.tool.eclipse.client.variables.*;
import org.openmaji.implementation.tool.eclipse.editor.common.model.ConnectionElement;
import org.openmaji.implementation.tool.eclipse.editor.common.model.Element;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.Diagram;



/**
 * <code>PropertyChangeToVariableMapConnectionEditPolicy</code>.
 * <p>
 * @author Kin Wong
 */
public class PropertyChangeToVariableMapConnectionEditPolicy
	extends PropertyChangeToVariableMapEditPolicy {
	/**
	 * Constructs an instance of <code>PropertyChangeToVariableMapConnectionEditPolicy</code>.
	 * <p>
	 * @param source
	 */
	public PropertyChangeToVariableMapConnectionEditPolicy(IVariableSource source) {
		super(source);
	}
	
	private ConnectionElement getConnectionModel() {
		return (ConnectionElement)getElementModel();
	}
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.client.variables.PropertyChangeToVariableMapEditPolicy#getVariableMap()
	 */
	public VariableMap getVariableMap() {
		ConnectionElement connection = getConnectionModel();
		if(connection.getSource() != null) {
			Element sourceRoot = ((Element)connection.getSource()).getRoot();
			if(sourceRoot instanceof Diagram) 
			return ((Diagram)sourceRoot).getProxy().getVariableMapProxy();
		}
		
		if(connection.getTarget() != null) {
			Element targetRoot = ((Element)connection.getTarget()).getRoot();
			if(targetRoot instanceof Diagram)
			return ((Diagram)targetRoot).getProxy().getVariableMapProxy();
		}
		
		return null;
	}
}
