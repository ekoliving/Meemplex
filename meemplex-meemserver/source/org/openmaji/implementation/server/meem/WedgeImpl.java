/*
 * @(#)WedgeImpl.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

/* ToDo:
 * - Consider adding all System Wedge in-bound Facets to the Dynamic
 *   Proxy so that you can still do all the System Wedge stuff with any
 *   given Reference.  However, have to watch out for method signature
 *   collisions, which could be a significant problem.  So, also consider
 *   just adding the ReferenceHandler to the Dynamic Proxy, so that having
 *   any Reference to a part of a Meem can be used to get the other Facets.
 *   Perhaps the best way to go would be to create a System Wedge that can
 *   provide a Reference to the core Meem with all it's System Wedges.
 *   This minimal Wedge can be added to all Dynamic Proxies.
 *
 * - Consider whether this should have a org.openmaji.meem.Wedge interface.
 *
 * - Consider whether getWedgeInterfaces(Direction) should be generalized.
 */

package org.openmaji.implementation.server.meem;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.*;

import org.meemplex.meem.Conduit;
import org.meemplex.meem.MeemContext;
import org.openmaji.implementation.server.classloader.MeemkitClassLoader;
import org.openmaji.implementation.server.meem.core.MeemContextImpl;
import org.openmaji.implementation.server.request.RequestContextImpl;
import org.openmaji.implementation.server.utility.ConduitImpl;
import org.openmaji.implementation.server.utility.ObjectUtility;
import org.openmaji.meem.Facet;
import org.openmaji.meem.MeemPath;
import org.openmaji.meem.definition.FacetOutboundAttribute;
import org.openmaji.meem.definition.WedgeAttribute;
import org.openmaji.meem.wedge.configuration.ConfigurationIdentifier;
import org.openmaji.meem.wedge.configuration.ConfigurationSpecification;
import org.openmaji.system.meem.core.MeemCore;
import org.openmaji.system.meem.definition.MeemContent;
import org.openmaji.utility.CollectionUtility;
import org.swzoo.log2.core.LogFactory;
import org.swzoo.log2.core.LogTools;
import org.swzoo.log2.core.Logger;

/**
 * <p>
 * This is the run-time model for a Meem Wedge instance.
 * </p>
 * <p>
 * The WedgeImpl maintains "Meem instance" information used at run-time and does not duplicate any of the information already contained in the WedgeAttribute.
 * </p>
 * <p>
 * A WedgeImpl consists of a reference to the WedgeDefinition, a collection of Facets, a Wedge implementation class and an indicator for whether this is a system or an application
 * Wedge.
 * </p>
 * <p>
 * The WedgeImpl can also provide a Dynamic Proxy Object "invocationTarget" for the Wedge implementation. This invocationTarget is built on-the-fly as required, depending on
 * whether Facets have been added or removed from the Wedge. The invocationTarget allows the Feature invocation mechanism to intercept every in-bound Facet method call.
 * </p>
 * <p>
 * Note: Implementation thread safe = Single-threaded (2003-04-01)
 * </p>
 * 
 * @author Andy Gelme
 * @version 1.0
 * @see org.openmaji.meem.Meem
 * @see org.openmaji.meem.definition.WedgeAttribute
 */

public class WedgeImpl {
	/**
	 * Collection of inbound Facets
	 */
	private Map<String, InboundFacetImpl> inboundFacets = new HashMap<String, InboundFacetImpl>();

	/**
	 * Collection of outbound facets
	 */
	private Map<String, OutboundFacetImpl> outboundFacets = new HashMap<String, OutboundFacetImpl>();

	/**
	 * Implementation class instance
	 */
	private Object implementation;

	/**
	 * Indicates whether this is a system defined or application defined Wedge
	 */
	private boolean systemWedge;

	/**
	 * Definition for this Wedge instance
	 */
	private WedgeAttribute wedgeAttribute;

	private MeemCore meemCore;

	private final String implementationClassName;

	/**
	 * Create a WedgeImpl.
	 * 
	 * @param meemCore
	 *            current meem core
	 * @param wedgeAttribute
	 *            Definition for this Wedge instance
	 * @param systemWedge
	 *            Is a system defined Wedge
	 * @exception IllegalArgumentException
	 *                Unknown implementation class
	 * @exception RuntimeException
	 *                Problem instantiating the class
	 */
	public WedgeImpl(MeemCore meemCore, WedgeAttribute wedgeAttribute, boolean systemWedge) throws ClassNotFoundException, IllegalArgumentException, RuntimeException {
		this.wedgeAttribute = wedgeAttribute;
		this.systemWedge = systemWedge;
		this.implementationClassName = wedgeAttribute.getImplementationClassName();

		try {
			implementation = ObjectUtility.create(Object.class, wedgeAttribute.getImplementationClassName());
		}
		catch (IllegalAccessException illegalAccessException) {
			throw new RuntimeException("IllegalAccessException creating: " + implementation);
		}
		catch (InstantiationException instantiationException) {
			throw new RuntimeException("InstantiationException creating: " + implementation);
		}

		this.meemCore = meemCore;

		// if a MeemkitClassloader was used to load the class, store it
		ClassLoader classLoader = implementation.getClass().getClassLoader();
		if (classLoader instanceof MeemkitClassLoader) {
			((MeemkitClassLoader) classLoader).addReferencedMeemPath(meemCore.getMeemPath());
		}

		// setup fields, like MeemContext and Conduits
		processFields(implementation);
	}

	/**
	 * Create a WedgeImpl using an existing Wedge implementation instance.
	 * 
	 * @param meemCore
	 *            current meem core
	 * @param wedgeAttribute
	 *            Definition for this Wedge instance
	 * @param systemWedge
	 *            Is a system defined Wedge
	 * @param implementation
	 *            Existing Wedge implementation
	 * @exception IllegalArgumentException
	 *                Unknown implementation class
	 * @exception RuntimeException
	 *                Problem instantiating the class
	 */

	public WedgeImpl(MeemCore meemCore, WedgeAttribute wedgeAttribute, boolean systemWedge, Object implementation) throws IllegalArgumentException, RuntimeException {
		this.implementationClassName = implementation.getClass().getName();

		String wedgeAttributeClassName = wedgeAttribute.getImplementationClassName();

		if (implementationClassName.equals(wedgeAttributeClassName)) {
			this.wedgeAttribute = wedgeAttribute;
			this.systemWedge = systemWedge;
			this.implementation = implementation;
		}
		else {
			throw new IllegalArgumentException("Existing Wedge implementation: " + implementationClassName + " can't be used as part of WedgeAttribute: " + wedgeAttributeClassName);
		}

		this.meemCore = meemCore;

		processFields(implementation);
	}

	/**
	 * Setup special fields of the wedge, like MeemContext and Conduits
	 * 
	 * @param implementation
	 */
	private void processFields(Object implementation) {
		Class<?> specification = implementation.getClass();

		try {
			Field[] fields = specification.getFields();

			for (int i = 0; i != fields.length; i++) {
				Field field = fields[i];
				String fieldName = field.getName();

				if (fieldName.equals("meemCore") && field.getType().equals(MeemCore.class)) {
					field.set(implementation, meemCore);
				}
				else if (field.getAnnotation(MeemContext.class) != null || fieldName.equals("meemContext")) {
					field.set(implementation, new MeemContextImpl(meemCore, this));
				}
				else if (fieldName.equals("requestContext")) {
					field.set(implementation, new RequestContextImpl(meemCore, meemCore.getMeemPath()));
				}
				else if (field.getAnnotation(Conduit.class) != null || fieldName.endsWith("Conduit")) {
					String conduitName = fieldName;
					if (fieldName.endsWith("Conduit")) {
						conduitName = fieldName.substring(0, fieldName.length() - "Conduit".length());
					}
					Conduit c = field.getAnnotation(Conduit.class);
					if (c != null && c.name() != null && !c.name().isEmpty()) {
						conduitName = c.name();
					}
					
					// conduit class must be a Facet (for now)
					Class<? extends Facet> type = (Class<? extends Facet>)field.getType();
					
					Object conduit = meemCore.getConduitSource(conduitName, type);
					Object value = field.get(implementation);

					if (value != null) {
						if (value != implementation) {
							processFields(value);
						}

						if (Proxy.isProxyClass(conduit.getClass())) {
							ConduitImpl conduitImpl = (ConduitImpl) Proxy.getInvocationHandler(conduit);
							conduitImpl.addTarget(value, implementation);
						}
						else {
							// this should never be called
							meemCore.addConduitTarget(conduitName, type, value);
						}
					}

					field.set(implementation, conduit);
				}
				else if (Modifier.isPublic(field.getModifiers()) && field.getType() == ConfigurationSpecification.class) {
					ConfigurationSpecification spec = (ConfigurationSpecification) field.get(implementation);
					String valueName = field.getName().substring(0, field.getName().length() - "Specification".length());

					spec.setIdentifier(new ConfigurationIdentifier(this.getWedgeAttribute().getIdentifier(), valueName));
				}
			}
		}
		catch (IllegalAccessException e) {
			e.printStackTrace();

			throw new IllegalArgumentException("Wedge " + specification + " illegal access for field - " + e.getMessage(), e);
		}
	}

	/**
	 * @param wedgeFields
	 */
	public void setWedgeContent(Map<String, Serializable> wedgeFields) {
		Object wedgeImplClass = this.getImplementation();

		Set<Map.Entry<String, Serializable>> fields = wedgeFields.entrySet();

		if (fields == null) { // something is wrong
			return;
		}

		for (Map.Entry<String, Serializable> entry : fields) {

			String fieldName = (String) entry.getKey();
			Object fieldValue = entry.getValue();

			// get the Field
			Field field = null;
			try {
				field = wedgeImplClass.getClass().getField(fieldName);
			}
			catch (SecurityException e) {
				LogTools.error(logger, "Exception while getting field [" + fieldName + "]", e);
				continue;
			}
			catch (NoSuchFieldException e) {
				LogTools.error(logger, "Exception while getting field[" + fieldName + "]", e);
				continue;
			}

			if (field != null) {
				try {
					field.set(wedgeImplClass, fieldValue);
				}
				catch (IllegalArgumentException e) {
					LogTools.error(logger, "Exception while setting field [" + fieldName + "] value [" + fieldValue + "]", e);
					continue;
				}
				catch (IllegalAccessException e) {
					LogTools.error(logger, "Exception while setting field [" + fieldName + "] value [" + fieldValue + "]", e);
					continue;
				}
			}
		}

	}

	/**
	 * Puts persistent fields from the Wedge implementation in the MeemContent
	 * Uses the WedgeAttribute to determine which fields are supposed to be persistent.
	 * 
	 * @param meemContent
	 */
	public void parseWedge(MeemContent meemContent) {
		Object implementation = this.getImplementation();

		WedgeAttribute wedgeAttribute = this.getWedgeAttribute();

		Collection<String> fields = wedgeAttribute.getPersistentFields();

		if (fields == null) { // something is wrong
			return;
		}

		for (String fieldName : fields) {

			// get the Field
			Field field = null;
			try {
				field = implementation.getClass().getField(fieldName);
			}
			catch (SecurityException e) {
				LogTools.error(logger, "Exception while getting field [" + fieldName + "]", e);
				continue;
			}
			catch (NoSuchFieldException e) {
				LogTools.error(logger, "Exception while getting field[" + fieldName + "]", e);
				continue;
			}

			if (field != null) {
				Serializable fieldValue = null;
				Serializable fieldValueCopy = null;
				try {
					fieldValue = (Serializable) field.get(implementation);

					// if (fieldValue != null) {
					// synchronized(fieldValue) {
					// if (!(fieldValue instanceof Serializable)) {
					// LogTools.error(logger, "Field [" + fieldName + "] is not Serializable");
					// continue;
					// }
					//
					try {
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						ObjectOutputStream oos = new ObjectOutputStream(baos);

						oos.writeObject(fieldValue);
						oos.close();

						ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
						ObjectInputStream ois = new ObjectInputStream(bais);

						fieldValueCopy = (Serializable) ois.readObject();

						ois.close();
					}
					catch (IOException e) {
						LogTools.error(logger, "IOException while copying field [" + fieldName + "]", e);
						continue;
					}
					catch (ClassNotFoundException e) {
						LogTools.error(logger, "ClassNotFoundException while copying field [" + fieldName + "]", e);
						continue;
					}

					// }
					// }
				}
				catch (IllegalArgumentException e) {
					LogTools.error(logger, "Exception while getting field [" + fieldName + "] value", e);
					continue;
				}
				catch (IllegalAccessException e) {
					LogTools.error(logger, "Exception while getting field [" + fieldName + "] value", e);
					continue;
				}

				meemContent.addPersistentField(wedgeAttribute.getIdentifier(), fieldName, fieldValueCopy);
			}
		}
	}

	/**
	 * <p>
	 * Add another Facet to the Wedge.
	 * </p>
	 * <p>
	 * If a Facet already exists that matches the new Facet, then the old Facet is replaced by the new one.
	 * </p>
	 * <p>
	 * This operation invalidates the invocationTarget.
	 * </p>
	 * 
	 * @param facetIdentifier
	 *            Uniquely identifies the Facet
	 * @param inboundFacetImpl
	 *            Run-time Facet model
	 */
	public void addInboundFacet(String facetIdentifier, InboundFacetImpl inboundFacetImpl) {
		inboundFacets.put(facetIdentifier, inboundFacetImpl);
	}

	/**
	 * <p>
	 * Add another Facet to the Wedge.
	 * </p>
	 * <p>
	 * If a Facet already exists that matches the new Facet, then the old Facet is replaced by the new one.
	 * </p>
	 * <p>
	 * Create an invocationSource.
	 * </p>
	 * 
	 * @param facetIdentifier
	 *            Uniquely identifies the Facet
	 * @param outboundFacetImpl
	 *            Run-time Facet model
	 */
	public void addOutboundFacet(String facetIdentifier, OutboundFacetImpl outboundFacetImpl) {
		try {
			FacetOutboundAttribute attr = (FacetOutboundAttribute) outboundFacetImpl.getFacetAttribute();
			Field field = this.implementation.getClass().getField(attr.getWedgePublicFieldName());

			Class<?> type = field.getType();

			if (type.isInterface() == false) {
				throw new IllegalArgumentException("Wedge implementation '" + this.implementationClassName + "' field '" + facetIdentifier + "' type '" + type.getName() + "' must be a Java interface");
			}

			field.set(implementation, (Proxy) outboundFacetImpl.makeProxy());
		}
		catch (NoSuchFieldException e) {
			throw new RuntimeException("no such facet identifier '" + facetIdentifier + "' on wedge.", e);
		}
		catch (IllegalAccessException e) {
			throw new RuntimeException("facet identifier '" + facetIdentifier + "' exists on wedge but not accessible.", e);
		}

		outboundFacets.put(facetIdentifier, outboundFacetImpl);
	}

	/**
	 * Provides an Iterator for all of the Facets.
	 * 
	 * @return Iterator for all of the Facets
	 */
	public Collection<FacetImpl> getFacets() {
		ArrayList<FacetImpl> facets = new ArrayList<FacetImpl>();
		facets.addAll(this.getInboundFacets());
		facets.addAll(this.getOutboundFacets());
		return facets;
	}

	/**
	 * <p>
	 * Remove the specified Facet from the Wedge.
	 * </p>
	 * <p>
	 * It is not considered to be a problem if the specified Facet doesn't exist.
	 * </p>
	 * <p>
	 * This operation invalidates the invocationTarget.
	 * </p>
	 * 
	 * @param facetIdentifier
	 *            FacetDefinition to remove
	 */
	public synchronized void removeInboundFacet(String facetIdentifier) {
		inboundFacets.remove(facetIdentifier);
	}

	/**
	 * <p>
	 * Remove the specified Facet from the Wedge.
	 * </p>
	 * <p>
	 * It is not considered to be a problem if the specified Facet doesn't exist.
	 * </p>
	 * 
	 * @param facetIdentifier
	 *            FacetDefinition to remove
	 */
	public synchronized void removeOutboundFacet(String facetIdentifier) {
		outboundFacets.remove(facetIdentifier);
	}

	/**
	 * Provides the Implementation instance.
	 * 
	 * @return Implementation instance
	 */
	public Object getImplementation() {
		return implementation;
	}

	/**
	 * Return the name of the underlying implementation class.
	 * 
	 * @return String
	 */
	public String getImplementationClassName() {
		return implementationClassName;
	}

	/**
	 * Provides the WedgeAttribute.
	 * 
	 * @return Definition for this Wedge instance
	 */

	public WedgeAttribute getWedgeAttribute() {
		return wedgeAttribute;
	}

	public Collection<OutboundFacetImpl> getOutboundFacets() {
		return new ArrayList<OutboundFacetImpl>(outboundFacets.values());
	}

	public Collection<InboundFacetImpl> getInboundFacets() {
		return new ArrayList<InboundFacetImpl>(inboundFacets.values());
	}

	/**
	 * Indicates whether a Wedge was defined by the system or the application.
	 * 
	 * @return True if this is a system Wedge
	 */
	public boolean isSystemWedge() {
		return systemWedge;
	}

	public void disconnectConduits() {
		Object implementation = getImplementation();
		Class<?> specification = implementation.getClass();

		try {
			Field[] fields = specification.getFields();

			for (int i = 0; i != fields.length; i++) {
				Field field = fields[i];
				String fieldName = field.getName();

				String conduitName = null;
				Conduit c = field.getAnnotation(Conduit.class);
				if (c != null) { // check conduit annotation
					conduitName = fieldName;
					if (c.name() != null && !c.name().isEmpty()) {
						conduitName = c.name();
					}
				}
				else if (fieldName.endsWith("Conduit")) { // old way of determining conduits
					conduitName = fieldName.substring(0, fieldName.length() - "Conduit".length());
				}
				
				if (conduitName != null) {
					Class<? extends Facet> type = (Class<? extends Facet>) field.getType();
					Object conduit = meemCore.getConduitSource(conduitName, type);

					if (Proxy.isProxyClass(conduit.getClass())) {
						ConduitImpl conduitImpl = (ConduitImpl) Proxy.getInvocationHandler(conduit);
						conduitImpl.removeDeclaringClassTarget(implementation);
					}
					field.set(implementation, null);
				}
			}
		}
		catch (IllegalAccessException e) {
			throw new IllegalArgumentException("Wedge " + specification + " illegal access for field - " + e.getMessage());
		}
	}

	public MeemPath getMeemPath() {
		return meemCore.getMeemPath();
	}

	/* ---------- Object class method overrides -------------------------------- */

	/**
	 * <p>
	 * Compares WedgeImpl to the specified object. The result is true, if and only if all the Facets are equal and the implementation and systemWedge flag must also match.
	 * </p>
	 * <p>
	 * Note: The invocationTarget is deliberately left out of equals().
	 * </p>
	 * 
	 * @return true if WedgeImpls are equal
	 */

	public synchronized boolean equals(Object object) {

		if (object == this)
			return (true);

		if ((object instanceof WedgeImpl) == false)
			return (false);

		WedgeImpl thatWedgeImpl = (WedgeImpl) object;

		if (implementation != thatWedgeImpl.getImplementation())
			return (false);

		if (systemWedge != thatWedgeImpl.isSystemWedge())
			return (false);

		if (wedgeAttribute.equals(thatWedgeImpl.getWedgeAttribute()) == false) {
			return (false);
		}

		return CollectionUtility.equals(this.getInboundFacets(), thatWedgeImpl.getInboundFacets()) && CollectionUtility.equals(this.getOutboundFacets(), thatWedgeImpl.getOutboundFacets());
	}

	/**
	 * <p>
	 * Provides the Object hashCode. Must follow the Object.hashCode() and Object.equals() contract.
	 * </p>
	 * <p>
	 * Note: The invocationTarget is deliberately left out of hashCode().
	 * </p>
	 * 
	 * @return WedgeImpl hashCode
	 */

	public synchronized int hashCode() {
		return (inboundFacets.hashCode() ^ outboundFacets.hashCode() ^ implementation.hashCode() ^ new Boolean(systemWedge).hashCode() ^ wedgeAttribute.hashCode());
	}

	/**
	 * Provides a String representation of the WedgeImpl.
	 * 
	 * @return String representation of the WedgeImpl
	 */
	public synchronized String toString() {
		return (getClass().getName() + "[" + "implementation=" + implementation + ", systemWedge=" + systemWedge + ", inboundFacets=" + inboundFacets + ", outboundFacets=" + outboundFacets + "]");
	}

	private static final Logger logger = LogFactory.getLogger();

}
