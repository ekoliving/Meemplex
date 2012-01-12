/*
 * @(#)MeemCloneCommand.java
 * Created on 29/04/2003
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic.commands;


import org.eclipse.draw2d.geometry.Rectangle;
import org.openmaji.common.VariableMap;
import org.openmaji.implementation.intermajik.model.ViewModeConstants;
import org.openmaji.implementation.tool.eclipse.client.CategoryProxy;
import org.openmaji.implementation.tool.eclipse.client.ConfigurationHandlerProxy;
import org.openmaji.implementation.tool.eclipse.client.InterMajikClientProxyFactory;
import org.openmaji.implementation.tool.eclipse.client.MeemClientProxy;
import org.openmaji.implementation.tool.eclipse.editor.common.model.ElementContainer;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.Meem;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.MeemModelFactory;
import org.openmaji.meem.MeemPath;
import org.openmaji.meem.wedge.lifecycle.LifeCycleState;


/**
 * <code>MeemCloneCommand</code>.
 * <p>
 * @author Kin Wong
 */
public class MeemCloneCommand extends MeemDropCommand {
	/**
	 * Constructs an instance of <code>MeemCloneCommand</code>.
	 * <p>
	 * @param container
	 * @param categoryProxy
	 * @param variableMap
	 * @param meemPath
	 * @param location
	 */
	public MeemCloneCommand(ElementContainer container, CategoryProxy categoryProxy, VariableMap variableMap, MeemPath meemPath, Rectangle location) {
		super(container, categoryProxy, variableMap, meemPath, location);
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.kinetic.commands.MeemDropCommand#execute()
	 */
	public void execute() {
		super.execute();
		setMeemIdentifier();
	}

	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.kinetic.commands.MeemDropCommand#undo()
	 */
	public void undo() {
		super.undo();
		destroyMeem();
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.kinetic.commands.MeemDropCommand#redo()
	 */
	public void redo() {
		createMeem();
		super.redo();
	}

	protected void destroyMeem() {
		// destroy the cloned meem 
		getMeem().getProxy().getLifeCycle().
			changeLifeCycleState(LifeCycleState.ABSENT);
		setMeem(null);
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.kinetic.commands.MeemDropCommand#createMeem()
	 */
	protected void createMeem() {
		// Clone the meem located by the meem path
		MeemClientProxy proxy = InterMajikClientProxyFactory.getInstance().clone(getMeemPath(), LifeCycleState.LOADED, null);
		Meem meem = MeemModelFactory.create(proxy);
		meem.setCollapse(false);
		meem.setViewMode(ViewModeConstants.VIEW_MODE_DETAILED);
		setMeem(meem);
	}
	
	protected void setMeemIdentifier() {
		MeemClientProxy proxy = getMeem().getProxy();
		proxy.getLifeCycleLimit().limitLifeCycleState(LifeCycleState.READY);
		
		proxy.getConfigurationHandler().
			valueChanged(ConfigurationHandlerProxy.ID_MEEM_IDENTIFIER, getCategoryEntryName());
	}
}
