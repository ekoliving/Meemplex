/*
 * @(#)PaletteBuilder.java
 * Created on 30/05/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic;

import java.util.ArrayList;
import java.util.List;


import org.eclipse.draw2d.Cursors;
import org.eclipse.gef.Tool;
import org.eclipse.gef.palette.CombinedTemplateCreationEntry;
import org.eclipse.gef.palette.ConnectionCreationToolEntry;
import org.eclipse.gef.palette.MarqueeToolEntry;
import org.eclipse.gef.palette.PaletteContainer;
import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.gef.palette.PaletteGroup;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.palette.SelectionToolEntry;
import org.eclipse.gef.palette.ToolEntry;
import org.eclipse.gef.requests.CreationFactory;
import org.eclipse.gef.tools.ConnectionCreationTool;
import org.eclipse.jface.resource.ImageDescriptor;
import org.openmaji.implementation.intermajik.model.ViewModeConstants;
import org.openmaji.implementation.tool.eclipse.client.*;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.Category;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.Facet;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.Meem;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.MeemPlex;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.Wedge;
import org.openmaji.implementation.tool.eclipse.images.Images;
import org.openmaji.implementation.tool.eclipse.plugin.MajiPlugin;
import org.openmaji.meem.definition.MeemDefinition;
import org.openmaji.meem.definition.MeemDefinitionFactory;
import org.openmaji.meem.wedge.lifecycle.LifeCycleState;


/**
 * <code>PaletteBuilder</code> builds the tool palette of the KINetic editor.
 * <p>
 * @author Kin Wong
 */
public class PaletteBuilder {
	// Creatable Parts
	public static final String TEMPLATE_WEDGE = "wedge template";
	public static final String TEMPLATE_INBOUND_FACET= "inbound facet template";
	public static final String TEMPLATE_OUTBOUND_FACET = "outbound facet template";

	// Creatable Components
	public static final String TEMPLATE_MEEM = "meem template";
	public static final String TEMPLATE_MEEMPLEX = "meemplex template";
	public static final String TEMPLATE_CATEGORY = "category template";

	/**
	 * Creates all the tools at the root of the palette.
	 * @param root The root of palette model.
	 * @return List A list contains all the palette entries for KINetic editor.
	 */
	static public List createTools(PaletteRoot root){
		List categories = new ArrayList();
		categories.add(createControlGroup(root));
		//categories.add(createPartsDrawer());
		categories.add(createComponentsDrawer());
		//categories.add(createMeemPlexDrawer());
		return categories;
	}
	
	/**
	 * Creates the control group set including Selection, Marquee and connection
	 * creation tools.
	 * @param root The platte root model.
	 * @return PaletteContainer The palette container with all the control group
	 * tools.
	 */
	static private PaletteContainer createControlGroup(PaletteRoot root){
		PaletteGroup controlGroup = new PaletteGroup(Messages.Palette_ToolsGroup_Label);
		//controlGroup.setType(PaletteContainer.PALETTE_TYPE_GROUP);
		List entries = new ArrayList();
		
		ToolEntry tool = new SelectionToolEntry(
			Messages.Palette_ToolsGroup_Selection_Label,
			Messages.Palette_ToolsGroup_Selection_Description);
		entries.add(tool);
		root.setDefaultEntry(tool);

		tool = new MarqueeToolEntry(
			Messages.Palette_ToolsGroup_Marquee_Label,
			Messages.Palette_ToolsGroup_Marquee_Description);
		entries.add(tool);

		tool = new CfgConnectionCreationToolEntry(
			Messages.Palette_ToolsGroup_ConnectionCreation_Label,
			Messages.Palette_ToolsGroup_ConnectionCreation_Description,
			null,
			ImageDescriptor.createFromFile(Images.class, "icons/connection16.gif"),
			ImageDescriptor.createFromFile(Images.class, "icons/connection24.gif"));
		entries.add(tool);
		
		controlGroup.addAll(entries);
		return controlGroup;
	}
	
	/**
	 * Creates the part group including wedge and facet creation tools.
	 * @return PaletteContainer The palette container with all the part creation
	 * tools.
	 */
//	static private PaletteContainer createPartsDrawer() {
//		PaletteDrawer drawer = new PaletteDrawer(
//			Messages.Palette_Parts_Label,
//			ImageDescriptor.createFromFile(Images.class, "icons/parts16.gif"));
//
//		List entries = new ArrayList();
//
//		CombinedTemplateCreationEntry combined = 
//			new CombinedTemplateCreationEntry(
//			Messages.Palette_Parts_Wedge_Label,
//			Messages.Palette_Parts_Wedge_Description,
//			TEMPLATE_WEDGE,
//			new WedgeFactory(),
//			ImageDescriptor.createFromFile(Images.class, "icons/wedge16.gif"),
//			ImageDescriptor.createFromFile(Images.class, "icons/wedge24.gif"));
//		entries.add(combined);
//		
//		combined = 
//			new CombinedTemplateCreationEntry(
//			Messages.Palette_Parts_Inbound_Facet_Label,
//			Messages.Palette_Parts_Inbound_Facet_Description,
//			TEMPLATE_INBOUND_FACET,
//			new InboundFacetFactory(),
//			ImageDescriptor.createFromFile(Images.class, "icons/facet16.gif"),
//			ImageDescriptor.createFromFile(Images.class, "icons/facet24.gif"));
//		entries.add(combined);
//
//		combined = 
//			new CombinedTemplateCreationEntry(
//			Messages.Palette_Parts_Outbound_Facet_Label,
//			Messages.Palette_Parts_Outbound_Facet_Description,
//			TEMPLATE_OUTBOUND_FACET,
//			new OutboundFacetFactory(),
//			ImageDescriptor.createFromFile(Images.class, "icons/facet16.gif"),
//			ImageDescriptor.createFromFile(Images.class, "icons/facet24.gif"));
//		entries.add(combined);
//
//		drawer.addAll(entries);
//		return drawer;
//	}

	static private PaletteContainer createComponentsDrawer() {
		PaletteDrawer drawer = new PaletteDrawer(
			Messages.Palette_Components_Label,
			ImageDescriptor.createFromFile(Images.class, "icons/components16.gif"));

		List entries = new ArrayList();
	
		CombinedTemplateCreationEntry combined = 
			new CombinedTemplateCreationEntry(
			Messages.Palette_Components_Meem_Label,
			Messages.Palette_Components_Meem_Description,
			TEMPLATE_MEEM,
			new MeemFactory(),
			ImageDescriptor.createFromFile(Images.class, "icons/meem16.gif"),
			ImageDescriptor.createFromFile(Images.class, "icons/meem48.gif"));
		entries.add(combined);

/*
		combined = new CombinedTemplateCreationEntry(
			Messages.Palette_Components_MeemPlex_Label,
			Messages.Palette_Components_MeemPlex_Description,
			TEMPLATE_MEEMPLEX,
			new MeemPlexFactory(),
			ImageDescriptor.createFromFile(Images.class, "icons/MeemPlex16.gif"),
			ImageDescriptor.createFromFile(Images.class, "icons/MeemPlex24.gif"));
		entries.add(combined);
*/
		combined = new CombinedTemplateCreationEntry(
			Messages.Palette_Components_Category_Label,
			Messages.Palette_Components_Category_Description,
			TEMPLATE_CATEGORY,
			new CategoryFactory(),
			ImageDescriptor.createFromFile(Images.class, "icons/category16.gif"), 
			ImageDescriptor.createFromFile(Images.class, "icons/category48.gif"));
		entries.add(combined);
		
		//entries.add(new PaletteSeparator());

		drawer.addAll(entries);
		return drawer;
	}

//	static private PaletteContainer createMeemPlexDrawer() {
//		PaletteDrawer drawer = new PaletteDrawer( "Automation",
//			//ConfigurationMessages.Palette_Components_Label,
//			ImageDescriptor.createFromFile(Images.class, "icons/components16.gif"));
//		 return drawer;
//	}
	
	static public CreationFactory getTemplateFactory(String template) {
		if(TEMPLATE_WEDGE.equals(template)) {
			return new WedgeFactory();
		}
		else
		if(TEMPLATE_INBOUND_FACET.equals(template)) {
			return new InboundFacetFactory();
		}
		else
		if(TEMPLATE_OUTBOUND_FACET.equals(template)) {
			return new OutboundFacetFactory();
		}
		else
		if(TEMPLATE_MEEM.equals(template)) {
			return new MeemFactory();
		}
		else
		if(TEMPLATE_MEEMPLEX.equals(template)) {
			return new MeemPlexFactory();
		}
		else
		if(TEMPLATE_CATEGORY.equals(template)) {
			return new CategoryFactory();
		}
		return null;
	}
}

class MeemFactory implements CreationFactory {
	/* (non-Javadoc)
	 * @see org.eclipse.gef.requests.CreationFactory#getNewObject()
	 */
	public Object getNewObject() {
		
		org.openmaji.meem.Meem lcmMeem = null;
		MeemClientProxy worksheetProxy = MajiPlugin.getDefault().getActiveWorksheetProxy();
		if (worksheetProxy != null) {
		  LifeCycleManagementClientProxy proxy = worksheetProxy.getLifeCycleManagementClient();
			
			while (!proxy.isContentInitialized()) {
				try {
					Thread.sleep(250);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		  
			lcmMeem = (org.openmaji.meem.Meem) proxy.getParentLifeCycleManager();
			
			if (lcmMeem != null) {
				lcmMeem = InterMajikClientProxyFactory.getInstance().locate(lcmMeem.getMeemPath()).getUnderlyingMeem();
			}
		} 
		
		final MeemClientProxy proxy = InterMajikClientProxyFactory.getInstance().create(
				DefinitionFactory.createMeem(), 
				LifeCycleState.LOADED, 
				lcmMeem);
		
		Runnable runnable = new Runnable() {
			public void run() {				
				LifeCycleLimitProxy lifeCycleLimitProxy = proxy.getLifeCycleLimit();
				while (!lifeCycleLimitProxy.isConnected()) {
					try {
						Thread.sleep(250);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				lifeCycleLimitProxy.limitLifeCycleState(LifeCycleState.READY);
			}
		};
		
		new Thread(runnable).start();
		
		Meem meem = new Meem(proxy);
		meem.setName("Meem ");
		meem.setViewMode(ViewModeConstants.VIEW_MODE_DETAILED);
		meem.setCollapse(false);
		return meem;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.requests.CreationFactory#getObjectType()
	 */
	public Object getObjectType() {
		return Meem.class;
	}
}

class CategoryFactory implements CreationFactory {
	/* (non-Javadoc)
	 * @see org.eclipse.gef.requests.CreationFactory#getNewObject()
	 */
	public Object getNewObject() {
		MeemDefinition definition = MeemDefinitionFactory.spi.create().createMeemDefinition(org.openmaji.system.space.Category.class);
		org.openmaji.meem.Meem lcmMeem = null;
		MeemClientProxy worksheetProxy = MajiPlugin.getDefault().getActiveWorksheetProxy();
		if (worksheetProxy != null) {
			LifeCycleManagementClientProxy proxy = worksheetProxy.getLifeCycleManagementClient();
			
			while (!proxy.isContentInitialized()) {
				try {
					Thread.sleep(250);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			lcmMeem = (org.openmaji.meem.Meem) proxy.getParentLifeCycleManager();
			
			if (lcmMeem != null) {
				lcmMeem = InterMajikClientProxyFactory.getInstance().locate(lcmMeem.getMeemPath()).getUnderlyingMeem();
			}
		}
		
		MeemClientProxy proxy = InterMajikClientProxyFactory.getInstance().create(
				definition, 
				LifeCycleState.READY, 
				lcmMeem);
		
		Category category = new Category(proxy);
		category.setName("Category ");
		category.setCollapse(false);
		return category;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.requests.CreationFactory#getObjectType()
	 */
	public Object getObjectType() {
		return Category.class;
	}
}


class MeemPlexFactory implements CreationFactory {
	/* (non-Javadoc)
	 * @see org.eclipse.gef.requests.CreationFactory#getNewObject()
	 */
	public Object getNewObject() {
		return new MeemPlex(null);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.requests.CreationFactory#getObjectType()
	 */
	public Object getObjectType() {
		return MeemPlex.class;
	}
}

class WedgeFactory implements CreationFactory {
	/* (non-Javadoc)
	 * @see org.eclipse.gef.requests.CreationFactory#getNewObject()
	 */
	public Object getNewObject() {
		Wedge wedge = new Wedge();
		wedge.setCollapse(false);
		return wedge;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.requests.CreationFactory#getObjectType()
	 */
	public Object getObjectType() {
		 return Wedge.class;
		
	}
}

abstract class FacetFactory implements CreationFactory {
	/* (non-Javadoc)
	 * @see org.eclipse.gef.requests.CreationFactory#getObjectType()
	 */
	public Object getObjectType() {
		return Facet.class;
	}
}

class InboundFacetFactory extends FacetFactory {
	/* (non-Javadoc)
	 * @see org.eclipse.gef.requests.CreationFactory#getNewObject()
	 */
	public Object getNewObject() {
		//Facet facet = new FacetInbound();
		//facet.setName("Inbound Facet " + System.currentTimeMillis());
		//return facet;
		return null;
	}
}

class OutboundFacetFactory extends FacetFactory {
	/* (non-Javadoc)
	 * @see org.eclipse.gef.requests.CreationFactory#getNewObject()
	 */
	public Object getNewObject() {
		//Facet facet = new FacetOutbound();
		//facet.setName("Outbound Facet " + System.currentTimeMillis());
		//return facet;
		return null;
	}
}

class CfgConnectionCreationToolEntry extends ConnectionCreationToolEntry {
		
	/**
	 * Constructs an instance of <code>CfgConnectionCreationToolEntry</code>.
	 * <p>
	 * @param label
	 * @param shortDesc
	 * @param factory
	 * @param iconSmall
	 * @param iconLarge
	 */
	public CfgConnectionCreationToolEntry(
		String label,
		String shortDesc,
		CreationFactory factory,
		ImageDescriptor iconSmall,
		ImageDescriptor iconLarge) {
		super(label, shortDesc, factory, iconSmall, iconLarge);
	}
	/* (non-Javadoc)
	 * @see org.eclipse.gef.palette.ConnectionCreationToolEntry#createTool()
	 */
	public Tool createTool() {
		ConnectionCreationTool tool = new ConnectionCreationTool(factory);
		tool.setDefaultCursor(Cursors.CROSS);
		return tool;
	}
}

