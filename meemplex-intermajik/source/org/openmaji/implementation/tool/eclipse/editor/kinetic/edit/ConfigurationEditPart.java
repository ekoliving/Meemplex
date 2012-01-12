package org.openmaji.implementation.tool.eclipse.editor.kinetic.edit;

import org.eclipse.draw2d.AutomaticRouter;
import org.eclipse.draw2d.BendpointConnectionRouter;
import org.eclipse.draw2d.ConnectionLayer;
import org.eclipse.draw2d.FanRouter;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.editpolicies.RootComponentEditPolicy;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.openmaji.implementation.tool.eclipse.editor.features.ui.BackdropFreeformLayer;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.Configuration;
import org.openmaji.implementation.tool.eclipse.images.Images;


/**
 * @author Kin Wong
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class ConfigurationEditPart extends DiagramEditPart {
	/**
	 * Constructs an instance of configuration.
	 * @param configuration The configuration to be associated with this edit part.
	 */
	public ConfigurationEditPart(Configuration configuration) {
		super(configuration);
	}

	protected void createEditPolicies() {
		super.createEditPolicies();
		removeEditPolicy(EditPolicy.NODE_ROLE);
		removeEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE);
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new RootComponentEditPolicy());
	}

	/**
	 * Gets the model as configuration.
	 * @return Configuration
	 */
	protected Configuration getConfiguration() {
		return (Configuration)getModel();
	}
	/**
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	protected IFigure createFigure() {
		Figure figure = new BackdropFreeformLayer(Images.class, "configurationdark.bmp");	
		figure.setLayoutManager(new FreeformLayout());
		figure.setBorder(new MarginBorder(10));
		figure.setBackgroundColor(new Color(Display.getDefault(), 0, 0, 40));
		figure.setOpaque(true);

		ConnectionLayer cLayer = (ConnectionLayer) getLayer(LayerConstants.CONNECTION_LAYER);
		AutomaticRouter router = new FanRouter();
		router.setNextRouter(new BendpointConnectionRouter());
		cLayer.setConnectionRouter(router);

		return figure;
	}
	
	protected void refreshVisuals(){
	}
}
