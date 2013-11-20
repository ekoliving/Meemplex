/*
 * @(#)IncomingSecurityHook.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - Create a subject for the Meem.
 * - Get Meem's subject.
 * - Create keystore Meem for storing keys and certificates.
 */

package org.openmaji.implementation.server.meem.hook.security;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.security.AccessControlException;
import java.security.Principal;
import java.security.PrivilegedExceptionAction;
import java.util.*;

import javax.security.auth.Subject;
import javax.security.auth.x500.X500Principal;

import org.openmaji.implementation.server.meem.invocation.ReflectionInvocation;
import org.openmaji.implementation.server.security.auth.MeemCoreRootAuthority;
import org.openmaji.meem.Wedge;
import org.openmaji.meem.definition.FacetAttribute;
import org.openmaji.meem.definition.WedgeAttribute;
import org.openmaji.meem.filter.ExactMatchFilter;
import org.openmaji.meem.filter.Filter;
import org.openmaji.meem.filter.FilterChecker;
import org.openmaji.meem.filter.IllegalFilterException;
import org.openmaji.meem.wedge.error.ErrorHandler;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClient;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClientAdapter;
import org.openmaji.meem.wedge.reference.Reference;
import org.openmaji.system.meem.core.MeemCore;
import org.openmaji.system.meem.definition.MeemStructure;
import org.openmaji.system.meem.hook.Hook;
import org.openmaji.system.meem.hook.HookProcessor;
import org.openmaji.system.meem.hook.invoke.Invocation;
import org.openmaji.system.meem.hook.security.AccessControl;
import org.openmaji.system.meem.hook.security.AccessControlClient;
import org.openmaji.system.meem.hook.security.AccessLevel;
import org.openmaji.system.meem.hook.security.InboundSecurityHook;
import org.openmaji.system.meem.hook.security.OwnerControl;
import org.openmaji.system.meem.hook.security.Principals;
import org.openmaji.system.meem.wedge.reference.ContentClient;
import org.openmaji.system.meem.wedge.reference.ContentException;
import org.openmaji.system.meem.wedge.reference.ContentProvider;


import java.util.logging.Level;
import java.util.logging.Logger;



/**
 * <p>
 * Provides an inbound Facet for adding and removing access to a Meem.
 * Also performs security check on all invocations when added as an invocation Hook to the Meem.
 * </p>
 * <p>
 * There are various access levels a Subject may have on a Meem.  They are:
 * 	DENY: no access
 * 	READ: may add outbound references, and write (send messages) to System Facets
 * 	WRITE: may write (send messages) to application Facets
 * 	READ_WRITE: may write to all Facets
 * 	CONFIGURE: may write to configuration Facet
 * 	ADMINISTER: may change access control permissions
 * </p>
 * <p>
 * Note: Implementation thread safe = Not considered yet
 * </p>
 *
 */
public class InboundSecurityHookWedge 
	implements InboundSecurityHook, Hook, AccessControl, Wedge, FilterChecker
{
	private static final Logger logger = Logger.getAnonymousLogger();
	
	private static final boolean TRACE = false;

    public MeemCore					meemCore;
    
    public ErrorHandler    	        errorHandlerConduit;
	
    public AccessControlClient      accessControlClientConduit;
	
    public AccessControlClient      accessControlClient;
    
    public final ContentProvider    accessControlClientProvider = new ContentProvider() {
        public void sendContent(Object target, Filter filter) throws ContentException {
            AccessControlClient     client = (AccessControlClient)target;
            Object template = null;
            if (filter != null && filter instanceof ExactMatchFilter) {
            	template = ((ExactMatchFilter)filter).getTemplate();
            }
            for (Principal p : principals.keySet()) {
                if (template == null || template.equals(p)) {
                    client.accessAdded(p, principals.get(p));
                }                
            }
        }
    };
    
    public OwnerControl ownerControlConduit = new OwnerControlConduit();
    
    public AccessControl            accessControlConduit = this;

    /**
     * a set of Principals with a mapping to their access level
     */
    public Map<Principal, AccessLevel> principals = new HashMap<Principal, AccessLevel>();

//	/**
//	 * Collection of subjects we have authorised
//	 */
//	private Hashtable knownSubjects = CollectionUtility.createHashtable();

	/**
	 * The Principal that is considered to "own" this Meem.  This Subject is given Administrator access.
	 * TODO This should not be stored, but a handle to the Subject that is dynamically updated should.
	 */
	public Principal owningPrincipal;

    /**
     * The subject associated with this Meem, if any.  This will be obtained
     * by using a handle to the Subject and providing credentials to authenticate
     * against the Maji authenticator.
     * NOT YET IMPLEMENTED.
     */
    private Subject meemSubject = null;

	/**
	 * 
	 */
	public LifeCycleClient lifeCycleClientConduit = new LifeCycleClientAdapter(this);

	/**
	 * a set of system Facet names.
	 */
	private HashSet<String> systemFacets = new HashSet<String>();
	
	private boolean   initialized = false;
	
	private transient Subject activatingSubject = null;

	
	/**
	 *
	 */
	public InboundSecurityHookWedge()
	{
		// assume the Activating Subject is the one in context when this Wedge is created.
		activatingSubject = getCurrentSubject();
	}

	/* (non-Javadoc)
	 * @see org.openmaji.implementation.server.meem.hook.security.AccessControl#authorisationLevel(javax.security.auth.Subject, org.openmaji.implementation.server.meem.hook.security.AccessLevel)
	 */
	public void addAccess(Principal principal, AccessLevel level)
	{
		if (TRACE) {
		logger.log(Level.INFO, "Add access for " + principal + ". level = " + level);
		}
		
		principals.put(principal, level);

//		// make sure the knownSubjects cache is reset if any changes happen.
//      knownSubjects.clear();

		accessControlClientConduit.accessAdded(principal, level);
        accessControlClient.accessAdded(principal, level);
	}
	
	/**
	 * Remove access by the Principal to this Meem.
	 */
	public void removeAccess(Principal principal)
	{
		if (TRACE) {
		logger.log(Level.INFO, "Remove access for " + principal + ".");
		}
		
		AccessLevel	level = null;
		
		level = (AccessLevel)principals.remove(principal);

//		// make sure the knownSubjects cache is reset if any changes happen.
//      knownSubjects.clear();

		accessControlClientConduit.accessRemoved(principal, level);
		accessControlClient.accessRemoved(principal, level);
	}

	
	/* ----------------- Hook interface ---------------------- */
	
	/**
	 * Process the invocation.
	 * @return Whether invocation processing should continue.
	 */
    public boolean process(Invocation invocation, final HookProcessor hookProcessor) 
    	throws Throwable 
	{
    	if (!initialized) {
    		initialize();
    		initialized = true;
    	}
    	
    	// perform access control
    	if ( !checkAccess(invocation) ) {
    		ReflectionInvocation reflectionInvocation = (ReflectionInvocation)invocation;
    		Method method = reflectionInvocation.getMethod();
			String message = "no access for Subject on, \"" + meemCore.getMeemPath() + ":" + invocation.getFacetIdentifier() + "." + method.getName() + "\" - level = " + getCurrentAccessLevel(principals);
    		errorHandlerConduit.thrown(new SecurityException(message));
    		logger.log(Level.INFO, message, new SecurityException("No Access"));
    		return false;
    	}

    	// now put new Subject in context if the Meem is associated with one.

		if (meemSubject != null) {
	    	final PrivilegedExceptionAction<Boolean> action = new PrivilegedExceptionAction<Boolean>() {
				public Boolean run() throws Exception {
					try {
						return new Boolean(hookProcessor.processHooks());
					}
					catch (Throwable t) {
						if (t instanceof Exception) {
							throw (Exception) t;
						}
						else {
							throw new Exception(t);
						}
					}
				}
			};

			Boolean result = Subject.doAsPrivileged(meemSubject, action, null);
			return result.booleanValue();
		}
		else {
			// implicit delegation.  The thread will continue in the context of the current Subject
			return true;
		}
    }

    
    /* ------------------------- Lifecycle Methods ------------------- */
    
    public void commence() {
	    
    	// TODO reconstruct meemSubject if required - on DORMANT->LOADED

	    // determine system Facets
	    
	    systemFacets.clear();
		MeemStructure meemStructure = meemCore.getMeemStructure();
		synchronized (meemStructure) {
			Iterator<Serializable> wedgeIter = meemStructure.getWedgeAttributeKeys().iterator();
			while (wedgeIter.hasNext()) {
				Serializable wedgeKey = wedgeIter.next();
				WedgeAttribute wedgeAttribute = meemStructure.getWedgeAttribute(wedgeKey);

				if (wedgeAttribute.isSystemWedge()) {
					Iterator<String> facetIter = meemStructure.getFacetAttributeKeys(wedgeKey).iterator();
					while (facetIter.hasNext()) {
						String facetKey = facetIter.next();
						FacetAttribute facetAttribute = meemStructure.getFacetAttribute(facetKey);
						systemFacets.add(facetAttribute.getIdentifier());
					}
				}
			}
		}
    }

    public void conclude() {
    	// TODO deconstruct meemSubject if credentials present - on LOADED->DORMANT
    }
    
    
    /* ------------------------ FilterChecker interface ------------------------ */
    
    /* (non-Javadoc)
     * @see org.openmaji.meem.filter.FilterChecker#invokeMethodCheck(org.openmaji.meem.filter.Filter, java.lang.String, java.lang.Object[])
     */
    public boolean invokeMethodCheck(
        Filter filter,
        String facetName,
        String methodName,
        Object[] args)
        throws IllegalFilterException
    {
        if (filter instanceof ExactMatchFilter) {
            ExactMatchFilter    f = (ExactMatchFilter)filter;
            
            if (f.getTemplate() instanceof Principal) {
                if (methodName.equals("accessAdded") || methodName.equals("accessRemoved")) {
                    return args[0].equals(f.getTemplate());
                }
            }
        }
        
        return true;
    }
    
    
    /* -------------------- utility methods ------------------------- */
    
    /**
     * Set up initial access permissions.
     * 
	 * Set the owning Principal of this Meem to be the main Principal of the Subject 
	 * in context when MeemCore is first created
	 * 
	 * The main Principal of the current Subject is assumed to be the owner, in time 
	 * the Meem's owning Principal may be configurable.
     */
	private void initialize() {

		if (owningPrincipal == null) {
			// assume this is first time that the Meem has been activated.

			// only set owning Principal once
			owningPrincipal = (Principal)activatingSubject.getPrincipals(X500Principal.class).toArray()[0];
			
			// Give the owning Principal administrator priviledges
			addAccess(owningPrincipal, AccessLevel.ADMINISTER);
			
		    // give the system user administrator access 
		    addAccess(Principals.SYSTEM, AccessLevel.ADMINISTER);
			
			// give other "users" read-only access
		    addAccess(Principals.OTHER, AccessLevel.READ);
		}

    }
    
	/**
	 * Check whether calling Subject has access to this Meem.
	 */
    private boolean checkAccess(Invocation invocation) throws Throwable
    {    	
		AccessLevel	level = getCurrentAccessLevel(principals);

		if ( level == null /*|| level.equals(AccessLevel.DENY)*/ ) {
//			errorHandlerConduit.thrown(new GeneralSecurityException("access violation - level is null."));
			return false;
		}

		String facetIdentifier = invocation.getFacetIdentifier();
		
		if (facetIdentifier.equals("meem")) {
			ReflectionInvocation inv = (ReflectionInvocation)invocation;
			String methodName = inv.getMethod().getName();
			if ( "addReference".equals(methodName) || "removeReference".equals(methodName) ) {
				// call to meem.addOutboundReference() or meem.removeOutboundReference()
				Reference ref = (Reference)inv.getArgs()[0];
				return checkReference(ref, level);
			}
			else {
				// TODO check for access on dependency methods
				return true;
			}
		}
		else if ( systemFacets.contains(facetIdentifier) ) {
			// invocation on a System Facet
			
			// allow access to inbound system Facets if Principal has write permission
			if (AccessLevel.WRITE.isGrantedBy(level)) {
				return true;
			}
		}
		else {
			// invocation on an application Facet
			
			// TODO this allow the system to work with "guest" user, change
			if (AccessLevel.READ.isGrantedBy(level)) {
				return true;
			}
		}

//		Subject currentSubject = Subject.getSubject(java.security.AccessController.getContext());
//		errorHandlerConduit.thrown(
//				new GeneralSecurityException(
//						"access violation - subject has no permission: " + 
//						(Principal)currentSubject.getPrincipals(X500Principal.class).toArray()[0]
//				)
//			);

		return false;
    }

    /**
     * Checks whether the given reference can be added or removed to or from
     * this Meem.
     * 
     * @param ref The Reference in question
     * @param level the AccessLevel of the current Subject
     */
    private boolean checkReference(Reference ref, AccessLevel level) {
		
		String refFacetIdentifier = ref.getFacetIdentifier();
		
		// check which Outbound Facet the reference is added or removed from.
		
		if (refFacetIdentifier.equals("accessControl")) {
			if (AccessLevel.ADMINISTER.isGrantedBy(level)) {
				return true;
			}
		}
		else if (
				refFacetIdentifier.equals("meemClientFacet") || 
				refFacetIdentifier.equals("remoteMeemClientFacet")
				) 
		{
			if (AccessLevel.READ.isGrantedBy(level)) {
				return true;
			}
		}
//		else if (systemFacets.contains(refFacetIdentifier)) {
//			if (AccessLevel.WRITE.isGrantedBy(level)) {
//				return true;
//			}
//		}
		else {
			if (AccessLevel.READ.isGrantedBy(level)) {
				return true;
			}
		}

		// TODO check for more specific system Facets

//		logger.log(Level.INFO, "Can not add or remove reference on Facet, " + refFacetIdentifier);

		// no access granted, send "contentFailed"
		Object refTarget = ref.getTarget();
		if (refTarget instanceof ContentClient) {
			ContentClient	client = (ContentClient) refTarget;
			
			client.contentFailed("subject has no permission");

//			logger.log(Level.INFO, "Client notified of lack of access.");
			// we've notified them, no need for an exception.
			return false;
		}
		
		return false;
    }
    
	/**
	 * Get the access level we are currently running with.
	 * 
	 * @param principals
	 * @return the current access level.
	 */
	private AccessLevel getCurrentAccessLevel(Map<Principal, AccessLevel> principalLevels)
	{
		Subject currentSubject = getCurrentSubject();
		
		// check for system-wide access level
		AccessLevel	level;
		
		try {
			level = MeemCoreRootAuthority.getAccessLevel(currentSubject);
		}
		catch (AccessControlException ex) {
			return AccessLevel.DENY;
		}

		if (level != null) {
			return level;
		}

		// check principals' access levels
		Set<X500Principal> principals = currentSubject.getPrincipals(X500Principal.class);
		for (X500Principal p : principals) {
			AccessLevel	l = principalLevels.get(p);
			if (level == null || level.isGrantedBy(l)) {
				level = l;
			}
		}

		if (level != null) {
//				knownSubjects.put(currentSubject, level);
			return level;
		}

		// OTHER is a special group.
		level = (AccessLevel) principalLevels.get(Principals.OTHER);
		if (level != null) {
			return level;
		}

		return AccessLevel.DENY; 
	}

	
    /**
     * The current running subject - may return null if there is no subject in the current 
     * acess control context.
     * 
     * @return the subject in the current AccessControlContext.
     */
    private Subject getCurrentSubject()
    {
		return Subject.getSubject(java.security.AccessController.getContext());
    }

    /**
     * 
     */
    private final class OwnerControlConduit implements OwnerControl {
		public void setOwner(Subject owningSubject) {

			// TODO check if caller has access

			meemSubject = owningSubject;
		}
    }
}
