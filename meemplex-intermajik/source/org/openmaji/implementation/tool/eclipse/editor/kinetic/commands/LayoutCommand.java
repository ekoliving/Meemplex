/*
 * Created on 25/03/2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.openmaji.implementation.tool.eclipse.editor.kinetic.commands;

import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.commands.Command;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.layout.deathman.ConfigurationLayoutObjectFactory;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.layout.deathman.Layout;



/**
 * @author Kin Wong
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class LayoutCommand extends Command {
	private GraphicalEditPart editPart;
	
	/**
	 * Constructs a LayoutCommand that layout all the object in the 
	 * configuration.
	 * @param editPart The edit part that contains all object to be 
	 * repositioned.
	 */	
	public LayoutCommand(GraphicalEditPart editPart) {
		this.editPart = editPart;
	}
	/**
	 * @see org.eclipse.gef.commands.Command#canUndo()
	 */
	public boolean canUndo() {
		return false;
	}
	/**
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	public void execute() {
		//System.out.println("===== Start Executing LayoutCommand =====");
		Layout layout = new Layout(new ConfigurationLayoutObjectFactory());
		layout.build(editPart);
		//layout.setIteration(20);
		//for(int i = 0; i < 50; i++)
		for(int i = 1; i <= 10; i++)
		{
			layout.setIteration(50 * i);
			layout.layout(i);
			layout.update();
			editPart.getFigure().getUpdateManager().performUpdate();
			//editPart.getFigure().getUpdateManager().addDirtyRegion();
			//System.out.println("Frame " + Integer.toString(i));
		}
		//System.out.println("===== Finish Executing LayoutCommand =====");
	}
}
