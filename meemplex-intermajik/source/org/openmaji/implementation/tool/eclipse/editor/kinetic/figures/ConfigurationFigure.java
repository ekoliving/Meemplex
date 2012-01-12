package org.openmaji.implementation.tool.eclipse.editor.kinetic.figures;
import java.util.Hashtable;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.FreeformViewport;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.ScrollPane;
import org.eclipse.draw2d.StackLayout;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Point;

/**
 * @author Kin Wong
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class ConfigurationFigure extends FreeformLayer {
	static Point initialPosition = new Point(0,0);
	
	Hashtable meemFigures = new Hashtable();
	Hashtable depedencyFigures = new Hashtable();
	private IFigure pane;
	
	public IFigure getContentPane(){
		return pane;
	}
	
	public ConfigurationFigure() {
		setBorder(new MarginBorder(5));
		setBackgroundColor(ColorConstants.orange);
		setOpaque(true);
		
		ScrollPane scrollpane = new ScrollPane();
		pane = new FreeformLayer();
//		pane.setLayoutManager(new ConfigurationLayout());
		pane.setLayoutManager(new XYLayout());
		setLayoutManager(new StackLayout());
		add(scrollpane);
		scrollpane.setViewport(new FreeformViewport());
		scrollpane.setContents(pane);
	}
}
