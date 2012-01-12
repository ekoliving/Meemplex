/*
 * @(#)ExportData.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.server.nursery.space.hyperspace.export;

import java.io.Serializable;
import java.util.LinkedHashMap;

import org.openmaji.meem.definition.MeemDefinition;
import org.openmaji.system.meem.definition.MeemContent;


/**
 * <p>
 * ...
 * </p>
 * @author  mg
 * @version 1.0
 */
public class ExportData implements Serializable {
	private static final long serialVersionUID = 534540102928363464L;

	public LinkedHashMap categories = new LinkedHashMap();
	public LinkedHashMap meems = new LinkedHashMap();

	public class ExportedMeem implements Serializable {
		private static final long serialVersionUID = 534540102928363464L;
		public MeemContent content;
		public MeemDefinition definition;
	}

}
