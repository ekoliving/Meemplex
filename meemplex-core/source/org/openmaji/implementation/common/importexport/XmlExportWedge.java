/*
 * @(#)FileExportWedge.java
 *
 * Copyright 2004 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
 
package org.openmaji.implementation.common.importexport;

import java.io.*;
import java.net.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.openmaji.meem.*;
import org.openmaji.meem.definition.MeemDefinition;
import org.openmaji.meem.wedge.configuration.*;
import org.openmaji.meem.wedge.lifecycle.*;

import org.openmaji.system.meem.definition.MeemContent;
import org.openmaji.system.space.CategoryEntry;

import org.swzoo.log2.core.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @author  Andy Gelme
 * @version 1.0
 */

public class XmlExportWedge
  implements Wedge {
    
  public URL outputURL = null;
  
  private Document     document;
  
  private static Logger logger = LogFactory.getLogger(); 

  
  public XmlExportWedge() {
  	try {
  		outputURL = new URL("file:/tmp/export.meem");
  	}
  	catch (MalformedURLException ex) {
  	}
  }
  
/* ---------- Configuration section ---------------------------------------- */

  public ConfigurationClient configurationClientConduit =
    new ConfigurationClientAdapter(this);

  public transient ConfigurationSpecification outputURLSpecification =
    new ConfigurationSpecification("Export URL");

  public void setOutputURL(
    String outputURL) {

  	try {
  		this.outputURL = new URL(outputURL);
  	}
  	catch (MalformedURLException ex) {
  	}
  }

/* ---------- ExportParameters Conduit ------------------------------------- */

  public ExportParameters exportParametersConduit =
    new ExportParametersHandler();
    
  public class ExportParametersHandler implements ExportParameters {

    public void exportParametersChanged(
      MeemPath sourceMeemPath,
      URL      exportURL,
      int      depthLimit) {

       XmlExportWedge.this.outputURL = exportURL;
    }
  }

/* ---------- LifeCycle section -------------------------------------------- */

  public LifeCycleClient lifeCycleClientConduit = new LifeCycleClientAdapter(this);

  public void conclude() {
  	OutputStream outputStream = null;
  	try {
	    outputStream = outputURL.openConnection().getOutputStream();
	  	Transformer transformer = TransformerFactory.newInstance().newTransformer();  	
	  	transformer.transform(new DOMSource(document), new StreamResult(outputStream));
	    outputStream.close();
  	}
  	catch (IOException ioException) {
      	LogTools.error(logger, "Problem opening or writing to URL: " + outputURL, ioException);
  	}
  	catch (TransformerConfigurationException ex) {
  		LogTools.error(logger, "Problem with XML tranformer configuration", ex);
  	}
  	catch (TransformerException ex) {
  		LogTools.error(logger, "Problem with XML tranformation", ex);  		
  	}
  	finally {
        outputStream = null;
  	}
  }

/* ---------- ImportExportContentConduit ----------------------------------- */

  public ImportExportContent importExportContentConduit =
    new ExportContentConduitHandler();
    
  public class ExportContentConduitHandler implements ImportExportContent {
  	
  	private int meemCount = 0;
    
    public void categoryContentChanged(
      CategoryEntry categoryCategoryEntry,
      CategoryEntry[] categoryEntries) 
    {

      initializeOutputDocument();
      
      document.appendChild( buildNode(categoryCategoryEntry) );
      document.appendChild( buildNode(categoryEntries) );	
    }

    public void meemChanged(
      MeemPath       meemPath,
      MeemDefinition meemDefinition,
      MeemContent    meemContent) 
    {

      initializeOutputDocument();
            
	  Node node = buildNode(meemPath, meemDefinition, meemContent);
	  document.appendChild(node);
    }

    public void contentSent() {
      LogTools.info(logger, "Export complete: " + outputURL+ " [" + meemCount + " Meems]");
      
      conclude();
    }

    public void contentFailed(String reason) {

      LogTools.info(logger, "Export failed: " + outputURL);

      conclude();
    }
  }
  
  private void initializeOutputDocument() {
  	if (document == null) {
	  	try {
		  	DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		  	document = documentBuilder.newDocument();  	
	
	  	}
	  	catch (ParserConfigurationException ex) {
	  		LogTools.error(logger, "Could not create export document.", ex);
	  	}
  	}
  }
  
  private Node buildNode(CategoryEntry entry) {
  	Node entryNode = document.createElement("category-entry");
  	
  	Element nameNode  = document.createElement("name");
  	nameNode.appendChild( document.createTextNode(entry.getName()) );
  	
  	Element  pathNode    = document.createElement("meem-path");
  	MeemPath meemPath    = entry.getMeem().getMeemPath();
  	String   meemPathStr = meemPath.getSpace().getType() + ":" + meemPath.getLocation();
  	pathNode.appendChild( document.createTextNode(meemPathStr) );
  	
  	entryNode.appendChild(nameNode);
  	entryNode.appendChild(pathNode);
  	return entryNode;
  }
  
  private Node buildNode(CategoryEntry[] entries) {
  	Node entriesNode = document.createElement("category-entries");
  	
  	for (int i=0; i<entries.length; i++) {
  		Node entryNode = buildNode(entries[i]);
  		entriesNode.appendChild(entryNode);
  	}
  	
  	return entriesNode;
  }
  
  private Node buildNode(MeemPath meemPath, MeemDefinition meemDefinition, MeemContent meemContent) {
  	Element  pathNode    = document.createElement("meem-path");
  	String   meemPathStr = meemPath.getSpace().getType() + ":" + meemPath.getLocation();
  	pathNode.appendChild( document.createTextNode(meemPathStr) );
  	
  	//Element meemDefNode = document.createElement("meem-definition");

  	//Element meemContentNode = document.createElement("meem-content");
  	
  	return null;
  }
}