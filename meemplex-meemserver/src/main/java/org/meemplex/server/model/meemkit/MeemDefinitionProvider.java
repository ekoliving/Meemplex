package org.meemplex.server.model.meemkit;

import javax.xml.bind.annotation.XmlType;

import javax.xml.bind.annotation.XmlAttribute;

@XmlType
public class MeemDefinitionProvider {

	@XmlAttribute(name="class")
	public String className;
}
