/*
 * @(#)FieldLabelProvider.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.tool.eclipse.ui.dialog;

import org.eclipse.jface.viewers.LabelProvider;
import org.openmaji.implementation.tool.eclipse.library.classlibrary.FieldDescriptor;


/**
 * @author mg
 * Created on 13/01/2003
 */
public class FieldLabelProvider extends LabelProvider {

	/**
	 * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
	 */
	public String getText(Object element) {
		if (element instanceof FieldDescriptor) {
			return makeLabel((FieldDescriptor) element);
		}
		return super.getText(element);
	}

	public String makeLabel(FieldDescriptor descriptor) {
		return descriptor.getName() + " : " + parseType(descriptor.getType());
	}

	public static String parseType(String type) {
		boolean isArray = false;

		StringBuffer typeBuffer = new StringBuffer();
		if (type.startsWith("[")) {
			isArray = true;
			type = type.substring(1);
		}

		switch (type.charAt(0)) {
			case 'Z' :
				typeBuffer.append("boolean");
				break;
			case 'B' :
				typeBuffer.append("byte");
				break;
			case 'C' :
				typeBuffer.append("char");
				break;
			case 'S' :
				typeBuffer.append("short");
				break;
			case 'I' :
				typeBuffer.append("int");
				break;
			case 'J' :
				typeBuffer.append("long");
				break;
			case 'F' :
				typeBuffer.append("float");
				break;
			case 'D' :
				typeBuffer.append("double");
				break;
			case 'L' :
				// class name
				// loose first and last chars

				type = type.substring(1, type.length() - 1);
				type = type.replace('/', '.');

				if (type.startsWith("java.lang."))
					type = type.substring(10);

				typeBuffer.append(type);

		}

		if (isArray)
			typeBuffer.append("[]");

		return typeBuffer.toString();
	}

}

/*

Signature 	Java Type 
		Z 			boolean  
		B 			byte 
		C 			char 
		S 			short 
		I 			int 
		J 			long 
		F 			float 
		D 			double 
		L 			fully-qualified-class ;  fully-qualified-class  
		[ 			type type[] 
		( 			arg-types ) ret-type method type 

For example, the Prompt.getLine method has the signature: 

(Ljava/lang/String;)Ljava/lang/String;

whereas the Callbacks.main method has the signature: 
([Ljava/lang/String;)V

Array types are indicated by a leading square bracket ([) followed by the type of the array elements. 

*/