/*
 * Created on 9/04/2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.openmaji.implementation.tool.eclipse.editor.features.multiview;

import org.eclipse.gef.Request;
import org.openmaji.implementation.intermajik.model.ViewMode;
import org.openmaji.implementation.tool.eclipse.editor.features.RequestConstants;


/**
 * @author Kin Wong
 *
 * ViewModeRequest allows an edit part to change figure.
 */
public class ViewModeRequest extends Request implements RequestConstants {
	private ViewMode viewMode;
	
	public ViewModeRequest(ViewMode viewMode) {
		setType(REQ_CHANGE_VIEW_MODE);
		this.viewMode = viewMode;
	}

	/**
	 * Gets the current view mode.
	 * @return String The current view mode.
	 */
	public ViewMode getViewMode() {
		return viewMode;
	}
}
