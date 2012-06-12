/*
 * @(#)StandardDefinitionStore.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.server.space.meemstore.definition.db;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Level;
import java.util.logging.Logger;


import org.openmaji.implementation.server.space.meemstore.MeemStoreWedge;
import org.openmaji.implementation.server.space.meemstore.definition.MeemStoreDefinitionStore;
import org.openmaji.meem.MeemPath;
import org.openmaji.meem.definition.MeemDefinition;
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
 * @author  stormboy
 * @version 1.0
 */
public class SqlDefinitionStore implements MeemStoreDefinitionStore {

	static private final Logger logger = Logger.getAnonymousLogger();

	private String driver = "org.apache.derby.jdbc.EmbeddedDriver";

	private String database = "meembase";

	private String url = "jdbc:derby:" + database + ";create=true";
	
	private String username = null;
	
	private String password = null;
	
	private Connection connection;
	
	private PreparedStatement selectMeemPaths = null;
	
	private PreparedStatement selectMeemDef = null;

	private PreparedStatement insertMeemDef = null;
	
	private PreparedStatement updateMeemDef = null;

	private PreparedStatement deleteMeemDef = null;
	
	private static final boolean DEBUG = false;
	

	public void configure(MeemStore meemStore, Properties properties) {
		this.database = properties.getProperty(MeemStoreWedge.MEEMSTORE_LOCATION);
		if (this.database == null) {
			this.database = "meemstore";
		}
		this.url = "jdbc:derby:" + database + ";create=true";
		connect();
	}

	public void close() {
		if (DEBUG) {
			logger.log(Level.INFO, "Closing");
		}

		selectMeemPaths = null;
		selectMeemDef = null;
		updateMeemDef = null;
		insertMeemDef = null;
		deleteMeemDef = null;

		if (connection != null) {
			try {
				connection.close();
			}
			catch (SQLException e) {
			}
		}
	}
	

	/**
	 */
	public MeemDefinition load(MeemPath meemPath) {

		if (DEBUG) {
			logger.log(Level.INFO, "Loading definition for " + meemPath);
		}
		
		MeemDefinition definition = null;

		// This classloader change is to allows classes loaded by eclipse to 
		// perform Class.forName()
		ClassLoader previousClassLoader = Thread.currentThread().getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(getClass().getClassLoader());

			try {
				selectMeemDef.setString(1, meemPath.getLocation());
				ResultSet fields = selectMeemDef.executeQuery();
				if (fields.next()) {
					try {
						//Object fieldValue = fields.getObject(2);
						InputStream is = fields.getBinaryStream(2);
						ObjectInputStream ois = new ObjectInputStream(is);
						definition = (MeemDefinition) ois.readObject();
						ois.close();
					}
					catch (Exception e) {
						String msg = "Exception while loading MeemDefinition " + meemPath.toString();
						logger.log(Level.INFO, msg, e);
					}
				}
			}
			catch (SQLException e) {
				logger.log(Level.INFO, "Exception while loading MeemContent " + meemPath.toString(), e);
			}
		
		} finally {
			Thread.currentThread().setContextClassLoader(previousClassLoader);
		}

		return definition;
	}

	public void store(MeemPath meemPath, MeemDefinition definition) {

		if (DEBUG) {
			logger.log(Level.INFO, "Storing Definition for " + meemPath);
		}

		try {
			updateMeemDef.setString(3, meemPath.getLocation());
			insertMeemDef.setString(3, meemPath.getLocation());
									
			int version = definition.getMeemAttribute().getVersion();
			updateMeemDef.setInt(2, version);
			insertMeemDef.setInt(2, version);

			byte[] buffer;
			try {
				//insertField.setObject(3, definition);
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				ObjectOutputStream oos = new ObjectOutputStream(bos);
				oos.writeObject(definition);
				oos.close();
				buffer = bos.toByteArray();						
				
				
				updateMeemDef.setBytes(1, buffer);						
				int n = updateMeemDef.executeUpdate();				// insert field value
				if (n == 0) {
					insertMeemDef.setBytes(1, buffer);						
					insertMeemDef.executeUpdate();				// insert field value
				}
			}
			catch (IOException e) {
				String msg = "Exception while storing MeemContent field " + meemPath.toString();
				logger.log(Level.INFO, msg, e);						
			}
		}
		catch (SQLException e) {
			logger.log(Level.INFO, "Exception while storing MeemDefinition " + meemPath.toString(), e);
		}

	}

	public void remove(MeemPath meemPath) {

		if (DEBUG) {
			logger.log(Level.INFO, "Removing " + meemPath);
		}

		try {
			deleteMeemDef.setString(1, meemPath.getLocation());
			deleteMeemDef.execute();
		}
		catch (SQLException e) {
			logger.log(Level.INFO, "Exception while removing MeemDefinition " + meemPath.toString(), e);
		}
	}

	public int getVersion(MeemPath meemPath) {
		if (DEBUG) {
			logger.log(Level.INFO, "Loading MeemDefinition for: " + meemPath);
		}


		int version = -1;
		
		try {
			selectMeemDef.setString(1, meemPath.getLocation());
			ResultSet fields = selectMeemDef.executeQuery();
			if (fields.next()) {
				version = fields.getInt(1);
			}
		}
		catch (SQLException e) {
			logger.log(Level.INFO, "Exception while loading MeemContent " + meemPath.toString(), e);
		}
		
		return version;
	}

	public Set<MeemPath> getAllPaths() {
		Set<MeemPath> paths = new HashSet<MeemPath>();
		
		if (DEBUG) {
			logger.log(Level.INFO, "Getting all MeemPaths");
		}

		try {
			ResultSet resultSet = selectMeemPaths.executeQuery();
	
			while (resultSet.next()) {
				MeemPath meemPath = MeemPath.spi.create(Space.MEEMSTORE, resultSet.getString(1));
//				if (DEBUG) {
//					logger.log(Level.INFO, "Got MeemPath: " + meemPath);
//				}
				paths.add(meemPath);
			}
		}
		catch (SQLException e) {
			logger.log(Level.INFO, "Exception while retriving MeemPaths from MeemStore", e);
		}
		if (DEBUG) {
			logger.log(Level.INFO, "Returning " + paths.size() + " MeemPaths");
		}
		return paths;
	}

	private void connect() {
		if (DEBUG) {
			logger.log(Level.INFO, "Connecting to database");
		}

		try {
			Class.forName(driver);
		}
		catch (ClassNotFoundException ex) {
			logger.log(Level.INFO, "Could not load JDBC driver", ex);
		}

		try {
			connection = DriverManager.getConnection(url, username, password);

			createTables();

			selectMeemPaths = connection.prepareStatement(SQL_SELECT_MEEMPATHS);
			selectMeemDef = connection.prepareStatement(SQL_SELECT_MEEMDEF);
			updateMeemDef = connection.prepareStatement(SQL_UPDATE_MEEMDEF);
			insertMeemDef = connection.prepareStatement(SQL_INSERT_MEEMDEF);
			deleteMeemDef = connection.prepareStatement(SQL_DELETE_MEEMDEF);
		}
		catch (SQLException ex) {
			logger.log(Level.INFO, "Could not connect to database or prepare", ex);
		}
	}
	
	private void createTables() {
		Statement statement;
		try {
			statement = connection.createStatement();
			statement.execute(SQL_CREATE_DEF_TABLE);
			statement.close();
		}
		catch (SQLException e) {
			//logger.log(Level.INFO, "Problem creating table", e);
		}
	}	
	

	private static final String TABLE_CONTENT_DEF = "content_definition";
	
	private static final String SQL_CREATE_DEF_TABLE = 
		"CREATE TABLE " + TABLE_CONTENT_DEF + "(" + 
			"meempath varchar(255), " +
			"version int, " + 
			"meem_def blob, " + 
			"PRIMARY KEY (meempath)" + 
		")";

	private static final String SQL_SELECT_MEEMPATHS = 
		"SELECT DISTINCT meempath FROM " + TABLE_CONTENT_DEF;
	
	private static final String SQL_SELECT_MEEMDEF = 
		"SELECT version, meem_def FROM " + TABLE_CONTENT_DEF + " WHERE meempath=?";

	private static final String SQL_INSERT_MEEMDEF = 
		"INSERT INTO " + TABLE_CONTENT_DEF + " (meem_def, version, meempath) VALUES (?, ?, ?)";

	private static final String SQL_UPDATE_MEEMDEF = 
		"UPDATE " + TABLE_CONTENT_DEF + " SET meem_def=?, version=? WHERE meempath=?";

	private static final String SQL_DELETE_MEEMDEF = 
		"DELETE FROM " + TABLE_CONTENT_DEF + " WHERE meempath=?";

	
	
	private static final String TABLE_MEEM_DEF = "meem";
	private static final String TABLE_WEDGE_DEF = "wedge";

	private static final String SQL_CREATE_MEEM_DEF_TABLE = 
		"CREATE TABLE " + TABLE_MEEM_DEF + "(" + 
			"meem_id VARCHAR(255) NOT NULL, " +
			"version INT, " + 
			"meem_def BLOB, " + 
			"PRIMARY KEY (meem_id)" + 
		")";

	private static final String SQL_CREATE_WEDGE_DEF_TABLE = 
		"CREATE TABLE " + TABLE_WEDGE_DEF + "(" + 
			"wedge_id LONG NOT NULL GENERATED ALWAYS AS IDENTITY, " +
			"meem_id CARCHAR(255) NOT NULL, " +
			"wedge_name CARCHAR(255) NOT NULL, " +
			"wedge_class VARCHAR(255) NOT NULL, " +
			"is_system BYTE NOT NULL DEFAULT '0', " +
			"wedge_def BLOB, " + 
			"PRIMARY KEY (wedge_id), " +
			"UNIQUE (meem_id, wedge_name) " +
		")";

//	private static final String SQL_CREATE_WEDGE_DEF_TABLE = 
//		"CREATE TABLE " + TABLE_WEDGE_DEF + "(" + 
//			"wedge_def_id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY, " +
//			"meem_id varchar(255) NOT NULL, " +
//			"wedge_name varchar(255) NOT NULL, " +
//			"wedge_def blob, " + 
//			"PRIMARY KEY (meem_id, wedge_name)" +
//		")";
}
