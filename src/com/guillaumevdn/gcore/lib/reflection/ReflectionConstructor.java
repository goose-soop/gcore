package com.guillaumevdn.gcore.lib.reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.guillaumevdn.gcore.lib.object.ObjectUtils;
import com.guillaumevdn.gcore.lib.string.StringUtils;

/**
 * @author GuillaumeVDN
 */
public class ReflectionConstructor {

	private Constructor constructor = null;

	private ReflectionConstructor(Class<?> clazz, Class... params) throws Throwable {
		// direct
		try {
			constructor = clazz.getDeclaredConstructor(params);
		} catch (NoSuchMethodException exception) {}
		// indirect
		if (constructor == null) {
			exploreConstructors: for (Constructor meth : clazz.getDeclaredConstructors()) {
				Parameter[] methParams = meth.getParameters();
				if (methParams.length != params.length) continue;
				for (int i = 0; i < params.length; ++i) {
					if (params[i] != null && !ObjectUtils.instanceOf(params[i], methParams[i].getType())) {
						continue exploreConstructors;
					}
				}
				// found one
				constructor = meth;
				break;
			}
		}
		if (constructor == null) throw new NoSuchMethodException("Class " + clazz + ", params " + StringUtils.toTextString(", ", (Object[]) params));
	}

	// get
	public Constructor getConstructor() {
		return constructor;
	}

	// methods
	public ReflectionObject newInstance(Object... params) throws Throwable {
		try {
			if (!constructor.isAccessible()) constructor.setAccessible(true);
			Object result = constructor.newInstance(params);
			return result == null ? null : ReflectionObject.of(result);
		} catch (Throwable exception) {
			throw new InstantiationException("Class " + constructor.getDeclaringClass() + ", params " + StringUtils.toTextString(", ", (Object[]) params) + ", constructor params " + StringUtils.toTextString(", ", (Object[]) constructor.getParameters()));
		}
	}

	// cache
	private static Map<Integer, ReflectionConstructor> cache = new HashMap<>();

	public static ReflectionConstructor of(Class<?> clazz, Class... params) throws Throwable {
		int hash = Objects.hash(clazz, params);
		ReflectionConstructor method = cache.get(hash);
		if (method == null) {
			cache.put(hash, method = new ReflectionConstructor(clazz, params));
		}
		return method;
	}

}
