package com.guillaumevdn.gcore.lib.reflection;

import com.guillaumevdn.gcore.lib.concurrency.RWHashMap;

/**
 * @author GuillaumeVDN
 */
public class ReflectionEnum {

	private Class<?> enumClass;

	private ReflectionEnum(Class<?> enumClass) throws Throwable {
		this.enumClass = enumClass;
	}

	// ----- get
	public Class<?> getEnumClass() {
		return enumClass;
	}

	// ----- methods
	public ReflectionObject valueOf(String valueName) throws Throwable {
		for (Object value : enumClass.getEnumConstants()) {
			if (valueName.equalsIgnoreCase(ReflectionObject.of(value).invokeMethod("name").get())) {
				return ReflectionObject.of(value);
			}
		}
		throw new NoSuchFieldException(valueName);
	}

	public ReflectionObject safeValueOf(String valueName) {
		try {
			return valueOf(valueName);
		} catch (Throwable exception) {
			return ReflectionObject.of(null);
		}
	}

	public Object[] values() throws Throwable {
		return enumClass.getEnumConstants();
	}

	// ----- valuesCache
	private static RWHashMap<Integer, ReflectionEnum> cache = new RWHashMap<>();

	public static ReflectionEnum of(Class<?> enumClass) throws Throwable {
		// hash by class name
		ReflectionEnum method = cache.get(enumClass.getName().hashCode());
		if (method == null) {
			cache.put(enumClass.getName().hashCode(), method = new ReflectionEnum(enumClass));
		}
		return method;
	}

}
