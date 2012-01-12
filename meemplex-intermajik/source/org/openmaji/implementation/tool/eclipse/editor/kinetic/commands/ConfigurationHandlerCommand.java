/*
 * @(#)ConfigurationHandlerCommand.java
 * Created on 11/06/2004
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic.commands;


import java.io.Serializable;

import org.eclipse.gef.commands.Command;
import org.openmaji.implementation.tool.eclipse.client.ConfigurationHandlerProxy;
import org.openmaji.meem.wedge.configuration.ConfigurationIdentifier;


/**
 * <code>ConfigurationHandlerCommand</code>.<p>
 * @author Kin Wong
 */
public class ConfigurationHandlerCommand extends Command {
	private ConfigurationHandlerProxy config;
	private ConfigurationIdentifier identifier;
	private Serializable value;
	
	/**
	 * Constructs an instance of <code>ConfigurationHandlerCommand</code>.<p>
	 * @param config
	 * @param value
	 */
	public ConfigurationHandlerCommand(
		ConfigurationHandlerProxy config, ConfigurationIdentifier identifier, Serializable value) {
		this.config = config;
		this.identifier = identifier;
		this.value = value;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	public void execute() {
		new Thread(new Runnable() {
			public void run() {
				while (config.isReadOnly()) {
					try {
						Thread.sleep(250);
					} catch (InterruptedException e) {
						// -mg- Auto-generated catch block
						e.printStackTrace();
					}			
				}
				config.valueChanged(identifier, value);
			}
		}).start();		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	public void undo() {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#redo()
	 */
	public void redo() {
		execute();
	}
}
