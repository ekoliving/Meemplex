package org.openmaji.implementation.server.log;

public class LogServer {

	/**
	 * Serves up, via a socket connections, the contents of a file. When data is
	 * appended to the file the data is sent to all the open socket connections.
	 * 
	 * "filename" and "port" may be inclkuded as arguments.
	 * filename is the file to read - each line is sent to clients.
	 * The port is the port to bind to and listen on for client connections.
	 * 
	 * @param args
	 */
	public static final void main(String[] args) {
		try {
			int port = 10010;
			String filename = "meemkitDescriptor.xml";
			
			if (args.length > 0) {
				filename = args[0];
			}
			if (args.length > 1) {
				port = Integer.parseInt(args[1]);
			}
			
			LogSocketServer server = new LogSocketServer(port);
			LogReader logReader = new LogReader(filename);			
			server.addLogReader(logReader);
			server.start();
			logReader.start();
		}
		catch (Throwable t) {
			t.printStackTrace();
		}
	}
}
