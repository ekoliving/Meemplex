/*
 * Copyright 2004 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.server.manager.lifecycle.meemkit;

import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.openmaji.system.meemkit.core.MeemkitDependency;
import org.openmaji.system.meemkit.core.MeemkitDescriptor;
import org.openmaji.system.meemkit.core.MeemkitEntryDescriptor;
import org.openmaji.system.meemkit.core.MeemkitHeader;
import org.openmaji.system.meemkit.core.MeemkitLibrary;
import org.openmaji.system.meemkit.core.MeemkitLibraryExport;
import org.openmaji.system.meemkit.core.MeemkitVersion;
import org.openmaji.system.meemkit.core.MeemkitWizardDescriptor;
import org.openmaji.system.meemkit.core.ToolkitCategoryEntry;


import java.util.logging.Level;
import java.util.logging.Logger;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Contains a number of static utility methods to for parsing meemkit descriptor
 * files and creating instances of MeemkitDescriptor and MeemkitHeader.
 *
 * @author Chris Kakris
 */

public class MeemkitUtility
{	
  private static final Logger logger = Logger.getAnonymousLogger();

  private static EntityResolver entityResolver = null;
	
	static {  	
  	try {
			new URL("http://www.majitek.com").openConnection().connect();
		} catch (MalformedURLException e) {
		} catch (IOException e) {
			entityResolver = new EntityResolver() {
				public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
					return new InputSource(new StringReader(""));
				}
			};
		}
	}
  
  /**
   * Parses an XML document and constructs an instance of MeemkitHeader.
   * 
   * @param filename The name of an XML file containing the meemkit descriptor
   * @return A MeemkitHeader constructed by parsing the specified file
   */

  public static MeemkitHeader createMeemkitHeader(String filename)
  {
    Document doc = null;

    try
    {
      SAXBuilder builder = new SAXBuilder();
      builder.setEntityResolver(entityResolver);
      doc = builder.build(new FileReader(filename));
    }
    catch ( Exception ex )
    {
      logger.log(Level.WARNING, "createMeemkitHeader() - unable to parse meemkit descriptor "+filename,ex);
      return null;
    }

    return createMeemkitHeader(doc,filename);
  }
  
  /**
   * Constructs an instance of MeemkitHeader from a given Document.
   * 
   * @param doc A Document created from the meemkitDescriptor file
   * @param filename The name of an XML file containing the meemkit descriptor
   * @return A MeemkitHeader constructed by parsing the specified file
   */

  public static MeemkitHeader createMeemkitHeader(Document doc, String filename)
  {    
    Element rootElement = doc.getRootElement();
    String name = getAttribute(rootElement,"name");
    String version = getAttribute(rootElement,"version");
    MeemkitVersion meemkitVersion = null;
    try
    {
      meemkitVersion = new MeemkitVersion(name,version);
    }
    catch ( IllegalArgumentException ex )
    {
      logger.log(Level.WARNING, "createMeemkitHeader() - bad meemkit descriptor for "+filename,ex);
    }
    
    MeemkitHeader header = new MeemkitHeader(meemkitVersion);
    header.setFilename(filename);
    header.setAuthor(getAttribute(rootElement,"author"));
    header.setCompany(getAttribute(rootElement,"company"));
    header.setCopyright(getAttribute(rootElement,"copyright"));
    header.setSummary(getText(rootElement.getChild("summary")));
    header.setResourceClassName(getAttribute(rootElement,"resourceClass"));

    return header;
  }

  /**
   * Parses an XML document and constructs an instance of MeemkitDescriptor.
   * 
   * @param filename The name of an XML file containing the meemkit descriptor
   * @return A MeemkitHeader constructed by parsing the specified file
   */

  public static MeemkitDescriptor createMeemkitDescriptor(String filename)
  {
    Document doc = null;

    try
    {
      SAXBuilder builder = new SAXBuilder();
      builder.setEntityResolver(entityResolver);
      doc = builder.build(new FileReader(filename));
    }
    catch ( Exception ex )
    {
      logger.log(Level.WARNING, "createMeemkitDescriptor() - unable to parse meemkit descriptor "+filename,ex);
      return null;
    }
    
    return createMeemkitDescriptor(doc,filename);
  }
  
  /**
   * Parses an XML document and constructs an instance of MeemkitDescriptor.
   * 
   * @param location The location of an XML file containing the meemkit descriptor
   * @return A MeemkitDescriptor constructed by parsing the specified file
   */

  public static MeemkitDescriptor createMeemkitDescriptor(URL location)
  {
    Document doc = null;

    try
    {
      SAXBuilder builder = new SAXBuilder();
      builder.setEntityResolver(entityResolver);
      doc = builder.build(location);
    }
    catch ( Exception ex )
    {
      logger.log(Level.WARNING, "createMeemkitDescriptor() - unable to parse meemkit descriptor "+location,ex);
      return null;
    }
    
    return createMeemkitDescriptor(doc, location.toExternalForm());
  }
  
  /**
   * Parses an XML document and constructs an instance of MeemkitDescriptor. 
   * This method only includes the MeemkitHeader and libraries fields.
   * 
   * @param location The location of an XML file containing the meemkit descriptor
   * @return A MeemkitDescriptor constructed by parsing the specified file
   */

  public static MeemkitDescriptor createMeemkitLibrariesDescriptor(URL location)
  {
    Document doc = null;

    try
    {
      SAXBuilder builder = new SAXBuilder();
      builder.setEntityResolver(entityResolver);
      doc = builder.build(location);
    }
    catch ( Exception ex )
    {
      logger.log(Level.WARNING, "createMeemkitDescriptor() - unable to parse meemkit descriptor "+location+": "+ex.getMessage());
      return null;
    }
    
    MeemkitHeader header = createMeemkitHeader(doc, location.toExternalForm());
    MeemkitDescriptor descriptor = new MeemkitDescriptor(header);
    
    setLibraries(descriptor,doc.getRootElement().getChild("libraries"));

    return descriptor;
    
  }
  
  /**
   * Constructs an instance of MeemkitDescriptor from a given Document.
   * 
   * @param doc A Document created from the meemkitDescriptor file
   * @param filename The name of an XML file containing the meemkit descriptor
   * @return A MeemkitDescriptor constructed by parsing the specified file
   */

  public static MeemkitDescriptor createMeemkitDescriptor(Document doc, String filename)
  {
    MeemkitHeader header = createMeemkitHeader(doc,filename);
    MeemkitDescriptor descriptor = new MeemkitDescriptor(header);
    
    Element rootElement = doc.getRootElement();
    setDependencies(descriptor,rootElement.getChild("dependencies"));
   	processMeemview(descriptor,rootElement.getChild("meemview"));
   	processWedgeview(descriptor,rootElement.getChild("wedgeview"));
    setLibraries(descriptor,rootElement.getChild("libraries"));
    setWizardDescriptors(descriptor,rootElement.getChild("wizards"));

    return descriptor;
  }

  /**
   * Add the category and meemview entries to the MeemkitDescriptor.
   * 
   * @param descriptor  The descriptor to update
   * @param parent      The JDOM element to parse.
   */
  private static void processMeemview(MeemkitDescriptor descriptor, Element parent)
  {
    if ( parent == null ) return;

    String meemkitName = descriptor.getHeader().getName();
    descriptor.setMeemViewCategoryEntries(getToolkitCategoryEntries(meemkitName,parent.getChild("hierarchy")));
    descriptor.setMeemDescriptors(getEntries(descriptor,parent));
  }

  /**
   * Add the category and wedgeview entries to the MeemkitDescriptor.
   * 
   * @param descriptor  The descriptor to update
   * @param parent      The JDOM element to parse.
   */
  private static void processWedgeview(MeemkitDescriptor descriptor, Element parent)
  {
    if ( parent == null ) return;

    String meemkitName = descriptor.getHeader().getName();
    descriptor.setWedgeViewCategoryEntries(getToolkitCategoryEntries(meemkitName,parent.getChild("hierarchy")));
    descriptor.setWedgeDescriptors(getEntries(descriptor,parent));
  }

  /**
   * Return the list of toolkit category entries specified in the JDOM element.
   * 
   * @param meemkitName  The name of the meemkit
   * @param parent       The JDOM element to process
   * @return             The list of toolkit category entries
   */
  private static ToolkitCategoryEntry[] getToolkitCategoryEntries(String meemkitName, Element parent)
  {
    if ( parent == null ) return null;
    
    ArrayList<ToolkitCategoryEntry> list = new ArrayList<ToolkitCategoryEntry>();
    List entries = parent.getChildren("category");
    for ( int i=0; i<entries.size(); i++ )
    {
      Element element = (Element) entries.get(i);
      String name = getAttribute(element,"name");
      String icon = getAttribute(element,"icon");
      String path = getAttribute(element,"path");
      if ( name == null || path == null )
      {
        logger.log(Level.WARNING, "getToolkitCategoryEntries() - must specify path and name attributes for toolkit category entry in meemkit "+meemkitName);
      }
      else
      {
        ToolkitCategoryEntry entry = new ToolkitCategoryEntry(name,path);
        entry.setIconName(icon);
        list.add(entry);
      }
    }

    if ( list.size() == 0 ) return null;
    ToolkitCategoryEntry[] toolkitEntries = new ToolkitCategoryEntry[list.size()];
    list.toArray(toolkitEntries);
    return toolkitEntries;
  }

  /**
   * Set the dependencies for a MeemkitDescriptor.
   * 
   * @param descriptor  The MeemkitDescriptor to update
   * @param parent      The JDOM node to process
   */
  private static void setDependencies(MeemkitDescriptor descriptor, Element parent)
  {
    if ( parent == null ) return;

    List list = parent.getChildren("dependentMeemkit");
    if ( list == null || list.size() == 0 ) return;

    ArrayList<MeemkitDependency> dependencyList = new ArrayList<MeemkitDependency>();

    for ( int i=0; i<list.size(); i++ )
    {
      Element element = (Element) list.get(i);
      String meemkitName = getAttribute(element,"name");
      String version = getAttribute(element,"version");
      try
      {
        MeemkitVersion meemkitVersion = new MeemkitVersion(meemkitName,version);
        dependencyList.add(new MeemkitDependency(meemkitVersion));
      }
      catch ( IllegalArgumentException ex )
      {
        logger.log(Level.WARNING, "setDependencies() - bad dependency specified for dependentMeemkit "+descriptor.getHeader().getName()+": "+ex.getMessage());
      }
    }

    MeemkitDependency[] dependencies = new MeemkitDependency[dependencyList.size()];
    dependencyList.toArray(dependencies);
    descriptor.setDependencies(dependencies);
  }
  
  /**
   * Set the libraries for a MeemkitDescriptor.
   * 
   * @param descriptor  The MeemkitDescriptor to update
   * @param parent      The JDOM node to process
   */
  private static void setLibraries(MeemkitDescriptor descriptor, Element parent)
  {
    if ( parent == null ) return;

    List list = parent.getChildren("library");
    if ( list == null || list.size() == 0 ) return;

    ArrayList<MeemkitLibrary> libraryList = new ArrayList<MeemkitLibrary>();

    for ( int i=0; i<list.size(); i++ )
    {
      Element element = (Element) list.get(i);
      String libraryName = getAttribute(element,"name");
      try
      {
        MeemkitLibrary meemkitLibrary = new MeemkitLibrary(libraryName);
        setLibraryExports(meemkitLibrary, element);
        libraryList.add(meemkitLibrary);
      }
      catch ( IllegalArgumentException ex )
      {
        logger.log(Level.WARNING, "setLibraries() - bad library specified "+descriptor.getHeader().getName()+": "+ex.getMessage());
      }
    }

    MeemkitLibrary[] libraries = new MeemkitLibrary[libraryList.size()];
    libraryList.toArray(libraries);
    descriptor.setLibraries(libraries);
  }

  private static void setWizardDescriptors(MeemkitDescriptor descriptor, Element parent)
  {
    if ( parent == null )
    {
      return;
    }

    List list = parent.getChildren("wizard");
    if ( list == null || list.size() == 0 ) return;

    ArrayList<MeemkitWizardDescriptor> wizardList = new ArrayList<MeemkitWizardDescriptor>();

    for ( int i=0; i<list.size(); i++ )
    {
      Element element = (Element) list.get(i);
      String text = getAttribute(element,"text");
      if ( text == null )
      {
        logger.log(Level.WARNING, "The attribute 'text' is missing in the <wizard> tag for meemkit "+descriptor.getHeader().getName());
        return;
      }
      String wizardClass = getAttribute(element,"wizardClass");
      if ( wizardClass == null )
      {
        logger.log(Level.WARNING, "The attribute 'wizardClass' is missing in the <wizard> tag for meemkit "+descriptor.getHeader().getName());
        return;
      }
      String resourceClass = getAttribute(element,"resourceClass");
      String imageFilename = getAttribute(element,"imageFilename");
      MeemkitWizardDescriptor wizardDescriptor = new MeemkitWizardDescriptor(text,wizardClass,resourceClass,imageFilename);
      wizardList.add(wizardDescriptor);
    }

    MeemkitWizardDescriptor[] wizards = new MeemkitWizardDescriptor[wizardList.size()];
    wizardList.toArray(wizards);
    descriptor.setWizardDescriptors(wizards);
  }

  /**
   * Set the library exports for a MeemkitLibrary.
   * 
   * @param meemkitLibrary  The MeemkitLibrary to update
   * @param parent      The JDOM node to process
   */
  private static void setLibraryExports(MeemkitLibrary meemkitLibrary, Element parent)
  {
    if ( parent == null ) return;

    ArrayList<MeemkitLibraryExport> exportList = new ArrayList<MeemkitLibraryExport>();
    
    List list = parent.getChildren("export");
    if ( list == null || list.size() == 0 ) 
    {
    	MeemkitLibraryExport meemkitLibraryExport = new MeemkitLibraryExport("*");
    	exportList.add(meemkitLibraryExport);
    }

    for ( int i=0; i<list.size(); i++ )
    {
      Element element = (Element) list.get(i);
      String exportValue = getAttribute(element,"value");
      try
      {
      	MeemkitLibraryExport meemkitLibraryExport = new MeemkitLibraryExport(exportValue);
      	exportList.add(meemkitLibraryExport);
      }
      catch ( IllegalArgumentException ex )
      {
        logger.log(Level.WARNING, "setLibraryExport() - bad library export specified "+meemkitLibrary.getName()+": "+ex.getMessage());
      }
    }

    MeemkitLibraryExport[] exports = new MeemkitLibraryExport[exportList.size()];
    exportList.toArray(exports);
    meemkitLibrary.setExports(exports);
  }

  /**
   * Return the normalized attribute from the JDOM element.
   * 
   * @param element  The JDOM element
   * @param name     The name of the attribute
   * @return         Returns the attribute if it is set or the empty string if it isn't
   */

  private static String getNormalizedAttribute(Element element, String name)
  {
    if ( element == null ) return "";
    Attribute attribute = element.getAttribute(name);
    return ( attribute == null ? "" : attribute.getValue() );
  }

  /**
   * Return the text from the JDOM element.
   * 
   * @param element  The JDOM element
   * @return         Returns the text if it is set or null if it isn't
   */

  private static String getText(Element element)
  {
    if ( element == null ) return null;
    String text = element.getTextNormalize();
    return ( text.length() == 0 ? null : text );
  }

  /**
   * Return the attribute from the JDOM element. If the attribute isn't set or if
   * it is set to the empty string then return a null.
   * 
   * @param element  The JDOM element
   * @param name     The name of the attribute
   * @return         Returns the attribute if it is set or null
   */

  private static String getAttribute(Element element, String name)
  {
    if ( element == null ) return null;
    Attribute attribute = element.getAttribute(name);
    if ( attribute == null ) return null;
    String text = attribute.getValue();
    return text.length() == 0 ? null : text;
  }

  /**
   * Parses a JDOM tree and constructs an array of meemkit descriptor entries. 
   * 
   * @param parent The Element node of the JDOM tree containing the meemkit descriptor entries
   * @return The array of MeemkitEntryDescriptor
   */

  private static MeemkitEntryDescriptor[] getEntries(MeemkitDescriptor descriptor, Element parent)
  {
    if ( parent == null ) return null;

    ArrayList<MeemkitEntryDescriptor> entriesList = new ArrayList<MeemkitEntryDescriptor>();
    List list = parent.getChildren("entry");
    for ( int i=0; i<list.size(); i++ )
    {
      Element element = (Element) list.get(i);
      MeemkitEntryDescriptor entry = getEntry(descriptor,element);
      if ( descriptor != null ) entriesList.add(entry);
    }

    if ( entriesList == null ) return null;
    MeemkitEntryDescriptor[] entries = new MeemkitEntryDescriptor[entriesList.size()];
    entriesList.toArray(entries);
    return entries;
  }
  
  /**
   * Parse a portion of the JDOM tree specifying a meemkit descriptor entry and contructs
   * an instance of MeemkitEntryDescriptor.
   * 
   * @param element The Element containing the meemkit descriptor entry
   * @return The MeemkitEntryDescriptor
   */

  private static MeemkitEntryDescriptor getEntry(MeemkitDescriptor descriptor, Element element)
  {
    String name = getAttribute(element,"name");
    if ( name == null )
    {
      logger.log(Level.WARNING, "getEntry() - bad meemkit descriptor, entry has no name");
      return null;
    }
    
    String path = getAttribute(element,"path");
    if ( path == null )
    {
      logger.log(Level.WARNING, "getEntry() - entry has no path for meemkit entry "+name);
      return null;
    }

    MeemkitEntryDescriptor entry = new MeemkitEntryDescriptor(name,path);
    Element description = (Element) element.getChild("description");
    if ( description == null )
    {
      logger.log(Level.WARNING, "getEntry() - name=["+name+"] - entry has no description");
      return null;
    }

    entry.setDetail(getText(description.getChild("detail")));
    entry.setIcon(getAttribute(element,"icon"));
    entry.setLargeIcon(getAttribute(element,"largeicon"));
    entry.setTitle(getAttribute(description,"title"));
    entry.setOverview(getAttribute(description,"overview"));

    MeemkitHeader header = descriptor.getHeader();
    String temp = getAttribute(description,"version");
    MeemkitVersion meemkitVersion=header.getMeemkitVersion();
    entry.setVersion(temp == null ? meemkitVersion.getVersion():temp);
    
    temp = getAttribute(description,"author");
    entry.setAuthor(temp == null ? header.getAuthor() : temp);
    temp = getAttribute(description,"company");
    entry.setCompany(temp == null ? header.getCompany() : temp);
    temp = getAttribute(description,"copyright");
    entry.setCopyright(temp == null ? header.getCopyright() : temp);
    
    
    
    Element licenseType = (Element) element.getChild("meemlicensetype");
    if ( licenseType != null )
    {
    	temp = getAttribute(licenseType,"id");
      entry.setMeemLicenseType(temp);
    } else {
    	entry.setMeemLicenseType("");
    }

    Element mdpElement = (Element) element.getChild("meemDefinitionProvider");
    List wedgeList = element.getChildren("wedge");

    if ( wedgeList.size() == 0 && mdpElement == null )
    {
      logger.log(Level.WARNING, "getEntry() - name=["+name+"] - no wedges or meemDefinitionProvider defined");
      return null;
    }

    if ( wedgeList.size() > 0 && mdpElement != null )
    {
      logger.log(Level.WARNING, "getEntry() - name=["+name+"] - can not define both wedges and meemDefinitionProvider");
      return null;
    }

    if ( mdpElement != null )
    {
      entry.setMeemDefinitionProviderClassName(getClassName(mdpElement,"class",name));
      return entry;
    }

    ArrayList<String> tempList = new ArrayList<String>();
    for ( int j=0; j<wedgeList.size(); j++ )
    {
      Element wedgeElement = (Element) wedgeList.get(j);
      tempList.add(getClassName(wedgeElement,"class",name));
    }
    String[] wedges = new String[tempList.size()];
    tempList.toArray(wedges);
    entry.setWedgeClassNames(wedges);

    return entry;
  }

  /**
   * Return the class specified as an attribute of a JDOM element. If the
   * class can't be loaded return null.
   * 
   * @param element    The JDOM element
   * @param attribute  The name of the attribute
   * @param entryName  The name of the meemkit entry declaring this class 
   * @return  The class or null
   */
  private static String getClassName(Element element, String attribute, String entryName)
  {
    String classname = getNormalizedAttribute(element,attribute);
    if ( classname.length() == 0 )
    {
      logger.log(Level.WARNING, "getClass() - name=["+entryName+"] - no class attribute specified");
      return null;
    }
    return classname;
  }
  
}
