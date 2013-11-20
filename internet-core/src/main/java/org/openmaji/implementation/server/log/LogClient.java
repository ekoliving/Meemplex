package org.openmaji.implementation.server.log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

import javax.net.SocketFactory;

public class LogClient {

	public static void main(String[] args) {
		String host = "zoup";
		int port = 20020;
		
		try {
			Socket socket = SocketFactory.getDefault().createSocket(host, port);
			InputStreamReader reader = new InputStreamReader(socket.getInputStream());
			BufferedReader bufferedReader = new BufferedReader(reader);
			
			for (String str = bufferedReader.readLine(); str != null; str = bufferedReader.readLine()) {
				System.out.println(str);
			}
			System.err.println("fallen out");
		}
		catch (Throwable t) {
			t.printStackTrace();
		}
	}
}
