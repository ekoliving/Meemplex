/*
 * @(#)MeemPropertySource.java
 * Created on 15/09/2003
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic.properties;

import java.util.ArrayList;


import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.openmaji.implementation.intermajik.model.ViewMode;
import org.openmaji.implementation.intermajik.model.ViewModeConstants;
import org.openmaji.implementation.tool.eclipse.client.ConfigurationHandlerProxy;
import org.openmaji.implementation.tool.eclipse.client.ErrorHandlerProxy;
import org.openmaji.implementation.tool.eclipse.client.LifeCycleLimitProxy;
import org.openmaji.implementation.tool.eclipse.client.LifeCycleProxy;
import org.openmaji.implementation.tool.eclipse.client.MetaMeemProxy;
import org.openmaji.implementation.tool.eclipse.editor.common.model.Element;
import org.openmaji.implementation.tool.eclipse.editor.features.properties.LocationPropertySource;
import org.openmaji.implementation.tool.eclipse.editor.features.properties.ObjectComboBoxPropertyDescriptor;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.Messages;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.commands.CategoryEntryRenameCommand;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.Diagram;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.Meem;
import org.openmaji.implementation.tool.eclipse.ui.property.ConfigurationPropertySource;
import org.openmaji.meem.wedge.lifecycle.LifeCycleState;


/**
 * <code>MeemPropertySource</code>.
 * <p>
 * @author Kin Wong
 */
public class MeemPropertySource implements IPropertySource {

	static private org.eclipse.ui.views.properties.PropertyDescriptor[] descriptors;
	static private Object[] lifeCycleStates = {
		new ComboLifeCycleState(LifeCycleState.ABSENT),
		new ComboLifeCycleState(LifeCycleState.DORMANT),
		new ComboLifeCycleState(LifeCycleState.LOADED),
		new ComboLifeCycleState(LifeCycleState.READY)
	};
		
	static private Object[] viewModes = new Object[] {
		ViewModeConstants.VIEW_MODE_DETAILED,
		ViewModeConstants.VIEW_MODE_ICONIC
	};
	
	static {
		ArrayList<PropertyDescriptor> descList = new ArrayList<PropertyDescriptor>();
		
		PropertyDescriptor 
		desc = new TextPropertyDescriptor(Element.ID_NAME, Messages.Property_Meem_Name_Label);
		desc.setFilterFlags(PropertyFilters.STRINGS_FILTER_DEFINITION);
		descList.add(desc);
		
		desc = new PropertyDescriptor(Meem.ID_SIZE, Messages.Property_Meem_Size_Label);
		desc.setFilterFlags(PropertyFilters.STRINGS_FILTER_PRESENTATION);
		descList.add(desc);
		
		desc = new PropertyDescriptor(Meem.ID_LOCATION, Messages.Property_Meem_Location_Label);
		desc.setFilterFlags(PropertyFilters.STRINGS_FILTER_PRESENTATION);
		descList.add(desc);

		desc = 
			new ObjectComboBoxPropertyDescriptor(
				Meem.ID_VIEW_MODE, 
				Messages.Property_Meem_ViewMode_Label, 
				viewModes);
		desc.setFilterFlags(PropertyFilters.STRINGS_FILTER_PRESENTATION);
		descList.add(desc);
		
		desc = new PropertyDescriptor(Meem.ID_MEEM_PATH, Messages.Property_Meem_MeemPath_Label);
		desc.setFilterFlags(PropertyFilters.STRINGS_FILTER_DEFINITION);
		descList.add(desc);
		
		desc = 
			new ObjectComboBoxPropertyDescriptor(	
				LifeCycleProxy.ID_LIFE_CYCLE_STATE, 
				Messages.Property_Meem_LifeCycleState_Label, 
				lifeCycleStates);
		desc.setDescription(Messages.Property_Meem_LifeCycleState_Description);
		desc.setFilterFlags(PropertyFilters.STRINGS_FILTER_DEFINITION);
		descList.add(desc);

		desc = 
			new ObjectComboBoxPropertyDescriptor(	
				LifeCycleLimitProxy.ID_LIFE_CYCLE_STATE_LIMIT, 
				Messages.Property_Meem_LifeCycleStateLimit_Label, 
				lifeCycleStates);
		desc.setDescription(Messages.Property_Meem_LifeCycleStateLimit_Description);
		desc.setFilterFlags(PropertyFilters.STRINGS_FILTER_DEFINITION);
		descList.add(desc);
																						
		desc = 
			new PropertyDescriptor(	MetaMeemProxy.ID_MEEM_ATTRIBUTE, 
															Messages.Property_Meem_MeemAttributes_Label);
		desc.setFilterFlags(PropertyFilters.STRINGS_FILTER_DEFINITION);
		descList.add(desc);
															
		desc = 
			new PropertyDescriptor(	ConfigurationHandlerProxy.ID_CONFIGURATION, 
															Messages.Property_Meem_Configuration_Label);
		desc.setFilterFlags(PropertyFilters.STRINGS_FILTER_CONFIGURATION);
		descList.add(desc);

		
		desc = 
			new PropertyDescriptor(	ErrorHandlerProxy.ID_THROWABLE, 
															Messages.Property_Meem_LastMessage_Label);
		desc.setFilterFlags(PropertyFilters.STRINGS_FILTER_DIAGNOSIS);
		descList.add(desc);

		desc = 
			new PropertyDescriptor(	ConfigurationHandlerProxy.ID_LAST_REJECTED_REASON, 
															Messages.Property_Meem_LastRejectedReason_Label);
		desc.setDescription(Messages.Property_Meem_LastRejectedReason_Description);
		desc.setFilterFlags(PropertyFilters.STRINGS_FILTER_CONFIGURATION);
		descList.add(desc);

		descriptors = 
		(PropertyDescriptor[])descList.toArray(new PropertyDescriptor[0]);
	}
	
	private Meem meem;
	public MeemPropertySource(Meem meem) {
		this.meem = meem;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyValue(java.lang.Object)
	 */
	public Object getPropertyValue(Object id) {
		if(id.equals(Element.ID_NAME)) {
			return meem.getName();
		}
		else
		if(id.equals(Meem.ID_SIZE)) 
			return new DimensionPropertySource(meem.getSize());
		else
		if(id.equals(Meem.ID_LOCATION))
			return new LocationPropertySource(meem.getLocation());
		else
		if(id.equals(Meem.ID_VIEW_MODE))
			return meem.getViewMode();
		else
		if(id.equals(Meem.ID_MEEM_PATH))
			return meem.getProxy().getMeemPath().toString();
		else
		if(id.equals(LifeCycleProxy.ID_LIFE_CYCLE_STATE)) {
			return new ComboLifeCycleState(meem.getProxy().getLifeCycle().getState());
		}
		else
		if(id.equals(LifeCycleLimitProxy.ID_LIFE_CYCLE_STATE_LIMIT)) {
			return new ComboLifeCycleState(meem.getProxy().getLifeCycleLimit().getLifeCycleStateLimit());
		}
		else
		if(id.equals(ConfigurationHandlerProxy.ID_CONFIGURATION)) {
			return new ConfigurationPropertySource(meem.getProxy().getConfigurationHandler());
		}
		else
		if(id.equals(ConfigurationHandlerProxy.ID_LAST_REJECTED_REASON)) {
			return meem.getProxy().getConfigurationHandler().getLastRejectedReason();
		}
		else
		if(id.equals(ErrorHandlerProxy.ID_THROWABLE))
			return meem.getProxy().getErrorHandler().getLastThrown();
		else
		if(id.equals(MetaMeemProxy.ID_MEEM_ATTRIBUTE))
			return new MeemAttributePropertySource(meem.getProxy().getMetaMeem().getStructure().getMeemAttribute());
		else
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#resetPropertyValue(java.lang.Object)
	 */
	public void resetPropertyValue(Object id) {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#setPropertyValue(java.lang.Object, java.lang.Object)
	 */
	public void setPropertyValue(Object id, Object value) {
		if(id.equals(Element.ID_NAME)) {
			if(meem.getParent() instanceof Diagram) {
				Diagram diagram = (Diagram) meem.getParent();
				CategoryEntryRenameCommand command = new CategoryEntryRenameCommand(
						diagram.getCategory(), 
						meem.getProxy().getMeemPath(), 
						meem.getName(), 
						(String) value
					);
				command.execute();																					
			}
		}
		else
		if(id.equals(Meem.ID_SIZE)) {
			DimensionPropertySource dimPS = (DimensionPropertySource) value;
			meem.setSize(new Dimension(dimPS.getValue()));
		}
		else
		if(id.equals(Meem.ID_LOCATION)) {
			LocationPropertySource locPS = (LocationPropertySource) value;
			meem.setLocation(new Point(locPS.getValue()));
		}
		else
		if(id.equals(Meem.ID_VIEW_MODE)) {
			meem.setViewMode((ViewMode)value);
		}
		else
		if(id.equals(LifeCycleProxy.ID_LIFE_CYCLE_STATE)) {
			ComboLifeCycleState comboState = (ComboLifeCycleState)value;
			if(!comboState.getState().equals(meem.getProxy().getLifeCycle().getState())) {
				meem.getProxy().getLifeCycle().changeLifeCycleState(comboState.getState());
			}
		}
		else
		if(id.equals(LifeCycleLimitProxy.ID_LIFE_CYCLE_STATE_LIMIT)) {
			ComboLifeCycleState comboState = (ComboLifeCycleState)value;
			if(!comboState.getState().equals(meem.getProxy().getLifeCycleLimit().getLifeCycleStateLimit())) {
				meem.getProxy().getLifeCycleLimit().limitLifeCycleState(comboState.getState());
			}
		}
		else
		if(id.equals(MetaMeemProxy.ID_MEEM_ATTRIBUTE)) {
			MeemAttributePropertySource attrPS = (MeemAttributePropertySource) value;
			meem.getProxy().getMetaMeem().updateMeemAttribute(attrPS.getMeemAttribute());
		}
	}
	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#getEditableValue()
	 */
	public Object getEditableValue() {
		return this;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyDescriptors()
	 */
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return descriptors;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#isPropertySet(java.lang.Object)
	 */
	public boolean isPropertySet(Object id) {
		return false;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "";
	}
}

class ComboLifeCycleState {
	private LifeCycleState state;

	public ComboLifeCycleState(LifeCycleState state) {
		this.state = state;
	}
	public  LifeCycleState getState() {
		return state;
	}
		
	public String toString() {
		return state.getCurrentState();
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if(obj == null) return false;
		if(!(obj instanceof ComboLifeCycleState)) return false;
		ComboLifeCycleState that = (ComboLifeCycleState)obj;
		return getState().equals(that.getState());
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return state.hashCode();
	}
}
