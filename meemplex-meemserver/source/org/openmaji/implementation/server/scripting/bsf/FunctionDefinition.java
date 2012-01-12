package org.openmaji.implementation.server.scripting.bsf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * This class is used to hold the definition of a beanshell function. Used in conjunction with BSFScriptProcessor it is used to provide a textual description of a function
 * including its arguments and return type.
 * 
 * @author Chris Kakris
 */

public class FunctionDefinition {
	private String name;

	private String type;

	private String description;

	private String filename;

	private List<FunctionArgument> arguments = Collections.synchronizedList(new ArrayList<FunctionArgument>());

	/**
	 * Create an instance with the specified function name, return type and description.
	 * 
	 * @param name
	 *            The name of the beanshell function
	 * @param type
	 * @param description
	 */

	public FunctionDefinition(String name, String description, String type, String filename) {
		this.name = name;
		this.description = description;
		this.type = type;
		this.filename = filename;
	}

	/**
	 * Add an argument description.
	 */

	public void addArgument(FunctionArgument functionArgument) {
		arguments.add(functionArgument);
	}

	public Iterator<FunctionArgument> getArguments() {
		return arguments.iterator();
	}

	/**
	 * Return the name of the beanshell function.
	 * 
	 * @return The name of the beanshell function
	 */

	public String getName() {
		return this.name;
	}

	public String getFunctionType() {
		return this.type;
	}

	/**
	 * Return the description of the beanshell function.
	 * 
	 * @return The description of the beanshell function.
	 */

	public String getDescription() {
		return this.description;
	}

	public String getFilename() {
		return filename;
	}

	/**
	 * Returns a summary of this function suitable to be used as a key by BSFScriptProcessor when stored in a TreeMap. The key consists of the function name appended with the
	 * arguments of the function's arguments.
	 * 
	 * @return A summary of this function
	 */

	public String getSummary() {
		StringBuffer buffer = new StringBuffer();
		Iterator<FunctionArgument> iterator = arguments.iterator();
		buffer.append(this.name);
		while (iterator.hasNext()) {
			buffer.append('_');
			FunctionArgument argument = (FunctionArgument) iterator.next();
			argument.appendSummary(buffer);
		}
		return buffer.toString();
	}

	public String toString() {
		return getClass().getName() + "[name=" + name + " type=" + type + " description=" + description + "]";
	}
}
