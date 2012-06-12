/*
 * Copyright 2005 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.rpc.test;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openmaji.common.Binary;
import org.openmaji.implementation.rpc.binding.facet.InboundBinary;
import org.openmaji.implementation.rpc.binding.facet.OutboundBinary;
import org.openmaji.implementation.rpc.client.MajiRpcClient;

public class BinaryTester {
	private static final Logger logger = Logger.getAnonymousLogger();
	
	//private String rpcUrl = "http://localhost:8000/maji/rpc";
	private String rpcUrl = "http://zoup:8000/maji/rpc";
	
	//private String meemPath = "hyperspace:/aa/LoopbackBinary";
	//private String meemPath = "hyperSpace:/site/stanebrae/location/kit1/kit1downlight0";
	private String meemPath = "hyperSpace:/site/stanebrae/location/kit1/kit1fan0";
	private String inFacet = "binaryInput";
	private String outFacet = "binaryOutput";

	private MajiRpcClient majiRPCClient = null;

	private InboundBinary inboundBinary;
	private OutboundBinary outboundBinary;

	public static void main(String[] args) throws InterruptedException {
		try {
			BinaryTester tester = new BinaryTester();
			
			logger.log(Level.INFO, "creating client...");
			tester.getMajiRPCClient();
			
			logger.log(Level.INFO, "create client");
			Thread.sleep(1);
			
			InboundBinary inboundBinary = tester.getInboundBinary();
			OutboundBinary binary = tester.getOutboundBinary();
			
			
			Thread.sleep(1000);
			
			Boolean value = false;
			logger.log(Level.INFO, "setting value to " + value);
			binary.valueChanged(value);
			Thread.sleep(1000);
			
			value = !value;
			logger.log(Level.INFO, "setting value to " + value);
			binary.valueChanged(value);
			Thread.sleep(1000);
			
			value = !value;
			logger.log(Level.INFO, "setting value to " + value);
			binary.valueChanged(value);
			Thread.sleep(1000);
			
			value = !value;
			logger.log(Level.INFO, "setting value to " + value);
			binary.valueChanged(value);
			Thread.sleep(1000);
			
			value = !value;
			logger.log(Level.INFO, "setting value to " + value);
			binary.valueChanged(value);
			
			Thread.sleep(10000);
			
			logger.log(Level.INFO, "complete.");
			
			tester.getMajiRPCClient().stop();
		}
		catch (Throwable t) {
			t.printStackTrace();
		}
	}

	private OutboundBinary getOutboundBinary() {
		if (outboundBinary == null) {
			outboundBinary = new OutboundBinary();
			outboundBinary.setMeemPath(meemPath);
			outboundBinary.setFacetId(inFacet);
			outboundBinary.addFacetEventListener(getMajiRPCClient());
		}
		return outboundBinary;
	}
	
	private InboundBinary getInboundBinary() {
		if (inboundBinary == null) {
			inboundBinary = new InboundBinary();
			inboundBinary.setMeemPath(meemPath);
			inboundBinary.setFacetId(outFacet);
			inboundBinary.setFacetEventSender(getMajiRPCClient());
	
			inboundBinary.addBinaryFacet(new Binary() {
				public void valueChanged(boolean value) {
					logger.log(Level.INFO, "got value: " + value);
				}
			});
		}
		return inboundBinary;
	}

	private MajiRpcClient getMajiRPCClient() {
		if (majiRPCClient == null) {
			majiRPCClient = new MajiRpcClient();
			try {
				majiRPCClient.setAddress(new URL(rpcUrl));
				// majiRPCClient.setUsername("guest");
				// majiRPCClient.setPassword("guest99");
			}
			catch (MalformedURLException e) {
				e.printStackTrace();
			}
			catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		return majiRPCClient;
	}
}
