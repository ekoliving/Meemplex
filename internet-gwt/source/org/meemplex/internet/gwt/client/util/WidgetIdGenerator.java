package org.meemplex.internet.gwt.client.util;

public class WidgetIdGenerator {

	private static int count = 0;
	
	public static String getId() {
		return "widget" + count++;
	}
}
