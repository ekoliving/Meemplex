package org.openmaji.implementation.tool.eclipse.editor.common.figures;

import java.io.Serializable;

import org.eclipse.draw2d.IFigure;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.openmaji.implementation.tool.eclipse.editor.features.ui.CursorDescriptor;
import org.openmaji.implementation.tool.eclipse.editor.features.ui.FontDescriptor;



/**
 * @author Kin Wong
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class FigureScheme implements Cloneable, Serializable {
	private static final long serialVersionUID = 6424227717462161145L;

	// Colors
	protected Color background;
	protected Color foreground;

	// Caption
	protected Color captionTextColor;
	protected Color captionBackgroundColor;
	protected FontDescriptor captionFont;
	
	// Description
	protected Color descriptionTextColor;
	protected Color descriptionBackgroundColor;
	protected FontDescriptor descriptionFont;
	
	// Border
	protected Color borderColor;
	protected int	borderWidth = 1;
	
	// Selection
	protected Color selectionColor;
	protected int selectionWidth = 2;
	
	// Cursor
	protected CursorDescriptor cursor = new CursorDescriptor(CursorDescriptor.ARROW);
	
	/**
	 * Sets the colors of the figure scheme.
	 * @param foreground The foreground color of the figure.
	 * @param background The background color fo the figure.
	 */
	public void setColors(Color foreground, Color background) {
		this.foreground = foreground;
		this.background = background;
	}
	
	/**
	 * Sets the border of this figure scheme.
	 * @param color The color of the border.
	 * @param width The width of the border.
	 */
	public void setBorder(Color color, int width) {
		this.borderColor = color;
		this.borderWidth = width;
	}
	
	/**
	 * Gets the cursor of this figure scheme.
	 * @return Cursor The cursor of this figure scheme.
	 */
	public Cursor getCursor() {
		return cursor.getCursor();
	}
	
	/**
	 * Sets the cursor of this figure scheme.
	 * @param cursorId The Id in defined in CursorDescriptor.
	 * @see CursorDescriptor
	 */
	public void setCursor(int cursorId) {
		cursor.setCursor(cursorId);
	}
	
	/**
	 * Returns the background of this figure scheme.
	 * @return Color
	 */
	public Color getBackground() {
		return background;
	}
	
	/**
	 * Returns the border color of this figure scheme.
	 * @return Color The border color.
	 */
	public Color getBorderColor() {
		return borderColor;
	}

	/**
	 * Returns the border width of this figure scheme.
	 * @return int
	 */
	public int getBorderWidth() {
		return borderWidth;
	}

	/**
	 * Returns the captionBackground of this figure scheme.
	 * @return Color
	 */
	public Color getCaptionBackground() {
		return captionBackgroundColor;
	}

	/**
	 * Returns the captionText of this figure scheme.
	 * @return Color
	 */
	public Color getCaptionText() {
		return captionTextColor;
	}

	/**
	 * Returns the foreground color of this figure scheme.
	 * @return Color
	 */
	public Color getForeground() {
		return foreground;
	}

	/**
	 * Sets the background color of this figure scheme.
	 * @param background The background to set
	 */
	public void setBackground(Color background) {
		this.background = background;
	}

	/**
	 * Sets the border color of this figure scheme.
	 * @param borderColor The border color to set.
	 */
	public void setBorder(Color borderColor) {
		this.borderColor = borderColor;
	}

	/**
	 * Sets the border width of this figure scheme.
	 * @param borderWidth The borderWidth to set
	 */
	public void setBorderWidth(int borderWidth) {
		this.borderWidth = borderWidth;
	}
	
	/**
	 * Sets the caption colors of this figure scheme.
	 * @param foreground The foreground color of the caption.
	 * @param background The background color of the caption.
	 */
	public void setCaptionColors(Color foreground, Color background) {
		captionTextColor = foreground;
		captionBackgroundColor = background;
	}

	/**
	 * Sets the caption background color of this figure scheme.
	 * @param captionBackground The caption background color to set.
	 */
	public void setCaptionBackground(Color captionBackground) {
		this.captionBackgroundColor = captionBackground;
	}

	/**
	 * Sets the caption text color of this figure scheme.
	 * @param captionText The caption text color to set.
	 */
	public void setCaptionText(Color captionText) {
		this.captionTextColor = captionText;
	}

	/**
	 * Sets the foreground text color of this figure scheme.
	 * @param foreground The foreground text color to set.
	 */
	public void setForeground(Color foreground) {
		this.foreground = foreground;
	}

	/**
	 * Returns the description background color of this figure scheme.
	 * @return Color The description background color of this figure scheme.
	 */
	public Color getDescriptionBackground() {
		return descriptionBackgroundColor;
	}

	/**
	 * Returns the description text color of this figure scheme.
	 * @return Color The description text color of this figure scheme
	 */
	public Color getDescriptionText() {
		return descriptionTextColor;
	}

	/**
	 * Sets the description background color of this figure scheme.
	 * @param descriptionBackground The descriptionBackground to set.
	 */
	public void setDescriptionBackground(Color descriptionBackground) {
		this.descriptionBackgroundColor = descriptionBackground;
	}

	/**
	 * Sets the description text color of this figure scheme.
	 * @param descriptionText The descriptionText to set.
	 */
	public void setDescriptionText(Color descriptionText) {
		this.descriptionTextColor = descriptionText;
	}
	
	/**
	 * @return FontDescriptor
	 */
	public FontDescriptor getCaptionFont() {
		return captionFont;
	}

	/**
	 * @return FontDescriptor
	 */
	public FontDescriptor getDescriptionFont() {
		return descriptionFont;
	}
	
	/**
	 * Sets the caption font and its colors.
	 * @param name The name of the caption font to set.
	 * @param height The height of the caption font to set.
	 * @param style the style of the caption font to set.
	 * @param foreground The foreground color of the caption font to set.
	 * @param background The background color of the caption font to set.
	 */
	public void setCaptionFont(String name, int height, int style , Color foreground, Color background) {
		setCaptionFont(new FontDescriptor(name, height, style), foreground, background);
	}
	
	/**
	 * Sets the caption font and its colors.
	 * @param captionFont The caption font to set.
	 * @param foreground The foreground color of the caption font to set.
	 * @param background The background color of the caption font to set.
	 */
	public void setCaptionFont(FontDescriptor captionFont, Color foreground, Color background) {
		this.captionFont = captionFont;
		this.captionTextColor = foreground;
		this.captionBackgroundColor = background;
	}
	
	/**
	 * Sets the description font and its colors.
	 * @param name The name of the description font to set.
	 * @param height The height of the description font to set.
	 * @param style the style of the description font to set.
	 * @param foreground The foreground color of the description font to set.
	 * @param background The background color of the description font to set.
	 */
	public void setDescriptionFont(String name, int height, int style, Color foreground, Color background) {
		setDescriptionFont(new FontDescriptor(name, height, style), foreground, background);
	}
	
	/**
	 * Sets the description font and its colors.
	 * @param descriptionFont The description font to set.
	 * @param foreground The foreground color of the description font to set.
	 * @param background The background color of the description font to set.
	 */
	public void setDescriptionFont(FontDescriptor descriptionFont, Color foreground, Color background) {
		this.descriptionFont = descriptionFont;
		this.descriptionTextColor = foreground;
		this.descriptionBackgroundColor = background;
	}
	
	public void applyColors(IFigure figure) {
		figure.setForegroundColor(foreground);
		figure.setBackgroundColor(background);
	}

	public void applyCaptionColors(IFigure figure) {
		figure.setForegroundColor(captionTextColor);
		figure.setBackgroundColor(captionBackgroundColor);
	}

	public void applyDescriptionColors(IFigure figure) {
		figure.setForegroundColor(descriptionTextColor);
		figure.setBackgroundColor(descriptionBackgroundColor);
	}
	/**
	 * Sets the borderColor.
	 * @param borderColor The borderColor to set
	 */
	public void setBorderColor(Color borderColor) {
		this.borderColor = borderColor;
	}

	/**
	 * Sets the captionFont.
	 * @param captionFont The captionFont to set
	 */
	public void setCaptionFont(FontDescriptor captionFont) {
		this.captionFont = captionFont;
	}

	/**
	 * Sets the descriptionFont.
	 * @param descriptionFont The descriptionFont to set
	 */
	public void setDescriptionFont(FontDescriptor descriptionFont) {
		this.descriptionFont = descriptionFont;
	}
	/**
	 * @return Color
	 */
	public Color getSelectionColor() {
		return selectionColor;
	}

	/**
	 * @return int
	 */
	public int getSelectionWidth() {
		return selectionWidth;
	}

	/**
	 * Sets the selectionColor.
	 * @param selectionColor The selectionColor to set
	 */
	public void setSelectionColor(Color selectionColor) {
		this.selectionColor = selectionColor;
	}

	/**
	 * Sets the selectionWidth.
	 * @param selectionWidth The selectionWidth to set
	 */
	public void setSelectionWidth(int selectionWidth) {
		this.selectionWidth = selectionWidth;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public Object clone() {
		try {
			return super.clone();
		}
		catch(CloneNotSupportedException e) {
			
		}
		return null;
	}
}
