/*
 * @(#)CategoryTraverserWedge.java
 *
 * Copyright 2004 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.common.importexport;

/**
 * @author  Andy Gelme
 * @version 1.0
 */

import java.net.URL;
import java.util.*;

import org.openmaji.common.Binary;
import org.openmaji.meem.*;
import org.openmaji.meem.definition.*;
import org.openmaji.meem.space.Space;
import org.openmaji.meem.wedge.configuration.*;
import org.openmaji.meem.wedge.dependency.*;

import org.openmaji.system.meem.wedge.reference.ContentClient;
import org.openmaji.system.space.*;
import org.openmaji.system.utility.MeemUtility;


public class CategoryTraverserWedge
  implements CategoryClient,  // inbound Facet
             Binary,          // inbound Facet for testing only
             ContentClient,
             Wedge, WedgeDefinitionProvider {

  public MeemContext meemContext;
  
  public CategoryContent categoryContentConduit;               

  public int depthLimit = 16;
  
  public MeemPath initialMeemPath = MeemPath.spi.create(Space.HYPERSPACE, "/");
  
  private List<CategoryEntry> categoryQueue = Collections.synchronizedList(new LinkedList<CategoryEntry>());

  private CategoryEntry categoryEntries[] = null;
  
  private DependencyAttribute dependencyAttribute = null;
  
  private boolean isIdle = true;
  
/* ---------- Configuration section ---------------------------------------- */

  public ConfigurationClient configurationClientConduit =
    new ConfigurationClientAdapter(this);

  public transient ConfigurationSpecification depthLimitSpecification =
    new ConfigurationSpecification(
      "Maximum depth of Categories to descend", Integer.class
    );

  public void setDepthLimit(
    Integer depthLimitInteger) {

    depthLimit = depthLimitInteger.intValue();      
  }

  public transient ConfigurationSpecification initialMeemPathSpecification =
    new ConfigurationSpecification("HyperSpace MeemPath to export");

  public void setInitialMeemPath(String initialMeemPathString) {

    int position = initialMeemPathString.indexOf(':') + 1;
    
    if (position > 0) {
      initialMeemPathString = initialMeemPathString.substring(position); 
    }
        
    initialMeemPath =
      MeemPath.spi.create(Space.HYPERSPACE, initialMeemPathString);
  }

/* ---------- ExportParameters Conduit ------------------------------------- */

  public ExportParameters exportParametersConduit =
    new ExportParametersHandler();
    
  public class ExportParametersHandler implements ExportParameters {

    public void exportParametersChanged(
      MeemPath sourceMeemPath,
      URL      exportURL,
      int      depthLimit) {
        
      CategoryTraverserWedge.this.depthLimit      = depthLimit;
      CategoryTraverserWedge.this.initialMeemPath = sourceMeemPath;

//-ag- Replace this sneaky "start mechanism" with a Task Meem start() Conduit.

      start();      
    }
  }

/* ---------- Binary Facet method(s) --------------------------------------- */

  public void valueChanged(boolean value) {

//-ag- Replace this sneaky "start mechanism" with a Task Meem start() Facet.

    start();      
  }

/* ------------------------------------------------------------------------- */

  private void start() {
    if (isIdle) {
      isIdle = false;
      
      categoryQueue.add(
        new CategoryEntry(
          initialMeemPath.toString(), Meem.spi.get(initialMeemPath)
        )
      );
      
      categoryTraverser();
    }
  }
  
  private void categoryTraverser() {
    if (categoryQueue.isEmpty()) {
      categoryContentConduit.contentSent();

      isIdle = true;
    }
    else {
//-ag- If the "outstanding MeemPath" is not a Category, then just list it !

      CategoryEntry categoryEntry = (CategoryEntry) categoryQueue.get(0);
      
//      String name = categoryEntry.getName();
//      
//      MeemPath meemPath = categoryEntry.getMeem().getMeemPath();

//-ag- Should be able to define whether "contentRequired" or not.

      dependencyAttribute = new DependencyAttribute(
        DependencyType.WEAK,
        Scope.LOCAL,
        categoryEntry.getMeem(),
        "categoryClient"
      );

      dependencyHandlerConduit.addDependency(
        "categoryInput", dependencyAttribute, LifeTime.TRANSIENT
      );
    }
  }
  
/* ---------- CategoryClient Facet method(s) ------------------------------- */

  public void entriesAdded(CategoryEntry[] newEntries) {

    categoryEntries = newEntries;
  }

  public void entriesRemoved(CategoryEntry[] removedEntries) {
  }

  public void entryRenamed(
    CategoryEntry oldEntry,
    CategoryEntry newEntry) 
  {
  }

/* ---------- ContentClient method(s) -------------------------------------- */

//-ag- Need to time-out if request not fulfilled in time.

  public void contentSent() {
  	
    if (categoryEntries != null) {
      CategoryEntry categoryCategoryEntry = (CategoryEntry) categoryQueue.remove(0);
      
      String categoryName = categoryCategoryEntry.getName();
      
//      MeemPath categoryMeemPath = categoryCategoryEntry.getMeem().getMeemPath();
      
      for (int index = 0; index < categoryEntries.length; index ++) {
        CategoryEntry meemCategoryEntry = categoryEntries[index];

        MeemPath meemPath = meemCategoryEntry.getMeem().getMeemPath();

        Meem meem = Meem.spi.get(meemPath);
        
        if (meemContext.getSelf().getMeemPath().equals(meemPath) == false) {        
	        if (MeemUtility.spi.get().hasA(
	              meem,
	              "categoryClient",
	              CategoryClient.class,
	              Direction.OUTBOUND)) 
	        {	                
	          if (categoryName.endsWith("/") == false) categoryName += "/";
	
	          categoryQueue.add(
	            new CategoryEntry(
	              categoryName + meemCategoryEntry.getName(),
	              meemCategoryEntry.getMeem()
	            )
	          );
	        }
        }
      }

      categoryContentConduit.categoryContentChanged(
        categoryCategoryEntry, categoryEntries
      );

      categoryEntries = null;
    }
  }

  public void contentFailed(
    String reason) {

    categoryQueue.clear();

    categoryEntries = null;    

    categoryContentConduit.contentFailed(reason);
    
    throw new RuntimeException(
      "ContentClient.contentFailed() reason: " + reason
    );
  }

/* ---------- DependencyHandler section ------------------------------------ */

  public DependencyHandler dependencyHandlerConduit;  
  
  public DependencyClient dependencyClientConduit =
    new DependencyClientHandler();
    
  public class DependencyClientHandler implements DependencyClient {

    public void dependencyAdded(
      String facetId, DependencyAttribute dependencyAttribute) {
    }

    public void dependencyRemoved(
      DependencyAttribute dependencyAttribute) {
    }
    
    public void dependencyUpdated(DependencyAttribute dependencyAttribute) {
    }
    
    public void dependencyConnected(
      DependencyAttribute dependencyAttribute) {

      if (CategoryTraverserWedge.this.dependencyAttribute == dependencyAttribute) {
        dependencyHandlerConduit.removeDependency(dependencyAttribute);
      }
    }
    
    public void dependencyDisconnected(
      DependencyAttribute dependencyAttribute) {

      if (CategoryTraverserWedge.this.dependencyAttribute == dependencyAttribute) {
        CategoryTraverserWedge.this.dependencyAttribute = null;

        categoryTraverser();
      }
    }
  }

/* ---------- WedgeDefinitionProvider method(s) ---------------------------- */

  public WedgeDefinition getWedgeDefinition() {
    WedgeDefinition wedgeDefinition =
      WedgeDefinitionFactory.spi.create().inspectWedgeDefinition(
        this.getClass()
      );

    WedgeDefinitionUtility.renameFacetIdentifier(
      wedgeDefinition, "categoryClient", "categoryInput"
    );
      
    return(wedgeDefinition);
  }
}