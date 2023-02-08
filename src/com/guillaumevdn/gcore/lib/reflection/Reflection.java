package com.guillaumevdn.gcore.lib.reflection;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.compatibility.Version;
import com.guillaumevdn.gcore.lib.concurrency.RWHashMap;
import com.guillaumevdn.gcore.lib.concurrency.RWHashSet;
import com.guillaumevdn.gcore.lib.function.ThrowableConsumer;
import com.guillaumevdn.gcore.lib.logic.ComparisonType;
import com.guillaumevdn.gcore.lib.object.ObjectUtils;
import com.guillaumevdn.gcore.lib.string.StringUtils;

/**
 * @author GuillaumeVDN
 */
public final class Reflection {

	// ----- class
	private static RWHashMap<Integer, Class> cache = new RWHashMap<>(10, 1f);

	private static Class getClassMaybeCached(String path) throws Throwable {
		int hash = path.hashCode();
		Class clazz = cache.get(hash);
		if (clazz == null) {
			clazz = Class.forName(path);
			cache.put(hash, clazz);
		}
		return clazz;
	}

	public static Class classOrNull(String path) {
		try {
			return getClassMaybeCached(path);
		} catch (Throwable exception) {
			return null;
		}
	}

	public static Class getNmsClass(String path) throws Throwable {
		return getClassMaybeCached(Version.CURRENT.getNMSPackage() + "." + path);
	}

	public static Class getCraftbukkitClass(String path) throws Throwable {
		return getClassMaybeCached(Version.CURRENT.getCraftbukkitPackage() + "." + path);
	}

	public static Class safeArrayClass(Class typeClass, int depth) {
		try {
			return getArrayClass(typeClass, depth);
		} catch (Throwable exception) {
			exception.printStackTrace();
			return null;
		}
	}

	public static Class getArrayClass(Class typeClass) throws Throwable {
		return getArrayClass(typeClass, 1);
	}

	public static Class getArrayClass(Class typeClass, int depth) throws Throwable {
		return getClassMaybeCached(StringUtils.repeatString("[", depth) + "L" + typeClass.getName() + ";");
	}

	public static <T> T[] createArray(Class<T> type, Collection<?> content) {
		T[] array = (T[]) Array.newInstance(type, content.size());
		int i = -1;
		for (Object elem : content) {
			array[++i] = (T) elem;
		}
		return array;
	}

	// ----- version
	public static <T> T getForThisVersion(Object... objects) {
		return getForThisVersion(CollectionUtils.asMap(objects));
	}

	public static <T> T getForThisVersion(Map<Version, T> map) {
		try {
			T current = null;
			for (Version version : CollectionUtils.asSortedList(map.keySet(), Version::compareTo)) {
				if (Version.CURRENT.isMoreOrEqualsTo(version)) {
					current = map.get(version);
				}
			}
			return current;
		} catch (Throwable exception) {
			exception.printStackTrace();
			return null;
		}
	}

	// ----- enum
	public static ReflectionEnum getEnum(String path) throws Throwable {
		return ReflectionEnum.of(getClassMaybeCached(path));
	}

	public static ReflectionEnum getNmsEnum(String path) throws Throwable {
		return ReflectionEnum.of(getNmsClass(path));
	}

	public static ReflectionFakeEnum getFakeEnum(String path) throws Throwable {
		return ReflectionFakeEnum.of(getClassMaybeCached(path));
	}

	public static ReflectionFakeEnum getNmsFakeEnum(String path) throws Throwable {
		return ReflectionFakeEnum.of(getNmsClass(path));
	}

	// ----- method
	public static <T> boolean isOverridenSafe(Class<? extends T> subclass, Class<T> superclass, String methodName, Class... methodParams) {
		try {
			return isOverriden(subclass, superclass, methodName, methodParams);
		} catch (Throwable ignored) {
			return false;
		}
	}

	public static <T> boolean isOverriden(Class<? extends T> subclass, Class<T> superclass, String methodName, Class... methodParams) throws Throwable {
		Class<?> declaring = subclass.getMethod(methodName, methodParams).getDeclaringClass();
		return !declaring.equals(superclass) && ObjectUtils.instanceOf(declaring, superclass);
	}

	public static ReflectionObject invokeNmsMethod(String path, String name, Object object, Object... params) throws Throwable {
		return invokeMethod(getNmsClass(path), name, object, params);
	}

	public static ReflectionObject invokeCraftbukkitMethod(String path, String name, Object object, Object... params) throws Throwable {
		return invokeMethod(getCraftbukkitClass(path), name, object, params);
	}

	public static ReflectionObject invokeMethod(String path, String name, Object object, Object... params) throws Throwable {
		return invokeMethod(getClassMaybeCached(path), name, object, params);
	}

	public static ReflectionObject invokeMethod(Class clazz, String name, Object object, Object... params) throws Throwable {
		return getMethodForParams(clazz, name, params).invoke(object, params);
	}

	public static ReflectionObject invokeMethod(Class clazz, String name, Object object, Collection<?> params) throws Throwable {
		return getMethod(clazz, name, params.stream().map(param -> param == null ? null : param.getClass()).collect(Collectors.toList())).invoke(object, params);
	}

	public static ReflectionMethod getMethodForParams(Class clazz, String name, Object... params) throws Throwable {
		return ReflectionMethod.of(clazz, name, Stream.of(params).map(param -> param == null ? null : param.getClass()).collect(Collectors.toList()));
	}

	public static ReflectionMethod getMethod(Class clazz, String name, List<Class<?>> params) throws Throwable {
		return ReflectionMethod.of(clazz, name, params);
	}

	// ----- constructor
	public static ReflectionObject newNmsInstance(String path, Object... params) throws Throwable {
		return newInstance(getNmsClass(path), params);
	}

	public static ReflectionObject newInstance(String path, Object... params) throws Throwable {
		return newInstance(getClassMaybeCached(path), params);
	}

	public static ReflectionObject newInstance(Class<?> clazz, Object... params) throws Throwable {
		return ReflectionConstructor.of(clazz, getClasses(params)).newInstance(params);
	}

	public static ReflectionParams params(boolean condition, Object... params) {
		return new ReflectionParams().setIf(condition, params);
	}

	public static ReflectionParams params(ComparisonType comparison, Version version, Object... params) {
		return new ReflectionParams().setIf(comparison.compare(Version.CURRENT, version), params);
	}

	public static Class[] getClasses(Object... params) {
		Class[] result = new Class[params.length];
		for (int i = 0; i < params.length; ++i) {
			result[i] = params[i] == null ? null : params[i].getClass();
		}
		return result;
	}

	// ----- field
	public static ReflectionField getNmsField(String path, String name) throws Throwable {
		return ReflectionField.of(getNmsClass(path), name);
	}

	public static ReflectionField getField(String path, String name) throws Throwable {
		return ReflectionField.of(getClassMaybeCached(path), name);
	}

	// ----- block
	public static ReflectionObject processBlockData(Block block, ThrowableConsumer<ReflectionObject> dataProcessor) throws Throwable {
		return processBlockData(ReflectionObject.of(block), dataProcessor);
	}

	public static ReflectionObject processBlockData(ReflectionObject block, ThrowableConsumer<ReflectionObject> dataProcessor) throws Throwable {
		ReflectionObject data = block.invokeMethod("getBlockData");
		dataProcessor.accept(data);
		block.invokeMethod("setBlockData", (Object) data.get());
		return block;
	}

	// ----- packet
	public static ReflectionObject getPlayerConnection(Player player) throws Throwable {
		return ReflectionObject.of(player).invokeMethod("getHandle").getField(Version.ATLEAST_1_17 ? "b" : "playerConnection");
	}

	public static void sendNmsPacket(Player player, String path, Object... params) throws Throwable {
		sendNmsPacket(player, newNmsInstance(path, params).get());
	}

	public static void sendNmsPacket(Player player, Object packet) throws Throwable {
		getPlayerConnection(player).invokeMethod(Version.ATLEAST_1_18 ? "a" : "sendPacket", packet);
	}

	public static void sendNmsPacket(Collection<Player> players, String path, Object... params) throws Throwable {
		sendNmsPacket(players, newNmsInstance(path, params).get());
	}

	public static void sendNmsPacket(Collection<Player> players, Object packet) throws Throwable {
		if (players instanceof RWHashSet) {
			((RWHashSet<Player>) players).forEachThrowable(player -> {
				sendNmsPacket(player, packet);
			});
		} else {
			for (Player player : players) {
				sendNmsPacket(player, packet);
			}
		}
	}

	// ----- error
	public static void logAndRethrowError(Throwable exception, String log) throws Error {
		final String id = StringUtils.generateRandomAlphanumericString(10);
		Bukkit.getLogger().info("---- Reflection error " + id + " ----\n" + log);
		exception.printStackTrace(System.out);
		System.out.flush();
		throw new Error(id, exception);
	}

	public static String stackTraceToString(Throwable exception) {
		try (StringWriter writer = new StringWriter()) {
			exception.printStackTrace(new PrintWriter(writer));
			return writer.toString();
		} catch (IOException ignored) {
			return null;
		}
	}

	public static boolean stackTraceContainsIgnoreCase(Throwable exception, String... containsOne) {
		String trace = stackTraceToString(exception).toLowerCase();
		return Arrays.stream(containsOne).anyMatch(contains -> trace.contains(contains.toLowerCase()));
	}

}
