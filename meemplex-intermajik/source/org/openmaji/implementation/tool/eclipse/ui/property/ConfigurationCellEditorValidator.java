/*
 * @(#)ConfigurationCellEditorValidator.java
 * Created on 10/05/2004
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.ui.property;

import java.lang.reflect.Constructor;


import org.eclipse.jface.viewers.ICellEditorValidator;
import org.openmaji.meem.wedge.configuration.ConfigurationSpecification;

/**
 * <code>ConfigurationCellEditorValidator</code>.
 * <p>
 * @author Kin Wong
 */
public class ConfigurationCellEditorValidator implements ICellEditorValidator {
	private ConfigurationSpecification specification;
	private Constructor						constructor;		// conversion constructor
	/**
	 * Constructs an instance of <code>ConfigurationCellEditorValidator</code>.
	 * <p>
	 */
	public ConfigurationCellEditorValidator(ConfigurationSpecification specification) {
		this.specification = specification;
		
		if (specification.getType() != String.class)
		{
			Class	implementation = specification.getType();
			
			try
			{
				constructor = implementation.getConstructor(new Class[] { String.class });
			}
			catch (NoSuchMethodException e)
			{
				//
				// no conversion constructor available - turn off validation
				//
				constructor = null;
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ICellEditorValidator#isValid(java.lang.Object)
	 */
	public String isValid(Object value) {
		if(value == null) return null;
		
		if (value.getClass() == specification.getType())
		{
			return specification.validate(value);
		}
		else if (value.getClass() == String.class && constructor != null)
		{
			//
			// we attempt to create the right object type from the string, if this fails we
			// just fall back and assume the meem will take care of it.
			//
			try
            {
                Object	v = constructor.newInstance(new Object[] { value });
                
                return specification.validate(v);
            }
            catch (Exception e)
            {
                return "The string \"" + value + "\" is not valid input.";
            }
		}
		
		return null;
	}
}
