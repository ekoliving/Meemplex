/*
 * @(#)OutboundConfigurationHandler.java
 * 
 * Copyright 2004 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.rpc.binding.facet;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.openmaji.meem.wedge.configuration.ConfigurationHandler;
import org.openmaji.meem.wedge.configuration.ConfigurationIdentifier;
import org.openmaji.rpc.binding.OutboundBinding;

/**
 * @author Ravishankar Hiremath
 *
 * An outbound ConfigurationHandler Facet
 * Connect to an inbound Facet on the meem SystemModel
 * 
 */
public class OutboundConfigurationHandler extends OutboundBinding implements
        ConfigurationHandler {

    /**
     * constructor
     *
     */
    public OutboundConfigurationHandler() {
        setFacetClass(ConfigurationHandler.class);
    }
    
    
    /* (non-Javadoc)
     * @see org.openmaji.meem.wedge.configuration.ConfigurationHandler#valueChanged(org.openmaji.meem.wedge.configuration.ConfigurationIdentifier, java.io.Serializable)
     */
    public void valueChanged(ConfigurationIdentifier configurationIdentifier, Serializable value) {
        Map<String, String> configIdentifierTable = new HashMap<String, String>();
        configIdentifierTable.put("wedgeID",configurationIdentifier.getWedgeIdentifier());
        configIdentifierTable.put("propertyName",configurationIdentifier.getFieldName());
        String propertyValue = (String) value;
        System.out.println("ConfigIdentifierTable = " + configIdentifierTable.toString());
        System.out.println("Property Value = " + propertyValue);
        send("valueChanged", new Object[] { configIdentifierTable, propertyValue });
    }

}
