/*
 * Copyright 2004 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.system.meemkit.core;

import java.io.Serializable;
import java.util.List;

/**
 * Models a Meemkit descriptor file. The meemkit descriptor is an XML document that a developer uses to specify what pattern Meems to place into the Toolkit's Meem view and Wedge
 * view.
 * 
 * @author Chris Kakris
 */

public class MeemkitDescriptor implements Serializable {
	private static final long serialVersionUID = 534540102928363464L;

	/**
	 * The suffix used to identify a meemkit archive and distinguish it from the meemkit class jar file which has a suffix of .jar
	 */
	public static final String ARCHIVE_SUFFIX = ".meemkit";

	private final MeemkitHeader header;

	private MeemkitDependency[] dependencies = null;

	private ToolkitCategoryEntry[] meemViewCategoryEntries = null;

	private ToolkitCategoryEntry[] wedgeViewCategoryEntries = null;

	private MeemkitEntryDescriptor[] meemDescriptors = null;

	private MeemkitEntryDescriptor[] wedgeDescriptors = null;

	private MeemkitLibrary[] libraries = null;

	private MeemkitWizardDescriptor[] wizardDescriptors = null;

	/**
	 * Creates an instance with the given MeemkitHeader
	 * 
	 * @param header
	 *            The header representing the meemkit descriptor
	 */

	public MeemkitDescriptor(MeemkitHeader header) {
		this.header = header;
	}

	/**
	 * Return the header for this descriptor
	 * 
	 * @return The header for this descriptor
	 */

	public MeemkitHeader getHeader() {
		return header;
	}

	/**
	 * Return the array of MeemkitEntryDescriptors for the entries to be added to the toolkit Meem view.
	 * 
	 * @return The array of MeemkitEntryDescriptors
	 */

	public MeemkitEntryDescriptor[] getMeemDescriptors() {
		return meemDescriptors;
	}

	/**
	 * Set the list of MeemkitEntryDescriptors for the meemkit's meems
	 * 
	 * @param descriptors
	 *            The list of meem entries
	 */

	public void setMeemDescriptors(MeemkitEntryDescriptor[] descriptors) {
		meemDescriptors = descriptors;
	}

	/**
	 * Return the array of MeemkitEntryDescriptors for the entries to be added to the toolkit Wedge view.
	 * 
	 * @return The array of MeemkitEntryDescriptors
	 */

	public MeemkitEntryDescriptor[] getWedgeDescriptors() {
		return wedgeDescriptors;
	}

	/**
	 * Set the list of MeemkitEntryDescriptors for the meemkit's wedges
	 * 
	 * @param descriptors
	 *            The list of wedge entries
	 */

	public void setWedgeDescriptors(MeemkitEntryDescriptor[] descriptors) {
		wedgeDescriptors = descriptors;
	}

	/**
	 * Return all of the Meemkit's dependencies.
	 * 
	 * @return An array of dependencies
	 */
	public MeemkitDependency[] getDependencies() {
		return dependencies;
	}

	/**
	 * Set the meemkit's dependencies.
	 * 
	 * @param dependencies
	 *            Set the meemkit's dependencies
	 */
	public void setDependencies(MeemkitDependency[] dependencies) {
		this.dependencies = dependencies;
	}

	/**
	 * Return all of the Meemkit's libraries.
	 * 
	 * @return An array of libraries
	 */
	public MeemkitLibrary[] getLibraries() {
		return libraries == null ? new MeemkitLibrary[0] : libraries;
	}

	/**
	 * Set the meemkit's libraries.
	 * 
	 * @param libraries
	 *            Set the meemkit's libraries
	 */
	public void setLibraries(MeemkitLibrary[] libraries) {
		this.libraries = libraries;
	}

	/**
	 * Return the category entries for the Toolkit meemview.
	 * 
	 * @return An array of ToolkitCategoryEntry
	 */
	public ToolkitCategoryEntry[] getMeemViewCategoryEntries() {
		return meemViewCategoryEntries;
	}

	/**
	 * Set the category entries for the Toolkit meemview.
	 * 
	 * @param categoryEntries
	 *            The Toolkit Category entries
	 */
	public void setMeemViewCategoryEntries(ToolkitCategoryEntry[] categoryEntries) {
		meemViewCategoryEntries = categoryEntries;
	}

	/**
	 * Return the category entries for the Toolkit wedgeview.
	 * 
	 * @return An array of ToolkitCategoryEntry
	 */
	public ToolkitCategoryEntry[] getWedgeViewCategoryEntries() {
		return wedgeViewCategoryEntries;
	}

	/**
	 * Set the category entries for the Toolkit wedgeview.
	 * 
	 * @param categoryEntries
	 *            The Toolkit Category entries
	 */
	public void setWedgeViewCategoryEntries(ToolkitCategoryEntry[] categoryEntries) {
		wedgeViewCategoryEntries = categoryEntries;
	}

	/**
	 * Return a textual overview of the descriptor suitable for displaying to a user.
	 * 
	 * @return A textual overview of the descriptor.
	 */
	public String getOverview() {
		StringBuffer buffer = new StringBuffer();
		if (header.getSummary() != null) {
			buffer.append(header.getSummary());
			buffer.append("\n");
		}
		MeemkitVersion meemkitVersion = header.getMeemkitVersion();
		buffer.append("\nVersion = ");
		buffer.append(meemkitVersion.getMajor());
		buffer.append('.');
		buffer.append(meemkitVersion.getMinor());

		if (header.getAuthor() != null) {
			buffer.append("\nAuthor = ");
			buffer.append(header.getAuthor());
		}
		if (header.getCompany() != null) {
			buffer.append("\nCompany = ");
			buffer.append(header.getCompany());
		}
		if (header.getCopyright() != null) {
			buffer.append("\nCopyright = ");
			buffer.append(header.getCopyright());
		}
		if (dependencies != null) {
			buffer.append("\n\nDependencies =");
			for (int i = 0; i < dependencies.length; i++) {
				buffer.append(' ');
				buffer.append(dependencies[i].getMeemkitVersion().toString());
			}
		}
		if (libraries != null) {
			buffer.append("\n\nLibraries =");
			for (int i = 0; i < libraries.length; i++) {
				buffer.append(' ');
				buffer.append(libraries[i].getName());
			}
		}
		if (meemDescriptors == null && wedgeDescriptors == null) {
			buffer.append("\nDoes not contain any toolkit entries");
		}
		else {
			buffer.append("\nContains toolkit entries: Meems=[");
			if (meemDescriptors == null || meemDescriptors.length == 0) {
				buffer.append('0');
			}
			else {
				buffer.append(meemDescriptors.length);
			}
			buffer.append("] Wedges=[");
			if (wedgeDescriptors == null || wedgeDescriptors.length == 0) {
				buffer.append('0');
			}
			else {
				buffer.append(wedgeDescriptors.length);
			}
			buffer.append("]");
		}

		return buffer.toString();
	}

	/**
	 * Returns whether or not this descriptor's dependencies are listed in the specified list of meemkit versions.
	 * 
	 * @param installedVersions
	 *            The list of installed meemkit versions
	 * @return Whether or not all of the dependencies are installed
	 */
	public boolean allDependenciesInstalled(List<MeemkitVersion> installedVersions) {
		if (dependencies == null || dependencies.length == 0)
			return true;

		for (int i = 0; i < dependencies.length; i++) {
			MeemkitVersion dependeeVersion = dependencies[i].getMeemkitVersion();
			boolean resolved = false;
			for (MeemkitVersion installedVersion : installedVersions) {
				if (installedVersion.compatibleWith(dependeeVersion)) {
					resolved = true;
					break;
				}
			}
			if (resolved == false) {
				return false;
			}
		}

		return true;
	}

	public boolean dependsOn(MeemkitDescriptor other) {
		if (dependencies == null || dependencies.length == 0) {
			return false;
		}
		
		MeemkitVersion otherVersion = other.getHeader().getMeemkitVersion();
		for (int i = 0; i < dependencies.length; i++) {
			MeemkitVersion dependeeVersion = dependencies[i].getMeemkitVersion();
			if (dependeeVersion.compatibleWith(otherVersion)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * @return Returns the wizardDescriptors.
	 */
	public MeemkitWizardDescriptor[] getWizardDescriptors() {
		return wizardDescriptors;
	}

	/**
	 * @param wizardDescriptors
	 *            The wizardDescriptors to set.
	 */
	public void setWizardDescriptors(MeemkitWizardDescriptor[] wizardDescriptors) {
		this.wizardDescriptors = wizardDescriptors;
	}
}
