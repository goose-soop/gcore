package com.guillaumevdn.gcore.lib.element.struct.map;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import com.guillaumevdn.gcore.TextEditorGeneric;
import com.guillaumevdn.gcore.lib.collection.SerializerLowerCaseLinkedHashMap;
import com.guillaumevdn.gcore.lib.element.struct.Element;
import com.guillaumevdn.gcore.lib.element.struct.IElement;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.object.Optional;
import com.guillaumevdn.gcore.lib.serialization.Serializer;
import com.guillaumevdn.gcore.lib.string.Text;
import com.guillaumevdn.gcore.lib.tuple.Pair;

/**
 * @author GuillaumeVDN
 */
public abstract class AbstractMapElement<K, V extends IElement> extends Element {

	private final Serializer<K> keySerializer;
	private SerializerLowerCaseLinkedHashMap<K, V> elements;

	public AbstractMapElement(Class<K> keyClass, Element parent, String id, Need need, Text editorDescription) {
		this(Serializer.find(keyClass), parent, id, need, editorDescription);
	}

	public AbstractMapElement(Serializer<K> keySerializer, Element parent, String id, Need need, Text editorDescription) {
		super(parent, id, need.getType(), editorDescription);
		this.keySerializer = keySerializer;
		reinitializeElements(0, 1f);
	}

	protected void reinitializeElements(int initialCapacity, float loadFactor) {
		this.elements = new SerializerLowerCaseLinkedHashMap<>(keySerializer, initialCapacity, loadFactor);
	}

	// ----- get
	protected final Serializer<K> getKeySerializer() {
		return keySerializer;
	}

	protected int size() {
		return elements.size();
	}

	protected boolean isEmpty() {
		return elements.isEmpty();
	}

	public final List<K> keys() {
		return elements.keySet();  // map already returns an unmodifiable collection
	}

	public final List<V> values() {
		return elements.values();  // map already returns an unmodifiable collection
	}

	public List<Pair<K, V>> getElements() {	
		return elements.getElements();  // map already returns an unmodifiable collection
	}

	public Optional<V> getElement(K key) {
		return Optional.of(elements.get(key));
	}

	public Optional<V> findElement(Predicate<V> predicate) {
		return Optional.of(elements.valuesStream().filter(predicate).findFirst().orElse(null));
	}

	@Override
	public boolean hasParseableLocations() {
		for (V element : elements.values()) {
			if (element.hasParseableLocations()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isCurrentlyDefault() {
		return isEmpty();
	}

	// ----- add/remove
	/** @eturn the value parameter */
	protected V add(K key, V value) {
		if (value instanceof Element && !equals(((Element) value).getParent())) throw new IllegalStateException("added elements must be children of this element");
		elements.put(key, value);
		return value;
	}

	/** @eturn the value parameter associated with the key parameter, or null if none */
	public final V remove(K key) {
		return elements.remove(key);
	}

	/** @eturn true if the value parameter was present and removed from the map */
	public final boolean remove(V value) {
		return Objects.deepEquals(remove(keySerializer.deserialize(value.getId())), value);
	}

	protected void clear() {
		elements.clear();
	}

	// ----- editor
	@Override
	public List<String> editorCurrentValue() {
		return size() == 0 ? null : (size() <= 1 ? TextEditorGeneric.elementElementCountSingle.parseLines() : TextEditorGeneric.elementElementCountPlural.replace("{count}", () -> size()).parseLines());
	}

}
