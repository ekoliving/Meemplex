package org.meemplex.internet.gwt.client.widget;

import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;

public class ClockWidget extends Composite {
	DateTimeFormat dtf = DateTimeFormat.getFormat("h:mm a");
	
	private Timer t = new Timer() {
		public void run() {
			updateTime();
		}
	};
	
	private Label timeLabel;

	public ClockWidget() {
		initWidget(getTimeLabel());
		start();
    }
	
	public void start() {
	    // make time update
		updateTime();
	    t.scheduleRepeating(30000);
	}
	
	public void stop() {
		t.cancel();
	}
	
	private Label getTimeLabel() {
		if (timeLabel == null) {
			timeLabel = new Label();
		}
		return timeLabel;
	}
	
	private void updateTime() {
		getTimeLabel().setText( dtf.format(new Date()) );
	}

}
