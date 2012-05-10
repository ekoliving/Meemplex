/*
 * @(#)ConfigurationRejectedException.java
 *
 * Copyright 2004 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
package org.openmaji.meem.wedge.configuration;

/**
 * If a configuration setter is called and the arguments passed are invalid use the 
 * ConfigurationRejectedException to signall back to the requestor that the configuration
 * request was rejected.
 * <p>
 * The requestor will be passed back the message argument to the exception as the reason.
 */
public class ConfigurationRejectedException extends Exception
{
	private static final long serialVersionUID = -1178365040590887015L;


    /**
     * Base constructor.
     */
    public ConfigurationRejectedException()
    {
        super();
    }

    /**
     * @param message
     */
    public ConfigurationRejectedException(String message)
    {
        super(message);
    }

    /**
     * @param cause
     */
    public ConfigurationRejectedException(Throwable cause)
    {
        super(cause);
    }
}
