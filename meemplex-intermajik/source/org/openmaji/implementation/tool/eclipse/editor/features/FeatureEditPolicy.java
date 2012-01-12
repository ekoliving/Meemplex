/*
 * Created on 10/04/2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.openmaji.implementation.tool.eclipse.editor.features;

/**
 * The <code>FeatureEditPolicy</code> Interface defines all the roles for the 
 * features provided in the framework.
 * <p>
 * @author Kin Wong
 */
public interface FeatureEditPolicy extends org.eclipse.gef.EditPolicy {
	/**
	 * The key used to install an EditPolicy that handles hightlight requests.
	 */
	String HIGHLIGHT_ROLE = "highlight role";
	
	/**
	 * The key used to install an EditPolicy that handles drag-and-drop 
	 * requests.
	 * <p>
	 */
	String DROP_ROLE = "drop role";
	
	String CONNECTABLE_ROLE = "Connectable role";

	/**
	 * The key is used to install EditPolicy that supports the 
	 * collapsibility. It particularly focuses on handling collapse and expand
	 * requests.
	 * <p>
	 * @see org.openmaji.implementation.tool.eclipse.editor.features.collapsible.CollapseAction
	 * @see org.openmaji.implementation.tool.eclipse.editor.features.collapsible.CollapsibleEditPolicy
	 * @see org.openmaji.implementation.tool.eclipse.editor.features.collapsible.CollapseCommand
	 */
	String COLLAPSE_EXPAND_ROLE  = "Collapse expand role";

	/**
	 * The key used to install an EditPolicy that supports synamic view mode. 
	 * It should handle view mode switching request.
	 * <p>
	 * @see org.openmaji.implementation.tool.eclipse.editor.features.multiview.ViewModeRequest
	 * @see org.openmaji.implementation.tool.eclipse.editor.features.multiview.ViewModeEditPolicy
	 * @see org.openmaji.implementation.tool.eclipse.editor.features.multiview.ViewModeCommand
	 */
	String VIEW_MODE_ROLE = "view mode role";
	
	/**
	 * The key used to install an EditPolicy that handles the bringing the 
	 * figure to front.
	 * <p>
	 * @see org.openmaji.implementation.tool.eclipse.editor.features.editpolicies.BringToFrontEditPolicy 
	 */
	String BRING_TO_FRONT_ROLE = "bring to front role";
	
	/**
	 * The key used to install an EditPolicy that provides sorting facility to 
	 * the container.
	 * <p>
	 * @see org.openmaji.implementation.tool.eclipse.editor.features.sorting.SortAction
	 * @see org.openmaji.implementation.tool.eclipse.editor.features.sorting.SortRequest
	 * @see org.openmaji.implementation.tool.eclipse.editor.features.sorting.SortEditPolicy
	 * @see org.openmaji.implementation.tool.eclipse.editor.features.sorting.SortCommand
	 */
	String SORT_ROLE = "sort role";
	
	/**
	 * The key used to install an EditPolicy to connection edit part that 
	 * provides dyanamic visibility control based on the positions and/or 
	 * visibility of its connected parts.
	 * <p>
	 * @see org.openmaji.implementation.tool.eclipse.editor.features.dynamicconnections.DynamicConnectionEditPolicy
	 */
	String CONNECTION_VISIBILITY_ROLE = "connection visibility role";
	
	String ANIMATION_ROLE = "animation edit policy";
}
