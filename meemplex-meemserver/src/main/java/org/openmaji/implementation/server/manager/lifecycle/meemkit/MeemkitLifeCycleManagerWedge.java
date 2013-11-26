/*
 * Copyright 2004 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.server.manager.lifecycle.meemkit;

import java.net.URL;
import java.util.*;

import org.openmaji.implementation.server.manager.lifecycle.LifeCycleManagerWedge;
import org.openmaji.implementation.server.manager.lifecycle.adapter.lifecycle.LifeCycleAdapterWedge;
import org.openmaji.implementation.server.manager.lifecycle.category.LifeCycleManagerCategoryWedge;
import org.openmaji.implementation.server.manager.lifecycle.transitory.TransientLifeCycleManagerWedge;
import org.openmaji.implementation.server.meemkit.Meemkit;
import org.openmaji.implementation.server.meemkit.MeemkitWedge;

import org.openmaji.meem.Facet;
import org.openmaji.meem.Meem;
import org.openmaji.meem.MeemPath;
import org.openmaji.meem.Wedge;
import org.openmaji.meem.definition.*;
import org.openmaji.meem.filter.Filter;
import org.openmaji.meem.wedge.dependency.DependencyHandler;
import org.openmaji.meem.wedge.error.ErrorHandler;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClient;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClientAdapter;
import org.openmaji.meem.wedge.lifecycle.LifeCycleState;
import org.openmaji.meem.wedge.reference.Reference;
import org.openmaji.system.manager.lifecycle.LifeCycleManager;
import org.openmaji.system.manager.lifecycle.LifeCycleManagerClient;
import org.openmaji.system.meem.core.MeemCore;
import org.openmaji.system.meem.wedge.reference.ContentProvider;
import org.openmaji.system.meem.wedge.reference.MeemClientCallback;
import org.openmaji.system.meem.wedge.reference.MeemClientConduit;
import org.openmaji.system.meemserver.MeemServer;
import org.openmaji.system.request.RequestContext;
import org.openmaji.system.request.RequestCreationException;
import org.openmaji.system.utility.MeemUtility;


import java.util.logging.Level;
import java.util.logging.Logger;

public class MeemkitLifeCycleManagerWedge implements MeemkitLifeCycleManager, Wedge, MeemDefinitionProvider {
	private static final Logger logger = Logger.getAnonymousLogger();

	private static final boolean DEBUG = false;

	public MeemCore meemCore;

	public RequestContext requestContext;

	/* ------------------------- outbound facets ----------------------- */

	public Meemkit meemkitOutput;

	public final ContentProvider<Meemkit> meemkitOutputProvider = new MeemkitOutputProvider();

	public MeemkitLifeCycleManagerClient meemkitLifeCycleManagerClient;

	public final ContentProvider<MeemkitLifeCycleManagerClient> meemkitLifeCycleManagerClientProvider = new MeemkitLifeCycleManagerClientProvider();

	/* ---------------------------- conduits ------------------------ */

	public LifeCycleClient lifeCycleClientConduit = new LifeCycleClientAdapter(this);

	public LifeCycleManager lifeCycleManagerConduit;

	public LifeCycleManagerClient lifeCycleManagerClientConduit = new MyLifeCycleManagerClient();

	public ErrorHandler errorHandlerConduit;

	public MeemClientConduit meemClientConduit;

	public DependencyHandler dependencyHandlerConduit;

	/* ---------------------- private members ----------------------- */

	private final Map<String, Meem> meems = new HashMap<String, Meem>();

	private final Map<MeemPath, String> destroyingMeems = new HashMap<MeemPath, String>();

	private final MeemDefinitionFactory meemDefinitionFactory = MeemDefinitionFactory.spi.create();

	private final Map<String, URL> meemkitDescriptorLocations = new HashMap<String, URL>();

	// private boolean startupDone = false;

	private Map<Meem, DependencyAttribute> dependencyAttributes = new HashMap<Meem, DependencyAttribute>();

	private int totalMeemkits;

	private int startedMeemkits = 0;

	/* ------------------ Meem functionality ---------------------------------- */

	public void commence() {
		String meemServerName = MeemServer.spi.getName();
		meemCore.getMeemStructure().getMeemAttribute().setIdentifier(meemServerName);
		logger.log(Level.INFO, "commence() - server=[" + meemServerName + "]");
	}

	public void conclude() {
		// The only time this method gets called will be when a MeemServer is being
		// shutdown. And since this is a transient Meem we don't bother doing any
		// cleanup of the Meems we have created.
	}

	/* ------------------ inbound MeemkitLCM facet methods -------------------- */

	public void detailsChanged(String[] names, URL[] descriptorLocations) {
		if (DEBUG) {
			logger.log(Level.INFO, "detailsChanged()");
		}
		totalMeemkits += names.length;

		for (int i = 0; i < names.length; i++) {
			String name = names[i];
			if (DEBUG) {
				logger.log(Level.INFO, "detailsChanged() name=" + name);
			}
			URL descriptorLocation = null;
			if (descriptorLocations != null) {
				descriptorLocation = descriptorLocations[i];
			}

			Meem meem = meems.get(name);
			if (descriptorLocation == null) {
				if (meem != null) { 
					// The meemkit has been removed so we need to kill the corresponding Meemkit meem
					destroyingMeems.put(meem.getMeemPath(), name);
					lifeCycleManagerConduit.destroyMeem(meem);
				}
				continue;
			}

			if (meem == null) {
				// This meemkit is not already installed so we need to create a new Meemkit meem
				MeemDefinition meemDefinition = meemDefinitionFactory.createMeemDefinition(MeemkitWedge.class);
				meemDefinition.getMeemAttribute().setIdentifier(name);
				try {
					requestContext.begin(Integer.MAX_VALUE, descriptorLocation);
				}
				catch (RequestCreationException ex) {
					errorHandlerConduit.thrown(ex);
					continue;
				}
				lifeCycleManagerConduit.createMeem(meemDefinition, LifeCycleState.READY);
				continue;
			}

			// The meemkit's descriptor has changed so we need to tell the Meemkit meem to reload it
			Meemkit meemkit = (Meemkit) MeemUtility.spi.get().getTarget(meem, Meemkit.spi.getIdentifier(), Meemkit.class);
			meemkit.detailsChanged(new String[] { name }, new URL[] { descriptorLocation });
		}
	}

	/* --------- MeemDefinitionProvider method(s) ----------------------------- */

	public MeemDefinition getMeemDefinition() {
		MeemDefinition meemDefinition = meemDefinitionFactory.createMeemDefinition(new Class[] { TransientLifeCycleManagerWedge.class, LifeCycleManagerCategoryWedge.class, LifeCycleManagerWedge.class, LifeCycleAdapterWedge.class, this.getClass(), });
		return meemDefinition;
	}

	/* ------------------------------------------------------------------------ */

	private class MyLifeCycleManagerClient implements LifeCycleManagerClient {
		
		public void meemCreated(Meem meem, String identifier) {
			Object context = requestContext.get();
			if (context == null) {
				return;
			}

			meems.put(identifier, meem);

			Facet proxy = meemCore.getTargetFor(new MeemkitClient(), Meemkit.class);

			DependencyAttribute dependencyAttribute = new DependencyAttribute(DependencyType.WEAK, Scope.LOCAL, meem, "meemkitClient");
			dependencyAttributes.put(meem, dependencyAttribute);

			dependencyHandlerConduit.addDependency(proxy, dependencyAttribute, LifeTime.TRANSIENT);

			URL descriptorURL = (URL) context;
			MeemClientCallback<Meemkit> callback = new MeemkitCallback(identifier, descriptorURL);
			meemClientConduit.provideReference(meem, "meemkit", Meemkit.class, callback);

			requestContext.end();
		}

		public void meemDestroyed(Meem meem) {
			String name = destroyingMeems.remove(meem.getMeemPath());

			meems.remove(name);
			meemkitOutput.detailsChanged(new String[] { name }, null);

			DependencyAttribute dependencyAttribute = (DependencyAttribute) dependencyAttributes.remove(meem);

			// remove dependency
			dependencyHandlerConduit.removeDependency(dependencyAttribute);
		}

		public void meemTransferred(Meem meem, LifeCycleManager targetLifeCycleManager) {
			errorHandlerConduit.thrown(new Exception("Unexpected invocation"));
		}
	}

	/* ------------------------------------------------------------------------ */

	private class MeemkitCallback implements MeemClientCallback<Meemkit> {
		private final String identifier;

		private final URL descriptorURL;

		public MeemkitCallback(String identifier, URL descriptorURL) {
			this.identifier = identifier;
			this.descriptorURL = descriptorURL;
		}

		public void referenceProvided(Reference<Meemkit> reference) {
			Meemkit meemkit = reference.getTarget();
			meemkit.detailsChanged(new String[] { identifier }, new URL[] { descriptorURL });
		}
	}

	/* ------------------------------------------------------------------------ */

	private class MeemkitClient implements Meemkit {
		public void detailsChanged(String[] names, URL[] descriptorLocations) {
			startedMeemkits++;
			if (startedMeemkits == totalMeemkits) {
				// startupDone = true;
				meemkitLifeCycleManagerClient.classLoadingCompleted();
			}

			meemkitOutput.detailsChanged(names, descriptorLocations);

			for (int i = 0; i < names.length; i++) {
				meemkitDescriptorLocations.put(names[i], descriptorLocations[i]);
			}
		}
	}

	/* ------------------------------------------------------------------------ */

	private class MeemkitLifeCycleManagerClientProvider implements ContentProvider<MeemkitLifeCycleManagerClient> {
		public void sendContent(MeemkitLifeCycleManagerClient target, Filter filter) {
			if (startedMeemkits == totalMeemkits) {
				target.classLoadingCompleted();
			}
		}
	}

	/* ------------------------------------------------------------------------ */

	private class MeemkitOutputProvider implements ContentProvider<Meemkit> {
		public void sendContent(Meemkit client, Filter filter) {
			if (meemkitDescriptorLocations.size() == 0) {
				return;
			}

			String[] names = new String[meemkitDescriptorLocations.size()];
			URL[] descriptorLocations = new URL[meemkitDescriptorLocations.size()];
			int i = 0;
			for (String name : meemkitDescriptorLocations.keySet()) {
				names[i] = name;
				descriptorLocations[i++] = meemkitDescriptorLocations.get(name);
			}

			client.detailsChanged(names, descriptorLocations);
		}
	}
}
