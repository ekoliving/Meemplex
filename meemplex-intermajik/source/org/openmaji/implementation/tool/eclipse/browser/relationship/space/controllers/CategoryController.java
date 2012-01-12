/*
 * @(#)CategoryController.java
 * Created on 28/01/2004
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.browser.relationship.space.controllers;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.PrivilegedAction;

import javax.security.auth.Subject;


import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.openmaji.implementation.common.importexport.ImportExportManager;
import org.openmaji.implementation.intermajik.worksheet.WorksheetMeem;
import org.openmaji.implementation.server.manager.lifecycle.subsystem.SubsystemMeem;
import org.openmaji.implementation.tool.eclipse.browser.common.actions.OpenWorksheetAction;
import org.openmaji.implementation.tool.eclipse.client.*;
import org.openmaji.implementation.tool.eclipse.client.security.SecurityManager;
import org.openmaji.implementation.tool.eclipse.hierarchy.nodes.CategoryNode;
import org.openmaji.implementation.tool.eclipse.images.Images;
import org.openmaji.implementation.tool.eclipse.plugin.MajiPlugin;
import org.openmaji.implementation.tool.eclipse.ui.dialog.CreateWorksheetDialog;
import org.openmaji.implementation.tool.eclipse.ui.dialog.DuplicateInputValidator;
import org.openmaji.meem.Meem;
import org.openmaji.meem.MeemPath;
import org.openmaji.meem.definition.MeemDefinition;
import org.openmaji.meem.definition.MeemDefinitionFactory;
import org.openmaji.meem.space.Space;
import org.openmaji.meem.wedge.lifecycle.LifeCycleState;
import org.openmaji.server.helper.MeemPathResolverHelper;
import org.openmaji.system.manager.lifecycle.subsystem.SubsystemFactoryClient;
import org.openmaji.system.space.Category;
import org.openmaji.system.space.hyperspace.StandardHyperSpaceCategory;


/**
 * <code>CategoryController</code>.
 * <p>
 * @author Kin Wong
 */
public class CategoryController extends MeemController {
	private static final String WORKSHEET_SUBSYSTEM = " Worksheet Subsystem";
	
	Action addCategoryAction;
	Action addWorksheetAction;
	Action addDiagnosticViewAction;	
	private Action importAction;
	private Action exportAction;
	/**
	 * Constructs an instance of <code>CategoryController</code>.
	 * <p>
	 * @param node
	 */
	public CategoryController(CategoryNode node) {
		super(node);
	}
	
	protected CategoryNode getCategoryNode() {
		return (CategoryNode)getNode();
	}
	
	/**
	 * @see org.openmaji.implementation.tool.eclipse.browser.relationship.space.controllers.MeemController#createActions()
	 */
	protected void createActions() {
		super.createActions();
		addCategoryAction = new Action("Add Category...") {
			public void run() {
				addCategory();
			}
		};
		addCategoryAction.setImageDescriptor(Images.ICON_CATEGORY);
		addCategoryAction.setToolTipText("Add new category");

		addWorksheetAction = new Action("Add Worksheet...") {
			public void run() {
				addWorksheet();
			}
		};
		addWorksheetAction.setImageDescriptor(Images.ICON_WORKSHEET);
		addWorksheetAction.setToolTipText("Add Worksheet");
		
		importAction = new Action("Import...") {
			public void run() {
				importMeem();
			}
		};
		importAction.setToolTipText("Import...");
		importAction.setEnabled(true);

		exportAction = new Action("Export...") {
			public void run() {
				exportMeem();
			}
		};
		exportAction.setToolTipText("Export...");
		exportAction.setEnabled(true);
	}
	
	/**
	 * @see org.openmaji.implementation.tool.eclipse.browser.relationship.space.controllers.MeemController#destroyActions()
	 */
	protected void destroyActions() {
		addCategoryAction = null;
		addWorksheetAction = null;
		addDiagnosticViewAction = null;
		super.destroyActions();

	}

	/**
	 * @see org.openmaji.implementation.tool.eclipse.browser.relationship.space.controllers.MeemController#fillMenu(org.eclipse.jface.action.IMenuManager)
	 */
	public void fillMenu(IMenuManager menu) {
		super.fillMenu(menu);
		menu.appendToGroup(GROUP_IO, exportAction);
		if(getCategoryNode().getCategory().isReadOnly()) return;
		menu.appendToGroup(GROUP_CREATION, addCategoryAction);
		menu.appendToGroup(GROUP_CREATION, addWorksheetAction);
		menu.appendToGroup(GROUP_IO, importAction);		
	}
	
	/**
	 * @see org.openmaji.implementation.tool.eclipse.browser.relationship.space.controllers.MeemController#fillMainMenu(org.eclipse.jface.action.IMenuManager)
	 */
	public void fillMainMenu(IMenuManager menu) {
		super.fillMainMenu(menu);
		if (menu.find(addWorksheetAction.getId()) != null) {
			menu.remove(addWorksheetAction.getId());
		}
		if(getCategoryNode().getCategory().isReadOnly()) return;
		
		menu.appendToGroup(MajiPlugin.GROUP_START, addWorksheetAction);
	}

	
	private void addCategory() {
		MeemDefinition definition = MeemDefinitionFactory.spi.create().createMeemDefinition(Category.class);
		createMeem(definition, "Add Category");
	}
	
	private void addWorksheet() {
		MeemDefinition definition = MeemDefinitionFactory.spi.create().createMeemDefinition(WorksheetMeem.class);
		createWorksheetMeem(definition);
	}
	
	/**
	 * Creates a new meem and add it to this category.
	 * @param definition
	 * @param title
	 */
	protected MeemClientProxy createMeem(MeemDefinition definition, String title) {
		CategoryProxy category = getCategoryNode().getCategory();
		InputDialog dlg = new InputDialog(getShell(), title, "Enter name:", "", 
			new DuplicateInputValidator(getCategoryEntryNames(category), "Name already exists"));

		if(dlg.open() != Window.OK) return null;

		MeemClientProxy proxy = 
			InterMajikClientProxyFactory.getInstance().create(definition, LifeCycleState.READY);
		String name = CategoryEntryNameFactory.createUniqueEntryName(category, dlg.getValue());
		category.addEntry(name, proxy.getUnderlyingMeem());
		proxy.getConfigurationHandler().
			valueChanged(ConfigurationHandlerProxy.ID_MEEM_IDENTIFIER, dlg.getValue());
		return proxy;
	}
	
	protected void createWorksheetMeem(MeemDefinition definition) {
		CategoryProxy category = getCategoryNode().getCategory();

		CreateWorksheetDialog dlg = new CreateWorksheetDialog(getShell(),	new DuplicateInputValidator(getCategoryEntryNames(category), "Name already exists"));

		if(dlg.open() != Window.OK) return;

		MeemClientProxy proxyWorksheet;
		
		String name = CategoryEntryNameFactory.createUniqueEntryName(category, dlg.getWorksheetName());
		
		definition.getMeemAttribute().setIdentifier(name);
		
		// subsystem
		String subsystemName = dlg.getWorksheetSubsystem();
		if (subsystemName.equals(CreateWorksheetDialog.DEFAULT_WORKSHEET_SUBSYSTEM)) {
			 proxyWorksheet = InterMajikClientProxyFactory.getInstance().create(definition, LifeCycleState.READY);
			 worksheetMeemCreated(proxyWorksheet, name);
		} else if (subsystemName.equals(CreateWorksheetDialog.NEW_WORKSHEET_SUBSYSTEM)) {
			// create new subsystem
			createWorksheetSubsystem(name, definition);
		} else {
			// find existing subsystem
			Meem subsystemMeem = InterMajikClientProxyFactory.getInstance().locate(MeemPath.spi.create(Space.HYPERSPACE, StandardHyperSpaceCategory.MAJI_SUBSYSTEM_INSTALLED + "/" + subsystemName)).getUnderlyingMeem();
			proxyWorksheet = InterMajikClientProxyFactory.getInstance().create(definition, LifeCycleState.READY, subsystemMeem);
			worksheetMeemCreated(proxyWorksheet, name);
		}

	}
	
	private void worksheetMeemCreated(final MeemClientProxy proxyWorksheet, String name) {
		CategoryProxy category = getCategoryNode().getCategory();
		
		category.addEntry(name, proxyWorksheet.getUnderlyingMeem());
		proxyWorksheet.getConfigurationHandler().valueChanged(ConfigurationHandlerProxy.ID_MEEM_IDENTIFIER, name);

		// this is a bit dodgy, but has to be done otherwise the proxy won't be initialized properly and drag/drop won't be enabled
		new Thread(new Runnable() {
			public void run() {
				while (!proxyWorksheet.getVariableMapProxy().isConnected()) {
					try {
						Thread.sleep(250);
					} catch (InterruptedException e) {
					}
				}
				proxyWorksheet.getSynchronizer().execute(new Runnable() {
					public void run() {
						OpenWorksheetAction.openWorksheet(getViewPart(), proxyWorksheet.getMeemPath());
					}
				});
			}
		}).start();
	}
	
	private void createWorksheetSubsystem(final String name, final MeemDefinition definition) {
		final SubsystemFactoryProxy proxySubsystemFactory = InterMajikClientProxyFactory.getInstance().locateSubsystemFactory().getSubsystemFactoryProxy();
		proxySubsystemFactory.addClient(new SubsystemFactoryClient() {
		  public void subsystemCreated(Meem subsystemMeem, MeemDefinition subsystemMeemDefinition){
		  	if (subsystemMeemDefinition.getMeemAttribute().getIdentifier().equals(name + WORKSHEET_SUBSYSTEM)) {
		  		proxySubsystemFactory.removeClient(this);
			  	Meem meem = InterMajikClientProxyFactory.getInstance().locate(subsystemMeem.getMeemPath()).getUnderlyingMeem();
			  	worksheetMeemCreated(InterMajikClientProxyFactory.getInstance().create(definition, LifeCycleState.READY, meem), name);
		  	}
		  };

			public void definitionsAdded(MeemDefinition[] meemDefinitions){};
		  public void definitionsRemoved(MeemDefinition[] meemDefinitions){};
		  public void subsystemDestroyed(Meem meem){};
		});
		
		proxySubsystemFactory.createSubsystem(getMeemDefinition(name));
	}

	/**
	 * 
	 */
	private void importMeem() {
		final String importFileName = getFileName(true);
		
		if (importFileName == null)
			return;
			
		final File file = new File(importFileName);
			
		Subject.doAs(SecurityManager.getInstance().getSubject(), 	
			new PrivilegedAction() {
				public Object run() {	
					
					URL url = null;
					try {
						url = file.toURL();
					} catch (MalformedURLException e) {
						e.printStackTrace();
						return null;
					}
					
					MeemPath importExportManagerMeemPath = MeemPath.spi.create(Space.HYPERSPACE, StandardHyperSpaceCategory.MAJI_SYSTEM + "/importExport");
					
					Meem importExportManagerMeem = MeemPathResolverHelper.getInstance().resolveMeemPath(importExportManagerMeemPath);
					
					MeemPath meemPath = getMeemNode().getMeemPath();
			
					ImportExportManager importExportManager = (ImportExportManager)SecurityManager.getInstance().getGateway().getTarget(importExportManagerMeem, "importExportManager", ImportExportManager.class);
			
					importExportManager.createImportMeem(url, meemPath);	
					
					return null;
				}
			}); 
		
	}

	/**
	 * 
	 */
	private void exportMeem() {
		final String exportFileName = getFileName(false);

		if(exportFileName == null) return;
		
		final File file = new File(exportFileName);
		
		Subject.doAs(SecurityManager.getInstance().getSubject(), 	
			new PrivilegedAction() {
				public Object run() {
					
					URL url = null;
					try {
						url = file.toURL();
					} catch (MalformedURLException e) {
						e.printStackTrace();
						return null;
					}
				
					MeemPath importExportManagerMeemPath = MeemPath.spi.create(Space.HYPERSPACE, StandardHyperSpaceCategory.MAJI_SYSTEM + "/importExport");
		
					Meem importExportManagerMeem = MeemPathResolverHelper.getInstance().resolveMeemPath(importExportManagerMeemPath);
					
					MeemPath meemPath = getMeemNode().getMeemPath();
			
					ImportExportManager importExportManager = (ImportExportManager)SecurityManager.getInstance().getGateway().getTarget(importExportManagerMeem, "importExportManager", ImportExportManager.class);
			
					importExportManager.createExportMeem(meemPath, url, 0);
					
					return null;
				}
		}); 
	}

	private String getFileName(boolean open) {
		FileDialog dialog = new FileDialog(getSite().getShell(), open ? SWT.OPEN : SWT.SAVE);
		String userDir = System.getProperty("user.home");
		dialog.setFileName(userDir + System.getProperty("file.separator") + "meems.export");
		return dialog.open();
	}
	
	private MeemDefinition getMeemDefinition(String name) {
		MeemDefinition meemDefinition = MeemDefinitionFactory.spi.create().createMeemDefinition(SubsystemMeem.class);
		
		meemDefinition.getMeemAttribute().setIdentifier(name + WORKSHEET_SUBSYSTEM);
		
		return meemDefinition;
	}
}
