/*
 * @(#)Facet.java
 * Created on 16/04/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.openmaji.implementation.intermajik.model.ViewMode;
import org.openmaji.implementation.intermajik.model.ViewModeConstants;
import org.openmaji.implementation.tool.eclipse.client.MeemClientProxy;
import org.openmaji.implementation.tool.eclipse.editor.common.model.BoundsObject;
import org.openmaji.implementation.tool.eclipse.editor.common.model.ElementContainer;
import org.openmaji.implementation.tool.eclipse.editor.features.connectable.IConnectable;
import org.openmaji.implementation.tool.eclipse.editor.features.connectable.IConnection;
import org.openmaji.implementation.tool.eclipse.editor.features.connectable.IConnectionContainer;
import org.openmaji.implementation.tool.eclipse.editor.features.multiview.IViewModeProvider;
import org.openmaji.implementation.tool.eclipse.editor.features.multiview.ViewModeDescriptor;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.Messages;
import org.openmaji.implementation.tool.eclipse.images.Images;
import org.openmaji.meem.definition.FacetAttribute;



/**
 * <code>Facet</code>.
 * <p>
 * @author Kin Wong
 */
public abstract class Facet extends BoundsObject
	implements 	IConnectable, IConnectionContainer, ViewModeConstants, IViewModeProvider {
	
	private static final long serialVersionUID = 6424227717462161145L;

	static public String ID_INPUT_DEPENDECY = "input dependency";
	static public String ID_OUTPUT_DEPENDECY = "output dependency";
	static public String ID_DEPENDENCY_KEY = "dependency key";
	static public String ID_VIEW_MODE = "view mode";
	static public String ID_ATTRIBUTE = "attribute";
	
	static private final List<ViewModeDescriptor> viewModes = new ArrayList<ViewModeDescriptor>();
	
	static private final Map<ViewMode, ViewModeDescriptor> viewModeTable = new Hashtable<ViewMode, ViewModeDescriptor>();
	
	static {
		ViewModeDescriptor descriptor = new ViewModeDescriptor(VIEW_MODE_DETAILED);
		descriptor.setLabel(Messages.ViewModeDetailed_Label);
		descriptor.setTooltip(Messages.ViewModeDetailed_Tooltip);
		descriptor.setImage(Images.ICON_DETAILED_VIEW);
		viewModes.add(descriptor);
		viewModeTable.put(descriptor.getViewMode(), descriptor);

		descriptor = new ViewModeDescriptor(VIEW_MODE_DEVICE);
		descriptor.setLabel(Messages.ViewModeDevice_Label);
		descriptor.setTooltip(Messages.ViewModeDevice_Tooltip);
		descriptor.setImage(Images.ICON_DEVICE_VIEW);
		viewModes.add(descriptor);
		viewModeTable.put(descriptor.getViewMode(), descriptor);
	}
	
	private FacetAttribute attribute;
	private Serializable dependencyKey;
	private ViewMode viewMode = VIEW_MODE_DETAILED;
	
	protected HashMap inDependencies;		// Many Input Dependencies
	
	protected HashMap outDependencies;	// Many Output Dependencies
	
	private MeemClientProxy proxy;
	
	/**
	 * Constructs an instance of <code>Facet</code>.<p>
	 * @param attribute The facet attribute of this facet.
	 */
	protected Facet(MeemClientProxy proxy, FacetAttribute attribute) {
		this.proxy = proxy;
		this.attribute = (FacetAttribute)attribute.clone();
	}
	
	public MeemClientProxy getProxy() {
		return proxy;
	}
	
	/**
	 * Gets the dependency key associates with this facet.<p>
	 */
	public Serializable getDependencyKey() {
		return dependencyKey;
	}
	
	public void setDependencyKey(Serializable key) {
		if(dependencyKey == key) return;
		if((dependencyKey != null) && (dependencyKey.equals(key))) return;
		Object oldKey = dependencyKey;
		dependencyKey = key;
		firePropertyChange(ID_DEPENDENCY_KEY, oldKey, dependencyKey);
	}
	/**
	 * Updates the attribute associates with this facet.
	 * <p>
	 * @param attribute
	 */
	public void updateAttribute(FacetAttribute attribute) {
		if(!this.attribute.getIdentifier().equals(attribute.getIdentifier()))	return;
		if(this.attribute.contentEquals(attribute)) return;
		this.attribute = (FacetAttribute)attribute.clone();
		firePropertyChange(ID_ATTRIBUTE, null, this.attribute);
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.common.model.Element#setParent(org.openmaji.implementation.tool.eclipse.editor.common.model.ElementContainer)
	 */
	protected void setParent(ElementContainer parent) {
		super.setParent(parent);
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.common.model.Element#getId()
	 */
	public Serializable getId() {
		return getAttributeIdentifier();
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.common.model.Element#getName()
	 */
	public String getName() {
		return getAttribute().getIdentifier();
	}

	/**
	 * Gets the facet attribute identifier of this facet.
	 * @return Object The facet attribute identifier of this facet.
	 */
	public String getAttributeIdentifier() {
		return attribute.getIdentifier();
	}
	
	/**
	 * Gets the facet attribute associates with this facet.
	 * @return FacetAttribute The facet attribute associates with this facet
	 */
	public FacetAttribute getAttribute() {
		return attribute;
	}
	
	/**
	 * Gets the wedge that contains this facet.
	 * @return Wedge The wedge that contains this facet.
	 */
	public Wedge getWedge() {
		return (Wedge)getParent();
	}
	/**
	 * Gets the meem that contains this facet.
	 * @return Meem The meem that contains this facet, null if it has not yet added to 
	 * a wedge or meem.
	 */
	public Meem getMeem() {
		Wedge wedge = getWedge();
		if(wedge == null) return null;
		return wedge.getMeem();
	}
	
	/**
	 * Returns whether this facet is an inbound facet.
	 * @return boolean true if this facet is an inbound facet, false otherwise.
	 */
	abstract public boolean isInbound();

	/**
	 * Returns whether this facet is an outbound facet.
	 * @return boolean true if this facet is an outbound facet, false otherwise.
	 */
	public boolean isOutbound() {
		return !isInbound();
	}
	
	public List getSourceConnections() {
		if(outDependencies == null) {
			return Collections.EMPTY_LIST;
		}
		return Collections.unmodifiableList(new Vector(outDependencies.values()));
	}
	
	public Map getSourceConnectionsMap() {
		if(outDependencies == null) {
			return Collections.EMPTY_MAP;
		}
		return Collections.unmodifiableMap(outDependencies);
	}
	
	public List getTargetConnections() {
		if(inDependencies == null) {
			return Collections.EMPTY_LIST;
		}
		return Collections.unmodifiableList(new Vector(inDependencies.values()));
	}

	public Map getTargetConnectionsMap() {
		if(inDependencies == null) {
			return Collections.EMPTY_MAP;
		}
		return Collections.unmodifiableMap(inDependencies);
	}
	
	
	public Dependency findTargetDependency(Object dependencyId) {
		if(inDependencies == null) {
			return null;
		}
		return (Dependency)inDependencies.get(dependencyId);
	}

	/*
	 *  (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.features.connectable.IConnectable#connectOutput(org.openmaji.implementation.tool.eclipse.editor.features.connectable.IConnection)
	 */
	public void connectSource(IConnection connection) throws Exception {
		if(!(connection instanceof Dependency)) { 
			throw new Exception("Must be dependency");
		}
		
		Dependency dependency = (Dependency)connection;
		if(outDependencies == null) {
			outDependencies = new HashMap();
		}
		outDependencies.put(dependency.getId(), dependency);
		firePropertyChange(ID_OUTPUT_DEPENDECY, null, null);
		if(getWedge() != null) {
			getWedge().innerSourceConnectionChange();
		}
	}

	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.common.model.ConnectableElement#disconnectOutput(org.openmaji.implementation.tool.eclipse.editor.common.model.ConnectionElement)
	 */
	public void disconnectSource(IConnection connection) {
		if(!(connection instanceof Dependency)) {
			//System.out.println("Must be dependency");
			return;
		} 
		
		Dependency dependency = (Dependency)connection;
		if(null != outDependencies.remove(dependency.getId())) {
			firePropertyChange(ID_OUTPUT_DEPENDECY, null, null);
			if(outDependencies.size() == 0) {
				outDependencies = null;
			}
			if(getWedge() != null) {
				getWedge().innerSourceConnectionChange();
			}
		}
	}
	
	/*
	 *  (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.features.connectable.IConnectable#connectInput(org.openmaji.implementation.tool.eclipse.editor.features.connectable.IConnection)
	 */
	public void connectTarget(IConnection connection) throws Exception {
		if(!(connection instanceof Dependency)) 
			throw new Exception("Must be dependency");
		
		Dependency dependency = (Dependency)connection;
		if(inDependencies == null) inDependencies = new HashMap();
		inDependencies.put(dependency.getId(), dependency);
		firePropertyChange(ID_INPUT_DEPENDECY, null, null);
		if(getWedge() != null) getWedge().innerTargetConnectionChange();
	}

	/*
	 * (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.features.connectable.IConnectable#disconnectInput(org.openmaji.implementation.tool.eclipse.editor.features.connectable.IConnection)
	 */
	public void disconnectTarget(IConnection connection) {
		if(!(connection instanceof Dependency)) {
			//System.out.println("Must be dependency");
			return;
		} 
		
		Dependency dependency = (Dependency)connection;
		if(null != inDependencies.remove(dependency.getId())) {
			firePropertyChange(ID_INPUT_DEPENDECY, connection, null);
			if(inDependencies.size() == 0) inDependencies = null;
			if(getWedge() != null) getWedge().innerTargetConnectionChange();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.features.multiview.IViewModeProvider#getViewMode()
	 */
	public ViewMode getViewMode() {
		return viewMode;
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.features.multiview.IViewModeProvider#setViewMode(java.lang.Object)
	 */
	public void setViewMode(ViewMode viewMode) {
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.features.multiview.IViewModeProvider#getViewModes()
	 */
	public List<ViewModeDescriptor> getViewModes() {
		return new ArrayList<ViewModeDescriptor>();
	}

	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.features.multiview.IViewModeProvider#supportViewMode(java.lang.Object)
	 */
	public boolean supportViewMode(ViewMode viewMode) {
		return false;
	}
}
