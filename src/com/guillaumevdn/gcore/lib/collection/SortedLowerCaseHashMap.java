package com.guillaumevdn.gcore.lib.collection;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.function.BiConsumer;

import com.guillaumevdn.gcore.lib.collection.SortedHashMap.Order;
import com.guillaumevdn.gcore.lib.collection.SortedHashMap.Type;
import com.guillaumevdn.gcore.lib.object.ObjectUtils;

/**
 * @author GuillaumeVDN
 */
public class SortedLowerCaseHashMap<V> implements Cloneable {

	private final SortedHashMap<String, V> map;

	public SortedLowerCaseHashMap(Type type, Order order) {
		this.map = new SortedHashMap<>(type, order);
	}

	// ----- methods
	public final Type getType() {
		return map.getType();
	}

	public final Order getOrder() {
		return map.getOrder();
	}

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
	public boolean containsKey(String key) {
		return map.containsKey(lower(key));
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
	 * @return the value associated with this key (a null value might mean that it's mapped with a null value, or that there's no mapping as well)
	 */
	public V get(String key) {
		return map.get(lower(key));
	}

	/**
	 * @param key the key
	 * @return the value associated with this key, or the provided value
	 */
	public V getOrDefault(String key, V def) {
		return map.getOrDefault(lower(key), def);
	}

	/**
	 * @return true if the map is empty
	 */
	public boolean isEmpty() {
		return map.isEmpty();
	}

	/**
	 * @return an immutable set of keys for this map, eventually sorted depending on this map type
	 */
	public NavigableSet<String> keySet() {
		return map.keySet();
	}

	/**
	 * @return an immutable set of keys for this map, sorted and reverted depending on this map type
	 * @throws IllegalStateException if the set isn't sorted
	 */
	public List<String> copyRevertedKeySet() {
		return map.copyRevertedKeySet();
	}

	/**
	 * Map a value to a key
	 * @param key the key
	 * @param value the value
	 * @return the value previously associated with this key (a null value might mean that it was mapped with a null value, or that there was no mapping as well)
	 */
	public V put(String key, V value) {
		return map.put(lower(key), value);
	}

	public void putAll(Map<String, ? extends V> putAll) {
		putAll.forEach((key, value) -> put(key, value));
	}

	/**
	 * Remove a key from the map
	 * @param key the key
	 * @return the value previously associated with this key (a null value might mean that it was mapped with a null value, or that there was no mapping as well)
	 */
	public V remove(String key) {
		return map.remove(lower(key));
	}

	/**
	 * @return the size of the map
	 */
	public int size() {
		return map.size();
	}

	/**
	 * @return a list of keys for this map, eventually sorted depending on this map type
	 */
	public Collection<V> values() {
		return map.values();
	}

	/**
	 * @return an immutable list of keys for this map, eventually sorted depending on this map type
	 */
	public List<V> copyRevertedValues() {
		return map.copyRevertedValues();
	}

	@Override
	public String toString() {
		return map.toString();
	}

	@Override
	public int hashCode() {
		return map.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !ObjectUtils.instanceOf(obj, getClass())) {
			return false;
		}
		SortedLowerCaseHashMap<V> other = (SortedLowerCaseHashMap<V>) obj;
		return other.map.equals(map);
	}

	@Override
	public SortedLowerCaseHashMap<V> clone() {
		SortedLowerCaseHashMap<V> clone = new SortedLowerCaseHashMap<V>(getType(), getOrder());
		forEach((key, value) -> clone.put(key, value));
		return this;
	}

	// ----- methods
	public String getKeyByValue(V value) {
		return map.getKeyByValue(value);
	}

	public String getKeyAt(int index) {
		return map.getKeyAt(index);
	}

	public V getValueAt(int index) {
		return map.getValueAt(index);
	}

	public V removeKeyAt(int index) {
		return map.removeKeyAt(index);
	}

	public int indexOf(String key) {
		return map.indexOf(lower(key));
	}

	public void forEach(BiConsumer<String, V> action) {
		map.forEach(action);
	}

	// ----- static methods
	public static <TV> SortedLowerCaseHashMap<TV> asMap(Type type, Order order, Object... objects) {
		if (objects.length != 0 && objects.length % 2 != 0) throw new IllegalArgumentException("size isn't a multiple of 2");
		SortedLowerCaseHashMap<TV> map = new SortedLowerCaseHashMap<>(type, order);
		for (int i = 0; i < objects.length; i += 2) {
			map.put((String) objects[i], (TV) objects[i + 1]);
		}
		return map;
	}

	public static <TV> SortedLowerCaseHashMap<TV> keySorted() {
		return new SortedLowerCaseHashMap<>(Type.KEY_SORTED, Order.NATURAL);
	}

	public static <TV> SortedLowerCaseHashMap<TV> keySortedReverse() {
		return new SortedLowerCaseHashMap<>(Type.KEY_SORTED, Order.REVERSE);
	}

	public static <TV> SortedLowerCaseHashMap<TV> valueSorted() {
		return new SortedLowerCaseHashMap<>(Type.VALUE_SORTED, Order.NATURAL);
	}

	public static <TV> SortedLowerCaseHashMap<TV> valueSortedReverse() {
		return new SortedLowerCaseHashMap<>(Type.VALUE_SORTED, Order.REVERSE);
	}

	private static String lower(Object key) {
		if (key == null) {
			return null;
		}
		if (!ObjectUtils.instanceOf(key, String.class)) {
			throw new IllegalArgumentException("key isn't a string : " + key);
		}
		return ((String) key).toLowerCase();
	}

}
