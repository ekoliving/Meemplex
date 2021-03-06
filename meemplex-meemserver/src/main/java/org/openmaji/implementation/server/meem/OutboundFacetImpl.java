/*
 * Created on Mar 1, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.openmaji.implementation.server.meem;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openmaji.implementation.server.classloader.SystemExportList;
import org.openmaji.implementation.server.meem.invocation.MeemInvocationSource;
import org.openmaji.implementation.server.meem.wedge.reference.AsyncContentProvider;
import org.openmaji.implementation.server.utility.ObjectUtility;
import org.openmaji.meem.Facet;
import org.openmaji.meem.definition.Direction;
import org.openmaji.meem.definition.FacetAttribute;
import org.openmaji.meem.definition.FacetOutboundAttribute;
import org.openmaji.meem.filter.FilterChecker;
import org.openmaji.system.meem.wedge.reference.ContentProvider;

/**
 * @author Chris Kakris
 */
public class OutboundFacetImpl <T extends Facet> extends FacetImpl<T> {

	public OutboundFacetImpl(WedgeImpl wedgeImpl, FacetOutboundAttribute facetOutboundAttribute) throws ClassNotFoundException {
		super(wedgeImpl, ObjectUtility.getClass((Class<T>) Facet.class, facetOutboundAttribute.getInterfaceName()));

		this.facetOutboundAttribute = facetOutboundAttribute;
	}

	/**
	 * Provides the FacetAttribute.
	 * 
	 * @return Attribute for this Facet implementation
	 */
	public FacetAttribute getFacetAttribute() {
		return facetOutboundAttribute;
	}

	/**
	 * Provides the Facet Direction.
	 * 
	 * @return Direction for this Facet instance
	 */
	public Direction getDirection() {
		return Direction.OUTBOUND;
	}

	public MeemInvocationSource<T> getMeemInvocationSource() {
		if (meemInvocationSource == null) {
			Object target = getWedgeImpl().getImplementation();

			AsyncContentProvider<T> asyncContentProvider = null;
			ContentProvider<T> contentProvider = null;

			try {
				Class<?> targetClass = target.getClass();

				String fieldName = facetOutboundAttribute.getWedgePublicFieldName();
				Field field = targetClass.getField(fieldName);

				final String contentProviderFieldName = fieldName + "Provider";
				Field contentProviderField = field.getDeclaringClass().getDeclaredField(contentProviderFieldName);
				int modifiers = contentProviderField.getModifiers();

				if (Modifier.isPublic(modifiers) && Modifier.isFinal(modifiers)) {
					Class<?> type = contentProviderField.getType();

					if (AsyncContentProvider.class.isAssignableFrom(type)) {
						asyncContentProvider = (AsyncContentProvider<T>) contentProviderField.get(target);
					}
					else if (ContentProvider.class.isAssignableFrom(type)) {
						contentProvider = (ContentProvider<T>) contentProviderField.get(target);
					}
				}
			}
			catch(NoSuchFieldException e) {
				// ignore
			}
			catch (Exception e) {
				if (DEBUG) {
					logger.log(Level.INFO, "Problem creating content provider", e);
				}
			}

			FilterChecker filterChecker = target instanceof FilterChecker ? (FilterChecker) target : null;

			meemInvocationSource = new MeemInvocationSource<T>(this, asyncContentProvider, contentProvider, filterChecker);
		}

		return meemInvocationSource;
	}

	public T makeProxy() {
		ClassLoader classLoader = SystemExportList.getInstance().getClassLoaderFor(this.getSpecification().getName());

		Class<?>[] interfaces = new Class[] { this.getSpecification() };

		try {
			@SuppressWarnings("unchecked")
			T result = (T) Proxy.newProxyInstance(classLoader, interfaces, getMeemInvocationSource());
			return result;
		}
		catch (RuntimeException e) {
			// DEBUG
			System.err.println("Exception making proxy: " + e);
			System.err.println("Interfaces: ");
			for (int i = 0; i < interfaces.length; i++) {
				System.err.println("  " + interfaces[i]);
			}
			throw e;
		}
	}

	private final FacetOutboundAttribute facetOutboundAttribute;

	private MeemInvocationSource<T> meemInvocationSource = null;
	
	private static final Logger logger = Logger.getAnonymousLogger();
	
	private static final boolean DEBUG = true;
}
