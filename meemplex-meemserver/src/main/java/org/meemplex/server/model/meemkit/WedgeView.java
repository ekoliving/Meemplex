package org.meemplex.server.model.meemkit;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

public class WedgeView {
	
	@XmlElementWrapper(name="hierarchy")
	@XmlElement(name="category")
	public List<Category> hierarchy;
	
	@XmlElement(name="entry")
	public List<WedgeEntry> entry;

}
