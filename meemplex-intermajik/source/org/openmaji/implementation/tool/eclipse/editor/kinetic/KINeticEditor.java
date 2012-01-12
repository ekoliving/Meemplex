/*
 * @(#)KINeticEditor.java
 * Created on 15/04/2003
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic;

import java.io.Serializable;
import java.util.List;


import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.RootEditPart;
import org.eclipse.gef.dnd.TemplateTransferDragSourceListener;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.ui.palette.PaletteContextMenuProvider;
import org.eclipse.gef.ui.palette.PaletteViewer;
import org.eclipse.gef.ui.parts.GraphicalEditorWithPalette;
import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;
import org.eclipse.gef.ui.properties.UndoablePropertySheetEntry;
import org.eclipse.gef.ui.stackview.CommandStackInspectorPage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.openmaji.implementation.tool.eclipse.client.ConfigurationClientStub;
import org.openmaji.implementation.tool.eclipse.client.ConfigurationHandlerProxy;
import org.openmaji.implementation.tool.eclipse.client.InterMajikClientProxyFactory;
import org.openmaji.implementation.tool.eclipse.client.LifeCycleClientStub;
import org.openmaji.implementation.tool.eclipse.client.MeemClientProxy;
import org.openmaji.implementation.tool.eclipse.client.security.SecurityListener;
import org.openmaji.implementation.tool.eclipse.client.security.SecurityManager;
import org.openmaji.implementation.tool.eclipse.editor.common.dnd.MeemTransferDropTargetListener;
import org.openmaji.implementation.tool.eclipse.editor.common.dnd.WedgeKitTransferDropTargetListener;
import org.openmaji.implementation.tool.eclipse.editor.common.pages.OverviewOutlinePage;
import org.openmaji.implementation.tool.eclipse.editor.features.animation.AnimatableManager;
import org.openmaji.implementation.tool.eclipse.editor.features.labels.ScalableFreeformLabelRootEditPart;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.builders.ConfigurationBuilder;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.Configuration;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.properties.FilterPropertySheetPage;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.variables.VariableSourceFactory;
import org.openmaji.implementation.tool.eclipse.properties.PropertySheetPage;
import org.openmaji.implementation.tool.eclipse.ui.dnd.NamedMeemTransfer;
import org.openmaji.implementation.tool.eclipse.ui.dnd.WedgeKitTransfer;
import org.openmaji.implementation.tool.eclipse.util.MeemEditorInput;
import org.openmaji.meem.wedge.configuration.ConfigurationClient;
import org.openmaji.meem.wedge.configuration.ConfigurationIdentifier;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClient;
import org.openmaji.meem.wedge.lifecycle.LifeCycleState;
import org.openmaji.meem.wedge.lifecycle.LifeCycleTransition;


/**
 * <code>KINeticEditor</code>.
 * <p>
 * @author Kin Wong
 */
public class KINeticEditor extends GraphicalEditorWithPalette {
	private IPartListener partListener = new IPartListener() {
		// If an open, unsaved file was deleted, query the user to either do a "Save As"
		// or close the editor.
		public void partActivated(IWorkbenchPart part) {}
		public void partBroughtToTop(IWorkbenchPart part) {}
		public void partClosed(IWorkbenchPart part) {}
		public void partDeactivated(IWorkbenchPart part) {}
		public void partOpened(IWorkbenchPart part) {}
	};
	
	private MeemClientProxy diagram = null;
	private KINeticActionBuilder actionBuilder = null;
	private AnimatableManager animatableManager = null;
	
	private Configuration configuration = null;
	private ConfigurationBuilder builder = null;
	private KINeticEditPartFactory editPartFactory = null;
	private double[] zoomLevels = {0.25, 0.5, 0.75, 1.0, 1.5, 1.75, 2.0, 2.5, 3.0, 4.0, 5.0};
	/** the overview outline page */
	private OverviewOutlinePage overviewOutlinePage;
	private FilterPropertySheetPage propertySheetPage;

	private SecurityListener securityListener = new SecurityListener() {
		public void onLogin(SecurityManager manager) {}
		public void onLogout(SecurityManager manager) {closeEditor(false);}
	};
	
	//=== Outbound Facet Implementations ===
	private ConfigurationClient configurationClient = new ConfigurationClientStub() {
		public void valueAccepted(ConfigurationIdentifier id, Serializable value) {
			if(id.equals(ConfigurationHandlerProxy.ID_MEEM_IDENTIFIER)) updateTitle();
		}
	};
	
	private LifeCycleClient lifeCycleClient = new LifeCycleClientStub() {
		private boolean goneReady = false;
		
		public void lifeCycleStateChanged(LifeCycleTransition transition) {
			if (transition.getCurrentState() == LifeCycleState.READY && !goneReady) {
				goneReady = true;
			}
			if(transition.getCurrentState() == LifeCycleState.ABSENT && goneReady) {
				closeEditor(false);
			}
		}
	};
	
	/**
	 * Constructs an instance of <code>KINeticEditor</code>.
	 * <p>
	 */
	public KINeticEditor() {
		setEditDomain(new KINeticEditDomain(this));
	}
	
	public void dispose() {
		if(diagram != null) {
			diagram.getLifeCycle().removeClient(lifeCycleClient);
			diagram.getConfigurationHandler().removeClient(configurationClient);
			animatableManager.stop();
			animatableManager = null;
		}
		
		SecurityManager.getInstance().removeSecurityListener(securityListener);
		builder.clear();
		
		getSite().getWorkbenchWindow().getPartService().removePartListener(partListener);
		partListener = null;
		super.dispose();
	}
	
	public ZoomManager getZoomManager() {
		ScrollingGraphicalViewer viewer = (ScrollingGraphicalViewer)getGraphicalViewer();
		if(viewer == null) return null;
		return ((ScalableFreeformLabelRootEditPart)viewer.getRootEditPart()).getZoomManager();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.ui.parts.GraphicalEditor#createGraphicalViewer(org.eclipse.swt.widgets.Composite)
	 */
	protected void createGraphicalViewer(Composite parent) {
		GraphicalViewer viewer = new ScrollingGraphicalViewer();
		viewer.createControl(parent);
		setGraphicalViewer(viewer);
		configureGraphicalViewer();
		hookGraphicalViewer();
		initializeGraphicalViewer();
	}

	/**
	 * @see org.eclipse.gef.ui.parts.GraphicalEditor#configureGraphicalViewer()
	 */
	protected void configureGraphicalViewer() {
		super.configureGraphicalViewer();
		ScrollingGraphicalViewer viewer = (ScrollingGraphicalViewer)getGraphicalViewer();
		
		//viewer.setKeyHandler(new ConfigurationViewerKeyHandler(viewer));
		viewer.setRootEditPart(new ScalableFreeformLabelRootEditPart());
		
		editPartFactory = new KINeticEditPartFactory();
		editPartFactory.
			setVariableMap(configuration.getProxy().getVariableMapProxy());

		viewer.setEditPartFactory(editPartFactory);
			viewer.setContextMenu(new KINeticContextMenuProvider(viewer, getActionRegistry()));

		initialiseDragDrop();
		SecurityManager.getInstance().addSecurityListener(securityListener);
		
		ZoomManager zoomManager = getZoomManager();
		zoomManager.setZoomLevels(zoomLevels);
		actionBuilder.createViewerActions(viewer);
	}
	
	/**
	 * Initialises drag-and-drop.
	 */	
	private void initialiseDragDrop() {
		ScrollingGraphicalViewer viewer = (ScrollingGraphicalViewer)getGraphicalViewer();
		
		// dnd Data from Palette		
		viewer.addDropTargetListener(	
			new PaletteTemplateTransferDropTargetListener(viewer));
		
		// dnd Data from Space Browser
		viewer.addDropTargetListener(	
			new MeemTransferDropTargetListener(
				viewer, 
				NamedMeemTransfer.getInstance()));
			
		// dnd Data from Toolkit, Meem view
		viewer.addDropTargetListener(	
			new MeemTransferDropTargetListener(
				viewer, 
				NamedMeemTransfer.getInstanceForClone()));
		
		// dnd Data From Toolkit, Wedge view
		viewer.addDropTargetListener(	
			new WedgeKitTransferDropTargetListener(
				viewer, 
				WedgeKitTransfer.getInstance()));
	}

	protected void configurePaletteViewer() {
		super.configurePaletteViewer();
		PaletteViewer viewer = getPaletteViewer();
		getPaletteViewer().setContextMenu(new PaletteContextMenuProvider(viewer));
	}

	/**
	 * @see org.eclipse.gef.ui.parts.GraphicalEditor#initializeGraphicalViewer()
	 */
	protected void initializeGraphicalViewer() {
		ScrollingGraphicalViewer viewer = (ScrollingGraphicalViewer)getGraphicalViewer();
		try {
			viewer.setContents(configuration);
		}
		catch(Exception exp) {
			exp.printStackTrace();
		}
	}
	
	protected void initializePaletteViewer() {
		super.initializePaletteViewer();
		getPaletteViewer().addDragSourceListener(
			new TemplateTransferDragSourceListener(getPaletteViewer()));
	}
	
	public PropertySheetPage getPropertySheetPage() {
		return propertySheetPage;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class type){
		if (type == IPropertySheetPage.class) {
			if(propertySheetPage != null) return propertySheetPage;
			propertySheetPage = new FilterPropertySheetPage();
			UndoablePropertySheetEntry entry = new UndoablePropertySheetEntry(getCommandStack());
			
			// TODO see why Kin used a subclass of UndoablePropertySheetEntry
//			entry.addPropertySheetEntryListener(
//					new IPropertySheetEntryListener() {
//						public void childEntriesChanged(IPropertySheetEntry node) {
//						}
//						public void errorMessageChanged(IPropertySheetEntry entry) {
//						}
//						public void valueChanged(IPropertySheetEntry entry) {
//						}
//					}
//				);
			
			propertySheetPage.setRootEntry(entry);
			return propertySheetPage;
		}
		else
		if (type == CommandStackInspectorPage.class) {
			return new CommandStackInspectorPage(getCommandStack());
		}
		else
		if(type == ZoomManager.class) {
			return getZoomManager();
		} 
		else
		if(type == AnimatableManager.class) {
			return animatableManager;
		}
		else
		if (type == IContentOutlinePage.class) {
			return getOverviewOutlinePage();		
		}
		else
		if(type == MeemClientProxy.class) {
			return diagram;
		}
		else
		return super.getAdapter(type);
	}
	
	protected OverviewOutlinePage getOverviewOutlinePage() {
		if (null == overviewOutlinePage && null != getGraphicalViewer()) {
			RootEditPart rootEditPart = getGraphicalViewer().getRootEditPart();
			if (rootEditPart instanceof ScalableFreeformRootEditPart) {
				overviewOutlinePage = new OverviewOutlinePage(
				(ScalableFreeformRootEditPart)rootEditPart);
			}
		}
		return overviewOutlinePage;
	}

	protected void createActions() {
		super.createActions();
		actionBuilder = 
			new KINeticActionBuilder(
				getActionRegistry(), 
				this, 
				getSelectionActions());
		actionBuilder.createActions();
	}
	
	public void setInput(IEditorInput input) {
		super.setInput(input);
		if (!(input instanceof MeemEditorInput)) return;
		
		final MeemEditorInput mei = (MeemEditorInput)input;
		MeemClientProxy meem = InterMajikClientProxyFactory.getInstance().locate(mei.getMeemPath());
		if (meem.getUnderlyingMeem() == null) return;
		animatableManager = new AnimatableManager();
		
		diagram = meem;
		diagram.getConfigurationHandler().addClient(configurationClient);
		diagram.getLifeCycle().addClient(lifeCycleClient);
		
		configuration = new Configuration(diagram);
		builder = new ConfigurationBuilder(VariableSourceFactory.getInstance());
		builder.setConfiguration(configuration);
		builder.activate();
		builder.refresh();
		updateTitle();
		animatableManager.start();
	}
		 
	protected void closeEditor(final boolean save) {
			getSite().getPage().closeEditor(KINeticEditor.this, save);
	}
	
	private void updateTitle() {
		if(diagram == null) return;
		String title = "Untitled";
				
		Object meemIdentifierValue = diagram.
			getConfigurationHandler().
			getValue(ConfigurationHandlerProxy.ID_MEEM_IDENTIFIER);
			if(meemIdentifierValue instanceof String) {
				title = (String)meemIdentifierValue;
			}

				
		MeemEditorInput editorInput = (MeemEditorInput)getEditorInput();
		if(editorInput != null) {
			if(editorInput.getViewOrdinal() != 0) {
				title += "(View " + Integer.toString(editorInput.getViewOrdinal()) + ")";
			}
		}
		setPartName(title);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.ui.parts.GraphicalEditor#updateActions(java.util.List)
	 */
	public void updateActions(List actionIds) {
		super.updateActions(actionIds);
	}

	/**
	 * @see org.eclipse.ui.IEditorPart#doSave(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void doSave(IProgressMonitor progressMonitor) {
	}

	/**
	 * @see org.eclipse.ui.IEditorPart#doSaveAs()
	 */
	public void doSaveAs() {
	}

	/**
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#setSite(IWorkbenchPartSite)
	 */
	protected void setSite(IWorkbenchPartSite site){
		super.setSite(site);
		getSite().getWorkbenchWindow().getPartService().addPartListener(partListener);
	}

	/**
	 * @see org.eclipse.ui.IEditorPart#gotoMarker(org.eclipse.core.resources.IMarker)
	 */
	public void gotoMarker(IMarker arg0) {
	}

	/**
	 * @see org.eclipse.ui.IEditorPart#isDirty()
	 */
	public boolean isDirty() {
		return false;
	}

	/**
	 * @see org.eclipse.ui.IEditorPart#isSaveAsAllowed()
	 */
	public boolean isSaveAsAllowed() {
		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.ui.parts.GraphicalEditorWithPalette#getPaletteRoot()
	 */
	protected PaletteRoot getPaletteRoot() {
		PaletteRoot palette = new PaletteRoot();
		palette.addAll(PaletteBuilder.createTools(palette));
		return palette;
	}
}
