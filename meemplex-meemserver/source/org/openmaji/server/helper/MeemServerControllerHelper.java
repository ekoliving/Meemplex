/*
 * @(#)MeemServerControllerHelper.java
 *
 * Copyright 2004 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.server.helper;

import org.openmaji.meem.Meem;
import org.openmaji.system.meemserver.controller.MeemServerController;



/**
 * <p>
 * ...
 * </p>
 *
 * @author  mg
 * @version 1.0
 */
public class MeemServerControllerHelper {

	private static Meem meemServerControllerMeem = null;
	private static MeemServerControllerHelper instance = new MeemServerControllerHelper();

	private MeemServerControllerHelper() {
	}

	public static MeemServerControllerHelper getInstance() {
		return instance;
	}

	public Meem getMeemServerControllerMeem() {
		if (meemServerControllerMeem == null) {

			meemServerControllerMeem = EssentialMeemHelper.getEssentialMeem(MeemServerController.spi.getIdentifier());
		}
		return meemServerControllerMeem;
	}

}
