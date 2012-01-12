/*
 * @(#)SecurityView.java
 * Created on 2/02/2004
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.ui.view;

import java.net.URL;
import java.util.logging.Logger;

import javax.security.auth.Subject;


import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.internal.WorkbenchWindow;
import org.eclipse.ui.part.ViewPart;
import org.openmaji.implementation.server.meemkit.Meemkit;
import org.openmaji.implementation.server.security.auth.MeemCoreRootAuthority;
import org.openmaji.implementation.tool.eclipse.client.SWTClientSynchronizer;
import org.openmaji.implementation.tool.eclipse.client.security.SecurityListener;
import org.openmaji.implementation.tool.eclipse.client.security.SecurityManager;
import org.openmaji.implementation.tool.eclipse.images.Images;
import org.openmaji.implementation.tool.eclipse.plugin.MajiPlugin;
import org.openmaji.implementation.tool.eclipse.ui.dialog.LoginDialog;
import org.openmaji.meem.Facet;
import org.openmaji.meem.Meem;
import org.openmaji.meem.MeemPath;
import org.openmaji.meem.space.Space;
import org.openmaji.meem.wedge.reference.Reference;
import org.openmaji.system.gateway.ServerGateway;
import org.openmaji.system.manager.user.AuthenticatorStatus;
import org.openmaji.system.meemserver.MeemServer;
import org.swzoo.log2.core.LogTools;


/**
 * <code>SecurityView</code>.
 * <p>
 * @author Kin Wong
 */
abstract public class SecurityView extends ViewPart {
	
	private static final Logger logger = Logger.getAnonymousLogger();
	private static final boolean DEBUG = true;
	
	private static boolean actionAdded = false;

	private Action securityAction;
	private Composite securityPane;
	private Button loginButton;
	
	//
	private volatile boolean authenticatorLocated = false;
	private volatile boolean classLoaderLoaded = true;	// Warren 26/10/2011 : default to true because OSGI classloader is now used.

	//
	private Meem meemkitLifeCycleManagerMeem;
	private Reference meemkitClientReference;
	private ServerGateway serverGateway;
	private Facet meemkitClientProxy;
	private Meemkit meemkitClient;

	private SelectionListener loginButtonListener = 
		new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				loginInOut();
			}
			public void widgetDefaultSelected(SelectionEvent e) {}
		};
	
	private SelectionListener waitLoginButtonListener = 
		new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				pleaseWait();
			}
			public void widgetDefaultSelected(SelectionEvent e) {}
		};
  
	private SecurityListener securityListener = 
		new SecurityListener() {
			public void onLogin(SecurityManager manager) {
				handleLogin();
			}
	
			public void onLogout(SecurityManager manager) {
				handleLogout();
			}
		};

	/**
	 * Constructor.
	 */
	public SecurityView() {

		if (MajiPlugin.getDefault().startMajiJob.getState() == Job.NONE) {
			setupMeemkitLifeCycleManagerReference();
		}
		else {
			IJobChangeListener listener = new StartMajiJobListener();
			MajiPlugin.getDefault().startMajiJob.addJobChangeListener(listener);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createPartControl(Composite parent) {
		this.securityPane = createSecurityPane(parent);

		createActions();
		createMenu();
		createActionBars();
		
		SecurityManager.getInstance().addSecurityListener(securityListener);
		
		// check initial state
		if(isLoggedIn()) {
			handleLogin();
		}
		else {
			handleLogout();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPart#dispose()
	 */
	public void dispose() {
		removeActions();
		SecurityManager.getInstance().removeSecurityListener(securityListener);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPart#setFocus()
	 */
	public void setFocus() {
	}
	
	protected boolean isLoggedIn() {
		return SecurityManager.getInstance().isLoggedIn();
	}
	
	/**
	 * Handles login to Maji.
	 * <p>
	 */
	protected void handleLogin() {
		clearLogoutView(securityPane);
		clear();
		createLoginView(securityPane);
		securityPane.layout();
		updateSecurityAction();
	}
	
	/**
	 * Handles logout from Maji.
	 * <p>
	 */
	protected void handleLogout() {
		clearLoginView(securityPane);
		clear();
		createLogoutView(securityPane);
		securityPane.layout();
		updateSecurityAction();
	}

	private void createMenu() {
		IMenuManager menu = getViewSite().getActionBars().getMenuManager();
		fillMenu(menu);
		
		if (!actionAdded) {
			menu = ((WorkbenchWindow)MajiPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow()).getMenuBarManager();
			menu = menu.findMenuUsingPath("InterMajik");
			menu.appendToGroup(MajiPlugin.GROUP_SECURITY, securityAction);
			actionAdded = true;
		}
	}
	
	private void createActionBars() {
		IToolBarManager toolBar = getViewSite().getActionBars().getToolBarManager();
		fillActionBars(toolBar);
	}
	
	protected Composite createSecurityPane(Composite parent) {
		return parent;
	}
	
	/**
	 * Fills the menu.
	 * @param menu
	 */
	protected void fillMenu(IMenuManager menu) {
		menu.add(securityAction);
	}
	
	protected void fillActionBars(IToolBarManager toolBar) {
		//toolBar.add(securityAction);
	}
	
	/**
	 * Creates all view actions.
	 * <p>
	 */
	protected void createActions() {
		securityAction = new Action() {
			public void run() {
				loginInOut();
			}
		};
		securityAction.setImageDescriptor(Images.ICON_LOGIN);
	}
	
	/**
	 * Removes all view actions.
	 * <p>
	 */
	protected void removeActions() {
		securityAction = null;
	}
	
	private void updateSecurityAction() {
		
		if(securityAction != null) {
			if ( !majiReady() ) {
				securityAction.setText("Please wait ...");
				securityAction.setDescription("InterMajik is starting");
				securityAction.setToolTipText("InterMajik is starting");
				securityAction.setImageDescriptor(Images.ICON_LOGOUT);
			}
  			else if(!isLoggedIn()) {
				securityAction.setText("Login...");
				securityAction.setDescription("Login to InterMajik");
				securityAction.setToolTipText("Login to InterMajik");
				securityAction.setImageDescriptor(Images.ICON_LOGIN);
			}
			else {
				securityAction.setText("Logout");
				securityAction.setDescription("Logout from InterMajik");
				securityAction.setToolTipText("Logout from InterMajik");
				securityAction.setImageDescriptor(Images.ICON_LOGOUT);
			}
		}
	}

	protected void clear() {
		if(securityPane == null) return;
		Control[] controls = securityPane.getChildren();
		for(int i = 0; i < controls.length; i++) {
			controls[i].dispose();
		}
	}

	abstract protected void createLoginView(Composite parent);

	protected void clearLoginView(Composite parent) {
	}
	
	/**
	 * 
	 * @param parent
	 */
	protected void createLogoutView(Composite parent) {

		Control[] children = parent.getChildren();
		for ( int i = 0; i < children.length; i++ )
		{
		  children[i].dispose();
		}
		
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		parent.setLayout(layout);
		Label loginLabel = new Label(parent,SWT.RIGHT);
		loginButton = new Button(parent, SWT.PUSH);
		loginButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
		
		if (majiReady()) {
		    loginLabel.setImage(Images.ICON_LOGIN.createImage());
		    loginButton.addSelectionListener(loginButtonListener);
		    loginButton.setText("Login InterMajik");
		    loginButton.setToolTipText("Click here to login to InterMajik");    	
		}
		else {
		    loginLabel.setImage(Images.ICON_LOGOUT.createImage());
		    loginButton.addSelectionListener(waitLoginButtonListener);
		    loginButton.setText("Please wait");
		    loginButton.setToolTipText("Please wait for InterMajik to start");
		}
    
    	parent.layout();
	}

	private void pleaseWait() {
		// Do nothing
	}
	
	protected void clearLogoutView(Composite parent) {
		if(loginButton == null) return;
		loginButton.removeSelectionListener(loginButtonListener);
		loginButton = null;
	}

	protected void loginInOut() {
		if(isLoggedIn()) {
			// Logging Out		
			MessageBox msgBox = new MessageBox(getSite().getShell(), SWT.YES | SWT.NO);
			msgBox.setText("InterMajik Logout");
			msgBox.setMessage("Do you want to logout from InterMajik?");
			if(SWT.YES != msgBox.open()) return;
			SecurityManager.getInstance().logout();
		}
		else {
			// Logging In
			LoginDialog.promptLogin();
		}
	}
  
	/**
	 * 
	 */
	private void setupMeemkitLifeCycleManagerReference() {
  	
		// create a ServerGateway for the MeemCore Root Subject
		Subject subject = MeemCoreRootAuthority.getSubject();
		serverGateway = ServerGateway.spi.create(subject);

		// setup a reference so we can be notified when the classloader is loaded

		MeemPath lcmPath = MeemPath.spi.create(Space.HYPERSPACE, MeemServer.spi.getEssentialMeemsCategoryLocation() + "/meemkitLifeCycleManager");
		meemkitLifeCycleManagerMeem = serverGateway.getMeem(lcmPath);
		
		meemkitClient = new MeemkitClient();
		meemkitClientProxy = serverGateway.getTargetFor(meemkitClient,Meemkit.class);
		meemkitClientReference = Reference.spi.create("meemkitOutput", meemkitClientProxy, true, null);
		
		meemkitLifeCycleManagerMeem.addOutboundReference(meemkitClientReference, false);

		// setup a reference so we can be notified when the authenticator is located
		
		MeemPath authLookupPath = MeemPath.spi.create(Space.HYPERSPACE, MeemServer.spi.getEssentialMeemsCategoryLocation() + "/authenticatorLookup");
		Meem authenticatorLookupMeem = serverGateway.getMeem(authLookupPath);
		
		AuthenticatorStatus authLookupClient = new AuthStatusClient();
		Facet authLookupClientProxy = serverGateway.getTargetFor(authLookupClient, AuthenticatorStatus.class);
		Reference authLookupClientReference = Reference.spi.create("authenticatorStatus", authLookupClientProxy, true, null);
		
		authenticatorLookupMeem.addOutboundReference(authLookupClientReference, false);

	}
  
	/**
	 *
	 */
	private void removeMeemkitLifeCycleManagerReference() {
		meemkitLifeCycleManagerMeem.removeOutboundReference(meemkitClientReference);
		serverGateway.revokeTarget(meemkitClientProxy, meemkitClient);
		
		meemkitLifeCycleManagerMeem = null;
		meemkitClientReference      = null;
		meemkitClientProxy          = null;
		meemkitClient               = null;
	}

	private boolean majiReady() {
		return (authenticatorLocated && classLoaderLoaded);
	}
	
	/**
	 * 
	 */
	private class MeemkitClient implements Meemkit {
		
		public void detailsChanged(String[] names, URL[] descriptorLocations) {
		
		  // We need to wait until the core meemkit classloader has started
		  // before we allow the user to login. Reason being that the authentication
		  // mechanism is part of the core meemkit.
		
		  for ( int i = 0; i < names.length; i++ ) {
		    if ( names[i].equals("openmaji-core") ) {
				if (DEBUG) {
					logger.info("classloader loaded");
				}

		    	classLoaderLoaded = true;
		    	changeLoginButton();
		
		    	// no longer need to listen for the Meemkit
		    	removeMeemkitLifeCycleManagerReference();
		    }
		  }
		}
		
		private void changeLoginButton() {
			if (isLoggedIn()) {
				return;
			}
			if (securityPane == null) {
				return;
			}
			SWTClientSynchronizer.get(securityPane.getDisplay()).execute(new Runnable() {
				public void run() {
          updateSecurityAction();
					createLogoutView(securityPane);
				}
			});
		}
	}

	/**
	 *  
	 */
	private class AuthStatusClient implements AuthenticatorStatus {
  	
		public void authenticatorLocated() {
			if (DEBUG) {
				logger.info("authenticator located");
			}
			authenticatorLocated = true;
			changeLoginButton();
		}
		
		public void authenticatorLost() {
			if (DEBUG) {
				logger.info("authenticator lost");
			}
			authenticatorLocated = false;
			changeLoginButton();
		}
		
		private void changeLoginButton() {
			// change the login button if not logged in
			if (isLoggedIn()) {
				return;
			}
			if ( securityPane == null ) {
				return;
			}
			if ( securityPane.isDisposed() ) {
				return;
			}
			SWTClientSynchronizer.get(securityPane.getDisplay()).execute(
					new Runnable() {
						public void run() {
							createLogoutView(securityPane);
						}
					}
				);
		}
	}
  
	private class StartMajiJobListener implements IJobChangeListener {

		public void aboutToRun(IJobChangeEvent event) {}
		public void awake(IJobChangeEvent event) {}
		
		public void done(IJobChangeEvent event) {
			setupMeemkitLifeCycleManagerReference();
		}
		
		public void running(IJobChangeEvent event) {}
		public void scheduled(IJobChangeEvent event) {}
		public void sleeping(IJobChangeEvent event) {}
	}

}
