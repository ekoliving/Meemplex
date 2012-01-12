/*
 * @(#)MeemClient.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.meem;



import org.openmaji.meem.wedge.reference.Reference;

/**
 * <p>
 * The MeemClient Facet reports adding and removing of References.
 * </p>
  * @author  Andy Gelme
 * @version 1.0
 * @see org.openmaji.meem.wedge.reference.Reference
 */
public interface MeemClient extends Facet {

	/**
	 * Report that a <code>Reference</code> has been added.
	 * 
	 * @param reference <code>Reference</code> that was added. 
	 */
	public void referenceAdded(Reference reference);

	/**
	 * Report that a previously-added <code>Reference</code> has been removed.
	 * 
	 * @param reference <code>Reference</code> that was removed. 
	 */
	public void referenceRemoved(Reference reference);


/* ---------- Nested class for SPI ----------------------------------------- */

	public class spi {
		public static String getIdentifier() {
			return ("meemClient");
		};
	}
}