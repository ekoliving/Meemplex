/*
 * @(#)Vector2D.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
package org.openmaji.implementation.tool.eclipse.editor.common.layout;

/**
 * @author Peter
 */
public class Vector2D
{
	public Vector2D()
	{
		this.x = 0.0;
		this.y = 0.0;
	}

	public Vector2D(double x, double y)
	{
		this.x = x;
		this.y = y;
	}

	public double getX()
	{
		return x;
	}
	
	public double getY()
	{
		return y;
	}

	public String toString()
	{
		return "[" + x + ", " + y + "]";
	}

	protected double x, y;
}
