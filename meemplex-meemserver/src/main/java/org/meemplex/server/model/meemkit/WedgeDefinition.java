package org.meemplex.server.model.meemkit;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlType
public class WedgeDefinition {

	@XmlAttribute(name="class")
	public String className;
}
