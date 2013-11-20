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

import org.openmaji.meem.*;
import org.openmaji.meem.definition.MeemDefinition;
import org.openmaji.meem.wedge.configuration.*;
import org.openmaji.meem.wedge.lifecycle.*;

import org.openmaji.system.meem.definition.MeemContent;
import org.openmaji.system.space.CategoryEntry;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author  Andy Gelme
 * @version 1.0
 */

public class FileExportWedge
  implements Wedge {
    
  public File outputFile = new File("/tmp/export.meem");
  
  private ObjectOutputStream objectOutputStream = null;
  
  private static Logger logger = Logger.getAnonymousLogger(); 
  
/* ---------- Configuration section ---------------------------------------- */

  public ConfigurationClient configurationClientConduit =
    new ConfigurationClientAdapter(this);

  public transient ConfigurationSpecification outputFileSpecification =
    new ConfigurationSpecification("Export file path");

  public void setOutputFile(
    String outputFilePath) {
      
    outputFile = new File(outputFilePath);
  }

/* ---------- ExportParameters Conduit ------------------------------------- */

  public ExportParameters exportParametersConduit =
    new ExportParametersHandler();
    
  public class ExportParametersHandler implements ExportParameters {

    public void exportParametersChanged(
      MeemPath sourceMeemPath,
      URL      exportURL,
      int      depthLimit) {

       FileExportWedge.this.outputFile = new File(exportURL.getFile());
    }
  }

/* ---------- LifeCycle section -------------------------------------------- */

  public LifeCycleClient lifeCycleClientConduit = new LifeCycleClientAdapter(this);

  public void conclude() {
    if (objectOutputStream != null) {
      try {
        objectOutputStream.close();
      }
      catch (IOException ioException) {}
      finally {
        objectOutputStream = null;
      }
    }
  }

/* ---------- ImportExportContentConduit ----------------------------------- */

  public ImportExportContent importExportContentConduit =
    new ExportContentConduitHandler();
    
  public class ExportContentConduitHandler implements ImportExportContent {
  	
  	private int meemCount = 0;
    
    public void categoryContentChanged(
      CategoryEntry categoryCategoryEntry,
      CategoryEntry[] categoryEntries) {

      initializeOutputStream();
      
      try {
        objectOutputStream.writeObject(categoryCategoryEntry);
        objectOutputStream.writeObject(categoryEntries);
      }
      catch (IOException ioException) {
        logger.log(
          Level.WARNING, "Exporting Category to file: " + outputFile, ioException          
        );

        conclude();
      }
    }

    public void meemChanged(
      MeemPath       meemPath,
      MeemDefinition meemDefinition,
      MeemContent    meemContent) {

      initializeOutputStream();
            
      try {
        objectOutputStream.writeObject(meemPath);
        objectOutputStream.writeObject(meemDefinition);
        objectOutputStream.writeObject(meemContent);
        
        meemCount++;
      }
      catch (IOException ioException) {
        logger.log(
          Level.WARNING, "Exporting Meem to file: " + outputFile, ioException
        );

        conclude();
      }
    }

    public void contentSent() {
      logger.log(Level.INFO, "Export complete: " + outputFile + " [" + meemCount + " Meems]");
      
      conclude();
    }

    public void contentFailed(
      String reason) {

      logger.log(Level.INFO, "Export failed: " + outputFile);

      conclude();
    }
  }
  
  private void initializeOutputStream() {
    if (objectOutputStream == null) {
      try {
        objectOutputStream =
          new ObjectOutputStream(new FileOutputStream(outputFile));
      }
      catch (IOException ioException) {
        logger.log(Level.WARNING, "Creating file: " + outputFile, ioException);
      }
    }
  }
}