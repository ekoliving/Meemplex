/*
 * @(#)ErrorRepository.java
 *
 * Copyright 2004 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
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
