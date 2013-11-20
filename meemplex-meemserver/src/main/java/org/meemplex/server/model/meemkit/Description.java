package org.meemplex.server.model.meemkit;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlType
public class Description {

	@XmlAttribute
	public String overview;
	
	public String detail;
}
