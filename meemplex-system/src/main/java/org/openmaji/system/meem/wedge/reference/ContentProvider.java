/*
 * @(#)ContentProvider.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
package org.openmaji.system.meem.wedge.reference;

import org.openmaji.meem.Facet;
import org.openmaji.meem.filter.Filter;

/**
 * <p>
 * The interface that content providers should implement.
 * </p>
 * <p>
 * When a meem inbound facet is passed as a reference to another meem if the reference
 * specifies initial content the inbound facet is passed to the content provider for the outbound 
 * facet it is connecting to. The idea of the content provider is to pass back to the inbound facet 
 * any initial state and the like that the meem has available. Alternately, when used in conjunction
 * with automaticRemove in Reference.addOutboundReference() content providers can be
 * used to provide a simple form of request response.
 * </p>
 * <p>
 * Content providers are normally implemented as inner classes and have a similar name
 * to the facet they are for - the facet name plus "Provider". For example if we wanted to implement a 
 * content provider for a binary facet the code could look something like the following:
 * <pre>
 *     public boolean currentValue = false;
 * 
 *     public Binary binaryClient;
 *     public final ContentProvider binaryClientProvider = new ContentProvider()
 *     {
 *         public void sendContent(Object target, Filter filter) 
 *        	  throws ContentException
 *         {
 *             Binary binaryTarget = (Binary)target;
 * 
 *             binaryTarget.valueChanged(currentValue);
 *         }
 *     };
 * </pre>
 * <p>
 * @author  Andy Gelme
 * @version 1.0
 * @see org.openmaji.meem.filter.Filter
 * @see org.openmaji.system.meem.wedge.reference.ContentClient
 * @see org.openmaji.meem.wedge.reference.Reference
 */
public interface ContentProvider<T extends Facet>
{
	/**
	 * Send content to the passed in target facet.
	 * <p>
	 * Note: if target facet implements ContentClient it will also have its contentSent method
	 * invoked once the content has been sent.
	 * </p>
	 * @param target an inbound facet.
	 * @param filter an optional filter object used to check content for relevance before it is sent.
	 * @throws ContentException if a problem occurs sending the content.
	 */
	public void sendContent(T target, Filter filter) throws ContentException;
}
