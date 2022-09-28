package com.guillaumevdn.gcore.lib.reflection.field;

import java.util.Map.Entry;
import java.util.function.Consumer;

import com.guillaumevdn.gcore.lib.collection.CollectionUtils;

/**
 * @author GuillaumeVDN
 */
public class FieldMapper<T> {

	private T value = null;

	public FieldMapper() {
	}

	public T get() {
		return value;
	}

	public T orElse(T field) {
		if (this.value == null) {
			this.value = field;
		}
		return (T) value;
	}

	public FieldMapper<T> setIf(boolean condition, T field) {
		if (condition) {
			this.value = field;
		}
		return this;
	}

	public FieldMapper<T> orSetIf(boolean condition, T field) {
		if (value == null && condition) {
			this.value = field;
		}
		return this;
	}

	public void ifPresent(Consumer<T> consumer) {
		if (value != null) {
			consumer.accept(value);
		}
	}

	public static <E> FieldMapper<E> of() {
		return new FieldMapper<E>();
	}

	public static <E> FieldMapper<E> of(Class<E> clazz) {
		return new FieldMapper<E>();
	}

	public static <E> FieldMapper<E> createIf(boolean condition, E field) {
		return new FieldMapper<E>().setIf(condition, field);
	}

	public static <E> FieldMapper<E> fromMap(Class<E> clazz, Object... mapElements) {
		final FieldMapper<E> mapper = new FieldMapper<E>();
		for (Entry<Object, Object> entry : CollectionUtils.asMap(mapElements).entrySet()) {
			if ((boolean) entry.getKey()) {
				mapper.value = (E) entry.getValue();
				break;
			}
		}
		return mapper;
	}

}
