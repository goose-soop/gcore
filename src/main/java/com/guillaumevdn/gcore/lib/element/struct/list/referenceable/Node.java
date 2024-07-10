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
	private final String globalId;
	private final V value;

	public Node(ElementsContainer<? extends V> ref, String key, String globalId) {
		this.ref = ref;
		this.type = NodeType.GLOBAL;
		this.key = key;
		this.globalId = globalId;
		this.value = null;
	}

	public Node(ElementsContainer<? extends V> ref, String key, V element) {
		if (element == null) {
			throw new IllegalArgumentException("local elements can't be null");
		}
		this.ref = ref;
		this.type = NodeType.LOCAL;
		this.key = key;
		this.globalId = null;
		this.value = element;
	}

	public NodeType getType() {
		return type;
	}

	public String getKey() {
		return key;
	}

	public String getGlobalId() {
		return globalId;
	}

	public V getValue() {
		return value != null ? value : ref.getElement(globalId).orNull();
	}

	public boolean keyOrGlobalIdEquals(String id) {
		return id.equalsIgnoreCase(key) || id.equalsIgnoreCase(globalId /* maybe null */);
	}

	// ----- abstract element
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

	// ----- object
	@Override
	public int hashCode() {
		return Objects.hash(key);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		Node<V> other = ObjectUtils.castOrNull(obj, getClass());
		return other != null && key.equals(other.key);
	}

}
