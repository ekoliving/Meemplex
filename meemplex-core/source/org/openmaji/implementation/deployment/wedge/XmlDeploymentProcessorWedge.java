/*
 * Copyright 2005 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.deployment.wedge;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.openmaji.diagnostic.Debug;
import org.openmaji.implementation.deployment.ConfigurationParameter;
import org.openmaji.implementation.deployment.DependencyDescriptor;
import org.openmaji.implementation.deployment.Descriptor;
import org.openmaji.implementation.deployment.MeemDeploymentDescriptor;
import org.openmaji.implementation.deployment.TagRegistrator;
import org.openmaji.implementation.server.utility.ObjectUtility;
import org.openmaji.meem.Meem;
import org.openmaji.meem.MeemPath;
import org.openmaji.meem.Wedge;
import org.openmaji.meem.definition.DependencyAttribute;
import org.openmaji.meem.definition.DependencyType;
import org.openmaji.meem.definition.LifeTime;
import org.openmaji.meem.definition.MeemDefinition;
import org.openmaji.meem.definition.MeemDefinitionFactory;
import org.openmaji.meem.definition.MeemDefinitionProvider;
import org.openmaji.meem.definition.Scope;
import org.openmaji.meem.definition.WedgeDefinition;
import org.openmaji.meem.definition.WedgeDefinitionFactory;
import org.openmaji.meem.definition.WedgeDefinitionUtility;
import org.openmaji.meem.filter.Filter;
import org.openmaji.meem.space.Space;
import org.openmaji.meem.wedge.configuration.ConfigurationIdentifier;
import org.openmaji.meem.wedge.lifecycle.LifeCycle;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClient;
import org.openmaji.meem.wedge.lifecycle.LifeCycleState;
import org.openmaji.meem.wedge.lifecycle.LifeCycleTransition;
import org.openmaji.system.space.Category;
import org.openmaji.system.space.CategoryEntry;
import org.openmaji.system.utility.CategoryUtility;
import org.openmaji.system.utility.MeemUtility;

import org.jdom.Element;
import org.swzoo.log2.core.LogFactory;
import org.swzoo.log2.core.LogTools;
import org.swzoo.log2.core.Logger;

/**
 * This wedge processes a DOM representing an XML deployment configuation file. It processes the DOM and constructs a list of deployment descriptors which it passes onto other
 * wedges via the deploymentProcessorConduit. If any &lt;category&gt; tags are found it will process those tags first before passing any descriptors onto the conduit.
 * 
 * @author Warren Bloomer
 */
public class XmlDeploymentProcessorWedge implements Wedge, DomProcessor {

	private static final Logger logger = LogFactory.getLogger();

	/* ------------------ conduits --------------------- */

	/**
	 * conduit on which to receive a DOM Element
	 */
	public DomProcessor domProcessorConduit = this;

	/**
	 * Conduit on which to send deploymentDescriptors and process command.
	 */
	public DeploymentProcessor deploymentProcessorConduit;

	/**
	 * New tags are registered on this conduit.
	 */
	public TagRegistrator tagRegistratorConduit = new TagRegistratorConduit();

	/**
	 * To receive lifecycle state changes of this meem.
	 */
	public LifeCycleClient lifeCycleClientConduit = new LifeCycleClientHandler();

	/**
	 * conduit from which to receive the debug level.
	 */
	public Debug debugConduit = new MyDebugConduit();

	/* ----------------- private properties ---------------- */

	private final HashSet<String> createdCategories = new HashSet<String>();

	private final Map<String, Class<? extends Descriptor>> tags = new HashMap<String, Class<? extends Descriptor>>();

	private Meem hyperspaceMeem;

	private MeemDefinitionFactory meemDefinitionFactory;

	private WedgeDefinitionFactory wedgeDefinitionFactory;

	private int debugLevel = 5;

	/**
	 * Constructor.
	 */
	public XmlDeploymentProcessorWedge() {
		meemDefinitionFactory = MeemDefinitionFactory.spi.create();
		wedgeDefinitionFactory = WedgeDefinitionFactory.spi.create();
	}

	/* ---------------------------------------------------------------------- */

	private void commence() {
	}

	private void conclude() {
		createdCategories.clear();
		hyperspaceMeem = null;
	}

	/* ---------------------------------------------------------------------- */

	/**
	 * Process the DOM
	 */
	public void process(Element rootElement) {
		hyperspaceMeem = Meem.spi.get(MeemPath.spi.create(Space.HYPERSPACE, "/"));
		Collection<Descriptor> deploymentDescriptors = processDOM(rootElement);
		deploymentProcessorConduit.setDescriptors(deploymentDescriptors);
		deploymentProcessorConduit.processDescriptors();
	}

	/* ---------------------------------------------------------------------- */

	private Collection<Descriptor> processDOM(Element rootElement) {
		List<Descriptor> descriptors = new ArrayList<Descriptor>();

		// process all <category> tags by creating hyperspace entries before proceeding
		List<Element> categoryElements = rootElement.getChildren("category");
		for (Element element : categoryElements) {
			processCategoryElement(element);
		}

		// loop through the list of registered tags and process the corresponding
		// nodes in the DOM
		for (String tag : tags.keySet()) {
			Class<? extends Descriptor> theclass = tags.get(tag);
			processTags(tag, rootElement, theclass, descriptors);
		}

		// process all <meem> tags and construct descriptors for each one
		List<?> meemElements = rootElement.getChildren("meem");
		for (Iterator<?> eilter = meemElements.iterator(); eilter.hasNext();) {
			Element meemElement = (Element) eilter.next();
			MeemDeploymentDescriptor descriptor = loadDescriptor(meemElement);
			if (descriptor != null) {
				descriptors.add(descriptor);
			}
		}

		return descriptors;
	}

	/**
	 * 
	 * @param tag
	 *            the XML element name
	 * @param rootElement
	 *            The root element of the XML document
	 * @param theclass
	 *            A class that implements Descriptor
	 * @param descriptors
	 *            a collection of descriptors
	 */
	private void processTags(String tag, Element rootElement, Class<? extends Descriptor> theclass, Collection<Descriptor> descriptors) {

		List<Element> elements = rootElement.getChildren(tag);
		for (Element element : elements) {
			Descriptor descriptor = null;
			try {
				descriptor = theclass.newInstance();
			}
			catch (Exception ex) {
				LogTools.error(logger, "Unable to instantiate " + theclass.getName() + " for tag " + tag + ": " + ex.getMessage());
				return;
			}
			descriptor.processElement(element);
			descriptors.add(descriptor);
		}
	}

	private void processCategoryElement(Element element) {

		String path = element.getAttributeValue("path");
		boolean removeEntries = false;
		boolean destroyEntries = false;
		String tmp = element.getAttributeValue("existing-entries");

		if (tmp != null) {
			if ("remove".equalsIgnoreCase(tmp)) {
				removeEntries = true;
			}
			if ("destroy".equalsIgnoreCase(tmp)) {
				destroyEntries = true;
			}
		}

		MeemPath categoryPath = MeemPath.spi.create(Space.HYPERSPACE, path);
		Category category = CategoryUtility.spi.get().getCategory(hyperspaceMeem, categoryPath);

		if (destroyEntries || removeEntries) {
			Meem categoryMeem = Meem.spi.get(categoryPath);
			Map<String, CategoryEntry> entries = CategoryUtility.spi.get().getCategoryEntries(categoryMeem);
			for (String name : entries.keySet()) {
				category.removeEntry(name);
				if (destroyEntries) {
					CategoryEntry entry = entries.get(name);
					Meem meem = entry.getMeem();
					LifeCycle lifeCycle = (LifeCycle) MeemUtility.spi.get().getTarget(meem, "lifeCycle", LifeCycle.class);
					lifeCycle.changeLifeCycleState(LifeCycleState.ABSENT);
				}
			}
		}
	}

	/**
	 * Load a subsystem descriptor from a JDOM element.
	 * 
	 * @param subsystemElement
	 *            A JDOM element describing the descriptor.
	 * @return the loaded SubsystemDescriptor.
	 */
	// private SubsystemDescriptor loadSubsystemDescriptor(Element subsystemElement) {
	// String name = subsystemElement.getAttributeValue("name");
	// SubsystemDescriptor descriptor = new SubsystemDescriptor(name);
	//
	// List meemElements = subsystemElement.getChildren("meem");
	// for (Iterator eilter = meemElements.iterator(); eilter.hasNext();) {
	// Element meemElement = (Element) eilter.next();
	// descriptor.addMeemId(meemElement.getAttributeValue("id"));
	// }
	//
	// return descriptor;
	// }

	/**
	 * Load descriptor for a particular meem.
	 * 
	 * @param meemElement
	 *            The JDOM element that describes the Meem deployment.
	 * @return A MeemDeploymentDescriptor.
	 */
	private MeemDeploymentDescriptor loadDescriptor(Element meemElement) {
		MeemDeploymentDescriptor descriptor = null;

		String id = meemElement.getAttributeValue("id");
		MeemDefinition meemDefinition = getMeemDefinition(id, meemElement.getChild("definition"));

		if (meemDefinition != null) {
			Collection<ConfigurationParameter> configProperties = getConfiguration(meemElement.getChild("configuration"));
			Collection<DependencyDescriptor> dependencies = getDependencies(meemElement.getChild("dependencies"));
			Collection<MeemPath> paths = getPaths(meemElement.getChild("paths"));

			descriptor = new MeemDeploymentDescriptor(id, meemDefinition, configProperties, dependencies, paths);
		}

		return descriptor;
	}

	/**
	 * Create a MeemDefinition from XML description
	 * 
	 * @param id
	 * @param definitionElement
	 */
	private MeemDefinition getMeemDefinition(String id, Element definitionElement) {

		if (debugLevel > 0) {
			LogTools.info(logger, "creating meem definition for " + id);
		}

		MeemDefinition meemDefinition = meemDefinitionFactory.createMeemDefinition(new Class[0]);

		if (definitionElement == null) {
			return meemDefinition;
		}

		try {

			meemDefinition.getMeemAttribute().setIdentifier(id);

			// handle meem definition providers
			List providers = definitionElement.getChildren("meem");

			for (int i = 0; i < providers.size(); i++) {
				Element providerElement = (Element) providers.get(i);
				String providerClassName = providerElement.getAttributeValue("class");
				try {
					Class<MeemDefinitionProvider> providerClass = ObjectUtility.getClass(MeemDefinitionProvider.class, providerClassName);
					MeemDefinitionProvider provider = providerClass.newInstance();
					for (WedgeDefinition wedgeDefinition : provider.getMeemDefinition().getWedgeDefinitions()) {
						meemDefinition.addWedgeDefinition(wedgeDefinition);
					}
				}
				catch (ClassNotFoundException e) {
					LogTools.info(logger, "Could not find class", e);
				}
			}

			// handle wedge definitions
			List<?> wedges = definitionElement.getChildren("wedge");

			for (int i = 0; i < wedges.size(); i++) {
				Element wedgeElement = (Element) wedges.get(i);

				String wedgeClassName = wedgeElement.getAttributeValue("class");
				String wedgeId = wedgeElement.getAttributeValue("id");

				try {
					Class<Wedge> wedgeClass = ObjectUtility.getClass(Wedge.class, wedgeClassName);
					WedgeDefinition wedgeDefinition = wedgeDefinitionFactory.createWedgeDefinition(wedgeClass);
					wedgeDefinition.getWedgeAttribute().setIdentifier(wedgeId);
	
					List<?> facets = wedgeElement.getChildren("facet");
					for (int j = 0; j < facets.size(); j++) {
						Element facetElement = (Element) facets.get(j);
						String newId = facetElement.getAttributeValue("id");
						String oldId = facetElement.getAttributeValue("original-id");
	
						// facet renaming
						WedgeDefinitionUtility.renameFacetIdentifier(wedgeDefinition, oldId, newId);
					}
	
					meemDefinition.addWedgeDefinition(wedgeDefinition);
				}
				catch (ClassNotFoundException e) {
					LogTools.info(logger, "Could not find class", e);
				}
			}
		}
		catch (IllegalArgumentException ex) {
			LogTools.error(logger, "Class not found", ex);
			return null;
		}
		catch (IllegalAccessException ex) {
			LogTools.error(logger, "No access to create instance", ex);
			return null;
		}
		catch (InstantiationException ex) {
			LogTools.error(logger, "Could not instatiate class", ex);
			return null;
		}

		return meemDefinition;
	}

	/**
	 * 
	 * @param configurationElement
	 *            a JDOM element
	 * @return A collection of configuration parameters.
	 */
	private Collection<ConfigurationParameter> getConfiguration(Element configurationElement) {
		Vector<ConfigurationParameter> configParams = new Vector<ConfigurationParameter>();

		if (configurationElement == null) {
			return configParams;
		}

		List wedgeElements = configurationElement.getChildren("wedge");
		for (int i = 0; i < wedgeElements.size(); i++) {
			Element wedgeElement = (Element) wedgeElements.get(i);
			String wedgeId = wedgeElement.getAttributeValue("id");
			List propertyElements = wedgeElement.getChildren("property");

			for (int j = 0; j < propertyElements.size(); j++) {
				Element propertyElement = (Element) propertyElements.get(j);
				String name = propertyElement.getAttributeValue("name");
				String type = propertyElement.getAttributeValue("type");
				String valueString = propertyElement.getTextTrim();

				ConfigurationIdentifier configurationIdentifier = new ConfigurationIdentifier(wedgeId, name);
				ConfigurationParameter configurationParameter = new ConfigurationParameter(configurationIdentifier, type, valueString);

				configParams.add(configurationParameter);
			}
		}

		return configParams;
	}

	/**
	 * 
	 * @param dependenciesElement
	 *            a JDOM element.
	 * @return a collection of DependencyDescriptors.
	 */
	private Collection<DependencyDescriptor> getDependencies(Element dependenciesElement) {
		Vector<DependencyDescriptor> dependencies = new Vector<DependencyDescriptor>();

		if (dependenciesElement == null) {
			return dependencies;
		}

		List dependencyElements = dependenciesElement.getChildren("dependency");
		for (int i = 0; i < dependencyElements.size(); i++) {
			Element dependencyElement = (Element) dependencyElements.get(i);

			String localFacetId = dependencyElement.getAttributeValue("facet-id");
			String typeString = dependencyElement.getAttributeValue("type");
			String scopeString = dependencyElement.getAttributeValue("scope");
			String lifetimeString = dependencyElement.getAttributeValue("lifetime");
			String contentReqString = dependencyElement.getAttributeValue("content-required");

			DependencyType dependencyType = getDependencyType(typeString);
			Scope scope = getScope(scopeString);
			LifeTime lifeTime = getLifetime(lifetimeString);
			boolean contentRequired = getContentRequired(contentReqString);
			Filter filter = null; // TODO allow filters

			List otherMeemElements = dependencyElement.getChildren("other-meem");
			for (int j = 0; j < otherMeemElements.size(); j++) {
				try {
					Element otherMeemElement = (Element) otherMeemElements.get(j);
					String remoteFacetId = otherMeemElement.getAttributeValue("facet-id");

					String pathString = otherMeemElement.getAttributeValue("path");
					MeemPath meemPath = parseMeemPath(pathString);

					if (debugLevel > 0) {
						LogTools.info(logger, "adding dependency: " + localFacetId + "->" + meemPath + "/" + remoteFacetId + ". " + lifeTime);
					}

					DependencyAttribute dependencyAttribute = new DependencyAttribute(dependencyType, scope, meemPath, remoteFacetId, filter, contentRequired);
					DependencyDescriptor descriptor = new DependencyDescriptor(localFacetId, dependencyAttribute, lifeTime);

					// add dependency descriptor to collection of dependencies
					dependencies.add(descriptor);
				}
				catch (URISyntaxException ex) {
					LogTools.error(logger, "malformed uri", ex);
				}
			}
		}

		return dependencies;
	}

	/**
	 * 
	 * @param pathsElement
	 *            a JDOM element
	 * @return a collection of MeemPaths representing hyperspace paths in which to place the Meem.
	 */
	private Collection<MeemPath> getPaths(Element pathsElement) {
		Vector<MeemPath> paths = new Vector<MeemPath>();

		if (pathsElement == null) {
			return paths;
		}

		List pathElements = pathsElement.getChildren("path");

		for (int i = 0; i < pathElements.size(); i++) {
			Element pathElement = (Element) pathElements.get(i);
			String location = pathElement.getTextTrim();
			int index = location.lastIndexOf("/");
			String categoryName = location.substring(0, index);
			if (!createdCategories.contains(categoryName)) {
				MeemPath categoryPath = MeemPath.spi.create(Space.HYPERSPACE, categoryName);
				CategoryUtility.spi.get().getCategory(hyperspaceMeem, categoryPath); // Create the category
				if (debugLevel > 0) {
					LogTools.info(logger, "path: " + categoryPath);
				}
				createdCategories.add(categoryName);
			}

			MeemPath meemPath = MeemPath.spi.create(Space.HYPERSPACE, location);
			paths.add(meemPath);
		}

		return paths;
	}

	/**
	 * 
	 * @param str
	 * @return LifeTime
	 */
	private LifeTime getLifetime(String str) {

		if ("TRANSIENT".equalsIgnoreCase(str)) {
			return LifeTime.TRANSIENT;
		}
		else if ("PERMANENT".equalsIgnoreCase(str)) {
			return LifeTime.PERMANENT;
		}
		else {
			return LifeTime.PERMANENT;
		}
	}

	/**
	 * 
	 * @param str
	 * @return Scope.
	 */
	private static Scope getScope(String str) {

		if ("DISTRIBUTED".equalsIgnoreCase(str)) {
			return Scope.DISTRIBUTED;
		}
		else if ("FEDERATED".equalsIgnoreCase(str)) {
			return Scope.FEDERATED;
		}
		else if ("LOCAL".equalsIgnoreCase(str)) {
			return Scope.LOCAL;
		}
		else if ("MEEMPLEX".equalsIgnoreCase(str)) {
			return Scope.MEEMPLEX;
		}
		else {
			return Scope.DISTRIBUTED;
		}
	}

	private static DependencyType getDependencyType(String str) {

		if ("STRONG".equalsIgnoreCase(str)) {
			return DependencyType.STRONG;
		}
		else if ("STRONG-MANY".equalsIgnoreCase(str)) {
			return DependencyType.STRONG_MANY;
		}
		else if ("STRONGMANY".equalsIgnoreCase(str)) {
			return DependencyType.STRONG_MANY;
		}
		else if ("STRONG_MANY".equalsIgnoreCase(str)) {
			return DependencyType.STRONG_MANY;
		}
		else if ("WEAK".equalsIgnoreCase(str)) {
			return DependencyType.WEAK;
		}
		else if ("WEAK-MANY".equalsIgnoreCase(str)) {
			return DependencyType.WEAK_MANY;
		}
		else if ("WEAKMANY".equalsIgnoreCase(str)) {
			return DependencyType.WEAK_MANY;
		}
		else if ("WEAK_MANY".equalsIgnoreCase(str)) {
			return DependencyType.WEAK_MANY;
		}
		else {
			return DependencyType.STRONG;
		}
	}

	private static boolean getContentRequired(String str) {
		boolean contentRequired = !("false".equalsIgnoreCase(str) || "0".equals(str) || "no".equalsIgnoreCase(str));
		return contentRequired;
	}

	private static MeemPath parseMeemPath(String pathString) throws URISyntaxException {
		URI uri = new URI(pathString);
		String scheme = uri.getScheme();
		String path = uri.getPath();

		Space space = null;

		if ("meemstore".equalsIgnoreCase(scheme)) {
			space = Space.MEEMSTORE;
		}
		else if ("transient".equalsIgnoreCase(scheme)) {
			space = Space.TRANSIENT;
		}
		else if ("hyperspace".equalsIgnoreCase(scheme)) {
			space = Space.HYPERSPACE;
		}
		else {
			space = Space.HYPERSPACE;
		}

		return MeemPath.spi.create(space, path);
	}

	/* ---------------------------------------------------------------------- */

	private class LifeCycleClientHandler implements LifeCycleClient {

		public void lifeCycleStateChanged(LifeCycleTransition transition) {
			if (transition.equals(LifeCycleTransition.PENDING_READY))
				commence();
		}

		public void lifeCycleStateChanging(LifeCycleTransition transition) {
			if (transition.equals(LifeCycleTransition.READY_PENDING))
				conclude();
		}
	}

	/* ---------------------------------------------------------------------- */

	private class TagRegistratorConduit implements TagRegistrator {

		public void register(String tag, Class descriptorClass) {
			tags.put(tag, descriptorClass);
		}
	}

	private class MyDebugConduit implements Debug {

		public void debugLevelChanged(int level) {
			debugLevel = level;
		}
	}
}
