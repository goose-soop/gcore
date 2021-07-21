package com.guillaumevdn.gcore.lib.reflection;

import java.lang.reflect.Field;

import com.guillaumevdn.gcore.lib.concurrency.RWHashMap;

/**
 * @author GuillaumeVDN
 */
public class ReflectionFakeEnum {

	private Class<?> fakeEnumClass;

	private ReflectionFakeEnum(Class<?> fakeEnumClass) throws Throwable {
		this.fakeEnumClass = fakeEnumClass;
	}

	// ----- get
	public Class<?> getFakeEnumClass() {
		return fakeEnumClass;
	}

	// ----- methods
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

	// ----- valuesCache
	private static RWHashMap<Integer, ReflectionFakeEnum> cache = new RWHashMap<>();

	public static ReflectionFakeEnum of(Class<?> enumClass) throws Throwable {
		// hash by class name
		ReflectionFakeEnum method = cache.get(enumClass.getName().hashCode());
		if (method == null) {
			cache.put(enumClass.getName().hashCode(), method = new ReflectionFakeEnum(enumClass));
		}
		return method;
	}

}
