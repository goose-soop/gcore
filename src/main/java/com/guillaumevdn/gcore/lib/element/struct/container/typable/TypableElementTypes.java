package com.guillaumevdn.gcore.lib.element.struct.container.typable;

import java.util.Collection;
import java.util.Collections;
import java.util.function.BiConsumer;

import com.guillaumevdn.gcore.lib.concurrency.RWLowerCaseHashMap;

/**
 * @author GuillaumeVDN
 */
public abstract class TypableElementTypes<T extends TypableElementType> {

	private final Class<T> typeClass;
	private final RWLowerCaseHashMap<T> types = new RWLowerCaseHashMap<>(5, 1f);

	public TypableElementTypes(Class<T> typeClass) {
		this.typeClass = typeClass;
	}

	// ----- get
	public final Class<T> getTypeClass() {
		return typeClass;
	}

	public final Collection<T> values() {
		return Collections.unmodifiableCollection(types.copyValues());
	}

	public void iterateValues(BiConsumer<String, T> consumer) {
		types.forEach(consumer);
	}

	public abstract T defaultValue();

	public final T safeValueOf(String id) {
		return types.get(id);
	}

	public final T valueOf(String id) throws IllegalArgumentException {
		T value = safeValueOf(id);
		if (value == null) throw new IllegalArgumentException("there's no type with id " + id);
		return value;
	}

	// ----- set
	public final <TT extends T> TT register(TT type) {
		types.put(type.getId(), type);
		return type;
	}

	public final void unregister(T type) {
		if (type != null) {
			types.remove(type.getId());
		}
	}

	public final void unregister(String id) {
		if (id != null) {
			types.remove(id);
		}
	}

}
