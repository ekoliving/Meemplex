/*
 * @(#)ClassDescriptor.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.tool.eclipse.library.classlibrary;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author mg
 * Created on 14/01/2003
 */
public class ClassDescriptor {
	
	private String jarFile;
	private String className;	
	private Collection interfaces = new ArrayList();
	private Collection fields = new ArrayList();
	private boolean isInterface;
	
	public ClassDescriptor(String className, String jarFile, boolean isInterface) {
		this.className = className;
		this.jarFile = jarFile;
		this.isInterface = isInterface;		 
	}

	public void addInterface(String interfaceName){
		interfaces.add(interfaceName.replace('/','.'));
	}
	
	public void addField(String fieldName, String fieldDescriptor){
		fields.add(new FieldDescriptor(fieldName, fieldDescriptor));
	}

	public String getClassName() {
		return className;
	}
	
	public boolean isInterface() {
		return isInterface;
	}
	
	public Collection getInterfaces() {
		return interfaces;
	}
	
	public Collection getFields() {
		return fields;
	}

	public String getJarFile() {
		return jarFile;
	}
	
	public String toString() {
		return(
				"ClassDescriptor [" +
				"className="      + className    +
				", isInterface=" + Boolean.toString(isInterface()) +
				"]"
			);
	}

}
