/*
 * @(#)MeemDropRequest.java
 * Created on 29/04/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.common.dnd.requests;

import org.openmaji.implementation.intermajik.model.ViewModeConstants;
import org.openmaji.implementation.tool.eclipse.client.InterMajikClientProxyFactory;
import org.openmaji.implementation.tool.eclipse.client.MeemClientProxy;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.Meem;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.MeemModelFactory;
import org.openmaji.implementation.tool.eclipse.ui.dnd.NamedMeem;


/**
 * <code>MeemDropRequest</code>.
 * <p>
 * @author Kin Wong
 */
public class MeemDropRequest extends NamedMeemRequest {
	public static final String REQ_MEEM_DROP = "Meem Drop Request";

	/**
	 * Constructs an instance of <code>MeemDropRequest</code>.
	 * <p>
	 */
	public MeemDropRequest() {
		super(REQ_MEEM_DROP);
	}

	protected Object createNewObject(NamedMeem namedMeem) {
		MeemClientProxy proxy = InterMajikClientProxyFactory.getInstance().
			locate(namedMeem.getMeemPath());
		Meem meem = MeemModelFactory.create(proxy);
		meem.setName(namedMeem.getName());
		meem.setViewMode(ViewModeConstants.VIEW_MODE_DETAILED);
		meem.setCollapse(false);
		return meem;
	}
}
