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
package org.openmaji.implementation.server.space.meemstore.content.db;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Level;
import java.util.logging.Logger;

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
public class SqlContentStore implements MeemStoreContentStore {

	static private final Logger logger = Logger.getAnonymousLogger();

	private String database = "meembase";

	private String driver = "org.apache.derby.jdbc.EmbeddedDriver";

	private String url = "jdbc:derby:" + database + ";create=true";

	private String username = null;

	private String password = null;

	private Connection connection = null;

	private PreparedStatement selectMeemPaths = null;
	
	private PreparedStatement selectFields = null;

	private PreparedStatement insertField = null;
	
	private PreparedStatement updateField = null;

	private PreparedStatement deleteFields = null;
	
	private static final boolean DEBUG = false;
	
	/**
	 * 
	 */
	public void configure(MeemStore meemStore, Properties properties) {
		this.database = properties.getProperty(MeemStoreWedge.MEEMSTORE_LOCATION);
		if (this.database == null) {
			this.database = "meemstore";
		}
		this.url = "jdbc:derby:" + database + ";create=true";
		connect();
	}

	/**
	 * 
	 */
	public void close() {
		if (DEBUG) {
			logger.log(Level.INFO, "Closing");
		}
		
		selectMeemPaths = null;
		selectFields = null;
		updateField = null;
		insertField = null;
		deleteFields = null;

		if (connection != null) {
			try {
				connection.close();
			}
			catch (SQLException e) {
			}
		}
	}

	/**
	 * 
	 */
	public MeemContent load(MeemPath meemPath) {
		
		if (DEBUG) {
			logger.log(Level.INFO, "Loading " + meemPath);
		}
		
		MeemContent meemContent = new MeemContent();

		// This classloader change is to allows classes loaded by eclipse to perform Class.forName()
		ClassLoader previousClassLoader = Thread.currentThread().getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(getClass().getClassLoader());

			try {
				selectFields.setString(1, meemPath.getLocation());
				ResultSet fields = selectFields.executeQuery();
				while (fields.next()) {
					String wedgeId = fields.getString(1);
					String fieldName = fields.getString(2);
					try {
						//Object fieldValue = fields.getObject(3);

						InputStream is = fields.getBinaryStream(3);
						ObjectInputStream ois = new ObjectInputStream(is);
						Serializable fieldValue = (Serializable) ois.readObject();
						ois.close();

						meemContent.addPersistentField(wedgeId, fieldName, fieldValue);
					}
					catch (Exception e) {
						String msg = "Exception while loading field " + meemPath.toString() + "." + wedgeId + "." + fieldName;
						logger.log(Level.INFO, msg, e);
					}
				}

			}
			catch (SQLException e) {
				logger.log(Level.INFO, "Exception while loading MeemContent " + meemPath.toString(), e);
			}

		}
		finally {
			Thread.currentThread().setContextClassLoader(previousClassLoader);
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
			updateField.setString(2, meemPath.getLocation());
			insertField.setString(2, meemPath.getLocation());
									
			Collection<String> wedgeIds = content.getWedgeIdentifiers();
			Iterator<String> wedgeIter = wedgeIds.iterator();
			while (wedgeIter.hasNext()) {
				String wedgeId = wedgeIter.next();
				
				updateField.setString(3, wedgeId);
				insertField.setString(3, wedgeId);

				Map<String, Serializable> fields = content.getPersistentFields(wedgeId);
				Iterator<String> fieldNameIter = fields.keySet().iterator();
				while (fieldNameIter.hasNext()) {
					String fieldName = fieldNameIter.next();
					Object fieldValue = fields.get(fieldName);
					
					updateField.setString(4, fieldName);
					insertField.setString(4, fieldName);
					
					byte[] buffer;
					try {
						ByteArrayOutputStream bos = new ByteArrayOutputStream();
						ObjectOutputStream oos = new ObjectOutputStream(bos);
						oos.writeObject(fieldValue);
						oos.close();
						buffer = bos.toByteArray();						
						updateField.setBytes(1, buffer);	
						
						int n = updateField.executeUpdate();	// try to update the field value
						if (n == 0) {
							// if update failed, insert the field value
							insertField.setBytes(1, buffer);						
							insertField.executeUpdate();		// insert field value
						}
					}
					catch (IOException e) {
						String msg = "Exception while storing MeemContent field " + meemPath.toString() + "." + wedgeId + "." + fieldName;
						logger.log(Level.INFO, msg, e);						
					}
				}
			}
		}
		catch (SQLException e) {
			logger.log(Level.INFO, "Exception while storing MeemContent " + meemPath.toString(), e);
		}
	}

	/**
	 * 
	 */
	public void remove(MeemPath meemPath) {
		
		if (DEBUG) {
			logger.log(Level.INFO, "Removing " + meemPath);
		}

		try {
			deleteFields.setString(1, meemPath.getLocation());
			deleteFields.execute();
		}
		catch (SQLException e) {
			logger.log(Level.INFO, "Exception while removing MeemContent " + meemPath.toString(), e);
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
			logger.log(Level.INFO, "Exception while retrieving MeemPaths from MeemStore", e);
		}
		if (DEBUG) {
			logger.log(Level.INFO, "Returning " + paths.size() + " MeemPaths");
		}
		return paths;
	}

	/**
	 * connect to database
	 */
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
			selectFields = connection.prepareStatement(SQL_SELECT_FIELDS);
			updateField = connection.prepareStatement(SQL_UPDATE_FIELD);
			insertField = connection.prepareStatement(SQL_INSERT_FIELD);
			deleteFields = connection.prepareStatement(SQL_DELETE_FIELDS);
		}
		catch (SQLException ex) {
			logger.log(Level.INFO, "Could not connect to database or prepare statements for: " + url, ex);
		}
	}

	private void createTables() {
		Statement statement;
		try {
			statement = connection.createStatement();
			statement.execute(SQL_CREATE_FIELD_TABLE);
			statement.close();
		}
		catch (SQLException e) {
		}
	}

	private static final String TABLE_CONTENT_FIELD = "field_content";

	private static final String SQL_CREATE_FIELD_TABLE = 
		"CREATE TABLE " + TABLE_CONTENT_FIELD + "(" + 
			"meem_id varchar(255), " + 
			"wedge_id varchar(255)," + 
			"field_name varchar(512)," + 
			"field_value blob," + 
			"PRIMARY KEY (meem_id, wedge_id, field_name)" + 
		")";

	private static final String SQL_SELECT_MEEMPATHS = 
		"SELECT DISTINCT meem_id FROM " + TABLE_CONTENT_FIELD;
	
	private static final String SQL_SELECT_FIELDS = 
		"SELECT wedge_id, field_name, field_value FROM " + TABLE_CONTENT_FIELD + " WHERE meem_id=?";

	private static final String SQL_INSERT_FIELD = 
		"INSERT INTO " + TABLE_CONTENT_FIELD + " (field_value, meem_id, wedge_id, field_name) VALUES (?, ?, ?, ?)";

	private static final String SQL_UPDATE_FIELD = 
		"UPDATE " + TABLE_CONTENT_FIELD + " SET field_value=? WHERE meem_id=? AND wedge_id=? AND field_name=?";
	
	private static final String SQL_DELETE_FIELDS = 
		"DELETE FROM " + TABLE_CONTENT_FIELD + " WHERE meem_id=?";

}
