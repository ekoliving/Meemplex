/*
 * Created on 10/04/2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.openmaji.implementation.tool.eclipse.editor.features.multiview;

import org.eclipse.gef.commands.Command;
import org.openmaji.implementation.intermajik.model.ViewMode;


/**
 * <code>ViewModeCommand</code> works inconjuction with the 
 * <code>IViewModeProvider</code> to change the view mode of the implementation.
 * <p>
 * @author Kin Wong
 */
public class ViewModeCommand extends Command {
	private ViewMode viewMode;
	private ViewMode oldViewMode;
	private IViewModeProvider viewModeProvider;
	
	public ViewModeCommand(IViewModeProvider viewModeProvider) {
		this.viewModeProvider = viewModeProvider;
	}
	public void setViewMode(ViewMode viewMode) {
		this.viewMode = viewMode;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	public void execute() {
		oldViewMode = viewModeProvider.getViewMode();
		viewModeProvider.setViewMode(viewMode);
	}
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	public void undo() {
		viewModeProvider.setViewMode(oldViewMode);
	}
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#redo()
	 */
	public void redo() {
		viewModeProvider.setViewMode(viewMode);
	}
}
