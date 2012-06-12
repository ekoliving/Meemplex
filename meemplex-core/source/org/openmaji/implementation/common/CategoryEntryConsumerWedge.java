package org.openmaji.implementation.common;



import java.util.logging.Level;
import java.util.logging.Logger;
import org.openmaji.meem.Wedge;
import org.openmaji.meem.filter.Filter;
import org.openmaji.system.meem.wedge.reference.ContentProvider;
import org.openmaji.system.space.CategoryEntry;
import org.openmaji.util.CategoryEntryConsumer;

public class CategoryEntryConsumerWedge implements Wedge, CategoryEntryConsumer{
	private static final Logger logger = Logger.getAnonymousLogger();
	/**
	 * CategoryEntryConsumer Outbound
	 */
	public CategoryEntryConsumer entryConsumerClient;
	public final ContentProvider entryConsumerClientProvider=new ContentProvider(){
		public synchronized void sendContent(Object target, Filter filter){
			if(DebugFlag.TRACE)logger.log(Level.FINE, "sendContent() - invoked");
			if(entry!=null){
				((CategoryEntryConsumer)target).entry(entry);
			};
		}
	};
	
	/**
	 * CategoryEntry maintained by this Wedge and persisted by the Maji framework
	 */
	public CategoryEntry entry = null;

	/**
	 * The conduit through which other Wedges in the Meem are notified of a
	 * request to change categoryentry.  
	 */
	public CategoryEntryConsumer entryConsumerControlConduit = null;

	/**
	 * The conduit through which categoryentry changes are received other Wedges in the Meem. 
	 */
	public CategoryEntryConsumer entryConsumerStateConduit = new EntryConsumerStateConduit();

	public synchronized void entry(CategoryEntry entry) {	    	      
		if ( DebugFlag.TRACE ) logger.log(Level.FINE, "entry() - invoked on in-bound facet");
	    entryConsumerControlConduit.entry(entry);
	    
	}

	class EntryConsumerStateConduit implements CategoryEntryConsumer {

	    public synchronized void entry(CategoryEntry newEntry) {
	      if ( DebugFlag.TRACE ) logger.log(Level.FINE, "entry() - invoked on CategoryEntryConsumerStateConduit");
	      entry = newEntry;
	      entryConsumerClient.entry(entry);
	    }
	}
}
