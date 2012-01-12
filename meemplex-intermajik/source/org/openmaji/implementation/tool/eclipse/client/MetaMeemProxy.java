/*
 WedgeAttribute wedgeDefinition,
 * @(#)MetaMeemProxy.java
 * Created on 2/05/2003
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.client;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.openmaji.implementation.server.meem.definition.MeemStructureImpl;
import org.openmaji.implementation.server.meem.definition.MetaMeemStructureAdapter;

import org.openmaji.meem.Facet;
import org.openmaji.meem.definition.*;
import org.openmaji.system.meem.definition.MeemStructure;
import org.openmaji.system.meem.definition.MetaMeem;
import org.openmaji.system.meem.wedge.reference.ContentClient;


/**
 * <code>MetaMeemProxy</code> represents the client side proxy of MetaMeem
 * interfaces.
 * <p>
 * 
 * @author Kin Wong
 */
public class MetaMeemProxy extends FacetProxy implements MetaMeem {
	static private FacetSpecificationPair defaultSpecs = new FacetSpecificationPair(new FacetInboundSpecification(MetaMeem.class, "metaMeem"), new FacetOutboundSpecification(MetaMeem.class, "metaMeemClient"));

	public static String ID_MEEM_ATTRIBUTE = "MeemAttribute";

	public static String ID_WEDGE_ATTRIBUTE = "WedgeAttribute";

	public static String ID_FACET_ATTRIBUTE = "FacetAttribute";

	private MeemStructure structure;

	private Map<String, Set<String>> interfaces;

	private MetaMeemProxyClient client;

	/**
	 * Constructs an instance of <code>MetaMeemProxy</code>.
	 * <p>
	 * 
	 * @param meemClientProxy
	 * @param specs
	 */
	public MetaMeemProxy(MeemClientProxy meemClientProxy, FacetSpecificationPair specs) {
		super(meemClientProxy, specs);
	}

	/**
	 * Constructs an instance of <code>MetaMeemProxy</code>.
	 * <p>
	 * 
	 * @param meemClientProxy
	 */
	public MetaMeemProxy(MeemClientProxy meemClientProxy) {
		this(meemClientProxy, defaultSpecs);
	}

	private MetaMeem getMetaMeem() {
		return (MetaMeem) getInboundReference();
	}

	// === External Helpers
	// =======================================================
	/**
	 * Returns the <code>MeemStructure</code> associates with this
	 * <code>MetaMeemProxy</code>.
	 * 
	 * @return MeemStructure The <code>MeemStructure</code> associates with
	 *         this <code>MetaMeemProxy</code>.
	 */
	public MeemStructure getStructure() {
		return structure;
	}

	/**
	 * Checks whether the meem has the given facet.
	 * <p>
	 * 
	 * @param specification
	 *            The class of the facet.
	 * @return true if the meem has the facet, false otherwise.
	 */
	public boolean hasFacet(Class specification) {
		if (!isContentInitialized() && isConnected()) {
			System.err.println("MetaMeemProxy.hasFacet(): content not initialized " + isConnected() + " : " + specification);
			new Throwable().printStackTrace();
		}
		return getInterfaces().containsKey(specification.getName());
	}

	synchronized public boolean hasA(String facetIdentifier, Class specification, Direction direction) {
		Set<String> attributeSet = getInterfaces().get(specification.getName());
		if (attributeSet == null) {
			return false;
		}

		for (Iterator<String> iter = attributeSet.iterator(); iter.hasNext();) {
			String facetKey = iter.next();
			FacetAttribute facetAttribute = structure.getFacetAttribute(facetKey);
			if (facetAttribute != null) {
				if (facetAttribute.getIdentifier().equals(facetIdentifier) && facetAttribute.isDirection(direction)) {
					return true;
				}
			}
		}
		return false;
	}

	// === Internal Implementation
	// ================================================

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openmaji.implementation.tool.eclipse.client.FacetProxy#isEssential()
	 */
	protected boolean isEssential() {
		return true;
	}

	/**
	 * @see org.openmaji.implementation.tool.eclipse.client.FacetProxy#getOutboundTarget()
	 */
	protected Facet getOutboundTarget() {
		if (client == null) {
			client = new MetaMeemProxyClient(this, structure);
		}
		return client;
	}

	private Map<String, Set<String>> getInterfaces() {
		if (interfaces == null) {
			interfaces = new HashMap<String, Set<String>>();  
		}
		return interfaces;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openmaji.implementation.tool.eclipse.client.FacetProxy#clearContent()
	 */
	protected void clearContent() {
		client = null;
		structure = new MeemStructureImpl();
		getInterfaces().clear();
		setContentInitialize(false);
	}

	synchronized private void addFacetInterface(FacetAttribute facetAttribute) {
		Set<String> attributeSet = getInterfaces().get(facetAttribute.getInterfaceName());

		if (attributeSet == null) {
			attributeSet = new HashSet<String>();
			getInterfaces().put(facetAttribute.getInterfaceName(), attributeSet);
		}
		attributeSet.add(facetAttribute.getIdentifier());
	}

	synchronized private void updateFacetInterface(FacetAttribute facetAttribute) {
		FacetAttribute oldFacetAttribute = getStructure().getFacetAttribute(facetAttribute.getIdentifier());

		if (oldFacetAttribute.getInterfaceName().equals(facetAttribute.getInterfaceName()))
			return;

		// Remove old key from attribute set
		removeFacetInterface(oldFacetAttribute);
		addFacetInterface(facetAttribute);
	}

	synchronized private void removeFacetInterface(String key) {
		FacetAttribute facetAttribute = getStructure().getFacetAttribute(key);
		if (facetAttribute == null)
			return;
		removeFacetInterface(facetAttribute);
	}

	synchronized private void removeFacetInterface(FacetAttribute facetAttribute) {
		Set<String> attributeSet = getInterfaces().get(facetAttribute.getInterfaceName());
		if (attributeSet == null) {
			return;
		}

		if (attributeSet.remove(facetAttribute.getIdentifier())) {
			if (attributeSet.isEmpty()) {
				getInterfaces().remove(facetAttribute.getInterfaceName());
			}
		}
	}

	// === Client Management
	// ======================================================
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openmaji.implementation.tool.eclipse.client.FacetProxy#realizeClientContent(java.lang.Object)
	 */
	synchronized protected void realizeClientContent(Object client) {
		MetaMeem metaMeemClient = (MetaMeem) client;

		// Update Meem Attribute
		metaMeemClient.updateMeemAttribute(structure.getMeemAttribute());

		// Add Wedges attributes
		synchronized (structure) {
			Iterator<Serializable> itWedgeKeys = structure.getWedgeAttributeKeys().iterator();
			while (itWedgeKeys.hasNext()) {
				Serializable wedgeKey = itWedgeKeys.next();
				WedgeAttribute wedgeAttribute = structure.getWedgeAttribute(wedgeKey);
				if (wedgeAttribute != null) {
					metaMeemClient.addWedgeAttribute(wedgeAttribute);

					// Add Facet Attributes
					Iterator<String> itFacetKeys = structure.getFacetAttributeKeys(wedgeKey).iterator();
					while (itFacetKeys.hasNext()) {
						String facetKey = itFacetKeys.next();
						FacetAttribute facetAttribute = structure.getFacetAttribute(facetKey);
						if (facetAttribute != null) {
							metaMeemClient.addFacetAttribute(wedgeKey, facetAttribute);
						}
					}
				}
			}
			// Add Dependency attributes
			Iterator<Serializable> itDependencyKeys = structure.getDependencyAttributeKeys().iterator();
			while (itDependencyKeys.hasNext()) {
				Serializable dependencyKey = itDependencyKeys.next();
				DependencyAttribute dependencyAttribute = structure.getDependencyAttribute(dependencyKey);

				if (dependencyAttribute != null) {
					String facetKey = structure.getFacetKeyFromDependencyKey(dependencyKey);
					if (facetKey != null) {
						metaMeemClient.addDependencyAttribute(facetKey, dependencyAttribute);
					}
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openmaji.implementation.tool.eclipse.client.FacetProxy#clearClientContent(java.lang.Object)
	 */
	synchronized protected void clearClientContent(Object client) {
		MetaMeem metaMeemClient = (MetaMeem) client;
		synchronized (structure) {
			// Remove Dependency attributes
			Iterator<Serializable> itDependencyKeys = structure.getDependencyAttributeKeys().iterator();
			while (itDependencyKeys.hasNext()) {
				metaMeemClient.removeDependencyAttribute(itDependencyKeys.next());
			}

			Iterator<Serializable> itWedgeKeys = structure.getWedgeAttributeKeys().iterator();
			while (itWedgeKeys.hasNext()) {
				Serializable wedgeKey = (Serializable) itWedgeKeys.next();
				// Remove Facet Attributes
				Iterator<String> itFacetKeys = structure.getFacetAttributeKeys(wedgeKey).iterator();
				while (itFacetKeys.hasNext()) {
					metaMeemClient.removeFacetAttribute(itFacetKeys.next());
				}
				// Remove Wedge Attributes
				metaMeemClient.removeWedgeAttribute(wedgeKey);
			}
		}
	}

	// === External MetaMeem Implementation
	// =======================================
	public void addDependencyAttribute(final String facetId, final DependencyAttribute dependencyAttribute) {
		if (isReadOnly())
			return;

		getMetaMeem().addDependencyAttribute(facetId, dependencyAttribute);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openmaji.meem.definition.MetaMeem#addFacetAttribute(java.lang.Object,
	 *      org.openmaji.meem.definition.FacetAttribute)
	 */
	public void addFacetAttribute(final Serializable wedgeId, final FacetAttribute facetDefinition) {
		if (isReadOnly())
			return;

		getMetaMeem().addFacetAttribute(wedgeId, facetDefinition);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openmaji.meem.definition.MetaMeem#addWedgeAttribute(org.openmaji.meem.definition.WedgeAttribute)
	 */
	public void addWedgeAttribute(final WedgeAttribute wedgeDefinition) {
		if (isReadOnly())
			return;

		getMetaMeem().addWedgeAttribute(wedgeDefinition);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openmaji.meem.definition.MetaMeem#removeDependencyAttribute(java.lang.Object)
	 */
	public void removeDependencyAttribute(final Serializable dependencyId) {
		if (isReadOnly())
			return;

		getMetaMeem().removeDependencyAttribute(dependencyId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openmaji.meem.definition.MetaMeem#removeFacetAttribute(java.lang.Object)
	 */
	public void removeFacetAttribute(final String facetId) {
		if (isReadOnly()) {
			return;
		}

		getMetaMeem().removeFacetAttribute(facetId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openmaji.meem.definition.MetaMeem#removeWedgeAttribute(java.lang.Object)
	 */
	public void removeWedgeAttribute(final Serializable wedgeId) {
		if (isReadOnly())
			return;

		getMetaMeem().removeWedgeAttribute(wedgeId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openmaji.meem.definition.MetaMeem#updateDependencyAttribute(org.openmaji.meem.definition.DependencyAttribute)
	 */
	public void updateDependencyAttribute(final DependencyAttribute dependencyAttribute) {
		if (isReadOnly())
			return;

		getMetaMeem().updateDependencyAttribute(dependencyAttribute);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openmaji.meem.definition.MetaMeem#updateFacetAttribute(org.openmaji.meem.definition.FacetAttribute)
	 */
	public void updateFacetAttribute(final FacetAttribute facetDefinition) {
		if (isReadOnly())
			return;

		getMetaMeem().updateFacetAttribute(facetDefinition);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openmaji.meem.definition.MetaMeem#updateMeemAttribute(org.openmaji.meem.definition.MeemAttribute)
	 */
	public void updateMeemAttribute(final MeemAttribute meemDefinition) {
		if (isReadOnly())
			return;

		getMetaMeem().updateMeemAttribute(meemDefinition);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openmaji.meem.definition.MetaMeem#updateWedgeAttribute(org.openmaji.meem.definition.WedgeAttribute)
	 */
	public void updateWedgeAttribute(final WedgeAttribute wedgeDefinition) {
		if (isReadOnly())
			return;

		getMetaMeem().updateWedgeAttribute(wedgeDefinition);
	}
	
	public static class MetaMeemProxyClient extends MetaMeemStructureAdapter implements MetaMeem, ContentClient {
		MetaMeemProxy p;

		MetaMeemProxyClient(MetaMeemProxy p, MeemStructure structure) {
			super(structure);
			this.p = p;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.openmaji.meem.definition.MetaMeem#addDependencyAttribute(java.lang.Object,
		 *      org.openmaji.meem.definition.DependencyAttribute)
		 */
		public synchronized void addDependencyAttribute(final String facetId, final DependencyAttribute dependencyAttribute) {
			super.addDependencyAttribute(facetId, dependencyAttribute);

			if (p.containsClient())
				p.getSynchronizer().execute(new Runnable() {
					public void run() {
						Object[] clients = p.getClients();
						for (int i = 0; i < clients.length; i++) {
							MetaMeem client = (MetaMeem) clients[i];
							client.addDependencyAttribute(facetId, dependencyAttribute);
						}
					}
				});
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.openmaji.meem.definition.MetaMeem#addFacetAttribute(java.lang.Object,
		 *      org.openmaji.meem.definition.FacetAttribute)
		 */
		public synchronized void addFacetAttribute(final Serializable wedgeKey, final FacetAttribute facetAttribute) {
			super.addFacetAttribute(wedgeKey, facetAttribute);
			p.addFacetInterface(facetAttribute);

			if (p.containsClient())
				p.getSynchronizer().execute(new Runnable() {
					public void run() {
						Object[] clients = p.getClients();
						for (int i = 0; i < clients.length; i++) {
							MetaMeem client = (MetaMeem) clients[i];
							client.addFacetAttribute(wedgeKey, facetAttribute);
						}
					}
				});
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.openmaji.meem.definition.MetaMeem#addWedgeAttribute(org.openmaji.meem.definition.WedgeAttribute)
		 */
		public synchronized void addWedgeAttribute(final WedgeAttribute wedgeAttribute) {
			super.addWedgeAttribute(wedgeAttribute);

			if (p.containsClient())
				p.getSynchronizer().execute(new Runnable() {
					public void run() {
						Object[] clients = p.getClients();
						for (int i = 0; i < clients.length; i++) {
							MetaMeem client = (MetaMeem) clients[i];
							client.addWedgeAttribute(wedgeAttribute);
						}
					}
				});
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.openmaji.meem.definition.MetaMeem#removeDependencyAttribute(java.lang.Object)
		 */
		public synchronized void removeDependencyAttribute(final Serializable dependencyKey) {
			super.removeDependencyAttribute(dependencyKey);

			if (p.containsClient())
				p.getSynchronizer().execute(new Runnable() {
					public void run() {
						Object[] clients = p.getClients();
						for (int i = 0; i < clients.length; i++) {
							MetaMeem client = (MetaMeem) clients[i];
							client.removeDependencyAttribute(dependencyKey);
						}
					}
				});
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.openmaji.meem.definition.MetaMeem#removeFacetAttribute(java.lang.Object)
		 */
		public synchronized void removeFacetAttribute(final String facetKey) {
			super.removeFacetAttribute(facetKey);
			p.removeFacetInterface(facetKey);
			if (p.containsClient())
				p.getSynchronizer().execute(new Runnable() {
					public void run() {
						Object[] clients = p.getClients();
						for (int i = 0; i < clients.length; i++) {
							MetaMeem client = (MetaMeem) clients[i];
							client.removeFacetAttribute(facetKey);
						}
					}
				});
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.openmaji.meem.definition.MetaMeem#removeWedgeAttribute(java.lang.Object)
		 */
		public synchronized void removeWedgeAttribute(final Serializable wedgeKey) {
			super.removeWedgeAttribute(wedgeKey);

			if (p.containsClient())
				p.getSynchronizer().execute(new Runnable() {
					public void run() {
						Object[] clients = p.getClients();
						for (int i = 0; i < clients.length; i++) {
							MetaMeem client = (MetaMeem) clients[i];
							client.removeWedgeAttribute(wedgeKey);
						}
					}
				});
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.openmajik.meem.definition.MetaMeemStructureAdapter#setMeemAttribute(org.openmaji.meem.definition.MeemAttribute)
		 */
		public synchronized void setMeemAttribute(final MeemAttribute meemAttribute) {
			super.setMeemAttribute(meemAttribute);

			if (p.containsClient())
				p.getSynchronizer().execute(new Runnable() {
					public void run() {
						Object[] clients = p.getClients();
						for (int i = 0; i < clients.length; i++) {
							MetaMeem client = (MetaMeem) clients[i];
							client.updateMeemAttribute(meemAttribute);
						}
					}
				});
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.openmaji.meem.definition.MetaMeem#updateDependencyAttribute(org.openmaji.meem.definition.DependencyAttribute)
		 */
		public synchronized void updateDependencyAttribute(final DependencyAttribute dependencyAttribute) {
			super.updateDependencyAttribute(dependencyAttribute);

			if (p.containsClient())
				p.getSynchronizer().execute(new Runnable() {
					public void run() {
						Object[] clients = p.getClients();
						for (int i = 0; i < clients.length; i++) {
							MetaMeem client = (MetaMeem) clients[i];
							client.updateDependencyAttribute(dependencyAttribute);
						}
					}
				});
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.openmaji.meem.definition.MetaMeem#updateFacetAttribute(org.openmaji.meem.definition.FacetAttribute)
		 */
		public synchronized void updateFacetAttribute(final FacetAttribute facetAttribute) {
			super.updateFacetAttribute(facetAttribute);
			p.updateFacetInterface(facetAttribute);

			if (p.containsClient())
				p.getSynchronizer().execute(new Runnable() {
					public void run() {
						Object[] clients = p.getClients();
						for (int i = 0; i < clients.length; i++) {
							MetaMeem client = (MetaMeem) clients[i];
							client.updateFacetAttribute(facetAttribute);
						}
					}
				});
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.openmaji.meem.definition.MetaMeem#updateMeemAttribute(org.openmaji.meem.definition.MeemAttribute)
		 */
		public synchronized void updateMeemAttribute(final MeemAttribute meemAttribute) {
			super.updateMeemAttribute(meemAttribute);

			if (p.containsClient())
				p.getSynchronizer().execute(new Runnable() {
					public void run() {
						Object[] clients = p.getClients();
						for (int i = 0; i < clients.length; i++) {
							MetaMeem client = (MetaMeem) clients[i];
							client.updateMeemAttribute(meemAttribute);
						}
					}
				});
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.openmaji.meem.definition.MetaMeem#updateWedgeAttribute(org.openmaji.meem.definition.WedgeAttribute)
		 */
		public synchronized void updateWedgeAttribute(final WedgeAttribute wedgeAttribute) {
			super.updateWedgeAttribute(wedgeAttribute);

			if (p.containsClient())
				p.getSynchronizer().execute(new Runnable() {
					public void run() {
						Object[] clients = p.getClients();
						for (int i = 0; i < clients.length; i++) {
							MetaMeem client = (MetaMeem) clients[i];
							client.updateWedgeAttribute(wedgeAttribute);
						}
					}
				});
		}

		/**
		 * @see org.openmaji.system.meem.wedge.reference.ContentClient#contentFailed(java.lang.String)
		 */
		public void contentFailed(String reason) {
			System.err.println("MetaMeemProxyClient.contentFailed(): " + reason);
		}

		/**
		 * @see org.openmaji.system.meem.wedge.reference.ContentClient#contentSent()
		 */
		public void contentSent() {
			p.setContentInitialize(true);
		}

	}

}
