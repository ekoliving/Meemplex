package org.openmaji.implementation.tool.eclipse.editor.common.model;

import org.openmaji.implementation.tool.eclipse.editor.features.collapsible.ICollapsible;


/**
 * @author Kin Wong
 * Collapsible represents an element that is collapsible.
 */
abstract public class Collapsible extends ElementContainer implements ICollapsible {
	private boolean collapsed = false;
	public static final String ID_COLLAPSED = "collapsed";
	/**
	 * Sets the collapsing state of this collapsible object.
	 * @param collapsed true if the collapsible object is to be collapsed, 
	 * false otherwise.
	 */
	public void setCollapse(boolean collapsed) {
		if(this.collapsed == collapsed) return;
		this.collapsed = collapsed;
		if(collapsed) {
			Collapsing();
		}
		else {
			Expanding();
		}
		firePropertyChange(ID_COLLAPSED, new Boolean(!collapsed), new Boolean(collapsed));
	}
	/**
	 * Returns whether the collapsible object is currently collapsed.
	 * @return boolean true if the collapsible object is currently collapsed, 
	 * false otherwise.
	 */
	public boolean isCollapsed() {
		return collapsed;
	}
	/**
	 * Returns whether the collapsible object is currently expanded.
	 * @return boolean true if the collapsible object is currently expanded, 
	 * false otherwise.
	 */
	public boolean isExpanded() {
		return !collapsed;
	}
	/**
	 * Invoked when this collapsible has just been collapsed. Default 
	 * implementation does nothing. Derived class can override to perform 
	 * additional task.
	 */
	protected void Collapsing() {
	}
	/**
	 * Invoked when this collapsible has just been expanded. Default 
	 * implementation does nothing. Derived class can override to perform 
	 * additional task.
	 */
	protected void Expanding() {
	}
}
