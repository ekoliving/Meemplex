/*
 * @(#)Meem.java
 * Created on 12/03/2003
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */
 
package org.openmaji.implementation.tool.eclipse.editor.kinetic.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.openmaji.implementation.intermajik.model.ViewMode;
import org.openmaji.implementation.intermajik.model.ViewModeConstants;
import org.openmaji.implementation.tool.eclipse.client.InterMajikClientProxyFactory;
import org.openmaji.implementation.tool.eclipse.client.MeemClientProxy;
import org.openmaji.implementation.tool.eclipse.editor.common.model.Collapsible;
import org.openmaji.implementation.tool.eclipse.editor.common.model.ElementContainer;
import org.openmaji.implementation.tool.eclipse.editor.features.connectable.IConnectable;
import org.openmaji.implementation.tool.eclipse.editor.features.connectable.IConnection;
import org.openmaji.implementation.tool.eclipse.editor.features.connectable.IConnectionContainer;
import org.openmaji.implementation.tool.eclipse.editor.features.multiview.IViewModeProvider;
import org.openmaji.implementation.tool.eclipse.editor.features.multiview.ViewModeDescriptor;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.Messages;
import org.openmaji.implementation.tool.eclipse.images.Images;
import org.openmaji.meem.MeemPath;
import org.openmaji.meem.wedge.lifecycle.LifeCycleState;



/**
 * <code>Meem</code> represents the view model of a meem in the configuration.
 * <p>
 * @author Kin Wong
 */
public class Meem extends Collapsible 
	implements IConnectable, IConnectionContainer, IViewModeProvider {

	private static final long serialVersionUID = 6424227717462161145L;

	static public final String ID_MEEM_PATH = "meem path";
	static public final String ID_VIEW_MODE = "view mode";
	static public final String ID_SHOWSYSTEMWEDGES = "show system wedges";
	static public final String ID_INNER_SOURCE_CONNECTIONS = "inner source connections";
	static public final String ID_INNER_TARGET_CONNECTIONS = "inner target connections";
	static public final String ID_ENTRY_TO_CATEGORY = "entry to category";
	
	static private final List<ViewModeDescriptor> viewModes = new ArrayList<ViewModeDescriptor>();
	
	static private final Map<ViewMode, ViewModeDescriptor> viewModeTable = new Hashtable<ViewMode, ViewModeDescriptor>();
	
	static {
		ViewModeDescriptor descriptor = new ViewModeDescriptor(ViewModeConstants.VIEW_MODE_DETAILED);
		descriptor.setLabel(Messages.ViewModeDetailed_Label);
		descriptor.setTooltip(Messages.ViewModeDetailed_Tooltip);
		descriptor.setImage(Images.ICON_DETAILED_VIEW);
		viewModes.add(descriptor);
		viewModeTable.put(descriptor.getViewMode(), descriptor);

		descriptor = new ViewModeDescriptor(ViewModeConstants.VIEW_MODE_ICONIC);
		descriptor.setLabel(Messages.ViewModeIconic_Label);
		descriptor.setTooltip(Messages.ViewModeIconic_Tooltip);
		descriptor.setImage(Images.ICON_ICONIC_VIEW);
		viewModes.add(descriptor);
		viewModeTable.put(descriptor.getViewMode(), descriptor);
	}

	protected transient MeemClientProxy proxy;
	
	private String entryName;
	private MeemPath meemPath;
	private Serializable id;
	private boolean showSystemWedges = false;
	protected ViewMode viewMode = ViewModeConstants.VIEW_MODE_ICONIC;
	
	/**
	 * Category Entries (Source connections)
	 */
	protected final Map<Object, EntryOf> entriesInCategories = new HashMap<Object, EntryOf>();

	/**
	 * Constructs an instance of <code>Meem</code>.
	 * <p>
	 * @param proxy The Meem proxy associates with this Meem.
	 */
	public Meem(MeemClientProxy proxy) {
		this.proxy = proxy;
		meemPath = this.proxy.getMeemPath();
		id = meemPath;
	}
	
	/**
	 * Overridden to return the id of this meem in its containing 
	 * diagram. The entry name identifies this meem uniquely in the diagram.
	 * @see org.openmaji.implementation.tool.eclipse.editor.common.model.Element#getId()
	 */
	public Serializable getId() {
		return id;
	}
	
	/**
	 * Gets the life cycle state of the meem.
	 * @return LifeCycleState The life cycle state of the meem.
	 */
	public LifeCycleState getLCS() {
		return getProxy().getLifeCycle().getState();
	}
	
	/**
	 * Returns whether the life cycle state of the underlying meem is LOADED.
	 * @return boolean true if the life cycle state is LOADED.
	 */
	public boolean isLCSLoaded() {
		return getLCS().equals(LifeCycleState.LOADED);
	}
	
	/**
	 * Returns whether the life cycle state of the underlying meem is READY.
	 * @return boolean true if the life cycle state is READY.
	 */
	public boolean isLCSReady() {
		return getLCS().equals(LifeCycleState.READY);
	}

	/**
	 * Gets the attribute key of this meem.
	 * @return Object A key that uniquely identifies the meem attributes of this
	 * meem.
	 */
	public Object getAttributeKey() {
		return getProxy().getMetaMeem().getStructure().getMeemAttribute().getKey();
	}
	
	/**
	 * Overridden to return the category entry name of this meem that uniquely 
	 * identifies it in its containing diagram.
	 * @see org.openmaji.implementation.tool.eclipse.editor.common.model.Element#getName()
	 */
	public String getName() {
		return entryName;
	}
	
	/**
	 * Gets the meem path of the underlying meem.
	 * <p>
	 * @return MeemPath The meem path of the underlying meem.
	 */	
	public MeemPath getMeemPath() {
		return meemPath;
	}
	
	public void setName(String entryName) {
		if(entryName.equals(this.entryName)) return;	// Identical Name - do nothing
		
		String oldEntryName = this.entryName;
		this.entryName = entryName;
		firePropertyChange(ID_NAME, oldEntryName, entryName);
	}
	
	/**
	 * Returns whether system wedges are shown.
	 * <p>
	 * @return boolean true is system wedges should be shown, false otherwise.
	 */
	public boolean isSystemWedgeShown() {
		return showSystemWedges;
	}
	
	public void showSystemWedge(boolean show) {
		if(showSystemWedges == show) return;
		showSystemWedges = show;
		firePropertyChange(ID_SHOWSYSTEMWEDGES, null, null);
	}
	
	/**
	 * Overrides to return whether the child is an instance of Wedge.
	 * @see org.openmaji.implementation.tool.eclipse.editor.common.model.ElementContainer#isValidNewChild(java.lang.Object)
	 */
	public boolean isValidNewChild(Object child) {
		if(!super.isValidNewChild(child)) return false;
		return (child instanceof Wedge);
	}
	
	/**
	 * Gets the meem edit proxy of this meem.
	 * @return MeemClientProxy The Meem edit proxy of this meem.
	 */
	public MeemClientProxy getProxy() {
		return proxy;
	}
	
	public void innerSourceConnectionChange() {
		firePropertyChange(Meem.ID_INNER_SOURCE_CONNECTIONS, null, null);
	}
	
	public void innerTargetConnectionChange() {
		firePropertyChange(Meem.ID_INNER_TARGET_CONNECTIONS, null, null);
	}

	/**
	 * Overridden to instantiate transient data members.
	 * @param in The stream that contains the serialized element.
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream in)throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		try {
			proxy = InterMajikClientProxyFactory.getInstance().locate(meemPath);
			meemPath = proxy.getMeemPath();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets the diagram of where this meem exists.
	 * @return Diagram The diagram of where this meem exists.
	 */
	public Diagram getDiagram() {
		ElementContainer parent = getParent();
		if(parent == null) return null;
		if(parent instanceof Diagram) return (Diagram)parent;
		return null;
	}
	
	public Collection getEntriesToCategory() {
		return entriesInCategories.values();
	}

	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.features.connectable.IConnectable#connectOutput(org.openmaji.implementation.tool.eclipse.editor.features.connectable.IConnection)
	 */
	public void connectSource(IConnection connection) throws Exception {
		if(!(connection instanceof EntryOf))  {
			throw new Exception("Meem can only be used as the source of an EntryOf");
		}

		EntryOf entryOf = (EntryOf)connection;
		entriesInCategories.put(entryOf.getId(), entryOf);
		
		firePropertyChange(ID_ENTRY_TO_CATEGORY, null, entryOf);
	}

	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.features.connectable.IConnectable#disconnectOutput(org.openmaji.implementation.tool.eclipse.editor.features.connectable.IConnection)
	 */
	public void disconnectSource(IConnection connection) {
		if(!(connection instanceof EntryOf)) {
			return;
		}
		EntryOf entryOf = (EntryOf)connection;
		
		if(null != entriesInCategories.remove(entryOf.getId())) {
			firePropertyChange(ID_ENTRY_TO_CATEGORY, connection, null);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.features.connectable.IConnectable#connectInput(org.openmaji.implementation.tool.eclipse.editor.features.connectable.IConnection)
	 */
	public void connectTarget(IConnection connection) throws Exception {
		throw new Exception("Meem can only be used as the source of a EntryOf connection");
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.features.connectable.IConnectable#disconnectInput(org.openmaji.implementation.tool.eclipse.editor.features.connectable.IConnection)
	 */
	public void disconnectTarget(IConnection connection) {
	}
	
	/**
	 * Gets the current view mode.
	 * @return Object The current view mode of this object.
	 */
	public ViewMode getViewMode() {
		return viewMode;
	}
	
	/**
	 * Sets the view mode.
	 */
	public void setViewMode(ViewMode viewMode) {
		Assert.isNotNull(viewMode);
		
		if(this.viewMode.equals(viewMode)) {
			return; // Nothing to change
		}
		this.viewMode = viewMode;
		firePropertyChange(ID_VIEW_MODE, null, viewMode);
	}

	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.features.multiview.IViewModeProvider#getViewModes()
	 */
	public List<ViewModeDescriptor> getViewModes() {
		return viewModes;
	}

	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.features.multiview.IViewModeProvider#supportViewMode(java.lang.Object)
	 */
	public boolean supportViewMode(ViewMode viewMode) {
		return (viewModeTable.get(viewMode) != null);
	}

	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.features.connectable.IConnectionContainer#getSourceConnections()
	 */
	public List<EntryOf> getSourceConnections() {
		return new ArrayList<EntryOf>(entriesInCategories.values());
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.features.connectable.IConnectionContainer#getTargetConnections()
	 */
	public List getTargetConnections() {
		return Collections.EMPTY_LIST;
	}
}
