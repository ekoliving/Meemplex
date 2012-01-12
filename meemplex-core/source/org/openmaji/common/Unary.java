/*
 * Copyright 2005 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */
package org.openmaji.common;
import org.openmaji.meem.Facet;
/**
 * The Unary Facet is used to inform something changed. This
 * facet is implemented by wedges which simply respond to
 * the change.
 *   
 * @author Diana Huang
 * 
 */
public interface Unary extends Facet{
	/**
	 * Inform the change
	 */
	public void valueChanged();
}
