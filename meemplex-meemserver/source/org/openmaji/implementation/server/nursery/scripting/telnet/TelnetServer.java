/*
 * @(#)TelnetServer.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.server.nursery.scripting.telnet;

import org.openmaji.meem.Facet;
import org.openmaji.spi.MajiSPI;

/**
 * <p>
 * ...
 * </p>
 * 
 * @author mg
 * @version 1.0
 */
public interface TelnetServer extends Facet {

	public void addServer(int port, String sessionClass);

	public void removeServer(int port);

	/* ---------- Nested class for SPI ----------------------------------------- */

	public class spi {
		public static TelnetServer create() {
			return ((TelnetServer) MajiSPI.provider().create(TelnetServer.class));
		}

		public static String getIdentifier() {
			return ("telnetServer");
		};
	}
}