package org.openmaji.implementation.rpc.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TestHandler {
	private static final Logger logger = Logger.getAnonymousLogger();

	private static long count = 0;
	
	private long instanceNum = count++;
	
	private String message = "initial message";
	
	public String getMessage() {
		logger.log(Level.INFO, "getting message: " + message + ". instance count is " + instanceNum);
		return message;
	}
	
	public String putMessage(String message) {
		logger.log(Level.INFO, "putting message. " + message);
		this.message = message;
		return message;
	}

	public String putMessages(Object[] messages) {
		logger.log(Level.INFO, "putting messages.");
		StringBuilder builder = new StringBuilder();
		for (Object msg : messages) {
			builder.append(msg);
		}
		
		this.message = builder.toString();
		
		return this.message;
	}
	
	public Map<String, Object> getMap() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("test", "hello");
		map.put("go away", "bonjour");
		return map;
	}
	
	public List<Map<String, Object>> getList() {
		List<Map<String, Object>> result = new ArrayList<Map<String,Object>>();
		result.add(getMap());
		result.add(getMap());
		
		return result;
	}
}
