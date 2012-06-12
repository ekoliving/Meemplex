package org.openmaji.implementation.diagnostic.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class TestInvocationHandler implements InvocationHandler {
	private static final Logger logger = Logger.getAnonymousLogger();
	
	private String name;
	//private Class facetClass;

	public TestInvocationHandler(String name, Class facetClass) {
		this.name = name;
		//this.facetClass = facetClass;
	}
	
	public Object invoke(Object object, Method method, Object[] arguments) throws Throwable {
		String methodName = method.getName();
		
		if ("toString".equals(methodName) && (arguments == null || arguments.length == 0) ) {
			return toString();
		}
		else if ("equals".equals(methodName) && arguments != null && arguments.length == 1){
			Object other = arguments[0];
			return new Boolean(equals(other));
		}
		else if ("hashCode".equals(methodName)  && (arguments == null || arguments.length == 0)) {
			return new Integer(hashCode());
		}
		else {
			StringBuffer sb = new StringBuffer();
			for (int i=0; arguments != null && i<arguments.length; i++) {
				if (i>0) {
					sb.append(", ");
				}
				sb.append(arguments[i]);
			}
			logger.log(Level.INFO, "Called " + object + "." + method.getName() + "(" + sb + ")");
		}
		
		return null;
	}

	/**
	 * 
	 */
	public String toString() {
		return "[proxy: " + name + "]"; // + " (" + facetClass + ")";
	}
}
