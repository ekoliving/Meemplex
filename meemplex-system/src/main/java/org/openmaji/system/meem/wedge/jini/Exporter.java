/*
 * @(#)Exporter.java
 *
 * Copyright 2004 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
package org.openmaji.system.meem.wedge.jini;

import org.openmaji.meem.Facet;
import org.openmaji.spi.MajiSPI;


/**
 * Place holder for the underlying implementation of the wedge that does Jini exporting.
 * <p>
 * To make use of the Jini exporter you should include Exporter as one of the wedges associated
 * with the definition of the meem being defined, as in:
 * <pre>
 *      MeemDefinition    meemDefinition = MeemDefinitionFactory.spi.create().createMeemDefinition(new Class[] { Exporter.class, OtherWedge.class });
 * </pre> 
 * RMI objects which need to be exported to Jini can then be passed to the exportableServiceConduit and will subsequently be published
 * as Jini services avaliable as part of the meem server's name space.
 */
public interface Exporter
    extends Facet
{ 
	/**
	 * Nested class for service provider.
	 */
	public class spi
    {
        public static Exporter create()
        {
            return ((Exporter)MajiSPI.provider().create(Exporter.class));
        }

        public static String getIdentifier()
        {
            return ("jiniExport");
        };
    }
}
