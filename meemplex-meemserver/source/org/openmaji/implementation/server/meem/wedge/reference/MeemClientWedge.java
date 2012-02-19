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
        public void provideReference(Meem meem, String inboundFacetIdentifier,
            Class specification, MeemClientCallback referenceCallback)
        {
			if (meem.equals(meemCore.getSelf()))
			{
				Reference inboundReference = Reference.spi.create(inboundFacetIdentifier,
							meemCore.getTarget(inboundFacetIdentifier), false);
							
				referenceCallback.referenceProvided(inboundReference);
				return;
			}
			
            CallbackReferenceClient myReferenceClient =
                new CallbackReferenceClient(referenceCallback);

            MeemClient meemClientProxy = (MeemClient)
                meemCore.getLimitedTargetFor(myReferenceClient, MeemClient.class);

            Reference meemReference = Reference.spi.create(
                "meemClientFacet",
                meemClientProxy,
                true,
                new FacetDescriptor(inboundFacetIdentifier, specification));

            meem.addOutboundReference(meemReference, true);

            // TODO[peter] Set a timeout?
        }
	}
	
    public class CallbackReferenceClient implements MeemClient, ContentClient
    {
        public CallbackReferenceClient(MeemClientCallback referenceCallback)
        {
            this.referenceCallback = referenceCallback;
        }

        public void referenceAdded(Reference reference)
        {
            this.reference = reference;
        }

        public void referenceRemoved(Reference reference)
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

        private MeemClientCallback referenceCallback;
        private Reference reference = null;
    }
}