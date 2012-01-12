/*
 * @(#)DoAsMeem.java
 * 
 * Copyright 2004 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */
package org.openmaji.implementation.server.security;

import java.io.Serializable;
import java.security.PrivilegedAction;

import javax.security.auth.Subject;

import org.openmaji.meem.Facet;
import org.openmaji.meem.Meem;
import org.openmaji.meem.MeemPath;
import org.openmaji.meem.definition.DependencyAttribute;
import org.openmaji.meem.definition.LifeTime;
import org.openmaji.meem.wedge.reference.Reference;


/**
 * Basic handler for a proxy wrapping a meem and setting a subject.
 * Ued by the Server gateway for attachng appropriate Subject to invocations on the Meem.
 */
public class DoAsMeem implements Meem, Serializable
{
	private static final long serialVersionUID = 534540102928363464L;
	
    private Meem        meem;
    private Subject     subject;
    
    public DoAsMeem(
        Meem    meem,
        Subject subject)
    {
        this.meem = meem;
        this.subject = subject;
    }
    
    /**
     * 
     */
    public MeemPath getMeemPath() 
    {
        return meem.getMeemPath();
    }

    public void addDependency(
    		final Facet facet, 
    		final DependencyAttribute dependencyAttribute, 
    		final LifeTime lifeTime) 
    {
        if (facet == null)
        {
            throw new IllegalArgumentException("attempt to call addDependency with null facet.");
        }
        
        Subject.doAs(
        		subject, 
        		new PrivilegedAction() {
					public Object run() {
						meem.addDependency(facet, dependencyAttribute, lifeTime);
						return null;
					}
        		}
        	);
    }
    
    public void addDependency(
    		final String facetIdentifier, 
    		final DependencyAttribute dependencyAttribute, 
    		final LifeTime lifeTime) 
    {
        if (facetIdentifier == null)
        {
            throw new IllegalArgumentException("attempt to call addDependency with null facet identifier.");
        }
        
        Subject.doAs(
        		subject, 
        		new PrivilegedAction() {
					public Object run() {
						meem.addDependency(facetIdentifier, dependencyAttribute, lifeTime);
						return null;
					}
        		}
        	);
    }

    public void removeDependency(final DependencyAttribute dependencyAttribute) {
        if (dependencyAttribute == null)
        {
            throw new IllegalArgumentException("attempt to call removeDependency with null dependency attribute.");
        }
        
        Subject.doAs(
        		subject, 
        		new PrivilegedAction() {
					public Object run() {
						meem.removeDependency(dependencyAttribute);
						return null;
					}
        		}
        	);
    }
    
    public void updateDependency(final DependencyAttribute dependencyAttribute) {
        if (dependencyAttribute == null)
        {
            throw new IllegalArgumentException("attempt to call updateDependency with null dependency attribute.");
        }
        
        Subject.doAs(
        		subject, 
        		new PrivilegedAction() {
					public Object run() {
						meem.removeDependency(dependencyAttribute);
						return null;
					}
        		}
        	);
    }
   
    public void addOutboundReference(
        final Reference reference,
        final boolean automaticRemove) 
    {
        if (reference == null)
        {
            throw new IllegalArgumentException("attempt to call addOutboundReference with null reference.");
        }
        
        Subject.doAs(subject, new PrivilegedAction()
                              {
                                public Object run() 
                                {
                                    meem.addOutboundReference(reference, automaticRemove);
                                    
                                    return null;
                                }
                              });
    }

    public void removeOutboundReference(
        final Reference reference) 
    {
        if (reference == null)
        {
            throw new IllegalArgumentException("attempt to call removeOutboundReference with null reference.");
        }
        
        Subject.doAs(subject, new PrivilegedAction()
                  {
                    public Object run() 
                    {
                        meem.removeOutboundReference(reference);
                        
                        return null;
                    }
                  });
        
    }
    
    public boolean equals(
        final Object    o)
    {
        return ((Boolean)Subject.doAs(subject, new PrivilegedAction()
                  {
                    public Object run() 
                    {
                        return Boolean.valueOf(meem.equals(o));
                    }
                  })).booleanValue();
    }
    
    public int hashCode()
    {
        return ((Integer)Subject.doAs(subject, new PrivilegedAction()
                  {
                    public Object run() 
                    {
                        return new Integer(meem.hashCode());
                    }
                  })).intValue();
    }    
}