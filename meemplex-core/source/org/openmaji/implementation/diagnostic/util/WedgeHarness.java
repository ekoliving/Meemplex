package org.openmaji.implementation.diagnostic.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.meemplex.meem.Conduit;
import org.openmaji.meem.Facet;
import org.openmaji.meem.Wedge;

/**
 * This class populates outbound meemContext, meemCore, outbound Facet and outbound conduit fields of a Wedge with proxies.
 * This enables Wedge code to be run outside of Maji.
 *  
 * TODO provide a client callback to deliver information on this wedge such as Facets and Conduits and inbound Facets and Conduits
 * 
 * @author stormboy
 *
 */
public class WedgeHarness {

	private static final Logger logger = Logger.getAnonymousLogger();
	
	private Wedge wedge;
		
	/**
	 * 
	 * @param wedge
	 */
	public WedgeHarness(Wedge wedge) {
		this.wedge = wedge;
		
		createMeemProxies();
		createOutboundConduits();
		createOutboundFacets();
	}
	
	/**
	 * 
	 * @return the wedge that this harness is harnessing.
	 */
	public Wedge getWedge() {
		return wedge;
	}
	

	/* ----------------- private methods ------------------- */
	
	private void createMeemProxies() {
		try {
			Field field = wedge.getClass().getField("meemContext");
			// TODO create a special MeemContext InvocationHandler
			InvocationHandler handler = new TestInvocationHandler(field.getName(), field.getType());
			Object value = Proxy.newProxyInstance(
					field.getType().getClassLoader(),
                    new Class[] { field.getType() },
                    handler
                   );
			field.set(wedge, value);
		}
		catch (NoSuchFieldException e) {
		}
		catch (IllegalAccessException e) {
		}

		try {
			Field field = wedge.getClass().getField("meemCore");
			// TODO create a special MeemCore InvocationHandler
			InvocationHandler handler = new TestInvocationHandler(field.getName(), field.getType());
			Object value = Proxy.newProxyInstance(
					field.getType().getClassLoader(),
                    new Class[] { field.getType() },
                    handler
                   );
			field.set(wedge, value);
		}
		catch (NoSuchFieldException e) {
		}
		catch (IllegalAccessException e) {
		}
	}
	
	private void createOutboundConduits() {
		Field[] fields = wedge.getClass().getFields();
		for (int i=0; i<fields.length; i++) {
			Field field = fields[i];
			
			String conduitName = null;
			if (field.getAnnotation(Conduit.class) != null) {
				conduitName = field.getName();
			}
			else if (field.getName().endsWith("Conduit")) {
				String fieldName = field.getName();
				conduitName = fieldName.substring(0, fieldName.length() - "Conduit".length());
			}

			if (conduitName != null) {
				try {
					Object value = field.get(wedge);
					if (value == null) {
						logger.info("Got outbound Conduit: " + field);
						
						// create a proxy
						InvocationHandler handler = new TestInvocationHandler(conduitName, field.getType());
						value = Proxy.newProxyInstance(
								field.getType().getClassLoader(),
                                new Class[] { field.getType() },
                                handler
                               );
						field.set(wedge, value);
					}
					else {
						logger.info("Got inbound Conduit: " + field);
					}
				}
				catch (IllegalAccessException e) {
					logger.log(Level.INFO, "Could not get field value", e);
				}
			}
			
		}		
	}
	
	private void createOutboundFacets() {
		Field[] fields = wedge.getClass().getFields();
		for (int i=0; i<fields.length; i++) {
			Field field = fields[i];
			try {
				Object value = field.get(wedge);

				if (value == null &&  Facet.class.isAssignableFrom(field.getType())) {
					logger.info("Got outbound Facet: " + field);
					
					// create a proxy
					InvocationHandler handler = new TestInvocationHandler(field.getName(), field.getType());
					value = Proxy.newProxyInstance(
							field.getType().getClassLoader(),
                            new Class[] { field.getType() },
                            handler
                           );
					field.set(wedge, value);					
				}
			}
			catch (IllegalAccessException e) {
				logger.log(Level.INFO, "Could not get field value", e);
			}
		}
	}

}
