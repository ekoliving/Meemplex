package org.meemplex.internet.gwt.client.notification;

public interface NotificationService {

	/**
	 * Deliver a message.
	 * 
	 * @param type
	 * @param title
	 * @param details
	 */
	void message(NotificationType type, String title, String details);
}