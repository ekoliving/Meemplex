/*
 * @(#)InjectMeemWedge.java
 *
 * Copyright 2004 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
 
package org.openmaji.implementation.common.importexport;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

import org.openmaji.implementation.server.utility.ObjectUtility;
import org.openmaji.meem.*;
import org.openmaji.meem.definition.*;
import org.openmaji.meem.filter.ExactMatchFilter;
import org.openmaji.meem.space.Space;
import org.openmaji.meem.wedge.configuration.*;
import org.openmaji.meem.wedge.dependency.DependencyHandler;
import org.openmaji.meem.wedge.lifecycle.*;
import org.openmaji.meem.wedge.reference.Reference;

import org.openmaji.system.gateway.ServerGateway;
import org.openmaji.system.manager.lifecycle.LifeCycleManager;
import org.openmaji.system.manager.lifecycle.LifeCycleManagerClient;
import org.openmaji.system.meem.definition.MeemContent;
import org.openmaji.system.meemserver.MeemServer;
import org.openmaji.system.space.*;
import org.openmaji.system.space.hyperspace.StandardHyperSpaceCategory;
import org.openmaji.system.space.meemstore.MeemDefinitionClient;
import org.openmaji.system.space.meemstore.MeemStore;
import org.openmaji.system.utility.*;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author  Andy Gelme
 * @version 1.0
 */

public class InjectMeemWedge
  implements Wedge, LifeCycleManagerClient {

  public MeemPath initialMeemPath = MeemPath.spi.create(Space.HYPERSPACE, "/");

  private Meem meemStoreMeem = null;
  
  private MeemStore meemStore = null;              
  
  private Meem worksheetLifeCycleManagerMeem = null;
  
  private LifeCycleManager worksheetLifeCycleManager = null;              
  
  public MeemContext meemContext;
  
  private boolean isIdle = true;
  
  private int totalMeemCount = 0;
  
  private int transferredMeemCount = 0;
  
  private CategoryEntry[] categoryEntries = null;
  
  private int meemCount = 0;
  
  public DependencyHandler dependencyHandlerConduit;
  
  private static Logger logger = Logger.getAnonymousLogger();  
  
/* ---------- Configuration section ---------------------------------------- */

  public ConfigurationClient configurationClientConduit =
    new ConfigurationClientAdapter(this);

  public transient ConfigurationSpecification initialMeemPathSpecification =
    ConfigurationSpecification.create("HyperSpace MeemPath to import");

  public void setInitialMeemPath(
    String initialMeemPathString) {

    int position = initialMeemPathString.indexOf(':') + 1;
    
    if (position > 0) {
      initialMeemPathString = initialMeemPathString.substring(position); 
    }
        
    initialMeemPath =
      MeemPath.spi.create(Space.HYPERSPACE, initialMeemPathString);
  }

/* ---------- ImportParameters Conduit ------------------------------------- */

  public ImportParameters importParametersConduit =
    new ImportParametersHandler();
    
  public class ImportParametersHandler implements ImportParameters {

    public void importParametersChanged(
      URL      importURL,
      MeemPath targetMeemPath) {

      InjectMeemWedge.this.initialMeemPath = targetMeemPath;
    }
  }

/* ---------- LifeCycle section -------------------------------------------- */

  public LifeCycleClient lifeCycleClientConduit = new LifeCycleClientAdapter(this);

  public void commence() {
    isIdle = true;
    
//-ag- Deal with MeemStore failure    
    
    if (meemStoreMeem == null) {
        meemStoreMeem = ServerGateway.spi.create().getMeem(MeemPath.spi.create(Space.HYPERSPACE, MeemServer.spi.getEssentialMeemsCategoryLocation() + "/meemStore"));

      meemStore = (MeemStore) MeemUtility.spi.get().getTarget(
        meemStoreMeem, "meemStore", MeemStore.class
      );
    }  
    
    if (worksheetLifeCycleManagerMeem == null) {
    	String meemServerName = MeemServer.spi.getName();
    	
    	String worksheetLCMLocation = StandardHyperSpaceCategory.DEPLOYMENT + "/" + meemServerName + "/worksheet";
    	
    	MeemPath meemPath = MeemPath.spi.create(Space.HYPERSPACE, worksheetLCMLocation);
    	worksheetLifeCycleManagerMeem = Meem.spi.get(meemPath);

    	worksheetLifeCycleManager = 
    		(LifeCycleManager) MeemUtility.spi.get().getTarget(
    			worksheetLifeCycleManagerMeem, 
				"lifeCycleManager", 
				LifeCycleManager.class
    		);
      
    	DependencyAttribute dependencyAttributeLCMClient = new DependencyAttribute(DependencyType.STRONG, Scope.LOCAL, worksheetLifeCycleManagerMeem, "lifeCycleManagerClient", null, false);
    	dependencyHandlerConduit.addDependency("lifeCycleManagerClient", dependencyAttributeLCMClient, LifeTime.TRANSIENT);
	  
    }    
  }
  
/* ---------- LifeCycleManagerClient --------------------------------------- */

	/**
	 * @see org.openmaji.system.manager.lifecycle.LifeCycleManagerClient#meemCreated(org.openmaji.meem.Meem, java.lang.String)
	 */
	public void meemCreated(Meem arg0, String arg1) {
		// don't care	
	}
	
	/**
	 * @see org.openmaji.system.manager.lifecycle.LifeCycleManagerClient#meemDestroyed(org.openmaji.meem.Meem)
	 */
	public void meemDestroyed(Meem arg0) {
		// don't care	
	}
	
	/**
	 * @see org.openmaji.system.manager.lifecycle.LifeCycleManagerClient#meemTransferred(org.openmaji.meem.Meem, org.openmaji.system.manager.lifecycle.LifeCycleManager)
	 */
	public void meemTransferred(Meem meem, LifeCycleManager lifeCycleManager) {
		transferredMeemCount++;
		
		if (meemCount == transferredMeemCount) {
			createCategoryEntries();
		}
	
	}
	
	/**
	 *  We have to create the category entries after all the meems have been imported
	 */
	private void createCategoryEntries() {
		Category category = CategoryUtility.spi.get().getCategory(Meem.spi.get(initialMeemPath));
              
		for (int index = 0; index < categoryEntries.length; index ++) {
			category.addEntry(
					categoryEntries[index].getName(), categoryEntries[index].getMeem()
			);
		}
    
		logger.log(Level.INFO, "Import complete [" + meemCount + " of "+ totalMeemCount + " Meems]");
	}

  
/* ---------- ImportExportContentConduit ----------------------------------- */

  public ImportExportContent importExportContentConduit =
    new ImportContentConduitHandler();
    
  public class ImportContentConduitHandler implements ImportExportContent {
    
    public void categoryContentChanged(
      CategoryEntry categoryCategoryEntry,
      CategoryEntry[] categoryEntries) {
                
      if (isIdle) {  // Only inject the top-level Category
        isIdle = false;
        
        InjectMeemWedge.this.categoryEntries = categoryEntries;
      }
    }

    public void meemChanged(
      MeemPath       meemPath,
      MeemDefinition meemDefinition,
      MeemContent    meemContent) {

      isIdle = false;

      if (meemDefinition == null) {
        logger.log(
          Level.WARNING,
          "Ignoring Meem with no MeemDefinition " +
          "(it's probably the EssentialLifeCycleManager): " +
          meemPath
        );
      }
      else {
      	totalMeemCount++;
      	
      	// check to see if this meemPath is already in the MeemStore
      	MeemDefinitionClient client = new MeemDefinitionClientImpl(meemPath, meemDefinition, meemContent);
      	
      	Facet proxy = meemContext.getTargetFor(client, MeemDefinitionClient.class);
      	
      	ExactMatchFilter filter = ExactMatchFilter.create(meemPath);
		
      	Reference reference = Reference.spi.create("meemDefinitionClient", proxy, true, filter);
		
      	meemStoreMeem.addOutboundReference(reference, true);
      }
    }

    public void contentSent() {      
      isIdle = true;
    }

    public void contentFailed(
      String reason) {

      logger.log(Level.INFO, "Import failed: " + reason);

      isIdle = true;
    }
  }
  
  public class MeemDefinitionClientImpl implements MeemDefinitionClient {
  	
  	private final MeemPath meemPath; 
  	private final MeemDefinition meemDefinition; 
  	private final MeemContent meemContent;
  	
  	public MeemDefinitionClientImpl(MeemPath meemPath, MeemDefinition meemDefinition, MeemContent meemContent) {
  		this.meemPath       = meemPath;
  		this.meemDefinition = adapt(meemDefinition);
  		this.meemContent    = meemContent;			// TODO adapt MeemContent to any changes in MeemDefinition
  	}

  	
  	/**
	 * @see org.openmaji.system.space.meemstore.MeemDefinitionClient#meemDefinitionChanged(org.openmaji.meem.MeemPath, org.openmaji.meem.definition.MeemDefinition)
	 */
	public void meemDefinitionChanged(MeemPath meemPath, MeemDefinition meemDefinition) {
		if (meemDefinition == null) {
			meemCount++;

		   	// TODO use the Meem's parent LCM id to determine the LCM to transfer to
//			Map fieldMap = this.meemContent.getPersistentFields("lifeCycleWedge");
//			MeemPath parentMeemPath = (MeemPath) fieldMap.get("parentLifeCycleManagerMeemPath");
			// TODO check if parent LCM Meem exists. If not add to generic LCM.

			meemStore.storeMeemDefinition(this.meemPath, this.meemDefinition);

		   	meemStore.storeMeemContent(this.meemPath, this.meemContent);
		   	
		   	worksheetLifeCycleManager.transferMeem(Meem.spi.get(this.meemPath), worksheetLifeCycleManager);
		} 
		else {
			logger.log(Level.WARNING, "MeemDefinition for " + this.meemPath + " already in MeemStore. Ignoring");
		}
	}
	
	/**
	 * Adapt the MeemDefinition to the current instances of Wedge implementation classes.
	 * 
	 * Match Wedge implementation class Facet interfaces with inbound facet definitions.
	 * Match Wedge outbound fields with outbound facet definition attribute.
	 * 
	 */
  	private MeemDefinition adapt(MeemDefinition meemDefinition) {
  		
  		// get the Wedge implementation classes
  		Class[] classes = getWedgeClasses(meemDefinition);

  		// create a new MeemDefinition based on original MeemDefinition Wedge classes
  		MeemDefinition newMeemDefinition = MeemDefinitionFactory.spi.create().createMeemDefinition(classes);
  		
  		MeemAttribute meemAttribute = meemDefinition.getMeemAttribute();

  		// up the Meem version number
  		meemAttribute.setVersion(meemAttribute.getVersion()+1);
  		
  		newMeemDefinition.setMeemAttribute(meemAttribute);
  		  		
  		// merge the old MeemDefinition with the new
  		merge(newMeemDefinition, meemDefinition);
  
//		logger.log(Level.INFO, "new meemDefnition: " + newMeemDefinition);					

  		return newMeemDefinition;
  	}
  	
  	
  	private Class<?>[] getWedgeClasses(MeemDefinition meemDefinition)  {
  		List<Class<?>> classVector = new ArrayList<Class<?>>();
  		for (WedgeDefinition wedgeDefinition : meemDefinition.getWedgeDefinitions()) {
  			String className = wedgeDefinition.getWedgeAttribute().getImplementationClassName();
  			
  			try {
	  			Class<?> cls = ObjectUtility.getClass(Object.class, className);
	  			classVector.add(cls);
  			}
  			catch (ClassNotFoundException ex) {
  				logger.log(
  						Level.WARNING, 
  						"Wedge class \"" + className + "\" not found.  " +
						"It will not be included in the MeemDefinition for Meem " + meemPath
					);
  			}
  			catch (IllegalArgumentException ex) {
  				logger.log(
  						Level.WARNING, 
  						"Wedge class \"" + className + "\" not found.  " +
						"It will not be included in the MeemDefinition for Meem " + meemPath
					);
  			}
  		}
  		
  		// get list of wedge classes
  		Class<?>[] classes = new Class[classVector.size()];
  		for (int i=0; i<classes.length; i++) {
  			classes[i] = classVector.get(i); 
  		}

  		return classes;
  	}
  	
  	/**
  	 * Merge the second MeemDefinition with the first.  
  	 * 
  	 * @param meemDefinition1
  	 * @param meemDefinition2
  	 */
  	private void merge(MeemDefinition meemDefinition1, MeemDefinition meemDefinition2) {
  		
  		List<WedgeDefinition> wedgeDefinitions2 = new ArrayList<WedgeDefinition>();
  		Iterator<WedgeDefinition> wedgeDefIter = meemDefinition2.getWedgeDefinitions().iterator();
  		while (wedgeDefIter.hasNext()) {
  			wedgeDefinitions2.add(wedgeDefIter.next());
  		}

  		wedgeDefIter = meemDefinition1.getWedgeDefinitions().iterator();
  		while (wedgeDefIter.hasNext()) {
  			WedgeDefinition wedgeDefinition1 = (WedgeDefinition) wedgeDefIter.next();
  			String className = wedgeDefinition1.getWedgeAttribute().getImplementationClassName();

  			WedgeDefinition wedgeDefinition2 = removeFirst(className, wedgeDefinitions2);
  			
  			merge(wedgeDefinition1, wedgeDefinition2);
  		}
  	}
  	
  	/**
  	 * Remove from the List and return the first WedgeDefinition with the given implementation classname.
  	 * 
  	 * @param className
  	 * @param wedgeDefinitions
  	 */
  	private WedgeDefinition removeFirst(String className, List wedgeDefinitions) {
  		ListIterator iter = wedgeDefinitions.listIterator();
  		while (iter.hasNext()) {
  			WedgeDefinition wedgeDefinition = (WedgeDefinition) iter.next();
  			if ( className.equals( wedgeDefinition.getWedgeAttribute().getImplementationClassName() ) ) {
  				iter.remove();
  				return wedgeDefinition;
  			}
  		}
  		return null;
  	}
  	
  	/**
  	 * Merge the second WedgeDefinition with the first.  
  	 * 
  	 * TODO If there are any duplicate Facet names, rename Facets in the first WedgeDefinition.
  	 * 
  	 * @param wedgeDefinition1
  	 * @param wedgeDefinition2
  	 */
  	private void merge(WedgeDefinition wedgeDefinition1, WedgeDefinition wedgeDefinition2) {

		//String className = wedgeDefinition1.getWedgeAttribute().getImplementationClassName();
		
		// set the wedge identifier of the wedgeDefinition1 to that of wedgeDefinition2
		wedgeDefinition1.getWedgeAttribute().setIdentifier(
				wedgeDefinition2.getWedgeAttribute().getIdentifier()
			);

		Iterator facetDefs = wedgeDefinition1.getFacetDefinitions().iterator();
		while (facetDefs.hasNext()) {
			FacetDefinition facetDefinition = (FacetDefinition) facetDefs.next();
			
			FacetAttribute facetAttribute = facetDefinition.getFacetAttribute();
			if (facetAttribute instanceof FacetInboundAttribute) {
				FacetInboundAttribute inboundAttribute = (FacetInboundAttribute) facetAttribute;
				
				// check if a FacetDefinition for the Facet interface exists
				FacetDefinition fd =
					getFacetDefinitionForInterface(wedgeDefinition2, inboundAttribute.getInterfaceName());
				if (fd != null) {
					facetDefinition.setFacetAttribute(fd.getFacetAttribute());
//					logger.log(Level.INFO, "merged inbound facet: " + facetDefinition);
				}
				else {
//					logger.log(Level.INFO, "new inbound facet: " + facetDefinition);					
				}
			}
			else if (facetAttribute instanceof FacetOutboundAttribute) {
				FacetOutboundAttribute outboundAttribute = (FacetOutboundAttribute) facetAttribute;

				// check if the Wedge public field exists for the outbound Facet
				FacetDefinition fd = 
					getFacetDefinitionForField(wedgeDefinition2, outboundAttribute.getWedgePublicFieldName());
				if (fd != null) {
					facetDefinition.setFacetAttribute(fd.getFacetAttribute());
//					logger.log(Level.INFO, "merged outbound facet: " + facetDefinition);
				}
				else {
//					logger.log(Level.INFO, "new outbound facet: " + facetDefinition);					
				}
			}
		}

		// TODO eliminate duplicate Facet identifiers

  	}

  	private FacetDefinition getFacetDefinitionForInterface(WedgeDefinition wedgeDefinition, String interfaceName) {
		Iterator facetDefs = wedgeDefinition.getFacetDefinitions().iterator();
		while (facetDefs.hasNext()) {
			FacetDefinition facetDefinition = (FacetDefinition) facetDefs.next();

			FacetAttribute facetAttribute = facetDefinition.getFacetAttribute();
			if (facetAttribute instanceof FacetInboundAttribute) {
				FacetInboundAttribute inboundAttribute = (FacetInboundAttribute) facetAttribute;
				if ( interfaceName.equals(inboundAttribute.getInterfaceName()) ) {
					return facetDefinition;
				}
			}
		}
		return null;
  	}

  	private FacetDefinition getFacetDefinitionForField(WedgeDefinition wedgeDefinition, String fieldName) {
		Iterator facetDefs = wedgeDefinition.getFacetDefinitions().iterator();
		while (facetDefs.hasNext()) {
			FacetDefinition facetDefinition = (FacetDefinition) facetDefs.next();
			
			FacetAttribute facetAttribute = facetDefinition.getFacetAttribute();
			if (facetAttribute instanceof FacetOutboundAttribute) {
				FacetOutboundAttribute outboundAttribute = (FacetOutboundAttribute) facetAttribute;
				if ( fieldName.equals(outboundAttribute.getWedgePublicFieldName()) ) {
					return facetDefinition;
				}
			}
		}
		return null;
  	}

  }
}