package org.openmaji.implementation.tool.eclipse.editor.common.model.commands;

import org.eclipse.draw2d.Bendpoint;

/**
 * @author Kin Wong
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class BendpointDeleteCommand extends BendpointCommand {
	private Bendpoint deletedBendpoint;

	public void execute() {
		deletedBendpoint = (Bendpoint)getConnectionElement().getBendpoints().get(getIndex());
		getConnectionElement().removeBendpoint(getIndex());
		super.execute();
	}
	public void undo() {
		super.undo();
		getConnectionElement().insertBendpoint(getIndex(), deletedBendpoint);
	}
}
