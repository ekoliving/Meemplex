/*
 * @(#)WeakReferenceProvider.java
 *
 * Copyright 2004 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */
package org.openmaji.implementation.server.meem.wedge.remote;

import java.lang.ref.WeakReference;

/**
 * @author mg
 */
public interface WeakReferenceProvider {

	public WeakReference obtainWeakReference();
	
}
