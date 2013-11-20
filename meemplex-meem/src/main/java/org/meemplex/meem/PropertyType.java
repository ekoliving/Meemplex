package org.meemplex.meem;

import java.util.Date;

public enum PropertyType {
	BOOLEAN,
	NUMBER,
	STRING,
	DATE,
	OBJECT;
	
	
	public static PropertyType typeOf(Object content) {
		if (content instanceof Boolean) {
			return BOOLEAN;
		}
		else if (content instanceof String) {
			return STRING;
		}
		else if (content instanceof Number) {
			return NUMBER;
		}
		else if (content instanceof Date) {
			return DATE;
		}
		else {
			return OBJECT;
		}
	}

	public static PropertyType type(Class<?> cls) {
		if (Boolean.class.isAssignableFrom(cls)) {
			return BOOLEAN;
		}
		else if (String.class.isAssignableFrom(cls)) {
			return STRING;
		}
		else if (Number.class.isAssignableFrom(cls)) {
			return NUMBER;
		}
		else if (Date.class.isAssignableFrom(cls)) {
			return DATE;
		}
		else {
			return OBJECT;
		}
	}
}
