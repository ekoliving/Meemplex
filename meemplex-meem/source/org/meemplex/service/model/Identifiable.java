package org.meemplex.service.model;

import java.util.UUID;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A globally, uniquely identifiable object
 * 
 * @author stormboy
 *
 */
@XmlRootElement
public class Identifiable {

	/**
	 * A Meem UUID.
	 */
	private UUID id;

	public void setId(UUID id) {
	    this.id = id;
    }

	@XmlAttribute
	public UUID getId() {
	    return id;
    }

}
