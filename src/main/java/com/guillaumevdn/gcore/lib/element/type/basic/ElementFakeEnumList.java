package com.guillaumevdn.gcore.lib.element.type.basic;

import java.util.Collections;
import java.util.List;
import java.util.WeakHashMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.guillaumevdn.gcore.lib.element.struct.Element;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.string.Text;

/**
 * @author GuillaumeVDN
 */
public abstract class ElementFakeEnumList<E> extends ElementAbstractEnumList<E> {

	public ElementFakeEnumList(Class<E> typeClass, Element parent, String id, Need need, Text editorDescription) {
		super(typeClass, true, parent, id, need, editorDescription);
	}

	protected abstract List<E> cacheOrBuild();  // there will be multiple elements of this type ; to avoid having 783478384 lists that contain the exact same list, this returns a static valuesCache (weak reference to the plugin lifecycle reference)
	protected final List<E> cachedOrBuild(WeakHashMap<Object, List<E>> cache, Supplier<Stream<E>> buildSorted) {
		List<E> values = cache.get(getSuperElement().getPlugin().getLifecycleReference());
		if (values == null) {
			values = Collections.unmodifiableList(buildSorted.get().collect(Collectors.toList()));
			cache.put(getSuperElement().getPlugin().getLifecycleReference(), values);
		}
		return values;
	}

	@Override
	public List<E> getValues() {
		return cacheOrBuild();
	}

}
