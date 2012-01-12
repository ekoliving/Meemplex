package org.meemplex.internet.gwt.client.widget;

import org.meemplex.internet.gwt.client.notification.NotificationService;
import org.meemplex.internet.gwt.client.notification.NotificationType;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

public class NotificationWidget extends Composite implements NotificationService {

	private FlowPanel panel;
	
	private Label titleLabel;
	
	private Label detailsLabel;
	
	public NotificationWidget() {
		initWidget(getPanel());
	}
	
	
	@Override
	public void message(NotificationType type, String title, String details) {
		getTitleLabel().setText(title);
		getDetailsLabel().setText(details);
	}
	
	private FlowPanel getPanel() {
		if (panel == null) {
			panel = new FlowPanel();
			panel.add(getTitleLabel());
			panel.add(getDetailsLabel());
		}
		return panel;
	}
	
	private Label getTitleLabel() {
		if (titleLabel == null) {
			titleLabel = new Label();
		}
		return titleLabel;
	}

	private Label getDetailsLabel() {
		if (detailsLabel == null) {
			detailsLabel = new Label();
		}
		return detailsLabel;
	}
}
