package com.guillaumevdn.gcore.lib.element.type.basic;

import java.util.Collections;
import java.util.List;

import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.concurrency.RWHashMap;
import com.guillaumevdn.gcore.lib.concurrency.RWWeakHashMap;
import com.guillaumevdn.gcore.lib.element.struct.Element;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.string.Text;

/**
 * @author GuillaumeVDN
 */
public abstract class ElementEnum<E extends Enum<E>> extends ElementAbstractEnum<E> {

	private static RWWeakHashMap<Object, RWHashMap<Class<?>, List<?>>> valueCache = new RWWeakHashMap<>();
	private List<E> values;

	public ElementEnum(Class<E> enumClass, Element parent, String id, Need need, Text editorDescription) {
		super(enumClass, true, parent, id, need, editorDescription);

		RWHashMap<Class<?>, List<?>> cache = valueCache.computeIfAbsent(getSuperElement().getPlugin().getLifecycleReference(), __ -> new RWHashMap<>());
		values = (List<E>) cache.get(enumClass);
		if (values == null) {
			values = CollectionUtils.asList(enumClass.getEnumConstants());
			values.sort(Enum::compareTo);
			values = Collections.unmodifiableList(values);
			cache.put(enumClass, values);
		}
	}

	@Override
	public final List<E> getValues() {
		return values;
	}

}
