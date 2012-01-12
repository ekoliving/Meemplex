/*
 * Created on 10/04/2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.openmaji.implementation.tool.eclipse.editor.kinetic.figures;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Shape;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;
import org.openmaji.implementation.tool.eclipse.editor.common.figures.FigureScheme;
import org.openmaji.implementation.tool.eclipse.editor.common.shapes.Hexagon;
import org.openmaji.implementation.tool.eclipse.editor.features.dynamicshapes.IShapeProvider;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.shapes.MeemPlexShape;


/**
 * <code>MeemPlexShapeFigure</code> represents the hexagonal shape figure.
 * @author Kin Wong
 */
public class MeemPlexShapeFigure extends Figure implements IShapeProvider {
	static public int DEFAULT_WIDTH = 60;
	static public int DEFAULT_HEIGHT = 60;
	static private int DEFAULT_INNER_WIDTH = 20;
	static private int DEFAULT_INNER_HEIGHT = 20;

	private MeemPlexShape meem;
	private Hexagon innerHexagon;
	/**
	 * Constructs an instance of <code>MeemPlexShapeFigure</code>.
	 */
	public MeemPlexShapeFigure() {
		setOpaque(false);
		meem = new MeemPlexShape();
		setPreferredSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		innerHexagon = new Hexagon();
		add(meem);
		add(innerHexagon);
	}
	/**
	 * Gets the inner hexagon of this <code>MeemPlexShapeFigure</code>.
	 * @return Hexagon The inner hexagon of this <code>MeemPlexShapeFigure</code>.
	 */
	public Hexagon getInnerHexagon() {
		return innerHexagon;
	}
	public MeemPlexShape getMeemShape() {
		return meem;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.draw2d.Figure#validate()
	 */
	public void validate() {
		super.validate();
		meem.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		Rectangle bounds = getBounds();
		Point center = bounds.getCenter();
		center.x = DEFAULT_WIDTH >> 1;
		center.y = DEFAULT_HEIGHT >> 1;
		innerHexagon.setBounds( 
			new Rectangle(	center.x - (DEFAULT_INNER_WIDTH >> 1), 
							center.y - (DEFAULT_INNER_HEIGHT >>1),
							DEFAULT_INNER_WIDTH, 
							DEFAULT_INNER_HEIGHT));
	}
	/**
	 * Applies the figure scheme to this figure.
	 * @param scheme The figure scheme to apply.
	 */
	public void apply(FigureScheme scheme) {
		setForegroundColor(scheme.getBorderColor());
		setBackgroundColor(scheme.getBackground());
		meem.setLineWidth(scheme.getBorderWidth());
		meem.setCursor(scheme.getCursor());
		innerHexagon.setLineWidth(scheme.getBorderWidth());
		innerHexagon.setCursor(scheme.getCursor());
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.draw2d.Figure#setForegroundColor(org.eclipse.swt.graphics.Color)
	 */
	public void setForegroundColor(Color fg) {
		super.setForegroundColor(fg);
		meem.setForegroundColor(fg);
		innerHexagon.setForegroundColor(fg);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.draw2d.Figure#setBackgroundColor(org.eclipse.swt.graphics.Color)
	 */
	public void setBackgroundColor(Color bg) {
		super.setBackgroundColor(bg);
		meem.setBackgroundColor(bg);
		innerHexagon.setBackgroundColor(bg);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.draw2d.Figure#useLocalCoordinates()
	 */
	protected boolean useLocalCoordinates() {
		return true;
	}
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.features.shapes.IShapeProvider#createFeedbackShape()
	 */
	public Shape createFeedbackShape() {
		MeemPlexShape meem = new MeemPlexShape();
		meem.setSize(getSize());
		meem.setInnerOutline(false);
		meem.setLineWidth(this.meem.getLineWidth());
		meem.setForegroundColor(ColorConstants.white);
		return meem;
	}

	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.features.dynamicshapes.IShapeProvider#createSelectionShape(org.eclipse.draw2d.IFigure)
	 */
	public Shape createSelectionShape(IFigure figure) {
		MeemPlexShape meem = new MeemPlexShape();
		meem.setInnerOutline(false);
		meem.setSize(getSize());
		meem.setFill(false);
		meem.setLineWidth(this.meem.getLineWidth());
		meem.setForegroundColor(ColorConstants.white);
		return meem;
	}
}
