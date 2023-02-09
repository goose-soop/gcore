package com.guillaumevdn.gcore.lib.reflection;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.concurrency.RWHashMap;
import com.guillaumevdn.gcore.lib.object.ObjectUtils;
import com.guillaumevdn.gcore.lib.string.StringUtils;

/**
 * @author GuillaumeVDN
 */
public class ReflectionMethod {

	private Method method = null;

	private ReflectionMethod(Class<?> original, String name, List<Class<?>> params) throws Throwable {
		List<Class<?>> classes = CollectionUtils.asList(original);
		Set<Class<?>> explored = new HashSet<>();
		// explore classes
		exploreClasses: while (method == null && !classes.isEmpty()) {
			Class<?> clazz = classes.remove(0);
			exploreMethods: for (Method meth : clazz.getDeclaredMethods()) {
				// not the same name
				if (!meth.getName().equals(name)) {
					continue;
				}
				// params don't match
				Parameter[] methParams = meth.getParameters();
				if (methParams.length != params.size()) {
					continue;
				}
				int i = -1;
				for (Class<?> param : params) {
					if (param != null && !ObjectUtils.instanceOf(param, methParams[++i].getType())) {
						continue exploreMethods;
					}
				}
				// found one
				method = meth;
				break exploreClasses;
			}
			// set parent class to explore
			Class<?> parent = clazz.getSuperclass();
			if (parent != null && canCheck(parent)) {
				classes.add(parent);
			}
			for (Class<?> inter : original.getInterfaces()) {
				if (inter != null && canCheck(inter) && explored.add(inter)) {
					classes.add(inter);
				}
			}
		}
		if (method == null) {
			Reflection.logAndRethrowError(new NoSuchMethodException(),
					"Class " + original
					+ "\nName '" + name + "'"
					+ "\nParameters '" + StringUtils.toTextString(", ", CollectionUtils.asList(params).stream().map(param -> param == null ? "null" : param.getName())) + "'"
					);
		}
	}

	private static boolean canCheck(Class<?> clazz) {
		return clazz != null && !clazz.isPrimitive() && !clazz.equals(Object.class) /*&& !clazz.equals(Enum.class) actually allow enum classes, we want sometimes to get the name() method*/;
	}

	// ----- get
	public Method getMethod() {
		return method;
	}

	// ----- methods
	public ReflectionObject invoke(Object object, Collection params) throws Throwable {
		return invoke(object, params.toArray(new Object[params.size()]));
	}

	public ReflectionObject invoke(Object object, Object... params) throws Throwable {
		if (!method.isAccessible()) {
			method.setAccessible(true);
		}
		try {
			return ReflectionObject.of(method.invoke(object, params));
		} catch (IllegalArgumentException exception) {
			Reflection.logAndRethrowError(exception,
					"Class " + method.getDeclaringClass()
					+ "\nName " + method.getName()
					+ "\nParameters '" + StringUtils.toTextString(", ", Stream.of(params))
					+ "\nMethod parameters '" + StringUtils.toTextString(", ", CollectionUtils.asList(method.getParameters()).stream().map(param -> param.getType())) + "'"
					);
			return null;
		}
	}

	// ----- valuesCache
	private static RWHashMap<Integer, ReflectionMethod> cache = new RWHashMap<>(10, 1f);

	public static ReflectionMethod of(Class<?> original, String name, List<Class<?>> params) throws Throwable {
		// hash by class name
		int hash = original.getName().hashCode();
		hash = hash * 31 + name.hashCode();
		for (Class<?> param : params) hash = 31 * hash + (param == null ? 0 : param.getName().hashCode());
		// construct
		ReflectionMethod method = cache.get(hash);
		if (method == null) {
			cache.put(hash, method = new ReflectionMethod(original, name, params));
		}
		return method;
	}

}
