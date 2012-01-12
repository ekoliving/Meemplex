package org.openmaji.implementation.rpc.server;

import java.util.Random;

public class IdGenerator {
	
	static final Random random = new Random();
	
	static final char[] chars = new char[] 
	  { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 
		'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
		'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
	  };
	
	static final int idLength = 20;

	public static String newId() {
		return randomString(idLength);
	}
	
	/**
	 * 
	 * @param length
	 */
	private static String randomString(int length) {
		char[] buf = new char[length];
		for (int i=0; i<length; i++) {
			buf[i] = chars[random.nextInt(chars.length)];
		}
		return new String(buf);
	}
	
}
