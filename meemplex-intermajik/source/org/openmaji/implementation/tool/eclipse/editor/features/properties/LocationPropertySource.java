/*
 * @(#)LocationPropertySource.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.tool.eclipse.editor.features.properties;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import org.eclipse.draw2d.geometry.Point;
import org.openmaji.implementation.tool.eclipse.editor.common.Messages;

public class LocationPropertySource implements IPropertySource{
	public static String ID_XPOS = "xPos"; 
	public static String ID_YPOS = "yPos"; 
	protected static IPropertyDescriptor[] descriptors;
	
	static{
		descriptors = new IPropertyDescriptor[] {
			new TextPropertyDescriptor(ID_XPOS,Messages.LocationPropertySource_Property_X_Label),
			new TextPropertyDescriptor(ID_YPOS,Messages.LocationPropertySource_Property_Y_Label)
		};
}

protected Point point = null;

public LocationPropertySource(Point point){
	this.point = new Point(point);
}

public Object getEditableValue(){
	return this;
}

public IPropertyDescriptor[] getPropertyDescriptors(){
	return descriptors;
}

public Object getPropertyValue(Object propName){
	if(ID_XPOS.equals(propName)){
		return new String(new Integer(point.x).toString());
	}
	if(ID_YPOS.equals(propName)){
		return new String(new Integer(point.y).toString());
	}
	return null;
}

public Point getValue(){
	return new Point(point);
}

public boolean isPropertySet(Object propName){
	if(ID_XPOS.equals(propName) || ID_YPOS.equals(propName))return true;
	return false;
}

public void resetPropertyValue(Object propName){}

public void setPropertyValue(Object propName, Object value){
	if(ID_XPOS.equals(propName)){
		Integer newInt = new Integer((String)value);
		point.x = newInt.intValue();
	}
	if(ID_YPOS.equals(propName)){
		Integer newInt = new Integer((String)value);
		point.y = newInt.intValue();
	}
}

public String toString(){
	return new String("["+point.x+","+point.y+"]");
}

}
