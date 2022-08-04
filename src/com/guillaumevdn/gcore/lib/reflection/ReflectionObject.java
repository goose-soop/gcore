package com.guillaumevdn.gcore.lib.reflection;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;

import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.object.ObjectUtils;
import com.guillaumevdn.gcore.lib.plugin.PluginUtils;
import com.guillaumevdn.gcore.lib.string.StringUtils;

/**
 * @author GuillaumeVDN
 */
public class ReflectionObject {

	private Object object;

	private ReflectionObject(Object object) {
		this.object = object;
	}

	// ----- get
	public Object justGet() {
		return object;
	}

	public <T> T get() {
		return (T) object;
	}

	public <T> T get(Class<T> clazz) {
		return ObjectUtils.cast(object, clazz);
	}

	public <T> T orElse(T def) {
		return object != null ? get() : def;
	}

	public <T> T orNull() {
		return orElse((T) null);
	}

	public ReflectionObject orElse(ReflectionObject other) {
		return object != null ? this : other;
	}

	// ----- set
	/** @return this object, for chaining convenience because it's cool */
	public ReflectionObject set(Object object) {
		this.object = object;
		return this;
	}

	// ----- methods
	public ReflectionObject invokeMethod(String name, Object... params) throws Throwable {
		if (object == null) {
			throw new IllegalStateException("can't invoke method " + name + "(" + StringUtils.toTextString(", ", CollectionUtils.asList(params).stream().map(param -> param == null ? "null" : param.getClass().getSimpleName())) + ") on a null object");
		}
		return Reflection.invokeMethod(object.getClass(), name, object, params);
	}

	public ReflectionObject invokeMethodArray(String name, Object[] params) throws Throwable {
		if (object == null) {
			throw new IllegalStateException("can't invoke method " + name + "(" + params.getClass() + ") on a null object");
		}
		List list = new ArrayList<>();
		list.add(params);
		return Reflection.invokeMethod(object.getClass(), name, object, list);
	}

	/** @return this object, for chaining convenience because it's cool */
	public ReflectionObject invokeVoidMethod(String name, Object... params) throws Throwable {
		if (object == null) {
			throw new IllegalStateException("can't invoke void method " + name + "(" + StringUtils.toTextString(", ", CollectionUtils.asList(params).stream().map(param -> param == null ? "null" : param.getClass().getSimpleName())) + ") on a null object");
		}
		invokeMethod(name, params);
		return this;
	}

	public ReflectionObject getField(String name) throws Throwable {
		return new ReflectionField(object.getClass(), name).retrieve(object);
	}

	/** @return this object, for chaining convenience because it's cool */
	public ReflectionObject setField(String name, Object value) throws Throwable {
		new ReflectionField(object.getClass(), name).set(object, value);
		return this;
	}

	public void printFields() {
		printFields(false);
	}

	public void printFields(boolean withValues) {
		String result = "";
		if (object == null) {
			result = "null";
		} else {
			Field[] fields = object.getClass().getDeclaredFields();
			result = "" + fields.length;
			if (fields.length > 0) {
				result += "\n> " + StringUtils.toTextString("\n> ", Arrays.stream(fields).map(f -> {
					String r = f.isAccessible() ? "public" : "private";
					r += " " + f.getType().getName();
					r += " " + f.getName();
					if (withValues) {
						if (!f.isAccessible()) f.setAccessible(true);
						r += " = ";
						try {
							r += f.get(object);
						} catch (IllegalArgumentException | IllegalAccessException exception) {
							r += "?";
						}
					}
					return r;
				}));
			}
		}
		Bukkit.getLogger().info("Fields of " + (object == null ? "null" : object.getClass().getName() + " : ") + result);
	}

	// ----- object
	@Override
	public String toString() {
		return "ReflectionObject{" + (object == null ? "null" : "class=" + object.getClass().getName() + ",value=" + String.valueOf(object)) + "}";
	}

	// ----- static
	public static ReflectionObject of(Object obj) {
		ReflectionObject inst = ObjectUtils.castOrNull(obj, ReflectionObject.class);
		return inst != null ? inst : new ReflectionObject(obj);
	}

	public static ReflectionObject ofOrNull(Object obj) {
		return obj == null ? null : of(obj);
	}

	public static ReflectionObject ofPlugin(String pluginName) {
		return new ReflectionObject(PluginUtils.getPlugin(pluginName));
	}

}
