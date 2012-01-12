package org.meemplex.service.model;

import java.io.Serializable;
import java.util.UUID;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * An event from/to a Meem
 * 
 * @author stormboy
 *
 */
public abstract class MeemEvent implements Serializable {
	
	private static final long serialVersionUID = 0L;

	/**
	 * The definitive UUID path for the Meem
	 */
	private UUID meemId;
	
	/**
	 * 
	 */
	protected MeemEvent() {
	}

	public void setMeemId(UUID meemId) {
	    this.meemId = meemId;
    }

	@XmlAttribute
	public UUID getMeemId() {
	    return meemId;
    }
}
