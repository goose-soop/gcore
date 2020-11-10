package com.guillaumevdn.gcore.lib.reflection;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.compatibility.Version;
import com.guillaumevdn.gcore.lib.function.ThrowableConsumer;
import com.guillaumevdn.gcore.lib.logic.ComparisonType;
import com.guillaumevdn.gcore.lib.object.ObjectUtils;
import com.guillaumevdn.gcore.lib.string.StringUtils;

/**
 * @author GuillaumeVDN
 */
public final class Reflection {

	// class
	public static String getNmsPackage() {
		return "net.minecraft.server." + Version.CURRENT.getPackageName();
	}

	public static String getCraftbukkitPackage() {
		return "org.bukkit.craftbukkit." + Version.CURRENT.getPackageName();
	}

	public static Class getNmsClass(String path) throws Throwable {
		return Class.forName(getNmsPackage() + "." + path);
	}

	public static Class getCraftbukkitClass(String path) throws Throwable {
		return Class.forName(getCraftbukkitPackage() + "." + path);
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
		return Class.forName(StringUtils.repeatString("[", depth) + "L" + typeClass.getName() + ";");
	}

	// version
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

	// enum
	public static ReflectionEnum getEnum(String path) throws Throwable {
		return ReflectionEnum.of(Class.forName(path));
	}

	public static ReflectionEnum getNmsEnum(String path) throws Throwable {
		return ReflectionEnum.of(getNmsClass(path));
	}

	public static ReflectionFakeEnum getFakeEnum(String path) throws Throwable {
		return ReflectionFakeEnum.of(Class.forName(path));
	}

	public static ReflectionFakeEnum getNmsFakeEnum(String path) throws Throwable {
		return ReflectionFakeEnum.of(getNmsClass(path));
	}

	// method
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
		return invokeMethod(Class.forName(path), name, object, params);
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

	// constructor
	public static ReflectionObject newNmsInstance(String path, Object... params) throws Throwable {
		return newInstance(getNmsClass(path), params);
	}

	public static ReflectionObject newInstance(String path, Object... params) throws Throwable {
		return newInstance(Class.forName(path), params);
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

	// field
	public static ReflectionField getNmsField(String path, String name) throws Throwable {
		return new ReflectionField(getNmsClass(path), name);
	}

	public static ReflectionField getField(String path, String name) throws Throwable {
		return new ReflectionField(Class.forName(path), name);
	}

	// block
	public static ReflectionObject processBlockData(Block block, ThrowableConsumer<ReflectionObject> dataProcessor) throws Throwable {
		return processBlockData(ReflectionObject.of(block), dataProcessor);
	}

	public static ReflectionObject processBlockData(ReflectionObject block, ThrowableConsumer<ReflectionObject> dataProcessor) throws Throwable {
		ReflectionObject data = block.invokeMethod("getBlockData");
		dataProcessor.accept(data);
		block.invokeMethod("setBlockData", data.get());
		return block;
	}

	// packet
	public static ReflectionObject getPlayerConnection(Player player) throws Throwable {
		return ReflectionObject.of(player).invokeMethod("getHandle").getField("playerConnection");
	}

	public static void sendNmsPacket(Player player, String path, Object... params) throws Throwable {
		sendNmsPacket(player, newNmsInstance(path, params).get());
	}

	public static void sendNmsPacket(Player player, Object packet) throws Throwable {
		getPlayerConnection(player).invokeMethod("sendPacket", packet);
	}

	public static void sendNmsPacket(Collection<Player> players, String path, Object... params) throws Throwable {
		sendNmsPacket(players, newNmsInstance(path, params).get());
	}

	public static void sendNmsPacket(Collection<Player> players, Object packet) throws Throwable {
		for (Player player : players) {
			sendNmsPacket(player, packet);
		}
	}

}
