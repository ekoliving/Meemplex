package org.meemplex.service.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Category contains a List of named entries.  Entries may be Categories or other Meems.
 * 
 * @author stormboy
 *
 */
@XmlRootElement(name="category")
public class Category extends Identifiable implements Serializable {
	
	private static final long serialVersionUID = 0L;
	
	private List<CategoryEntry> entries = new ArrayList<CategoryEntry>();

	public void addEntry(String name, Identifiable meem) {
		if (name != null) {
			CategoryEntry entry = new CategoryEntry(name, meem);
			removeEntry(name);	// make sure old entry is removed if it exists
			entries.add(entry);
		}
	}

	public void addEntry(CategoryEntry entry) {
		if (entry != null) {
			removeEntry(entry.getName());	// make sure old entry is removed if it exists
			entries.add(entry);
		}
    }
	
	public void removeEntry(String entryName) {
		if (entryName != null) {
			for (CategoryEntry entry : entries) {
				if ( entryName.equals(entry.getName()) ) {
					entries.remove(entry);
					break;
				}
			}
		}
	}

	public CategoryEntry getEntry(String entryName) {
		if (entryName != null) {
			for (CategoryEntry entry : entries) {
				if ( entryName.equals(entry.getName()) ) {
					return entry;
				}
			}
		}
		return null;
	}
	
	public void setEntries(List<CategoryEntry> entries) {
	    this.entries = entries;
    }

	@XmlElement(name="entry")
	public List<CategoryEntry> getEntries() {
	    return entries;
    }
	
}
