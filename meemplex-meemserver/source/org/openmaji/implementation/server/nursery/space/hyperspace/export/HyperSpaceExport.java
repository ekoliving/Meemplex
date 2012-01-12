/*
 * @(#)HyperSpaceExport.java
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

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;


import org.openmaji.implementation.server.space.hyperspace.utility.CategoryTraverser;
import org.openmaji.meem.Meem;
import org.openmaji.meem.MeemPath;
import org.openmaji.meem.definition.MeemDefinition;
import org.openmaji.meem.filter.ExactMatchFilter;
import org.openmaji.meem.space.Space;
import org.openmaji.meem.wedge.reference.Reference;
import org.openmaji.server.helper.EssentialMeemHelper;
import org.openmaji.server.helper.ReferenceHelper;
import org.openmaji.server.utility.PigeonHole;
import org.openmaji.server.utility.TimeoutException;
import org.openmaji.system.meem.definition.MeemContent;
import org.openmaji.system.meem.wedge.persistence.ManagedPersistenceClient;
import org.openmaji.system.meem.wedge.persistence.ManagedPersistenceHandler;
import org.openmaji.system.space.meemstore.MeemDefinitionClient;
import org.openmaji.system.space.meemstore.MeemStore;
import org.swzoo.log2.core.LogFactory;
import org.swzoo.log2.core.LogTools;
import org.swzoo.log2.core.Logger;


/**
 * <p>
 * Given a root category, export all child categories/meems.
 * </p>
 * @author  mg
 * @version 1.0
 */
public class HyperSpaceExport implements ManagedPersistenceClient, MeemDefinitionClient {

	private PigeonHole definitionHole = new PigeonHole();
	private PigeonHole contentHole = new PigeonHole();
	private static final long timeout = 60000;

	//private boolean scanningMeem = false;

	private LinkedHashSet meemsToScan = new LinkedHashSet();

	private MeemStore meemStore;
	private Meem meemStoreMeem;

	ExportData exportData = new ExportData();

	int iUpto = 0;

	public synchronized boolean exportMeems(MeemPath meemPath, ObjectOutputStream output) {

		CategoryTraverser traverser = new CategoryTraverser();

		Map meems = traverser.traverse(meemPath);

		parseMeems(meems);

		if (output != null) {
			try {
				output.writeObject(exportData);
				output.close();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	private void parseMeems(Map meems) {
		exportData.categories.put("", new HashMap());

		for (Iterator i = meems.entrySet().iterator(); i.hasNext();) {
			Map.Entry entry = (Map.Entry) i.next();

			String currentMeemName = (String) entry.getKey();
			MeemPath meemPath = (MeemPath) entry.getValue();

			Meem meem = Meem.spi.get(meemPath);

			String categoryName = currentMeemName.substring(0, currentMeemName.lastIndexOf("/"));
			String meemName = currentMeemName.substring(currentMeemName.lastIndexOf("/") + 1);

			if (categoryName.equals("")) {
				((HashMap) exportData.categories.get(categoryName)).put(meemName, meemPath);
			}

			if (meemsToScan.add(meemPath)) {
				//scanningMeem = true;
				// haven't scanned this one yet

				ExportData.ExportedMeem exportedMeem = exportData.new ExportedMeem();

				// get content

				ManagedPersistenceHandler meemPersist =
					(ManagedPersistenceHandler) ReferenceHelper.getTarget(
						meem,
						"managedPersistenceHandler",
						ManagedPersistenceHandler.class);

				// add ourselves as a persistanceClient for this meem 
				Reference referencePersistance = Reference.spi.create("managedPersistenceClient", this, false, null);

				meem.addOutboundReference(referencePersistance, false);

				meemPersist.persist();

				try {
					exportedMeem.content = (MeemContent) contentHole.get(timeout);
				} catch (TimeoutException ex) {
					LogTools.info(logger, "Timout waiting for MeemContent", ex);
				}

				// remove ourselves as a persistanceClient
				meem.removeOutboundReference(referencePersistance);

				// get definition				

				getMeemStore();

				MeemPath meemStorePath = MeemPath.spi.create(Space.MEEMSTORE, meem.getMeemPath().getLocation());

				Reference meemDefinitionClientReference =
					Reference.spi.create("meemDefinitionClient", this, true, new ExactMatchFilter(meemStorePath));

				meemStoreMeem.addOutboundReference(meemDefinitionClientReference, false);

				try {
					exportedMeem.definition = (MeemDefinition) definitionHole.get(timeout);
				} catch (TimeoutException ex) {
					LogTools.info(logger, "Timeout waiting for MeemDefinition", ex);
				}

				meemStoreMeem.removeOutboundReference(meemDefinitionClientReference);

				exportData.meems.put(meem.getMeemPath(), exportedMeem);

				//scanningMeem = false;
			}

		}

	}

	private synchronized MeemStore getMeemStore() {
		if (meemStore == null) {
			meemStoreMeem = EssentialMeemHelper.getEssentialMeem(MeemStore.spi.getIdentifier());		

			meemStore = (MeemStore) ReferenceHelper.getTarget(meemStoreMeem, "meemStore", MeemStore.class);
		}
		return meemStore;
	}

	/**
	 */
	public void meemContentChanged(MeemPath meemPath, MeemContent meemContent) {
		contentHole.put(meemContent);
	}

	/**
	 */
	public void restored(MeemPath meemPath) {
		// don't cate - ignore
	}

	/**
	 */
	public void meemDefinitionChanged(MeemPath meemPath, MeemDefinition meemDefinition) {
		definitionHole.put(meemDefinition);
	}

	/** Logger for the class */
	private static Logger logger = LogFactory.getLogger();
}
