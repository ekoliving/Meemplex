package org.openmaji.automation.protocol;

import org.openmaji.automation.address.AddressSpecification;

public class SimpleProtocol implements Protocol {

	private static final long serialVersionUID = 0L;
	
	private AddressSpecification addressSpecification;
	
	private String name;
	
	public SimpleProtocol(String name, AddressSpecification addressSpecification) {
		this.name = name;
		this.addressSpecification = addressSpecification;
    }
	
	public AddressSpecification getAddressSpecification() {
		return addressSpecification;
	}

	public String getName() {
		return name;
	}

}
