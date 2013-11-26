/*
 * @(#)AsyncContentProvider.java
 *
 * Copyright 2004 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
package org.openmaji.implementation.server.meem.wedge.reference;

import org.openmaji.meem.Facet;
import org.openmaji.meem.filter.Filter;
import org.openmaji.system.meem.wedge.reference.ContentClient;


/**
 * @author Peter
 */
public interface AsyncContentProvider<T extends Facet>
{
	void asyncSendContent(T target, Filter filter, ContentClient contentClient);
}
