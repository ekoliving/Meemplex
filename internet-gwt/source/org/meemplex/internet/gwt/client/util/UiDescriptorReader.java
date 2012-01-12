package org.meemplex.internet.gwt.client.util;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.user.client.ui.Widget;

public class UiDescriptorReader {

	public static List<Widget> createWidgets(String uiString) {
		List<Widget> widgets = new ArrayList<Widget>();
		
		JSONObject uiObject = JSONParser.parseLenient(uiString).isObject();
		JSONArray widgetDescs = uiObject.get("widgets").isArray();
		for (int i=0; i<widgetDescs.size(); i++) {
			JSONObject widgetDesc = widgetDescs.get(i).isObject();
			Widget w = createWidget(widgetDesc);
			widgets.add(w);
		}
		
		return widgets;
	}
	
	
	public static Widget createWidget(JSONObject widgetDesc) {
		String type = widgetDesc.get("type").isString().stringValue();
		
		return null;
	}
}
