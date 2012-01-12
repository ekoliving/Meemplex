/*
 * @(#)PropertyChangeToVariableMapEditPolicy.java
 * Created on 7/10/2003
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.client.variables;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;



import org.eclipse.core.runtime.Assert;
import org.eclipse.gef.editpolicies.AbstractEditPolicy;
import org.openmaji.common.VariableMap;
import org.openmaji.implementation.intermajik.model.ElementPath;
import org.openmaji.implementation.intermajik.model.ValueBag;
import org.openmaji.implementation.tool.eclipse.editor.common.model.Element;


/**
 * <code>PropertyChangeToVariableMapEditPolicy</code>.
 * <p>
 * @author Kin Wong
 */
abstract public class PropertyChangeToVariableMapEditPolicy extends AbstractEditPolicy {
	IVariableSource source;
	
	private PropertyChangeListener changeListener = new PropertyChangeListener() {
		public void propertyChange(PropertyChangeEvent evt) {
			handlePropertyChange(evt);
		}
	};

	public PropertyChangeToVariableMapEditPolicy(IVariableSource source) {
		Assert.isNotNull(source);
		this.source = source;
	}
	
	protected Element getElementModel() {
		return (Element)getHost().getModel();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.AbstractEditPolicy#activate()
	 */
	public void activate() {
		super.activate();
		getElementModel().addPropertyChangeListener(changeListener);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.AbstractEditPolicy#deactivate()
	 */
	public void deactivate() {
		getElementModel().removePropertyChangeListener(changeListener);
		super.deactivate();
	}

	private void handlePropertyChange(PropertyChangeEvent evt) {
		VariableMap variableMap = getVariableMap();
		if(variableMap == null) return;
		
		//	Ignore all changes if no variable map or Id is attached.
		ElementPath path = getElementModel().getPath();
		/*
		System.out.println(	"handlePropertyChange path:" + path + 
												", " + 
												evt.getPropertyName() + "=" + evt.getNewValue());
		*/
		ValueBag bag = new ValueBag();
		source.extract(evt.getPropertyName(), bag);
		if(!bag.isEmpty()) variableMap.merge(path, bag);
	}
	
	abstract public VariableMap getVariableMap();
}

