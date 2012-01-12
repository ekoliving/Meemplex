package org.openmaji.implementation.tool.eclipse.editor.common.model.commands;
import org.eclipse.gef.commands.Command;
import org.openmaji.implementation.tool.eclipse.editor.common.model.Element;
import org.openmaji.implementation.tool.eclipse.editor.common.model.ElementContainer;


/**
 * @author Kin Wong
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class OrphanChildCommand extends Command {
	private ElementContainer container;
	private Element child;
	private int index;

	public OrphanChildCommand (ElementContainer container, Element child) {
		this.container = container;
		this.child = child;
	}

	public void execute() {
		index = container.childIndexOf(child);
		container.removeChild(child);
	}

	public void redo() {
		container.removeChild(child);
	}

	public void undo() {
		container.addChild(index, child);
	}

}
