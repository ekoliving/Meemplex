/*
 * @(#)HyperSpaceImport.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.server.nursery.space.hyperspace.export;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.openmaji.meem.Meem;
import org.openmaji.meem.MeemPath;
import org.openmaji.server.helper.EssentialMeemHelper;
import org.openmaji.server.helper.HyperSpaceHelper;
import org.openmaji.server.helper.ReferenceHelper;
import org.openmaji.system.space.Category;
import org.openmaji.system.space.meemstore.MeemStore;


/**
 * <p>
 * ...
 * </p>
 * @author  mg
 * @version 1.0
 */
public class HyperSpaceImport {

	ExportData exportData = null;
	HashMap meemIdMap = new HashMap();

	public synchronized boolean importMeems(MeemPath rootPath, ObjectInputStream input) {

		if (input != null) {
			try {
				exportData = (ExportData) input.readObject();
				input.close();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				return false;
			}
		}

		// trim trailing slash off root path

		String location = rootPath.getLocation();
		if (location.endsWith("/"))
			location = location.substring(0, location.length() - 1);

		rootPath = MeemPath.spi.create(rootPath.getSpace(), location);

		createMeems();
		createCategories(rootPath);

		return false;
	}

	private void createMeems() {

		// get MeemStore

		Meem meemStoreMeem = EssentialMeemHelper.getEssentialMeem(MeemStore.spi.getIdentifier());
		MeemStore meemStore = (MeemStore) ReferenceHelper.getTarget(meemStoreMeem, "meemStore", MeemStore.class);

		for (Iterator i = exportData.meems.entrySet().iterator(); i.hasNext();) {

			Map.Entry entry = (Map.Entry) i.next();

			MeemPath meemPath = (MeemPath)entry.getKey();
			ExportData.ExportedMeem meem = (ExportData.ExportedMeem) entry.getValue();

			meemStore.storeMeemDefinition(meemPath, meem.definition);

			meemStore.storeMeemContent(meemPath, meem.content);

		}
	}

	private void createCategories(MeemPath rootPath) {
		for (Iterator i = exportData.categories.keySet().iterator(); i.hasNext();) {
			String path = (String) i.next();

			Category newCategory = null;

			newCategory = HyperSpaceHelper.getInstance().getCategory(rootPath);
			

			HashMap categoryMeems = (HashMap) exportData.categories.get(path);

			if (categoryMeems.size() > 0) {

				for (Iterator j = categoryMeems.keySet().iterator(); j.hasNext();) {
					String name = (String) j.next();
					
					MeemPath meemPath = (MeemPath) categoryMeems.get(name);

					newCategory.addEntry(name, Meem.spi.get(meemPath));
				}
			}

		}

	}

}
