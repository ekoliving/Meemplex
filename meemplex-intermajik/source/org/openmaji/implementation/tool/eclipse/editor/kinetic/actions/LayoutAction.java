/*
 * Created on 25/03/2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.openmaji.implementation.tool.eclipse.editor.kinetic.actions;

import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.ui.actions.EditorPartAction;
import org.eclipse.ui.IEditorPart;
import org.openmaji.implementation.tool.eclipse.client.MeemClientProxy;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.Messages;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.commands.LayoutCommand;


/**
 * @author Kin Wong
 *
 * The layoutAction represents a generic layout action.
 */
public class LayoutAction extends EditorPartAction {
	static public String ID_LAYOUT_STANDARD = "layout standard";
	
	public LayoutAction(IEditorPart editorPart, String layout) {
		super(editorPart);
		setup(layout);
	}
	
	private void setup(String layout) {
		setId(layout);
		if(layout.equals(ID_LAYOUT_STANDARD)) {
			setToolTipText(Messages.LayoutAction_LayoutStandard_Label);
			setDescription(Messages.LayoutAction_LayoutStandard_Description);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.ui.actions.EditorPartAction#calculateEnabled()
	 */
	protected boolean calculateEnabled() {
		return getDiagram().getVariableMapProxy().isModifiable();
	}
	
	protected MeemClientProxy getDiagram() {
		return (MeemClientProxy)getEditorPart().getAdapter(MeemClientProxy.class);
	}
	
	public void run() {
		GraphicalViewer viewer = (GraphicalViewer)getEditorPart().getAdapter(GraphicalViewer.class);
		execute(new LayoutCommand((GraphicalEditPart)viewer.getContents()));
	}
}
