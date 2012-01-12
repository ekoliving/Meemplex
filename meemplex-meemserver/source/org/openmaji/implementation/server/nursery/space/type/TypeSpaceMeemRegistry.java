/*
 * @(#)TypeSpaceMeemRegistry.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.server.nursery.space.type;

import java.io.Serializable;
import java.util.*;


import org.openmaji.implementation.server.Common;
import org.openmaji.implementation.server.manager.registry.MeemRegistryWedge;
import org.openmaji.meem.Meem;
import org.openmaji.meem.MeemPath;
import org.openmaji.meem.definition.DependencyAttribute;
import org.openmaji.meem.definition.FacetAttribute;
import org.openmaji.meem.definition.MeemAttribute;
import org.openmaji.meem.definition.WedgeAttribute;
import org.openmaji.meem.filter.ExactMatchFilter;
import org.openmaji.meem.filter.Filter;
import org.openmaji.meem.filter.FilterChecker;
import org.openmaji.meem.filter.IllegalFilterException;
import org.openmaji.meem.wedge.reference.Reference;
import org.openmaji.system.meem.definition.MetaMeem;
import org.openmaji.system.meem.wedge.reference.ContentClient;
import org.openmaji.system.meem.wedge.reference.ContentProvider;
import org.openmaji.system.space.CategoryEntry;
import org.openmaji.system.space.type.TypeSpace;
import org.openmaji.system.space.type.TypeSpaceClient;
import org.openmaji.system.spi.MajiSystemProvider;
import org.openmaji.system.spi.SpecificationType;
import org.swzoo.log2.core.*;

/**
 * <p>
 * ...
 * </p>
 * @author  mg
 * @version 1.0
 */
public class TypeSpaceMeemRegistry extends MeemRegistryWedge implements TypeSpace, FilterChecker {

	private static final Logger logger = LogFactory.getLogger();

	private Vector<String> systemInterfaces = new Vector<String>();
	
	private HashMap<String, Set<MeemPath>> interfaces = new HashMap<String, Set<MeemPath>>();
	
	private HashMap<MeemPath, Set<String>> reverseInterfaces = new HashMap<MeemPath, Set<String>>();

	private Reference reference = null;
	
	private Meem meem = null;

	
    public TypeSpaceClient typeSpaceClient;
    public final ContentProvider typeSpaceClientProvider = new ContentProvider() {
        public synchronized void sendContent(Object target, Filter filter) throws IllegalArgumentException {
            TypeSpaceClient typeSpaceClient = (TypeSpaceClient) target;
    
            if (filter instanceof ExactMatchFilter) {
                ExactMatchFilter exactMatchFilter = (ExactMatchFilter) filter;
    
                if (exactMatchFilter.getTemplate() instanceof String) {
    
                    String interfaceName = (String) exactMatchFilter.getTemplate();
    
                    Set interfaceList = (Set) interfaces.get(interfaceName);
    
                    if (interfaceList != null) {
    
                        for (Iterator i = interfaceList.iterator(); i.hasNext();) {
                            MeemPath meemPath = (MeemPath) i.next();
    
                            typeSpaceClient.entriesAdded(new CategoryEntry[] {new CategoryEntry(meemPath.toString(), Meem.spi.get(meemPath))});
                            
                        }
                    }
                }
            }
        }
    };
    
	public TypeSpaceMeemRegistry() {
    
    MajiSystemProvider majiSystemProvider = MajiSystemProvider.systemProvider();

		// we don't want system facets showing up in here

    Collection<Class> specifications = new ArrayList<Class>();
    specifications.addAll(majiSystemProvider.getSpecifications(SpecificationType.SYSTEM_WEDGE));
    specifications.addAll(majiSystemProvider.getSpecifications(SpecificationType.SYSTEM_HOOK));
    
    Iterator systemWedgeSpecifications = specifications.iterator();

		while (systemWedgeSpecifications.hasNext()) {
      Class specification = (Class) systemWedgeSpecifications.next();

      String wedgeImplementationName =
       majiSystemProvider.getSpecificationEntry(specification).getImplementation().getName();

			systemInterfaces.add(wedgeImplementationName);
		}

		// most things will have org.openmaji.meem.Facet so we'll ignore it
		systemInterfaces.add("org.openmaji.meem.Facet");

		//everything will have the following as well
		// -mg- is there a better way to find out about these instead of having them hardcoded?
		systemInterfaces.add("org.openmaji.meem.Meem");
		systemInterfaces.add("org.openmaji.system.meem.core.MeemCore");

	}

	/**
	 */
	public void deregisterMeem(Meem meem) {
		super.deregisterMeem(meem);

		MeemPath meemPath = meem.getMeemPath();

		Set reverseInterfaceList = (Set) reverseInterfaces.get(meemPath);

		for (Iterator i = reverseInterfaceList.iterator(); i.hasNext();) {
			String interfaceName = (String) i.next();

			Set interfaceList = (Set) interfaces.get(interfaceName);

			interfaceList.remove(meemPath);
			
			typeSpaceClient.entriesRemoved(new CategoryEntry[] { new CategoryEntry(meemPath.toString(), Meem.spi.get(meemPath)) });

		}

		reverseInterfaces.remove(meemPath);
	}

	/**
	 */
	public void registerMeem(Meem meem) {
		super.registerMeem(meem);
		
		LogTools.trace(logger, Common.getLogLevelVerbose(), "registerMeem: " + meem.getMeemPath());

		reference = Reference.spi.create("metaMeemClient", new MetaMeemClient(meem.getMeemPath()), true, null);

		meem.addOutboundReference(reference, false);
	}

	/**
	 */
	public synchronized boolean invokeMethodCheck(Filter filter, String methodName, Object[] args) throws IllegalFilterException {

		if (!(filter instanceof ExactMatchFilter)) {
			throw new IllegalFilterException("Can't check filter: " + filter);
		}

		if (methodName.equals("entryAdded") || methodName.equals("entryRemoved"))
		{
			CategoryEntry categoryEntry = (CategoryEntry) args[0];
			if (categoryEntry != null) {
				ExactMatchFilter exactMatchFilter = (ExactMatchFilter) filter;
				return exactMatchFilter.equals(categoryEntry.getMeem().getMeemPath());
			}
		}

		return false;
	}
	
	
	private void addFacet(MeemPath meemPath, String interfaceName) {
		try {
			addInterface(meemPath, interfaceName);

			Class facetImpl = Class.forName(interfaceName);

			Class[] interfaces = facetImpl.getInterfaces();

			for (int i = 0; i < interfaces.length; i++)
				addInterface(meemPath, interfaces[i].getName());

		} catch (ClassNotFoundException e) {
			LogTools.error(logger, "Error getting facet interface", e);
		}
	}

	private void addInterface(MeemPath meemPath, String interfaceName) {
		if (systemInterfaces.contains(interfaceName))
			return;

		Set<MeemPath> interfaceList = interfaces.get(interfaceName);

		if (interfaceList == null) {
			interfaceList = new HashSet<MeemPath>();
			interfaces.put(interfaceName, interfaceList);
		}

		if (interfaceList.add(meemPath)) {
			// add to reverse lookup list

			Set<String> reverseInterfaceList = reverseInterfaces.get(meemPath);

			if (reverseInterfaceList == null) {
				reverseInterfaceList = new HashSet<String>();
				reverseInterfaces.put(meemPath, reverseInterfaceList);
			}

			reverseInterfaceList.add(interfaceName);
			
			typeSpaceClient.entriesAdded(new CategoryEntry[] {new CategoryEntry(meemPath.toString(), Meem.spi.get(meemPath))});
			// LogTools.trace(logger, Common.getLogLevelVerbose(), "interface added: " + interfaceName + " : " + meemPath);
		}
	}
	
	

	private final class MetaMeemClient implements MetaMeem, ContentClient {

		private MeemPath meemPath;

		public MetaMeemClient(MeemPath meemPath) {
			this.meemPath = meemPath;
		}

		/**
		 */
		public void addFacetAttribute(Serializable wedgeKey, FacetAttribute facetAttribute) {
			// -mg- should we only have inbound facets?
			//if (facetAttribute.isDirection(Direction.INBOUND))
				addFacet(meemPath, facetAttribute.getInterfaceName());
		}

		/**
		 */
		public void addDependencyAttribute(String facetKey, DependencyAttribute dependencyAttribute) {
			// don't care
		}

		/**
		 */
		public void addWedgeAttribute(WedgeAttribute wedgeAttribute) {
			// don't care
		}

		/**
		 */
		public void removeDependencyAttribute(Serializable dependencyKey) {
			// don't care
		}

		/**
		 */
		public void removeFacetAttribute(String facetKey) {
			// don't care
		}

		/**
		 */
		public void removeWedgeAttribute(Serializable wedgeKey) {
			// don't care
		}

		/**
		 */
		public void updateDependencyAttribute(DependencyAttribute dependencyAttribute) {
			// don't care
		}

		/**
		 */
		public void updateFacetAttribute(FacetAttribute facetAttribute) {
			// don't care
		}

		/**
		 */
		public void updateMeemAttribute(MeemAttribute meemAttribute) {
			// don't care
		}

		/**
		 */
		public void updateWedgeAttribute(WedgeAttribute wedgeAttribute) {
			// don't care
		}

		/**
		 */
		public void contentSent() {
			meem.removeOutboundReference(reference);
		}

		/**
		 */
		public void contentFailed(String reason) {
			meem.removeOutboundReference(reference);
		}
	}



}
