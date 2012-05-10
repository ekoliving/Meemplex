/*
 * @(#)IDocumentationAuthor.java
 * Created on 2/03/2004
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.browser.patterns.common;

import org.eclipse.jface.text.Document;

/**
 * <code>IDocumentationAuthor</code>.
 * <p>
 * @author Kin Wong
 */
public interface IDocumentationAuthor {
	void write(Document document);
}
