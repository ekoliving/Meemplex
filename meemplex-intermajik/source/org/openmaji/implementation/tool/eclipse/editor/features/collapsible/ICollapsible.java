/*
 * Created on 9/04/2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.openmaji.implementation.tool.eclipse.editor.features.collapsible;

/**
 * ICollapsible is the contract that defines an object that can be collapsed 
 * and expanded.
 * <p>
 * @author Kin Wong
 */
public interface ICollapsible {
	/**
	 * Sets the collapse state of this object.
	 * @param collapsed true if the object is to be collapsed, false otherwise.
	 */
	void setCollapse(boolean collapsed);
	/**
	 * Returns whether the object is currently collapsed.
	 * @return true if the object is currently collapsed, false otherwise. 
	 */
	boolean isCollapsed();
}
