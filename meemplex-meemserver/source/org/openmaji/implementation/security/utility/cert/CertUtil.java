/*
 * @(#)CertUtil.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
 package org.openmaji.implementation.security.utility.cert;

import java.io.ByteArrayInputStream;
import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.Date;

import javax.security.auth.x500.X500Principal;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERBMPString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.misc.MiscObjectIdentifiers;
import org.bouncycastle.asn1.misc.NetscapeCertType;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AuthorityKeyIdentifier;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.SubjectKeyIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x509.X509Extensions;
import org.bouncycastle.asn1.x509.X509Name;
import org.bouncycastle.jce.X509Principal;
import org.bouncycastle.jce.interfaces.PKCS12BagAttributeCarrier;
import org.bouncycastle.x509.X509V1CertificateGenerator;
import org.bouncycastle.x509.X509V3CertificateGenerator;

/**
 * utilities to create root and other level certificates.
 */
public class CertUtil
{
    static X509V1CertificateGenerator  v1CertGen = new X509V1CertificateGenerator();
    static X509V3CertificateGenerator  v3CertGen = new X509V3CertificateGenerator();

    //
    // create the subject key identifier.
    //
    private static SubjectKeyIdentifier createSubjectKeyId(
        PublicKey   pubKey)
    {
        try
        {
            ByteArrayInputStream    bIn = new ByteArrayInputStream(
                                                    pubKey.getEncoded());
            SubjectPublicKeyInfo info = new SubjectPublicKeyInfo(
                    (ASN1Sequence) new ASN1InputStream(bIn).readObject()
				);
//                (ASN1Sequence)new DERInputStream(bIn).readObject());

            return new SubjectKeyIdentifier(info);
        }
        catch (Exception e)
        {
            throw new RuntimeException("error creating key");
        }
    }

    //
    // create the authority key identifier.
    //
    private static AuthorityKeyIdentifier createAuthorityKeyId(
        PublicKey       pubKey,
        X509Name     name,
        BigInteger       sNumber)
    {
        try
        {
            ByteArrayInputStream    bIn = new ByteArrayInputStream(
                                                    pubKey.getEncoded());
            SubjectPublicKeyInfo info = new SubjectPublicKeyInfo(
                    (ASN1Sequence) new ASN1InputStream(bIn).readObject()
            	);
//            (ASN1Sequence)new DERInputStream(bIn).readObject()

            GeneralName             genName = new GeneralName(name);
            ASN1EncodableVector     v = new ASN1EncodableVector();

            v.add(genName);

            return new AuthorityKeyIdentifier(
                info, new GeneralNames(new DERSequence(v)), sNumber);
        }
        catch (Exception e)
        {
            throw new RuntimeException("error creating AuthorityKeyId");
        }
    }

	/**
	 * Create a root certificate (version 1).
	 * 
	 * @param pubKey
	 * @param serialNumber
	 * @param name
	 * @param notBefore
	 * @param notAfter
	 * @param signatureAlgorithm
	 * @param privKey
	 * @param friendlyName
	 * @return X509Certificate
	 * @throws Exception
	 */
    public static X509Certificate createRootCert(
    	PublicKey		pubKey,
    	BigInteger		serialNumber,
    	String				name,
		Date				notBefore,
		Date				notAfter,
		String				signatureAlgorithm,
		PrivateKey		privKey,
		String				friendlyName)
        throws Exception
    {
        //
        // signers name 
        //
        byte[]	nameBytes = new X500Principal(name).getEncoded();

        //
        // create the certificate - version 1
        //

        v1CertGen.setSerialNumber(serialNumber);
        v1CertGen.setIssuerDN(new X509Principal(nameBytes));
        v1CertGen.setNotBefore(notBefore);
        v1CertGen.setNotAfter(notAfter);
        v1CertGen.setSubjectDN(new X509Principal(nameBytes));
        v1CertGen.setPublicKey(pubKey);
        v1CertGen.setSignatureAlgorithm(signatureAlgorithm);

        X509Certificate cert = v1CertGen.generateX509Certificate(privKey);

        cert.checkValidity(new Date());

        cert.verify(pubKey);

		if (friendlyName != null)
		{
        	PKCS12BagAttributeCarrier   bagAttr = (PKCS12BagAttributeCarrier)cert;

        	//
        	// this is actually optional - but if you want to have control
        	// over setting the friendly name this is the way to do it...
        	//
        	bagAttr.setBagAttribute(
            	PKCSObjectIdentifiers.pkcs_9_at_friendlyName,
            	new DERBMPString(friendlyName));
		}

        return cert;
    }

	/**
	 * Creates a lower level certificate, adding authority key-id and subject
     * key-id extensions to the resulting certificate (version 3).
	 * 
	 * @param pubKey
	 * @param serialNumber
	 * @param name
	 * @param notBefore
	 * @param notAfter
	 * @param signatureAlgorithm
	 * @param issuerPrivKey
	 * @param issuerCert
	 * @param friendlyName
	 * @return X509Certificate
	 * @throws Exception
	 */
    public static X509Certificate createCert(
        PublicKey       	pubKey,
        BigInteger			serialNumber,
        String					name,
		Date					notBefore,
		Date					notAfter,
		String					signatureAlgorithm,
		PrivateKey      	issuerPrivKey,
        X509Certificate   issuerCert,
        String					friendlyName)
        throws Exception
    {
    	byte[]					nameBytes = new X500Principal(name).getEncoded();
    	
        //
        // create the certificate - version 3
        //
        v3CertGen.reset();

        v3CertGen.setSerialNumber(serialNumber);
        v3CertGen.setIssuerDN(new X509Principal(issuerCert.getSubjectX500Principal().getEncoded()));
        v3CertGen.setNotBefore(notBefore);
        v3CertGen.setNotAfter(notAfter);
        v3CertGen.setSubjectDN(new X509Principal(nameBytes));
        v3CertGen.setPublicKey(pubKey);
        v3CertGen.setSignatureAlgorithm(signatureAlgorithm);

        //
        // add the extensions
        //
        v3CertGen.addExtension(
            X509Extensions.SubjectKeyIdentifier,
            false,
            createSubjectKeyId(pubKey));

        v3CertGen.addExtension(
            X509Extensions.AuthorityKeyIdentifier,
            false,
            createAuthorityKeyId(issuerCert.getPublicKey(), 
            					new X509Principal(issuerCert.getSubjectX500Principal().getEncoded()),
            					serialNumber));

        v3CertGen.addExtension(
            X509Extensions.BasicConstraints,
            false,
            new BasicConstraints(false));

        v3CertGen.addExtension(
            MiscObjectIdentifiers.netscapeCertType,
            false,
            new NetscapeCertType(NetscapeCertType.sslServer | NetscapeCertType.sslClient | NetscapeCertType.objectSigning | NetscapeCertType.smime));

        X509Certificate cert = v3CertGen.generateX509Certificate(issuerPrivKey);

		if (friendlyName != null)
		{
        	PKCS12BagAttributeCarrier   bagAttr = (PKCS12BagAttributeCarrier)cert;

        	bagAttr.setBagAttribute(
            	PKCSObjectIdentifiers.pkcs_9_at_friendlyName,
            	new DERBMPString(friendlyName));
        	bagAttr.setBagAttribute(
            	PKCSObjectIdentifiers.pkcs_9_at_localKeyId,
                	createSubjectKeyId(pubKey));
		}

        return cert;
    }
    
    /**
     * Generate a certificate which is a "copy" of another certificate, but 
	 * resigned by a different issuer.
     *
     * @param initialCert
     * @param serialNumber
     * @param signatureAlgorithm
     * @param issuerPrivKey
     * @param issuerCert
     * @return X509Certificate
     */
    public static X509Certificate resignCert(
		X509Certificate   	initialCert,
		BigInteger			serialNumber,
		String				signatureAlgorithm,
		PrivateKey      	issuerPrivKey,
		X509Certificate   	issuerCert)
		throws Exception
	{
		//
		// create the certificate - version 3
		//
		v3CertGen.reset();

		v3CertGen.setSerialNumber(serialNumber);
		v3CertGen.setIssuerDN(new X509Principal(issuerCert.getSubjectX500Principal().getEncoded()));
		v3CertGen.setNotBefore(initialCert.getNotBefore());
		v3CertGen.setNotAfter(initialCert.getNotAfter());
		v3CertGen.setSubjectDN(new X509Principal(initialCert.getSubjectX500Principal().getEncoded()));
		v3CertGen.setPublicKey(initialCert.getPublicKey());
		v3CertGen.setSignatureAlgorithm(signatureAlgorithm);

		//
		// add the extensions
		//
		v3CertGen.addExtension(
			X509Extensions.SubjectKeyIdentifier,
			false,
			createSubjectKeyId(initialCert.getPublicKey()));

		v3CertGen.addExtension(
			X509Extensions.AuthorityKeyIdentifier,
			false,
			createAuthorityKeyId(issuerCert.getPublicKey(), 
								new X509Principal(issuerCert.getSubjectX500Principal().getEncoded()),
								serialNumber));

		v3CertGen.addExtension(
			X509Extensions.BasicConstraints,
			false,
			new BasicConstraints(false));

		v3CertGen.addExtension(
			MiscObjectIdentifiers.netscapeCertType,
			false,
			new NetscapeCertType(NetscapeCertType.sslClient | NetscapeCertType.objectSigning | NetscapeCertType.smime));

		X509Certificate cert = v3CertGen.generateX509Certificate(issuerPrivKey);

		return cert;
	}
}
