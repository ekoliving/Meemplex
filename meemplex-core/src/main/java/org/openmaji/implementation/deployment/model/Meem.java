package org.openmaji.implementation.deployment.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

@XmlType(name="meem")
public class Meem {

	@XmlAttribute
	private String id;

	@XmlElement
	private Object definition;
	
	@XmlElementWrapper(name="dependencies")
	@XmlElement(name="dependency")
	private List<Dependency> dependencies;
	
	@XmlElement(name="path")
	private List<Object> paths;
}
