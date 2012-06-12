package org.openmaji.implementation.server.space.meemstore.jpa;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;

import org.meemplex.service.model.Direction;
import org.openmaji.implementation.server.Common;
import org.openmaji.implementation.server.meem.definition.WedgeIntrospector;
import org.openmaji.implementation.server.meem.definition.WedgeIntrospectorException;
import org.openmaji.implementation.server.space.meemstore.MeemStoreWedge;
import org.openmaji.implementation.server.space.meemstore.definition.MeemStoreDefinitionStore;
import org.openmaji.meem.MeemPath;
import org.openmaji.meem.definition.FacetAttribute;
import org.openmaji.meem.definition.FacetDefinition;
import org.openmaji.meem.definition.FacetInboundAttribute;
import org.openmaji.meem.definition.FacetOutboundAttribute;
import org.openmaji.meem.definition.MeemAttribute;
import org.openmaji.meem.definition.MeemDefinition;
import org.openmaji.meem.definition.Scope;
import org.openmaji.meem.definition.WedgeAttribute;
import org.openmaji.meem.definition.WedgeDefinition;
import org.openmaji.meem.space.Space;
import org.openmaji.system.space.meemstore.MeemStore;

/*

 TODO instead of serializing MeemDefinition, break up into:
 Meem
 UID meemId
 String identifier
 Scope scope
 int version

 Key = (meemId)

 ImmutableAttributes
 UID meemId
 String Name
 Object Value

 Key = (meemId, Name)

 WedgeDefinition
 UID meemId
 String wedgeId;
 String className;
 boolean isSystemWedge;

 Key = (meemId, wedgeId)

 Persistent Fields
 UID meemId
 String wedgeId;
 String fieldName

 Key = (meemId, wedgeId, fieldName)

 Facet Definition
 UID meemId
 String wedgeId;
 String facetId
 String interfaceName

 Key = (meemId, wedgeId, facetId)
 */

/**
 * <p>
 * ...
 * </p>
 * 
 * @author stormboy
 * @version 1.0
 */
public class JpaDefinitionStore implements MeemStoreDefinitionStore {

	static private final Logger logger = Logger.getAnonymousLogger();

	private String driver = "org.apache.derby.jdbc.EmbeddedDriver";

	private String database = "meemstore";

	private String url = "jdbc:derby:" + database + ";create=true";

	private String username = null;

	private String password = null;

	private static boolean DEBUG = false;

	/**
	 * configure this meemstore
	 */
	public void configure(MeemStore meemStore, Properties properties) {
		if (DEBUG) {
			logger.log(Level.INFO, "Configure");
		}

		this.database = properties.getProperty(MeemStoreWedge.MEEMSTORE_LOCATION);
		if (this.database == null) {
			this.database = "meemstore";
		}
		this.url = "jdbc:derby:" + database + ";create=true";

		HashMap<String, String> myProperties = new HashMap<String, String>();
		myProperties.put("javax.persistence.jdbc.driver", driver);
		myProperties.put("javax.persistence.jdbc.url", url);
		myProperties.put("javax.persistence.jdbc.user", username);
		myProperties.put("javax.persistence.jdbc.password", password);
		myProperties.put("eclipselink.ddl-generation", "create-tables");
		/*
		myProperties.put("toplink.jdbc.driver", driver);
		myProperties.put("toplink.jdbc.url", url);
		myProperties.put("toplink.jdbc.user", username);
		myProperties.put("toplink.jdbc.password", password);
		*/

		PersistenceContext.instance().config(myProperties);
	}

	public void close() {
		if (DEBUG) {
			logger.log(Level.INFO, "Closing");
		}

		PersistenceContext.instance().close();
	}

	/**
	 */
	public MeemDefinition load(MeemPath meemPath) {
		
		if (DEBUG) {
			logger.log(Level.INFO, "Loading MeemDefinition for: " + meemPath);
		}

		MeemDefinition definition = null;

		// This classloader change is to allows classes loaded by eclipse to
		// perform Class.forName()
		ClassLoader previousClassLoader = Thread.currentThread().getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(getClass().getClassLoader());

			EntityManager em = PersistenceContext.instance().getEntityManager();
			em.getTransaction().begin();
			MeemEntity meemEntity = em.find(MeemEntity.class, meemPath.getLocation());
			if (meemEntity != null) {
				//definition = toMeemDefinition(meemEntity);
				// TODO construct MeemDefinition from Entities
				
				definition = meemEntity.getDefinition();
			}
			em.getTransaction().commit();
		}
		finally {
			PersistenceContext.instance().release();
			Thread.currentThread().setContextClassLoader(previousClassLoader);
		}

		return definition;
	}

	public void store(MeemPath meemPath, MeemDefinition definition) {

		if (DEBUG) {
			logger.log(Level.INFO, "storing MeemDefinition for : " + meemPath);
		}
		
		try {
			EntityManager em = PersistenceContext.instance().getEntityManager();
			em.getTransaction().begin();
			MeemEntity meemEntity = em.find(MeemEntity.class, meemPath.getLocation());
			if (meemEntity == null) {
				meemEntity = new MeemEntity();
				meemEntity.setId(meemPath.getLocation());
				em.persist(meemEntity);
			}
			
			MeemAttribute meemAttribute = definition.getMeemAttribute();
			meemEntity.setName(meemAttribute.getIdentifier());
			meemEntity.setKey(""+meemAttribute.getKey());
			meemEntity.setScope(meemAttribute.getScope().getIdentifier());
			meemEntity.setDefinition(definition);
			
			// make sure we have wedges
			if (meemEntity.getWedges() == null) {
				meemEntity.setWedges(new HashMap<String, WedgeEntity>());
			}
			
			Map<String, WedgeEntity> remaining = new HashMap<String, WedgeEntity>(meemEntity.getWedges());
			
			// update wedges
			int wedgeIndex = 0;
			Collection<WedgeDefinition> wedgeDefs = definition.getWedgeDefinitions();
			for (WedgeDefinition wedgeDef : wedgeDefs) {
				String wedgeName = wedgeDef.getWedgeAttribute().getIdentifier();
				
				WedgeEntity wedgeEntity = meemEntity.getWedge(wedgeName);
				if (wedgeEntity == null) {
					wedgeEntity = new WedgeEntity();
					wedgeEntity.setMeem(meemEntity);
					wedgeEntity.setName(wedgeName);
					//wedgeEntity.setClassname(wedgeDef.getWedgeAttribute().getImplementationClassName());
					meemEntity.getWedges().put(wedgeName, wedgeEntity);
					em.persist(wedgeEntity);
				}
				wedgeEntity.setClassname(wedgeDef.getWedgeAttribute().getImplementationClassName());
				wedgeEntity.setSystemWedge(wedgeDef.getWedgeAttribute().isSystemWedge());
				wedgeEntity.setSortIndex(wedgeIndex++);
				remaining.remove(wedgeName);	// make sure the wedge is removed from remaining list
				
//				Collection<String> fieldNames = wedgeDef.getWedgeAttribute().getPersistentFields();
//				for (String fieldName : fieldNames) {
//					wedgeEntity.setContentItem(fieldName, value);
//					wedgeEntity.getContentItem(fieldName);
//					ContentEntity contentItem = wedgeEntity.getContentItem(fieldName);
//				}

				// store facets
				
				if (wedgeEntity.getFacets() == null) {
					wedgeEntity.setFacets(new ArrayList<FacetEntity>());
				}
				List<FacetEntity> facetEntities = wedgeEntity.getFacets();

				int facetIndex = 0;
				Collection<FacetDefinition> facetDefs = wedgeDef.getFacetDefinitions();
				for (FacetDefinition facetDef : facetDefs) {
					boolean isNewFacet = false;
					FacetEntity facetEntity = getFacetEntity(facetDef.getFacetAttribute().getIdentifier(), facetEntities);
					if (facetEntity == null) {
						isNewFacet = true;
						facetEntity = new FacetEntity();
						facetEntity.setFacetName(facetDef.getFacetAttribute().getIdentifier());
					}
					
					FacetAttribute attr = facetDef.getFacetAttribute();
					facetEntity.setFacetClass(attr.getInterfaceName());
					if (attr instanceof FacetOutboundAttribute) {
						String fieldName = ((FacetOutboundAttribute)attr).getWedgePublicFieldName();
						facetEntity.setDirection(Direction.OUT);
						facetEntity.setFieldName(fieldName);
					}
					else {		// inbound facet
						facetEntity.setDirection(Direction.IN);
					}
					facetEntity.setWedge(wedgeEntity);
					facetEntity.setSortIndex(facetIndex++);
					if (isNewFacet) {
						em.persist(facetEntity);
					}
				}
			}

			// clear up old, unused wedges
//			if (debug) {
//				logger.log(Level.INFO, "there are : " + remaining.size() + " remaining wedges to clear up");
//			}
//			for (WedgeEntity unusedWedge : remaining.values()) {
//				meemEntity.getWedges().remove(unusedWedge.getName());
//				em.remove(unusedWedge);
//			}

			//em.persist(meemEntity);
			em.getTransaction().commit();
		}
		finally {
			PersistenceContext.instance().release();
		}
	}

	public void remove(MeemPath meemPath) {

		if (DEBUG) {
			logger.log(Level.INFO, "Removing " + meemPath);
		}

		try {
			EntityManager em = PersistenceContext.instance().getEntityManager();
			em.getTransaction().begin();
			MeemEntity meemEntity = em.find(MeemEntity.class, meemPath.getLocation());
			if (meemEntity != null) {
				Map<String, WedgeEntity> wedges = meemEntity.getWedges();
				if (wedges != null) {
					for (WedgeEntity wedge : wedges.values()) {
						em.remove(wedge);
					}
					wedges.clear();
				}
				em.remove(meemEntity);
			}
			em.getTransaction().commit();
		}
		catch (Exception e) {
			logger.log(Level.INFO, "Exception while removing MeemDefinition " + meemPath.toString(), e);
		}
		finally {
			PersistenceContext.instance().release();
		}
	}

	public int getVersion(MeemPath meemPath) {
		if (DEBUG) {
			logger.log(Level.INFO, "getting version of " + meemPath);
		}


		int version = -1;

		try {
			EntityManager em = PersistenceContext.instance().getEntityManager();
			em.getTransaction().begin();
			MeemEntity meemEntity = em.find(MeemEntity.class, meemPath.getLocation());
			if (meemEntity != null) {
				version = (int) meemEntity.getVersion();
			}
			em.getTransaction().commit();
		}
		catch (Exception e) {
			logger.log(Level.INFO, "Exception while removing MeemDefinition " + meemPath.toString(), e);
		}
		finally {
			PersistenceContext.instance().release();
		}

		return version;
	}

	/**
	 * Get all MeemPaths stored in this Store
	 */
	public Set<MeemPath> getAllPaths() {
		Set<MeemPath> paths = new HashSet<MeemPath>();

		if (DEBUG) {
			logger.log(Level.INFO, "Getting all MeemPaths");
		}

		try {
			EntityManager em = PersistenceContext.instance().getEntityManager();
			em.getTransaction().begin();
			
			@SuppressWarnings("unchecked")
			List<String> ids = em.createNamedQuery("Meem.selectIds").getResultList();
			for (String id : ids) {
				MeemPath meemPath = MeemPath.spi.create(Space.MEEMSTORE, id);
				paths.add(meemPath);
			}
			em.getTransaction().commit();
		}
		catch (Exception e) {
			logger.log(Level.INFO, "Exception while retriving MeemPaths from MeemStore", e);
		}
		finally {
			PersistenceContext.instance().release();
		}
		
		if (DEBUG) {
			logger.log(Level.INFO, "Returning " + paths.size() + " MeemPaths");
		}
		return paths;
	}

	private FacetDefinition getFacetDefinition(String facetId, Collection<FacetDefinition> facetDefs) {
		for (FacetDefinition facetDef : facetDefs) {
			if (facetId.equals(facetDef.getFacetAttribute().getIdentifier())) {
				return facetDef;
			}
		}
		return null;
	}
	
	private FacetEntity getFacetEntity(String facetId, Collection<FacetEntity> facetEntities) {
		for (FacetEntity facetEntity : facetEntities) {
			if (facetId.equals(facetEntity.getName())) {
				return facetEntity;
			}
		}
		return null;
	}
	
	/**
	 * TODO create a MeemDefinition from a MeemEntity
	 * 
	 * @param meemEntity
	 * @return
	 */
	private MeemDefinition toMeemDefinition(MeemEntity meemEntity) {
		MeemDefinition meemDefinition = null;
		if (meemEntity != null) {
			meemDefinition = new MeemDefinition();
			MeemAttribute meemAttribute = new MeemAttribute(meemEntity.getId(), Scope.DISTRIBUTED, (int) meemEntity.getVersion());
			meemDefinition.setMeemAttribute(meemAttribute);
			
			if (meemEntity.getWedges() != null) {
				for (WedgeEntity wedgeEntity : meemEntity.getWedges().values()) {
					WedgeDefinition wedgeDef = toWedgeDefinition(wedgeEntity);
					meemDefinition.addWedgeDefinition(wedgeDef);
				}
			}
		}
		return meemDefinition;
	}
	
	private WedgeDefinition toWedgeDefinition(WedgeEntity wedgeEntity) {
		WedgeDefinition wedgeDefinition = null;

		WedgeAttribute wedgeAttribute = new WedgeAttribute(wedgeEntity.getClassname(), wedgeEntity.getName());

		WedgeDefinition introspectedDefinition = null;

		try {
			Class<?> wedgeClass = Class.forName(wedgeEntity.getClassname());
			introspectedDefinition = WedgeIntrospector.getWedgeDefinition(wedgeClass);
		}
		catch (ClassNotFoundException e) {
			if (Common.TRACE_ENABLED) {
				logger.log(Level.INFO, "Could not locate class for Wedge", e);
			}
		}
		catch (WedgeIntrospectorException e) {
			if (Common.TRACE_ENABLED) {
				logger.log(Level.INFO, "Problem with intropection on Wedge", e);
			}
		}

		wedgeDefinition = new WedgeDefinition(wedgeAttribute);

		// load facets from storage
		List<FacetEntity> facets = wedgeEntity.getFacets();
		if (facets != null) {
			for (FacetEntity facet : facets) {
				FacetAttribute facetAttribute;
				if (facet.getDirection() == Direction.IN) {
					facetAttribute = new FacetInboundAttribute(facet.getName(), facet.getFacetClass());
				}
				else {	// it's an outbound Facet
					facetAttribute = new FacetOutboundAttribute(facet.getName(), facet.getFacetClass(), facet.getFieldName());
				}
				FacetDefinition facetDefinition = new FacetDefinition(facetAttribute);
				wedgeDefinition.addFacetDefinition(facetDefinition);
			}
		}

		// merger the introspected wedge definition with the stored version of the wedge def.
		merge(wedgeDefinition, introspectedDefinition);
		
		return wedgeDefinition;
	}
	
	/**
	 * Make sure stored wedgedef has any new persisted fields and facets
	 * 
	 * @param storedDefinition
	 * @param introspectedDefinition
	 */
	private void merge(WedgeDefinition storedDefinition, WedgeDefinition introspectedDefinition) {
		if (introspectedDefinition == null) {
			return;
		}
		
		// set persistent fields
		Collection<String> persistedFields = new ArrayList<String>(introspectedDefinition.getWedgeAttribute().getPersistentFields());
		storedDefinition.getWedgeAttribute().setPersistentFields(persistedFields);

		// set facets
		Collection<FacetDefinition> finalFacets = new LinkedHashSet<FacetDefinition>();
		Collection<FacetDefinition> storedFacets = storedDefinition.getFacetDefinitions();
		Collection<FacetDefinition> introFacets = introspectedDefinition.getFacetDefinitions();
		for (FacetDefinition introFacet : introFacets) {
			FacetDefinition storedFacet ;
			FacetAttribute attr = introFacet.getFacetAttribute();
			if (attr instanceof FacetOutboundAttribute) {
				String fieldName = ((FacetOutboundAttribute)attr).getWedgePublicFieldName();
				storedFacet = getFacetForField(fieldName, storedFacets);
			}
			else {		// inbound facet
				String interfaceName = introFacet.getFacetAttribute().getInterfaceName();
				storedFacet = getFacetForInterface(interfaceName, storedFacets);
			}
			if (storedFacet == null) {	// stored wedge does not have the facet
				finalFacets.add(introFacet);
			}
			else {
				finalFacets.add(storedFacet);
			}
		}
		storedDefinition.setFacetDefinitions(finalFacets);
	}

	/**
	 * Get Facet for given wedge field
	 * @param fieldName
	 * @param facets
	 */
	private FacetDefinition getFacetForField(String fieldName, Collection<FacetDefinition> facets) {
		for (FacetDefinition facet : facets) {
			FacetAttribute attr = facet.getFacetAttribute();
			if (attr instanceof FacetOutboundAttribute && fieldName.equals(((FacetOutboundAttribute)attr).getWedgePublicFieldName())) {
				return facet;
			}
		}
		return null;
	}

	/**
	 * Get inbound facet for the given interface classname
	 * @param interfaceName
	 * @param facets
	 * @return
	 */
	private FacetDefinition getFacetForInterface(String interfaceName, Collection<FacetDefinition> facets) {
		for (FacetDefinition facet : facets) {
			FacetAttribute attr = facet.getFacetAttribute();
			if (attr instanceof FacetInboundAttribute && interfaceName.equals(attr.getInterfaceName())) {
				return facet;
			}
		}
		return null;
	}
}
