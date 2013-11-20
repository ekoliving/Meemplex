/*
 * Copyright 2004 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.system.meemkit.core;

import java.io.Serializable;
import java.util.StringTokenizer;

/**
 * Represents the version of a Meemkit. A Meemkit's version is defined as being that kit's name along with its version number. The version number has the form "major.minor".
 * 
 * @author Chris Kakris
 */
public class MeemkitVersion implements Serializable {
	private static final long serialVersionUID = 534540102928363464L;

	private final String meemkitName;

	private final int major;

	private final int minor;

	private final String version;

	private final int myHashCode;

	private final String myStringRepresentation;

	public MeemkitVersion(String meemkitName, String version) {
		if (meemkitName == null)
			throw new IllegalArgumentException("Meemkit name must not be null");
		if (version == null)
			throw new IllegalArgumentException("Version must not be null");
		this.meemkitName = meemkitName;
		this.version = version;

		StringTokenizer stz = new StringTokenizer(version, ".");
		if (stz.countTokens() != 2)
			throw new IllegalArgumentException("Version number '" + version + "' badly formed");
		major = Integer.parseInt(stz.nextToken());
		minor = Integer.parseInt(stz.nextToken());
		verifyVersionNumbers(major, minor);
		myHashCode = calculateHashCode();
		myStringRepresentation = meemkitName + "_" + version;
	}

	public MeemkitVersion(String meemkitName, int major, int minor) {
		if (meemkitName == null)
			throw new IllegalArgumentException("Meemkit name must not be null");
		this.meemkitName = meemkitName;
		verifyVersionNumbers(major, minor);
		this.major = major;
		this.minor = minor;
		this.version = major + "." + minor;
		myHashCode = calculateHashCode();
		myStringRepresentation = meemkitName + "_" + major + "." + minor;
	}

	private void verifyVersionNumbers(int major, int minor) {
		if (major < 0)
			throw new IllegalArgumentException("The major version number must not be negative");
		if (minor < 0)
			throw new IllegalArgumentException("The minor version number must not be negative");
	}

	public String getMeemkitName() {
		return meemkitName;
	}

	public String getVersion() {
		return version;
	}

	public int getMajor() {
		return major;
	}

	public int getMinor() {
		return minor;
	}

	public boolean equals(Object object) {
		if (object == this)
			return true;
		if (object.getClass().equals(this.getClass()) == false)
			return false;
		MeemkitVersion that = (MeemkitVersion) object;
		if (this.meemkitName.equals(that.meemkitName) == false)
			return false;
		if (this.major != that.major)
			return false;
		if (this.minor != that.minor)
			return false;
		return true;
	}

	public int hashCode() {
		// All fields for this class are final so the hash code only needs to be
		// calculated the once at construction time.

		return this.myHashCode;
	}

	private int calculateHashCode() {
		// Suggested approaches to calculating a hashcode:
		// http://www.geocities.com/technofundo/tech/java/equalhash.html
		// http://www-106.ibm.com/developerworks/java/library/j-jtp05273.html

		int hash = 7;
		hash = 31 * hash + major;
		hash = 31 * hash + minor;
		hash = 31 * hash + (meemkitName == null ? 0 : meemkitName.hashCode());
		return hash;
	}

	public boolean compatibleWith(MeemkitVersion that) {
		if (this.meemkitName.equals(that.meemkitName) == false)
			return false;
		return (this.major == that.major);
	}

	public String toString() {
		return this.myStringRepresentation;
	}
}
