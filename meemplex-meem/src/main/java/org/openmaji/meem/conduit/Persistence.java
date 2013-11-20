/*
 * @(#)Persistence.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
package org.openmaji.meem.conduit;

/**
 * General conduit by which system and application wedges inform each other of the 
 * need to deal with self managed persistence or loading.
 * <p>
 * The conduit can be used in two ways, either purely to as a channel by which to advise 
 * other wedges of persistence events or as a means to also listen for persistence events
 * as they take place elsewhere in the Meem.
 * <p>
 * In the situation where the conduit is simply being used to advise other wedges of a
 * persistence event it needs to be included in the wedge in the following manner:
 * <pre>
 *     public Persistence persistenceConduit;
 * </pre>
 * Persist and restore events can then be communicated to other wedges by executing:
 * <pre>
 *     persistenceConduit.persist();
 * </pre>
 * or
 * <pre>
 *     persistenceConduit.restore();
 * </pre>
 * <p>
 * If the conduit also needs to be used to listen to persistence events you need to assign
 * a target to it, as in:
 * <pre>
 *     public Persistence persistenceConduit = new Persistence()
 *                                    {
 *                                        public void persist()
 *                                        {
 *                                                  // handle persist request...
 *                                        }
 * 
 *                                        public void restore()
 *                                        {
 *                                                 // handle restore request...
 *                                        }
 *                                    }
 * </pre>
 * This allows the wedge to also recieve notifications from other wedges, in addition to its 
 * own.
 */
public interface Persistence
{
	/**
	 * On the conduit: inform other wedges of the need to persist any data they are managing
	 * themselves.
	 * <p>
	 * As a listener: be told to persist.
	 */
    public void persist();
    
	/**
	 * On the conduit: inform other wedges of the need to load any data they are managing
	 * themselves.
	 * <p>
	 * As a listener: be told to load.
	 */
    public void restore();
}
