package com.guillaumevdn.gcore.lib.util.collection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;

import com.guillaumevdn.gcore.lib.util.Utils;

public class SortedMap<K, V> implements Cloneable {

	// base
	private Type type;
	private Order order;
	private Map<K, V> map = new HashMap<K, V>();
	private final Comparator<K> keyComparator;

	public SortedMap(Object... content) {
		this(Type.UNSORTED, null, content);
	}

	public SortedMap(final Type type, final Order order, final Object... content) {
		this.type = type;
		this.order = order;
		// key sorted
		if (type != null && type.equals(Type.KEY_SORTED)) {
			keyComparator = new Comparator<K>() {
				@Override
				public int compare(final K k1, final K k2) {
					// null or not comparable
					if (k1 == null || !(k1 instanceof Comparable<?>)) {
						return -order.signum;
					}
					if (k2 == null || !(k2 instanceof Comparable<?>)) {
						return order.signum;
					}
					// compare by key
					return order.signum * ((Comparable<K>) k1).compareTo(k2);
				}
			};
		}
		// value sorted
		else if (type != null && type.equals(Type.VALUE_SORTED)) {
			keyComparator = new Comparator<K>() {
				@Override
				public int compare(final K k1, final K k2) {
					// null or not comparable
					V v1 = map.get(k1);
					if (v1 == null || !(v1 instanceof Comparable<?>)) {
						return -order.signum;
					}
					V v2 = map.get(k2);
					if (v2 == null || !(v2 instanceof Comparable<?>)) {
						return order.signum;
					}
					// compare by value
					return order.signum * ((Comparable<V>) v1).compareTo(v2);
				}
			};
		}
		// unsorted
		else {
			keyComparator = null;
		}
		// init contents
		if (content != null) {
			if (content.length != 0 && content.length % 2 != 0) throw new IllegalArgumentException("size isn't a multiple of 2");
			for (int i = 0; i < content.length; i += 2) {
				map.put((K) content[i], (V) content[i + 1]);
			}
		}
	}

	// methods
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
		return map.containsKey(key);
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
	public V get(K key) {
		return map.get(key);
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
	public Set<K> keySet() {
		return Collections.unmodifiableSet(keyComparator == null ? map.keySet() : Utils.asSortedSet(map.keySet(), keyComparator));
	}

	/**
	 * Map a value to a key
	 * @param key the key
	 * @param value the value
	 * @return the value previously associated with this key (a null value might mean that it was mapped with a null value, or that there was no mapping as well)
	 */
	public V put(K key, V value) {
		return map.put(key, value);
	}

	public void putAll(Map<? extends K, ? extends V> putAll) {
		map.putAll(putAll);
	}

	/**
	 * Remove a key from the map
	 * @param key the key
	 * @return the value previously associated with this key (a null value might mean that it was mapped with a null value, or that there was no mapping as well)
	 */
	public V remove(K key) {
		return map.remove(key);
	}

	/**
	 * @return the size of the map
	 */
	public int size() {
		return map.size();
	}

	/**
	 * @return an immutable list of keys for this map, eventually sorted depending on this map type
	 */
	public List<V> values() {
		List<V> list = new ArrayList<V>();
		for (K k : keySet()) {
			list.add(get(k));
		}
		return Collections.unmodifiableList(list);
	}
	
    public void forEach(BiConsumer<? super K, ? super V> action) {
        Objects.requireNonNull(action);
        for (K key : keySet()) {
            action.accept(key, get(key));
        }
    }

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
		final int prime = 31;
		int result = 1;
		result = prime * result + ((keyComparator == null) ? 0 : keyComparator.hashCode());
		result = prime * result + ((map == null) ? 0 : map.hashCode());
		result = prime * result + ((order == null) ? 0 : order.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (!Utils.instanceOf(obj, getClass())) {
			return false;
		}
		SortedMap<K, V> other = (SortedMap<K, V>) obj;
		return other.type.equals(type) && other.order.equals(order) && other.keySet().equals(keySet());
	}

	@Override
	public SortedMap<K, V> clone() {
		SortedMap<K, V> clone = new SortedMap<K, V>(type, order);
		clone.putAll(map);
		return this;
	}

	// methods
	public K getKeyByValue(V value) {
		for (K key : keySet()) {
			if (get(key).equals(value)) {
				return key;
			}
		}
		return null;
	}

	public K getKeyAt(int index) {
		if (index < 0 || index >= map.size()) throw new IndexOutOfBoundsException("index " + index + ", size " + map.size());
		return Utils.getSetElement(keySet(), index);
	}

	public V removeKeyAt(int index) {
		return remove(getKeyAt(index));
	}
	
	public int indexOf(K key) {
		int i = -1;
		for (K k : keySet()) {
			++i;
			if (key == null ? k == null : key.equals(k)) {
				return i;
			}
		}
		return -1;
	}

	// type enum
	public enum Type {
		KEY_SORTED, VALUE_SORTED, UNSORTED;
	}

	// order enum
	public enum Order {

		NATURAL(1),
		REVERSE(-1);

		private final int signum;

		private Order(int value) {
			this.signum = value;
		}

	}

	// static methods
	public static <TK, TV> SortedMap<TK, TV> createMap(Object... objects) {
		if (objects.length != 0 && objects.length % 2 != 0) throw new IllegalArgumentException("size isn't a multiple of 2");
		SortedMap<TK, TV> map = new SortedMap<TK, TV>();
		for (int i = 0; i < objects.length; i += 2) {
			map.put((TK) objects[i], (TV) objects[i + 1]);
		}
		return map;
	}

}
