package com.guillaumevdn.gcore.lib.collection;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.guillaumevdn.gcore.lib.serialization.Serializer;
import com.guillaumevdn.gcore.lib.tuple.Pair;

/**
 * @author GuillaumeVDN
 */
public class SerializerLowerCaseLinkedHashMap<K, V> implements Cloneable {

	private final Serializer<K> keySerializer;
	private final LowerCaseLinkedHashMap<V> map;

	public SerializerLowerCaseLinkedHashMap(Class<K> keyClass, int initialCapacity, float loadFactor) {
		this(Serializer.find(keyClass), initialCapacity, loadFactor);
	}

	public SerializerLowerCaseLinkedHashMap(Serializer<K> keySerializer, int initialCapacity, float loadFactor) {
		this.keySerializer = keySerializer;
		this.map = new LowerCaseLinkedHashMap<>(initialCapacity, loadFactor);
	}

	// ----- methods
	/**
	 * Clear the map
	 */
	public void clear() {
		map.clear();
	}

	/**
	 * @param key the key
	 * @return true if the map contains a value for this key
	 */
	public boolean containsKey(K key) {
		return map.containsKey(keySerializer.serialize(key));
	}

	/**
	 * @param value the value
	 * @return true if the map contains at least one key for this value
	 */
	public boolean containsValue(V value) {
		return map.containsValue(value);
	}

	/**
	 * @param key the key
	 * @return the value associated with this key (a null value might mean that it's mapped with a null value, or that
	 *         there's no mapping as well)
	 */
	public V get(K key) {
		return map.get(keySerializer.serialize(key));
	}

	/**
	 * @return true if the map is empty
	 */
	public boolean isEmpty() {
		return map.isEmpty();
	}

	/**
	 * @return an immutable set of sorted keys for this map
	 */
	public List<K> keySet() {
		return Collections.unmodifiableList(keyStream().collect(Collectors.toList()));
	}

	/**
	 * @return a stream of sorted keys for this map
	 */
	public Stream<K> keyStream() {
		return map.keySet().stream().map(string -> keySerializer.deserialize(string));
	}

	/**
	 * @return an immutable list of sorted values for this map
	 */
	public List<V> values() {
		return Collections.unmodifiableList(CollectionUtils.asList(map.values()));
	}

	/**
	 * @return a stream of sorted values for this map
	 */
	public Stream<V> valuesStream() {
		return map.values().stream();
	}

	/**
	 * @return an immutable list of sorted key/value pairs for this map
	 */
	public List<Pair<K, V>> getElements() {
		return Collections.unmodifiableList(
				map.keySet().stream().map(string -> Pair.of(keySerializer.deserialize(string), map.get(string))).collect(Collectors.toList()));
	}

	/**
	 * Map a value to a key
	 * 
	 * @param key   the key
	 * @param value the value
	 * @return the value previously associated with this key (a null value might mean that it was mapped with a null value,
	 *         or that there was no mapping as well)
	 */
	public V put(K key, V value) {
		return map.put(keySerializer.serialize(key), value);
	}

	public void putAll(Map<? extends K, ? extends V> putAll) {
		putAll.forEach((key, value) -> put(key, value));
	}

	/**
	 * Remove a key from the map
	 * 
	 * @param key the key
	 * @return the value previously associated with this key (a null value might mean that it was mapped with a null value,
	 *         or that there was no mapping as well)
	 */
	public V remove(K key) {
		return map.remove(keySerializer.serialize(key));
	}

	/**
	 * @return the size of the map
	 */
	public int size() {
		return map.size();
	}

	public K getKeyByValue(V value) {
		for (String key : map.keySet()) {
			if (Objects.deepEquals(map.get(key), value)) {
				return keySerializer.deserialize(key);
			}
		}
		return null;
	}

	public K getKeyAt(int index) {
		return keySet().get(index);
	}

	public V getValueAt(int index) {
		return get(getKeyAt(index));
	}

	public V removeKeyAt(int index) {
		return remove(getKeyAt(index));
	}

	public int indexOf(K key) {
		return keySet().indexOf(key);
	}

	public void forEach(BiConsumer<K, V> action) {
		Objects.requireNonNull(action);
		keySet().forEach(key -> {
			action.accept(key, get(key));
		});
	}

	// ----- object
	@Override
	public String toString() {
		if (isEmpty()) {
			return "{}";
		}
		String str = "{ ";
		for (K k : keySet()) {
			V v = get(k);
			str += "[" + (k == null ? "null" : k.toString()) + ", " + (v == null ? "null" : v.toString()) + "], ";
		}
		str = str.substring(0, str.length() - ", ".length()) + " }";
		return str;
	}

	@Override
	public int hashCode() {
		return Objects.hash(keySerializer, map);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SerializerLowerCaseLinkedHashMap other = (SerializerLowerCaseLinkedHashMap) obj;
		if (map == null) {
			if (other.map != null)
				return false;
		} else if (!map.equals(other.map))
			return false;
		return true;
	}

	@Override
	public SerializerLowerCaseLinkedHashMap<K, V> clone() {
		SerializerLowerCaseLinkedHashMap<K, V> clone = new SerializerLowerCaseLinkedHashMap<K, V>(keySerializer.getTypeClass(), size(), 1f);
		forEach((key, value) -> clone.put(key, value));
		return this;
	}

}
