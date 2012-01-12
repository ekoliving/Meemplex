package org.openmaji.implementation.tool.eclipse.editor.features.containment;

import org.eclipse.gef.commands.Command;
import org.openmaji.implementation.tool.eclipse.editor.common.model.Element;


/**
 * @author Kin Wong
 */
public class DeleteCommand extends Command {
	private Object child;
	private IModelContainer parent;
	private int index;
	
	public DeleteCommand(IModelContainer parent, Element child) {
		this.parent = parent;
		this.child = child;
	}

	public void execute() {
		primExecute();
	}

	protected void primExecute() {
		index = parent.childIndexOf(child);
		if(index != -1) parent.removeChild(child);
	}

	public void redo() {
		primExecute();
	}

	public void undo() {
		if(index != -1) parent.addChild(index, child);
	}
}
