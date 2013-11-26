/*
 * @(#)MeemSpaceClientHelper.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - Nothing
 */
package org.openmaji.server.helper;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.openmaji.implementation.server.meem.definition.WedgeIntrospector;
import org.openmaji.implementation.server.meem.definition.WedgeIntrospectorException;
import org.openmaji.implementation.server.space.CategoryWedge;
import org.openmaji.meem.Meem;
import org.openmaji.meem.MeemPath;
import org.openmaji.meem.definition.MeemAttribute;
import org.openmaji.meem.definition.MeemDefinition;
import org.openmaji.meem.definition.Scope;
import org.openmaji.meem.definition.WedgeDefinition;
import org.openmaji.meem.space.Space;
import org.openmaji.meem.wedge.configuration.ConfigurationHandler;
import org.openmaji.meem.wedge.configuration.ConfigurationIdentifier;
import org.openmaji.meem.wedge.lifecycle.LifeCycleState;
import org.openmaji.system.gateway.AsyncCallback;
import org.openmaji.system.manager.thread.ThreadManager;
import org.openmaji.system.space.Category;
import org.openmaji.system.space.hyperspace.HyperSpace;


import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author mg Created on 20/01/2003
 */
public class HyperSpaceHelper {
	private static final Logger logger = Logger.getAnonymousLogger();

	private static Meem hyperSpaceMeem = null;

	private static HyperSpace hyperSpace = null;

	private static HyperSpaceHelper instance = new HyperSpaceHelper();

	private static WedgeDefinition categoryWedgeDefinition = null;
	
	private static Set<AsyncCallback<Meem>> hyperSpaceMeemCallbacks = new HashSet<AsyncCallback<Meem>>();

	private HyperSpaceHelper() {
	}

	public static HyperSpaceHelper getInstance() {
		return instance;
	}

	public synchronized void setHyperSpaceMeem(Meem meem) {
		if (hyperSpaceMeem == null) {
			hyperSpaceMeem = meem;

			deliverToCallbacks(meem);
			this.notifyAll();
		}
		else if (meem == null && hyperSpaceMeem != null) {
			hyperSpaceMeem = null;
			hyperSpace = null;
		}
		else {
			logger.log(Level.WARNING, "HyperSpace already found. Stale remote system Meems may not have expired in Jini Lookup Service.");
		}
	}

	// public static MeemPath getHyperSpaceMeemPath() {
	// return hyperSpaceMeemPath;
	// }

	public synchronized HyperSpace getHyperSpace() {
		if (hyperSpace == null && hyperSpaceMeem != null) {
			hyperSpace = ReferenceHelper.getTarget(getHyperSpaceMeem(), "hyperSpace", HyperSpace.class);
		}

		return hyperSpace;
	}
	
	/**
	 * Asynchronous and preferred way to get Hyperspace.
	 * @param callback
	 */
	public synchronized void getHyperSpace(final AsyncCallback<HyperSpace> callback) {
		if (hyperSpace == null && hyperSpaceMeem != null) {
			ReferenceHelper.getTarget(getHyperSpaceMeem(), "hyperSpace", HyperSpace.class, new AsyncCallback<HyperSpace>() {
				public void result(HyperSpace result) {
					hyperSpace = result;
					callback.result(result);
				};
				public void exception(Exception e) {
					callback.exception(e);
				}
			});
		}
		else {
			callback.result(hyperSpace);
		}
	}
	
	public synchronized Meem getHyperSpaceMeem() {
		while (hyperSpaceMeem == null) {
			// we haven't been told about hyperspace yet
			// so we'll just wait a little bit and hope it turns up
			// (this is quite dodgy, but so are get()s
			try {
				this.wait(30000);
			}
			catch (InterruptedException e) {
				break;
			}
		}

		return hyperSpaceMeem;
	}
	
	public synchronized void getHyperSpaceMeem(AsyncCallback<Meem> callback) {
		if (hyperSpaceMeem == null) {
			synchronized (hyperSpaceMeemCallbacks) {
				hyperSpaceMeemCallbacks.add(callback);
			}
		}
		else {
			callback.result(hyperSpaceMeem);
		}
	}

	public synchronized boolean isHyperSpaceSet() {
		return (hyperSpaceMeem != null);
	}

	public Category getCategory(MeemPath meemPath) {
		Meem meem = MeemPathResolverHelper.getInstance().resolveMeemPath(meemPath);
		return (Category) ReferenceHelper.getTarget(meem, "category", Category.class);
	}

	public static MeemDefinition getCategoryDefinition() {

		MeemAttribute meemAttribute = new MeemAttribute();
		meemAttribute.setScope(Scope.LOCAL);
		meemAttribute.setIdentifier("Category");
		meemAttribute.setVersion(1);

		MeemDefinition meemDefinition = new MeemDefinition(meemAttribute);

		try {
			if (categoryWedgeDefinition == null)
				categoryWedgeDefinition = WedgeIntrospector.getWedgeDefinition(CategoryWedge.class);

			meemDefinition.addWedgeDefinition(categoryWedgeDefinition);

		}
		catch (WedgeIntrospectorException e) {
			e.printStackTrace();
		}

		return meemDefinition;
	}

	/**
	 * Creates a full path from given string. Creates sub-categories as needed.
	 * 
	 * @param path
	 *            String of the format /cat1/cat2/cat3
	 * @return Last category in path as a Meem
	 */
	public Meem createPath(String path) {
		return createPath(path, true);
	}

	/**
	 * Creates a full path from given string. Creates sub-categories as needed. Any categories created are created as Transient Meems.
	 * 
	 * @param path
	 *            String of the format /cat1/cat2/cat3
	 * @return Last category in path as a Meem
	 */
	public Meem createTransientPath(String path) {
		return createPath(path, true);
	}

	/**
	 * Creates a full path from given string. Creates sub-categories as needed.
	 * 
	 * @param path
	 *            String of the format /cat1/cat2/cat3
	 * @param persistent
	 *            whether persistent or transient category Meems should be created
	 * @return Last category in path as a Meem
	 */
	protected Meem createPath(String path, boolean persistent) {
		List<String> paths = new LinkedList<String>();

		StringTokenizer tok = new StringTokenizer(path, "/");
		while (tok.hasMoreTokens()) {
			paths.add(tok.nextToken());
		}

		// work backwards through path to see if any of the categories already exist.

		int iUpto = paths.size() + 1;

		Meem meem = null;
		do {
			iUpto--;

			StringBuffer pathBuffer = new StringBuffer();
			for (int i = 0; i < iUpto; i++) {
				pathBuffer.append("/");
				pathBuffer.append((String) paths.get(i));
			}

			MeemPath currentPath = MeemPath.spi.create(Space.HYPERSPACE, pathBuffer.toString());

			meem = MeemPathResolverHelper.getInstance().resolveMeemPath(currentPath);
		} while (meem == null);

		// meem should be a Category

		Category category = (Category) ReferenceHelper.getTarget(meem, "category", Category.class);

		Meem newCategoryMeem = meem;

		for (int i = iUpto; i < paths.size(); i++) {
			// create a new category
			if (persistent) {
				newCategoryMeem = LifeCycleManagerHelper.doCreateMeem(getCategoryDefinition(), newCategoryMeem, LifeCycleState.READY);
			}
			else {
				newCategoryMeem = LifeCycleManagerHelper.createTransientMeem(getCategoryDefinition());
			}

			category.addEntry((String) paths.get(i), newCategoryMeem);

			category = (Category) ReferenceHelper.getTarget(newCategoryMeem, "category", Category.class);

			ConfigurationHandler ch = (ConfigurationHandler) ReferenceHelper.getTarget(newCategoryMeem, "configurationHandler", ConfigurationHandler.class);
			ConfigurationIdentifier ci = new ConfigurationIdentifier("MeemSystemWedge", "meemIdentifier");
			ch.valueChanged(ci, (String) paths.get(i));
		}

		return newCategoryMeem;
	}
	
	/**
	 * Send hyperspace meem to callbacks waiting for the meem.
	 * @param hyperSpaceMeem
	 */
	private void deliverToCallbacks(final Meem hyperSpaceMeem) {
		// deliver to callbacks
		ThreadManager.spi.create().queue(new Runnable() {
			public void run() {
				synchronized (hyperSpaceMeemCallbacks) {
					for (AsyncCallback<Meem> callback : hyperSpaceMeemCallbacks) {
						callback.result(hyperSpaceMeem);
					}
					hyperSpaceMeemCallbacks.clear();
				}
			}
		});

	}
}
