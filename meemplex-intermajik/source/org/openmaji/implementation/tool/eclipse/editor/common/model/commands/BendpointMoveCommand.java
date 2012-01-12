package org.openmaji.implementation.tool.eclipse.editor.common.model.commands;

import org.eclipse.draw2d.Bendpoint;
import org.openmaji.implementation.tool.eclipse.editor.common.model.ConnectionBendpoint;


/**
 * @author Kin Wong
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class BendpointMoveCommand extends BendpointCommand {

	private Bendpoint oldBendpoint;

	public void execute() {
		ConnectionBendpoint bp = new ConnectionBendpoint();
		bp.setRelativeDimensions(getFirstRelativeDimension(), 
						getSecondRelativeDimension());
		setOldBendpoint((Bendpoint)getConnectionElement().getBendpoints().get(getIndex()));
		getConnectionElement().setBendpoint(getIndex(), bp);
		super.execute();
	}

	protected Bendpoint getOldBendpoint() {
		return oldBendpoint;
	}

	public void setOldBendpoint(Bendpoint bp) {
		oldBendpoint = bp;
	}

	public void undo() {
		getConnectionElement().setBendpoint(getIndex(), getOldBendpoint());
	}


}
