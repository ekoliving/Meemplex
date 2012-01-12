/*
 * Created on 4/03/2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.openmaji.implementation.tool.eclipse.editor.features.ui;

import java.io.Serializable;

/**
 * @author Kin Wong
 * FontDescriptor describes a font.
 */
public class FontDescriptor implements Cloneable, Serializable {
	private static final long serialVersionUID = 6424227717462161145L;

	private String name;
	private int height;
	private int style;
	/**
	 * Consturcts a new font descriptor.
	 * @param name The name of the font.
	 * @param height The height of the font.
	 * @param style The style of the font.
	 */
	public FontDescriptor(String name, int height, int style) {
		this.name = name;
		this.height = height;
		this.style = style;
	}

	/**
	 * Gets the name of the font.
	 * @return String The name of the font.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the style of the font.
	 * @return int The style of the font.
	 */
	public int getStyle() {
		return style;
	}

	/**
	 * Sets the name of the font.
	 * @param name The name of the font to set.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Sets the style of the font.
	 * @param style The style of the font to set.
	 */
	public void setStyle(int style) {
		this.style = style;
	}

	/**
	 * Gets the height of the font.
	 * @return int The height of the font.
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Sets the height of the font.
	 * @param height The height of the font to set.
	 */
	public void setHeight(int height) {
		this.height = height;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return name.hashCode() + height + style;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if(obj == null) return false;
		if(obj == this) return true;
		if(!(obj instanceof FontDescriptor)) return false;
		
		FontDescriptor descriptor = (FontDescriptor)obj;
		if(!name.equalsIgnoreCase(descriptor.name)) return false;
		return (height == descriptor.height) && (style == descriptor.style);
	}
}
