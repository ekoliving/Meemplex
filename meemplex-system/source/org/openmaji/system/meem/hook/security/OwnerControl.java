/*
 * Created on 16/08/2005
 */
package org.openmaji.system.meem.hook.security;

import javax.security.auth.Subject;

import org.openmaji.meem.Facet;


/**
 * @author Warren Bloomer
 *
 */
public interface OwnerControl extends Facet {

	/**
	 * Sets the owning Subject of a Meem.
	 * 
	 * @param owningSubject
	 */
	void setOwner(Subject owningSubject);
}
