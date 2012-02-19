/*
 * @(#)Node.java
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
public class Node
{
	public static final double DEFAULT_HEIGHT = 2.0;
	public static final double DEFAULT_WIDTH = 2.0;
	public static final double DEFAULT_RADIUS = 1.0;
	public static final double DEFAULT_STRETCH = 1.0;
	public static final double DEFAULT_MASS = 1.0;
	public static final double DEFAULT_CHARGE = 0.75;

	public Node(Vector2D location)
	{
		this.location = location;
		this.height = DEFAULT_HEIGHT;
		this.width = DEFAULT_WIDTH;
		this.mass = DEFAULT_MASS;
		this.charge = DEFAULT_CHARGE;
	}

	public Node(Vector2D location, double height, double width, double mass, double charge)
	{
		this.location = location;
		this.height = height;
		this.width = width;
		this.mass = mass;
		this.charge = charge;
	}

	public void setLocking(boolean locked)
	{
		this.locked = locked;
	}

	public boolean isLocked()
	{
		return locked;
	}

	public Vector2D getLocation()
	{
		return location;
	}

	public double getHeight()
	{
		return height;
	}

	public double getWidth()
	{
		return width;
	}

	public double getMinX()
	{
		return location.x - width / 2;
	}

	public double getMinY()
	{
		return location.y - height / 2;
	}

	protected Vector2D location;
	protected double height, width;
	protected double mass, charge;
	protected Vector2D force = new Vector2D(0.0, 0.0);
	protected boolean locked = false;
}

