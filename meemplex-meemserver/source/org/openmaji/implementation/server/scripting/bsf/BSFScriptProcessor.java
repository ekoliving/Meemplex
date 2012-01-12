package org.openmaji.implementation.server.scripting.bsf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.TreeMap;

import org.openmaji.implementation.server.utility.StringUtility;
import org.swzoo.log2.core.LogFactory;
import org.swzoo.log2.core.LogTools;
import org.swzoo.log2.core.Logger;

/**
 * @author Chris Kakris
 */

public class BSFScriptProcessor {
	private static final Logger logger = LogFactory.getLogger();

	public static final String KEY_HELP_TOPIC = "topic:";

	public static final String KEY_HELP_KEYWORDS = "keywords:";

	public static final String KEY_HELP_DESCRIPTION = "description:";

	public static final String KEY_HELP_END = "endhelp:";

	public static final String KEY_FUNCTION = "function:";

	public static final String KEY_FUNCTION_DESCRIPTION = "functiondescription:";

	public static final String KEY_FUNCTION_TYPE = "functiontype:";

	public static final String KEY_FUNCTION_END = "functionend:";

	public static final String KEY_ARGUMENT = "argument:";

	public static final String KEY_ARGUMENT_DESCRIPTION = "argumentdescription:";

	public static final String KEY_ARGUMENT_TYPE = "argumenttype:";

	private TreeMap<String, FunctionDefinition> functions;

	private int longestFunctionName = 0;

	private Hashtable<String, List<String>> helpKeywords;

	private Hashtable<String, String> helpTopics;

	private String directoryName;

	private int numberDefinitions = 0;

	public BSFScriptProcessor(String directoryName) {
		this.functions = new TreeMap<String, FunctionDefinition>();
		this.helpKeywords = new Hashtable<String, List<String>>();
		this.helpTopics = new Hashtable<String, String>();
		this.directoryName = directoryName;
	}

	public void setDirectoryName(String directoryName) {
		this.directoryName = directoryName;
	}

	public String getFunction(String function) {
		String description = null;
		return description;
	}

	public Iterator<String> getFunctions() {
		return functions.keySet().iterator();
	}

	public String getHelp(String topicOrKeyword) throws IOException {
		return getKeywordOrTopic(topicOrKeyword.toLowerCase());
	}

	public String getHelpTopics() {
		StringBuffer buffer = new StringBuffer();
		Enumeration<String> enumeration = helpTopics.keys();
		buffer.append("Help topics available");
		while (enumeration.hasMoreElements()) {
			buffer.append("\n\t");
			buffer.append(enumeration.nextElement());
		}
		return buffer.toString();
	}

	public String getHelpKeywords() {
		StringBuffer buffer = new StringBuffer();
		Enumeration<String> enumeration = helpKeywords.keys();
		buffer.append("All known keywords\n");
		while (enumeration.hasMoreElements()) {
			buffer.append(enumeration.nextElement());
			buffer.append(" ");
		}
		buffer.toString();
		return buffer.toString();
	}

	/**
	 * Generate an index of the available functions in a simple text format.
	 * 
	 * @return A string containing the index.
	 */

	public String getIndexAsText() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(StringUtility.pad("Function name", longestFunctionName + 3));
		buffer.append("Brief description of function\n");
		buffer.append(StringUtility.pad("-------------", longestFunctionName + 3));
		buffer.append("-----------------------------\n");

		Iterator<String> iterator = functions.keySet().iterator();
		while (iterator.hasNext()) {
			String key = (String) iterator.next();
			FunctionDefinition definition = (FunctionDefinition) functions.get(key);
			buffer.append(StringUtility.pad(definition.getName(), longestFunctionName + 3));
			buffer.append(definition.getDescription());
			buffer.append('\n');
		}

		return buffer.toString();
	}

	public String getIndexAsXML() {
		StringBuffer buffer = new StringBuffer();

		buffer.append("<functions>\n");
		Iterator<String> iterator = functions.keySet().iterator();
		while (iterator.hasNext()) {
			String key = (String) iterator.next();
			FunctionDefinition definition = (FunctionDefinition) functions.get(key);
			buffer.append("  <function>\n");
			buffer.append("    <name>");
			buffer.append(definition.getName());
			buffer.append("</name>\n");
			buffer.append("    <description>");
			buffer.append(definition.getDescription());
			buffer.append("</description>\n");
			buffer.append("    <type>");
			buffer.append(definition.getFunctionType());
			buffer.append("</type>\n");
			buffer.append("    <filename>");
			buffer.append(definition.getFilename());
			buffer.append("</filename>\n");
			Iterator<FunctionArgument> argsIterator = definition.getArguments();
			if (argsIterator.hasNext()) {
				buffer.append("    <arguments>\n");
				while (argsIterator.hasNext()) {
					buffer.append("      <argument>\n");
					FunctionArgument argument = (FunctionArgument) argsIterator.next();
					buffer.append("        <name>");
					buffer.append(argument.getName());
					buffer.append("</name>\n");
					buffer.append("        <description>");
					buffer.append(argument.getDescription());
					buffer.append("</description>\n");
					buffer.append("        <type>");
					buffer.append(argument.getType());
					buffer.append("</type>\n");
					buffer.append("      </argument>\n");
				}
				buffer.append("    </arguments>\n");
			}
			buffer.append("  </function>\n");
		}
		buffer.append("</functions>\n");

		return buffer.toString();
	}

	public void scanScripts() throws IOException {
		functions.clear();
		helpKeywords.clear();
		helpTopics.clear();

		scanScripts(directoryName, directoryName.length());
	}

	private void scanScripts(String path, int originalPathLength) throws IOException {
		File file = new File(path);
		if (file.exists() == false)
			return;

		if (file.isDirectory() == false) {
			if (path.endsWith(".bsh")) {
				try {
					processScriptFile(file, path, originalPathLength);
				}
				catch (Exception ex) {
					LogTools.error(logger, "scanScripts() - " + ex.getMessage());
				}
			}
			return;
		}

		String[] files = null;
		files = file.list();
		if (files == null)
			return;
		for (int i = 0; i < files.length; i++) {
			String newPath = path + File.separator + files[i];
			scanScripts(newPath, originalPathLength);
		}
	}

	private void processScriptFile(File file, String path, int originalPathLength) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String keywords = null;
		String topic = null;
		String line = null;
		boolean helpDone = false;

		while ((line = reader.readLine()) != null) {
			String lowerLine = line.toLowerCase();
			if (helpDone == false) {
				if (lowerLine.startsWith(KEY_HELP_KEYWORDS))
					keywords = lowerLine.substring(KEY_HELP_KEYWORDS.length()).trim();
				else if (lowerLine.startsWith(KEY_HELP_TOPIC))
					topic = lowerLine.substring(KEY_HELP_TOPIC.length()).trim();
				if (keywords != null && topic != null && topic.length() > 0) {
					addHelpKeywords(keywords, topic, path);
					helpDone = true;
				}
			}
			if (lowerLine.startsWith(KEY_FUNCTION)) {
				String relativeFilename = file.getAbsolutePath().substring(originalPathLength + 1);
				processFunction(reader, line, file.getAbsolutePath(), relativeFilename);
			}
		}
		reader.close();

		if (topic == null)
			throw new IOException("The tag '" + KEY_HELP_TOPIC + "' not found in " + file.getAbsolutePath());
		if (keywords == null)
			throw new IOException("The tag '" + KEY_HELP_KEYWORDS + "' not found in " + file.getAbsolutePath());
	}

	private void processFunction(BufferedReader reader, String firstLine, String filename, String relativeFilename) throws IOException {
		String name = stripKey(firstLine, KEY_FUNCTION, null, filename);
		String description = stripKey(reader.readLine(), KEY_FUNCTION_DESCRIPTION, name, filename);
		String type = stripKey(reader.readLine(), KEY_FUNCTION_TYPE, name, filename);

		FunctionDefinition definition = new FunctionDefinition(name, description, type, relativeFilename);

		String line = null;
		while ((line = reader.readLine()) != null) {
			line = line.toLowerCase();
			if (line.startsWith(KEY_FUNCTION_END)) {
				addFunctionDefinition(definition);
				numberDefinitions++;
				return;
			}
			else if (line.startsWith(KEY_ARGUMENT)) {
				FunctionArgument functionArgument = processFunctionArgument(reader, line, name, filename);
				definition.addArgument(functionArgument);
			}
			else {
				throw new IOException("Definition for function '" + name + "' badly formed in " + filename);
			}
		}

		throw new IOException("Unexpected EOF for " + filename);
	}

	private FunctionArgument processFunctionArgument(BufferedReader reader, String line, String functionName, String filename) throws IOException {
		String name = stripKey(line, KEY_ARGUMENT, functionName, filename);
		String description = stripKey(reader.readLine(), KEY_ARGUMENT_DESCRIPTION, functionName, filename);
		String type = stripKey(reader.readLine(), KEY_ARGUMENT_TYPE, functionName, filename);

		return new FunctionArgument(name, type, description);
	}

	private String stripKey(String line, String key, String functionName, String filename) throws IOException {
		if (line == null)
			throw new IOException("Unexpected EOF for " + filename);
		if (line.toLowerCase().startsWith(key) == false)
			throw new IOException("Expected tag '" + key + "' for function '" + functionName + "' not found in " + filename);
		String value = line.substring(key.length()).trim();
		if (value == null || value.length() == 0)
			throw new IOException("The tag '" + key + "' for function '" + functionName + "' does not have a value in " + filename);
		return value;
	}

	private void addHelpKeywords(String keywords, String topic, String filename) throws IOException {
		if (keywords == null || topic == null)
			return;

		String tmp = (String) helpTopics.get(topic);
		if (tmp != null) {
			LogTools.error(logger, "addHelpKeywords() - The topic '" + topic + "' already exists");
			return;
		}

		helpTopics.put(topic, filename);

		StringTokenizer st = new StringTokenizer(keywords);
		while (st.hasMoreTokens()) {
			String keyword = st.nextToken();
			List<String> topics = helpKeywords.get(keyword);
			if (topics == null) {
				List<String> fred = new ArrayList<String>();
				fred.add(topic);
				helpKeywords.put(keyword, fred);
			}
			else {
				topics.add(topic);
			}
		}
	}

	private String getKeywordOrTopic(String str) throws IOException {
		StringBuffer buffer = new StringBuffer();
		List<String> topics = helpKeywords.get(str);
		if (topics == null) {
			String topic = str;
			String filename = helpTopics.get(topic);
			if (filename == null) {
				return "No keywords or topics matched your query";
			}
			return getHelpFromScript(filename);
		}

		if (topics.size() == 1) {
			String topic = topics.get(0);
			String filename = helpTopics.get(topic);
			return getHelpFromScript(filename);
		}

		buffer.append("Topics with that keyword:");
		for (int i = 0; i < topics.size(); i++) {
			buffer.append("\n\t");
			buffer.append(topics.get(i));
		}

		return buffer.toString();
	}

	private String getHelpFromScript(String filename) throws IOException {
		StringBuffer buffer = new StringBuffer();
		File file = new File(filename);
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line = null;
		boolean doPrint = false;
		boolean keepScanning = true;
		while (keepScanning == true) {
			line = reader.readLine();
			if (line != null) {
				if (doPrint == false && line.toLowerCase().startsWith(KEY_HELP_DESCRIPTION))
					doPrint = true;
				if (line.toLowerCase().startsWith(KEY_HELP_END))
					keepScanning = false;
				if (doPrint && keepScanning) {
					buffer.append(line);
					buffer.append('\n');
				}
			}
		}
		reader.close();

		return buffer.toString();
	}

	/**
	 * Add a function description.
	 * 
	 * @param description
	 *            The description of the beanshell function
	 */

	private void addFunctionDefinition(FunctionDefinition description) {
		functions.put(description.getSummary(), description);
		int length = description.getName().length();
		if (length > longestFunctionName)
			longestFunctionName = length;
	}

	private static void doMain(String[] args) throws Exception {
		if (args.length == 0)
			throw new Exception("The first argument must be specify a directory containing the beanshell scripts");

		BSFScriptProcessor processor = new BSFScriptProcessor(args[0]);
		processor.scanScripts();

		if (args.length == 1) {
			System.out.println(processor.getIndexAsText());
		}
		else if (args.length == 2) {
			String xml = processor.getIndexAsXML();
			File file = new File(args[1]);
			PrintWriter writer = new PrintWriter(new FileWriter(file));
			writer.write(xml);
			writer.close();
		}
	}

	public static void main(String[] args) {
		try {
			doMain(args);
		}
		catch (Exception ex) {
			System.err.println(ex.getMessage());
			System.exit(1);
		}
	}
}
