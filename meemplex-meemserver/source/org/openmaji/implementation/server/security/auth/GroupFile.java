/*
 * @(#)GroupFile.java
 * 
 * Copyright 2004 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */
package org.openmaji.implementation.server.security.auth;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.security.auth.Subject;

import org.openmaji.implementation.server.Common;
import org.openmaji.system.meem.hook.security.GroupPrincipal;
import org.openmaji.system.meem.hook.security.Principals;



/**
 */
public class GroupFile
{
    Map     groups      = new HashMap();
    Map     principals  = new HashMap();
    boolean initialised = false;
    String  groupFileName;
    
    public GroupFile(
        String groupFileName)
    {
        this.groupFileName = groupFileName;
    }
    
    private void init()
    {
        String          majitekDirectory = System.getProperty(Common.PROPERTY_MAJI_HOME);
        
        initialised = true;
        
        principals.put("system", Principals.SYSTEM);
        principals.put("other", Principals.OTHER);
        
        BufferedReader  rIn = null;
        
        try
        {
            rIn = new BufferedReader(new FileReader(majitekDirectory + groupFileName));

            String  line;
            
            while ((line = rIn.readLine()) != null)
            {
                StringTokenizer tk = new StringTokenizer(line, ":,");
                
                Set members = new HashSet();
                String  groupName = tk.nextToken().trim();
                
                groups.put(groupName, members);
                if (!principals.containsKey(groupName))
                {
                    principals.put(groupName, new GroupPrincipal(groupName));
                }
                
                while (tk.hasMoreTokens())
                {
                    String  id = tk.nextToken().trim();
                    members.add(id);
                    members.add(CoreAdminHelper.getPrincipal(id));
                }
            }
            
            rIn.close();
        }
        catch (FileNotFoundException e)
        {
            throw new RuntimeException("can't open groupfile: " + majitekDirectory + groupFileName);
        } 
        catch (Exception e)
        {
            throw new RuntimeException("exception parsing groupfile: " + e);
        }
    }
    
    /**
     * @param subject
     */
    public synchronized void addPrincipals(String id, Subject subject)
    {
        if (!initialised)
        {
            init();
        }
        
        Iterator    it = groups.keySet().iterator();
        while (it.hasNext())
        {
            String  groupName = (String)it.next();
            Set     members = (Set)groups.get(groupName);

            if (members.contains(id))
            {
                Principal   p = (Principal)principals.get(groupName);
                subject.getPrincipals().add(p);
                subject.getPublicCredentials().add(MeemCoreRootAuthority.getAuthenticator(p));
            }
        }
    }

    /**
     * Return the Group Principals for a particular user.
     * 
     * @param id
     */
    public synchronized Principal[] getGroups(String id)
    {
        if (!initialised)
        {
            init();
        }
     
        ArrayList groupPrincipals = new ArrayList();
        
        Iterator    it = groups.keySet().iterator();
        while (it.hasNext())
        {
            String  groupName = (String)it.next();
            Set     members = (Set)groups.get(groupName);

            if (members.contains(id))
            {
                Principal   p = (Principal)principals.get(groupName);
                groupPrincipals.add(p);
            }
        }
        
        Principal[] result = new Principal[groupPrincipals.size()];
        for (int i=0; i< groupPrincipals.size(); i++) {
        	result[i] = (Principal) groupPrincipals.get(i);
        }

        return result;
    }

    public boolean isGroup(
        String  groupID)
    {
        if (!initialised)
        {
            init();
        }
        
        return groups.containsKey(groupID);
    }
    
    public synchronized String addGroup(
        String groupName) 
    {
        if (!initialised)
        {
            init();
        }
        
        Set members = (Set)groups.get(groupName);
        
        if (members == null)
        {
            members = new HashSet();
            groups.put(groupName, members);
            principals.put(groupName, new GroupPrincipal(groupName));
        }
        else
        {
            return "Group " + groupName + " already present.";
        }
        
        this.save();
        
        return null;
    }
    
    public synchronized String addMember(
        String groupName, 
        String userID) 
    {
        if (!initialised)
        {
            init();
        }
        
        Set members = (Set)groups.get(groupName);
        
        if (members == null)
        {
            return "Group " + groupName + " not present.";
        }
        
        try
        {
            members.add(userID);
            members.add(CoreAdminHelper.getPrincipal(userID));
        }
        catch (Exception e)
        {
            return "Exception: " + e.toString();
        }
        
        this.save();
        
        return null;
    }
    
    public synchronized void save()
    {
        String          majitekDirectory = System.getProperty(Common.PROPERTY_MAJI_HOME);
        
        try
        {
            BufferedWriter rOut = new BufferedWriter(new FileWriter(majitekDirectory + groupFileName));
            
            Iterator    it = groups.keySet().iterator();

            while (it.hasNext())
            {
                String      groupName = (String)it.next();
                
                rOut.write(groupName);
                rOut.write(":");
                
                Iterator    mIt = ((Set)groups.get(groupName)).iterator();
                boolean     first = true;
                
                while (mIt.hasNext())
                {
                    Object  o = mIt.next();
                    
                    if (o instanceof String)
                    {
                        if (first)
                        {
                            first = false;
                        }
                        else
                        {
                            rOut.write(",");
                        }
                        rOut.write(o.toString());
                    }
                }
                
                rOut.newLine();
            }
            
            rOut.close();
        }
        catch (IOException e)
        {
            throw new RuntimeException("unable to save group file.", e);
        }
    }

    /**
     * Remove any occurance of userID from any group in the table.
     * 
     * @param userID
     */
    public synchronized String removeMember(
        String userID)
    {
        if (!initialised)
        {
            init();
        }
     
        boolean found = false;
        
        Iterator groupIt = groups.values().iterator();
        while (groupIt.hasNext())
        {
            boolean  hasMember = ((Set)groupIt.next()).remove(userID);
            
            if (hasMember)
            {
                found = true;
            }
        }
        
        if (!found)
        {
            return "Member " + userID + " not in found in any groups.";
        }
        
        this.save();
        
        return null;
    }
    
    /**
     * Remove userID from group groupName.
     * 
     * @param groupName
     * @param userID
     */
    public synchronized String removeMember(
        String groupName, 
        String userID)
    {
        if (!initialised)
        {
            init();
        }
     
        Set members = (Set)groups.get(groupName);
        
        if (members == null)
        {
            return "Group " + groupName + " does not exist.";
        }
        
        boolean  hasMember = members.remove(userID);
        
        if (!hasMember)
        {
            return "Member " + userID + " not in group " + groupName + ".";
        }
        
        this.save();
        
        return null;
    }
    
    

    /**
     * @param groupName
     */
    public String removeGroup(String groupName)
    {
        Object  group = groups.remove(groupName);
        
        if (group == null)
        {
            return "group named " + groupName + " not found.";
        }
        
        this.save();
        
        return null;
    }
    
    /**
     * Return a list of group names.
     * 
     * @return a list of group names.
     */
    public List getGroups()
    {
        return new ArrayList(groups.keySet());
    }
    
    /**
     * Return a copy of the current groups map, including the members.
     * 
     * @return a copy of the current group map.
     */
    public Map getGroupsAndMembers()
    {
        Iterator    it = groups.keySet().iterator();
        Map         groupsCopy = new HashMap();
        
        while (it.hasNext())
        {
            Object  key = it.next();
            
            HashSet     groupSet = new HashSet();
            Iterator    mIt = ((Set)groups.get(key)).iterator();
            while (mIt.hasNext())
            {
                Object  member = mIt.next();
                
                if (member instanceof String)
                {
                    groupSet.add(member);
                }
            }
            
            groupsCopy.put(key, groupSet);
        }
        
        return groupsCopy;
    }
}

