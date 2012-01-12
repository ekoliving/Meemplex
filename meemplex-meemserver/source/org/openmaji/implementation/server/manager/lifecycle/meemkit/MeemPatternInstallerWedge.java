/*
 * Copyright 2004 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.server.manager.lifecycle.meemkit;

import org.meemplex.meem.Conduit;
import org.openmaji.common.VariableMap;
import org.openmaji.implementation.common.VariableMapWedge;
import org.openmaji.implementation.server.nursery.pattern.MeemPatternWedge;
import org.openmaji.implementation.server.space.CategoryWedge;

import org.openmaji.meem.Meem;
import org.openmaji.meem.MeemPath;
import org.openmaji.meem.Wedge;
import org.openmaji.meem.definition.*;
import org.openmaji.meem.space.Space;
import org.openmaji.meem.wedge.configuration.ConfigurationHandler;
import org.openmaji.meem.wedge.configuration.ConfigurationIdentifier;
import org.openmaji.meem.wedge.error.ErrorHandler;
import org.openmaji.meem.wedge.lifecycle.LifeCycle;
import org.openmaji.meem.wedge.lifecycle.LifeCycleState;
import org.openmaji.server.helper.LifeCycleManagerHelper;
import org.openmaji.server.helper.ReferenceHelper;
import org.openmaji.server.presentation.PatternGroupWedge;
import org.openmaji.system.meemkit.core.MeemkitDescriptor;
import org.openmaji.system.meemkit.core.MeemkitEntryDescriptor;
import org.openmaji.system.meemkit.core.ToolkitCategoryEntry;
import org.openmaji.system.presentation.InterMajik;
import org.openmaji.system.presentation.MeemIconicPresentation;
import org.openmaji.system.presentation.ResourceExporter;
import org.openmaji.system.space.Category;
import org.openmaji.system.space.hyperspace.StandardHyperSpaceCategory;
import org.openmaji.system.utility.CategoryUtility;
import org.swzoo.log2.core.LogFactory;
import org.swzoo.log2.core.LogTools;
import org.swzoo.log2.core.Logger;

public class MeemPatternInstallerWedge implements Wedge, MeemPatternControl {
	private static final Logger logger = LogFactory.getLogger();

	private static final boolean DEBUG = false;

	@Conduit
	public MeemPatternControl meemPatternControlConduit = new MeemPatternControlConduit();;

	@Conduit
	public MeemPatternState meemPatternStateConduit;

	@Conduit(name="errorHandler")
	public ErrorHandler errorHandlerConduit;

	
	@Override
	public void installPatternMeems(MeemkitDescriptor descriptor) {
		meemPatternControlConduit.installPatternMeems(descriptor);
	}
	
	@Override
	public void uninstallPatternMeems(MeemkitDescriptor descriptor) {
		meemPatternControlConduit.uninstallPatternMeems(descriptor);
	}
	
	private void installPatterns(MeemkitDescriptor descriptor) {
		int numberPatternMeems = 0;
		String resourceClassName = descriptor.getHeader().getResourceClassName();

		if (descriptor.getMeemDescriptors() != null) {
			if (DEBUG) {
				System.err.println("MeemPatternInstallerWedge.installPatterns() - meem entries");
			}
			
			String path = StandardHyperSpaceCategory.MAJI_SYSTEM_PATTERN_MEEM;
			createToolkitCategories(path, descriptor.getMeemViewCategoryEntries(), resourceClassName);
			
			if (DEBUG) {
				System.err.println("MeemPatternInstallerWedge.installPatterns() - meem entries. categories done");
			}
			
			createToolkitEntries(path, descriptor.getMeemDescriptors(), resourceClassName);
			
			if (DEBUG) {
				System.err.println("MeemPatternInstallerWedge.installPatterns() - meem entries. entries done");
			}
			
			numberPatternMeems = numberPatternMeems + descriptor.getMeemDescriptors().length;
		}

		if (descriptor.getWedgeDescriptors() != null) {
			if (DEBUG) {
				System.err.println("MeemPatternInstallerWedge.installPatterns() - wedge entries");
			}
			String path = StandardHyperSpaceCategory.MAJI_SYSTEM_PATTERN_WEDGE;
			createToolkitCategories(path, descriptor.getWedgeViewCategoryEntries(), resourceClassName);
			createToolkitEntries(path, descriptor.getWedgeDescriptors(), resourceClassName);
			numberPatternMeems = numberPatternMeems + descriptor.getWedgeDescriptors().length;
		}

		LogTools.info(logger, "installPatterns() - name=[" + descriptor.getHeader().getName() + "] installed " + numberPatternMeems + " pattern meems into toolkit");
	}

	/**
	 * Create the category entries in the Toolkit.
	 * 
	 * @param toolkitPath
	 *            The path relative to the Toolkit's MeemView root or WedgeView root
	 * @param entries
	 *            The toolkit category entries to create
	 * @param resourceClassName
	 *            The name of the meemkit's resource class
	 */

	private void createToolkitCategories(String toolkitPath, ToolkitCategoryEntry[] entries, String resourceClassName) {
		if (entries != null) {
			for (int i = 0; i < entries.length; i++) {
				ToolkitCategoryEntry entry = entries[i];
				StringBuffer buffer = new StringBuffer(toolkitPath);
				buffer.append(entry.getPath());
				if (entry.getPath().equals("/") == false) {
					buffer.append("/");
				}
				buffer.append(entry.getName());
				createToolkitCategory(buffer.toString(), resourceClassName, entry);
			}
		}
	}

	/**
	 * Add a list of pattern Meems to the Toolkit given a path in the Toolkit.
	 * 
	 * @param kitPath
	 *            The path in the Toolkit to add the pattern Meems
	 * @param entries
	 *            The array of MeemkitEntryDescriptor from which to create the pattern Meems
	 */

	private void createToolkitEntries(String kitPath, MeemkitEntryDescriptor[] entries, String resourceClassName) {
		if (entries == null)
			return;
		for (int i = 0; i < entries.length; i++) {
			if (entries[i] == null) {
				LogTools.error(logger, "Unable to install all Meems / Wedges in this MeemKit.");
				LogTools.error(logger, "MeemKit descriptor has incomplete or poorly formed XML.");
				LogTools.error(logger, "Previous log messages may provide clues.");
				continue;
			}
			MeemkitEntryDescriptor entry = entries[i];
			if (entry.getWedgeClassNames() != null) {
				createToolkitEntryFromWedges(kitPath, entry, resourceClassName);
			}
			else {
				createToolkitEntry(kitPath, entry, resourceClassName);
			}
		}
	}

	/**
	 * Add a single pattern Meem to the Toolkit given a path in the Toolkit. The Meem is created from using a MeemDefinitionProvider.
	 * 
	 * @param kitPath
	 *            The path in the Toolkit to add the pattern Meem
	 * @param entry
	 *            The MeemkitEntryDescriptor from which to create the pattern Meem
	 */

	private void createToolkitEntry(String kitPath, MeemkitEntryDescriptor entry, String resourceClassName) {
		MeemDefinition md;

		String className = entry.getMeemDefinitionProviderClassName();
		try {
			md = MeemDefinitionFactory.spi.create().createMeemDefinition(Class.forName(className));
		}
		catch (Exception ex) {
			String message = "Unable to obtain MeemDefinition from '" + className + "': " + ex.getMessage();
			LogTools.error(logger, message);
			errorHandlerConduit.thrown(new Exception(message));
			return;
		}

		if (md == null) {
			LogTools.error(logger, "MeemDefinitionFactory unable to create meemdefinition for '" + entry.getName() + "'");
			return;
		}

		FacetDefinition facetDefinition1 = new FacetDefinition(new FacetInboundAttribute("variableMap", "org.openmaji.common.VariableMap", false));
		FacetDefinition facetDefinition2 = new FacetDefinition(new FacetOutboundAttribute("variableMapClient", "org.openmaji.common.VariableMapClient", "variableMapClient"));
		WedgeDefinition wedgeDefinition = new WedgeDefinition(new WedgeAttribute("org.openmaji.implementation.common.VariableMapWedge"));
		wedgeDefinition.addFacetDefinition(facetDefinition1);
		wedgeDefinition.addFacetDefinition(facetDefinition2);
		md.addWedgeDefinition(wedgeDefinition);

		FacetDefinition facetDefinition = new FacetDefinition(new FacetInboundAttribute("meemPattern", "org.openmaji.implementation.server.nursery.pattern.MeemPattern", false));
		wedgeDefinition = new WedgeDefinition(new WedgeAttribute("org.openmaji.implementation.server.nursery.pattern.MeemPatternWedge"));
		wedgeDefinition.addFacetDefinition(facetDefinition);
		md.addWedgeDefinition(wedgeDefinition);

		md.getMeemAttribute().setImmutableAttribute("meemlicensetype", entry.getMeemLicenseType());

		String path = kitPath + entry.getPath() + "/" + entry.getName();
		MeemPath meemPath = MeemPath.spi.create(Space.HYPERSPACE, path);
		Meem meem = null;
		Class<?> resourceClass = null;
		String icon = null;
		String largeIcon = null;

		try {
			meem = LifeCycleManagerHelper.createMeemAtPath(md, meemPath, LifeCycleState.LOADED);
		}
		catch (Exception ex) {
			String message = "Unable to create Meem for " + entry.getName() + ": " + ex.getMessage();
			errorHandlerConduit.thrown(new Exception(message));
			return;
		}

		if (meem == null) {
			String message = "Unable to create Meem for [" + entry.getName() + "]: null returned by LifeCycleManagerHelper";
			errorHandlerConduit.thrown(new Exception(message));
			return;
		}

		try {
			resourceClass = Class.forName(resourceClassName);
		}
		catch (ClassNotFoundException ex) {
			errorHandlerConduit.thrown(ex);
			return;
		}

		if (resourceClass != null) {
			ResourceExporter re = new ResourceExporter(resourceClass);
			MeemIconicPresentation icons = new MeemIconicPresentation();
			icon = entry.getIcon();
			if (icon != null && icon.length() > 0) {
				icons.setSmallIcon(re.extract(icon));
			}
			largeIcon = entry.getLargeIcon();
			if (largeIcon != null && largeIcon.length() > 0) {
				icons.setLargeIcon(re.extract(largeIcon));
			}
			VariableMap variableMap = (VariableMap) ReferenceHelper.getTarget(meem, "variableMap", VariableMap.class);
			variableMap.update(InterMajik.ICONIC_PRESENTATION_KEY, icons);
		}

		configureMeem(meem, entry);

		LogTools.trace(logger, 10, "createToolkitEntry() " + path);
	}

	/**
	 * Add a single pattern Meem to the Toolkit given a path in the Toolkit. The Meem is assembled from individual wedges.
	 * 
	 * @param kitPath
	 *            The path in the Toolkit to add the pattern Meem
	 * @param entry
	 *            The MeemkitEntryDescriptor from which to create the pattern Meem
	 */

	private void createToolkitEntryFromWedges(String kitPath, MeemkitEntryDescriptor entry, String resourceClassName) {
		String[] specifiedWedgeClassNames = entry.getWedgeClassNames();
		Class<?>[] specifiedWedges = new Class[specifiedWedgeClassNames.length];
		for (int i = 0; i < specifiedWedgeClassNames.length; i++) {
			try {
				specifiedWedges[i] = Class.forName(specifiedWedgeClassNames[i]);
			}
			catch (ClassNotFoundException ex) {
				errorHandlerConduit.thrown(ex);
				return;
			}
		}

		int numberWedges = specifiedWedges.length;
		Class<?>[] wedges = new Class[numberWedges + 2];
		for (int i = 0; i < numberWedges; i++) {
			wedges[i] = specifiedWedges[i];
		}
		wedges[numberWedges] = VariableMapWedge.class;
		wedges[numberWedges + 1] = MeemPatternWedge.class;

		String path = kitPath + entry.getPath() + "/" + entry.getName();
		Meem meem = null;
		Class<?> resourceClass = null;
		String icon = null;
		String largeIcon = null;

		if (DEBUG) {
			System.err.println("MeemPatternInstallerWedge.createToolkitEntryFromWedges() - before assembleMeem " + entry.getName());
		}

		try {
			MeemDefinition meemDefinition = MeemDefinitionFactory.spi.create().createMeemDefinition(wedges);
			meemDefinition.getMeemAttribute().setImmutableAttribute("meemlicensetype", entry.getMeemLicenseType());
			meem = LifeCycleManagerHelper.createMeemAtPath(meemDefinition, MeemPath.spi.create(Space.HYPERSPACE, path), LifeCycleState.LOADED);
		}
		catch (Exception ex) {
			String message = "Unable to create Meem for [" + entry.getName() + "]: " + ex.getMessage();
			errorHandlerConduit.thrown(new Exception(message));
			return;
		}

		if (meem == null) {
			String message = "Unable to create Meem for [" + entry.getName() + "]: null returned by LifeCycleManagerHelper";
			errorHandlerConduit.thrown(new Exception(message));
			return;
		}

		if (DEBUG)
			System.err.println("MeemPatternInstallerWedge.createToolkitEntryFromWedges() - after assembleMeem");
		try {
			resourceClass = Class.forName(resourceClassName);
		}
		catch (ClassNotFoundException ex) {
			errorHandlerConduit.thrown(ex);
			return;
		}

		if (resourceClass != null) {
			ResourceExporter re = new ResourceExporter(resourceClass);
			MeemIconicPresentation icons = new MeemIconicPresentation();
			icon = entry.getIcon();
			if (icon != null && icon.length() > 0) {
				icons.setSmallIcon(re.extract(icon));
			}
			largeIcon = entry.getLargeIcon();
			if (largeIcon != null && largeIcon.length() > 0) {
				icons.setLargeIcon(re.extract(largeIcon));
			}

			VariableMap variableMap = (VariableMap) ReferenceHelper.getTarget(meem, "variableMap", VariableMap.class);
			variableMap.update(InterMajik.ICONIC_PRESENTATION_KEY, icons);
		}
		
		/*
		if (meem == null) {
			StringBuffer buffer = new StringBuffer();
			buffer.append("createToolkitEntryFromWedges() - meem assembly failed for [");
			buffer.append(entry.getName());
			buffer.append("]. Tried path=[");
			buffer.append(path);
			buffer.append("] wedges=");
			for (int i = 0; i < wedges.length; i++) {
				buffer.append('[');
				buffer.append(wedges[i]);
				buffer.append(']');
			}
			LogTools.error(logger, buffer.toString());
		}
		*/
		else {
			configureMeem(meem, entry);
		}

		LogTools.trace(logger, 10, "createToolkitEntryFromWedges() " + path);
	}

	private void configureMeem(Meem meem, MeemkitEntryDescriptor entry) {
		VariableMap variableMap = (VariableMap) ReferenceHelper.getTarget(meem, "variableMap", VariableMap.class);
		variableMap.update(InterMajik.ABSTRACT_KEY, entry.getAbstract());

		ConfigurationHandler ch = (ConfigurationHandler) ReferenceHelper.getTarget(meem, "configurationHandler", ConfigurationHandler.class);
		ConfigurationIdentifier ci = new ConfigurationIdentifier("MeemSystemWedge", "meemIdentifier");
		ch.valueChanged(ci, entry.getName());
	}

	/**
	 * Add a single Category to the Toolkit given a path.
	 * 
	 * @param path
	 *            The path of the Category to add
	 * @param resourceClassName
	 *            The resource used to load an icon for the Category
	 * @param entry
	 *            The toolkit category entry
	 */

	private void createToolkitCategory(String path, String resourceClassName, ToolkitCategoryEntry entry) {
		Class<?>[] wedges = new Class[] { VariableMapWedge.class, CategoryWedge.class, PatternGroupWedge.class };
		Meem meem = LifeCycleManagerHelper.assembleMeem(wedges, LifeCycleState.READY, LifeCycleState.READY, path);
		if (meem == null) {
			Exception ex = new Exception("LifeCycleManagerHelper returned null for assembleMeem() - category entry " + entry.getName());
			errorHandlerConduit.thrown(ex);
			return;
		}

		Class<?> resourceClass = null;
		String icon = null;

		try {
			resourceClass = Class.forName(resourceClassName);
		}
		catch (ClassNotFoundException classNotFoundException) {
			LogTools.error(logger, "Class Not Found in MeemKit: " + path + ", ClassName: " + resourceClassName);
			return;
		}
		catch (UnsupportedClassVersionError unsupportedClassVersionError) {
			LogTools.error(logger, "Unsupported Class Version in MeemKit: " + path + ", ClassName: " + resourceClassName);
			return;
		}

		if (resourceClass != null) {
			ResourceExporter re = new ResourceExporter(resourceClass);
			MeemIconicPresentation icons = new MeemIconicPresentation();
			icon = entry.getIconName();
			if (icon != null && icon.length() > 0) {
				icons.setSmallIcon(re.extract(icon));
			}
			VariableMap variableMap = (VariableMap) ReferenceHelper.getTarget(meem, "variableMap", VariableMap.class);
			variableMap.update(InterMajik.ICONIC_PRESENTATION_KEY, icons);
		}

		ConfigurationHandler ch = (ConfigurationHandler) ReferenceHelper.getTarget(meem, "configurationHandler", ConfigurationHandler.class);
		ConfigurationIdentifier ci = new ConfigurationIdentifier("MeemSystemWedge", "meemIdentifier");
		ch.valueChanged(ci, entry.getName());

		LogTools.trace(logger, 10, "createToolkitCategory() " + path);
	}

	/**
	 * Uninstall a whole Meemkit into the Toolkit from the specified MeemkitDescriptor.
	 * 
	 * @param descriptor
	 *            The MeemkitDescriptor
	 */

	private void uninstallPatterns(MeemkitDescriptor descriptor) {
		int numberPatternMeems = 0;

		if (descriptor.getMeemDescriptors() != null) {
			String path = StandardHyperSpaceCategory.MAJI_SYSTEM_PATTERN_MEEM;
			removeToolkitEntries(path, descriptor.getMeemDescriptors());
			removeToolkitCategories(path, descriptor.getMeemViewCategoryEntries());
			numberPatternMeems = numberPatternMeems + descriptor.getMeemDescriptors().length;
		}

		if (descriptor.getWedgeDescriptors() != null) {
			String path = StandardHyperSpaceCategory.MAJI_SYSTEM_PATTERN_WEDGE;
			removeToolkitEntries(path, descriptor.getWedgeDescriptors());
			removeToolkitCategories(path, descriptor.getWedgeViewCategoryEntries());
			numberPatternMeems = numberPatternMeems + descriptor.getWedgeDescriptors().length;
		}

		LogTools.info(logger, "uninstallPatterns() - name=[" + descriptor.getHeader().getName() + "] uninstalled " + numberPatternMeems + " pattern meems from toolkit");
	}

	/**
	 * Remove a list of toolkit categories.
	 * 
	 * @param kitPath
	 *            The path in the Toolkit from which to remove the categories
	 * @param entries
	 *            The array of ToolkitCategoryEntry
	 */
	private void removeToolkitCategories(String kitPath, ToolkitCategoryEntry[] entries) {
		if (entries == null)
			return;

		// Remove the categories in reverse order otherwise things go boom
		for (int i = entries.length - 1; i >= 0; i--) {
			ToolkitCategoryEntry entry = entries[i];
			String parentCategoryPathName;
			if (entry.getPath() == null || entry.getPath().length() == 0) {
				parentCategoryPathName = kitPath;
			}
			else {
				parentCategoryPathName = kitPath + entry.getPath();
			}
			String categoryPathName = parentCategoryPathName + "/" + entry.getName();
			remove(parentCategoryPathName, categoryPathName, entry.getName());
		}
	}

	/**
	 * Remove a list of pattern Meems from the Toolkit.
	 * 
	 * @param kitPath
	 *            The path in the Toolkit from which to remove the pattern Meems
	 * @param entries
	 *            The array of MeemkitEntryDescriptor
	 */

	private void removeToolkitEntries(String kitPath, MeemkitEntryDescriptor[] entries) {
		if (entries == null)
			return;

		for (int i = 0; i < entries.length; i++) {
			MeemkitEntryDescriptor entry = entries[i];
			String categoryPath = kitPath + entry.getPath();
			String patternPath = categoryPath + "/" + entry.getName();
			remove(categoryPath, patternPath, entry.getName());
		}
	}

	/**
	 * Remove a Meem from the parent category and also change its lifecycle state to ABSENT.
	 * 
	 * @param parentPathName
	 *            The hyperspace path of the parent category
	 * @param childPathName
	 *            The hyperspace path of the meem
	 * @param childName
	 *            The name of the meem
	 */
	private void remove(String parentPathName, String childPathName, String childName) {
		MeemPath childMeemPath = MeemPath.spi.create(Space.HYPERSPACE, childPathName);
		MeemPath parentMeemPath = MeemPath.spi.create(Space.HYPERSPACE, parentPathName);

		Meem patternMeem = Meem.spi.get(childMeemPath);
		if (patternMeem == null) {
			LogTools.error(logger, "remove() - unable to get pattern meem " + childPathName);
			return;
		}

		Meem parentMeem = Meem.spi.get(parentMeemPath);
		if (parentMeem == null) {
			LogTools.error(logger, "remove() - unable to get parent meem " + parentPathName);
			return;
		}
		Category category = CategoryUtility.spi.get().getCategory(parentMeem);
		if (category == null) {
			LogTools.error(logger, "remove() - unable to get category meem " + parentPathName);
			return;
		}

		LifeCycle lifeCycle = (LifeCycle) ReferenceHelper.getTarget(patternMeem, "lifeCycle", LifeCycle.class);
		if (lifeCycle == null) {
			LogTools.error(logger, "remove() - unable to change '" + childName + "' to ABSENT state");
			return;
		}

		category.removeEntry(childName);
		lifeCycle.changeLifeCycleState(LifeCycleState.ABSENT);
		LogTools.trace(logger, 10, "remove() - " + childPathName);
	}

	/* ------------------------------------------------------------------------ */

	private class MeemPatternControlConduit implements MeemPatternControl {
		public void installPatternMeems(MeemkitDescriptor descriptor) {
			installPatterns(descriptor);
			meemPatternStateConduit.patternMeemsInstalled(descriptor.getHeader().getName());
		}

		public void uninstallPatternMeems(MeemkitDescriptor descriptor) {
			uninstallPatterns(descriptor);
			meemPatternStateConduit.patternMeemsUninstalled(descriptor.getHeader().getName());
		}
	}
}
