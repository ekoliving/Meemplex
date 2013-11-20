package org.meemplex.server.model.meemkit;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name="meemkit")
@XmlType
public class Meemkit {
	
	@XmlAttribute
	public String name;
	
	@XmlAttribute
	public String 		  version;
	
	@XmlAttribute
	public String 		  author;
	
	@XmlAttribute
	public String 		  company;
	
	@XmlAttribute
	public String 		  copyright;
	
	@XmlAttribute
	public String 		  resourceClass;
	
	public String summary;
	
	@XmlElementWrapper(name="dependencies")
	@XmlElement(name="dependentMeemkit")
	public List<Dependency> dependencies;
	
	@XmlElementWrapper(name="libraries")
	@XmlElement(name="library")
	public List<Library> libraries;
	
	@XmlElement(name="meemview")
	public MeemView meemView;
	
	@XmlElement(name="wedgeview")
	public WedgeView wedgeView;
	
}
