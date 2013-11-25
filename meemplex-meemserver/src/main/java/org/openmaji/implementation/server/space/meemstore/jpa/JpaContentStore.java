/*
 * @(#)StandardContentStore.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.server.space.meemstore.jpa;

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

import javax.persistence.EntityManager;

import org.meemplex.meem.PropertyType;
import org.openmaji.implementation.server.space.meemstore.MeemStoreWedge;
import org.openmaji.implementation.server.space.meemstore.content.MeemStoreContentStore;
import org.openmaji.meem.MeemPath;
import org.openmaji.meem.space.Space;
import org.openmaji.system.meem.definition.MeemContent;
import org.openmaji.system.space.meemstore.MeemStore;

/**
 * 
 * @author stormboy
 * @version 1.0
 * 
 */
public class JpaContentStore implements MeemStoreContentStore {

	static private final Logger logger = Logger.getAnonymousLogger();

	private String database = "meemstore";

	private String driver = "org.apache.derby.jdbc.EmbeddedDriver";

	private String url = "jdbc:derby:" + database + ";create=true";

	private String username = null;

	private String password = null;

	private static boolean DEBUG = false;

	/**
	 * 
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
		
		HashMap<String, Object> myProperties = new HashMap<String, Object>();
		myProperties.put("javax.persistence.jdbc.driver", driver);
		myProperties.put("javax.persistence.jdbc.url", url);
		myProperties.put("javax.persistence.jdbc.user", username);
		myProperties.put("javax.persistence.jdbc.password", password);

		PersistenceContext.instance().config(myProperties);
	}

	/**
	 * 
	 */
	public void close() {
		if (DEBUG) {
			logger.log(Level.INFO, "Closing");
		}

		PersistenceContext.instance().close();
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

			try {
				EntityManager em = PersistenceContext.instance().getEntityManager();
				em.getTransaction().begin();
				
				@SuppressWarnings("unchecked")
				List<ContentEntity> content = em.createNamedQuery("Content.selectForMeem").
					setParameter(1, meemPath.getLocation()).
					getResultList();
				
				for (ContentEntity contentItem : content) {
					meemContent.addPersistentField(contentItem.getWedgeName(), contentItem.getName(), contentItem.getValue());
				}

				em.getTransaction().commit();
			}
			catch (Exception e) {
				logger.log(Level.INFO, "Exception while loading MeemContent " + meemPath.toString(), e);
			}
			finally {
				PersistenceContext.instance().release();
			}
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

		try {
			EntityManager em = PersistenceContext.instance().getEntityManager();
			em.getTransaction().begin();
			
			String meemId = meemPath.getLocation();
//			MeemEntity meemEntity = em.find(MeemEntity.class, meemId);
//			if (meemEntity == null) {
//				//logger.log(Level.INFO, "could not locate Meem in storage, " + meemPath.getLocation());
//				meemEntity = new MeemEntity();
//				meemEntity.setId(meemPath.getLocation());
//				em.persist(meemEntity);
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
							logger.log(Level.INFO, "Storing " + meemPath + " field: " + name);
						}
						
						ContentPK key = new ContentPK(meemId, wedgeName, name);
						ContentEntity contentEntity = em.find(ContentEntity.class, key);
						if (contentEntity == null) {
							contentEntity = new ContentEntity();
							contentEntity.setMeemId(meemId);
							contentEntity.setWedgeName(wedgeName);
							contentEntity.setName(name);
							contentEntity.setType(PropertyType.typeOf(value));
							contentEntity.setValue(value);
							
							em.persist(contentEntity);
						}
						else {
							contentEntity.setType(PropertyType.typeOf(value));
							contentEntity.setValue(value);
						}
					}
				}
			}
			em.getTransaction().commit();
		}
		catch (Exception e) {
			logger.log(Level.INFO, "Exception while storing MeemContent " + meemPath.toString(), e);
		}
		finally {
			PersistenceContext.instance().release();
		}
	}

	/**
	 * 
	 */
	public void remove(MeemPath meemPath) {
		
		// TODO rely on Definition store to remove data

		if (DEBUG) {
			logger.log(Level.INFO, "Removing " + meemPath);
		}
		try {
			EntityManager em = PersistenceContext.instance().getEntityManager();
			em.getTransaction().begin();
			
			@SuppressWarnings("unchecked")
			List<ContentEntity> content = em.createNamedQuery("Content.selectForMeem").
				setParameter(1, meemPath.getLocation()).
				getResultList();
			
			for (ContentEntity contentItem : content) {
				em.remove(contentItem);
			}
			
			em.getTransaction().commit();
		}
		catch (Exception e) {
			logger.log(Level.INFO, "Exception while removing MeemContent " + meemPath.toString(), e);
		}
		finally {
			PersistenceContext.instance().release();
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

		try {
			EntityManager em = PersistenceContext.instance().getEntityManager();
			em.getTransaction().begin();
			
			@SuppressWarnings("unchecked")
			List<String> ids = em.createNamedQuery("Content.selectIds").getResultList();
			for (String id : ids) {
				MeemPath meemPath = MeemPath.spi.create(Space.MEEMSTORE, id);
//				if (DEBUG) {
//					logger.log(Level.INFO, "Got MeemPath: " + meemPath);
//				}
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

}
