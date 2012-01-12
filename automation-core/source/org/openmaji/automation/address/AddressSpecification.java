/*
 * @(#)AddressSpecification.java
 *
 *  Copyright 2004 by Majitek Limited.  All Rights Reserved.
 *
 *  This software is the proprietary information of Majitek Limited.
 *  Use is subject to license terms.
 */

package org.openmaji.automation.address;

import java.util.ArrayList;
import java.util.List;

/**
 * Used to define the structure of an address. An address is made up of a
 * number of AddressParts. Each part specifies how a portion of the address
 * looks like. Used together, a list of parts can construct and validate
 * addresses. 
 *
 * @author  mg
 * @version 1.0
 */
public abstract class AddressSpecification {
	
	protected List<AbstractAddressPart> parts = new ArrayList<AbstractAddressPart>();
	
	/**
   * Add an AddressPart.
   * 
   * @param part The part to add
   */
  public void addAddressPart(AbstractAddressPart part) {
		parts.add(part);
	}
	
	/**
   * Returns the list of address parts that make up this address specification.
   * 
   * @return  An array of AddressPart
   */
  public AbstractAddressPart[] getAddressParts() {
		return (AbstractAddressPart[])parts.toArray(new AbstractAddressPart[0]);
	}
	
	/**
   * Set the address.
   * 
   * @param address  The address
   */
  abstract public void setAddress(String address);

	/**
   * Return the address of this specification
   * 
   * @return The String representation of this specification
   */
  abstract public String getAddressString();
  
  abstract public Address getAddress();
	
}
