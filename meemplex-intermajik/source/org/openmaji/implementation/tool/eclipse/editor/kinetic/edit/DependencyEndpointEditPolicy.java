package org.openmaji.implementation.tool.eclipse.editor.kinetic.edit;

import org.eclipse.gef.editpolicies.ConnectionEndpointEditPolicy;

/**
 * @author Kin Wong
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class DependencyEndpointEditPolicy
	extends ConnectionEndpointEditPolicy {
		public DependencyEndpointEditPolicy() {
			//System.out.println("Constructing DependencyEndpointEditPolicy");
		}
		//protected DependencyFigure getDependencyFigure() {
		//	return (DependencyFigure)getConnectionFigure();
		//}
		
		protected void addSelectionHandles(){
			super.addSelectionHandles();
			//getDependencyFigure().setSelect(true);
		}

		protected void removeSelectionHandles(){
			super.removeSelectionHandles();
			//getDependencyFigure().setSelect(false);
		}
}
