/*
 * @(#)SimpleCertGenTool.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
 package org.openmaji.implementation.security.utility.cert;

import java.io.FileOutputStream;
import java.math.BigInteger;
import java.security.KeyPairGenerator;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

import java.util.Date;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * set up a server key store, truststore, and a client PKCS12 file.
 */
public class SimpleCertGenTool
{	
	private static String getFriendlyName(
		String	x500Name,
		String	defName)
	{
		String 	friendlyName = defName;
		int		ind = x500Name.indexOf("CN=");
		
		if (ind >= 0)
		{
			friendlyName = x500Name.substring(ind + 3);
			
			ind = friendlyName.indexOf(',');
			if (ind > 0)
			{
				friendlyName  = friendlyName.substring(0, ind);
			}
		}
		
		return friendlyName;
	}
	
    public static void main(
        String[]    args)
        throws Exception
    {
    	if (args.length != 4)
    	{
 			System.err.println("usage: SimpleCertGenTool serverName serverPasswd clientName clientPasswd");
 			System.err.println("             eg. SimpleCertGenTool \"CN=Server, C=AU\" spasswd \"CN=Client, C=AU\" cpasswd" );
 			System.exit(1);
    	}
    	
    	String	serverName = args[0];
    	char[]	serverPasswd = args[1].toCharArray();
    	String	clientName = args[2];
    	char[]	clientPasswd = args[3].toCharArray();
    	
		Security.addProvider(new BouncyCastleProvider());
		KeyPairGenerator	kpGen = KeyPairGenerator.getInstance("RSA", "BC");

		kpGen.initialize(1024, new SecureRandom());

        //
        // personal keys
        //
		KeyPair		    kp = kpGen.generateKeyPair();
        PrivateKey     	clPrivKey = kp.getPrivate();
        PublicKey   	clPubKey = kp.getPublic();

        //
        // ca keys
        //
		kp = kpGen.generateKeyPair();

        PrivateKey          rtPrivKey = kp.getPrivate();
        PublicKey 			rtPubKey = kp.getPublic();

		//
		//	valid dates
		//
		Date					notBefore = new Date(System.currentTimeMillis() - (1000L * 60 * 60 * 24 * 30));
		Date					notAfter = new Date(System.currentTimeMillis() + (1000L * 60 * 60 * 24 * 365));
		
		long	serial = 1;
		
		//
		// create the certficates
		//        
		X509Certificate   rtCert = CertUtil.createRootCert(
								rtPubKey, 
								BigInteger.valueOf(serial++), 
								serverName,
								notBefore, notAfter, 
								"SHA1WithRSAEncryption", rtPrivKey, getFriendlyName(serverName, "Server"));
								
		X509Certificate   clCert = CertUtil.createCert(
								clPubKey, 
								BigInteger.valueOf(serial++), 
								clientName,
								notBefore, 
								notAfter, 
								"SHA1WithRSAEncryption",
								clPrivKey, rtCert, getFriendlyName(clientName, "Client"));
						
		//
		// create the client store
		//		
		KeyStore store = KeyStore.getInstance("PKCS12", "BC");

		store.load(null, null);

		Certificate[] chain = new Certificate[2];

		chain[1] = rtCert;
		chain[0] = clCert;

		store.setKeyEntry("Client", clPrivKey, null, chain);

		FileOutputStream fOut = new FileOutputStream("client.p12");

		store.store(fOut, clientPasswd);

		//
		// create the server file.
		//
		chain = new Certificate[1];

		chain[0] = rtCert;

		store = KeyStore.getInstance("JKS");

		store.load(null, null);

		store.setCertificateEntry("Server", rtCert);

		fOut = new FileOutputStream("trusted.jks");

		store.store(fOut, serverPasswd);

		//
		// create the server pkcs12.
		//
		chain = new Certificate[1];

		chain[0] = rtCert;

		store = KeyStore.getInstance("JKS");

		store.load(null, null);

		store.setKeyEntry("Server", rtPrivKey, serverPasswd, chain);

		fOut = new FileOutputStream("server.jks");

		store.store(fOut, serverPasswd);
	}
}
