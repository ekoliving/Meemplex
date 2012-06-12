/*
 * Created on 23/08/2004
 *
 */
package org.openmaji.rpc.binding;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.Iterator;

import org.openmaji.meem.Facet;

/**
 * A subclass of this may overide the "invoke" method to perform specific processing of arguments.
 * Invocation is then passed onto clients of this Binding by calling the appropriate methods on the "proxy"
 * or by calling "invokeOnListeners()".
 * 
 * Note:-
 * The subclass of this has to be added to the file META-INF/registryFile.mrpc 
 * to register the binding.
 * 
 * @author Warren Bloomer
 *
 */
public class InboundBinding extends FacetBinding implements FacetEventListener {

	/**
	 * Listeners for the facet events that occur for the facet.  
	 * Methods on the Facet interface of the listeners are invoked.
	 */
	private final HashSet<Facet> listeners = new HashSet<Facet>();	

	/**
	 * Proxy object for invoking methods on listeners.
	 */
	protected Object proxy;
	
	private FacetEventSender sender = null;
	
	/**
	 * Constructor
	 */
	public InboundBinding() {
	}

	protected final void setFacetClass(Class<? extends Facet> facetClass) {
		super.setFacetClass(facetClass);

		InvocationHandler handler = new MyInvocationHandler();

	     proxy = Proxy.newProxyInstance(
	     			facetClass.getClassLoader(),
					new Class[] { facetClass },
					handler
	     );
	}
	
	public final void setFacetId(String facetId) {
		super.setFacetId(facetId);

		if (sender != null) {
			sender.addFacetEventListener(this);
		}
	}
	
	public final void setMeemPath(String meemPath) {
		super.setMeemPath(meemPath);
		if (sender != null) {
			sender.addFacetEventListener(this);
		}		
	}

	/* --------------------- Methods that can be used by subclasses ---------------------- */

	/**
	 * Add a facet listener to the set of listeners.
	 */
	public final void addListener(Facet listener) {
		if (listener != null) {
			synchronized (listeners) {
				listeners.add(listener);
			}
		}
	}

	/**
	 * Remove a facet listener from the set of listeners.
	 */
	public final void removeListener(Facet listener) {
		if (listener != null) {
			synchronized (listeners) {
				listeners.remove(listener);
			}		
		}
	}

	/**
	 * This is called before a FacetEvent is translated to a method invocation 
	 * on the listeners.
	 * 
	 * This may be overridden if, for example, the parameters passed over RPC
	 * transport do not match particular params of the method on the Facet.
	 * 
	 * @param methodName the name of the method
	 * @param params     the original parameters from the FacetEvent
	 * @return           the normalize parameters
	 */
	protected Object[] normalize(String methodName, Object[] params) {
		return params;
	}

	/* ----------------------------- Bean methods ---------------------------- */

	/**
	 * 
	 */
	public final synchronized void setFacetEventSender(FacetEventSender sender) {

		// deregister interest in Health events from existing event sender
		if (this.sender != null && this.sender instanceof FacetHealthSender) {
			((FacetHealthSender)this.sender).removeFacetHealthListener(this);
		}

		this.sender = sender;
		sender.addFacetEventListener(this);

		// register in health events from sender
		if (sender instanceof FacetHealthSender) {
			((FacetHealthSender)sender).addFacetHealthListener(this);
		}
	}
	
	/* ------------------ FacetEventListener interface ------------------------ */
	
	/**
	 * FacetEvent received by the binding.
	 * 
	 * Usually called from the RPC client or server.
	 * 
	 */
	public final void facetEvent(FacetEvent event) {
		
		// ignore events that do not relate to this binding
		if (isForThis(event)) {
			
			String methodName = event.getMethod();
			Object[] params     = normalize(methodName, event.getParams());
			
			invoke(methodName, params);
		}
	}
	
	/* ------------------------- utility methods ---------------------------- */
	
	/**
	 * 
	 * @param event
	 */
	private boolean isForThis(FacetEvent event) {
		return  (
				meemPath != null  &&
				facetId  != null  &&
				meemPath.equalsIgnoreCase(event.getMeemPath()) && 
				facetId.equalsIgnoreCase(event.getFacetId())
			);
	}
	
	/**
	 * 
	 * @param methodName
	 * @param params
	 */
	protected void invoke(String methodName, Object[] params) {
		Class<?>[] paramTypes = new Class<?>[params.length];
		for(int i=0; i<params.length; i++) {
			Object param = params[i];
			Class<?> type = param.getClass();
			
			// handle primitive types
			if (Boolean.class.isAssignableFrom(type)) {
				type = Boolean.TYPE;
			}
			else if (Integer.class.isAssignableFrom(type)) {
				type = Integer.TYPE;
			}
			else if (Long.class.isAssignableFrom(type)) {
				type = Long.TYPE;
			}
			else if (Float.class.isAssignableFrom(type)) {
				type = Float.TYPE;
			}
			else if (Double.class.isAssignableFrom(type)) {
				type = Double.TYPE;
			}
			
			paramTypes[i] = type;
		}

		try {
			Method method = facetClass.getMethod(methodName, paramTypes);
			invokeOnListeners(method, params);
		}
		catch (NoSuchMethodException ex) {
//			logger.log(Level.INFO, "Problem finding method for invocation: " + methodName, ex);
		}
	}
	
	/**
	 * 
	 * @param method
	 * @param args
	 */
	private void invokeOnListeners(Method method, Object[] args) {
		synchronized (listeners) {
			Iterator<Facet> listenerIter = listeners.iterator();
			while (listenerIter.hasNext()) {
				//logger.log(Level.INFO, "invoking method + " + method + " on listener");
				Object target = listenerIter.next();
				try {
					method.invoke(target, args);
				}
				catch (IllegalAccessException ex) {
//					logger.log(Level.INFO, "No access to invoke method: " + method, ex);
				}
				catch (InvocationTargetException ex) {
//					logger.log(Level.INFO, "Problem invoking method: " + method, ex.getCause());
				}
			}
		}
	}

	/**
	 * 
	 */
	class MyInvocationHandler implements InvocationHandler {
		
		public Object invoke(Object obj, Method method, Object[] params) {
			invokeOnListeners(method, params);
			return null;
		}
	}
}
