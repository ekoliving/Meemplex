/*
 * @(#)InterMajik.java
 * Created on 2/03/2004
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.system.presentation;

/**
 * <code>InterMajik</code> defines all the public keys for VariableMap 
 * used by InterMajik.
 * <p>
 * @author Kin Wong
 */
public interface InterMajik {
	/**
	 * Abstract Information about this meem. The value is of type 
	 * <code>Abstract<code>.
	 * <p>
	 * @see org.openmaji.system.meempool.metadata.Abstract
	 */
	static final String ABSTRACT_KEY = InterMajik.class.getName() + ".ABSTRACT";

	/**
	 * Iconic presentation of this meem. The value is of type 
	 * <code>MeemIconicPresentation<code>.
	 * <p>
	 * @see MeemIconicPresentation
	 */
	static final String ICONIC_PRESENTATION_KEY = InterMajik.class.getName() + ".ICONIC_PRESENTATION";
}
