/*
 * @(#)SecurityManager.java
 * Created on 7/01/2004
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.client.security;

import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import javax.security.auth.x500.X500Principal;

import org.openmaji.implementation.security.auth.LoginHelper;
import org.openmaji.implementation.tool.eclipse.plugin.MajiPlugin;
import org.openmaji.system.gateway.ServerGateway;



/**
 * <code>SecurityManager</code>.
 * <p>
 * @author Kin Wong
 * @author Warren Bloomer
 */
public class SecurityManager {
	
	static private SecurityManager securityManager = new SecurityManager();

	static public SecurityManager getInstance() {
		return securityManager;
	}
	
	private User 				user = null;
	private LoginContext        loginContext = null;
	private List<SecurityListener>	listeners = new ArrayList<SecurityListener>();
	private ServerGateway		gateway;
	
	private SecurityManager() {

	}
	
	public void addSecurityListener(SecurityListener listener) {
		synchronized(listeners) {
			listeners.add(listener);
//			if (isLoggedIn()) {
//				listener.onLogin(this);
//			}
//			else {
//				listener.onLogout(this);
//			}
		}
	}

	public void removeSecurityListener(SecurityListener listener) {
		synchronized(listeners) {
			listeners.remove(listener);
		}
	}
	
	public boolean isLoggedIn() {
		return user != null;
	}
	
	public boolean isLoggedOut() {
		return user == null;
	}
	
	public User getUser() {
		return user;
	}
	
	boolean guest;
	public boolean isGuest() {
		return guest;
	}
	
	public synchronized Subject getSubject() {
		if (loginContext == null) { 
			return null;
		}
		else {
			return loginContext.getSubject();
		}
	}
	
	public synchronized ServerGateway getGateway()
	{
	    return gateway;
	}

	/**
	 * 
	 * @param userName
	 * @param password
	 * @throws LoginException
	 */
	public void login(String userName, String password) throws LoginException {
		try {
			MajiPlugin.getDefault().startMajiJob.join();
		}
		catch (InterruptedException e) {
		}
		
		this.loginContext = LoginHelper.login(userName, password);
		Subject subject = loginContext.getSubject();
		
		gateway = ServerGateway.spi.create(subject);
		
		user = (User)Subject.doAs(subject, new MajiAccessAction());

		Subject.doAs(
				subject, 
				new PrivilegedAction<Object>() {
					public Object run()	{
						fireLogin();
						
//						Display display = PlatformUI.getWorkbench().getDisplay();
//						boolean running = true;
//						while (running) {
//							if (!display.readAndDispatch())
//								display.sleep();
//						}
						
						return null;
					}
				}
		);
		
		// TODO[Kin] Security should be enforced by the backend
		guest = (user.getName().equalsIgnoreCase("guest"));
	}
		
	public void logout() {
		if(isLoggedOut()) return;
		user = null;
		fireLogout();
		synchronized (this) {
			if (loginContext != null) {
				// TODO Currently Maji depends on the Subject that activates Meems to remain logged in.
//				try {
//					loginContext.logout();
//				}
//				catch (LoginException ex) {
//				}
				loginContext = null;
			}
		}
	}
	
	private void fireLogin() {
		if(listeners == null) return;
		SecurityListener[] listeners = getListenerArray();
		for(int i = 0; i < listeners.length; i++) {
			listeners[i].onLogin(this);
		}
	}
	
	private void fireLogout() {
		if(listeners == null) return;
		SecurityListener[] listeners = getListenerArray();
		for(int i = 0; i < listeners.length; i++) {
			listeners[i].onLogout(this);
		}
	}
	
	private SecurityListener[] getListenerArray() {
		if(listeners == null) return new SecurityListener[0];
		return (SecurityListener[])listeners.toArray(new SecurityListener[0]);
	}
}

class MajiAccessAction implements PrivilegedAction<User> {
	/* (non-Javadoc)
	 * @see java.security.PrivilegedAction#run()
	 */
	public User run() {
		AccessControlContext context = AccessController.getContext();
		Subject currentSubject = Subject.getSubject(context);
		Set<X500Principal>	principals = currentSubject.getPrincipals(X500Principal.class);
		
		X500Principal	id = (X500Principal)principals.toArray()[0];
		return new User(id);
	}
}
