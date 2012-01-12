package org.openmaji.implementation.common;

import org.swzoo.log2.core.LogFactory;
import org.swzoo.log2.core.LogTools;
import org.swzoo.log2.core.Logger;

import org.openmaji.common.IntegerPosition;
import org.openmaji.common.Linear;
import org.openmaji.common.Position;
import org.openmaji.common.StringValue;
import org.openmaji.common.Value;
import org.openmaji.common.Variable;
import org.openmaji.common.VariableList;
import org.openmaji.meem.Wedge;
import org.openmaji.meem.filter.Filter;
import org.openmaji.meem.wedge.configuration.ConfigurationClient;
import org.openmaji.meem.wedge.configuration.ConfigurationClientAdapter;
import org.openmaji.meem.wedge.configuration.ConfigurationSpecification;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClient;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClientAdapter;
import org.openmaji.system.meem.wedge.reference.ContentException;
import org.openmaji.system.meem.wedge.reference.ContentProvider;

/**
 * Provides a list of available Variables and accepts Variables from the list.
 * 
 * A conduit is used to send Linear, determining
 * the selected index.  Channel state is also received on a Linear conduit.
 * 
 * @author stormboy
 *
 */
public class VariableSelectWedge implements Wedge, Variable, Linear {


	private static final Logger logger = LogFactory.getLogger();
	
	private static boolean DEBUG = false;


	/* ---------------------- outbound facets ------------------------------ */
	
	/** facet on which to send the selected value */
	public Variable variableOutput;
	public ContentProvider variableOutputProvider = new ContentProvider() {
		public void sendContent(Object object, Filter filter) throws ContentException {
			((Variable)object).valueChanged(selectedChannelValue);
		}
	};

	/** facet on which to send selected variable index */
	public Linear linearOutput;
	public ContentProvider linearOutputProvider = new ContentProvider() {
		public void sendContent(Object object, Filter filter) throws ContentException {
			((Linear)object).valueChanged(
					new IntegerPosition(selectedVariableIndex, 1, 0, availableValues.length)
				);
		}
	};

	/** facet on which to send the possible values to select from */
	public VariableList variableListOutput;
	public ContentProvider variableListOutputProvider = new ContentProvider() {
		public void sendContent(Object object, Filter filter) throws ContentException {
			((VariableList)object).valueChanged(availableValues);
		}
	};


	/* ---------------------------- conduits ------------------------------- */
	
	/** 
	 * Conduit on which to received selected value.
	 */
	public Variable variableStateConduit = new Variable() {
		public void valueChanged(Value value) {
			if ( !value.equals(selectedChannelValue) ) {
				selectedChannelValue = value;
				variableOutput.valueChanged(value);
			}
			for (int i=0; i<availableValues.length; i++) {
				if (availableValues[i].equals(value)) {
					if ( i != selectedVariableIndex) {
						selectedVariableIndex = i;
						linearOutput.valueChanged(
							new IntegerPosition(selectedVariableIndex, 1, 0, availableValues.length)
						);
					}
					break;
				}
			}
		}
	};

	/** 
	 * Conduit on which to send selected value.
	 */
	public Variable variableControlConduit;


	/**
	 * Conduit on which to receive a list of available variables.
	 */
	public VariableList variableListStateConduit = new VariableList() {
		public void valueChanged(Value[] vars) {
			availableValues = vars;			
			variableListOutput.valueChanged(vars);
		}
	};
	
	public VariableList variableListControlConduit;
	
    /**
     * Conduit for handling lifecycle state changes. 
     */
	public LifeCycleClient lifeCycleClientConduit = new LifeCycleClientAdapter(this);

	/**
	 * Conduit for configuration of this wedge.
	 */
	public ConfigurationClient configurationClientConduit = new ConfigurationClientAdapter(this);


	/* ------------------------ configuration specification ---------------------- */
	
	public ConfigurationSpecification availableValuesSpecification =  new ConfigurationSpecification("List of available values");


	/* ----------------------- persisted properties -------------------------- */
	
	/**
	 *  the sources to select from
	 *  TODO make these configurable 
	 */
	public Value[] availableValues = new Value[] {
			new StringValue("CD"),
			new StringValue("TAPE"),
			new StringValue("TUNER - LAST BAND"),
			new StringValue("PHONO"),
			new StringValue("8CH"),
			new StringValue("VCR"),
			new StringValue("VID2"),
			new StringValue("VID1"),
			new StringValue("SAT"),
			new StringValue("DVD"),
			new StringValue("TUNER - DIRECT AM"),
			new StringValue("TUNER - DIRECT FM"),
	};


	/* -------------------------- private members -------------------------- */

	private int selectedVariableIndex = -1;

	private Value selectedChannelValue = null;


	/* ------------------- constructor -------------------------- */
	
	public VariableSelectWedge() {
	}


	/* -------------------------- configuration methods ------------------------ */
	
	public void setAvailableValues(String zoneString) {
		String[] channelNames = zoneString.split("\\s");
		this.availableValues = new Value[channelNames.length];
		for (int i=0; i<channelNames.length; i++) {
			this.availableValues[i] = new StringValue(channelNames[i]);
		}
	}

	public String getAvailableValues() {
		StringBuffer sb = new StringBuffer();
		for (int i=0; i<availableValues.length; i++) {
			if (i>0) { 
				sb.append(" ");
			}
			sb.append(availableValues.toString());
		}
		return sb.toString();
	}


	/* --------------------------- lifecycle methods ----------------------------- */
	
	public void commence() {
	}
	
	public void conclude() {
	}


	/* --------------------------- Variable interface ----------------------------- */

	/**
	 * Select a channel by name
	 */
	public void valueChanged(Value channelValue) {
		if (DEBUG) {
			LogTools.info(logger, "Received channel: " + channelValue);
		}

		variableControlConduit.valueChanged(channelValue);
	}
	

	/* --------------------------- Linear interface ----------------------------- */
	
	/**
	 * Select a channel by index
	 */
	public void valueChanged(Position position) {
		if (DEBUG) {
			LogTools.info(logger, "Received position: " + position);
		}
		
		int index = position.intValue();
		try {
			Value value = availableValues[index];			
			variableControlConduit.valueChanged(value);
		}
		catch (IndexOutOfBoundsException e) {
			//selectedChannelValue = new StringValue("Unknown");
		}
	}

}
