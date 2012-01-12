/*
 * @(#)MeemDropCommand.java
 * Created on 14/10/2003
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic.commands;


import org.eclipse.core.runtime.Assert;
import org.eclipse.draw2d.geometry.Rectangle;
import org.openmaji.common.VariableMap;
import org.openmaji.implementation.intermajik.model.ValueBag;
import org.openmaji.implementation.intermajik.model.ViewModeConstants;
import org.openmaji.implementation.tool.eclipse.client.CategoryEntryNameFactory;
import org.openmaji.implementation.tool.eclipse.client.CategoryProxy;
import org.openmaji.implementation.tool.eclipse.client.InterMajikClientProxyFactory;
import org.openmaji.implementation.tool.eclipse.client.variables.IVariableSource;
import org.openmaji.implementation.tool.eclipse.editor.common.model.ElementContainer;
import org.openmaji.implementation.tool.eclipse.editor.common.model.commands.CreateCommand;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.Meem;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.MeemModelFactory;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.variables.VariableSourceFactory;
import org.openmaji.meem.MeemPath;


/**
 * <code>MeemDropCommand</code>.
 * <p>
 * @author Kin Wong
 */
public class MeemDropCommand extends CreateCommand {
	private CategoryProxy categoryProxy;
	private MeemPath meemPath;
	private String name;
	private String categoryEntryName;
	private VariableMap variableMap;

	/**
	 * Constructs an instance of <code>MeemDropCommand</code>.
	 * <p>
	 * @param container The container of this meem view model.
	 * @param categoryProxy The category contains this meem.
	 * @param variableMap The variable map associates with this meem.
	 * @param meemPath The meem path of the meem for the command.
	 * @param location The location of view model in its container.
	 */
	public MeemDropCommand(
		ElementContainer container, 
		CategoryProxy categoryProxy, 
		VariableMap variableMap,
		MeemPath meemPath, 
		Rectangle location) {

		super(container, null, location);
		Assert.isNotNull(categoryProxy);
		Assert.isNotNull(variableMap);
		this.meemPath = meemPath;
		this.variableMap = variableMap;
		this.categoryProxy = categoryProxy;
	}
	
	/**
	 * Sets the meem associates with this meem command.
	 * @param meem The meem associates with this meem command.
	 */	
	protected void setMeem(Meem meem) {
		child = meem;
	}
	
	/**
	 * Sets the preferred name.<p>
	 * @param entryName The prefered name if available.
	 */
	public void setName(String entryName) {
		this.name = entryName;
	}
	
	/**
	 * Gets the preferred name.<p>
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Gets the meem associates with this meem command.
	 * @return Meem The meem associates with this meem command.
	 */
	protected Meem getMeem() {
		return (Meem)child;
	}
	
	/**
	 * Gets the variable map associates with this command.
	 * @return VariableMap The variable map associates with this command.
	 */
	protected VariableMap getVariableMap() {
		return variableMap;
	}
	
	/**
	 * Gets the category entry name.
	 * <p>
	 * @return The category entry name.
	 */
	protected String getCategoryEntryName() {
		return categoryEntryName;
	}
	
	/**
	 * Gets the category where this object
	 * @return CategoryProxy
	 */
	protected CategoryProxy getCategory() {
		return categoryProxy;
	}
	
	/**
	 * Gets the path of the meem associates with this command.
	 * @return MeemPath The path of the meem associates with this command.
	 */
	protected MeemPath getMeemPath() {
		return meemPath;
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.common.model.commands.CreateCommand#execute()
	 */
	public void execute() {
		createMeem();
		super.execute();	
		createVariable();
		createCategoryEntry();
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.common.model.commands.CreateCommand#undo()
	 */
	public void undo() {
		deleteCategoryEntry();
		destroyVariable();
		super.undo();
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.common.model.commands.CreateCommand#redo()
	 */
	public void redo() {
		super.redo();
		createVariable();
		createCategoryEntry();
	}

	/**
	 * Creates the view model meem, associates it with a located meem from the 
	 * meem path, and set the meem as the child to be added to its container.
	 * <p>
	 */
	protected void createMeem() {
		// Clone the meem located by the meem path
		Meem meem = MeemModelFactory.create(InterMajikClientProxyFactory.getInstance().locate(getMeemPath()));
		meem.setCollapse(false);
		meem.setViewMode(ViewModeConstants.VIEW_MODE_DETAILED);
		setMeem(meem);
	}
	
	protected void createCategoryEntry() {
		if(categoryEntryName == null) {
			if(name != null) {
				// Use preferred name
				categoryEntryName = CategoryEntryNameFactory.
					createUniqueEntryName(getCategory(), name);
			}
			else {
				categoryEntryName = CategoryEntryNameFactory.
				createUniqueEntryName(getCategory(), getMeem().getProxy());
			}
		}
		getCategory().addEntry(	getCategoryEntryName(), 
														getMeem().getProxy().getUnderlyingMeem());
		getMeem().setName(categoryEntryName);
	}

	protected void deleteCategoryEntry() {
		if(getCategoryEntryName() == null) return;
		getCategory().removeEntry(getCategoryEntryName());
	}
	
	protected void createVariable() {
		IVariableSource source = 
			VariableSourceFactory.getInstance().createVariableSource(getMeem());
		ValueBag bag = source.extractAll();
		getVariableMap().update(getMeem().getPath(), bag);
	}
	
	protected void destroyVariable() {
		getVariableMap().remove(getMeem().getPath());
	}
}
