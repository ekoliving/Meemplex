package org.openmaji.implementation.automation.loopback;

import org.openmaji.meem.Wedge;
import org.openmaji.system.space.CategoryEntry;
import org.openmaji.util.CategoryEntryConsumer;



import java.util.logging.Level;
import java.util.logging.Logger;

import org.openmaji.implementation.common.DebugFlag;

public class LoopbackCategoryEntryConsumerWedge implements Wedge{
	
	private static final Logger logger = Logger.getAnonymousLogger();

	public CategoryEntryConsumer entryConsumerControlConduit = new EntryConsumerControlConduit();
	public CategoryEntryConsumer entryConsumerStateConduit = null;
	  
	/* ---------- BinaryControlConduit ----------------------------------------- */

	class EntryConsumerControlConduit implements CategoryEntryConsumer {
	    public synchronized void entry(CategoryEntry entry){
	    	if ( DebugFlag.TRACE ) logger.log(Level.FINE, "entry() - invoked on CategoryEntryConsumerControlConduit");
	    	entryConsumerStateConduit.entry(entry);
	    }
	}
}
