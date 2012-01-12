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
	private long messageId;
	
	/**
	 * MeemPath of the Meem this message is to/from.
	 */
	private Object meemPath;

	/**
	 * The name of the Facet on the meem this message is to/from
	 */
	private String facetId;
	
	/**
	 * The type of Facet. This defines the methods and parameters that are possible.
	 */
	private String facetType;

	/**
	 * The method of the message
	 */
	private String method;

	/**
	 * Parameters of the message
	 */
	private List<Object> params;
}
