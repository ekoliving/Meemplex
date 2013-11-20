package org.meemplex.service.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * A Wedge configuration property
 * 
 * @author stormboy
 *
 */
@XmlRootElement(name="property")
@XmlType(propOrder = {"name", "description", "type", "value"})
public class PropertyDesc implements Serializable {
	
	private static final long serialVersionUID = 0L;	

	/**
	 * The name of the property.
	 * This is an alpha-numeric string, possibly with "/" character separating items.
	 */
	private String name;

	/**
	 * A description of what this property is for
	 */
	private String description;

	/**
	 * The type of property
	 * Number, String, Boolean, List/Array, Object
	 */
	private String type;
	
	/**
	 * A string representation of the value of the property
	 */
	private String value;


	public void setName(String name) {
	    this.name = name;
    }

	public String getName() {
	    return name;
    }

	public void setDescription(String description) {
	    this.description = description;
    }

	public String getDescription() {
	    return description;
    }

	public void setType(String type) {
	    this.type = type;
    }

	public String getType() {
	    return type;
    }

	public void setValue(String value) {
	    this.value = value;
    }

	public String getValue() {
	    return value;
    }
}
