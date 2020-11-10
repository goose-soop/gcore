package com.guillaumevdn.gcore.lib.reflection;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * @author GuillaumeVDN
 */
public class ReflectionFakeEnum {

	private Class<?> fakeEnumClass;

	private ReflectionFakeEnum(Class<?> fakeEnumClass) throws Throwable {
		this.fakeEnumClass = fakeEnumClass;
	}

	// get
	public Class<?> getFakeEnumClass() {
		return fakeEnumClass;
	}

	// methods
	public ReflectionObject valueOf(String valueName) throws Throwable {
		for (Field value : fakeEnumClass.getDeclaredFields()) {
			if (valueName.equalsIgnoreCase(value.getName())) {
				return ReflectionObject.of(value.get(null));
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
		return fakeEnumClass.getEnumConstants();
	}

	// cache
	private static Map<Class, ReflectionFakeEnum> cache = new HashMap<>();

	public static ReflectionFakeEnum of(Class<?> enumClass) throws Throwable {
		ReflectionFakeEnum method = cache.get(enumClass);
		if (method == null) {
			cache.put(enumClass, method = new ReflectionFakeEnum(enumClass));
		}
		return method;
	}

}
