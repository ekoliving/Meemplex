package org.openmaji.implementation.server.space.meemstore.orientdb;

import java.io.File;
import java.util.Properties;

import org.openmaji.implementation.server.space.meemstore.MeemStoreWedge;

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentPool;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.metadata.schema.OType;

public class DatabasePool {
	static boolean created = false;
	
	static String dbPath = "/opt/orientdb-1.0.1/databases/meemstore";
	static String username = "admin";
	static String password = "admin";
	
	static void configure(Properties properties) {
		dbPath = properties.getProperty(MeemStoreWedge.MEEMSTORE_LOCATION);
	}

	static ODatabaseDocumentTx getDatabase() {
		ODatabaseDocumentTx db;
		if (!created && !new File(dbPath).exists()) {
			db = new ODatabaseDocumentTx ("local:" + dbPath).create();
			
			OSchema schema = db.getMetadata().getSchema();
			// create schema
			OClass facet = schema.createClass("Facet");
			facet.createProperty("name", OType.STRING);
			facet.createProperty("type", OType.INTEGER);
			facet.createProperty("fieldName", OType.STRING);
			facet.createProperty("direction", OType.BYTE);
			facet.createProperty("sortIndex", OType.INTEGER);
			
			OClass content = schema.createClass("Content");
			content.createProperty("meemId", OType.STRING);
			content.createProperty("wedgeName", OType.STRING);
			content.createProperty("name", OType.STRING);
			content.createProperty("type", OType.STRING);
			content.createProperty("value", OType.BINARY);

			content.createIndex("ContentId", OClass.INDEX_TYPE.UNIQUE, "meemId", "wedgeName", "name");
			content.createIndex("MeemContent", OClass.INDEX_TYPE.NOTUNIQUE, "meemId");

			OClass wedge = schema.createClass("Wedge");
			wedge.createProperty("name", OType.STRING);
			wedge.createProperty("classname", OType.STRING);
			wedge.createProperty("isSystem", OType.BOOLEAN);
			wedge.createProperty("sortIndex", OType.INTEGER);
			wedge.createProperty("facets", OType.EMBEDDEDMAP, facet);
			//wedge.createProperty("contents", OType.LINKMAP, content);

			OClass meem = schema.createClass("Meem");
			meem.createProperty("id", OType.STRING);
			meem.createProperty("name", OType.STRING);
			meem.createProperty("description", OType.STRING);
			meem.createProperty("key", OType.STRING);
			meem.createProperty("version", OType.INTEGER);
			meem.createProperty("definition", OType.BINARY);
			meem.createProperty("wedges", OType.EMBEDDEDMAP, wedge);
			
			meem.createIndex("MeemId", OClass.INDEX_TYPE.UNIQUE, "id");
			
			schema.save();		// save the schema
			
			db.close();
			created = true;
		}

		db = ODatabaseDocumentPool.global().acquire("local:" + dbPath, username, password);

		return db;
	}

	/**
	 * Close the pool
	 */
	static void close() {
		ODatabaseDocumentPool.global().close();
	}
}
