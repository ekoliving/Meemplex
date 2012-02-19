/*
 * @(#)MajiPlugin.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.tool.eclipse.plugin;

import java.io.File;
import java.util.Hashtable;
import java.util.logging.Logger;

import javax.security.auth.Subject;

import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.internal.WorkbenchWindow;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.meemplex.server.MeemEngineLauncher;
import org.openmaji.implementation.server.security.auth.MeemCoreRootAuthority;
import org.openmaji.implementation.tool.eclipse.client.InterMajikClientProxyFactory;
import org.openmaji.implementation.tool.eclipse.client.MeemClientProxy;
import org.openmaji.implementation.tool.eclipse.client.security.SecurityListener;
import org.openmaji.implementation.tool.eclipse.client.security.SecurityManager;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.KINeticEditor;
import org.openmaji.implementation.tool.eclipse.maji.MajiSystemPreferencePage;
import org.openmaji.meem.Facet;
import org.openmaji.meem.Meem;
import org.openmaji.meem.MeemPath;
import org.openmaji.meem.space.Space;
import org.openmaji.meem.wedge.reference.Reference;
import org.openmaji.system.gateway.ServerGateway;
import org.openmaji.system.meemkit.core.MeemkitDescriptor;
import org.openmaji.system.meemkit.core.MeemkitManagerClient;
import org.openmaji.system.meemkit.core.MeemkitWizardDescriptor;
import org.openmaji.system.meemserver.MeemServer;
import org.osgi.framework.BundleContext;

/**
 * @author mg Created on 8/01/2003
 */
public class MajiPlugin extends AbstractUIPlugin {
	private static final Logger logger = Logger.getAnonymousLogger();

	static public String GROUP_SECURITY = MajiPlugin.class + ".security";

	static public String GROUP_START = MajiPlugin.class + ".start";

	static public String GROUP_END = MajiPlugin.class + ".end";

	static public String GROUP_MEEMKIT_WIZARDS = MajiPlugin.class + ".meemkit.wizards";

	public Job startMajiJob = new StartMajiJob();;

	public Job startMeemkitManagerJob;

	public static final String KINETIC_EDITOR = "Meem Configuration Editor";

	private static MajiPlugin inst;

	private static final String MAJI_PLUGIN_HAS_RUN = "MAJI_PLUGIN_HAS_RUN";

	private static MeemClientProxy hyperSpaceProxy;

	private static MeemClientProxy worksheetLifeCycleManager;

	private MenuManager menu;

	private SecurityListener listener = new SecurityListener() {
		public void onLogin(SecurityManager manager) {
			// create an LCM to look after the worksheets
			// -mg- this should probably happen as part of the meemspace restore
			// get hyperspace as a MeemClientProxy

			hyperSpaceProxy = InterMajikClientProxyFactory.getInstance().locateHyperSpace();
			hyperSpaceProxy.hashCode(); // stupid eclipse warning
			worksheetLifeCycleManager = InterMajikClientProxyFactory.getInstance().locateWorksheetLifeCycleManager();
		}

		public void onLogout(SecurityManager manager) {
		}
	};

	public MajiPlugin() {
		super();
	}

	/**
	 * Start the bundle
	 */
	public void start(BundleContext context) throws Exception {
//		if (this != getDefault()) {
//			getDefault().start(context);
//			return;
//		}
		inst = this;

		logger.info("starting intermajik: " + context.getBundle().getSymbolicName());
		
		super.start(context);
		
		logger.info("get prefstore");
		getPreferenceStore();
		
		
		// force loading of awt in main thread toolkit to workaround mac OSX hang issue with SWT and AWT
		//java.awt.Toolkit.getDefaultToolkit();

		// Check if this is the first time the plugin has been run
		checkFirstRun();
		SecurityManager.getInstance().addSecurityListener(listener);
		SecurityManager.getInstance().addSecurityListener(new MeemkitManagerListener());
		 
		// create intermajik menu 
		WorkbenchWindow window = (WorkbenchWindow)getWorkbench().getActiveWorkbenchWindow();
		MenuManager menubar = window.getMenuBarManager();
		menubar.insert(0, createInterMajikMenu());
		 
		startMajiJob.schedule();
	}

	/**
	 * Stop the bundle
	 */
	public void stop(BundleContext context) throws Exception {
//		if (this != getDefault()) {
//			getDefault().stop(context);
//			return;
//		}
		
		SecurityManager.getInstance().logout();
		ServerGateway serverGateway = SecurityManager.getInstance().getGateway();
		if (serverGateway == null) {
			// Did not login
			Subject subject = MeemCoreRootAuthority.getSubject();
			serverGateway = ServerGateway.spi.create(subject);
		}
		serverGateway.shutdown();
		super.stop(context);
	}
	
	
	public static MeemClientProxy getWorksheetLifeCycleManager() {
		return worksheetLifeCycleManager;
	}

	private MenuManager createInterMajikDocumentationSubMenu() {
		MenuManager menu = new MenuManager("Documentation", "Documentation");

		menu.add(new ShowHelpAction("Release Notes", "/org.openmajik.intermajik/html/release-notes/index.html"));
		menu.add(new ShowHelpAction("Getting Started", "/org.openmajik.intermajik/html/qs-tutorials/index.html"));
		menu.add(new ShowHelpAction("Beanshell Script Tutorials", "/org.openmajik.intermajik/html/tutorials/index.html"));
		menu.add(new ShowHelpAction("Java Developer Tutorials", "/org.openmajik.intermajik/html/dev-tutorials/index.html"));
		menu.add(new ShowHelpAction("Code Samples", "/org.openmajik.intermajik/html/dev-tutorials/code-samples/index.html"));
		menu.add(new ShowHelpAction("Maji API Javadoc", "/org.openmajik.intermajik/html/javadoc/index.html"));
		menu.add(new ShowHelpAction("Glossary", "/org.openmajik.intermajik/html/reference/Glossary.html"));
		menu.add(new ShowHelpAction("FAQ", "/org.openmajik.intermajik/html/reference/meemFAQ.html"));
		menu.add(new ShowHelpAction("Beanshell Function Reference", "/org.openmajik.intermajik/html/reference/beanshell-function-reference.html"));

		return menu;
	}

	/**
	 * 
	 */
	private MenuManager createInterMajikMenu() {
		menu = new MenuManager("InterMajik", "InterMajik");

		menu.add(new GroupMarker(GROUP_SECURITY));
		menu.add(new GroupMarker(GROUP_START));

		menu.add(new Separator());

		menu.add(new GroupMarker(GROUP_END));

		menu.add(new Separator());

		menu.add(new org.openmaji.implementation.tool.eclipse.ui.wizards.bugreport.OpenBugReportWizardAction());
		menu.add(new org.openmaji.implementation.tool.eclipse.ui.wizards.meemkit.OpenMeemkitWizardAction());
		menu.add(new GroupMarker(GROUP_MEEMKIT_WIZARDS));

		// TODO[ben] Re-enable SDK project item when SDK is ready
		// menu.add(new Separator());
		// menu.add(new org.openmaji.implementation.tool.eclipse.ui.wizards.sdkproject.OpenSDKProjectWizardAction());

		menu.add(new Separator());

		menu.add(new ShowWelcomeAction());
		menu.add(createInterMajikDocumentationSubMenu());
		menu.add(new LaunchURLAction("Website", "http://www.openmaji.org"));

		menu.add(new Separator());

		menu.add(ActionFactory.QUIT.create(getWorkbench().getActiveWorkbenchWindow()));

		return menu;
	}

	/**
	 * Gets the plugin singleton.
	 * 
	 * @return the default MajiPlugin instance
	 */
	public static MajiPlugin getDefault() {
		if (inst == null) {
			inst = new MajiPlugin();
		}
		return inst;
	}

	public static boolean startMaji() {
		logger.info("something called startMaji()");
		
		// try {
		// String launcherName = "org.openmaji.implementation.tool.eclipse.plugin.LaunchEclipsePlugin";
		// ClassLoader cl = new URLClassLoader(new URL[] {}, classLoader);
		// Class launcherClass = cl.loadClass(launcherName);
		// Method launchMethod = launcherClass.getMethod("startMaji", new Class[]{});
		// return ((Boolean) launchMethod.invoke(launcherClass, new Object[] {})).booleanValue();
		// }
		// catch (Exception e) {
		// e.printStackTrace();
		// return false;
		// }

		/*
		 * Commented out by Warren Blomer 26/10/2011 should be started by org.meemplex.meemserver 
		 * return LaunchEclipsePlugin.startMaji();
		 */

		return MeemEngineLauncher.instance().isStarted();
	}

	public void checkFirstRun() {
		if (!getPreferenceStore().contains(MAJI_PLUGIN_HAS_RUN)) {
			// get workbench install dir
			String installDir = "";

			String[] args = Platform.getCommandLineArgs();
			for (int i = 0; i < args.length; i++) {
				if (args[i].equals("-install")) {
					installDir = args[i + 1];
					break;
				}
			}

			// lets get rid of the file bit
			installDir = installDir.substring(installDir.indexOf(":") + 1);

			IPath installPath = new Path(installDir);
			installPath = installPath.removeLastSegments(1);
			// This path is within the installer's installed directory
			installPath = installPath.append("conf/maji-edgeServer-primary.properties");

			File processFile = installPath.toFile();
			if (processFile.exists()) {
				getPreferenceStore().setValue(MajiSystemPreferencePage.MAJI_LAUNCH_PROPERTIES, installPath.toOSString());
			}

			installPath = new Path(installDir);
			installPath = installPath.removeLastSegments(1);
			// This path is within the installer's installed directory
			installPath = installPath.append("conf/security/intermajik-users-keystore-01");

			File usersFile = installPath.toFile();
			if (usersFile.exists()) {
				getPreferenceStore().setValue(MajiSystemPreferencePage.INTERMAJIK_LAUNCH_PROPERTIES, installPath.toOSString());
			}

			getPreferenceStore().setValue(MAJI_PLUGIN_HAS_RUN, true);
		}
	}



	private class StartMajiJob extends Job {
		public StartMajiJob() {
			super("Start Maji Job");
		}

		public IStatus run(IProgressMonitor monitor) {
			/*
			 * comment out by Warren Bloomer 26/10/2011 should be started by org.meemplex.meemserver OSGI bundle
			 startMaji();
			 */
			try {
				Thread.sleep(1000);
			}
			catch (InterruptedException e) {
				
			}
			return Status.OK_STATUS;
		}
	}

	private class MeemkitManagerListener implements SecurityListener, MeemkitManagerClient {

		private Meem meem;

		private final Hashtable descriptors = new Hashtable();

		public void onLogin(SecurityManager manager) {
			if (meem != null) {
				return;
			}
			String path = MeemServer.spi.getEssentialMeemsCategoryLocation() + "/meemkitManager";
			MeemPath meemPath = MeemPath.spi.create(Space.HYPERSPACE, path);
			ServerGateway gateway = SecurityManager.getInstance().getGateway();
			meem = gateway.getMeem(meemPath);
			if (meem != null) {
				// Setup a MeemkitManagerClient proxy
				Facet facet = gateway.getTargetFor(this, MeemkitManagerClient.class);
				Reference reference = Reference.spi.create("meemkitManagerClientOutput", facet, true, null);
				meem.addOutboundReference(reference, false);
			}
		}

		public void onLogout(SecurityManager manager) {
			System.err.println("onLogout() - NOT IMPLEMENTED YET"); // TODO
		}

		public void meemkitInstalled(String meemkitName) {
			MeemkitDescriptor descriptor = (MeemkitDescriptor) descriptors.get(meemkitName);
			MeemkitWizardDescriptor[] wizardDescriptors = descriptor.getWizardDescriptors();
			if (wizardDescriptors != null) {
				for (int j = 0; j < wizardDescriptors.length; j++) {
					MeemkitWizardDescriptor wizardDescriptor = wizardDescriptors[j];
					String id = meemkitName + "." + wizardDescriptor.hashCode();
					ActionStarter actionStarter = new ActionStarter(id, wizardDescriptor);
					menu.insertBefore(GROUP_MEEMKIT_WIZARDS, actionStarter);
				}
			}
		}

		public void meemkitUpgraded(String meemkitName) {
			System.err.println("meemkitUpgraded() - NOT IMPLEMENTED YET"); // TODO
		}

		public void meemkitUninstalled(String meemkitName) {
			System.err.println("meemkitUninstalled() - NOT IMPLEMENTED YET"); // TODO
		}

		public void meemkitDescriptorsAdded(MeemkitDescriptor[] meemkitDescriptors) {
			for (int i = 0; i < meemkitDescriptors.length; i++) {
				String meemkitname = meemkitDescriptors[i].getHeader().getName();
				descriptors.put(meemkitname, meemkitDescriptors[i]);
			}
		}

		public void meemkitDescriptorsRemoved(MeemkitDescriptor[] meemkitDescriptors) {
			System.err.println("meemkitDescriptorsRemoved() - NOT IMPLEMENTED YET"); // TODO
		}
	}

	public MeemClientProxy getActiveWorksheetProxy() {
		WorkbenchWindow window = (WorkbenchWindow) getWorkbench().getActiveWorkbenchWindow();
		IEditorPart part = window.getActivePage().getActiveEditor();
		if (part instanceof KINeticEditor) {
			KINeticEditor editor = (KINeticEditor) part;
			MeemClientProxy proxy = (MeemClientProxy) editor.getAdapter(MeemClientProxy.class);
			return proxy;
		}
		return null;
	}

}
