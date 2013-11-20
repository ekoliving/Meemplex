/*
 * @(#)WorksheetLifeCycleManagerMeem.java
 *
 * Copyright 2004 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.server.nursery.worksheet;

import org.openmaji.implementation.server.manager.lifecycle.lazy.LazyLifeCycleManagerMeem;
import org.openmaji.meem.definition.MeemAttribute;
import org.openmaji.meem.definition.MeemDefinition;
import org.openmaji.meem.definition.MeemDefinitionProvider;



/**
 * <p>
 * ...
 * </p>
 *
 * @author  mg
 * @version 1.0
 */
public class WorksheetLifeCycleManagerMeem implements MeemDefinitionProvider {
	
	public static final String IDENTIFIER = "worksheetLifeCycleManager";

	/* ---------- MeemDefinitionProvider method(s) ----------------------------- */

	private MeemDefinition meemDefinition = null;

	public MeemDefinition getMeemDefinition() {
		if (meemDefinition == null) {
			meemDefinition = LazyLifeCycleManagerMeem.getMeemDefinition();
			
			MeemAttribute meemAttribute = new MeemAttribute(IDENTIFIER);
			
			meemDefinition.setMeemAttribute(meemAttribute);

		}
		return (meemDefinition);
	}

}