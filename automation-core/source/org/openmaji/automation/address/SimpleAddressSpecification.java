package org.openmaji.automation.address;

public class SimpleAddressSpecification extends AddressSpecification {

	private AbstractAddressPart addressPart;
	
	public SimpleAddressSpecification(AbstractAddressPart addressPart) {
		this.addressPart = addressPart;
    }
	
	@Override
	public Address getAddress() {
		return new SimpleAddress(addressPart.get());
	}

	@Override
	public String getAddressString() {
		return addressPart.get();
	}

	@Override
	public void setAddress(String address) {
		if (address != null && address.length() > 0) {
			addressPart.set(address);
		}
	}
}
