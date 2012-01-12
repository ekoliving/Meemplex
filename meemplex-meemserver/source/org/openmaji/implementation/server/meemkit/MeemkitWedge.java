/*
 * Copyright 2004 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.server.meemkit;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import org.openmaji.implementation.server.classloader.MeemkitClassLoader;
import org.openmaji.implementation.server.classloader.SystemExportList;
import org.openmaji.implementation.server.manager.lifecycle.meemkit.MeemkitUtility;

import org.openmaji.meem.Meem;
import org.openmaji.meem.MeemPath;
import org.openmaji.meem.Wedge;
import org.openmaji.meem.definition.MeemDefinition;
import org.openmaji.meem.definition.MeemDefinitionFactory;
import org.openmaji.meem.definition.MeemDefinitionProvider;
import org.openmaji.meem.wedge.lifecycle.*;
import org.openmaji.meem.wedge.reference.Reference;
import org.openmaji.system.meem.core.MeemCore;
import org.openmaji.system.meem.wedge.reference.MeemClientCallback;
import org.openmaji.system.meem.wedge.reference.MeemClientConduit;
import org.openmaji.system.meemkit.core.MeemkitDescriptor;
import org.openmaji.system.meemkit.core.MeemkitLibrary;
import org.openmaji.system.meemkit.core.MeemkitLibraryExport;
import org.swzoo.log2.core.LogFactory;
import org.swzoo.log2.core.LogTools;
import org.swzoo.log2.core.Logger;

public class MeemkitWedge implements Wedge, Meemkit, MeemDefinitionProvider {
	private static final Logger logger = LogFactory.getLogger();

	public MeemCore meemCore;

	public LifeCycleClient lifeCycleClientConduit = new LifeCycleClientAdapter(this);

	public Meemkit meemkitClient;

	public LifeCycle lifeCycleConduit;

	public MeemClientConduit meemClientConduit;

	private URL lastDescriptorURL = null;

	private MeemkitClassLoader meemkitClassLoader = null;

	private Set<MeemPath> referencedMeems = new HashSet<MeemPath>();

	// private String myName;

	/* ------------------ Meem functionality ---------------------------------- */

	public void commence() {
		// myName = meemCore.getMeemStructure().getMeemAttribute().getIdentifier();
		// LogTools.info(logger, "commence() - myName=["+myName+"]");
	}

	public void conclude() {
		// LogTools.info(logger, "conclude() - myName=["+myName+"]");
		shutdownClassLoader();
	}

	/* --------- inbound Meemkit facet methods -------------------------------- */

	public void detailsChanged(String[] names, URL[] descriptorLocations) {
		if (names.length != 1) {
			// TODO FIX THIS
			return;
		}
		URL descriptorLocation = descriptorLocations[0];
		String name = names[0];

		if (descriptorLocation == null) {
			// meemkit is no longer valid so shutdown
			lifeCycleConduit.changeLifeCycleState(LifeCycleState.ABSENT);
			return;
		}

		if (lastDescriptorURL != null && meemkitClassLoader != null) {
			// we've already started, so lets shutdown our classloader to be ready to start a new one

			shutdownClassLoader();
		}

		// get the MeemDefinition

		MeemkitDescriptor descriptor = MeemkitUtility.createMeemkitLibrariesDescriptor(descriptorLocation);

		if (descriptor == null) {
			// something went wrong

			// put error on errorhandler conduit and return

			return;
		}

		// Get the base URL

		String urlPath = descriptorLocation.getPath();
		String descriptorName = null;
		if (descriptorLocation.getProtocol().equals("http")) {
			descriptorName = urlPath.substring(urlPath.lastIndexOf("/") + 1);
		}
		else {
			descriptorName = urlPath.substring(urlPath.lastIndexOf(File.separator) + 1);
		}
		String baseURL = descriptorLocation.toExternalForm();
		baseURL = baseURL.substring(0, baseURL.length() - descriptorName.length());

		// create ClassLoader

		if (createClassLoader(descriptor, baseURL)) {
			// set ClassLoader export list

			setExportList(descriptor, baseURL);

			LogTools.info(logger, "Meemkit ClassLoader Started [" + name + "]");

			// stop any meems that used previous instances of this meemkits classloader

			stopReferencedMeems();

			lastDescriptorURL = descriptorLocation;

			// notify clients of success

			meemkitClient.detailsChanged(new String[] { name }, new URL[] { descriptorLocation });

		}
	}

	private boolean createClassLoader(MeemkitDescriptor descriptor, String baseURL) {

		// TODO creaton of classloader disabled by Warren 10/8/2011
		if (true) {
			return false;
		}

		MeemkitLibrary[] libraries = descriptor.getLibraries();
		if (libraries == null) {
			throw new RuntimeException("Meemkit " + descriptor.getHeader().getName() + " does not export any libraries");
		}

		URL[] urls = new URL[libraries.length];

		for (int i = 0; i < libraries.length; i++) {
			MeemkitLibrary library = libraries[i];
			try {
				urls[i] = new URL(baseURL + library.getName());
			}
			catch (MalformedURLException e) {
				e.printStackTrace();
				return false;
			}
		}

		meemkitClassLoader = new MeemkitClassLoader(descriptor.getHeader().getName(), urls, this.getClass().getClassLoader());

		return true;
	}

	private void setExportList(MeemkitDescriptor descriptor, String baseURL) {
		MeemkitLibrary[] libraries = descriptor.getLibraries();

		for (int i = 0; i < libraries.length; i++) {
			MeemkitLibrary library = libraries[i];
			URL libraryURL;
			try {
				if (library.getName().endsWith(".jar") || library.getName().endsWith(".zip")) {
					libraryURL = new URL(baseURL + library.getName());

					MeemkitLibraryExport[] exports = library.getExports();

					String[] exportStrings = new String[exports.length];
					for (int j = 0; j < exports.length; j++) {
						exportStrings[j] = exports[j].getValue();
					}

					meemkitClassLoader.addExportedLibrary(libraryURL, exportStrings);
				}
			}
			catch (MalformedURLException e) {
				e.printStackTrace();
			}

		}

	}

	private void shutdownClassLoader() {
		if (meemkitClassLoader != null) {

			referencedMeems = new HashSet<MeemPath>(meemkitClassLoader.getReferencedMeemPathsSet());

			SystemExportList.getInstance().removeClassLoader(meemkitClassLoader);

			LogTools.info(logger, "Meemkit ClassLoader Shutdown [" + meemkitClassLoader.getMeemkitName() + "]");

			meemkitClassLoader = null;
		}
	}

	private void stopReferencedMeems() {
		for (MeemPath meemPath : referencedMeems) {
			meemClientConduit.provideReference(Meem.spi.get(meemPath), "lifeCycle", LifeCycle.class, new LifeCycleCallback());
		}
	}

	public static class LifeCycleCallback implements MeemClientCallback {
		/**
		 * @see org.openmaji.system.meem.wedge.reference.MeemClientCallback#referenceProvided(org.openmaji.meem.wedge.reference.Reference)
		 */
		public void referenceProvided(Reference reference) {
			if (reference == null) {
				// This happens when a Meem has already been told to go dormant.
				// Should be safe to ignore
				return;
			}
			LifeCycle lifeCycle = (LifeCycle) reference.getTarget();
			lifeCycle.changeLifeCycleState(LifeCycleState.DORMANT);
		}
	}

	/* --------- MeemDefinitionProvider method(s) ----------------------------- */

	public MeemDefinition getMeemDefinition() {
		MeemDefinition meemDefinition = MeemDefinitionFactory.spi.create().createMeemDefinition(new Class[] { this.getClass() });
		meemDefinition.getMeemAttribute().setIdentifier(Meemkit.spi.getIdentifier());
		return meemDefinition;
	}

}
