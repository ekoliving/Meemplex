/*
 * @(#)CategoryHelper.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - Consider throwing a RuntimeException if CategoryClient.entryRemoved() or
 *   CategoryClient.entryRenamed() are invoked.
 */

package org.openmaji.server.helper;

import java.util.Hashtable;
import java.util.Map;

import org.openmaji.implementation.server.manager.gateway.GatewayManagerWedge;
import org.openmaji.meem.Facet;
import org.openmaji.meem.Meem;
import org.openmaji.meem.filter.ExactMatchFilter;
import org.openmaji.meem.filter.Filter;
import org.openmaji.meem.wedge.reference.Reference;
import org.openmaji.server.utility.PigeonHole;
import org.openmaji.server.utility.TimeoutException;
import org.openmaji.system.meem.wedge.reference.ContentClient;
import org.openmaji.system.space.CategoryClient;
import org.openmaji.system.space.CategoryEntry;
import org.swzoo.log2.core.LogFactory;
import org.swzoo.log2.core.LogTools;
import org.swzoo.log2.core.Logger;

/**
 * <p>
 * CategoryHelper provides synchronous convenience method(s) for interacting with Categories.
 * </p>
 * <p>
 * These methods can be used by Meems that require a blocking call or by Java objects that aren't Meems and need an entry point into the Maji system.
 * </p>
 * <p>
 * WARNING: Well-designed asynchronous Meems MUST NOT use this Helper !
 * </p>
 * <p>
 * Okay, if your Meem really, really needs to use this Helper, then make sure that you use a timeout parameter with a short duration. And, be prepared to deal with failure in the
 * form of an Exception.
 * </p>
 * <p>
 * Note: Implementation thread safe = Yes (2003-08-14)
 * </p>
 * 
 * @author Andy Gelme
 * @version 1.0
 */

public class CategoryHelper {

	/**
	 * Default in-bound Category Facet identifier
	 */

	public final static String DEFAULT_CATEGORY_INBOUND_FACET_IDENTIFER = "category";

	/**
	 * Default out-bound Category Facet identifier
	 */

	public final static String DEFAULT_CATEGORY_OUTBOUND_FACET_IDENTIFIER = "categoryClient";

	/**
	 * Synchronous convenience method for getting a table of CategoryEntries from a specified Category Meem. The default out-bound CategoryClient Facet identifier will be used.
	 * 
	 * @param categoryMeem
	 *            Meem that provides an out-bound CategoryClient Facet
	 * @return Hashtable of CategoryEntries
	 */

	@SuppressWarnings("unchecked")
	public static Map<String, CategoryEntry> getCategoryEntries(Meem categoryMeem) {
		PigeonHole pigeonHole = new PigeonHole();
		CategoryClient categoryClient = new AllEntriesCallback(pigeonHole);
		Facet proxy = GatewayManagerWedge.getTargetFor(categoryClient, CategoryClient.class);

		Reference reference = Reference.spi.create(DEFAULT_CATEGORY_OUTBOUND_FACET_IDENTIFIER, proxy, true);

		categoryMeem.addOutboundReference(reference, true);

		try {
			return (Map<String, CategoryEntry>) pigeonHole.get(TIMEOUT);
		}
		catch (TimeoutException e) {
			LogTools.info(logger, "Timeout waiting for CategoryEntries", e);
			return null;
		}
		finally {
			GatewayManagerWedge.revokeTarget(proxy, categoryClient);
		}
	}

	public static CategoryEntry getCategoryEntry(Meem categoryMeem, String entryName) {
		PigeonHole pigeonHole = new PigeonHole();
		CategoryClient categoryClient = new SingleEntryCallback(pigeonHole);
		Facet proxy = GatewayManagerWedge.getTargetFor(categoryClient, CategoryClient.class);
		Filter filter = new ExactMatchFilter(entryName);

		Reference reference = Reference.spi.create(DEFAULT_CATEGORY_OUTBOUND_FACET_IDENTIFIER, proxy, true, filter);

		categoryMeem.addOutboundReference(reference, true);

		try {
			return (CategoryEntry) pigeonHole.get(TIMEOUT);
		}
		catch (TimeoutException e) {
			LogTools.info(logger, "Timeout waiting for CategoryEntry", e);
			return null;
		}
		finally {
			GatewayManagerWedge.revokeTarget(proxy, categoryClient);
		}
	}

	public static class AllEntriesCallback implements CategoryClient, ContentClient {
		public AllEntriesCallback(PigeonHole pigeonHole) {
			this.pigeonHole = pigeonHole;
		}

		public void entriesAdded(CategoryEntry[] entries) {
			for (int i = 0; i < entries.length; ++i) {
				CategoryEntry entry = entries[i];
				allEntries.put(entry.getName(), entry);
			}
		}

		public void entriesRemoved(CategoryEntry[] entries) {
		}

		public void entryRenamed(CategoryEntry oldEntry, CategoryEntry newEntry) {
		}

		public void contentSent() {
			if (pigeonHole != null) {
				pigeonHole.put(allEntries);
				pigeonHole = null;
			}
		}

		public void contentFailed(String reason) {
			if (pigeonHole != null) {
				pigeonHole.put(null);
				pigeonHole = null;
			}
		}

		private PigeonHole pigeonHole;

		private Hashtable<String, CategoryEntry> allEntries = new Hashtable<String, CategoryEntry>();
	};

	public static class SingleEntryCallback implements CategoryClient, ContentClient {
		public SingleEntryCallback(PigeonHole pigeonHole) {
			this.pigeonHole = pigeonHole;
		}

		public void entriesAdded(CategoryEntry[] entries) {
			this.singleEntry = entries[0];
		}

		public void entriesRemoved(CategoryEntry[] entries) {
		}

		public void entryRenamed(CategoryEntry oldEntry, CategoryEntry newEntry) {
		}

		public void contentSent() {
			if (pigeonHole != null) {
				pigeonHole.put(singleEntry);
				pigeonHole = null;
			}
		}

		public void contentFailed(String reason) {
			if (pigeonHole != null) {
				pigeonHole.put(null);
				pigeonHole = null;
			}
		}

		private PigeonHole pigeonHole;

		private CategoryEntry singleEntry = null;
	};

	private static final long TIMEOUT = Long.parseLong(System.getProperty(ReferenceHelper.PIGEONHOLE_TIMEOUT, "30000"));

	private static final Logger logger = LogFactory.getLogger();
}
