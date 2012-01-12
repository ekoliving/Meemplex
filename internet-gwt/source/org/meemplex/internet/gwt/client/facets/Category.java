package org.meemplex.internet.gwt.client.facets;

import java.io.Serializable;

public interface Category {
	
	void addEntries(Category.Entry[] entries);

	void removeEntries(String[] names);

	void renameEntry(String oldName, String newName);

	/**
	 * 
	 */
	public class Entry implements Serializable {
		private static final long serialVersionUID = 0L;

		private String name;

		private String meemId;

		public Entry() {
		}

		public Entry(String name, String meemId) {
			setName(name);
			setMeemId(meemId);
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public void setMeemId(String meemId) {
			this.meemId = meemId;
		}

		public String getMeemId() {
			return meemId;
		}
	}
}
