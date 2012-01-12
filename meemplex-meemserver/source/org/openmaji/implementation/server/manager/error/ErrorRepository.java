/*
 * @(#)ErrorRepository.java
 *
 * Copyright 2004 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */
package org.openmaji.implementation.server.manager.error;

/**
 * @author mg
 */
public interface ErrorRepository {
	
	public class spi {
		public static String getIdentifier() {
			return "errorRepository";
		}
	}
}
