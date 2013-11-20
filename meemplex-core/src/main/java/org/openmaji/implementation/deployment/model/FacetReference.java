package org.openmaji.implementation.deployment.model;

import javax.xml.bind.annotation.XmlAttribute;

public class FacetReference {

	@XmlAttribute(name="path")
	private String path;
	
	@XmlAttribute(name="facet-id")
	private String facetId;
}
