package org.meemplex.server.model.meemkit;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlType
public class Dependency {

	@XmlAttribute
	public String name;
	
	@XmlAttribute
	public String version;
}
