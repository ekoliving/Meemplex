/*
 * Created on 6/09/2005
 */
package org.openmaji.implementation.server.meem;

import java.io.Serializable;

import org.openmaji.meem.filter.Filter;
import org.openmaji.meem.wedge.reference.Reference;


/**
 * @author Warren Bloomer
 *
 */
public class ReferenceFilter implements Filter, Serializable {
	private static final long serialVersionUID = -52939238923987L;

	private Reference reference;
	
	public ReferenceFilter(Reference reference) {
		this.reference = reference;
	}
	
	public boolean matches(Reference reference) {
		if (this.reference == null) {
			return null == reference;
		}
		return this.reference.equals(reference);
	}
}
