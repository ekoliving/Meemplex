package org.openmaji.implementation.server.space.meemstore.jpa;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.openmaji.implementation.server.Common;

public class PersistenceContext {
	static private final Logger logger = Logger.getAnonymousLogger();

	private static final String PERSISTENCE_UNIT = "meemstore";

	private static PersistenceContext instance;
	
	private Map<String, Object> properties = new HashMap<String, Object>();
	
	private EntityManagerFactory emf;
	
	private ThreadLocal<EntityManager> entityManagers = new ThreadLocal<EntityManager>();
	
	private boolean debug = false;
	
	/**
	 * Get singleton
	 * 
	 * @return
	 */
	public static synchronized PersistenceContext instance() {
		if (instance == null) {
			instance = new PersistenceContext();
		}
		return instance;
	}
	
	public synchronized void config(Map<String, Object> properties) {
		if (emf == null) {
			properties.put("javax.persistence.provider", "org.eclipse.persistence.jpa.PersistenceProvider");
			properties.put("eclipselink.ddl-generation", "create-tables");

			String logFile = System.getProperty(Common.PROPERTY_MAJI_HOME) + "/logs/jpa.log";
			properties.put("eclipselink.logging.file", logFile);
			//properties.put("eclipselink.logging.logger", "JavaLogger");	// use java logger
			//properties.put("eclipselink.logging.level", "SEVERE");
			/*
			properties.put("toplink.ddl-generation", "create-tables");
			*/

//			try {
//				StringBuffer sb = new StringBuffer();
//				URL url = this.getClass().getClassLoader().getResource("/META-INF/persistence.xml");
//				BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
//				for (String line = br.readLine(); line != null; line = br.readLine()) {
//					sb.append(line);
//					sb.append('\n');
//				}
//				logger.info("got persistence URL: " + sb);
//			}
//			catch (Exception e) {
//				logger.log(Level.INFO, "problem", e);
//			}
			
			//properties.put(PersistenceUnitProperties.CLASSLOADER, this.getClass().getClassLoader());
			
			//EntityManagerFactoryBuilder.createEntityManagerFactory()

			this.properties = properties;
			this.emf = Persistence.createEntityManagerFactory(PersistenceContext.PERSISTENCE_UNIT, properties);
			if (debug) {
				logger.log(Level.INFO, "persistence factory setup successfully: " + emf);
			}
		}
	}
	
	public synchronized void close() {
		if (emf != null) {
			emf.close();
			if (debug) {
				logger.log(Level.INFO, "persistence factory closed successfully: " + emf);
			}
			emf = null;
		}
	}
	
	public synchronized EntityManager getEntityManager() {
		EntityManager em = entityManagers.get();
		if (em == null || em.isOpen() == false) {
			em = emf.createEntityManager(properties);

			entityManagers.set(em);
//			if (debug) {
//				logger.log(Level.INFO, "entity manager setup successfully" + em);
//			}
		}
		return em;
	}
	
	public synchronized void release() {
		EntityManager em = this.entityManagers.get();
		if (em != null) {
			if (em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
//			em.close();
//			entityManagers.remove();
		}
	}	
}
