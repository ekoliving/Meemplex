package org.openmaji.implementation.network;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A Wake-up frame is a special data packet containing the Ethernet address of the 
 * remote network card. Somewhere in this frame should exist a byte stream (magic sequence) 
 * composed by, at the least, 16 times the repetition of the Ethernet address and preceded 
 * by a synchronization stream of 6 bytes of FFh.

 * TODO make a wedge that will keep sending wake-on-lan packets every so often
 * @author stormboy
 *
 */
public class WakeOnLan implements Runnable {

	private static final Logger logger = Logger.getAnonymousLogger();
	
	private static final int PORT = 9;

	private String broadcastAddress = "192.168.1.255";

	private String[] macAddresses = new String[] {
		// host touch1
		"00:16:e6:85:45:96",
		
		// host touch2
		"00:20:ED:01:35:DD",

		// host touch3
		"00:16:e6:85:08:60",

		// host touch4
		"00:1A:4D:6D:20:DA",

		// host touch5
		"00:16:e6:85:0b:99",

		// host touch6
		"00:20:ED:01:ff:ff",

		// host touch7
		"00:07:E9:DC:9B:2A",

		// host touch8
		"00:20:ED:01:37:A9",

		// host touch9
		"00:16:e6:85:0a:cc",

		// host touch10
		"00:16:e6:85:0a:d4",

		// host touch11
		"00:16:e6:85:0b:a5",

		// host touch12
		"00:1A:4D:6D:20:8A",

		// host touch13
		"00:20:ED:01:6D:32",

		// host touch14
		"00:16:e6:85:0b:89",

		// host touch15
		"00:1A:4D:6D:1E:75",

		// host touch16
		"00:0f:ea:59:9e:56",

		// host touch17
		"00:20:ED:01:35:11",

		// host touch18
		"00:20:ED:01:36:EF",

		// host touch19
		"00:20:ED:01:ff:ff",

		// host touch20
		"00:20:ED:00:91:89",

		// host touch21
		"00:00:00:00:00:00",

		// host touch22
		"00:16:E6:85:0A:63", 
	};
	
	private Thread thread = null;

	private long waitTime = 10000;

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		try {
			new WakeOnLan().start();
		}
		catch (Throwable t) {
			t.printStackTrace();
			logger.log(Level.WARNING, t.getMessage());
		}
	}

	/**
	 * 
	 * @param address
	 */
	public void setBroadcastAddress(String address) {
		this.broadcastAddress = address;
	}
	
	/**
	 * 
	 * @param addresses
	 */
	public void setMacAddresses(String[] addresses) {
		this.macAddresses = addresses;
	}
	
	/**
	 * 
	 * @param waitTime
	 */
	public void setWaitTime(long waitTime) {
		this.waitTime = waitTime;
	}
	
	public void start() {
		if (thread == null) {
			thread = new Thread(this, "Wake On LAN Thread");
			thread.start();
		}
	}
	
	public void stop() {
		thread = null;
	}

	/**
	 * This method is run in the runner Thread
	 */
	public void run() {
    	while (Thread.currentThread() == thread) {
    		wake(broadcastAddress, macAddresses);
    		synchronized (this) {
    			try {
    				this.wait(waitTime);
    			}
    			catch (InterruptedException e) {
    				stop();
    			}
   			}
    	}
    }

	/**
	 * Send wake messags to the mac addresses on the network specified by the broadcast
	 * address.
	 * 
	 * @param broadcastAddress
	 * @param macAddresses
	 */
	private void wake(String broadcastAddress, String[] macAddresses) {
		for (int i = 0; macAddresses != null && i < macAddresses.length; i++) {
			wake(broadcastAddress, macAddresses[i]);
		}
	}

	/**
	 * Send a wake-on-lan packet on the given broadcast address to wake the machine
	 * with a given network MAC address.
	 * 
	 * @param broadcastAddress the address to send the packet on, e.g. 192.168.0.255
	 * @param macAddress the MAC address of the machine to wake, e.g. 00:0D:61:08:22:4A
	 */
	private void wake(String broadcastAddress, String macAddress) {
		int startSize = 6;
		int repeats = 16;
		try {
			byte[] macBytes = getMacBytes(macAddress);
			byte[] bytes = new byte[startSize + repeats * macBytes.length];
			for (int i = 0; i < startSize; i++) {
				bytes[i] = (byte) 0xff;
			}
			for (int i = startSize; i < bytes.length; i += macBytes.length) {
				System.arraycopy(macBytes, 0, bytes, i, macBytes.length);
			}

			InetAddress address = InetAddress.getByName(broadcastAddress);
			DatagramPacket packet = new DatagramPacket(bytes, bytes.length, address, PORT);
			DatagramSocket socket = new DatagramSocket();
			socket.send(packet);
			socket.close();

			logger.log(Level.INFO, "Wake " + macAddress + " packet broadcast on " + broadcastAddress);
		}
		catch (Exception e) {
			logger.log(Level.INFO, "Failed to send Wake-on-LAN packet: " + e);
		}
	}

	/**
	 * Get MAC address in bytes from a string representation of the MAC address.
	 * 
	 * @param macStr
	 * @return bytes version of MAC address
	 * @throws IllegalArgumentException
	 */
	private static byte[] getMacBytes(String macStr) throws IllegalArgumentException {
		byte[] bytes = new byte[6];
		String[] hex = macStr.split("(\\:|\\-)");
		if (hex.length != 6) {
			throw new IllegalArgumentException("Invalid MAC address.");
		}
		try {
			for (int i = 0; i < 6; i++) {
				bytes[i] = (byte) Integer.parseInt(hex[i], 16);
			}
		}
		catch (NumberFormatException e) {
			throw new IllegalArgumentException("Invalid hex digit in MAC address.");
		}
		return bytes;
	}
}
