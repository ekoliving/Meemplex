/*
 * @(#)NamedMeemCloneTransfer.java
 * Created on 15/06/2004
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.ui.dnd;

/**
 * <code>MeemCloneTransfer</code>.<p>
 * @author Kin Wong
 */
public class NamedMeemCloneTransfer extends SerializableTransfer {
	private static final String NAMED_MEEM_TRANSFER = 
		NamedMeemCloneTransfer.class + ".type";
	
	private static final int TYPE = registerType(NAMED_MEEM_TRANSFER);
	
	//private static NamedMeemCloneTransfer instance = new NamedMeemCloneTransfer();
	
	/**
	 * Constructs an instance of <code>NamedMeemCloneTransfer</code>.<p>
	 */	
	public NamedMeemCloneTransfer() {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.dnd.Transfer#getTypeIds()
	 */
	protected int[] getTypeIds() {
		return new int[]{TYPE};
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.dnd.Transfer#getTypeNames()
	 */
	protected String[] getTypeNames() {
		return new String[]{NAMED_MEEM_TRANSFER};
	}
}
