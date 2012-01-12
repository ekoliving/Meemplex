/*
 * @(#)SubsystemDescriptor.java
 *
 * Copyright 2005 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */
package org.openmaji.implementation.deployment;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.jdom.Element;

/**
 * @author mg
 */
public class SubsystemDescriptor implements Descriptor {

	private String name;

	private HashSet<String> meemIds = new HashSet<String>();

	private boolean transientSubsystem = false;

	public SubsystemDescriptor() {
	}

	public SubsystemDescriptor(String name) {
		this.name = name;
	}

	public Collection<String> getMeemIds() {
		return meemIds;
	}

	public String getName() {
		return name;
	}

	public void addMeemId(String meemId) {
		meemIds.add(meemId);
	}

	public boolean isTransientSubsystem() {
		return transientSubsystem;
	}

	public void processElement(Element element) {
		this.name = element.getAttributeValue("name");
		String temp = element.getAttributeValue("transient");
		if (temp != null) {
			Boolean bool = new Boolean(temp);
			transientSubsystem = bool.booleanValue();
		}
		List meemElements = element.getChildren("meem");
		for (Iterator eilter = meemElements.iterator(); eilter.hasNext();) {
			Element meemElement = (Element) eilter.next();
			meemIds.add(meemElement.getAttributeValue("id"));
		}
	}
}
