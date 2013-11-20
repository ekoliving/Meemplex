package org.openmaji.implementation.server.utility;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ClassHelper {

	/**
	 * return field by given name
	 */
	public static Field getField(Class<?> cls, String name) throws NoSuchFieldException {
		if (cls == null) {
			throw new NoSuchFieldException("No field: " + name);
		}
		try {
			return cls.getDeclaredField(name);
		}
		catch (NoSuchFieldException e) {
			return getField(cls.getSuperclass(), name);
		}
	}
	
	
	/**
	 * return ALL declared fields in a Class.
	 * 
	 * TODO handle overridding fields in subclasses
	 * 
	 * @param cls
	 * @return
	 */
	public static List<Field> getFields(Class<?> cls) {
		List<Field> fields = new ArrayList<Field>();
//		HashMap<String, Field> fieldMap = new HashMap<String, Field>();
		
		if (cls == null) {
			return fields;
		}
		else {
			Field[] fieldArr = cls.getDeclaredFields();
//			for (Field f : fieldArr) {
//				fieldMap.put(f.getName(), f);
//			}
			fields.addAll(Arrays.asList(fieldArr));
			fields.addAll(getFields(cls.getSuperclass()));
		}
		return fields;
	}
	
	/**
	 * Return all fields for this class that have an annotation of a specific type.
	 * 
	 * @param cls
	 * @param annotation
	 * @return
	 */
	public static List<Field> getAnnotatedFields(Class<?> cls, Class<? extends Annotation> annotationClass) {
		List<Field> annotatedFields = new ArrayList<Field>();
		List<Field> fields = getFields(cls);
		for (Field field : fields) {
			Annotation annotation = field.getAnnotation(annotationClass);
			if (annotation != null) {
				field.setAccessible(true);		// make the fields accessible
				annotatedFields.add(field);
			}
		}
		return annotatedFields;
	}
	
	public static List<Method> getMethods(Class<?> cls) {
		List<Method> method = new ArrayList<Method>();
		if (cls == null) {
			return method;
		}
		else {
			Method[] methods = cls.getDeclaredMethods();
			method.addAll(Arrays.asList(methods));
			method.addAll(getMethods(cls.getSuperclass()));
		}
		return method;
	}

	/**
	 * Get all methods that have been Annotated with the given Annotation class.
	 * 
	 * @param cls
	 * @param annotationClass
	 * @return
	 */
	public static List<Method> getAnnotatedMethods(Class<?> cls, Class<? extends Annotation> annotationClass) {
		List<Method> annotatedMethods = new ArrayList<Method>();
		List<Method> methods = getMethods(cls);
		for (Method method : methods) {
			Annotation annotation = method.getAnnotation(annotationClass);
			if (annotation != null) {
				method.setAccessible(true);		// make the method accessible
				annotatedMethods.add(method);
			}
		}
		return annotatedMethods;
	}
}
