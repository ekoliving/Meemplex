/*
 * @(#)MeemPathResolverTest.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.tool.eclipse.search.test;


import org.openmaji.meem.*;
import org.openmaji.meem.space.Space;

/**
    * <p>
    * ...
    * </p>
    * @author  mg
    * @version 1.0
    */
public class MeemPathResolverTest {

	public MeemPathResolverTest() {
		Meem.spi.get(MeemPath.spi.create(Space.HYPERSPACE, "/category 1/child 1"));

	}

	public static void main(String[] args) {
//		MeemPathResolverTest meemPathResolverTest =
		new MeemPathResolverTest();
	}



}
