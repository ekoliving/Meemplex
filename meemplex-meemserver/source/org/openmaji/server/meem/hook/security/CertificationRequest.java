/*
 * @(#)CertificationRequest.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 * 
 * Created on 13/08/2003
 */
package org.openmaji.server.meem.hook.security;

import java.security.Principal;
import java.security.PublicKey;

/**
 * CertificationRequest
 * 
 * A request for a certificate.
 * 
 * @author stormboy
 */
public interface CertificationRequest {
	
	public Principal getPrincipal();
	
	public PublicKey getPublicKey();
	
	// TODO also provide proof of posession of priviate key
	// public XXXX getSignedThingy()

}
