/*
 * @(#)ErrorHandlerProxy.java
 * Created on 8/09/2003
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.client;

import org.openmaji.meem.Facet;
import org.openmaji.meem.wedge.error.ErrorHandler;


/**
 * <code>ErrorHandlerProxy</code>.
 * <p>
 * @author Kin Wong
 */
public class ErrorHandlerProxy extends FacetProxy implements ErrorHandler {
	static private FacetSpecificationPair defaultSpecs = 
		new FacetSpecificationPair(
			new FacetInboundSpecification(ErrorHandler.class, "errorHandler"),
			new FacetOutboundSpecification(ErrorHandler.class, "errorHandlerClient"));


	static public String ID_THROWABLE = "Throwable";
	
	private Throwable throwable;
	
	//=== Internal ErrorHandler Client Implementation ===============================
	public static class LocalErrorHandler
		implements ErrorHandler
	{
		ErrorHandlerProxy	p;
		
		public LocalErrorHandler(
			ErrorHandlerProxy p)
		{
			this.p = p;
		}
		public void thrown(final Throwable throwable) {
			p.throwable = throwable;

			if(p.containsClient())
			p.getSynchronizer().execute(new Runnable() {
				public void run() {
					p.fireThrown(throwable);
				}
			});
		}
	}
	
	private ErrorHandler errorHandlerClient = new LocalErrorHandler(this);
	/**
	 * Constructs an instance of <code>ErrorHandlerProxy</code>.
	 * <p>
	 * @param meemClientProxy
	 */
	public ErrorHandlerProxy(MeemClientProxy meemClientProxy) {
		super(meemClientProxy, defaultSpecs);
	}

	private ErrorHandler getErrorHandler() {
		return(ErrorHandler)getInboundReference();
	}
	
	public Throwable getLastThrown() {
		return throwable;
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.client.FacetProxy#getOutboundTarget()
	 */	
	protected Facet getOutboundTarget() {
		return errorHandlerClient;
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.client.FacetProxy#clearContent()
	 */
	protected void clearContent() {
		throwable = null;
	}

	//=== Client Management ======================================================
	private void fireThrown(Throwable throwable) {
		Object[] clients = getClients();
		for(int i=0; i < clients.length; i++) {
			ErrorHandler client = (ErrorHandler)clients[i];
			client.thrown(throwable);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.client.FacetProxy#realizeClientContent(java.lang.Object)
	 */	
	protected void realizeClientContent(Object client) {
		ErrorHandler errorHandler = (ErrorHandler)client;
		errorHandler.thrown(getLastThrown());
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.client.FacetProxy#clearClientContent(java.lang.Object)
	 */	
	protected void clearClientContent(Object client) {
		ErrorHandler errorHandler = (ErrorHandler)client;
		errorHandler.thrown(null);
	}

	//=== External ErrorHandler Implementation ===================================
	/* (non-Javadoc)
	 * @see org.openmaji.meem.aspect.wedge.error.ErrorHandler#thrown(java.lang.Throwable)
	 */
	public void thrown(final Throwable throwable) {
		if(isReadOnly()) return;

		getErrorHandler().thrown(throwable);
	}
}
