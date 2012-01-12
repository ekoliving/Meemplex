/*
 * @(#)TelnetIO.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */

/*
 * This code is based upon code from the TelnetD library.
 * http://sourceforge.net/projects/telnetd/
 * Used under the BSD license.
 */

package org.openmaji.implementation.server.nursery.scripting.telnet.io;

import java.io.*;
import java.net.InetAddress;

import org.openmaji.implementation.server.nursery.scripting.telnet.TelnetConnection;
import org.openmaji.implementation.server.nursery.scripting.telnet.event.ConnectionEvent;


/**
 * Class that represents the TelnetIO implementation. It contains
 * an inner IACHandler class to handle the telnet protocol level
 * communication.
 * <p>
 *  Although supposed to work full-duplex, we only process the telnet protocol
 *  layer communication in case of reading requests from the higher levels.
 *  This is the only way to meet the one thread per connection requirement.
 * </p>
 * <p> 
 *  The output is done via byte-oriented streams, definately suitable for the 
 *  telnet protocol. The format of the  output is UTF-8 (Unicode), which is a
 *  standard and supported by any telnet client, including the ones included
 *  in Microsoft OS's.
 * </p>
 *  <em>Notes:</em>
 *  <ul>
 *  <li>The underlying output is buffered, to ensure that all bytes written
 *  	are send, the flush() method has to be called.
 *  <li>This low-level routines ensure nice multithreading behaviour on I/O.
 *      Neither large outputs, nor input sequences excuted by the connection thread
 *      can hog the system. 
 *  </ul> 
 *
 *  @author Dieter Wimberger
 *  @version 1.2 06/09/99
 *   
 */
public class TelnetIO extends OutputStream {

	//Associations
	private TelnetConnection telnetConnection; //a reference to the connection this instance works for
	private DataOutputStream outstream; //the byte oriented outputstream
	private DataInputStream instream; //the byte oriented input stream

	//Aggregations
	private IACHandler iach; //holds a reference to the aggregated IACHandler

	//Members
	private InetAddress localAddy; //address of the host the telnetd is running on
	private boolean NOIAC = false; //describes if IAC was found and if its just processed 

	private boolean initializing;
	//private boolean closing;
	private boolean cr;
//	private boolean nl;

	/**  
	 * Creates a TelnetIO object for the given connection.<br>
	 * Input- and OutputStreams are properly set and the primary telnet
	 * protocol initialization is carried out by the inner IACHandler class.<BR>
	 * 
	 * @param telnetConnection the I/O is working for.
	 */
	public TelnetIO(TelnetConnection telnetConnection) {
		try {
			this.telnetConnection = telnetConnection;

			//we make an instance of our inner class
			iach = new IACHandler();
			//we setup underlying byte oriented streams 
			instream = new DataInputStream(telnetConnection.getSocket().getInputStream());
			outstream = new DataOutputStream(new BufferedOutputStream(telnetConnection.getSocket().getOutputStream()));
			//we save the local address (necessary?)
			localAddy = telnetConnection.getSocket().getLocalAddress();
			//closing = false;
			cr = false;
			//bootstrap telnet communication
			initTelnetCommunication();
		} catch (IOException e) {
			//handle properly
		}
	} //constructor 

	/**** Implementation of OutputStream ****************************************************/

	/**
	 * Method to output a byte. Ensures that CR(\r) is never send
	 * alone,but CRLF(\r\n), which is a rule of the telnet protocol.
	 *
	 * @param b Byte to be written.
	 */
	public void write(byte b) {
		try {
			//ensure CRLF(\r\n) is written for LF(\n) to adhere
			//to the telnet protocol. 
			if (!cr && b == 10) {
				outstream.write(13);
			}
			//ensure CRLF(\r\n) is written for CR(\r) to adhere
			//to the telnet protocol. 
			if (cr && b != 10) {
				outstream.write(10);
			}

			outstream.write(b);

			if (b == 13) {
				cr = true;
			} else {
				cr = false;
			}
		} catch (IOException e) {
			if (telnetConnection.isAlive())
				telnetConnection.processConnectionEvent(new ConnectionEvent(telnetConnection, ConnectionEvent.CONNECTION_BROKEN));
		}
	} //write(byte)

	/**
		 * Method to output an int.
	 *
		 * @param i Integer to be written.
	 */
	public void write(int i) {
		write((byte) i);
	} //write(int)

	/**
	 * Method to write an array of bytes.
	 *
	 * @param sequence byte[] to be written.
	 */
	public void write(byte[] sequence) {
		for (int z = 0; z < sequence.length; z++) {
			write(sequence[z]);
		}
	} //write(byte[])

	public void write(byte[] b, int off, int len) throws IOException {
		for (int i = 0; i < len; i++) {
			write(b[i + off]);
		}
	}

	/**
	 *  Method to output an array of int' s.
	 *
	 *  @param sequence [] to write
	 */
	public void write(int[] sequence) {
		for (int j = 0; j < sequence.length; j++) {
			write((byte) sequence[j]);
		}
	} //write(int[])

	/**
	 * Method to write a char.
		 *
	 * @param ch char to be written.
	 */
	public void write(char ch) {
		write((byte) ch);
	} //write(char)

	/**
	 * Method to output a string.
	 *
	 * @param str to be written.
	 */
	public void write(String str) {
		write(str.getBytes());
	} //write(String)

	/**
	 * Method to flush all buffered output. 
	 */
	public void flush() {
		try {
			outstream.flush();
		} catch (IOException e) {
			if (telnetConnection.isAlive())
				telnetConnection.processConnectionEvent(new ConnectionEvent(telnetConnection, ConnectionEvent.CONNECTION_BROKEN));

		}
	} //flush

	/**
	 * Method to close the underlying output stream to free system resources.<br>
	 * Most likely only to be called by the ConnectionManager upon clean up of
	 * connections that ended or died.
	 */
	public void closeOutput() {
		//closing = true;
		//sends telnetprotocol logout acknowledgement
		write(IAC);
		write(DO);
		write(LOGOUT);
		//and now close underlying outputstream

		try {
			outstream.close();
		} catch (IOException ex) {
			//handle?
		}
	} //close

	public OutputStream getOutputStream() {
		return outstream;
	}

	/**** End implementation of OutputStream ***********************************************/

	/**** Implementation of InputStream ****************************************************/

	/**
	 * Method to read a byte from the InputStream.
	 * Invokes the IACHandler upon IAC (Byte=255).
	 *  
	 * @return int read from stream.
	 */
	public int read() {
		int c = rawread();
		//if (c == 255) {
		NOIAC = false;
		while ((c == 255) && (!NOIAC)) {
			/**
			* Read next, and invoke
			* the IACHandler he is taking care of the rest. Or at least he should :)
			*/
			c = rawread();
			if (c != 255) {
				iach.handleC(c);
				c = rawread();
			} else {
				NOIAC = true;
			}
		}
		return stripCRSeq(c);
	} //read

	/**
	 * Method to close the underlying inputstream to free system resources.<br>
	 * Most likely only to be called by the ConnectionManager upon clean up of
	 * connections that ended or died.
	 */
	public void closeInput() {
		try {
			instream.close();
		} catch (IOException e) {
			//handle?
		}
	} //closeInput

	/**
	 * This method reads an unsigned 16bit Integer from the stream,
	 * its here for getting the NAWS Data Values for height and width.
	 */
	private int read16int() {

		try {
			int c = instream.readUnsignedShort();
			return c;
		} catch (EOFException e) {
			if (telnetConnection.isAlive())
				telnetConnection.processConnectionEvent(new ConnectionEvent(telnetConnection, ConnectionEvent.CONNECTION_BROKEN));
			//for the sanity of the compiler we return something
			return -1;
		} catch (IOException e) {
			if (telnetConnection.isAlive())
				telnetConnection.processConnectionEvent(new ConnectionEvent(telnetConnection, ConnectionEvent.CONNECTION_BROKEN));
			//for the sanity of the compiler we return something
			return -1;
		}
	} //read16int

	/**
	 * Method to read a raw byte from the InputStream.<br>
	 * Telnet protocol layer communication is filtered and processed here.
	 *
	 * @return int read from stream.
	 */
	private int rawread() {

		int b = 0;

		try {
			b = instream.readUnsignedByte();
			telnetConnection.activity();
			
			return b;
		} catch (EOFException e) {

			if (telnetConnection.isAlive())
				telnetConnection.processConnectionEvent(new ConnectionEvent(telnetConnection, ConnectionEvent.CONNECTION_BROKEN));

			//for the sanity of the compiler we return something
			return -1;
		} catch (IOException e) {
			//this also means that the stream is buggy or something
			//going wrong. By definition we react the same as in above case
			if (!initializing && telnetConnection.isAlive()) {

				telnetConnection.processConnectionEvent(new ConnectionEvent(telnetConnection, ConnectionEvent.CONNECTION_BROKEN));
			}
			//for the sanity of the compiler we return something
			return -1;
		}
	} //rawread

	/**
	 * Checks for the telnet protocol specified  CR followed by NULL or LF<BR>
	 * Subsequently reads for the next byte and forwards
	 * only a ENTER represented by LF internally.
	 */
	private int stripCRSeq(int input) {
		if (input == 13) {
			rawread();
			return 10;
		}
		return input;
	} //stripCRSeq

	/**** Implementation of InputStream ****************************************************/

	/****
	 * Following methods implement init/request/answer procedures for telnet
	 * protocol level communication.
	 */

	/**
		 * Method that initializes the telnet communication layer.
	 */
	private void initTelnetCommunication() {

		initializing = true;
		try {
			//start out, some clients just wait
			iach.doInit();
			//open for a defined timeout so we read incoming negotiation
			telnetConnection.getSocket().setSoTimeout(1000);
//			int i = read();
			//this is important, dont ask me why :)
			telnetConnection.getSocket().setSoTimeout(0);
		} catch (Exception e) {
			//handle properly
		}
		initializing = false;
	} //initTelnetCommunication

	/**
	 * Method that represents the answer to the 
	 * AreYouThere question of the telnet protocol specification
	 * 
	 * Output of the String [HostAdress:Yes]
	 */
	private void IamHere() {
		write("[" + localAddy.toString() + ":Yes]");
		flush();
	} //IamHere

	/**
	 * Method that checks reported terminal sizes and sets the
	 * asserted values in the ConnectionData instance associated with
	 * the connection.
	 * 
	 * @param width Integer that represents the Window width in chars
	 * @param height Integer that represents the Window height in chars
	 */
	private void setTerminalGeometry(int width, int height) {
		if (width < SMALLEST_BELIEVABLE_WIDTH) {
			width = DEFAULT_WIDTH;
		}
		if (height < SMALLEST_BELIEVABLE_HEIGHT) {
			height = DEFAULT_HEIGHT;
		}
		//DEBUG: write("[New Window Size " + window_width + "x" + window_height + "]"); 
		telnetConnection.setTerminalGeometry(width, height);
	} //setTerminalGeometry

	/**** End telnet protocol level communication methods *******************************/

	/**
	 * An inner class for handling incoming option negotiations implementing the <B>telnet protocol</B>
	 * specification based upon following Standards and RFCs:
	 * <OL>
	 * <LI><A HREF="ftp://ds.internic.net/rfc/rfc854.txt">854 Telnet Protocol Specification</A>
	 * <LI><A HREF="ftp://ds.internic.net/rfc/rfc855.txt">855 Telnet Option Specifications</A>
	 * <LI><A HREF="ftp://ds.internic.net/rfc/rfc857.txt">857 Telnet Echo Option</A>
	 * <LI><A HREF="ftp://ds.internic.net/rfc/rfc858.txt">858 Telnet Supress Go Ahead Option</A>
	 * <LI><A HREF="ftp://ds.internic.net/rfc/rfc727.txt">727 Telnet Logout Option</A>
	 * <LI><A HREF="ftp://ds.internic.net/rfc/rfc1073.txt">1073 Telnet Window Size Option</A>
	 * <LI><A HREF="ftp://ds.internic.net/rfc/rfc1091.txt">1091 Telnet Terminal-Type Option</A>
	 * </OL>
	 * 
	 *   Furthermore there are some more, which helped to solve problems, or might be important
	 *   for future enhancements:<BR>
	 *	<A HREF="ftp://ds.internic.net/rfc/rfc1143.txt">1143 The Q Method of Implementing Option Negotiation</A><BR>
	 *	<A HREF="ftp://ds.internic.net/rfc/rfc1416.txt">1416 Telnet Authentication Option</A><BR>
	 *   
	 *   After an intense study of the available material (mainly cryptical written RFCs,
	 *   a telnet client implementation for the macintosh based upon NCSA telnet, and a server side
	 *   implementation called key, a mud-like system completely written in Java) I realized 
	 *   the problems we are facing regarding to the telnet protocol:
	 *   <OL>
	 *   <LI> a minimal spread of invented options, which means there are a lot of invented options,
	 *        but rarely they made it through to become a standard.
	 *   <LI> Dependency on a special type of implementation is dangerous in our case.
	 *        We are no kind of host that offers the user to run several processes at once,
	 *        a BBS is intended to be a single process the user is interacting with.
	 *   <LI> The <B>LAMER</B> has to be expected to log in with the standard Mircosoft telnet  
	 *        implementation. This means forget every nice feature and most of the almost-standards.
	 *
	 *	</OL>
	 *	<BR>
	 *
	 *	@author	Dieter Wimberger
	 *	@version	1.1 16/06/1998
	 *
	 *
	 *   <B>To-Do</B>:<UL>
	 *   <LI>UNIX conform new style TTYPE negotiation. Setting a list and selecting from it...
	 *   </UL> 
	 */
	class IACHandler {

		/**
		 * Int that came next from the stream upon IAC read method 
		 */
//		private int c = 0;

		/**
		 * describes if a TelnetOption is defined or not
		 */
//		private boolean UNDEFINED = false;

		/**
		 * describes if a TelnetOption is supported or not
		 */
//		private boolean SUPPORTED = false;

		/**
		 * A Telnet SubNegotiation String
		 */
//		private String subnstring;

		/** 
		 * Telnet readin buffer 
		 * Here its implemented guys. Open your eyes upon this solution.
		 * The others take a one byte solution :)
		 */
		private int buffer[] = new int[2];

		/** 
		 * DO_ECHO or not
		 */
		private boolean DO_ECHO = false;

		/** 
		 * DO_SUPGA or not
		 */
		private boolean DO_SUPGA = false;

		/** 
		 * DO_NAWS or not
		 */
		private boolean DO_NAWS = false;

		/** 
		 * DO_TTYPE or not
		 */
		private boolean DO_TTYPE = false;

		/** 
		 * Are we waiting for a DO reply?
		 */
		private boolean WAIT_DO_REPLY_SUPGA = false,
			WAIT_DO_REPLY_ECHO = false,
			WAIT_DO_REPLY_NAWS = false,
			WAIT_DO_REPLY_TTYPE = false;

		/** 
		 * Are we waiting for a WILL reply?
		 */
		private boolean WAIT_WILL_REPLY_SUPGA = false,
			WAIT_WILL_REPLY_ECHO = false,
			WAIT_WILL_REPLY_NAWS = false,
			WAIT_WILL_REPLY_TTYPE = false;

		/**
		 * Width of the terminal window
		 */
		private int width = 0;

		/**
		 * Height of the terminal window
		 */
		private int height = 0;

		/**
		 * TerminalStrings Vector
		 */
		//private Vector terminaltypes;

		/**
		 *  Terminal String
		 */
//		private String Terminalstr;

		/**
		 *  IAC Option Commands sending buffer
		 */
//		private byte sendbuffer[];

		/**
		 * Method to handle a IAC that came in over the line.
		 *
		 * @param i (int)ed byte that followed the IAC
		 */
		public void handleC(int i) {
			buffer[0] = i;
			if (!parseTWO(buffer)) {
				buffer[1] = rawread();
				parse(buffer);
			}

			buffer[0] = 0;
			buffer[1] = 0;

		} //handleC

		/**
		 * Method that does initial Server-Side interest negotiation.
		 * Its necessary to wall our abilities to _dumb_ telnet client
		 * implementations (eg. passive and buggy programmed Windows Software ;)
		 */
		public void doInit() {

			/**
			 * The following lines mean we will echo every single character
			 * That means we produce an incredible network traffic overhead with
			 * our 255 byte headers for each character...in both directions!
			 * <br><br>
			 * Maybe there are solutions for line mode in some read() or input() methods,
			 * where we at least get a line per package. But...hmm. 
			 */
			sendCommand(WILL, ECHO, true);
			sendCommand(DONT, ECHO, true);
			sendCommand(DO, NAWS, true);
			sendCommand(WILL, SUPGA, true);
			sendCommand(DO, SUPGA, true);
			sendCommand(DO, TTYPE, true);
			//getTTYPE, most likely appropiate
			getTTYPE();
		} //doInit	

		/**
		 * Method that parses for options with two characters.
		 * 
		 * @param buffer [] that represents the first byte that followed the IAC first.
		 *
		 * @return true when it was a two byte command (IAC OPTIONBYTE)
		 */
		private boolean parseTWO(int[] buffer) {
			switch (buffer[0]) {
				case IAC :
					//doubled IAC to escape 255 is handled within the
					//read method.
					break;
				case AYT :
					IamHere();
					break;
				case AO :
				case IP :
				case EL :
				case EC :
					break;
				default :
					return false;
			}
			return true;
		} //parseTWO

		/**
		 * Method that parses further on for options.
		 * 
		 * @param Buffer that represents the first two bytes that followed the IAC.
		 */
		private void parse(int[] buffer) {
			switch (buffer[0]) {
				/* First switch on the Negotiation Option */
				case WILL :
					if (supported(buffer[1]) && isEnabled(buffer[1])) {
						; // do nothing
					} else {
						if (waitDOreply(buffer[1]) && supported(buffer[1])) {
							enable(buffer[1]);
							setWait(DO, buffer[1], false);
						} else {
							if (supported(buffer[1])) {
								sendCommand(DO, buffer[1], false);
								enable(buffer[1]);
							} else {
								sendCommand(DONT, buffer[1], false);
							}
						}
					}
					break;
				case WONT :
					if (waitDOreply(buffer[1]) && supported(buffer[1])) {
						setWait(DO, buffer[1], false);
					} else {
						if (supported(buffer[1]) && isEnabled(buffer[1])) {
							// eanable() Method disables an Option that is already enabled
							enable(buffer[1]);
						}
					}
					break;
				case DO :
					if (supported(buffer[1]) && isEnabled(buffer[1])) {
						; // do nothing
					} else {
						if (waitWILLreply(buffer[1]) && supported(buffer[1])) {
							enable(buffer[1]);
							setWait(WILL, buffer[1], false);
						} else {
							if (supported(buffer[1])) {
								sendCommand(WILL, buffer[1], false);
								enable(buffer[1]);
							} else {
								sendCommand(WONT, buffer[1], false);
							}
						}
					}
					break;
				case DONT :
					if (waitWILLreply(buffer[1]) && supported(buffer[1])) {
						setWait(WILL, buffer[1], false);
					} else {
						if (supported(buffer[1]) && isEnabled(buffer[1])) {
							// enable() Method disables an Option that is already enabled
							enable(buffer[1]);
						}
					}
					break;

					/* Now about other two byte IACs */
				case DM : //How do I implement a SYNCH signal? 
					break;
				case SB :
					if ((supported(buffer[1])) && (isEnabled(buffer[1]))) {
						switch (buffer[1]) {
							case NAWS :
								handleNAWS();
								break;
							case TTYPE :
								handleTTYPE();
								break;
							default :
								;
						}
					} else {
						//do nothing
					}
					break;
				default :
					;
			} //switch
		} //parse

		/**
		 * Method that reads a subnegotiation String,
		 * one of those that end with a IAC SE combination.
		 *
		 * @return IAC SE terminated String
		 */
		private String readIACSETerminatedString() {
			int where = 0;
			char[] buffer = new char[40];
			char b = ' ';
			boolean cont = true;

			do {
				int i;
				i = rawread();
				switch (i) {
					case IAC :
						i = rawread();
						if (i == SE) {
							cont = false;
						}
						break;
					case -1 :
						return (new String("default"));
					default :
						}
				if (cont) {
					b = (char) i;
					if (b == '\n' || b == '\r') {
						cont = false;
					} else {
						buffer[where++] = b;
					}
				}
			} while (cont);

			return (new String(buffer, 0, where));
		} //readIACSETerminatedString

		/**
		 * Method that reads a NawsSubnegotiation that ends up with a IAC SE
		 * If the measurements are unbelieveable it switches to the defaults. 
		 */
		private void handleNAWS() {
			width = read16int();
			if (width == 255) {
				width = read16int(); //handle doubled 255 value;
			}
			height = read16int();
			if (height == 255) {
				height = read16int(); //handle doubled 255 value;
			}
			// The next two should be IAC SE by the protocol specs. 
			if (rawread() != IAC) {
				; //throw new IACException("NAWS Lack of IAC SE Termination");
			}
			if (rawread() != SE) {
				; //throw new IACException("NAWS Lack of IAC SE Termination");
			}
			setTerminalGeometry(width, height);
		} //handleNAWS

		/**
		 * Method that reads a TTYPE Subnegotiation String that ends up with a IAC SE
		 * If no Terminal is valid, we switch to the dumb "none" terminal.
		 */
		private void handleTTYPE() {
			String tmpstr = "";
			// The next read should be 0 which is IS by the protocol
			// specs. hmmm?
			rawread(); //that should be the is :)
			tmpstr = readIACSETerminatedString();
			telnetConnection.setNegotiatedTerminalType(tmpstr);

		} //handleTTYPE

		/**
		 * Method that sends a TTYPE Subnegotiation Request.
		 * IAC SB TERMINAL-TYPE SEND
		 */
		public void getTTYPE() {
			if (isEnabled(TTYPE)) {
				//System.out.println("DEBUG: successfully called getTTYPE");		

				write(IAC);
				write(SB);
				write(TTYPE);
				write(SEND);
				write(IAC);
				write(SE);

				flush();
			}
		} //getTTYPE

		/**
		 * Method that informs internally about the supported Negotiation Options
		 * @param Integer that represents requested the Option
		 * @return Boolean that represents support status
		 */
		private boolean supported(int i) {
			switch (i) {
				case SUPGA :
				case ECHO :
				case NAWS :
				case TTYPE :
					return true;
				default :
					return false;
			}
		} //supported

		/**
		 * Method that sends a Telnet IAC String with TelnetIO.write(byte b) method.
		 * @param Integer that represents requested Command Type (DO,DONT,WILL,WONT)
		 * @param Integer that represents the Option itself (e.g. ECHO, NAWS)
		 */
		private void sendCommand(int i, int j, boolean westarted) {
			write(IAC);
			write(i);
			write(j);
			// we started with DO OPTION and now wait for reply
			if ((i == DO) && westarted)
				setWait(DO, j, true);
			// we started with WILL OPTION and now wait for reply
			if ((i == WILL) && westarted)
				setWait(WILL, j, true);

			flush();
		} //sendCommand

		/**
		 * Method enables or disables a supported Option
		 * @param Integer that represents the Option
		 */
		private void enable(int i) {
			switch (i) {
				case SUPGA :
					if (DO_SUPGA)
						DO_SUPGA = false;
					else
						DO_SUPGA = true;
					break;
				case ECHO :
					if (DO_ECHO)
						DO_ECHO = false;
					else
						DO_ECHO = true;
					break;
				case NAWS :
					if (DO_NAWS)
						DO_NAWS = false;
					else
						DO_NAWS = true;
					break;
				case TTYPE :
					if (DO_TTYPE)
						DO_TTYPE = false;
					else {
						DO_TTYPE = true;
						getTTYPE();
					}
			}
		} //enable

		/**
		 * Method that informs internally about the status of the supported
		 * Negotiation Options.
		 * @param Integer that represents requested the Option
		 * @return Boolean that represents the enabled status
		 */
		private boolean isEnabled(int i) {
			switch (i) {
				case SUPGA :
					return DO_SUPGA;
				case ECHO :
					return DO_ECHO;
				case NAWS :
					return DO_NAWS;
				case TTYPE :
					return DO_TTYPE;
				default :
					return false;
			}
		} //isEnabled

		/**
		 * Method that informs internally about the WILL wait status
		 * of an option. 
		 * @param Integer that represents requested the Option
		 * @return Boolean that represents WILL wait status of the Option
		 */
		private boolean waitWILLreply(int i) {
			switch (i) {
				case SUPGA :
					return WAIT_WILL_REPLY_SUPGA;
				case ECHO :
					return WAIT_WILL_REPLY_ECHO;
				case NAWS :
					return WAIT_WILL_REPLY_NAWS;
				case TTYPE :
					return WAIT_WILL_REPLY_TTYPE;
				default :
					return false;
			}
		} //waitWILLreply

		/**
		 * Method that informs internally about the DO wait status
		 * of an option. 
		 * @param Integer that represents requested the Option
		 * @return Boolean that represents DO wait status of the Option
		 */
		private boolean waitDOreply(int i) {
			switch (i) {
				case SUPGA :
					return WAIT_DO_REPLY_SUPGA;
				case ECHO :
					return WAIT_DO_REPLY_ECHO;
				case NAWS :
					return WAIT_DO_REPLY_NAWS;
				case TTYPE :
					return WAIT_DO_REPLY_TTYPE;
				default :
					return false;
			}
		} //waitDOreply

		/**
		 * Method that mutates the wait status of an option in
		 * negotiation. We need the wait status to keep track of
		 * negotiation in process. So we cant miss if we started out
		 * or the other and so on. 
		 * @param Integer values of  DO or WILL
		 * @param Integer that represents the Option
		 * @param Boolean that represents the status of wait that should be set
		 */
		private void setWait(int WHAT, int OPTION, boolean WAIT) {
			switch (WHAT) {
				case DO :
					switch (OPTION) {
						case SUPGA :
							WAIT_DO_REPLY_SUPGA = WAIT;
						case ECHO :
							WAIT_DO_REPLY_ECHO = WAIT;
						case NAWS :
							WAIT_DO_REPLY_NAWS = WAIT;
						case TTYPE :
							WAIT_DO_REPLY_TTYPE = WAIT;
					}
					break;
				case WILL :
					switch (OPTION) {
						case SUPGA :
							WAIT_WILL_REPLY_SUPGA = WAIT;
						case ECHO :
							WAIT_WILL_REPLY_ECHO = WAIT;
						case NAWS :
							WAIT_WILL_REPLY_NAWS = WAIT;
						case TTYPE :
							WAIT_WILL_REPLY_TTYPE = WAIT;
					}
					break;
			}
		} //setWait

	} //inner class IACHandler

	/** Constants declaration ***********************************************/

	//Telnet Protocoll Constants

	/**
	 * Interpret As Command 
	 */
	protected static final int IAC = 255;

	/**
	 * Go Ahead <BR> Newer Telnets do not make use of this option
	 * that allows a specific communication mode.	
	 */
	protected static final int GA = 249;

	/**
	 * Negotiation: Will do option
	 */
	protected static final int WILL = 251;

	/**
	 * Negotiation: Wont do option
	 */
	protected static final int WONT = 252;

	/**
	 * Negotiation: Do option
	 */
	protected static final int DO = 253;

	/**
	 * Negotiation:  Dont do option
	 */
	protected static final int DONT = 254;

	/**
	 * Marks start of a subnegotiation.
	 */
	protected static final int SB = 250;

	/**
	 * Marks end of subnegotiation.
	 */
	protected static final int SE = 240;

	/**
	 * No operation
	 */
	protected static final int NOP = 241;

	/** 
	 *  Data mark its the data part of a SYNCH which helps to clean up the buffers between
	 *  Telnet Server &lt;-&gt; Telnet Client. <BR>
	 *  It should work like this we send a TCP urgent package and &lt;IAC&gt; &lt;DM&gt; the receiver
	 *  should get the urgent package (SYNCH) and just discard everything until he receives
	 *  our &lt;IAC&gt; &lt;DM&gt;.<BR>
	 *  <EM>Remark</EM>:
	 *  <OL>
	 *  	<LI>can we send a TCP urgent package?
	 *  	<LI>can we make use of the thing at all?
	 *	</OL>
	 */
	protected static final int DM = 242;

	/**
	 * Break
	 */
	protected static final int BRK = 243;

	/**
	 * The following implement the NVT (network virtual terminal) which offers the concept
	 * of a simple "printer". They are the basical meanings of control possibilities
	 * on a standard telnet implementation.
	 */

	/**
	 * Interrupt Process
	 */
	protected static final int IP = 244;

	/**
	 * Abort Output
	 */
	protected static final int AO = 245;

	/** 
	 * Are You There
	 */
	protected static final int AYT = 246;

	/**
	 * Erase Char
	 */
	protected static final int EC = 247;

	/** 
	 * Erase Line
	 */
	protected static final int EL = 248;

	/**
	 * The following are constants for supported options,
	 * which can be negotiated based upon the telnet protocol 
	 * specification.
	 */

	/**
	 * Telnet Option: ECHO 
	 */
	protected static final int ECHO = 1;

	/**
	 * Telnet Option: SUPress Go Ahead<br>
	 * This will be negotiated, all new telnet protocol implementations are
	 * recommended to do this.
	 */
	protected static final int SUPGA = 3;

	/** 
	 * The following options are options for which we also support subnegotiation
	 * based upon the telnet protocol specification.
	 */

	/**
	 * Telnet Option: Negotiate About Window Size<br>
	 * <ul>
	 * 	<li>Server request is IAC DO NAWS
	 * 	<li>Client response contains subnegotiation with data (columns, rows).
	 * </ul>
	 */
	protected static final int NAWS = 31;

	/** 
	 * Telnet Option: Terminal TYPE <br>
	 * <ul>
	 * 	<li>Server request contains subnegotiation SEND
	 * 	<li>Client response contains subnegotiation with data IS,terminal type string		
	 * </ul>
	 */
	protected static final int TTYPE = 24;

	/**
	 * TTYPE subnegotiation: IS
	 * 
	 */
	protected static final int IS = 0;

	/**
	 * TTYPE subnegotiation: SEND
	 */
	protected static final int SEND = 1;

	/** 
	 * Telnet Option: Logout<br>
	 * This allows nice goodbye to time-outed or unwanted clients.
	 */
	protected static final int LOGOUT = 18;

	/** 
	 * The following options are options which might be of interest, but are not
	 * yet implemented or in use.
	 */

	/**
	 * Unused
	 */
		protected static final int EXT_ASCII = 17, //Defines Extended ASCII
		SEND_LOC = 23, //Defines Send Location 	
		AUTHENTICATION = 37, //Defines Authentication 
	ENCRYPT = 38; //Defines Encryption	

	/** 
	 * Window Size Constants  
	 */
	private static final int SMALLEST_BELIEVABLE_WIDTH = 20, SMALLEST_BELIEVABLE_HEIGHT = 6, DEFAULT_WIDTH = 80, DEFAULT_HEIGHT = 25;

	/** end Constants declaration **************************************************/

} //class TelnetIO
