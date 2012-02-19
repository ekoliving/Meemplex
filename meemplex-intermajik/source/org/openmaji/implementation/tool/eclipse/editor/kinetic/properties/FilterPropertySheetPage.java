/*
 * @(#)FilterPropertySheetPage.java
 * Created on 28/04/2004
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic.properties;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.Messages;
import org.openmaji.implementation.tool.eclipse.images.Images;
import org.openmaji.implementation.tool.eclipse.properties.PropertySheetPage;

/**
 * <code>FilterPropertySheetPage</code>.
 * <p>
 * @author Kin Wong
 */
public class FilterPropertySheetPage extends PropertySheetPage {
	private static final String FILTER_CONFIGURATION = "configuration";
	private static final String FILTER_DEFINITION = "definition";
	private static final String FILTER_PRESENTATION = "presentation";
	private static final String FILTER_NONE = "none";

	private Action configurationAction;
	private Action definitionAction;
	private Action presentationAction;
	private Action showAllAction;
	
	//private String currentFilter = FILTER_CONFIGURATION;
	 
	/**
	 * Constructs an instance of <code>FilterPropertySheetPage</code>.
	 * <p>
	 */
	public FilterPropertySheetPage() {
	}

	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.properties.PropertySheetPage#createActions()
	 */
	protected void createActions() {
		//=== Configuration Filter Action ===
		configurationAction = 
		new Action(Messages.FilterPropertySheet_Configuration_Label, IAction.AS_RADIO_BUTTON) {
			public void run() {setFilter(FILTER_CONFIGURATION);}
		};
		configurationAction.setDescription(Messages.FilterPropertySheet_Configuration_Description);
		configurationAction.setToolTipText(Messages.FilterPropertySheet_Configuration_Tooltip);
		configurationAction.setImageDescriptor(Images.ICON_CONFIGURATION);
		configurationAction.setHoverImageDescriptor(Images.ICON_CONFIGURATION);
		
		//=== Definition Action ===
		definitionAction = 
		new Action(Messages.FilterPropertySheet_Definition_Label, IAction.AS_RADIO_BUTTON) {
			public void run() {setFilter(FILTER_DEFINITION);}
		};
		definitionAction.setDescription(Messages.FilterPropertySheet_Definition_Description);
		definitionAction.setToolTipText(Messages.FilterPropertySheet_Definition_Tooltip);
		definitionAction.setImageDescriptor(Images.ICON_DEFINITION);
		definitionAction.setHoverImageDescriptor(Images.ICON_DEFINITION);
	
		//=== Presentation Action ===
		presentationAction = 
		new Action(Messages.FilterPropertySheet_Presentation_Label, IAction.AS_RADIO_BUTTON) {
			public void run() {setFilter(FILTER_PRESENTATION);}
		};
		presentationAction.setText(Messages.FilterPropertySheet_Presentation_Label);
		presentationAction.setDescription(Messages.FilterPropertySheet_Presentation_Description);
		presentationAction.setToolTipText(Messages.FilterPropertySheet_Presentation_Tooltip);
		presentationAction.setImageDescriptor(Images.ICON_PRESENTATION);
		presentationAction.setHoverImageDescriptor(Images.ICON_PRESENTATION);

		//=== Show-All Action ===
		showAllAction = 
		new Action(Messages.FilterPropertySheet_ShowAll_Label, IAction.AS_RADIO_BUTTON) {
			public void run() {setFilter(FILTER_NONE);}
		};
		showAllAction.setText(Messages.FilterPropertySheet_ShowAll_Label);
		showAllAction.setDescription(Messages.FilterPropertySheet_ShowAll_Description);
		showAllAction.setToolTipText(Messages.FilterPropertySheet_ShowAll_Tooltip);
		showAllAction.setImageDescriptor(Images.ICON_FILTER_NONE);
		showAllAction.setHoverImageDescriptor(Images.ICON_FILTER_NONE);
		
		setFilter(FILTER_NONE);
	}

	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.properties.PropertySheetPage#updateActions()
	 */
	protected void updateActions() {
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.properties.PropertySheetPage#contributeToMenu(org.eclipse.jface.action.IMenuManager)
	 */
	protected void contributeToMenu(IMenuManager menu) {
		menu.add(configurationAction);
		menu.add(definitionAction);
		menu.add(presentationAction);
		menu.add(showAllAction);
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.properties.PropertySheetPage#contributeToToolBar(org.eclipse.jface.action.IToolBarManager)
	 */
	protected void contributeToToolBar(IToolBarManager toolBar) {
		toolBar.add(configurationAction);
		toolBar.add(definitionAction);
		toolBar.add(presentationAction);
		toolBar.add(showAllAction);
	}

	private void setFilter(String filter) {
		getViewer().removeAllFilters();
		configurationAction.setChecked(false);
		definitionAction.setChecked(false);
		presentationAction.setChecked(false);
		showAllAction.setChecked(false);
	
			if(FILTER_CONFIGURATION.equals(filter)) {
			getViewer().addFilter(PropertyFilters.FILTER_CONFIGURATION);
			configurationAction.setChecked(true);
		}
		else
		if(FILTER_DEFINITION.equals(filter)) {
			getViewer().addFilter(PropertyFilters.FILTER_DEFINITION);
			definitionAction.setChecked(true);
		}
		else
		if(FILTER_PRESENTATION.equals(filter)) {
			getViewer().addFilter(PropertyFilters.FILTER_PRESENTATION);
			presentationAction.setChecked(true);
		}
		else {
			showAllAction.setChecked(true);
		}
	}
}
