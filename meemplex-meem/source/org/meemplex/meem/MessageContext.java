package org.meemplex.meem;

import java.util.Stack;

/**
 * This is a context object that may be included as an annotated object.
 * 
 * @author stormboy
 *
 */
public class MessageContext {

	/**
	 * The stack of messages sent in order to reach this point.
	 */
	private Stack<Message> messageStack;
	
	/**
	 * The Subject who initiated the last message.
	 */
	//private Subject callingSubject;

	
	public void setMessageStack(Stack<Message> messageStack) {
	    this.messageStack = messageStack;
    }

	public Stack<Message> getMessageStack() {
	    return messageStack;
    }
}
