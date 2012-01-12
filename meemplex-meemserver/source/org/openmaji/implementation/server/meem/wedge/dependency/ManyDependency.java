/*
 * Created on 7/09/2005
 */
package org.openmaji.implementation.server.meem.wedge.dependency;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.openmaji.implementation.server.meem.core.MeemCoreImpl;
import org.openmaji.meem.Meem;
import org.openmaji.meem.definition.DependencyAttribute;
import org.openmaji.meem.definition.DependencyType;
import org.openmaji.meem.definition.LifeTime;
import org.openmaji.meem.definition.Scope;
import org.openmaji.meem.filter.Filter;
import org.openmaji.meem.wedge.reference.Reference;
import org.openmaji.system.meem.core.MeemCore;
import org.openmaji.system.meem.wedge.reference.ContentClient;
import org.openmaji.system.space.CategoryClient;
import org.openmaji.system.space.CategoryEntry;



/**
 * a class that handles 'many-dependencies'.  It listens on a Category to add and remove 
 * @author Warren Bloomer
 *
 */
public final class ManyDependency implements FacetConnectable, CategoryClient, ContentClient
{
	private final DependencyHandlerWedge dependencyHandlerWedge;
	private final MeemCore meemCore;
	private final Meem targetMeem;
	private final String sourceFacetIdentifier;
	private final String targetFacetIdentifier;
	private final boolean isSystem;
	private final DependencyType dependencyType;
	private final Scope dependencyScope;
	private final Filter dependencyFilter;
	private final Connectable connectable;
	private final LifeTime lifeTime;
	private final Map childDeps = new HashMap();

	private Reference categoryClientReference = null;
	private boolean connected = false;
	
	public ManyDependency(
			DependencyHandlerWedge dependencyHandlerWedge, 
			Meem meem, 
			DependencyDescriptor dependencyDescriptor,
			DependencyType dependencyType,
			boolean isSystem, 
			Connectable connectable
		)
	{
		DependencyAttribute dependencyAttribute = dependencyDescriptor.getDependencyAttribute();
		
		this.dependencyHandlerWedge = dependencyHandlerWedge;
		this.meemCore = dependencyHandlerWedge.meemCore;
		this.targetMeem = meem;

		this.sourceFacetIdentifier = dependencyDescriptor.getFacetIdentifier();
		this.targetFacetIdentifier = dependencyAttribute.getFacetIdentifier();
		this.dependencyType = dependencyType;	// the type for the individual dependencies (not the many)
		this.dependencyScope = dependencyAttribute.getScope();
		this.dependencyFilter = dependencyAttribute.getFilter();
		this.lifeTime = dependencyDescriptor.getLifeTime();
		
		this.isSystem = isSystem;
		this.connectable = connectable;
	}

	public synchronized void connect()
	{
		if (!connected) {
			categoryClientReference = Reference.spi.create(
					"categoryClient",
					meemCore.getLimitedTargetFor(this, CategoryClient.class), 
					true
				);
	
			targetMeem.addOutboundReference(categoryClientReference, false);
			connected = true;
		}		
	}

	public synchronized void disconnect()
	{
		if (connected) {
			((MeemCoreImpl) meemCore).revokeTargetProxy(categoryClientReference.getTarget(), this);
			targetMeem.removeOutboundReference(categoryClientReference);
	
			connectable.disconnect();
	
			Iterator iterator = childDeps.values().iterator();
			while (iterator.hasNext()) {
				DependencyAttribute childDep = (DependencyAttribute) iterator.next();
				dependencyHandlerWedge.doRemoveDependency(childDep, true);
			}
	
			childDeps.clear();
			connected = false;
		}
	}

	public Meem getMeem() {
		return targetMeem;
	}
	
	public boolean isSystemFacet() {
		return isSystem;
	}

	public String getLocalFacetId() {
		return sourceFacetIdentifier;
	}
	
	public LifeTime getLifeTime() {
		return lifeTime;
	}

	public void entriesAdded(CategoryEntry[] newEntries)
	{
		for (int i = 0; i < newEntries.length; i++)
		{
			Meem newMeem = newEntries[i].getMeem();

			DependencyAttribute childDep = new DependencyAttribute(
				dependencyType, dependencyScope, newMeem, targetFacetIdentifier, dependencyFilter, true);

			childDeps.put(newMeem, childDep);

			dependencyHandlerWedge.doAddDependency(sourceFacetIdentifier, childDep, LifeTime.TRANSIENT);			
		}
	}

	public void entriesRemoved(CategoryEntry[] removedEntries)
	{
		for (int i = 0; i < removedEntries.length; i++)
		{
			Meem removedMeem = removedEntries[i].getMeem();
			DependencyAttribute childDep = (DependencyAttribute) childDeps.remove(removedMeem);

			if (childDep != null)
			{
				dependencyHandlerWedge.doRemoveDependency(childDep, true);
			}
		}
	}

	public void entryRenamed(CategoryEntry oldEntry, CategoryEntry newEntry) 
	{	
	}

	public void contentSent()
	{
		connected = true;
		connectable.connect(/*targetMeem*/);
	}

	public void contentFailed(String reason) 
	{		
	}
}