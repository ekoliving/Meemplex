/*
 * @(#)FileImportWedge.java
 *
 * Copyright 2004 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */
 
package org.openmaji.implementation.common.importexport;

import java.io.*;
import java.net.*;

import org.openmaji.common.Binary;
import org.openmaji.meem.*;
import org.openmaji.meem.definition.MeemDefinition;
import org.openmaji.meem.wedge.configuration.*;
import org.openmaji.meem.wedge.lifecycle.*;

import org.openmaji.system.meem.definition.MeemContent;
import org.openmaji.system.space.CategoryEntry;


import org.swzoo.log2.core.*;

/**
 * @author  Andy Gelme
 * @version 1.0
 */

public class FileImportWedge
  implements Binary,  // inbound Facet for testing only
             Wedge {
    
  public File inputFile = new File("/tmp/import.meem");
  
  private boolean isIdle = true;
  
  private ObjectInputStream objectInputStream = null;
  
  private static Logger logger = LogFactory.getLogger(); 
  
/* ---------- Configuration section ---------------------------------------- */

  public ConfigurationClient configurationClientConduit =
    new ConfigurationClientAdapter(this);

  public transient ConfigurationSpecification inputFileSpecification =
    new ConfigurationSpecification("Import file path");

  public void setInputFile(
    String inputFilePath) {
      
    inputFile = new File(inputFilePath);
  }

/* ---------- ImportParameters Conduit ------------------------------------- */

  public ImportParameters importParametersConduit =
    new ImportParametersHandler();
    
  public class ImportParametersHandler implements ImportParameters {

    public void importParametersChanged(
      URL      importURL,
      MeemPath targetMeemPath) {

      FileImportWedge.this.inputFile = new File(importURL.getFile());
      
//-ag- Replace this sneaky "start mechanism" with a Task Meem start() Conduit.
      
//      start();

    }
  }

/* ---------- Binary Facet method(s) --------------------------------------- */

  public void valueChanged(
    boolean value) {
   
//-ag- Replace this sneaky "start mechanism" with a Task Meem start() Facet.

    start();   
  }

/* ---------- LifeCycle section -------------------------------------------- */

  public LifeCycleClient lifeCycleClientConduit = new LifeCycleClientAdapter(this);

  public void conclude() {
    if (objectInputStream != null) {
      try {
        objectInputStream.close();
      }
      catch (IOException ioException) {}
      finally {
        objectInputStream = null;
      }
    }
  }

/* ---------- ImportExportContentConduit ----------------------------------- */

  public ImportExportContent importExportContentConduit;

  private void start() {    
    if (isIdle) {
      try {
        isIdle = false;
      
        importMeems();
      }
      finally {
        isIdle = true;
      }
    }
  }

  public void importMeems() {
    initializeInputStream();
    
    boolean eof = false;
      
    try {
      while (eof == false) {  // objectInputStream.available() broken ?!?
        Object object = objectInputStream.readObject();
        
        if (object instanceof CategoryEntry) {
          CategoryEntry categoryEntry = (CategoryEntry) object;

          CategoryEntry categoryEntries[] =
            (CategoryEntry[]) objectInputStream.readObject();
            
            importExportContentConduit.categoryContentChanged(
              categoryEntry, categoryEntries
            );
        }
        else if (object instanceof MeemPath) {
          MeemPath meemPath = (MeemPath) object;
          
          MeemDefinition meemDefinition =
            (MeemDefinition) objectInputStream.readObject();
          
          MeemContent meemContent =
            (MeemContent) objectInputStream.readObject();

          importExportContentConduit.meemChanged(
            meemPath, meemDefinition, meemContent
          );
        }
        else {
          throw new RuntimeException(
            "Unexpected object type: " + object.getClass().getName()
          );
        }
      }
    }
    catch (EOFException eofException) {
      eof = true;

      importExportContentConduit.contentSent();
    }
    catch (Exception exception) {
      LogTools.error(
        logger, "Importing from file: " + inputFile, exception
      );

      importExportContentConduit.contentFailed(
        "Importing from file: " + inputFile
      );
    }
    finally {
      conclude();
    }
  }

  private void initializeInputStream() {
    if (objectInputStream == null) {
      try {
        objectInputStream =
          new ObjectInputStream(new FileInputStream(inputFile));
      }
      catch (IOException ioException) {
        LogTools.error(logger, "Reading file: " + inputFile, ioException);
      }
    }
  }
}