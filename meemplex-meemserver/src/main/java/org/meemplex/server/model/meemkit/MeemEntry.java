package org.meemplex.server.model.meemkit;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlType
public class MeemEntry {
	
	@XmlAttribute
	public String name;

	@XmlAttribute
	public String path;

	@XmlAttribute
	public String icon;
	
	
}
