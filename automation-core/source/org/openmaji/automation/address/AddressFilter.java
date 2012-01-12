package org.openmaji.automation.address;

import org.openmaji.meem.filter.Filter;

/**
 * This is a dependency filter that is used for determining whether a message for or
 * from an automation device is meant for a particular device meem.
 *  
 * @author stormboy
 *
 */
public class AddressFilter implements Filter {
	
	private static final long serialVersionUID = 0L;
	
	private Address address;

	/**
	 * 
	 * @param address
	 */
	public AddressFilter(Address address) {
		this.address = address;
	}
	
	/**
	 * 
	 */
	public Address getAddress() {
		return address;
	}
	
	/**
	 * 
	 * @param address
	 */
	public void setAddress(Address address) {
		this.address = address;
	}
	
	/**
	 * If the address specification matches the address of this filter.
	 * 
	 * @param address
	 */
	public boolean matches(Address address) {
		boolean result = false;
		
		if ( address != null && address.equals(this.address) ) {
			result = true;
		}
		
		return result;
	}
}
