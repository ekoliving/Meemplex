/*
 * @(#)MeemDocumentationAuthor.java
 * Created on 2/03/2004
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.browser.patterns.common;


import org.eclipse.jface.text.Document;
import org.openmaji.implementation.tool.eclipse.client.MeemClientProxy;
import org.openmaji.system.meempool.metadata.Abstract;
import org.openmaji.system.presentation.InterMajik;


/**
 * <code>MeemDocumentationAuthor</code>.
 * <p>
 * @author Kin Wong
 */
public class MeemDocumentationAuthor implements IDocumentationAuthor {
	public static final Abstract DEFAULT_ABSTRACT = new Abstract();
	static {
		DEFAULT_ABSTRACT.setVersion("0.00");
		DEFAULT_ABSTRACT.setDescription("No description available");
		DEFAULT_ABSTRACT.setCopyright("No Copyright");
	}

	private String name;
	private MeemClientProxy meem;
	private Document document;
	
	MeemDocumentationAuthor(String name, MeemClientProxy meem) {
		this.name = name;
		this.meem = meem;
	}
	
	protected MeemClientProxy getMeem() {
		return meem;
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.browser.patterns.common.IDocumentationAuthor#write(org.eclipse.jface.text.Document)
	 */
	public void write(Document document) {
		this.document = document;
		write(name + "\n");

		Abstract abztract = (Abstract)
			getMeem().getVariableMapProxy().get(InterMajik.ABSTRACT_KEY);
		if(abztract == null) abztract = DEFAULT_ABSTRACT;

		write("Description:\n");
		write(abztract.getDescription());
		write("\n");
		write("\n");
	}
		
	void write(String text) {
		try {
			document.replace(document.getLength(), 0, text);
		}
		catch(Exception e) {
		}
	}
}
