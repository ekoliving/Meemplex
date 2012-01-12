/*
 * @(#)BeanXMLTest.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.server.nursery.space.meemstore.beanxml;

import java.beans.ExceptionListener;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.openmaji.implementation.server.meem.definition.WedgeIntrospector;
import org.openmaji.implementation.server.meem.definition.WedgeIntrospectorException;
import org.openmaji.implementation.server.space.CategoryWedge;
import org.openmaji.meem.definition.MeemAttribute;
import org.openmaji.meem.definition.MeemDefinition;
import org.openmaji.meem.definition.Scope;
import org.openmaji.meem.space.Space;
import org.openmaji.system.meem.definition.MeemContent;



/**
 * <p>
 * ...
 * </p>
 * @author  mg
 * @version 1.0
 */
public class BeanXMLTest {

	public void testContent() {
		MeemContent content = new MeemContent();

		content.addPersistentField("wedge1", "fieldString", "value1");
		content.addPersistentField("wedge2", "fieldInteger", new Integer(1));
		content.addPersistentField("wedge3", "fieldDate", new Date());

		LinkedHashMap lhm = new LinkedHashMap();
		lhm.put("key1", new Boolean(true));
		lhm.put("key2", "frog");
		lhm.put("key3", new Float(1.3));

		content.addPersistentField("wedge4", "fieldLinkedHashMap", lhm);

		content.addPersistentField("wedge5", "fieldSpace", Space.HYPERSPACE);

		XMLEncoder encoder;
		try {
			encoder = new XMLEncoder(new BufferedOutputStream(new FileOutputStream("c:\\test.xml")));

			encoder.setExceptionListener(new ExceptionListener() {
				public void exceptionThrown(Exception exception) {
					exception.printStackTrace();
				}
			});

			encoder.setPersistenceDelegate(MeemContent.class, new MeemContentPersistenceDelegate());
			encoder.setPersistenceDelegate(Space.class, new SpacePersistenceDelegate());

			encoder.writeObject(content);

			encoder.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		XMLDecoder decoder;
		MeemContent decoded = null;
		try {
			decoder = new XMLDecoder(new BufferedInputStream(new FileInputStream("c:\\test.xml")));

			decoded = (MeemContent) decoder.readObject();
			decoder.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		Collection wedgeNames = decoded.getWedgeIdentifiers();

		for (Iterator iNames = wedgeNames.iterator(); iNames.hasNext();) {
			String wedgeName = (String) iNames.next();

			Map wedgeFields = decoded.getPersistentFields(wedgeName);

			if (wedgeFields != null) {
				for (Iterator iFields = wedgeFields.entrySet().iterator(); iFields.hasNext();) {

					Map.Entry entry = (Map.Entry) iFields.next();

					System.out.println(wedgeName + " " + entry.getKey() + " " + entry.getValue());

				}
			}
		}
	}

	public void testDefinition() {
		MeemAttribute meemAttribute = new MeemAttribute();
		meemAttribute.setScope(Scope.FEDERATED);
		meemAttribute.setIdentifier("test_definition");
		meemAttribute.setVersion(1);

		MeemDefinition meemDefinition = new MeemDefinition(meemAttribute);

		try {
			meemDefinition.addWedgeDefinition(WedgeIntrospector.getWedgeDefinition(CategoryWedge.class));
		} catch (WedgeIntrospectorException e) {
			e.printStackTrace();
		}
		
		XMLEncoder encoder;
		try {
			encoder = new XMLEncoder(new BufferedOutputStream(new FileOutputStream("c:\\test.xml")));

			encoder.setExceptionListener(new ExceptionListener() {
				public void exceptionThrown(Exception exception) {
					exception.printStackTrace();
				}
			});

			encoder.writeObject(meemDefinition);

			encoder.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		XMLDecoder decoder;
		MeemDefinition decoded = null;
		try {
			decoder = new XMLDecoder(new BufferedInputStream(new FileInputStream("c:\\test.xml")));

			decoded = (MeemDefinition) decoder.readObject();
			decoder.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		System.out.println(meemDefinition);
		System.out.println(decoded);
	}

	public static void main(String[] args) {
		BeanXMLTest et = new BeanXMLTest();
		//et.testContent();
		et.testDefinition();
	}

}
