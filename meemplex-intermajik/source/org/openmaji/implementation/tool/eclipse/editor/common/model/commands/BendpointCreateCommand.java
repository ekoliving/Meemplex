package org.openmaji.implementation.tool.eclipse.editor.common.model.commands;

import org.openmaji.implementation.tool.eclipse.editor.common.model.ConnectionBendpoint;

/**
 * @author Kin Wong
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class BendpointCreateCommand extends BendpointCommand {

	public void execute() {
		ConnectionBendpoint bendpoint = new ConnectionBendpoint();
		bendpoint.setRelativeDimensions(
			getFirstRelativeDimension(), 
			getSecondRelativeDimension());

		getConnectionElement().insertBendpoint(getIndex(), bendpoint);
		super.execute();
	}
	
	public void undo() {
		getConnectionElement().removeBendpoint(getIndex());
		super.undo();
	}
}