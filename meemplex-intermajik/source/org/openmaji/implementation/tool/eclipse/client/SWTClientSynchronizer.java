/*
 * @(#)SWTClientSynchronizer.java
 * Created on 27/11/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.client;

import java.security.PrivilegedAction;

import javax.security.auth.Subject;

import org.eclipse.swt.widgets.Display;
import org.openmaji.implementation.tool.eclipse.client.security.SecurityManager;

/**
 * <code>SWTClientSynchronizer</code> provides UI synchronization to SWT 
 * (Standard Widget Toolkit) used in <code>MeemClientProxy</code>.
 * <p>
 * @author Kin Wong
 */
public class SWTClientSynchronizer implements ClientSynchronizer {

	private static SWTClientSynchronizer synchronizer = new SWTClientSynchronizer();
	
	public static ClientSynchronizer getDefault() {
		return synchronizer;
	}
	
	public static ClientSynchronizer get(Display display) {
		return new SWTClientSynchronizer(display);
	}
	
	private final Display display;
	
	public SWTClientSynchronizer() {
		this(Display.getDefault());
    }
	
	public SWTClientSynchronizer(Display display) {
		this.display = display;
    }
	
	/**
	 * Implemented to execute the <code>Runnable</code> on the SWT UI thread 
	 * asynchronously.<p> 
	 * @see org.openmaji.implementation.tool.eclipse.client.ClientSynchronizer#execute(java.lang.Runnable)
	 */
	public void execute(final Runnable runnable) {
		final Subject subject = SecurityManager.getInstance().getSubject();
		
		Runnable theRunnable;
		if (subject == null) {
			theRunnable = runnable;
		}
		else {
			theRunnable = new Runnable() {
				public void run() {
			        Subject.doAs(subject, new PrivilegedAction<Void>() {
			        	public Void run() {
			        		runnable.run();
			        		return null;
			        	}
			        });
				}
			};
		}
		display.asyncExec(theRunnable);
	}
}
