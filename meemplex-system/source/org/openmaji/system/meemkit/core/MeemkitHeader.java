/*
 * Copyright 2004 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.system.meemkit.core;

import java.io.Serializable;

/**
 * Represents that header of a meemkit descriptor file.
 * 
 * @author Chris Kakris
 */

public class MeemkitHeader implements Serializable {
	private static final long serialVersionUID = 534540102928363464L;

	private final MeemkitVersion meemkitVersion;

	private String author = "";

	private String company = "";

	private String copyright = "";

	private String summary = "";

	private String filename = "";

	private String resourceClassName = "";

	/**
	 * Creates an instance of a MeemkitHeader with the specified version.
	 * 
	 * @param meemkitVersion
	 *            The version of the meemkit
	 */

	public MeemkitHeader(MeemkitVersion meemkitVersion) {
		this.meemkitVersion = meemkitVersion;
	}

	public MeemkitVersion getMeemkitVersion() {
		return meemkitVersion;
	}

	/**
	 * Return the name of the meemkit.
	 * 
	 * @return The name of the meemkit
	 */

	public String getName() {
		return meemkitVersion.getMeemkitName();
	}

	/**
	 * Return the author of the meemkit
	 * 
	 * @return The author of the meemkit
	 */

	public String getAuthor() {
		return author;
	}

	/**
	 * Set the author of the meemkit.
	 * 
	 * @param author
	 *            The authod of the meemkit
	 */

	public void setAuthor(String author) {
		this.author = author;
	}

	/**
	 * Return the company that produced the meemkit.
	 * 
	 * @return The company that produced the meemkit
	 */

	public String getCompany() {
		return company;
	}

	/**
	 * Set the company that produced the meemkit.
	 * 
	 * @param company
	 *            The company that produced the meemkit
	 */

	public void setCompany(String company) {
		this.company = company;
	}

	/**
	 * Return the copyright notice of this meemkit.
	 * 
	 * @return The copyright notice of this meemkit
	 */

	public String getCopyright() {
		return copyright;
	}

	/**
	 * Set the copyright notice for this meemkit
	 * 
	 * @param copyright
	 *            The copyright notice for this meemkit
	 */

	public void setCopyright(String copyright) {
		this.copyright = copyright;
	}

	/**
	 * Return the summary for this meemkit
	 * 
	 * @return The summary for this meemkit
	 */

	public String getSummary() {
		return summary;
	}

	/**
	 * Set the summary for this meemkit.
	 * 
	 * @param summary
	 *            The summary for this meemkit
	 */

	public void setSummary(String summary) {
		this.summary = summary;
	}

	/**
	 * Returns the name of the meemkit descriptor file
	 * 
	 * @return The name of the meemkit descriptor file
	 */

	public String getFilename() {
		return filename;
	}

	/**
	 * Set the name of the meemkit descriptor file.
	 * 
	 * @param filename
	 *            The name of the meemkit descriptor file.
	 */

	public void setFilename(String filename) {
		this.filename = filename;
	}

	/**
	 * Returns a String representation of this instance
	 * 
	 * @return Returns a String representation
	 */

	public String toString() {
		return meemkitVersion.toString();
	}

	/**
	 * Return the name of the meemkit's resource class.
	 * 
	 * @return The name of the resource class
	 */
	public String getResourceClassName() {
		return resourceClassName;
	}

	/**
	 * Set the name of the meemkit's resource class.
	 * 
	 * @param string
	 *            The name of the resource class
	 */
	public void setResourceClassName(String string) {
		resourceClassName = string;
	}

}
