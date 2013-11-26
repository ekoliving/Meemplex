package org.openmaji.server.utility;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.logging.Logger;

import org.openmaji.meem.Facet;

public class LoggerProxyFactory {
	private static final Logger logger = Logger.getAnonymousLogger();
	
	@SuppressWarnings("unchecked")
	public static <T extends Facet> T createProxy(final String name, final Class<T> cls) {
		return (T) Proxy.newProxyInstance(cls.getClassLoader(), cls.getInterfaces(), new InvocationHandler() {
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				StringBuilder sb = new StringBuilder();
				if (args != null) {
					for (Object arg : args) {
						sb.append(arg);
						sb.append(", ");
					}
				}
				logger.info("+++++ handling: " + name + " . " + method + "(" + sb + ")");
				return null;
			}
		});
	}

}
