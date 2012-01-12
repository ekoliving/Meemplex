/*
 * @(#)MeemContentPersistenceDelegate.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.server.nursery.space.meemstore.beanxml;

import java.beans.DefaultPersistenceDelegate;
import java.beans.Encoder;
import java.beans.Statement;
import java.util.*;

import org.openmaji.system.meem.definition.MeemContent;


/**
 * <p>
 * ...
 * </p>
 * @author  mg
 * @version 1.0
 */
public class MeemContentPersistenceDelegate extends DefaultPersistenceDelegate {

	/**
	 * @see java.beans.PersistenceDelegate#initialize(java.lang.Class, java.lang.Object, java.lang.Object, java.beans.Encoder)
	 */
	protected void initialize(Class type, Object oldInstance, Object newInstance, Encoder out) {
		super.initialize(type, oldInstance, newInstance, out);

		MeemContent content = (MeemContent) oldInstance;

		Collection wedgeNames = content.getWedgeIdentifiers();

		for (Iterator iNames = wedgeNames.iterator(); iNames.hasNext();) {
			String wedgeName = (String) iNames.next();

			Map wedgeFields = content.getPersistentFields(wedgeName);

			if (wedgeFields != null) {
				for (Iterator iFields = wedgeFields.entrySet().iterator(); iFields.hasNext();) {

					Map.Entry entry = (Map.Entry) iFields.next();

					out.writeStatement(
						new Statement(oldInstance, "addWedgeField", new Object[] { wedgeName, entry.getKey(), entry.getValue()}));
				}
			}
		}
	}
	
}
