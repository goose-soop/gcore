package com.guillaumevdn.gcore.lib.element.struct.list.referenceable;

import java.util.Objects;

import com.guillaumevdn.gcore.lib.element.struct.Element;
import com.guillaumevdn.gcore.lib.element.struct.IElement;
import com.guillaumevdn.gcore.lib.element.struct.SuperElement;
import com.guillaumevdn.gcore.lib.object.ObjectUtils;

/**
 * @author GuillaumeVDN
 */
public final class Node<V extends Element> implements IElement {

	private final ElementsContainer<? extends V> ref;
	private final NodeType type;
	private final String key;
	private final V value;

	public Node(ElementsContainer<? extends V> ref, String key) {
		this(ref, key, null);
	}

	public Node(ElementsContainer<? extends V> ref, String key, V element) {
		this.ref = ref;
		this.type = element == null ? NodeType.GLOBAL : NodeType.LOCAL;
		this.key = key;
		this.value = element;
	}

	// get
	public NodeType getType() {
		return type;
	}

	public String getKey() {
		return key;
	}

	public V getValue() {
		return value != null ? value : ref.getElement(key).orNull();
	}

	// abstract element
	@Override
	public String getId() {
		return getKey();
	}

	@Override
	public SuperElement getSuperElement() {
		return getValue().getSuperElement();
	}

	@Override
	public boolean hasParseableLocations() {
		return getValue().hasParseableLocations();
	}

	@Override
	public String getTypeName() {
		return getValue().getTypeName();
	}

	@Override
	public String getConfigurationPath() {
		return getValue().getConfigurationPath();
	}

	// object
	@Override
	public int hashCode() {
		return Objects.hash(key);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		Node<V> other = ObjectUtils.castOrNull(obj, getClass());
		return other != null && key.equals(other.key);
	}

}
