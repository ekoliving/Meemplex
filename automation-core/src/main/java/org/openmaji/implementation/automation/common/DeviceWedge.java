/*
 * Copyright 2004 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.automation.common;

import org.meemplex.meem.Conduit;
import org.meemplex.meem.ConfigProperty;
import org.meemplex.meem.Content;
import org.meemplex.meem.Facet;
import org.meemplex.meem.FacetContent;
import org.meemplex.service.model.Direction;
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
@org.meemplex.meem.Wedge
public class DeviceWedge implements Wedge, Device {

	/* ----------------------------- facets ------------------------------- */
	
	@Facet(name="deviceInput", direction=Direction.IN)
	public Device deviceInput = this;

	@Facet(name="deviceOutput", direction=Direction.OUT)
	public Device deviceOutput;

	@FacetContent(facet="deviceOutput")
	public final ContentProvider<Device> deviceOutputProvider = new ContentProvider<Device>() {
		public void sendContent(Device client, Filter filter) throws ContentException {
			if (deviceDescription != null) {
				client.descriptionChanged(deviceDescription);
			}
		}
	};


	/* -------------------------------- conduits ----------------------------------- */
	
	@Conduit(name="addressChanged")
	public AddressChanged addressChangedConduit;

	@Conduit(name="configurationClient")
	public ConfigurationClient configurationClientConduit = new ConfigurationClientAdapter(this);

	@Conduit(name="dependencyHandler")
	public DependencyHandler dependencyHandlerConduit;
	

	/* -------------------------- persisted properties ----------------------------- */

	@ConfigProperty(description="The address of the device")
	public String address;

	public transient ConfigurationSpecification<String> addressSpecification = ConfigurationSpecification.create("The address of the device");

	@ConfigProperty(description="The identifier of the device")
	public String identifier;

	public transient ConfigurationSpecification<String> identifierSpecification = ConfigurationSpecification.create("The identifier of the device");

	@ConfigProperty(description="A description of the device")
	public String description;

	public transient ConfigurationSpecification<String> descriptionSpecification = ConfigurationSpecification.create("A description of the device");

	@Content
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
