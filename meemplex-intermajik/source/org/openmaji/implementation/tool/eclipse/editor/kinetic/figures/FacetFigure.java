package org.openmaji.implementation.tool.eclipse.editor.kinetic.figures;

import org.eclipse.draw2d.Border;
import org.eclipse.draw2d.BorderLayout;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.Orientable;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.Triangle;
import org.eclipse.draw2d.geometry.Dimension;
import org.openmaji.implementation.tool.eclipse.editor.common.figures.FigureScheme;
import org.openmaji.implementation.tool.eclipse.editor.common.figures.FontManager;
import org.openmaji.implementation.tool.eclipse.editor.features.ui.FontDescriptor;


/**
 * @author Kin Wong
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class FacetFigure extends Figure {
	static private final int TRIANGLE_GAP = 2;
	static private final int TRIANGLE_SIZE = 10;
	static private Dimension TriangleSize = new Dimension(TRIANGLE_SIZE, TRIANGLE_SIZE);
	
	private Label label;
	private Triangle leftTriangle;
	private Triangle rightTriangle;
	private FontDescriptor captionDescriptor;
	
	public void dispose() {
		if(captionDescriptor != null) {
			FontManager.getInstance().checkIn(captionDescriptor);
			captionDescriptor = null;
		}
	}
	
	/**
	 * Gets the label of the facet figure.
	 * @return Label The label of the facet figure.
	 */
	public Label getLabel() {
		return label;
	}
	
	/**
	 * Gets the left triangle that indicates whether the facet is inbound or outbound.
	 * @return Triangle The triangle on the left.
	 */
	public Triangle getLeftTriangle() {
		return leftTriangle;
	}
	
	/**
	 * Gets the right triangle that indicates whether the facet is inbound or outbound.
	 * @return Triangle The triangle on the right.
	 */
	public Triangle getRightTriangle() {
		return rightTriangle;
	}
	
	/**
	 * Constructs an instance of FacetFigure.<p>
	 */
	public FacetFigure() {
		setLayoutManager(new BorderLayout());
		setOpaque(true);
		
		// Label
		label = new Label();
		add(label);
		
		// Left Triangle
		Border border = new MarginBorder(TRIANGLE_GAP);
		leftTriangle = new Triangle();
		leftTriangle.setBorder(border);
		leftTriangle.setOrientation(Orientable.HORIZONTAL);
		leftTriangle.setSize(TriangleSize);
		leftTriangle.setFill(true);
		add(leftTriangle);

		// Right Triangle
		rightTriangle = new Triangle();
		rightTriangle.setBorder(border);
		rightTriangle.setOrientation(Orientable.HORIZONTAL);
		rightTriangle.setSize(TriangleSize);
		rightTriangle.setFill(true);
		add(rightTriangle);

		setConstraint(label, BorderLayout.CENTER);
		setConstraint(leftTriangle, BorderLayout.LEFT);
		setConstraint(rightTriangle, BorderLayout.RIGHT);
	}
	
	public void apply(FigureScheme scheme) {
		scheme.applyColors(this);
		scheme.applyColors(getLabel());
		scheme.applyCaptionColors(getLabel());
		captionDescriptor = scheme.getCaptionFont();
		getLabel().setFont(FontManager.getInstance().checkOut(captionDescriptor));
		setBorder(new LineBorder(scheme.getBorderColor(), scheme.getBorderWidth()));
	}
	
	public void setBounds(boolean inbound) {
		leftTriangle.setDirection(	inbound
									? PositionConstants.EAST
									: PositionConstants.WEST);
		
		rightTriangle.setDirection(	inbound
									? PositionConstants.WEST
									: PositionConstants.EAST);
	}
	/*
	 *  (non-Javadoc)
	 * @see org.eclipse.draw2d.Figure#useLocalCoordinates()
	 */
	protected boolean useLocalCoordinates() {
		return false;
	}
}
