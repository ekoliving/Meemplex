/*
 * @(#)ControllerAction.java
 * Created on 26/05/2004
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.hierarchy.controllers;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

/**
 * <code>ControllerAction</code>.
 * <p>
 * @author Kin Wong
 */
public class ControllerAction extends Action {
	private Controller controller;
	/**
	 * Constructs an instance of <code>ControllerAction</code>.
	 * <p>
	 * 
	 */
	public ControllerAction(Controller controller) {
		super();
		this.controller = controller;
	}

	/**
	 * Constructs an instance of <code>ControllerAction</code>.
	 * <p>
	 * @param text
	 */
	public ControllerAction(Controller controller, String text) {
		super(text);
		this.controller = controller;
	}

	/**
	 * Constructs an instance of <code>ControllerAction</code>.
	 * <p>
	 * @param text
	 * @param image
	 */
	public ControllerAction(Controller controller, String text, ImageDescriptor image) {
		super(text, image);
		this.controller = controller;
	}

	/**
	 * Constructs an instance of <code>ControllerAction</code>.
	 * <p>
	 * @param text
	 * @param style
	 */
	public ControllerAction(Controller controller, String text, int style) {
		super(text, style);
		this.controller = controller;
	}
	
	public Controller getController() {
		return controller;
	}
	
	public void update() {
	}
}
