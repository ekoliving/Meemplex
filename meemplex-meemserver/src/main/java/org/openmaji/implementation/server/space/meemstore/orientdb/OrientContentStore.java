/*
 * @(#)OrientContentStore.java
 *
 */

package org.openmaji.implementation.server.space.meemstore.orientdb;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.meemplex.meem.PropertyType;
import org.openmaji.implementation.server.space.meemstore.content.MeemStoreContentStore;
import org.openmaji.meem.MeemPath;
import org.openmaji.meem.space.Space;
import org.openmaji.system.meem.definition.MeemContent;
import org.openmaji.system.space.meemstore.MeemStore;

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

/**
 * 
 * @author stormboy
 * @version 1.0
 * 
 */
public class OrientContentStore implements MeemStoreContentStore {

	static private final Logger logger = Logger.getAnonymousLogger();

	private static boolean DEBUG = false;

	private OSQLSynchQuery<ODocument> queryGetMeemContent = new OSQLSynchQuery<ODocument>("select from Content where meemId = ?");
	private OSQLSynchQuery<ODocument> queryMeemIds = new OSQLSynchQuery<ODocument>("select distinct(meemId) as id from Content");
	private OSQLSynchQuery<ODocument> queryGetContent = new OSQLSynchQuery<ODocument>("select from Content where meemId = ? AND wedgeName = ? AND name = ?");
	private OSQLSynchQuery<ODocument> deleteMeemContent = new OSQLSynchQuery<ODocument>("delete from Content where meemId = ?");
	
	/**
	 * 
	 */
	public void configure(MeemStore meemStore, Properties properties) {
		if (DEBUG) {
			logger.log(Level.INFO, "Configure");
		}
		DatabasePool.configure(properties);
	}

	/**
	 * 
	 */
	public void close() {
		if (DEBUG) {
			logger.log(Level.INFO, "Closing");
		}
		DatabasePool.close();
	}

	/**
	 * 
	 */
	public MeemContent load(MeemPath meemPath) {

		if (DEBUG) {
			logger.log(Level.INFO, "Loading content for " + meemPath);
		}

		MeemContent meemContent = new MeemContent();

		// This classloader change is to allows classes loaded by eclipse to perform Class.forName()
		//ClassLoader previousClassLoader = Thread.currentThread().getContextClassLoader();
		try {
			//Thread.currentThread().setContextClassLoader(getClass().getClassLoader());

			ODatabaseDocumentTx db = DatabasePool.getDatabase();
			try {
				db.begin();
				String meemId = meemPath.getLocation();
				List<ODocument> content = db.command(queryGetMeemContent).execute(meemId);
				for (ODocument contentItem : content) {
					String field = contentItem.field("name");
					byte[] bytes = contentItem.field("value");
					Serializable value = new SerializableObject<Serializable>().fromStream(bytes).getObject();
					String wedgeName = contentItem.field("wedgeName");
					meemContent.addPersistentField(wedgeName, field, value);
				}

				/*
				List<ODocument> result = db.command(queryGetMeem).execute(meemId);
				if (result != null && result.isEmpty() == false) {
					ODocument meem = result.get(0);
					Map<String, ODocument> wedges = meem.field("wedges");
					if (wedges != null) {
						for (Entry<String, ODocument> wedgeEntry : wedges.entrySet()) {
							Map<String, ODocument> content = wedgeEntry.getValue().field("contents");
							if (content != null) {
								for (ODocument contentItem : content.values()) {
									String field = contentItem.field("name");
									byte[] bytes = contentItem.field("value");
									Serializable value = new SerializableObject<Serializable>().fromStream(bytes).getObject();
									meemContent.addPersistentField(wedgeEntry.getKey(), field, value);
								}
							}						
						}
					}
				}
				*/
				
				db.commit();
			}
			catch (Exception e) {
				db.rollback();
				logger.log(Level.INFO, "Exception while loading MeemContent " + meemPath.toString(), e);
			}
			finally {
				db.close();
			}
		}
		catch (Exception e) {
			logger.log(Level.INFO, "Exception while loading MeemContent " + meemPath.toString(), e);
		}
		finally {
			//Thread.currentThread().setContextClassLoader(previousClassLoader);
		}

		return meemContent;
	}

	/**
	 * 
	 */
	public void store(MeemPath meemPath, MeemContent content) {
		if (content == null) {
			return;
		}

		if (DEBUG) {
			logger.log(Level.INFO, "Storing " + meemPath);
		}

		ODatabaseDocumentTx db = DatabasePool.getDatabase();
		try {
			db.begin();
			
			String meemId = meemPath.getLocation();

//			ODocument meem;
//			List<ODocument> result = db.command(queryGetMeem).execute(meemId);
//			if ( result == null || result.isEmpty() ) {
//				meem = new ODocument("Meem");
//				meem.field("id", meemId);
//				meem.field("version", 0);
//				meem.save();
//			}
//			else {
//				meem = result.get(0);
//			}
			
			Collection<String> wedgeIds = content.getWedgeIdentifiers();
			for (String wedgeName : wedgeIds) {
				if (DEBUG) {
					logger.log(Level.INFO, "Storing " + meemPath + " wedge: " + wedgeName);
				}
				Map<String, Serializable> fields = content.getPersistentFields(wedgeName);
				if (fields != null && fields.size() > 0) {
					for (Entry<String, Serializable> field : fields.entrySet()) {
						String name = field.getKey();
						Serializable value = field.getValue();
						if (DEBUG) {
							logger.log(Level.INFO, "Storing " + meemPath + " wedge: " + wedgeName + " field: " + name);
						}

						// get content item from meem.
						//ODocument contentItem = getContentItem(meem, wedgeName, name);
						ODocument contentItem = getContentItem(db, meemId, wedgeName, name);
						contentItem.field("type", PropertyType.typeOf(value).ordinal());
						contentItem.field("value", new SerializableObject<Serializable>(value).toStream());
						contentItem.save();
					}
				}
			}
//			meem.save();
			db.commit();
		}
		catch (Exception e) {
			db.rollback();
			logger.log(Level.INFO, "Exception while storing MeemContent " + meemPath.toString(), e);
		}
		finally {
			db.close();
		}
	}

	/**
	 * 
	 */
	public void remove(MeemPath meemPath) {
		
		// rely on Definition store to remove data

		if (DEBUG) {
			logger.log(Level.INFO, "Removing " + meemPath);
		}
		ODatabaseDocumentTx db = DatabasePool.getDatabase();
		try {
			db.begin();
			String meemId = meemPath.getLocation();
			db.command(deleteMeemContent).execute(meemId);
			db.commit();
		}
		catch (Exception e) {
			db.rollback();
			logger.log(Level.INFO, "Exception while removing MeemContent " + meemPath.toString(), e);
		}
		finally {
			db.close();
		}
	}

	/**
	 * 
	 */
	public Set<MeemPath> getAllPaths() {
		Set<MeemPath> paths = new HashSet<MeemPath>();
		
		if (DEBUG) {
			logger.log(Level.INFO, "Getting all MeemPaths");
		}

		ODatabaseDocumentTx db = DatabasePool.getDatabase();
		try {
			db.begin();
			
			List<ODocument> result = db.command(queryMeemIds).execute();
			for (ODocument meem : result) {
				String id = meem.field("id");
				MeemPath meemPath = MeemPath.spi.create(Space.MEEMSTORE, id);				
				paths.add(meemPath);
			}
			db.commit();
		}
		catch (Exception e) {
			logger.log(Level.INFO, "Exception while retrieving MeemPaths from MeemStore", e);
		}
		finally {
			db.rollback();
			db.close();
		}
		if (DEBUG) {
			logger.log(Level.INFO, "Returning " + paths.size() + " MeemPaths");
		}
		return paths;
	}

	private ODocument getWedge(ODocument meem, String wedgeName) {
		ODocument wedge = null;
		Map<String, ODocument> wedges = meem.field("wedges");
		if (wedges == null) {
			wedges = new HashMap<String, ODocument>();
			meem.field("wedges", wedges);
		}
		wedge = wedges.get(wedgeName);
		if (wedge == null) {
			wedge = new ODocument("Wedge");
			wedge.field("name", wedgeName);
			wedge.save();
			wedges.put(wedgeName, wedge);
		}
		return wedge;
	}
	
	private ODocument getContentItem(ODocument meem, String wedgeName, String contentName) {
		ODocument contentItem = null;
		ODocument wedge = getWedge(meem, wedgeName);
		if (wedge != null) {
			Map<String, ODocument> content = wedge.field("contents");
			if (content == null) {
				content = new HashMap<String, ODocument>();
				wedge.field("contents", content);
			}
			contentItem = content.get(contentName);
			
			if (contentItem == null) {
				contentItem = new ODocument("Content")
					.field("name", contentName)
					.field("meemId", meem.field("id"))
					.field("wedgeName", wedgeName);
				content.put(contentName, contentItem);
			}
		}
		return contentItem;
	}
	
	private ODocument getContentItem(ODatabaseDocumentTx db, String meemId, String wedgeName, String contentName) {
		ODocument contentItem = null;
		
		List<ODocument> content = db.command(queryGetContent).execute(meemId, wedgeName, contentName);
		if (content.isEmpty()) {
			contentItem = new ODocument("Content")
			.field("name", contentName)
			.field("meemId", meemId)
			.field("wedgeName", wedgeName);
		}
		else {
			contentItem = content.get(0);
		}

		return contentItem;
	}
}
