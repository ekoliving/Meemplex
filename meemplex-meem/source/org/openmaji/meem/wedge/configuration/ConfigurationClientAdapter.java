/*
 * @(#)Configuration.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
package org.openmaji.meem.wedge.configuration;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openmaji.meem.MeemContext;
import org.openmaji.meem.Wedge;
import org.openmaji.meem.filter.Filter;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClient;
import org.openmaji.meem.wedge.lifecycle.LifeCycleState;
import org.openmaji.meem.wedge.lifecycle.LifeCycleTransition;

/**
 * General listener for property changes - if an array is passed in initially it is used to filter configuration requests.
 * <p>
 * The adapter class is designed to provide an easy way of specifying a listener on a configuration conduit. It uses introspection on its parent wedge to find out what
 * configuration properties can be listened for, what their initial values, if any, are and how to change the values being set.
 * <p>
 * Assuming a wedge has configuration properties associated with it, the adapter is instanced as follows:
 * 
 * <pre>
 * public ConfigurationClient configurationClientConduit = new ConfigurationClientAdapter(this);
 * </pre>
 * <p>
 * The adapter works according to the following rules:
 * <ul>
 * <li>unless the limiting constructor is used, the adapter builds up a list of configuration properties that are available based on the public fields in the wedge that are defined
 * as ConfigurationSpecification objects. If the limiting constructor is used the adapter uses the array passed in as the source of the list.
 * <li>Properties are assumed to have similar names to the specifications associated with them, for example, a property called "hostName" should have a specification associated
 * with it called "hostNameSpecification"
 * <li>There should be a public set method associated with each property that takes the type associated with the property as an argument, for example, if "hostName" is of type
 * String, the adapter expects to find a method defined as:
 * 
 * <pre>
 *     public void setHostName(String hostName)
 * </pre>
 * 
 * <li>Optionally there can also be a get method associated with the property that returns its value. So for "hostName" we could also define:
 * 
 * <pre>
 *     public String getHostName()
 * </pre>
 * 
 * to return the value of the property. The get method is useful for situations where the property is not part of a meem's persistent content or type being published by the
 * configuration specification is not the same as the type of the object.
 * </ul>
 */
public class ConfigurationClientAdapter implements ConfigurationClient {
	private Map<ConfigurationIdentifier, ConfigurationSpecification> specificationMap = new HashMap<ConfigurationIdentifier, ConfigurationSpecification>();

	private Wedge parent;

	private Class<? extends Wedge> specification;

	private boolean initialised = false;

	private ConfigurationSpecification[] initialSpecifications = null;

	private LifeCycleState currentState;

	public MeemContext meemContext;

	public ConfigurationClient configurationClientConduit;

	public LifeCycleClient lifeCycleClientConduit = new LifeCycleClient() {
		public void lifeCycleStateChanging(LifeCycleTransition transition) {
			// ignore
		}

		public void lifeCycleStateChanged(LifeCycleTransition transition) {
			currentState = transition.getCurrentState();
		}
	};

	public ConfigurationHandler configurationHandlerConduit = new ConfigurationHandler() {
		/**
		 * Call this if we want to tell the world that a particular property has changed.
		 */
		public final void valueChanged(ConfigurationIdentifier id, Serializable value) {
			if (!initialised) {
				initialise();
			}

			//
			// fliter so the local implementation only gets requests it asked for.
			//
			if (specificationMap.containsKey(id)) {
				ConfigurationSpecification spec = (ConfigurationSpecification) specificationMap.get(id);
				StringBuffer methodName = new StringBuffer("set");

				methodName.append(id.getFieldName());
				methodName.setCharAt(3, id.getFieldName().toUpperCase().charAt(0));

				String check = ((ConfigurationSpecification) specificationMap.get(id)).validate(value);
				if (check != null) {
					logger.log(Level.INFO, check);
					configurationClientConduit.valueRejected(id, value, check);
					return;
				}

				if (LifeCycleState.STATES.indexOf(currentState) > LifeCycleState.STATES.indexOf(spec.getMaxLifeCycleState())) {
					String message = "property " + id.getFieldName() + " cannot be configured above LifeCycleState " + spec.getMaxLifeCycleState().getCurrentState();
					logger.log(Level.INFO, message);
					configurationClientConduit.valueRejected(id, value, message);
					return;
				}

				try {
					Method method = specification.getMethod(methodName.toString(), new Class[] { ((ConfigurationSpecification) specificationMap.get(id)).getType() });
					method.invoke(parent, new Object[] { value });
					configurationClientConduit.valueAccepted(id, value);
				}
				catch (NoSuchMethodException e) {
					String message = "no set method for property: " + id.getFieldName() + "(" + e + ")";
					logger.log(Level.INFO,  message);
					configurationClientConduit.valueRejected(id, value, message);
				}
				catch (IllegalAccessException e) {
					String message = "cannot access set method for property: " + id.getFieldName() + "(" + e + ")";
					logger.log(Level.INFO,  message);
					configurationClientConduit.valueRejected(id, value, message);
				}
				catch (Exception e) {
					StringBuffer buffer = new StringBuffer("exception while invoking set() method for ");
					buffer.append(id.getFieldName());
					buffer.append(". message=[");
					buffer.append(e.getMessage());
					buffer.append(']');
					if (e.getCause() != null) {
						buffer.append(" cause=[");
						buffer.append(e.getCause().getMessage());
						buffer.append(']');
					}
					logger.log(Level.INFO, buffer.toString());
					if (e instanceof InvocationTargetException) {
						InvocationTargetException ie = (InvocationTargetException) e;

						if (ie.getCause() == null) {
							String message = "invocationTargetException occured but no cause available: " + ie.getMessage();
							configurationClientConduit.valueRejected(id, value, message);
						}
						else {
							if (ie.getCause() instanceof ConfigurationRejectedException) {
								configurationClientConduit.valueRejected(id, value, ie.getCause().getMessage());
							}
							else if (ie.getCause() instanceof IllegalArgumentException) {
								configurationClientConduit.valueRejected(id, value, "exception using set method for property: " + id.getFieldName() + "(" + ie.getCause().getMessage() + ")");
							}
							else {
								configurationClientConduit.valueRejected(id, value, "exception using set method for property: " + id.getFieldName() + "(" + ie.getCause() + ")");
							}
						}
					}
					else {
						configurationClientConduit.valueRejected(id, value, "exception using set method for property: " + id.getFieldName() + "(" + e + ")");
					}
				}
			}
		}
	};

	public ConfigurationProvider configurationProviderConduit = new ConfigurationProvider() {
		/**
		 * provide config details if any have been published.
		 */
		public void provideConfiguration(ConfigurationClient client, Filter filter) {
			initialise();

			if (specificationMap.size() != 0) {
				ConfigurationSpecification[] specs = (ConfigurationSpecification[]) specificationMap.values().toArray(new ConfigurationSpecification[specificationMap.size()]);
				client.specificationChanged(null, specs);

				for (int i = 0; i != specs.length; i++) {
					try {
						Field valueField = specification.getField(specs[i].getIdentifier().getFieldName());
						Serializable value = (Serializable) valueField.get(parent);

						if (specs[i].getType() == String.class) {
							client.valueAccepted(specs[i].getIdentifier(), (value != null) ? value.toString() : null);
						}
						else {
							client.valueAccepted(specs[i].getIdentifier(), value);
						}
					}
					catch (Exception e) {
						//
						// look for a get method.
						//
						ConfigurationIdentifier id = specs[i].getIdentifier();
						StringBuffer methodName = new StringBuffer("get");

						methodName.append(id.getFieldName());
						methodName.setCharAt(3, id.getFieldName().toUpperCase().charAt(0));

						try {
							Method method = specification.getMethod(methodName.toString(), (Class[]) null);

							Serializable value = (Serializable) method.invoke(parent, (Object[]) null);

							client.valueAccepted(id, value);
						}
						catch (Exception ex) {
							// [TODO] dgh - log?
							ex.printStackTrace();
						}
					}
				}
			}
		}
	};

	private void initialise() {
		if (initialised) {
			return;
		}

		Class<? extends Wedge> specification = parent.getClass();

		Field[] fields = specification.getFields();

		for (int i = 0; i != fields.length; i++) {
			Field field = fields[i];

			// TODO get ConfigProperty annotations too.
			
			if (Modifier.isPublic(field.getModifiers()) && field.getType() == ConfigurationSpecification.class) {
				String valueName = field.getName().substring(0, field.getName().length() - "Specification".length());
				try {
					ConfigurationSpecification spec = (ConfigurationSpecification) field.get(parent);

					if (spec == null) {
						logger.log(Level.INFO, "Ignoring null configuration specification: " + field.getName() + " in " + parent.getClass());
						continue;
					}

					try {
						Field valueField = specification.getField(valueName);

						// TODO check if Field declaring class inherits Serializable
						// Serializable.class.isAssignableFrom(valueField.getDeclaringClass());

						spec.setDefaultValue((Serializable) valueField.get(parent));
					}
					catch (Exception e) {
						//
						// look for a get method.
						//
						StringBuffer methodName = new StringBuffer("get");

						methodName.append(valueName);
						methodName.setCharAt(3, valueName.toUpperCase().charAt(0));

						try {
							Method method = specification.getMethod(methodName.toString(), (Class[]) null);

							Object value = method.invoke(parent, (Object[]) null);

							spec.setDefaultValue((Serializable) value);
						}
						catch (Exception ex) {
							logger.log(Level.INFO, "cannot set default value for configuration property '" + valueName + "' in " + parent.getClass() + " reason: " + ex.getMessage());
						}
					}
				}
				catch (Exception e) {
					logger.log(Level.INFO, "cannot get access to configuration specification: " + field.getName() + " in " + parent.getClass());
				}
			}
		}

		for (int i = 0; i != fields.length; i++) {
			Field field = fields[i];

			if (Modifier.isPublic(field.getModifiers()) && field.getType() == ConfigurationSpecification.class) {
				try {
					ConfigurationSpecification spec = (ConfigurationSpecification) field.get(parent);

					if (initialSpecifications != null) {
						for (int j = 0; j != initialSpecifications.length; j++) {
							if (spec == initialSpecifications[j]) {
								specificationMap.put(spec.getIdentifier(), spec);
								break;
							}
						}
					}
					else {
						specificationMap.put(spec.getIdentifier(), spec);
					}
				}
				catch (Exception e) {
					e.printStackTrace();
					// TODO [dgh] - we need to decide a policy on this...
				}
			}
		}

		initialised = true;
	}

	/**
	 * Basic constructor - in this case the adapter will listen for any configuration property defined by the ConfigurationSpecifications it contains.
	 * 
	 * @param parent
	 *            the wedge containing the adapter.
	 */
	public ConfigurationClientAdapter(Wedge parent) {
		this.parent = parent;
		this.specification = parent.getClass();
	}

	/**
	 * Constructor which limits the configuration properties listened for to those described in the specifications.
	 * 
	 * @param parent
	 * @param specifications
	 */
	public ConfigurationClientAdapter(Wedge parent, ConfigurationSpecification[] specifications) {
		this(parent);

		initialSpecifications = specifications;
	}

	/**
	 * Broadcast that our configuration has changed.
	 * 
	 * @param oldSpecifications
	 *            our original, if any, property specifications.
	 * @param newSpecifications
	 *            the specifications for the properties, if any, we are now listening for.
	 */
	public final void specificationChanged(ConfigurationSpecification[] oldSpecifications, ConfigurationSpecification[] newSpecifications) {
		initialise();

		if (oldSpecifications != null) {
			for (int i = 0; i < oldSpecifications.length; i++) {
				specificationMap.remove(oldSpecifications[i].getIdentifier());
			}
		}

		for (int i = 0; i < newSpecifications.length; i++) {
			ConfigurationSpecification specification = newSpecifications[i];
			specificationMap.put(specification.getIdentifier(), specification);
		}

		//performConfigurationAvailableChanged(oldSpecifications, newSpecifications);
	}

	/**
	 * Broadcast that a particular property has had a change accepted.
	 * 
	 * @param id
	 *            the identifier associated with the property whose change we have accepted.
	 * @param value
	 *            the value the property now has.
	 */
	public final void valueAccepted(ConfigurationIdentifier id, Serializable value) {
		initialise();

		//performConfigurationAccepted(id, value);
	}

	/**
	 * Broadcast that a particular change attempt has been rejected.
	 * 
	 * @param id
	 *            the property the rejection is associated with.
	 * @param value
	 *            the value that was rejected.
	 * @param reason
	 *            the reason for the rejection.
	 */
	public final void valueRejected(ConfigurationIdentifier id, Serializable value, Serializable reason) {
		initialise();

		//performConfigurationRejected(id, value, reason);
	}

//	/*
//	 * @deprecated do not use!
//	 */
//	public final void performConfigurationProvide(ConfigurationClient client, Filter filter) {
//	};
//
//	/*
//	 * @deprecated do not use!
//	 */
//	public final void performConfigurationAvailableChanged(ConfigurationSpecification[] specification, ConfigurationSpecification[] value) {
//	}
//
//	/*
//	 * @deprecated do not use!
//	 */
//	public final void performConfigurationAccepted(ConfigurationIdentifier id, Serializable value) {
//	}
//
//	/*
//	 * @deprecated do not use!
//	 */
//	public final void performConfigurationRejected(ConfigurationIdentifier id, Serializable value, Serializable reason) {
//	}

	private static final Logger logger = Logger.getAnonymousLogger();
}
