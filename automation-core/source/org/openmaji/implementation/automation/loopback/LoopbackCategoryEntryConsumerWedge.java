package org.openmaji.implementation.automation.loopback;

import org.openmaji.meem.Wedge;
import org.openmaji.system.space.CategoryEntry;
import org.openmaji.util.CategoryEntryConsumer;

import org.swzoo.log2.core.LogFactory;
import org.swzoo.log2.core.LogTools;
import org.swzoo.log2.core.Logger;

import org.openmaji.implementation.common.DebugFlag;

public class LoopbackCategoryEntryConsumerWedge implements Wedge{
	
	private static final Logger logger = LogFactory.getLogger();

	public CategoryEntryConsumer entryConsumerControlConduit = new EntryConsumerControlConduit();
	public CategoryEntryConsumer entryConsumerStateConduit = null;
	  
	/* ---------- BinaryControlConduit ----------------------------------------- */

	class EntryConsumerControlConduit implements CategoryEntryConsumer {
	    public synchronized void entry(CategoryEntry entry){
	    	if ( DebugFlag.TRACE ) LogTools.trace(logger,20,"entry() - invoked on CategoryEntryConsumerControlConduit");
	    	entryConsumerStateConduit.entry(entry);
	    }
	}
}
