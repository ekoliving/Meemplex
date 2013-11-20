package org.meemplex.service.model;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * A message from/to Meemplex to/from a system outside of Meemplex.
 * @author stormboy
 *
 */
@XmlRootElement(name="meemMessage")
public class MeemMessage {

	//private long sessionId;
	
	/**
	 * sequence
	 */
	public long messageId;
	
	/**
	 * MeemPath of the Meem this message is to/from.
	 */
	public Object meemPath;

	/**
	 * The name of the Facet on the meem this message is to/from
	 */
	public String facetId;
	
	/**
	 * The type of Facet. This defines the methods and parameters that are possible.
	 */
	public String facetType;

	/**
	 * The method of the message
	 */
	public String method;

	/**
	 * Parameters of the message
	 */
	public List<Object> params;
}
