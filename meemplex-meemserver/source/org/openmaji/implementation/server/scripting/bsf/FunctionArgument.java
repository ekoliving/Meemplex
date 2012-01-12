package org.openmaji.implementation.server.scripting.bsf;

/**
 * Class used by FunctionDefinition to model the arguments of a function.
 * 
 * @author Chris Kakris
 */

public class FunctionArgument {
	private String name;

	private String type;

	private String description;

	public FunctionArgument(String name, String type, String description) {
		this.name = name;
		this.type = type;
		this.description = description;
	}

	public void appendSummary(StringBuffer buffer) {
		buffer.append(name);
		buffer.append('-');
		buffer.append(type);
	}

	public String getDescription() {
		return description;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

}
