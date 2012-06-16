/*
 * @(#)WeakReferenceProvider.java
 *
 * Copyright 2004 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
package org.openmaji.implementation.server.meem.wedge.remote;

import java.lang.ref.WeakReference;

/**
 * @author mg
 */
public interface WeakReferenceProvider <T> {

	public WeakReference<T> obtainWeakReference();
	
}
