/*
 * Created on 9/09/2004
 *
 */
package org.openmaji.implementation.rpc.binding;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.util.Hashtable;



import java.util.logging.Level;
import java.util.logging.Logger;


import org.openmaji.implementation.rpc.binding.facet.InboundBinary;
import org.openmaji.implementation.rpc.binding.facet.InboundCategoryClient;
import org.openmaji.implementation.rpc.binding.facet.InboundConfigurationHandler;
import org.openmaji.implementation.rpc.binding.facet.InboundFacetClient;
import org.openmaji.implementation.rpc.binding.facet.InboundLifeCycleClient;
import org.openmaji.implementation.rpc.binding.facet.InboundLinear;
import org.openmaji.implementation.rpc.binding.facet.InboundLinearList;
import org.openmaji.implementation.rpc.binding.facet.InboundVariable;
import org.openmaji.implementation.rpc.binding.facet.InboundVariableList;
import org.openmaji.implementation.rpc.binding.facet.OutboundBinary;
import org.openmaji.implementation.rpc.binding.facet.OutboundCategory;
import org.openmaji.implementation.rpc.binding.facet.OutboundCategoryClient;
import org.openmaji.implementation.rpc.binding.facet.OutboundConfigurationClient;
import org.openmaji.implementation.rpc.binding.facet.OutboundConfigurationHandler;
import org.openmaji.implementation.rpc.binding.facet.OutboundFacetClient;
import org.openmaji.implementation.rpc.binding.facet.OutboundLifeCycleClient;
import org.openmaji.implementation.rpc.binding.facet.OutboundLinear;
import org.openmaji.implementation.rpc.binding.facet.OutboundLinearList;
import org.openmaji.implementation.rpc.binding.facet.OutboundVariable;
import org.openmaji.implementation.rpc.binding.facet.OutboundVariableList;
import org.openmaji.implementation.server.utility.ObjectUtility;
import org.openmaji.meem.Facet;
import org.openmaji.meem.wedge.configuration.ConfigurationClient;
import org.openmaji.meem.wedge.configuration.ConfigurationHandler;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClient;
import org.openmaji.rpc.binding.BindingSPI;
import org.openmaji.rpc.binding.InboundBinding;
import org.openmaji.rpc.binding.OutboundBinding;
import org.openmaji.system.meem.FacetClient;
import org.openmaji.system.space.Category;
import org.openmaji.system.space.CategoryClient;
import org.openmaji.common.Binary;
import org.openmaji.common.Linear;
import org.openmaji.common.LinearList;
import org.openmaji.common.Variable;
import org.openmaji.common.VariableList;

/**
 * A service provider for aquiring inbound and outbound bindings for MajiRPC.
 * 
 * Bindings are aquired based on a class of a facet interface.
 * 
 * Classes that represent bindings to be instatiated can be registered with this provider.
 * These classes are used to instatiate new bindings when requested.
 * 
 * @author Warren Bloomer
 *
 */
public class BindingSPIImpl implements BindingSPI {

	private static final Logger logger = Logger.getAnonymousLogger();

	private static String PATH = "/META-INF/registryFile.mrpc";

	/** a map of facet classes to outbound binding classes */
	Hashtable<Class<? extends Facet>, Class<? extends OutboundBinding>> outboundBindings = 
		new Hashtable<Class<? extends Facet>, Class<? extends OutboundBinding>>();
	
	/** a map of facet classes to inbound binding classes */
	Hashtable<Class<? extends Facet>, Class<? extends InboundBinding>> inboundBindings  = 
		new Hashtable<Class<? extends Facet>, Class<? extends InboundBinding>>();

	/**
	 * Constructor.
	 * 
	 * Parses any existing mapping of facet classes to binding classes in a registery.
	 */
	public BindingSPIImpl() {
		parseRegistry();		
	}
	
	/**
	 * Register an outbound binding class for a given facet class.
	 * 
	 * @param facetClass The class of the facet interface.
	 * @param bindingClass The class of the binding to be instatiated for the facet interface class.
	 */
	public void registerOutbound(Class<? extends Facet> facetClass, Class<? extends OutboundBinding> bindingClass) {
		
		// check if bindingClass is an OutboundBinding
		if (OutboundBinding.class.isAssignableFrom(bindingClass)) {
			// check if bindingClass is of the Facet class
			if (facetClass.isAssignableFrom(bindingClass)) {
				outboundBindings.put(facetClass, bindingClass);
			}
			else {
				logger.log(Level.INFO, "Binding class, " + bindingClass + " is not of type, " + facetClass);
			}
		}
		else {
			logger.log(Level.INFO, "Binding class, " + bindingClass + " is not an outbound binding");
		}
	}

	/**
	 * Register an inbound binding class for a given facet interface class.
	 *  
	 * @param facetClass The class of the facet interface.
	 * @param bindingClass The class of the binding to be instantiated when a binding is requested.
	 */
	public void registerInbound(Class<? extends Facet> facetClass, Class<? extends InboundBinding> bindingClass) {

		// check if bindingClass is an InboundBinding
		if (InboundBinding.class.isAssignableFrom(bindingClass)) {
			inboundBindings.put(facetClass, bindingClass);
		}
		else {
			logger.log(Level.INFO, "Binding class, " + bindingClass + " is not an outbound binding");
		}

	}

	/**
	 * Returns an outbound binding for the given facet interface class, or null if no
	 * suitable binding is registered.
	 * 
	 * @param facetClass The class of the Facet.
	 * @return A new binding for the facet interface, or null if no suitable binding could be located.
	 */
	public OutboundBinding getOutboundBinding(Class<? extends Facet> facetClass) {
		
		OutboundBinding binding = null;
		
		Class<? extends OutboundBinding> bindingClass = outboundBindings.get(facetClass);

		if (bindingClass != null) {
			try {
				binding = (OutboundBinding) bindingClass.newInstance();
			}
			catch (InstantiationException ex) {
				logger.log(Level.INFO, "Could not instantiate binding class: " + bindingClass, ex);
			}
			catch (IllegalAccessException ex) {
				logger.log(Level.INFO, "No access to instantiate binding class: " + bindingClass, ex);
			}
		}		
		return binding;
	}

	/**
	 * Returns an inbound binding for a given facet class, or null if
	 * no suitable binding is registered.
	 * 
	 * @param facetClass the facet interface class.
	 * @return An inbound binding or null if non is registered for the facet interface.
	 */
	public InboundBinding getInboundBinding(Class<? extends Facet> facetClass) {

		InboundBinding binding = null;
		
		Class<? extends InboundBinding> bindingClass = inboundBindings.get(facetClass);

		if (bindingClass != null) {
			try {
				binding = (InboundBinding) bindingClass.newInstance();
			}
			catch (InstantiationException ex) {
				logger.log(Level.INFO, "Could not instantiate binding class: " + bindingClass, ex);
			}
			catch (IllegalAccessException ex) {
				logger.log(Level.INFO, "No access to instantiate binding class: " + bindingClass, ex);
			}
		}
		
		return binding;

	}
	
	/**
	 * Parse the registry file
	 */
	private void parseRegistry() {
		InputStream     is  = getClass().getResourceAsStream(PATH);
		Reader          r   = new BufferedReader(new InputStreamReader(is));
		StreamTokenizer st  = new StreamTokenizer(r);

		try {
			int i = st.nextToken();

			while (i != StreamTokenizer.TT_EOF) {
				while (i == StreamTokenizer.TT_EOL) {
					i = st.nextToken();
				}
				
				if ("Outbound".equalsIgnoreCase(st.sval)) {				
					try {
						i = st.nextToken();
						Class<?> facetClass = ObjectUtility.getClass(st.sval);
						i = st.nextToken();
						Class<?> bindingClass = ObjectUtility.getClass(st.sval);
						
						registerOutbound(facetClass.asSubclass(Facet.class), bindingClass.asSubclass(OutboundBinding.class));
					}
					catch (ClassNotFoundException ex) {
						logger.log(Level.INFO, "Could not find class", ex);						
					}
				}
				if ("Inbound".equalsIgnoreCase(st.sval)) {
					try {
						i = st.nextToken();
						Class<?> facetClass = ObjectUtility.getClass(st.sval);
						i = st.nextToken();
						Class<?> bindingClass = ObjectUtility.getClass(st.sval);
						
						registerInbound(facetClass.asSubclass(Facet.class), bindingClass.asSubclass(InboundBinding.class));
					}
					catch (ClassNotFoundException ex) {
						logger.log(Level.INFO, "Could not find class", ex);						
					}
				}
				i = st.nextToken();
			}
			
			r.close();
			is.close();
		}
		catch (IOException ex) {
			logger.log(Level.INFO, "Could not parse binding file", ex);	

			// register default outbound bindings
			registerOutbound(Binary.class,               OutboundBinary.class);
			registerOutbound(Linear.class,               OutboundLinear.class);
			registerOutbound(LinearList.class,           OutboundLinearList.class);
			registerOutbound(Variable.class,             OutboundVariable.class);
			registerOutbound(VariableList.class,         OutboundVariableList.class);
			registerOutbound(Category.class,             OutboundCategory.class);
			registerOutbound(CategoryClient.class,       OutboundCategoryClient.class);
			registerOutbound(FacetClient.class,          OutboundFacetClient.class);
			registerOutbound(ConfigurationHandler.class, OutboundConfigurationHandler.class);
			registerOutbound(ConfigurationClient.class,  OutboundConfigurationClient.class);
			registerOutbound(LifeCycleClient.class,      OutboundLifeCycleClient.class);

			// register default inbound bindings
			registerInbound(Binary.class,               InboundBinary.class);
			registerInbound(Linear.class, 	            InboundLinear.class);
			registerInbound(LinearList.class,           InboundLinearList.class);
			registerInbound(Variable.class,             InboundVariable.class);
			registerInbound(VariableList.class,         InboundVariableList.class);
			registerInbound(CategoryClient.class,       InboundCategoryClient.class);
			registerInbound(FacetClient.class,          InboundFacetClient.class);
			registerInbound(ConfigurationHandler.class, InboundConfigurationHandler.class);
			registerInbound(LifeCycleClient.class,      InboundLifeCycleClient.class);
		}

	}
}
