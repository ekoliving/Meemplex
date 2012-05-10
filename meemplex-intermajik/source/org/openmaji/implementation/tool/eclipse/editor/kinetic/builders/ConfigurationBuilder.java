/*
 * @(#)ConfigurationBuilder.java
 * Created on 22/10/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic.builders;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.openmaji.common.VariableMapClient;
import org.openmaji.implementation.intermajik.model.ElementPath;
import org.openmaji.implementation.intermajik.model.ValueBag;
import org.openmaji.implementation.tool.eclipse.client.variables.IVariableSource;
import org.openmaji.implementation.tool.eclipse.client.variables.IVariableSourceFactory;
import org.openmaji.implementation.tool.eclipse.editor.common.model.ConnectionElement;
import org.openmaji.implementation.tool.eclipse.editor.common.model.Element;
import org.openmaji.implementation.tool.eclipse.editor.features.layout.LayoutManager;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.Configuration;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.Meem;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.variables.VariableSourceFactory;
import org.openmaji.meem.MeemPath;



/**
 * <code>ConfigurationBuilder</code> is the root builder in the Maji building 
 * framework. It extends the diagram builder by providing access to the 
 * centralised variable map which stores view model states. It also maintains
 * all resolved and unresolved connections in the configuration.
 * <p>
 * @author Kin Wong
 */
public class ConfigurationBuilder extends DiagramBuilder {
	private IVariableSourceFactory factory;
	private LayoutManager layoutManager;
	
	private Map<Object, ElementBuilder> keyToBuilderMap =  new HashMap<Object, ElementBuilder>();
	
	/**
	 * Map connection id to resolved connection
	 */
	private Map<Object, ConnectionElement> connections = new HashMap<Object, ConnectionElement>();	
	
	/**
	 * Map connection path to resolved connection
	 */
	private Map<ElementPath, ConnectionElement> pathConnections = new HashMap<ElementPath, ConnectionElement>();
	
	/**
	 * Map connection Id to target key
	 */
	private Map<Object, Object> connectionTargetMap = new HashMap<Object, Object>();
	
	/**
	 * Map connection Id to source key
	 */
	private Map<Object, Object> connectionSourceMap = new HashMap<Object, Object>(); 
	
	private ConnectionCache unresolvedConnections = new ConnectionCache();
	
	private ConnectionCache resolvedConnections = new ConnectionCache();
	
	//=== Internal VariableMapClient Implementation ==============================
	private VariableMapClient variableMapClient = new VariableMapClient() {
		/* (non-Javadoc)
		 * @see org.openmaji.common.VariableMapClient#changed(java.util.Map.Entry[])
		 */
		public void changed(Entry<Serializable, Serializable>[] entries) {
			for(int i=0; i < entries.length; i++)
			changed(entries[i].getKey(), entries[i].getValue());
		}
		
		public void changed(Serializable key, Serializable value) {
			if((key instanceof ElementPath) && (value instanceof ValueBag))
			updateVariable((ElementPath)key, (ValueBag)value);
		}
		public void removed(Serializable key) {
			if(key instanceof ElementPath)
			deleteVariable((ElementPath)key);
		}
	};
	
	/**
	 * Constructs an instance of <code>ConfigurationBuilder</code>.
	 * <p>
	 * @param factory The <code>IVariableSourceFactory</code> associates with 
	 * this configuration builder.
	 */
	public ConfigurationBuilder(IVariableSourceFactory factory) {
		super(null);
		this.root = this;
		this.factory = factory;
	}
	
	/**
	 * Sets the configuration associates with this configuration builder.
	 * <p>
	 * @param configuration The configuration associates with this configuration 
	 * builder.
	 */
	public void setConfiguration(Configuration configuration) {
		setModel(configuration);
	}
	
	public void setLayoutManager(LayoutManager layoutManager) {
		this.layoutManager = layoutManager;
	}
	
	/**
	 * Gets the configuration associates with this configuration builder.
	 * <p>
	 * @return Configuration The configuration associates with this configuration 
	 * builder.
	 */
	public Configuration getConfiguration() {
		return (Configuration)getModel();
	}
	
	/**
	 * Gets the <code>IVariableSourceFactory</code> associates with this diagram
	 * builder.
	 * <p>
	 * @return IVariableSourceFactory The <code>IVariableSourceFactory</code> 
	 * associates with this diagram builder.
	 */
	protected IVariableSourceFactory getVariableSourceFactory() {
		return factory;
	}

	/**
	 * Overridden to connect to variable map as a client.
	 * <p>
	 * @see org.openmaji.implementation.tool.eclipse.editor.kinetic.builders.DiagramBuilder#activate()
	 */
	public void activate() {
		super.activate();
		getVariableMap().addClient(variableMapClient);
	}

	/**
	 * Overridden to disconnect from variable map as a client.
	 * <p>
	 * @see org.openmaji.implementation.tool.eclipse.editor.kinetic.builders.DiagramBuilder#deactivate()
	 */
	public void deactivate() {
		getVariableMap().removeClient(variableMapClient);
		super.deactivate();
	}
	
	//=== Builder Related Methods ================================================
	/**
	 * Registers the element builder as connectable builder to this configuration 
	 * builder.
	 * <p>
	 * @param builder The element builder to be registered in this configuration 
	 * builder.
	 * @return true if the builder has been registered, false otherwise.
	 */
	protected boolean registerConnectableBuilder(ElementBuilder builder) {
		// Add connectable builder to centralised lookup
		Object connectableKey = builder.getConnectableKey();
		if(connectableKey  == null) return false;
		
		keyToBuilderMap.put(connectableKey, builder); 
		// If the element builder being registered is a target of unresolved 
		// connection, refreshes source of those connection now.
		Object[] sourceKeys = unresolvedConnections.getSourcesFromTarget(connectableKey);

		// If the builder is not a target of any unsolved connection, return it.
		if(sourceKeys.length == 0) return true;
		
		for(int i = 0; i < sourceKeys.length; i++) {
			ElementBuilder sourceBuilder = findConnectableBuilder(sourceKeys[i]);
			if(sourceBuilder != null) sourceBuilder.refreshConnections();
		}
		return true;
	}
	
	/**
	 * Deregisters the element builder as connectable builder from this 
	 * configuration builder.
	 * <p>
	 * @param builder The element builder to be deregistered from this 
	 * configuration builder.
	 */
	protected boolean deregisterConnectableBuilder(ElementBuilder builder) {
		// remove builder from centralised lookup
		Object connectableKey = builder.getConnectableKey();
		if(connectableKey == null) return false;
		keyToBuilderMap.remove(connectableKey);
		unresolvedConnections.remove(builder.getId());
		resolvedConnections.remove(builder.getId());
		return true;
				
		// If the element builder being deregistered is a target of resolved 
		// connection, refreshes source of those connection now.
//		Object[] sourceIds = resolvedConnections.getSourcesFromTarget(builder.getId());
		
		// If the builder is not a target of any unsolved connection, return it.
//		if(sourceIds.length == 0) return;
		
//		for(int i = 0; i < sourceIds.length; i++) {
//			ElementBuilder sourceBuilder = findBuilder(sourceIds[i]);
//			if(sourceBuilder != null) sourceBuilder.refreshConnections();
//		}
	}
	
	/**
	 * Finds the element builder by key.
	 * <p>
	 * @param key The key that identifies the element builder in the configuration.
	 * @return ElementBuilder The element builder identified by the id.
	 */
	protected ElementBuilder findConnectableBuilder(Object key) {
		return (ElementBuilder)keyToBuilderMap.get(key);
	}
	
	/**
	 * Finds the Meem identified by the meem path in which its builder has been 
	 * previously registered to this configuration builder.
	 * @param meemPath The meem path that identifies the meem.
	 * @return Meem The meem identified by the meem path.
	 */
	protected Meem findMeem(MeemPath meemPath) {
		ElementBuilder builder = findConnectableBuilder(meemPath);
		if(builder == null) return null;
		return ((builder.getModel() instanceof Meem)? 
			(Meem)builder.getModel(): null);
	}
	
	//=== Connection related methods =============================================
	/**
	 * Adds a new connection object to the configuration builder.
	 * <p>
	 * @param builder the builder that adds it.
	 * @param connection The connection to be added.
	 * @param targetKey The target connectable key.
	 */
	protected void addConnection(ElementBuilder builder, ConnectionElement connection, Object targetKey) {
		connections.put(connection.getId(), connection);
		pathConnections.put(connection.getPath(), connection);
		connectionSourceMap.put(connection.getId(), builder.getConnectableKey()); 
		connectionTargetMap.put(connection.getId(), targetKey);
	
		// removes the newly added connection from the unresolved connection list.
		unresolvedConnections.remove(builder.getConnectableKey(), targetKey, connection.getId());
		resolvedConnections.add(builder.getConnectableKey(), targetKey, connection.getId());
		//System.out.println("Connection added: " + connection);
	}
	
	/**
	 * Removes a connection object from the configuration builder.
	 * <p>
	 * @param connection The connection to be removed.
	 */
	protected void removeConnection(ConnectionElement connection) {
		Object connectionId = connection.getId();
		Object sourceKey = connectionSourceMap.get(connectionId);
		Object targetKey = connectionTargetMap.get(connectionId);

		connectionSourceMap.remove(connectionId);
		connectionTargetMap.remove(connectionId);
		
		pathConnections.remove(connection.getPath().getHead());
		connections.remove(connectionId);

		unresolvedConnections.add(sourceKey, targetKey, connectionId);
		resolvedConnections.remove(sourceKey, targetKey, connectionId);
		//System.out.println("Connection removed: " + connection);
	}
	
	/**
	 * Finds a connection object by id.
	 * <p>
	 * @param id the id that identifies the connection.
	 * @return ConnectionElement The connection identified by the id, or null
	 * if not found.
	 */
	protected ConnectionElement findConnection(Object id) {
		return (ConnectionElement)connections.get(id);
	}
	
	/**
	 * Registers a connection that can not be resolved.
	 * @param sourceKey The source key of the connection. 
	 * @param targetKey The target key of the connection.
	 * @param connectionId The connection id.
	 */	
	protected void registerUnresolvedConnection(Object sourceKey, Object targetKey, Object connectionId) {
		// Add it to unresolved connection cache, note the target and source Id
		// is reversed in this case.
		//System.out.println("Adding Unresolved Connection: " + connectionId);
		unresolvedConnections.add(sourceKey, targetKey, connectionId);
	}
	
	protected void registerUnresolvedConnection(ConnectionElement connection) {
		Object connectionId = connection.getId();
		Object sourceKey = connectionSourceMap.get(connectionId);
		Object targetKey = connectionSourceMap.get(connectionId);
		registerUnresolvedConnection(sourceKey, targetKey, connectionId);
	}
	
	//=== Variable Map related methods ===========================================
	/**
	 * Updates a variable in the associated variable map.
	 * <p>
	 * @param path The path of the element.
	 * @param bag The value of the variable as a value bag.
	 */
	private void updateVariable(ElementPath path, ValueBag bag) {
		Element element = null;
		if(path.isConnection()) {
			element = (ConnectionElement)pathConnections.get(path);
		}
		else {
			element = getConfiguration().parsePath(new ElementPath(path));
		}
		if(element == null) return;
		IVariableSource source = getVariableSourceFactory().createVariableSource(element);
		if(source == null) return;
		source.merge(bag);
	}
	
	/**
	 * Removes a variable from the associated variable map.
	 * <p>
	 * @param path The element path of the variable.
	 */
	protected void deleteVariable(ElementPath path) {
		getVariableMap().remove(path);
	}
	
	/**
	 * Adds a variable to the associated variable map.
	 * <p>
	 * @param element The Element which the variable is defined for.
	 */
	protected void addVariable(Element element) {
		ElementPath path = element.getPath();
		IVariableSource vs = VariableSourceFactory.
			getInstance().createVariableSource(element);
		//System.err.println("Adding Variable Path-> " + path);
		if(vs == null) return;
		getVariableMap().update(path, vs.extractAll());
	}
	
	/**
	 * Loads a viarble from the associated variable map.
	 * <p>
	 * @param element The element in which the variable represents is loaded.
	 */
	protected boolean loadVariable(Element element) {
		ElementPath path = element.getPath();
		Object value = getVariableMap().get(path);
		if((value == null) || (!(value instanceof ValueBag))) {
			//System.err.println("Undefined Variable Path-> " + path);
			return false; 
		} 
		updateVariable(path, (ValueBag)value);
		return true;
	}
	
	//=== Layout related Methods =================================================
	void invalidateLayout(Object model) {
		if(layoutManager == null) return;
		layoutManager.invalidateModel(model);
	}
}
