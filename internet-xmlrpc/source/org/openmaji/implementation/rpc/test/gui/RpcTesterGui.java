/*
 * Copyright 2005 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.rpc.test.gui;

import java.awt.BorderLayout;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.openmaji.common.Binary;
import org.openmaji.implementation.rpc.binding.facet.InboundBinary;
import org.openmaji.implementation.rpc.binding.facet.OutboundBinary;
import org.openmaji.implementation.rpc.client.MajiRpcClient;
import org.openmaji.rpc.binding.FacetHealthEvent;
import org.openmaji.rpc.binding.FacetHealthListener;
import org.openmaji.rpc.binding.InboundBinding;
import org.openmaji.rpc.binding.OutboundBinding;


public class RpcTesterGui {
	private String basePath = "hyperspace:/work/test/rpc";

	private MajiRpcClient majiRPCClient = null;

	private int numBinary = 10;

	//private int numLinear = 10;
	
	private JPanel panel;
	
	private HashSet<InboundBinding> inboundBindings = new HashSet<InboundBinding>();
	private HashSet<OutboundBinding> outboundBindings = new HashSet<OutboundBinding>();
	

	public static void main(String[] args) throws InterruptedException {
		try {
			new RpcTesterGui().doit();
		}
		catch (Throwable t) {
			t.printStackTrace();
		}
	}

	private void doit() throws InterruptedException {
		JFrame frame = new JFrame("XML-RPC test");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(getPanel(), BorderLayout.CENTER);
		
		frame.setSize(640, 480);
		
		createMajiRPCClient();
		Thread.sleep(1000);
		createButtons();
		//createSliders();

		frame.setVisible(true);
		Thread.sleep(10000);		
	}
	
	private JPanel getPanel() {
		if (panel == null) {
			panel = new JPanel();
		}
		return panel;
	}
	
	private void createButtons() {
		for (int i = 0; i < numBinary; i++) {
			String name = "loopbackBinary" + i;
			BinaryButton button = createBinaryButton(name);

			getPanel().add(button);
		}
	}

	/*
	private void createSliders() {
		for (int i = 0; i < numLinear; i++) {
			String name = "loopbackLinear" + i;
			LinearSlider slider = createLinearSlider(name);

			getPanel().add(slider);
		}
	}
	*/

	private BinaryButton createBinaryButton(String name) {
		final BinaryButton button = new BinaryButton();
		button.setName(name);
		button.setText(name);
		button.setSize(50, 50);

		InboundBinary inboundBinary = new InboundBinary();
		inboundBinary.setMeemPath(basePath + "/" + name);
		inboundBinary.setFacetId("binaryOutput");
		inboundBinary.setFacetEventSender(majiRPCClient);
		inboundBindings.add(inboundBinary);

		inboundBinary.addBinaryFacet(new Binary() {
			public void valueChanged(boolean value) {
				button.valueChanged(value);
			}
		});
		
		inboundBinary.addBindingHealthListener(
				new FacetHealthListener() {
					public void facetHealthEvent(FacetHealthEvent event) {
						Logger.getAnonymousLogger().info("got facet health event from inbound: " + event);
//						button.setEnabled(
//								event.getBindingState() == FacetHealthEvent.FACET_RESOLVED
//							);
					}
				}
				);
		
		
		OutboundBinary outboundBinary = new OutboundBinary();
		outboundBinary.setMeemPath(basePath + "/" + name);
		outboundBinary.setFacetId("binaryInput");
		outboundBinary.addFacetEventListener(majiRPCClient);
		outboundBindings.add(outboundBinary);
		
		button.addListener(outboundBinary);
		
		outboundBinary.addBindingHealthListener(
				new FacetHealthListener() {
					public void facetHealthEvent(FacetHealthEvent event) {
						Logger.getAnonymousLogger().info("got facet health event from outbound: " + event);
//						button.setEnabled(
//								event.getBindingState() == FacetHealthEvent.FACET_RESOLVED
//							);
					}
				}
				);
		
		return button;
	}

	/*
	private LinearSlider createLinearSlider(String name) {
		final LinearSlider slider = new LinearSlider();
		slider.setSize(80, 50);
		slider.setName(name);
		slider.setToolTipText(name);

		InboundLinear inboundLinear = new InboundLinear();
		inboundLinear.setMeemPath(basePath + "/" + name);
		inboundLinear.setFacetId("linearOutput");
		inboundLinear.setFacetEventSender(majiRPCClient);

		inboundLinear.addLinearFacet(new Linear() {
			public void valueChanged(Position value) {
				slider.valueChanged(value);
			}
		});
		
		OutboundLinear outboundLinear = new OutboundLinear();
		outboundLinear.setMeemPath(basePath + "/" + name);
		outboundLinear.setFacetId("binaryInput");
		outboundLinear.setFacetEventListener(majiRPCClient);
		
		slider.addListener(outboundLinear);
		
		return slider;
	}
	*/
	
	private void createMajiRPCClient() {
		majiRPCClient = new MajiRpcClient();
		try {
			String address= "http://localhost:8000/maji/rpc";
			majiRPCClient.setAddress(new URL(address));
			// majiRPCClient.setUsername("guest");
			// majiRPCClient.setPassword("guest99");
		}
		catch (MalformedURLException ex) {
			ex.printStackTrace();
		}
		catch (RemoteException e) {
			e.printStackTrace();
		}
	}
}
