/*
 * @(#)MeemClientWedge.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.server.meem.wedge.reference;


import org.openmaji.meem.*;
import org.openmaji.meem.filter.FacetDescriptor;
import org.openmaji.meem.wedge.reference.Reference;
import org.openmaji.system.meem.core.MeemCore;
import org.openmaji.system.meem.wedge.reference.*;

/**
 * <p>
 * ...
 * </p>
 */
public class MeemClientWedge implements Wedge
{
	public MeemCore meemCore;

	public MeemClientConduit meemClientConduit = new MeemClientImpl();

	private class MeemClientImpl implements MeemClientConduit
	{
        public <T extends Facet> void provideReference(Meem meem, String inboundFacetIdentifier, Class<T> specification, MeemClientCallback<T> referenceCallback)
        {
			if (meem.equals(meemCore.getSelf()))
			{
				Reference<T> inboundReference = Reference.spi.create(inboundFacetIdentifier, (T)meemCore.getTarget(inboundFacetIdentifier), false);
				referenceCallback.referenceProvided(inboundReference);
				return;
			}
			
            CallbackReferenceClient<T> myReferenceClient = new CallbackReferenceClient<T>(referenceCallback);

            MeemClient meemClientProxy =  meemCore.getLimitedTargetFor(myReferenceClient, MeemClient.class);

            Reference<MeemClient> meemReference = Reference.spi.create(
                "meemClientFacet",
                meemClientProxy,
                true,
                new FacetDescriptor(inboundFacetIdentifier, specification));

            meem.addOutboundReference(meemReference, true);

            // TODO[peter] Set a timeout?
        }
	}
	
    public class CallbackReferenceClient<T extends Facet> implements MeemClient, ContentClient
    {
        public CallbackReferenceClient(MeemClientCallback<T> referenceCallback)
        {
            this.referenceCallback = referenceCallback;
        }

        public void referenceAdded(Reference<?> reference)
        {
            this.reference = (Reference<T>) reference;
        }

        public void referenceRemoved(Reference<?> reference)
        {
        }

        public void contentSent()
        {
            if (referenceCallback != null)
            {
                referenceCallback.referenceProvided(reference);
                referenceCallback = null;
            }
        }

		public void contentFailed(String reason)
		{
			if (referenceCallback != null)
			{
				referenceCallback.referenceProvided(null);
				referenceCallback = null;
			}
		}

        private MeemClientCallback<T> referenceCallback;
        private Reference<T> reference = null;
    }
}