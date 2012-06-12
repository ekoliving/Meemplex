/*
 * @(#)LatteBackEnd.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.tool.eclipse.util.log;

import java.util.Properties;

/**
 * This is basically a ripoff of the Brew class in the ShortBlack topology with the eclipse terminator added
 * @author  mg
 * @version 1.0
 */
public class LatteBackEnd {
	
}

//public class LatteBackEnd implements BackEnd {
//	private static final long serialVersionUID = 6424227717462161145L;
//
//	/** Current configuration. */
//	Properties config = getDefaultConfiguration();
//
//	public final LogNode entryNode;
//	public final LogNode filterNode;
//	public final LevelFilter levelFilter;
//	public final TraceLevelFilter traceLevelFilter;
//	public final LogNode decoratorNode;
//	public final TimestampDecorator timestampDecorator;
//	public final ThreadDecorator threadDecorator;
//	public final EnvironmentDecorator environmentDecorator;
//	public final SequenceDecorator sequenceDecorator;
//	public final ThrowableWrapper throwableWrapper;
//	public final HeartBeatNode heartBeatNode;
//	public final HandoffNode handoffNode;
//	public final LogNode consoleNode;
//	public final LogNode fileNode;
//	public final LogNode udpNode;
//	public final LogNode eclipseNode;
//	public final ConsoleTerminator consoleTerminator;
//	public final FileTerminator fileTerminator;
//	public final UDPTerminator udpTerminator;
//	public final EclipseTerminator eclipseTerminator;
//
//	static final String ENABLED = "enabled";
//	static final String FORMAT = "format";
//	static final String HEARTBEAT_BEATING = "heartbeat.beat";
//	static final String HEARTBEAT_INTERVAL = "heartbeat.interval";
//	static final String HEARTBEAT_TEXT = "heartbeat.text";
//	static final String CONSOLE_ENABLED = "console.enabled";
//	static final String FILE_ENABLED = "file.enabled";
//	static final String FILE_NAME = "file.name";
//	static final String FILE_APPEND = "file.append";
//	static final String DECORATE_THREAD = "decorate.thread";
//	static final String DECORATE_TIMESTAMP = "decorate.timestamp";
//	static final String DECORATE_JVM = "decorate.jvmname";
//	static final String DECORATE_IP = "decorate.inetaddress";
//	static final String DECORATE_HOST = "decorate.host";
//	static final String DECORATE_SEQUENCE = "decorate.sequence";
//	static final String DECORATE_SEQUENCE_START = "decorate.sequence.start";
//	static final String DECORATE_SEQUENCE_INCREMENT = "decorate.sequence.increment";
//	static final String DECORATE_THROWABLE = "decorate.throwable";
//	static final String FILTER_NORMAL_LEVEL = "filter.normal.level";
//	static final String FILTER_TRACE_LEVEL = "filter.trace.level";
//	static final String UDP_ENABLED = "udp.enabled";
//	static final String UDP_PORT = "udp.port";
//	static final String UDP_ADDRESS = "udp.address";
//
//	/** Java VM name setting. */
//	transient String jvmName = null;
//
//	/**
//	 * The default filename to use if the "file.name" and 
//	 * "java.vm.instance.name" properties are both  not set, but 
//	 * the "file.enabled" property is.
//	 */
//	protected String fileName = "latte.out";
//
//	public LatteBackEnd(ReentrantCounterSource counterSource) {
//		LogNode en = null;
//		LogNode lfn = null;
//		LevelFilter lf = null;
//		TraceLevelFilter tlf = null;
//		LogNode dn = null;
//		TimestampDecorator tsd = null;
//		ThreadDecorator td = null;
//		EnvironmentDecorator ed = null;
//		SequenceDecorator sd = null;
//		ThrowableWrapper tw = null;
//		HeartBeatNode hbn = null;
//		HandoffNode hn = null;
//		LogNode cn = null;
//		LogNode fn = null;
//		LogNode un = null;
//		LogNode ecln = null;
//		ConsoleTerminator ct = null;
//		FileTerminator ft = null;
//		UDPTerminator ut = null;
//		EclipseTerminator et = null;
//
//		// Create entry node.
//		en = new LogNode();
//
//		// Create heart beat node.
//		hbn = new HeartBeatNode();
//
//		// Create and configure the filter node.
//		//
//		{
//			lfn = new LogNode();
//
//			// Build filters.
//			FilterGroup filters = new FilterGroup(FilterGroup.AND);
//			{
//				// Level filter.
//				lf = new LevelFilter();
//
//				// Trace Level filter.
//				tlf = new TraceLevelFilter();
//
//				// Add the filters to the group.
//				filters.add(lf);
//				filters.add(tlf);
//			}
//
//			// Set the filters for this log node.
//			lfn.setProcessor(new FilterProcessor(filters));
//		}
//
//		// Create and prepare decorator node.
//		//
//		{
//			dn = new LogNode();
//
//			// Build decorators.
//			DecoratorGroup decorators = new DecoratorGroup();
//			{
//				// Timestamp.
//				tsd = new TimestampDecorator();
//
//				// Thread.
//				td = new ThreadDecorator();
//
//				// Environment.
//				ed = new EnvironmentDecorator();
//
//				// Sequence.
//				sd = new SequenceDecorator();
//
//				// Sequence.
//				tw = new ThrowableWrapper();
//
//				// Add the decorators to the container.
//				decorators.add(tsd);
//				decorators.add(td);
//				decorators.add(ed);
//				decorators.add(sd);
//				decorators.add(tw);
//			}
//
//			// Set the decorators for this log node.
//			dn.setProcessor(new DecoratorProcessor(decorators));
//		}
//
//		// Create the handoff node.
//		//
//		hn = new HandoffNode(counterSource);
//
//		// Create the console node.
//		cn = new LogNode();
//
//		// Create the file node.
//		fn = new LogNode();
//
//		// Create the UDP node.
//		un = new LogNode();
//		
//		//	Create the Eclipse node.
//	  ecln = new LogNode();
//
//		// Create the console terminator.
//		ct = new ConsoleTerminator(); // The default behaviour is fine.
//
//		// Create the file terminator.
//		ft = new FileTerminator(); // The default behaviour is fine.
//
//		// Create the UDP terminator.
//		ut = new UDPTerminator(); // The default behaviour is fine.
//
//		//	Get the Eclipse terminator from the Maji Plugin
//		et = EclipseLog.getInstance().getTerminator(); // The default behaviour is fine.
//
//		// Wire things up.
//		//
//		hbn.addSink(en);
//		en.addSink(lfn);
//		lfn.addSink(dn);
//		dn.addSink(hn);
//		hn.addSink(cn);
//		hn.addSink(fn);
//		hn.addSink(un);
//		hn.addSink(ecln);
//		cn.addSink(ct);
//		fn.addSink(ft);
//		un.addSink(ut);
//		ecln.addSink(et);
//
//		// Initialise finals...
//		entryNode = en;
//		filterNode = lfn;
//		levelFilter = lf;
//		traceLevelFilter = tlf;
//		decoratorNode = dn;
//		timestampDecorator = tsd;
//		threadDecorator = td;
//		environmentDecorator = ed;
//		sequenceDecorator = sd;
//		throwableWrapper = tw;
//		heartBeatNode = hbn;
//		handoffNode = hn;
//		consoleNode = cn;
//		fileNode = fn;
//		udpNode = un;
//		eclipseNode = ecln;
//		consoleTerminator = ct;
//		fileTerminator = ft;
//		udpTerminator = ut;
//		eclipseTerminator = et;
//	}
//
//	/* (non-Javadoc)
//	 * @see org.swzoo.log2.topology.common.BackEnd#getEntryNode()
//	 */
//	public LogNode getEntryNode() {
//		return entryNode;
//	}
//
//	/* (non-Javadoc)
//	 * @see org.swzoo.log2.topology.common.BackEnd#getTerminatorNode()
//	 */
//	public LogNode getTerminatorNode() {
//		return handoffNode;
//	}
//
//	/* (non-Javadoc)
//	 * @see org.swzoo.log2.core.Flushable#flush()
//	 */
//	public void flush() {
//		handoffNode.flush();
//	}
//
//	/* (non-Javadoc)
//	 * @see org.swzoo.log2.core.PropertiesConfigurable#getConfiguration()
//	 */
//	public Properties getConfiguration() {
//		return PropertiesUtil.mergeProperties(config, null);
//	}
//
//	/* (non-Javadoc)
//	 * @see org.swzoo.log2.core.PropertiesConfigurable#getDefaultConfiguration()
//	 */
//	public Properties getDefaultConfiguration() {
//		return PropertiesUtil.mergeProperties(PropertiesUtil.prepend(getInitialDefaults(), Latte.PREFIX_LATTE), null);
//	}
//
//	/* (non-Javadoc)
//	 * @see org.swzoo.log2.core.PropertiesConfigurable#setConfiguration(java.util.Properties, java.util.Properties)
//	 */
//	public void setConfiguration(Properties props, Properties overrides) {
//
//		// Merge, with the existing configuration as a backing.
//		props = PropertiesUtil.mergeProperties(props, config);
//
//		// Now actually override with overrides.
//		props = PropertiesUtil.overrideProperties(props, overrides);
//
//		// "java.vm.instance.name" is a special case.  We don't actually
//		// consider this one of our properties.
//		//
//		// First see if the "java.vm.instance.name" is set.
//		// We do this prior to deriving our local configuration context
//		// since "java.vm.instance.name" is a global property.
//		//
//		jvmName = props.getProperty("java.vm.instance.name");
//		if (overrides != null && jvmName == null)
//			jvmName = overrides.getProperty("java.vm.instance.name");
//
//		// Set our config
//		config = props;
//
//		// Now localise the properties.
//		Properties local =
//			PropertiesUtil.removePrefixFromKeys(
//				PropertiesUtil.extractPropertiesWithPrefix(props, Latte.PREFIX_LATTE),
//				Latte.PREFIX_LATTE);
//
//		// Configure this object.
//		internalConfigure(local);
//
//	}
//
//	/**
//	 * Perform internal configuration based on "localised" properties.
//	 *
//	 * @param local a localised properties reference.
//	 */
//	void internalConfigure(Properties local) {
//		boolean on = PropertiesUtil.booleanValue(ENABLED, local);
//		boolean heartBeatOn = PropertiesUtil.booleanValue(HEARTBEAT_BEATING, local);
//		boolean consoleOn = PropertiesUtil.booleanValue(CONSOLE_ENABLED, local);
//		boolean fileOn = PropertiesUtil.booleanValue(FILE_ENABLED, local);
//		boolean udpOn = PropertiesUtil.booleanValue(UDP_ENABLED, local);
//		boolean fileAppend = PropertiesUtil.booleanValue(FILE_APPEND, local);
//		boolean decorateThread = PropertiesUtil.booleanValue(DECORATE_THREAD, local);
//		boolean decorateTimestamp = PropertiesUtil.booleanValue(DECORATE_TIMESTAMP, local);
//		boolean decorateJVM = PropertiesUtil.booleanValue(DECORATE_JVM, local);
//		boolean decorateIP = PropertiesUtil.booleanValue(DECORATE_IP, local);
//		boolean decorateHost = PropertiesUtil.booleanValue(DECORATE_HOST, local);
//		boolean decorateSequence = PropertiesUtil.booleanValue(DECORATE_SEQUENCE, local);
//		boolean decorateThrowable = PropertiesUtil.booleanValue(DECORATE_THROWABLE, local);
//		int normalLevel = PropertiesUtil.intValue(FILTER_NORMAL_LEVEL, local);
//		int traceLevel = PropertiesUtil.intValue(FILTER_TRACE_LEVEL, local);
//
//		// The format.
//		String format = local.getProperty(FORMAT);
//		LogFormatter formatter = (format == null) ? null : TopologyUtil.getFreeFormatter(format);
//
//		// Entry node.
//		entryNode.setEnabled(on);
//
//		//
//		// Heartbeat node.
//		//
//
//		// Set the "heartbeat" text.
//		//
//		String text = heartBeatNode.getText();
//		String hbText = local.getProperty(HEARTBEAT_TEXT);
//		if (hbText != null && !hbText.equals(text))
//			heartBeatNode.setText(hbText);
//
//		// Set the "heartbeat" interval.
//		int interval = heartBeatNode.getInterval();
//		int hbInterval = PropertiesUtil.intValue(HEARTBEAT_INTERVAL, HeartBeatNode.DEFAULT_INTERVAL, local);
//		if (hbInterval != interval)
//			heartBeatNode.setInterval(hbInterval);
//
//		// Decorators.
//		threadDecorator.setEnabled(decorateThread);
//		timestampDecorator.setEnabled(decorateTimestamp);
//		environmentDecorator.setJVMDecorate(decorateJVM);
//		environmentDecorator.setIPDecorate(decorateIP);
//		environmentDecorator.setHostDecorate(decorateHost);
//		sequenceDecorator.setEnabled(decorateSequence);
//		throwableWrapper.setEnabled(decorateThrowable);
//
//		// Filters.
//		levelFilter.setLevel(normalLevel);
//		traceLevelFilter.setLevel(traceLevel);
//
//		// Do a bit more work with the sequencer.
//		long start = sequenceDecorator.getSequence();
//		int increment = sequenceDecorator.getIncrement();
//		long sbStart = PropertiesUtil.longValue(DECORATE_SEQUENCE_START, SequenceDecorator.DEFAULT_START, local);
//		int sbIncrement = PropertiesUtil.intValue(DECORATE_SEQUENCE_INCREMENT, SequenceDecorator.DEFAULT_INCREMENT, local);
//		if (sbStart != start)
//			sequenceDecorator.setSequence(sbStart);
//		if (sbIncrement != increment)
//			sequenceDecorator.setSequence(sbIncrement);
//
//		// Console.
//		consoleNode.setEnabled(consoleOn);
//
//		// File.
//		fileNode.setEnabled(fileOn);
//		fileTerminator.setAppend(fileAppend);
//		String filename = local.getProperty(FILE_NAME);
//		if (filename == null) {
//			if (jvmName != null)
//				filename = jvmName + ".log";
//			else
//				filename = fileName;
//		}
//		fileTerminator.setFile(filename);
//
//		// UDP.
//		udpNode.setEnabled(udpOn);
//		int port = PropertiesUtil.intValue(UDP_PORT, UDPTerminator.DEFAULT_PORT, local);
//		String address = local.getProperty(UDP_ADDRESS);
//		udpTerminator.setDetails(port, address);
//
//		// Format.
//		if (formatter != null) {
//			consoleTerminator.setFormatter(formatter);
//			fileTerminator.setFormatter(formatter);
//
//			// UDP terminator doesn't format.
//		}
//
//		// And, this is deliberately left till last:
//
//		// Should the "heartbeat" be started or stopped?
//		if (heartBeatOn)
//			heartBeatNode.start();
//		else
//			heartBeatNode.stop();
//	}
//
//	protected Properties getInitialDefaults() {
//
//		Properties defaults = new Properties();
//
//		// Define topology defaults.
//		defaults.setProperty("enabled", "true");
//		defaults.setProperty("format", TopologyUtil.DEFAULT_FREE_FORMAT);
//		defaults.setProperty("heartbeat.beat", "false");
//		defaults.setProperty("heartbeat.interval", "60000");
//		defaults.setProperty("heartbeat.text", "HeartBeat");
//		defaults.setProperty("console.enabled", "true");
//		defaults.setProperty("file.enabled", "true");
//		defaults.setProperty("file.append", "false");
//		defaults.setProperty("decorate.thread", "true");
//		defaults.setProperty("decorate.timestamp", "true");
//		defaults.setProperty("decorate.jvmname", "true");
//		defaults.setProperty("decorate.inetaddress", "true");
//		defaults.setProperty("decorate.host", "true");
//		defaults.setProperty("decorate.sequence", "true");
//		defaults.setProperty("decorate.sequence.start", "0");
//		defaults.setProperty("decorate.sequence.increment", "1");
//		defaults.setProperty("decorate.throwable", "true");
//		defaults.setProperty("filter.normal.level", Integer.toString(LogLevel.INFO));
//		defaults.setProperty("filter.trace.level", Integer.toString(-1));
//		defaults.setProperty("udp.enabled", "false");
//		defaults.setProperty("udp.port", Integer.toString(LogConstants.DEFAULT_LOG_UDP_PORT));
//		defaults.setProperty("udp.dest.address", LogConstants.DEFAULT_LOG_MULTICAST_ADDRESS);
//
//		return defaults;
//	}
//
//}
