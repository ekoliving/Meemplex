/*
 * @(#)CategoryEntry.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
package org.openmaji.system.space;

import java.io.Serializable;

import org.openmaji.meem.Meem;
import org.openmaji.meem.MeemPath;
import org.openmaji.utility.uid.UID;


/**
 * Category entries are used to represent the connection between a meem and the
 * entry name they are stored under.
 * 
 * @author mg
 */
public class CategoryEntry implements Serializable, Cloneable {
	private static final long serialVersionUID = 7943093657599948438L;
	
	private final String name;
	private final MeemPath meemPath;	
	private final UID uid;
	private transient Meem meem;

    /**
     * Create a category entry with the given name containing the passed in meem.
     * 
     * @param name the name to be associated with the entry.
     * @param meem the meem to be associated with the entry.
     */
	public CategoryEntry(String name, Meem meem) {
		this.uid = UID.spi.create();
		this.meemPath = meem.getMeemPath();
		this.name = name;
		this.meem = null;
	}
	
	/**
	 * @deprecated use Meem constructor.
	 * @param uid
	 * @param name
	 * @param meemPath
	 */
	private CategoryEntry(UID uid, String name, MeemPath meemPath, Meem meem) {
		this.uid = uid;
		this.meemPath = meemPath;
		this.name = name;
		this.meem = meem;
	}

	/**
	 * Return the name associated with this category entry.
	 * 
	 * @return the name for this entry.
	 */
    public String getName()
    {
    	return name;
    }
    
    /**
     * Return the meem contained in this category entry.
     * 
     * @return the meem stored in this category entry.
     */
	public Meem getMeem()
	{
		if (meem == null)
		{
			meem = Meem.spi.get(meemPath);
		}

		return meem;
	}
	
	/**
	 * Return a category entry which is a clone of this one, but with a new name.
	 * 
	 * @param newName the new name for the entry.
	 */
	public CategoryEntry rename(String newName)
	{
		return new CategoryEntry(this.uid, newName, this.meemPath, this.meem);
	}
	
	/**
	 * Return a category entry which is a clone of this one, but with a different meem.
	 * 
	 * @param newMeem the new entry.
	 * @return the CategoryEntry with the new meem in it.
	 */
	public CategoryEntry changeMeem(Meem newMeem)
	{
		return new CategoryEntry(this.uid, this.name, newMeem.getMeemPath(), newMeem);
	}

  public synchronized String toString() {
    return(
       "[name="     + name     +
      ", meemPath=" + meemPath +
			", uid=" 			+ uid +
      "]"
    );
  }

	public boolean equals(Object o) {
		if (o == this) return true;
		if (o instanceof CategoryEntry) {
			 CategoryEntry categoryEntry = (CategoryEntry)o;
			 return categoryEntry.uid.equals(this.uid);
		} 
		return false;
	}

	public int hashCode() {
		return uid.hashCode();
	}
}
