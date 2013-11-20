/*
 * Copyright 2004 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.system.meemkit.core;

import java.io.Serializable;

import org.openmaji.meem.definition.MeemLicensingType;
import org.openmaji.system.meempool.metadata.Abstract;


/**
 * Models the entries of a Meemkit descriptor file. Each entry represents
 * a single pattern Meem to be added to the Toolkit.
 * 
 * @author Chris Kakris
 */

public class MeemkitEntryDescriptor implements Serializable
{
	private static final long serialVersionUID = 534540102928363464L;

  protected final String name;
  protected final String path;
  protected String icon;
  protected String largeIcon;
  protected String[] wedgeClassNames = null;
  protected String meemDefinitionProviderClassName;
  protected String title;
  protected String overview;
  protected String author;
  protected String company;
  protected String copyright;
  protected String detail;
  protected String version;
  protected MeemLicensingType   meemLicenseType;

  public MeemkitEntryDescriptor(String name, String path)
  {
    this.name = name;
    this.path = path;
  }

  /**
	 * Return the Abstract representing this meemkit descriptor entry.
	 * 
	 * @return The Abstract
	 */

	public Abstract getAbstract()
  {
    Abstract meemAbstract = new Abstract(title == null ? "" : title);
    meemAbstract.setAuthor(author == null ? "" : author);
    meemAbstract.setVersion(version==null ? "":version);
    meemAbstract.setOverview(overview == null ? "" : overview);
    meemAbstract.setDescription(detail == null ? "" : detail);
    meemAbstract.setCompany(company == null ? "" : company);
    meemAbstract.setCopyright(copyright == null ? "" : copyright);
    return meemAbstract;
  }

  /**
   * 
   * 
   * @return The Author
   */
  public String getAuthor()
  {
    return author;
  }

  /**
   * 
   * 
   * @return The Detail
   */
  public String getDetail()
  {
    return detail;
  }

  /**
   * 
   * 
   * @return The icon name
   */
  public String getIcon()
  {
    return icon;
  }
  
  /**
    * 
    * 
    * @return The large icon name
    */
   public String getLargeIcon()
   {
     return largeIcon;
   }
   
  /**
   * 
   * 
   * @return The meem definition provider
   */
  public String getMeemDefinitionProviderClassName()
  {
    return meemDefinitionProviderClassName;
  }

  /**
   * 
   * 
   * @return The entry name
   */
  public String getName()
  {
    return name;
  }

  /**
   * 
   * 
   * @return The entry overview
   */
  public String getOverview()
  {
    return overview;
  }

  /**
   * 
   * 
   * @return The entry title
   */
  public String getTitle()
  {
    return title;
  }

  /**
   * 
   * 
   * @return The entry's wedges
   */
  public String[] getWedgeClassNames()
  {
    return wedgeClassNames;
  }

  /**
   * Getter for the meem licensing type.
   * 
   * @return the meem license.
   */
  public MeemLicensingType getMeemLicenseType()
  {
      return meemLicenseType;
  }
  
  public String getVersion(){
  	return version;
  }
  
  public void setVersion(String string){
  	version=string;
  }

  public void setAuthor(String string)
  {
    author = string;
  }

  public void setDetail(String string)
  {
    detail = string;
  }

  public void setIcon(String string)
  {
    icon = string;
  }

  public void setLargeIcon(String string)
  {
    largeIcon = string;
  }

  public void setMeemDefinitionProviderClassName(String className)
  {
    meemDefinitionProviderClassName = className;
  }

  public void setOverview(String string)
  {
    overview = string;
  }

  public void setTitle(String string)
  {
    title = string;
  }

  public void setWedgeClassNames(String[] names)
  {
    wedgeClassNames = names;
  }

  public String getPath()
  {
    return path;
  }

  public String getCompany()
  {
    return company;
  }

  public String getCopyright()
  {
    return copyright;
  }

  public void setCompany(String string)
  {
    company = string;
  }

  public void setCopyright(String string)
  {
    copyright = string;
  }

  /**
   * Setter for the meem license type.
   * 
   * @param string the license id.
   */
  public void setMeemLicenseType(String string)
  {
      meemLicenseType = new MeemLicensingType();
      meemLicenseType.setLicensingType(string);
  }
}
