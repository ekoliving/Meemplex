/*
 * Created on 11/04/2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.openmaji.implementation.intermajik.model;

/**
 * @author Kin Wong
 * 
 * To change this generated comment go to Window>Preferences>Java>Code
 * Generation>Code and Comments
 */
public interface ViewModeConstants {

	String ID_VIEW_MODE_DETAILED = ViewModeConstants.class + ".detailed";

	String ID_VIEW_MODE_ICONIC = ViewModeConstants.class + ".iconic";

	String ID_VIEW_MODE_DEVICE = ViewModeConstants.class + ".device";

	ViewMode VIEW_MODE_DETAILED = new ViewMode(ID_VIEW_MODE_DETAILED, "Detailed View");

	ViewMode VIEW_MODE_ICONIC = new ViewMode(ID_VIEW_MODE_ICONIC, "Iconic View");

	ViewMode VIEW_MODE_DEVICE = new ViewMode(ID_VIEW_MODE_DEVICE, "Device View");
}
