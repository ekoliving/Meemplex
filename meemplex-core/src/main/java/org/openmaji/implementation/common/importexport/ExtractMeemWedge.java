/*
 * @(#)ExtractMeemWedge.java
 *
 * Copyright 2004 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
 
package org.openmaji.implementation.common.importexport;

import java.util.*;

import org.openmaji.meem.*;
import org.openmaji.meem.definition.*;
import org.openmaji.meem.filter.ExactMatchFilter;
import org.openmaji.meem.space.Space;
import org.openmaji.meem.wedge.dependency.DependencyClient;
import org.openmaji.meem.wedge.dependency.DependencyHandler;
import org.openmaji.meem.wedge.lifecycle.*;
import org.openmaji.meem.wedge.reference.Reference;

import org.openmaji.system.gateway.ServerGateway;
import org.openmaji.system.meem.definition.MeemContent;
import org.openmaji.system.meem.wedge.persistence.*;
import org.openmaji.system.meem.wedge.reference.MeemClientCallback;
import org.openmaji.system.meem.wedge.reference.MeemClientConduit;
import org.openmaji.system.meemserver.MeemServer;
import org.openmaji.system.space.CategoryEntry;
import org.openmaji.system.space.meemstore.*;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author  Andy Gelme
 * @version 1.0
 */

public class ExtractMeemWedge
  implements MeemDefinitionClient,
             ManagedPersistenceClient,
             Wedge, WedgeDefinitionProvider {
               
  public MeemContext meemContext;
  
  public ImportExportContent importExportContentConduit;

  private Meem meemStoreMeem = null;
  
  private Map<MeemPath,Meem> extractedMeems = null;
  
  private Vector<MeemPath> outstandingMeems = null;

  private Map<MeemPath,Meem> outstandingMeemContent = null;
  
  private Map<MeemPath,MeemContent> cachedMeemContent = null;

  private boolean gotCategoryContentSent = false;
  
  private static Logger logger = Logger.getAnonymousLogger();
  
  public MeemClientConduit meemClientConduit;
  
  public Map<Meem,DependencyAttribute> dependencyAttributes = new HashMap<Meem,DependencyAttribute>();
  public DependencyHandler dependencyHandlerConduit;
	public DependencyClient dependencyClientConduit = new DependencyClientConduit();
  
/* ---------- LifeCycle section -------------------------------------------- */

  public LifeCycleClient lifeCycleClientConduit = new LifeCycleClientAdapter(this);

  public void commence() {
    
//-ag- Deal with MeemStore failure    
    
    if (meemStoreMeem == null) {
      meemStoreMeem = ServerGateway.spi.create().getMeem(MeemPath.spi.create(Space.HYPERSPACE, MeemServer.spi.getEssentialMeemsCategoryLocation() + "/meemStore"));
    }    
    
    extractedMeems = Collections.synchronizedMap(new HashMap<MeemPath,Meem>());

//-ag- Prevent extracted self, until fixed to work correctly
    extractedMeems.put(meemContext.getSelf().getMeemPath(), null);
    
    outstandingMeemContent = Collections.synchronizedMap(new HashMap<MeemPath,Meem>());
    
    cachedMeemContent = Collections.synchronizedMap(new HashMap<MeemPath,MeemContent>());
    
    outstandingMeems = new Vector<MeemPath>();
  }
  
/* ---------- CategoryContentConduit --------------------------------------- */

  public CategoryContent categoryContentConduit = new CategoryContentHandler();

  public class CategoryContentHandler implements CategoryContent {

    public void categoryContentChanged(
      CategoryEntry categoryCategoryEntry,
      CategoryEntry categoryEntries[]) {

      importExportContentConduit.categoryContentChanged(
        categoryCategoryEntry, categoryEntries
      );
        
      for (int index = 0; index < categoryEntries.length; index ++) {
      	extractMeem(
      		categoryEntries[index].getMeem().getMeemPath(), 
      		categoryEntries[index].getMeem()
      	);
      }
    }
    
    public void contentSent() {
    	gotCategoryContentSent = true;
    	
    	if (outstandingMeems.isEmpty()) {
				importExportContentConduit.contentSent();
			}
    }
    
    public void contentFailed(
      String reason) {
        
      importExportContentConduit.contentFailed(reason);
    }
  }      
  
/* ------------------------------------------------------------------------- */

  private void extractMeem(
    MeemPath meemPath,
    Meem     meem) {
   
		if ( !extractedMeems.containsKey(meemPath) ) {
				
			extractedMeems.put(meemPath, meem);   
				
			outstandingMeems.add(meemPath);
				
			outstandingMeemContent.put(meemPath, meem);
		  
			DependencyAttribute dependencyAttribute = new DependencyAttribute(DependencyType.WEAK, Scope.LOCAL, meem, "managedPersistenceClient");
	    
	    dependencyAttributes.put(meem, dependencyAttribute);
	    
	    dependencyHandlerConduit.addDependency("persistenceClient", dependencyAttribute, LifeTime.TRANSIENT);
		}
  }

/* ---------- ManagedPersistenceClient method(s) --------------------------- */

  public void meemContentChanged(
    MeemPath    meemPath,
    MeemContent meemContent) {

    Meem meem = (Meem) outstandingMeemContent.remove(meemPath);
    
    if (meem != null) {
    	DependencyAttribute dependencyAttribute = (DependencyAttribute) dependencyAttributes.remove(meem);
      dependencyHandlerConduit.removeDependency(dependencyAttribute);

      cachedMeemContent.put(meemPath, meemContent);    

//    -ag- Replace Reference with a Dependency
     
      Reference meemDefinitionClientReference =
        Reference.spi.create(
          "meemDefinitionClient",
          meemContext.getTarget("meemDefinitionClient"),
          true,
          ExactMatchFilter.create(meemPath)
        );

      meemStoreMeem.addOutboundReference(meemDefinitionClientReference, true);
    }
  }

  public void restored(
    MeemPath meemPath) {
  }

/* ---------- MeemDefinitionClient method(s) ------------------------------- */

  public void meemDefinitionChanged(
    MeemPath       meemPath,
    MeemDefinition meemDefinition) {

    if (meemDefinition == null) {
      logger.log(
        Level.WARNING,
        "Ignoring Meem with no MeemDefinition " +
        "(it's probably the EssentialLifeCycleManager): " +
        meemPath
      );
    }
    else {
      importExportContentConduit.meemChanged(
        meemPath,
        meemDefinition,
        (MeemContent) cachedMeemContent.remove(meemPath)
      );
    }
    
    extractedMeems.put(meemPath, null);
    
    outstandingMeems.remove(meemPath);
			
		if (outstandingMeems.isEmpty() && gotCategoryContentSent) {
			importExportContentConduit.contentSent();
		}
    
  }

/* ---------- WedgeDefinitionProvider method(s) ---------------------------- */

  public WedgeDefinition getWedgeDefinition() {
    WedgeDefinition wedgeDefinition =
      WedgeDefinitionFactory.spi.create().inspectWedgeDefinition(
        this.getClass()
      );

    WedgeDefinitionUtility.renameFacetIdentifier(
      wedgeDefinition, "managedPersistenceClient", "persistenceClient"
    );
      
    return(wedgeDefinition);
  }
  
  /* ------------------- MeemClientCallback ------------------------- */
  
  class PersistCallback implements MeemClientCallback {
  	
		public void referenceProvided(Reference reference) {
			ManagedPersistenceHandler managedPersistenceHandler = (ManagedPersistenceHandler) reference.getTarget();
			managedPersistenceHandler.persist();
		}
  }
  
  /* ------------------- DependencyClient ------------------------- */
  
  class DependencyClientConduit implements DependencyClient {
		
		public void dependencyConnected(DependencyAttribute dependencyAttribute) {
			if (dependencyAttributes.containsKey(dependencyAttribute)) {
				meemClientConduit.provideReference(dependencyAttribute.getMeem(), "managedPersistenceHandler", ManagedPersistenceHandler.class, new PersistCallback());
			}

		}
		
		public void dependencyDisconnected(DependencyAttribute dependencyAttribute) {
		}
		
		public void dependencyAdded(String facetId, DependencyAttribute dependencyAttribute) {
		}
		
		public void dependencyUpdated(DependencyAttribute arg0) {
		}
		
		public void dependencyRemoved(DependencyAttribute dependencyAttribute) {
		}		
}
}