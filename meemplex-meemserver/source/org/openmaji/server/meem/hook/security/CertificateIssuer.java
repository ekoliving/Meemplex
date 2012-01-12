/*
 * @(#)CertificateIssuer.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 * 
 * Created on 13/08/2003
 */
package org.openmaji.server.meem.hook.security;

import java.security.cert.CertificateException;

import org.openmaji.meem.Facet;


/**
 * CertificateIssuer
 * 
 * @author stormboy
 */
public interface CertificateIssuer extends Facet {
	
	/**
	 * Request a certificate of this issuer.  The issuer will check that the caller matches the principal, and is
	 * the holder of the corresponding private key.
	 * 
	 * @param certRequest
	 */
	public void requestCertificate(CertificationRequest certRequest)
		throws CertificateException;
	
	

	/* ---------- Factory specification ---------------------------------------- */

	/**
	 * Unique Factory identifier for this Maji platform concept
	 */

	public final static String IDENTIFIER = "certificateIssuer";

	/**
	 * Default Factory implementation class name
	 */

	public final static String DEFAULT_IMPLEMENTATION =
	  "org.openmaji.implementation.server.meem.hook.security.CertificateIssuerImpl";

	/**
	 * Property that can override the default Factory implementation class name
	 */

	public final static String PROPERTY_IMPLEMENTATION_CLASSNAME =
	  "org.openmaji.meem.aspect.security.CertificateIssuerImplImplClassName";

	/**
	 * Factory type used to group similar Maji platform concepts
	 */

	public final static String FACTORY_TYPE = "meem";
}
