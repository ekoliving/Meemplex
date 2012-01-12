package org.openmaji.implementation.server.nursery.jini;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.StringTokenizer;


import org.openmaji.implementation.server.Common;
import org.openmaji.implementation.server.space.meemspace.MeemSpace;
import org.openmaji.implementation.server.utility.PropertiesLoader;
import org.openmaji.meem.MeemContext;
import org.openmaji.meem.Wedge;
import org.openmaji.meem.definition.MeemDefinition;
import org.openmaji.meem.definition.MeemDefinitionFactory;
import org.openmaji.meem.definition.MeemDefinitionProvider;
import org.openmaji.meem.wedge.error.ErrorHandler;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClient;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClientAdapter;
import org.openmaji.meem.wedge.lifecycle.Vote;
import org.swzoo.log2.core.LogFactory;
import org.swzoo.log2.core.LogTools;
import org.swzoo.log2.core.Logger;


public class JiniServicesWedge implements Wedge, JiniServices, MeemDefinitionProvider {
	private static Logger logger = LogFactory.getLogger();

	public MeemContext meemContext;

	public ErrorHandler errorHandlerConduit;

	public Vote lifeCycleControlConduit = null;

	public LifeCycleClient lifeCycleClientConduit = new LifeCycleClientAdapter(this);

	private MeemDefinition meemDefinition = null;

	private int jiniStarterPort;
	
	private Process jiniProcess;

	/**
	 * Constructor
	 */
	public JiniServicesWedge() {

		jiniStarterPort = new Integer(System.getProperty(JiniStarter.JINI_STARTER_PORT)).intValue();

		LogTools.info(logger, "Got jiniStarterPort: " + jiniStarterPort);
		shutdownServer();
	}

	/* ------- Meem functionality ------------------------------------- */

	public void commence() {
		LogTools.info(logger, "commencing");
		jiniProcess = startProcess(jiniStarterArguments(), "jiniStarter");
	}

	public void conclude() {
		shutdownServer();
	}

	/* ---------- MeemDefinitionProvider method(s) ----------------------------- */

	public MeemDefinition getMeemDefinition() {
		if (meemDefinition == null) {
			meemDefinition = MeemDefinitionFactory.spi.create().createMeemDefinition(new Class[] { this.getClass() });
		}

		return meemDefinition;
	}

	private Process startProcess(String[] args, String processName) {
		Process process = null;
		Runtime runtime = Runtime.getRuntime();

		try {
			process = runtime.exec(args);
			LogTools.info(logger, "commence() - " + processName + " started");

			if (Common.TRACE_ENABLED && Common.TRACE_JINI_SERVICES) {
				StreamGobbler errorGobbler = new StreamGobbler(process.getErrorStream(), processName);
				errorGobbler.start();
			}
			StreamGobbler inputGobbler = new StreamGobbler(process.getInputStream(), processName);
			inputGobbler.start();
		}
		catch (Exception ex) {
			errorHandlerConduit.thrown(ex);
			lifeCycleControlConduit.vote(meemContext.getWedgeIdentifier(), false);
		}

		return process;
	}

	private String[] jiniStarterArguments() {
		String startClass = org.openmaji.implementation.server.nursery.jini.JiniStarter.class.getName();
		return new String[] { 
				getJavaExectuable(), 
				"-D" + Common.PROPERTY_MAJI_HOME + "=" + System.getProperty(Common.PROPERTY_MAJI_HOME), 
				"-D" + PropertiesLoader.PROPERTY_KEY + "=" + System.getProperty(PropertiesLoader.PROPERTY_KEY),
				"-D" + MeemSpace.PROPERTY_MEEMSPACE_IDENTIFIER + "=" + System.getProperty(MeemSpace.PROPERTY_MEEMSPACE_IDENTIFIER), 
				"-Djava.security.policy=" + System.getProperty("java.security.policy"), 
				"-classpath", 
				getClassPath(),
				startClass 
			};
	}

	private String getClassPath() {
		String classPathProperty = System.getProperty(JiniStarter.JINI_STARTER_CLASSPATH);

		String majitekHome = System.getProperty(Common.PROPERTY_MAJI_HOME);

		StringBuffer classPath = new StringBuffer();

		StringTokenizer tokenizer = new StringTokenizer(classPathProperty, ",");
		while (tokenizer.hasMoreTokens()) {
			classPath.append(majitekHome);
			classPath.append(tokenizer.nextToken());
			classPath.append(System.getProperty("path.separator"));
		}

		return classPath.toString();
	}

	private String getJavaExectuable() {
		return System.getProperty("java.home") + System.getProperty("file.separator") + "bin" + System.getProperty("file.separator") + "java";
	}

	/**
	 * Shutdown existing server.
	 * Use the jiniStarterPort for shutting down the jini services.
	 */
	private void shutdownServer() {
		// shut down any running jini vms
		try {
			// services shutdown by merely connecting to the server
			Socket socket = new Socket("localhost", jiniStarterPort);

			// send shutdown command
			OutputStreamWriter os = new OutputStreamWriter(socket.getOutputStream());
			os.write(JiniStarter.COMMAND_SHUTDOWN + "\n");
			os.flush();
		}
		catch (UnknownHostException e) {
		}
		catch (IOException e) {
		}

		if (jiniProcess != null) {
			jiniProcess.destroy();
			jiniProcess = null;
		}
	}

	/**
	 * Reads lines from a stream and logs them.
	 */
	private static final class StreamGobbler extends Thread {
		private InputStream is;

		StreamGobbler(InputStream is, String type) {
			this.is = is;
		}

		public void run() {
			try {
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				String line = null;
				while ((line = br.readLine()) != null) {
					//if (Common.TRACE_ENABLED) {
						LogTools.trace(logger, Common.getLogLevelVerbose(), line);
					//}
					LogTools.info(logger, "Jini: " + line);
				}
			}
			catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}

}
