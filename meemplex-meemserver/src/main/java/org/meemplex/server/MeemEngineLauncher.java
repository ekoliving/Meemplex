package org.meemplex.server;

import org.openmaji.implementation.security.auth.LoginHelper;
import org.openmaji.implementation.server.genesis.LaunchMeemServer;

/**
 * Launches Meemplex
 */
public class MeemEngineLauncher {

	private static MeemEngineLauncher singleton;
	
	/** 
	 * the launcher object 
	 */
	private LaunchMeemServer launcher;
	
	private boolean started = false;
	
	public static MeemEngineLauncher instance() {
		if (singleton == null) {
			singleton = new MeemEngineLauncher();
		}
		return singleton;
	}
	
	/**
	 * Constructor
	 */
	private MeemEngineLauncher() {
	}

	public boolean isStarted() {
		return started;
	}
	
	/**
	 * Launch the MeemPlex engine
	 */
	public void launch() {
		try {
			launcher = new LaunchMeemServer();
			launcher.launch();
			started = true;
		}
		catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void shutdown() {
		try {
			
			Runnable shutdown = new Runnable() {
				public void run() {
					try {
						launcher.shutdown();
					}
					catch (Exception e) {
						e.printStackTrace();
					}
				}
			};

			LoginHelper.doAs(shutdown, "system", "system99");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Main method.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			new MeemEngineLauncher().launch();
		}
		catch (Throwable t) {
			t.printStackTrace();
		}
	}

}
