package com.guillaumevdn.gcore.lib.reflection;

import java.util.HashMap;
import java.util.Map;

/**
 * @author GuillaumeVDN
 */
public class ReflectionEnum {

	private Class<?> enumClass;

	private ReflectionEnum(Class<?> enumClass) throws Throwable {
		this.enumClass = enumClass;
	}

	// get
	public Class<?> getEnumClass() {
		return enumClass;
	}

	// methods
	public ReflectionObject valueOf(String valueName) throws Throwable {
		for (Object value : enumClass.getEnumConstants()) {
			if (valueName.equalsIgnoreCase(ReflectionObject.of(value).invokeMethod("name").get())) {
				return ReflectionObject.of(value);
			}
		}
		throw new NoSuchFieldException(valueName);
	}

	public ReflectionObject safeValueOf(String valueName) throws Throwable {
		try {
			return valueOf(valueName);
		} catch (Throwable exception) {
			if (exception instanceof NoSuchFieldException && valueName.equals(exception.getMessage())) {
				return null;
			} else {
				throw exception;
			}
		}
	}

	public Object[] values() throws Throwable {
		return enumClass.getEnumConstants();
	}

	// cache
	private static Map<Class, ReflectionEnum> cache = new HashMap<>();

	public static ReflectionEnum of(Class<?> enumClass) throws Throwable {
		ReflectionEnum method = cache.get(enumClass);
		if (method == null) {
			cache.put(enumClass, method = new ReflectionEnum(enumClass));
		}
		return method;
	}

}
