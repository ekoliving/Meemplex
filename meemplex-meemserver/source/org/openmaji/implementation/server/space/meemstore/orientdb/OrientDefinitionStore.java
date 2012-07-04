package org.openmaji.implementation.server.space.meemstore.orientdb;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.meemplex.service.model.Direction;
import org.openmaji.implementation.server.space.meemstore.definition.MeemStoreDefinitionStore;
import org.openmaji.meem.MeemPath;
import org.openmaji.meem.definition.FacetAttribute;
import org.openmaji.meem.definition.FacetDefinition;
import org.openmaji.meem.definition.FacetOutboundAttribute;
import org.openmaji.meem.definition.MeemAttribute;
import org.openmaji.meem.definition.MeemDefinition;
import org.openmaji.meem.definition.WedgeDefinition;
import org.openmaji.meem.space.Space;
import org.openmaji.system.space.meemstore.MeemStore;

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;


/**
 * <p>
 * ...
 * </p>
 * 
 * @author stormboy
 * @version 1.0
 */
public class OrientDefinitionStore implements MeemStoreDefinitionStore {

	static private final Logger logger = Logger.getAnonymousLogger();

	private static boolean DEBUG = false;

	//private OSQLSynchQuery<ODocument> queryGetMeem = new OSQLSynchQuery<ODocument>("select value from index:MeemId where key = ?");
	private OSQLSynchQuery<ODocument> queryGetMeem = new OSQLSynchQuery<ODocument>("select from Meem where id = ?");
	private OSQLSynchQuery<ODocument> queryMeemIds = new OSQLSynchQuery<ODocument>("select id from Meem");

	
	/**
	 * configure this meemstore
	 */
	public void configure(MeemStore meemStore, Properties properties) {
		if (DEBUG) {
			logger.log(Level.INFO, "Configure");
		}

		DatabasePool.configure(properties);
	}

	public void close() {
		if (DEBUG) {
			logger.log(Level.INFO, "Closing");
		}
		DatabasePool.close();
	}

	/**
	 * Load meem definition for the meem at the given path.
	 */
	public MeemDefinition load(MeemPath meemPath) {
		
		if (DEBUG) {
			logger.log(Level.INFO, "Loading MeemDefinition for: " + meemPath);
		}
		
		ODatabaseDocumentTx db = DatabasePool.getDatabase();
		
		MeemDefinition definition = null;

		// This classloader change is to allows classes loaded by eclipse to perform Class.forName()
		ClassLoader previousClassLoader = Thread.currentThread().getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
			db.begin();
			List<ODocument> result = db.command(queryGetMeem).execute(meemPath.getLocation());
			if (result != null && result.size() > 0) {
				ODocument meem = result.get(0);
				byte[] bytes = meem.field("definition");
				if (bytes != null) {
					definition = new SerializableObject<MeemDefinition>().fromStream(bytes).getObject();
				}
				else {
					logger.log(Level.INFO, "no meem definition found for " + meemPath + " name " + meem.field("name"));
				}
			}
			db.commit();
		}
		catch (Exception e) {
			db.rollback();
		}
		finally {
			db.close();
			Thread.currentThread().setContextClassLoader(previousClassLoader);
		}

		return definition;
	}

	/**
	 * Store the definition for the given meempath
	 */
	public void store(MeemPath meemPath, MeemDefinition definition) {

		if (DEBUG) {
			logger.log(Level.INFO, "storing MeemDefinition for : " + meemPath);
		}
		
		ODatabaseDocumentTx db = DatabasePool.getDatabase();
		try {
			db.begin();
			
			String meemId = meemPath.getLocation();
			ODocument meem;
			List<ODocument> result = db.command(queryGetMeem).execute(meemId);
			if ( result == null || result.isEmpty() ) {
				meem = new ODocument("Meem");
				meem.field("id", meemId);
			}
			else {
				meem = result.get(0);
			}
			
			MeemAttribute meemAttribute = definition.getMeemAttribute();
			meem.field("name", meemAttribute.getIdentifier());
			meem.field("key", ""+meemAttribute.getKey());
			meem.field("scope", meemAttribute.getScope().getIdentifier());
			meem.field("version", meemAttribute.getVersion());
			meem.field("definition", new SerializableObject<MeemDefinition>(definition).toStream());
			
			Map<String, ODocument> wedges = meem.field("wedges");
			if (wedges == null) {
				// make sure we have wedges
				wedges = new HashMap<String, ODocument>();
				meem.field("wedges", wedges);
			}
			
			Map<String, ODocument> remainingWedges = new HashMap<String, ODocument>(wedges);
			
			// update wedges
			int wedgeIndex = 0;
			Collection<WedgeDefinition> wedgeDefs = definition.getWedgeDefinitions();
			for (WedgeDefinition wedgeDef : wedgeDefs) {
				String wedgeName = wedgeDef.getWedgeAttribute().getIdentifier();
				ODocument wedge = wedges.get(wedgeName);
				if (wedge== null) {
					wedge = new ODocument("Wedge");
					wedge.field("name", wedgeName);
					wedges.put(wedgeName, wedge);
				}
				wedge.field("classname", wedgeDef.getWedgeAttribute().getImplementationClassName());
				wedge.field("isSystem", wedgeDef.getWedgeAttribute().isSystemWedge());
				wedge.field("sortIndex", wedgeIndex++);
				remainingWedges.remove(wedgeName);	// make sure the wedge is removed from remaining list
				
				// store facets
				Map<String, ODocument> facets = wedge.field("facets");
				if (facets == null) {
					facets = new HashMap<String, ODocument>();
					wedge.field("facets", facets);
				}

				int facetIndex = 0;
				Collection<FacetDefinition> facetDefs = wedgeDef.getFacetDefinitions();
				for (FacetDefinition facetDef : facetDefs) {
					String facetName = facetDef.getFacetAttribute().getIdentifier();
					ODocument facet = facets.get(facetName);
					if (facet == null) {
						facet = new ODocument("Facet");
						facet.field("name", facetName);
						facets.put(facetName, facet);
					}
					
					FacetAttribute attr = facetDef.getFacetAttribute();
					facet.field("type", attr.getInterfaceName());
					if (attr instanceof FacetOutboundAttribute) {
						String fieldName = ((FacetOutboundAttribute)attr).getWedgePublicFieldName();
						facet.field("direction", Direction.OUT.ordinal());
						facet.field("fieldName", fieldName);
					}
					else {		// inbound facet
						facet.field("direction", Direction.IN.ordinal());
					}
					facet.field("sortIndex", facetIndex++);
					facet.save();
				}
				wedge.save();
			}

			// clear up old, unused wedges
//			if (debug) {
//				logger.log(Level.INFO, "there are : " + remaining.size() + " remaining wedges to clear up");
//			}
//			for (WedgeEntity unusedWedge : remaining.values()) {
//				meemEntity.getWedges().remove(unusedWedge.getName());
//				em.remove(unusedWedge);
//			}
			
			meem.save();

			db.commit();
		}
		catch (Exception e) {
			db.rollback();
		}
		finally {
			db.close();
		}
	}

	public void remove(MeemPath meemPath) {

		if (DEBUG) {
			logger.log(Level.INFO, "Removing " + meemPath);
		}

		ODatabaseDocumentTx db = DatabasePool.getDatabase();
		try {
			db.begin();
			List<ODocument> result = db.command(queryGetMeem).execute(meemPath.getLocation());

			if (result != null && result.isEmpty() == false) {
				ODocument meem = result.get(0);
				meem.delete();
				/*
				Map<String, ODocument> wedges = meem.field("wedges");
				if (wedges != null) {
					for (ODocument wedge : wedges.values()) {
						em.remove(wedge);
					}
					wedges.clear();
				}
				em.remove(meemEntity);
				*/
			}
			db.commit();
		}
		catch (Exception e) {
			db.rollback();
			logger.log(Level.INFO, "Exception while removing MeemDefinition " + meemPath.toString(), e);
		}
		finally {
			db.close();
		}
	}

	public int getVersion(MeemPath meemPath) {
		if (DEBUG) {
			logger.log(Level.INFO, "getting version of " + meemPath);
		}

		int version = -1;

		ODatabaseDocumentTx db = DatabasePool.getDatabase();
		try {
			db.begin();
			List<ODocument> result = db.command(queryGetMeem).execute(meemPath.getLocation());
			if (result != null && result.size() > 0) {
				version = result.get(0).field("version");
			}
			db.commit();
		}
		catch (Exception e) {
			db.rollback();
			logger.log(Level.INFO, "Exception while getting version of MeemDefinition " + meemPath.toString(), e);
		}
		finally {
			db.close();
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

		ODatabaseDocumentTx db = DatabasePool.getDatabase();
		try {
			db.begin();
			List<ODocument> meems = db.command(queryMeemIds).execute();
			for (ODocument meem : meems) {
				String id = meem.field("id");
				MeemPath meemPath = MeemPath.spi.create(Space.MEEMSTORE, id);				
				paths.add(meemPath);
			}
			db.commit();
		}
		catch (Exception e) {
			db.rollback();
			logger.log(Level.INFO, "Exception while retriving MeemPaths from MeemStore", e);
		}
		finally {
			db.close();
		}
		
		if (DEBUG) {
			logger.log(Level.INFO, "Returning " + paths.size() + " MeemPaths");
		}
		return paths;
	}
	
}
