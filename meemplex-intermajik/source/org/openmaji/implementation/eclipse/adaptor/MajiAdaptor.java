/*
 * @(#)MajiAdaptor.java
 *
 * Copyright 2004 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
package org.openmaji.implementation.eclipse.adaptor;

import org.eclipse.osgi.baseadaptor.BaseAdaptor;

/**
 * Not used at the moment.
 */
public class MajiAdaptor extends BaseAdaptor {

	/*
	 * Should be instantiated only by the framework (through reflection). 
	 */
	public MajiAdaptor(String[] args) {
		super(args);
		getHookRegistry().addClassLoadingHook(new MajiClassLoadingHook());
	}

}
