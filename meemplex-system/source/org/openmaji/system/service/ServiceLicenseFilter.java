/*
 * Created on 21/07/2005
 */
package org.openmaji.system.service;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;

import org.openmaji.meem.MeemPath;
import org.openmaji.meem.filter.Filter;


/**
 * A filter to check against service events.
 * The filter includes a service type and the a MeemPath
 * to the Meem that will consume the service.
 * 
 * @author Warren Bloomer
 *
 */
public class ServiceLicenseFilter implements Filter, Serializable {
	private static final long serialVersionUID = 534540102928363464L;

	private final MeemPath instanceId;
	private final HashSet<String>  serviceTypes = new HashSet<String>();
	
	/**
	 * 
	 * @param serviceTypes A collection of the type of service to match against.
	 * @param instanceId The meempath of the Meem that consumes the service.
	 */
	public ServiceLicenseFilter(MeemPath instanceId, String[] serviceTypes) {
		this.instanceId = instanceId;
		if (serviceTypes != null) {
			for (int i=0; i<serviceTypes.length; i++) {
				this.serviceTypes.add(serviceTypes[i]);
			}
		}
	}
	
	public MeemPath getInstanceId() {
		return instanceId;
	}
	
	public Collection<String> getServiceTypes() {
		return serviceTypes;
	}

	/**
	 * Checks if the given details match with this filter.  If so, the 
	 * content is allowed to go through.
	 * If this filter has a null value for serviceType or instanceId, 
	 * the corresponding parameter is not checked.
	 * 
	 * @param serviceType The type of service.
	 * @param instanceId The meempath of the meem consuming the service.
	 * @return whether the details match this filter.
	 */
	public boolean matches(MeemPath instanceId, String serviceType) {
		return 
			(this.instanceId == null || this.instanceId.equals(instanceId)) &&
			(this.serviceTypes.contains(serviceType)) ;
	}
	
	public int hashCode() {
		int hashCode = 0;
		if (instanceId != null) {
			hashCode = instanceId.hashCode();
		}
		hashCode ^= serviceTypes.hashCode();
		return hashCode;
	}
	
	public boolean equals(Object object) {
		if (! (object instanceof ServiceLicenseFilter) ) {
			return false;
		}
			
		ServiceLicenseFilter other = (ServiceLicenseFilter)object;
		
		boolean instanceIdMatch =  
			(instanceId != null || other.instanceId == null) &&
			(instanceId == other.instanceId || instanceId.equals( other.instanceId ));
		
		return instanceIdMatch && serviceTypes.equals( other.serviceTypes );
	}
}
