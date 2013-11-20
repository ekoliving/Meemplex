/*
 * @(#)CertificateIssuerClient.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 * 
 * Created on 13/08/2003
 */
package org.openmaji.server.meem.hook.security;

import java.security.cert.Certificate;


/**
 * CertificateIssuerClient
 * 
 * @author stormboy
 */
public interface CertificateIssuerClient {
	
	/**
	 * Issue a certificate to the client.
	 * 
	 * @param certificateChain
	 */
	public void issueCertificate(Certificate[] certificateChain);
}
