/*
 * @(#)Abstract.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.system.meempool.metadata;

import java.io.Serializable;

/**
 * <p>
 * An <code>Abstract</code> is a summary describing the publication details
 * of a {@link org.openmaji.meem.Meem}.  It includes such information as
 * the <code>Meem's</code> description, author and copyright information.
 * </p>
 * <p>
 * <code>Abtracts</code> are typically used inside <code>MeemKits</code>
 * to describe the various <code>Meems</code> contained within the
 * <code>MeemKit</code>.
 * InterMajik and other development tools can acquire and present the
 * <code>Abstract</code> for a {@link org.openmaji.meem.Meem}, to
 * provide developers with useful information about that <code>Meem</code>.
 * </p>
 * <p>
 * Note: In this case, <code>Abstract</code> should not be confused with
 * the other common meanings of the word, e.g. an abstract concept or an
 * abstract class.  Best to think of this in the context of publishing.
 * </p>
 * <blockquote>
 * "There is plenty of courage among us for the abstract but not for the
 *  concrete." - Helen Keller
 * </blockquote>
 * @author Kin Wong
 * @version 1.0
 */

public class Abstract implements Serializable, Cloneable {
	private static final long serialVersionUID = 1501074083161487962L;

  /**
   * Default empty text String for Abstract items
   */

  static private final String UNDEFINED = "";

  /**
   * Person or organization that created the Meem
   */

  private String author = UNDEFINED;

  /**
   * Organization that owns the Meem
   */

  private String company = UNDEFINED;

  /**
   * Copyright information pertaining to the Meem
   */

  private String copyright = UNDEFINED;

  /**
   * Detailed description of the Meem
   */

  private String description = UNDEFINED;

  /**
   * Name of the Meem
   */

  private String name = UNDEFINED;

  /**
   * Summary description of the Meem
   */

  private String overview = UNDEFINED;

  /**
   * Version number of the Meem
   */

  private String version = UNDEFINED;

  /**
   * Create an Abstract with undefined content.
   * By default, Abstract items contain an empty text String.
   */

  public Abstract() {
  }

  /**
   * Create an Abstract for a Meem with the specified name.
   * All other Abstract items will contain empty text Strings.
   *
   * @param name Name of the Meem
   */

  public Abstract(
    String name) {

    this.name = name;
  }

  /**
   * Creates and returns a copy of this object.
   * If the instance can't be cloned, then "null" is returned.
   *
   * @return A clone of this instance or null
   */

  public Object clone() {
    try {
      return super.clone();
    }
    catch(CloneNotSupportedException cloneNotSupportedException) {
      return(null);
    }
  }

  /**
   * Provides the person or organization that created the Meem.
   * By default, this is an empty String.
   *
   * @return Author's name
   */

  public String getAuthor() {
    return(author);
  }

  /**
   * Provides the organization that owns the Meem.
   * By default, this is an empty String.
   *
   * @return Company's name
   */

  public String getCompany() {
    return(company);
  }

  /**
   * Provides the copyright information pertaining to the Meem.
   * By default, this is an empty String.
   *
   * @return Copyright information
   */

  public String getCopyright() {
    return(copyright);
  }

  /**
   * Provides the detailed description of the Meem.
   * By default, this is an empty String.
   *
   * @return Detailed description
   */

  public String getDescription() {
    return(description);
  }

  /**
   * Provides the name of the Meem.
   * By default, this is an empty String.
   *
   * @return Meem's name
   */

  public String getName() {
    return(name);
  }

  /**
   * Provides the summary description of the Meem.
   * By default, this is an empty String.
   *
   * @return Summary description
   */

  public String getOverview() {
    return(overview);
  }

  /**
   * Provides the version number of the Meem.
   * By default, this is an empty String.
   *
   * @return Version number
   */

  public String getVersion() {
    return(version);
  }

  /**
   * Assign the person or organization that created the Meem.
   *
   * @param author Author's name
   */

  public void setAuthor(
    String author) {

    this.author = author;
  }

  /**
   * Assign the organization that owns the Meem.
   *
   * @param company Company's name
   */

  public void setCompany(
    String company) {

    this.company = company;
  }

  /**
   * Assign the copyright information.
   *
   * @param copyright Copyright information
   */

  public void setCopyright(
    String copyright) {

    this.copyright = copyright;
  }

  /**
   * Assign the detailed description of the Meem.
   *
   * @param description Detailed description
   */

  public void setDescription(
    String description) {

    this.description = description;
  }

  /**
   * Assign the name of the Meem.
   *
   * @param name Meem's name
   */

  public void setName(
    String name) {

    this.name = name;
  }

  /**
   * Assign the summary description of the Meem.
   *
   * @param overview Summary description
   */

  public void setOverview(
    String overview) {

    this.overview = overview;
  }

  /**
   * Assign the version number of the Meem.
   *
   * @param version Version number
   */

  public void setVersion(
    String version) {

    this.version = version;
  }

  /**
   * Provides a String representation of <code>Abstract</code>.
   *
   * @return String representation of Abstract
   */

  public synchronized String toString() {
    return(
      getClass().getName() + "[" +
        "name="        + name +
      ", author="      + author +
      ", company="     + company +
      ", copyright="   + copyright +
      ", description=" + description +
      ", overview="    + overview +
      ", version="     + version +
      "]"
    );
  }
}
