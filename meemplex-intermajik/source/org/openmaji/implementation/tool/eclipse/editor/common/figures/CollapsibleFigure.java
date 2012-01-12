package org.openmaji.implementation.tool.eclipse.editor.common.figures;

import org.eclipse.draw2d.BorderLayout;
import org.eclipse.draw2d.CompoundBorder;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.Layer;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.openmaji.implementation.tool.eclipse.editor.common.util.ColorTransformer;
import org.openmaji.implementation.tool.eclipse.editor.features.infotips.InfoTip;
import org.openmaji.implementation.tool.eclipse.editor.features.ui.FontDescriptor;


/**
 * @author Kin Wong
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class CollapsibleFigure extends Figure {
	static protected final int BUTTON_WIDTH = 11;
	static protected final int BUTTON_HEIGHT = 11;
	static protected final Dimension BUTTON_SIZE = new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT);
	static protected final int MAJOR_GAP_SIZE = 2;
	static protected final int MINOR_GAP_SIZE = 1;
	static protected final int CHILD_SPACE = 1;
	
	protected CollapseButton collapseButton;
	protected Label labelCaption;
	protected Figure labelLayer;
	protected IFigure contentPane;
	protected boolean collapsed = false;
	protected boolean refreshing = false;
	FontDescriptor captionDescriptor;
	
	/**
	 * Constructs a new CollapsibleFigure.
	 */	
	public CollapsibleFigure() {
		setOpaque(true);
		setLayoutManager(new BorderLayout());
		// Content Pane
		createLabelLayer();
		add(labelLayer);
		contentPane = createContentPane();
		setConstraint(labelLayer, BorderLayout.TOP);
		setConstraint(contentPane, BorderLayout.CENTER);
		setOpaque(true);
	}
	
	public void dispose() {
		if(captionDescriptor != null) {
			FontManager.getInstance().checkIn(captionDescriptor);
			captionDescriptor = null;
		}
	}
	/*
	 * Creates the label layer.
	 */
	protected void createLabelLayer() {
		// Caption Label
		labelCaption = new Label();
		labelCaption.setOpaque(false);
		labelCaption.setBorder(new MarginBorder(new Insets(0, MAJOR_GAP_SIZE,0,MAJOR_GAP_SIZE)));
		
		// Caption 
		labelLayer = new Layer();
		labelLayer.setBorder(new MarginBorder(MINOR_GAP_SIZE, MAJOR_GAP_SIZE, MINOR_GAP_SIZE, MAJOR_GAP_SIZE));
		BorderLayout labelLayoutMananger = new BorderLayout();
		labelLayer.setLayoutManager(labelLayoutMananger);
		
		// Collapse button
		collapseButton = new CollapseButton();
		collapseButton.setPreferredSize(BUTTON_SIZE);
		collapseButton.setToolTip(new InfoTip("Click to collapse and expand."));
		
		labelLayer.add(collapseButton);
		labelLayer.setConstraint(collapseButton, BorderLayout.LEFT);
		labelLayer.add(labelCaption);
		labelLayer.setConstraint(labelCaption, BorderLayout.CENTER);
	}
	
	/**
	 * Apply the figure scheme to this collapsible figure.
	 * @param scheme The figure scheme to be applied.
	 */
	public void apply(FigureScheme scheme) {
		Cursor cursor = scheme.getCursor();
		scheme.applyColors(this);
		scheme.applyColors(getContentPane());
		setBorder(new CompoundBorder(new LineBorder(scheme.getBorderColor(), 
													scheme.getBorderWidth()), 
													new MarginBorder(1)));
													
		LineBorder border = new LineBorder(ColorTransformer.darken(scheme.getBackground()));
		getCollapseButton().setBorder(border);
		setCursor(cursor);
		
		scheme.applyCaptionColors(getCaption());
		if(!scheme.getCaptionFont().equals(captionDescriptor)) {
			if(captionDescriptor != null) {
				FontManager.getInstance().checkIn(captionDescriptor);
			}
			captionDescriptor = scheme.getCaptionFont();
			getCaption().setFont(FontManager.getInstance().checkOut(captionDescriptor));
		}
		
		getLabelLayer().setCursor(cursor);
		getCaption().setBackgroundColor(scheme.getBackground());
		getCaption().setForegroundColor(scheme.getCaptionText());
		getCaption().setCursor(cursor);
	}
	
	/**
	 * Gets the content pane of this collapsible figure.<p>
	 * The content pane is the figure where it hosts child figures.<p>
	 * @return IFigure The content pane of this collapsible figure.
	 */
	public IFigure getContentPane() {
		return contentPane;
	}
	
	/**
	 * Gets the collapse button of this collapsible figure.
	 * @return CollapseButton The collapse button of this collapsible figure.
	 */
	public CollapseButton getCollapseButton() {
		return collapseButton;
	}
	
	/**
	 * Gets the label layer of this collapsible figure.
	 * @return Figure The label layer of this collapsible figure.
	 */
	public Figure getLabelLayer() {
		return labelLayer;
	}
	
	/**
	 * Gets the caption label of this collapsible figure.
	 * @return Label The caption label of this collapsible figure.
	 */
	public Label getCaption() {
		return labelCaption;
	}
	
	/**
	 * Sets the caption text.
	 * @param text The new caption text.
	 */
	public void setCaptionText(String text) {
		getCaption().setText(text);
	}
	
	/**
	 * Returns whether this collapsible figure is currently collapsed.
	 * @return boolean true if this collapsible figure is currently collapsed,
	 * false otherwise.
	 */
	public boolean isCollapsed() {
		return collapsed;
	}
	
	/**
	 * Returns whether this collapsible figure is currently expanded.
	 * @return boolean true if this collapsible figure is currently expanded,
	 * false otherwise.
	 */
	public boolean isExpanded() {
		return !collapsed;
	}
	
	/**
	 * Set the collapse state of the figure.
	 * @param collapsed true if the figure is to be collapsed, false to expand.
	 */
	public void setCollapse(boolean collapsed) {
		if(this.collapsed == collapsed) return;
		
		this.collapsed = collapsed;
		if(isCollapsed()) {
			// Collapse
			performCollapse();
		}
		else {
			// Expand
			performExpand();
		}
	}
	
	/**
	 * Collpases the content pane.
	 */
	public void Collapse() {
		setCollapse(true);
	}
	
	/**
	 * Expands the content pane.
	 */
	public void Expand() {
		setCollapse(false);
	}
	
	/**
	 * perform the collapse operation on the figure. The default implementation 
	 * sets the preferred size to be -1,0, indicating best width and 0 pixel 
	 * height. Derived class can override to perform additional task.
	 */
	protected void performCollapse() {
		Dimension minSize =  new Dimension(-1, 0); // Best width, 9 height
		getContentPane().setPreferredSize(minSize);
	}
	
	/**
	 * perform the expand operation on the figure. The default implementation 
	 * sets the preferred size to null. Derived class can override to perform 
	 * additional task.
	 */
	protected void performExpand() {
		getContentPane().setPreferredSize(null);
	}
	
	/**
	 * Invoked internally to create the collapsible content pane. This
	 * implementation creates a FreeformLayer object and derive class can
	 * override this to create other type of content pane.
	 * @return IFigure A FreeformLayer used as the collapsible content pane.
	 */
	protected IFigure createContentPane() {
		Layer contentPane = new Layer();
		ToolbarLayout paneLayoutMananger = new ToolbarLayout(ToolbarLayout.VERTICAL);
		paneLayoutMananger.setStretchMinorAxis(true);
		// The gap is required between items so dnd arrangement and creation is
		// possible.
		paneLayoutMananger.setSpacing(CHILD_SPACE);	
		contentPane.setLayoutManager(paneLayoutMananger);
		add(contentPane);
		return contentPane;
	}
	
	/**
	 * Overridden here to return true to indicate that Figures within it are
	 * positioned in local coordinates.
	 * @see org.eclipse.draw2d.Figure#useLocalCoordinates()
	 */
	protected boolean useLocalCoordinates() {
		return true;
	}
	

	/* (non-Javadoc)
	 * @see org.eclipse.draw2d.Figure#setForegroundColor(org.eclipse.swt.graphics.Color)
	 */

	public void setForegroundColor(Color fg) {
		super.setForegroundColor(fg);
		getCaption().setForegroundColor(fg);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.draw2d.Figure#setBackgroundColor(org.eclipse.swt.graphics.Color)
	 */
	public void setBackgroundColor(Color bg) {
		super.setBackgroundColor(bg);
		getCaption().setBackgroundColor(bg);
	}
}
