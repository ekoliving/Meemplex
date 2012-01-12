/*
 * @(#)PasswordFile.java
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

import org.bouncycastle.util.encoders.Base64;
import org.openmaji.implementation.server.Common;


/**
 */
public class PasswordFile
{
    String          fileName;
    boolean         initialised = false;
    private Map     passwords = new HashMap();
    
    
    PasswordFile(
        String  fileName)
    {
        this.fileName = fileName;
    }
    
    private void init()
    {
        String          majitekDirectory = System.getProperty(Common.PROPERTY_MAJI_HOME);
        
        initialised = true;
        
        BufferedReader  rIn = null;
        
        try
        {
            rIn = new BufferedReader(new FileReader(majitekDirectory + fileName));

            String  line;
            
            while ((line = rIn.readLine()) != null)
            {
                StringTokenizer tk = new StringTokenizer(line, ":");
                
                passwords.put(tk.nextToken().trim(), Base64.decode(tk.nextToken().trim()));
            }
            
            rIn.close();
        }
        catch (FileNotFoundException e)
        {
            throw new RuntimeException("can't open password file: " + majitekDirectory + fileName);
        } 
        catch (Exception e)
        {
            throw new RuntimeException("exception parsing password file: " + e);
        }
    }
    
    public byte[] getPassword(
        String  userID)
    {
        if (!initialised)
        {
            init();
        }
            
        return Base64.decode((byte[])passwords.get(userID));
    }
    
    public void setPassword(
        String  userID,
        byte[]  password)
    {
        if (!initialised)
        {
            init();
        }

        passwords.put(userID, password);
        
        save();
    }
    

    /**
     * @param userID
     */
    public void remove(String userID)
    {
        if (!initialised)
        {
            init();
        }

        passwords.remove(userID);
        
        save();
    }
    
    private void save()
    {
        String          majitekDirectory = System.getProperty(Common.PROPERTY_MAJI_HOME);
        
        try
        {
            BufferedWriter rOut = new BufferedWriter(new FileWriter(majitekDirectory + fileName));
            
            Iterator    it = passwords.keySet().iterator();

            while (it.hasNext())
            {
                String      userID = (String)it.next();
                
                rOut.write(userID);
                rOut.write(":");
                rOut.write(new String(Base64.encode((byte[])passwords.get(userID))));
                
                rOut.newLine();
            }
            
            rOut.close();
        }
        catch (IOException e)
        {
            throw new RuntimeException("unable to save group file.", e);
        }
    }
}
