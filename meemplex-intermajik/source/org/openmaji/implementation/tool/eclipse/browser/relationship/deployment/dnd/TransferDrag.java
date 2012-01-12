/*
 * @(#)TransferDrag.java
 *
 * Copyright 2005 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */
package org.openmaji.implementation.tool.eclipse.browser.relationship.deployment.dnd;

import java.io.Serializable;

import org.openmaji.meem.MeemPath;


/**
 * @author mg
 */
public class TransferDrag implements Serializable {
	private static final long serialVersionUID = 6424227717462161145L;

	private final MeemPath meemPath;
	private final MeemPath parentMeemPath;
	
	public TransferDrag(MeemPath meemPath, MeemPath parentMeemPath) {
		this.meemPath = meemPath;
		this.parentMeemPath = parentMeemPath;
	}
	
	/**
	 * @return Returns the meemPath.
	 */
	public MeemPath getMeemPath() {
		return meemPath;
	}
	/**
	 * @return Returns the parentMeemPath.
	 */
	public MeemPath getParentMeemPath() {
		return parentMeemPath;
	}
}
