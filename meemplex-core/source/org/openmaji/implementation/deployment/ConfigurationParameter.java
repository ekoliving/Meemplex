/*
 * Created on 29/06/2005
 */
package org.openmaji.implementation.deployment;

import java.io.File;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.jdom.Element;
import org.swzoo.log2.core.LogFactory;
import org.swzoo.log2.core.LogTools;
import org.swzoo.log2.core.Logger;

import org.openmaji.meem.wedge.configuration.ConfigurationIdentifier;

/**
 * @author Warren Bloomer
 *
 */
public final class ConfigurationParameter {

    private static final Logger logger = LogFactory.getLogger();

    private final ConfigurationIdentifier configurationIdentifier;
	private final Serializable value;

	public ConfigurationParameter(ConfigurationIdentifier ci, Serializable value) {
		this.configurationIdentifier = ci;
		this.value = value;
	}
	
    public ConfigurationParameter(ConfigurationIdentifier ci, String type, String valueString) {
        this.configurationIdentifier = ci;
        this.value = parseValueString(type,valueString);
    }
    
    public ConfigurationParameter(Element element) {
      String wedge = element.getAttributeValue("wedge");
      String name = element.getAttributeValue("name");
      this.configurationIdentifier = new ConfigurationIdentifier(wedge,name);
      String type = element.getAttributeValue("type");
      String valueString = element.getAttributeValue("value");
      this.value = parseValueString(type,valueString);
    }

    private Serializable parseValueString(String type, String valueString) {
    	Serializable value = null;
        
        try {
            if (type == null) {
                value = valueString;
            }
            else if ( type.equalsIgnoreCase("string") ) {
                value = new String(valueString);
            }
            else if ( type.equalsIgnoreCase("integer") ) {
                value = new Integer(valueString);
            }
            else if ( type.equalsIgnoreCase("long") ) {
                value = new Long(valueString);
            }
            else if ( type.equalsIgnoreCase("float") ) {
                value = new Float(valueString);
            }
            else if ( type.equalsIgnoreCase("double") ) {
                value = new Double(valueString);
            }
            else if ( type.equalsIgnoreCase("boolean") ) {
                value = new Boolean(valueString);
            }
            else if ( type.equalsIgnoreCase("date") ) {
                // TODO get date format
                DateFormat dateFormat = new SimpleDateFormat();
                try {
                    value = dateFormat.parse(valueString);
                }
                catch (ParseException ex) {
                    LogTools.info(logger, "date has invalid format: " + valueString);
                }
            }
            else if ( type.equalsIgnoreCase("file") ) {
                value = new File(valueString);
            }
            else {
                value = valueString;            
            }
        }
        catch (RuntimeException ex) {
            LogTools.info(logger, "problem getting parameter value for \"" + valueString + "\"", ex);
        }
        return value;
    }

    public ConfigurationIdentifier getConfigurationIdentifier() {
		return configurationIdentifier;
	}
	
	public Serializable getValue() {
		return value;
	}
}
