package org.openmaji.implementation.automation.serial;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.TooManyListenersException;

import javax.comm.CommPortIdentifier;
import javax.comm.PortInUseException;
import javax.comm.SerialPort;
import javax.comm.SerialPortEvent;
import javax.comm.SerialPortEventListener;

import org.swzoo.log2.core.LogFactory;
import org.swzoo.log2.core.LogTools;
import org.swzoo.log2.core.Logger;

import org.openmaji.meem.MeemContext;
import org.openmaji.meem.Wedge;
import org.openmaji.meem.wedge.configuration.ConfigurationClient;
import org.openmaji.meem.wedge.configuration.ConfigurationClientAdapter;
import org.openmaji.meem.wedge.configuration.ConfigurationSpecification;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClient;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClientAdapter;
import org.openmaji.meem.wedge.lifecycle.LifeCycleState;
import org.openmaji.meem.wedge.lifecycle.Vote;

import org.openmaji.automation.util.HexHelper;
import org.openmaji.common.StringValue;
import org.openmaji.common.Value;
import org.openmaji.common.Variable;
import org.openmaji.diagnostic.Debug;

/**
 * 

Regarding BenQ Projector
========================

This works:
	speed 9600 baud; line = 0;
	-brkint -imaxbel

all:
	-ignbrk -brkint -ignpar -parmrk -inpck -istrip -inlcr -igncr icrnl ixon -ixoff -iuclc -ixany -imaxbel
	isig icanon iexten echo echoe echok -echonl -noflsh -xcase -tostop -echoprt echoctl echoke  
	-parenb -parodd cs8 hupcl -cstopb cread clocal -crtscts 
	opost -olcuc -ocrnl onlcr -onocr -onlret -ofill -ofdel nl0 cr0 tab0 bs0 vt0 ff0  

diff to standard:
	-brkint -imaxbel


this is what RXTX does:
	speed 9600 baud; line = 0;
	min = 0; time = 0;
	-brkint -icrnl -imaxbel
	-opost -onlcr
	-isig -icanon -iexten -echo -echoe -echok -echoctl -echoke

turns off: translate carriage return to newline (icrnl)
turns off: postprocess output (opost)
turns off: translate newline to carriage return-newline (onlcr)
turns off: enable interrupt, quit, and suspend special characters (isig)
turns off: enable erase, kill, werase, and rprnt special characters (icanon)
turns off: enable non-POSIX special characters (iexten)
turns off: echo input characters (echo)
turns off: echo erase characters as backspace-space-backspace (echoe)
turns off: echo a newline after a kill character (echok)
turns off: echo control characters in hat notation (echoctl/ctlecho)
turns off: kill all line by obeying the echoprt and echoe settings 
           (-) kill all line by obeying the echoctl and echok settings
           (echoke/crtkill)

 */
public class VariableToSerialWedge implements Wedge, Variable {

	private static final Logger logger = LogFactory.getLogger();
	
	public MeemContext meemContext;
	
	
	/* ------------------ outbound facets ----------------- */
	
	public Variable variableOutput;


	/* ---------------------- conduits --------------------- */
	
	/**
	 * 
	 */
	public Variable variableControlConduit = new VariableControlConduit();
    public Debug debugConduit = new MyDebugConduit();

	/**
	 * 
	 */
	public Variable variableStateConduit;
	
	/**
	 * For voting on lifecycle state changes
	 */
	public Vote lifeCycleControlConduit;
	
	/**
	 * For receiving lifecycle state changes.
	 */
	public LifeCycleClient lifeCycleClientConduit = new LifeCycleClientAdapter(this);

	/**
	 * Conduit to allow this wedge to be configured. 
	 */
	public ConfigurationClient configurationClientConduit = new ConfigurationClientAdapter(this);


	/* --------------- persisted properties ---------------- */
	
	public String devString = "/dev/ttyUSB0";

	public transient ConfigurationSpecification deviceStringSpecification  = 
		new ConfigurationSpecification("The device string", String.class, LifeCycleState.READY);
	

	/* ----------------- private members ------------------- */
	
    private CommPortIdentifier portId;
    private SerialPort serialPort;
    private OutputStream outputStream;
    private InputStream inputStream;
    private int debugLevel;
    
    public void setDeviceString(String devString) {
    	this.devString = devString;
    }
    
    public String getDeviceString() {
    	return devString;
    }
    
    /* ---------------- Variable interface ------------------ */
    
	/**
	 * 
	 */
	public void valueChanged(Value value) {
		String command = value.toString();
		writeCommand(command);
	}
	
	
	/* --------------- lifecycle methods ------------------- */
	
	public void commence() {
		portId = getSerialPort(devString);
		
		if (portId == null) {
			LogTools.info(logger, "Could not find port: " + devString);			
			lifeCycleControlConduit.vote(meemContext.getWedgeIdentifier(), false);
			return;
		}
		
		try {
			serialPort = (SerialPort) portId.open("SerialWedge", 2000);
		}
		catch (PortInUseException e) {
			LogTools.info(logger, "Serial port already in use", e);
			lifeCycleControlConduit.vote(meemContext.getWedgeIdentifier(), false);
			return;
		}

//		try {
//			serialPort.setSerialPortParams(
//					9600,
//					SerialPort.DATABITS_8, 
//					SerialPort.STOPBITS_1,
//					SerialPort.PARITY_NONE);
//		}
//		catch (UnsupportedCommOperationException e) {
//			LogTools.info(logger, "Could not set port prameters", e);
//			lifeCycleControlConduit.vote(meemContext.getWedgeIdentifier(), false);
//			return;
//		}
		
		// set up for writing to the port
		try {
			outputStream = serialPort.getOutputStream();
		}
		catch (IOException e) {
			LogTools.info(logger, "Problem getting output stream", e);
			lifeCycleControlConduit.vote(meemContext.getWedgeIdentifier(), false);
			return;
		}

		// set up for reading from the port.
		try {
			inputStream = serialPort.getInputStream();
		}
		catch (IOException e) {
			LogTools.info(logger, "Problem getting input stream", e);
			lifeCycleControlConduit.vote(meemContext.getWedgeIdentifier(), false);
			return;
		}
		
		try {
			serialPort.addEventListener(new SerialPortListener());
		}
		catch (TooManyListenersException e) {
			LogTools.info(logger, "Too many listeners on serial port", e);
			lifeCycleControlConduit.vote(meemContext.getWedgeIdentifier(), false);
			return;
		}
		
		serialPort.notifyOnDataAvailable(true);
	}
	
	public void conclude() {
		if (outputStream != null) {
			try {
				outputStream.close();
			}
			catch (IOException ex) {
			}
			outputStream = null;
		}
		if (inputStream != null) {
			try {
				inputStream.close();
			}
			catch (IOException ex) {
			}
			inputStream = null;
		}
		
		if (serialPort != null) {
			serialPort.close();
			serialPort = null;
		}

	}
	
	
	/* ------------------- private methods ---------------------- */
	
	private CommPortIdentifier getSerialPort(String devString) {
        Enumeration portList = CommPortIdentifier.getPortIdentifiers();

        while (portList.hasMoreElements()) {
            portId = (CommPortIdentifier) portList.nextElement();
            if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                if (portId.getName().equals(devString)) {
                	return portId;
                }
            }
        }
        return null;
	}

    private void writeCommand(String command) {

    	if (outputStream == null) {
    		return;
    	}

		try {
			outputStream.write(command.getBytes());
		}
		catch (IOException e) {
			LogTools.info(logger, "Could not write to port output stream");
			return;
		}
	}
        
    /**
     * 
     */
    private void read() {
    	if (inputStream == null) {
    		return;
    	}

		try {
			byte[] readBuffer = new byte[256];
			while (inputStream.available() > 0) {
				int numBytes = inputStream.read(readBuffer);
                if ( debugLevel > 0 ) {
                  LogTools.info(logger,HexHelper.toHexString(readBuffer,numBytes).toString());
                }
                String str = new String(readBuffer, 0, numBytes);
				Value value = new StringValue(str);
				variableOutput.valueChanged(value);
			}
		}
		catch (IOException e) {
			LogTools.info(logger, "Problem reading from input stream");
		}
	}

    /* ---------------- inner classes ------------------ */

    /**
     * 
     */
    final private class VariableControlConduit implements Variable {
    	public void valueChanged(Value value) {
    		VariableToSerialWedge.this.valueChanged(value);
    	}
    }

	/**
	 * 
	 */
	final private class SerialPortListener implements SerialPortEventListener {
		public void serialEvent(SerialPortEvent event) {
			switch (event.getEventType()) {
				case SerialPortEvent.BI:
				case SerialPortEvent.OE:
				case SerialPortEvent.FE:
				case SerialPortEvent.PE:
				case SerialPortEvent.CD:
				case SerialPortEvent.CTS:
				case SerialPortEvent.DSR:
				case SerialPortEvent.RI:
				case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
					break;
	
				case SerialPortEvent.DATA_AVAILABLE:
					read();
					break;
			}
		}
	}

    private class MyDebugConduit implements Debug {
      public void debugLevelChanged(int level) {
        debugLevel = level;
      }
    }
}
