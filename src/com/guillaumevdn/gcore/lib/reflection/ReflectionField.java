package com.guillaumevdn.gcore.lib.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import com.guillaumevdn.gcore.libs.com.google.gson.internal.JavaVersion;

/**
 * @author GuillaumeVDN
 */
public class ReflectionField {

	private Field field = null;

	ReflectionField(Class<?> clazz, String name) throws Throwable {
		Class<?> original = clazz;
		while (field == null && (clazz != null && !clazz.isPrimitive() && !clazz.equals(Object.class))) {
			try {
				field = clazz.getDeclaredField(name);
				if (field != null) break;
			} catch (NoSuchFieldException exception) {}
			clazz = clazz.getSuperclass();
		}
		if (field == null) {
			Reflection.logAndRethrowError(new NoSuchFieldException(),
					"Class " + original
					+ "\nName '" + name + "'"
					);
		}
	}

	// ----- get
	public Field getField() {
		return field;
	}

	// ----- methods
	public ReflectionObject retrieve(Object object) throws Throwable {
		if (!field.isAccessible()) field.setAccessible(true);
		Object result = field.get(object);
		return result == null ? null : ReflectionObject.of(result);
	}

	/** @return this object, for chaining convenience because it's cool */
	public ReflectionField set(Object object, Object value) throws Throwable {
		try {
			if (!field.isAccessible()) {
				field.setAccessible(true);
			}
			if (Modifier.isFinal(field.getModifiers())) {
				if (JavaVersion.getMajorJavaVersion() >= 16) {
					Reflection.logAndRethrowError(new Error("can't set final fields in Java 16+ with reflection, find a workaround"),
							"Class " + field.getDeclaringClass()
							+ "\nName '" + field.getName() + "'"
							+ "\nValue '" + value + "'"
							+ "\nValue class '" + (value == null ? "null" : value.getClass()) + "'"
							);
				} else if (JavaVersion.getMajorJavaVersion() >= 12) {
					ReflectionObject lookup = Reflection.invokeMethod(java.lang.invoke.MethodHandles.class, "privateLookupIn", null, Field.class, java.lang.invoke.MethodHandles.lookup());
					ReflectionObject modifiers = lookup.invokeMethod("findVarHandle", Field.class, "modifiers", int.class);
					modifiers.invokeMethod("set", field.getModifiers() & ~Modifier.FINAL);
				} else {
					Field modifiersField = Field.class.getDeclaredField("modifiers");
					modifiersField.setAccessible(true);
					modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
				}
			}
			field.set(object, value);
		} catch (Throwable throwable) {
			Reflection.logAndRethrowError(throwable,
					"Class " + field.getDeclaringClass()
					+ "\nName '" + field.getName() + "'"
					+ "\nValue '" + value + "'"
					+ "\nValue class '" + (value == null ? "null" : value.getClass()) + "'"
					);
		}
		return this;
	}

}
