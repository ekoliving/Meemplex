/*
 * @(#)CategoryUtilityImpl.java
 *
 * Copyright 2004 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
package org.openmaji.implementation.server.utility;

import java.util.Map;
import java.util.StringTokenizer;

import org.openmaji.meem.Meem;
import org.openmaji.meem.MeemPath;
import org.openmaji.meem.wedge.configuration.ConfigurationHandler;
import org.openmaji.meem.wedge.configuration.ConfigurationIdentifier;
import org.openmaji.meem.wedge.lifecycle.LifeCycleState;
import org.openmaji.server.helper.CategoryHelper;
import org.openmaji.server.helper.HyperSpaceHelper;
import org.openmaji.server.helper.LifeCycleManagerHelper;
import org.openmaji.server.helper.ReferenceHelper;
import org.openmaji.system.space.Category;
import org.openmaji.system.space.CategoryEntry;
import org.openmaji.system.utility.CategoryUtility;


/**
 *  SPI implementation for CategoryUtility
 */
public class CategoryUtilityImpl implements CategoryUtility
{
    /* (non-Javadoc)
     * @see org.openmaji.system.utility.CategoryUtility#getCategory(org.openmaji.meem.Meem)
     */
    public Category getCategory(Meem categoryMeem)
    {
        return (Category)ReferenceHelper.getTarget(categoryMeem, "category", Category.class);
    }
    
    /* (non-Javadoc)
     * @see org.openmaji.system.utility.CategoryUtility#getCategory(org.openmaji.meem.Meem, org.openmaji.meem.MeemPath)
     */
    public Category getCategory(final Meem rootMeem, final MeemPath categoryPath)
    {
        Meem        root = rootMeem;
        Category    cat = getCategory(rootMeem);
        
        StringTokenizer tok = new StringTokenizer(categoryPath.getLocation(), "/");
        while (tok.hasMoreTokens())
        {
            String  name = tok.nextToken();
            
            CategoryEntry entry = getCategoryEntry(root, name);
            if (entry != null)
            {
                root = entry.getMeem();
            }
            else
            {
                cat = getCategory(root);
                Meem newCategoryMeem = LifeCycleManagerHelper.doCreateMeem(
                        HyperSpaceHelper.getCategoryDefinition(), root, LifeCycleState.READY);
                cat.addEntry(name, newCategoryMeem);
                root = newCategoryMeem;
                ConfigurationHandler ch = (ConfigurationHandler)ReferenceHelper.getTarget(
                                                       newCategoryMeem, "configurationHandler", ConfigurationHandler.class);
                ConfigurationIdentifier ci = new ConfigurationIdentifier("MeemSystemWedge", "meemIdentifier");
                ch.valueChanged(ci, name);
            }
        }

        return getCategory(root);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.openmaji.system.utility.CategoryUtility#getCategoryEntries(org.openmaji.meem.Meem)
     */
    public Map<String, CategoryEntry> getCategoryEntries(Meem categoryMeem)
    {
        return CategoryHelper.getCategoryEntries(categoryMeem);
    }

    /* (non-Javadoc)
     * @see org.openmaji.system.utility.CategoryUtility#getCategoryEntry(org.openmaji.meem.Meem, java.lang.String)
     */
    public CategoryEntry getCategoryEntry(Meem categoryMeem, String entryName)
    {
        return CategoryHelper.getCategoryEntry(categoryMeem, entryName);
    }
}
