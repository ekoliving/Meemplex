/*
 * Copyright 2004 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.automation.common;

import org.openmaji.automation.address.AddressChanged;
import org.openmaji.automation.device.Device;
import org.openmaji.automation.device.DeviceDescription;
import org.openmaji.meem.Wedge;
import org.openmaji.meem.filter.Filter;
import org.openmaji.meem.wedge.configuration.ConfigurationClient;
import org.openmaji.meem.wedge.configuration.ConfigurationClientAdapter;
import org.openmaji.meem.wedge.configuration.ConfigurationSpecification;
import org.openmaji.meem.wedge.dependency.DependencyHandler;
import org.openmaji.system.meem.wedge.reference.ContentException;
import org.openmaji.system.meem.wedge.reference.ContentProvider;


//TODO See JIRA IMJ-1181

/**
 * @author Chris Kakris
 */
public class DeviceWedge implements Wedge, Device {

	/* ----------------------------- outbound facets ------------------------------- */

	public Device deviceOutput;

	public final ContentProvider deviceOutputProvider = new ContentProvider() {
		public void sendContent(Object target, Filter filter) throws ContentException {
			if (deviceDescription != null) {
				Device client = (Device) target;
				client.descriptionChanged(deviceDescription);
			}
		}
	};


	/* -------------------------------- conduits ----------------------------------- */
	
	public AddressChanged addressChangedConduit;

	public ConfigurationClient configurationClientConduit = new ConfigurationClientAdapter(this);

	public DependencyHandler dependencyHandlerConduit;
	

	/* -------------------------- persisted properties ----------------------------- */
	
	public String address;

	public transient ConfigurationSpecification addressSpecification = new ConfigurationSpecification("The address of the device");

	public String identifier;

	public transient ConfigurationSpecification identifierSpecification = new ConfigurationSpecification("The identifier of the device");

	public String description;

	public transient ConfigurationSpecification descriptionSpecification = new ConfigurationSpecification("A description of the device");

	public DeviceDescription deviceDescription;


	/* ------------------------ inbound Device facet -------------------------------- */

	public void descriptionChanged(DeviceDescription newDeviceDescription) {
		if (newDeviceDescription == null) {
			return;
		}

		addressChangedConduit.addressChanged(newDeviceDescription.getAddress());

		deviceDescription = newDeviceDescription;
		if (newDeviceDescription.getAddress() != null) {
			address = newDeviceDescription.getAddress().toString();
		}

		identifier = newDeviceDescription.getIdentifier();
		description = newDeviceDescription.getDescription();
	}


	/* ------------------ ConfigurationChangeHandler listener ----------------------- */

	public void setAddress(String address) {
		this.address = address;
		addressChangedConduit.addressChanged(address);

		//    deviceDescription.setAddress(address);  <- Damn. The address is type String but we need it as type Address!
		//    deviceOutput.descriptionChanged(deviceDescription);
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
		if (deviceDescription != null) {
			deviceDescription.setIdentifier(identifier);
			deviceOutput.descriptionChanged(deviceDescription);
		}
	}

	public void setDescription(String description) {
		this.description = description;
		if (deviceDescription != null) {
			deviceDescription.setDescription(description);
			deviceOutput.descriptionChanged(deviceDescription);
		}
	}
	
}
