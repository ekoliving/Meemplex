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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.openmaji.common.Binary;
import org.openmaji.implementation.rpc.binding.facet.InboundBinary;
import org.openmaji.implementation.rpc.binding.facet.OutboundBinary;
import org.openmaji.implementation.rpc.client.MajiRpcClient;

public class MessageTester {
	private static final Logger logger = Logger.getAnonymousLogger();
	
	private String rpcUrl = "http://localhost:8000/rpc";
	
	private XmlRpcClient client;
	

	public static void main(String[] args) throws InterruptedException {
		try {
			MessageTester tester = new MessageTester();
			
			logger.log(Level.INFO, "creating client...");
			XmlRpcClient client = tester.getClient();
			
			logger.log(Level.INFO, "create client");
			Thread.sleep(1000);
			
			List<Object> setParams = new ArrayList<Object>();
			List<Object> getParams = new ArrayList<Object>();
			Object result;
			
			result = client.execute("test.getMap", getParams);
			logger.log(Level.INFO, "got map: " + result);
			Thread.sleep(1000);
			
			result = client.execute("test.getList", getParams);
			logger.log(Level.INFO, "got list: " + result);
			logger.log(Level.INFO, "got list: " + (Object[])result);
			logger.log(Level.INFO, "got item: " + ((Object[])result)[0]);
			Thread.sleep(1000);
			
			result = client.execute("test.getMessage", getParams);
			logger.log(Level.INFO, "got message: " + result);
			Thread.sleep(1000);
			
			setParams.add("Cheese");
			client.execute("test.putMessage", setParams);
			
			result = client.execute("test.getMessage", getParams);
			logger.log(Level.INFO, "got message: " + result);
			Thread.sleep(1000);
			
			setParams.set(0, "Meister");
			client.execute("test.putMessage", setParams);
			
			result = client.execute("test.getMessage", getParams);
			logger.log(Level.INFO, "got message: " + result);
			Thread.sleep(1000);
			
			setParams.set(0, new String[] {"Give", " Me", " a", " Break"});
			client.execute("test.putMessages", setParams);
			
			result = client.execute("test.getMessage", getParams);
			logger.log(Level.INFO, "got message: " + result);
		}
		catch (Throwable t) {
			t.printStackTrace();
		}
	}
	
	private XmlRpcClient getClient() throws Exception {
		if (client == null) {
			XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
			config.setServerURL(new URL(rpcUrl));
			config.setBasicUserName("testuser");
			config.setBasicPassword("testpassword");
			
			client = new XmlRpcClient();
			client.setConfig(config);
		}
		return client;
		
	}

}
