package com.guillaumevdn.gcore.lib.serialization.gson;

import java.lang.reflect.Field;

import com.guillaumevdn.gcore.libs.com.google.gson.ExclusionStrategy;
import com.guillaumevdn.gcore.libs.com.google.gson.FieldAttributes;

// https://stackoverflow.com/questions/16476513/class-a-declares-multiple-json-fields

public class DuplicateFieldsExclusionStrategy implements ExclusionStrategy {

	@Override
	public boolean shouldSkipClass(Class<?> arg0) {
		return false;
	}

	@Override
	public boolean shouldSkipField(FieldAttributes fieldAttributes) {
		String fieldName = fieldAttributes.getName();
		Class<?> theClass = fieldAttributes.getDeclaringClass();
		return isFieldInSuperclass(theClass, fieldName);
	}

	private boolean isFieldInSuperclass(Class<?> subclass, String fieldName) {
		Class<?> superclass = subclass.getSuperclass();
		Field field;
		while (superclass != null) {
			field = getField(superclass, fieldName);
			if (field != null) {
				return true;
			}
			superclass = superclass.getSuperclass();
		}
		return false;
	}

	private Field getField(Class<?> theClass, String fieldName) {
		try {
			return theClass.getDeclaredField(fieldName);
		} catch (Throwable ignored) {
			return null;
		}
	}

}
