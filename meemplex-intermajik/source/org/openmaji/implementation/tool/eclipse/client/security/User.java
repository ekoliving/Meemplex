/*
 * @(#)User.java
 * Created on 13/01/2004
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.client.security;

import java.util.StringTokenizer;

import javax.security.auth.x500.X500Principal;

/**
 * <code>User</code>.
 * <p>
 * @author Kin Wong
 */
public class User {
	private String name = "";
	private String ou = "";
	private String organization = "";
	private String location = "";
	private String state = "";
	private String country = "";
	
	public User(X500Principal principal) {
		StringTokenizer	st = new StringTokenizer(principal.toString(), "=,");
		while (st.hasMoreTokens()) {
			String	tok = st.nextToken().trim();
			
			if (tok.equalsIgnoreCase("CN")) {
				setName(st.nextToken().trim());
				//st.nextToken();
			}
			else
			if(tok.equalsIgnoreCase("OU")) {
				setOU(st.nextToken().trim());
			}
			else
			if(tok.equalsIgnoreCase("O")) {
				setOrganization(st.nextToken().trim());
			}
			else
			if(tok.equalsIgnoreCase("L")) {
				setLocation(st.nextToken().trim());
			}
			else
			if(tok.equalsIgnoreCase("ST")) {
				setState(st.nextToken().trim());
			}
			else
			if(tok.equalsIgnoreCase("C")) {
				setCountry(st.nextToken().trim());
			}
			else
			st.nextToken();
			
		}
	}
	
	public String getName() {
		return name;
	}
	
	protected void setName(String name) {
		this.name = name;
	}

	protected void setState(String state) {
		this.state = state;
	}
	protected void setOU(String ou) {
		this.ou = ou;
	}

	protected void setOrganization(String organization) {
		this.organization = organization;
	}
	
	protected void setLocation(String location) {
		this.location = location;
	}
	
	protected void setCountry(String country) {
		this.country = country;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return name.hashCode() + 
			ou.hashCode() +
			organization.hashCode() +
			location.hashCode() +		
			state.hashCode() +
			country.hashCode();
		}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if(obj == null) return false;
		if(this == obj) return true;
		if(!(obj instanceof User)) return false;
		
		User that = (User)obj;
		if(!this.name.equals(that.name)) return false;
		if(!this.ou.equals(that.ou)) return false;
		if(!this.organization.equals(that.organization)) return false;
		if(!this.location.equals(that.location)) return false;
		if(!this.state.equals(that.state)) return false;
		return this.country.equals(that.country);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		String 
		text = "cn=" + name;
		text += ",ou=" + ou;
		text += ",o=" + organization;
		text += ",l=" + location;
		text += ",st=" + state;
		text += ",c=" + country;
		return text;
	}
}

