package com.guillaumevdn.gcore.lib.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Objects;
import java.util.TreeMap;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import com.guillaumevdn.gcore.lib.object.ObjectUtils;

/**
 * @author GuillaumeVDN
 */
public class SortedHashMap<K, V> implements Cloneable {

	private final Type type;
	private final Order order;
	private final TreeMap<K, V> map;
	private final Map<K, V> refMap;  // only for value-sorted maps
	private final Comparator<K> keyComparator;

	public SortedHashMap(final Type type, final Order order) {
		if (type == null) throw new IllegalArgumentException("type can't be null");
		if (order == null) throw new IllegalArgumentException("order can't be null");
		this.type = type;
		this.order = order;
		// key sorted
		if (type.equals(Type.KEY_SORTED)) {
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
					int comp = ((Comparable<K>) k1).compareTo(k2);
					if (comp == 0) {
						comp = k1.hashCode() - k2.hashCode();
					}
					return order.signum * comp;
				}
			};
			refMap = null;
		}
		// value sorted
		else {
			refMap = new HashMap<>();
			keyComparator = new Comparator<K>() {
				@Override
				public int compare(final K k1, final K k2) {
					// null or not comparable
					V v1 = refMap.get(k1);
					if (v1 == null || !(v1 instanceof Comparable<?>)) {
						return -order.signum;
					}
					V v2 = refMap.get(k2);
					if (v2 == null || !(v2 instanceof Comparable<?>)) {
						return order.signum;
					}
					// compare by value
					int comp = ((Comparable<V>) v1).compareTo(v2);
					if (comp == 0) {
						comp = v1.hashCode() - v2.hashCode();
					}
					return order.signum * comp;
				}
			};
		}
		map = new TreeMap<>(keyComparator);
	}

	// ----- methods
	public final Type getType() {
		return type;
	}

	public final Order getOrder() {
		return order;
	}

	/**
	 * Clear the map
	 */
	public void clear() {
		map.clear();
		if (refMap != null) {
			refMap.clear();
		}
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
	 * @param key the key
	 * @return the value associated with this key, or the provided value
	 */
	public V getOrDefault(String key, V def) {
		return map.getOrDefault(key, def);
	}

	/**
	 * @return true if the map is empty
	 */
	public boolean isEmpty() {
		return map.isEmpty();
	}

	/**
	 * @return a set of keys for this map, eventually sorted depending on this map type
	 */
	public NavigableSet<K> keySet() {
		return map.navigableKeySet();
	}

	/**
	 * @return an immutable set of keys for this map, sorted and reverted depending on this map type
	 * @throws IllegalStateException if the set isn't sorted
	 */
	public List<K> copyRevertedKeySet() {
		return CollectionUtils.asRevertSet(map.navigableKeySet());
	}

	/**
	 * Map a value to a key
	 * @param key the key
	 * @param value the value
	 * @return the value previously associated with this key (a null value might mean that it was mapped with a null value, or that there was no mapping as well)
	 */
	public V put(K key, V value) {
		if (refMap != null) {  // bah évidemment ça ça se met avant espèce de giga débile vu qu'on s'en sert pour sort
			refMap.put(key, value);
		}
		V v = map.put(key, value);
		return v;
	}

	public void putAll(Map<? extends K, ? extends V> putAll) {
		if (refMap != null) {
			refMap.putAll(putAll);
		}
		map.putAll(putAll);
	}

	public V computeIfAbsent(K key, Supplier<V> ifAbsent) {
		if (refMap != null) {
			refMap.computeIfAbsent(key, __ -> ifAbsent.get());
		}
		V v = map.computeIfAbsent(key, __ -> ifAbsent.get());
		return v;
	}

	/**
	 * Remove a key from the map
	 * @param key the key
	 * @return the value previously associated with this key (a null value might mean that it was mapped with a null value, or that there was no mapping as well)
	 */
	public V remove(K key) {
		V v = map.remove(key);
		if (refMap != null) {
			refMap.remove(key);
		}
		return v;
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
		List<V> list = new ArrayList<V>();
		for (K k : copyRevertedKeySet()) {
			list.add(get(k));
		}
		return Collections.unmodifiableList(list);
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
		if (obj == null || !ObjectUtils.instanceOf(obj, getClass())) {
			return false;
		}
		SortedHashMap<K, V> other = (SortedHashMap<K, V>) obj;
		return other.type.equals(type) && other.order.equals(order) && other.keySet().equals(keySet());
	}

	@Override
	public SortedHashMap<K, V> clone() {
		SortedHashMap<K, V> clone = new SortedHashMap<K, V>(getType(), getOrder());
		forEach((key, value) -> clone.put(key, value));
		return this;
	}

	// ----- methods
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
		Iterator<K> iterator = keySet().iterator();
		int i = -1;
		while (iterator.hasNext()) {
			K key = iterator.next();
			if (++i == index) {
				return key;
			}
		}
		return null;
	}

	public V getValueAt(int index) {
		return get(getKeyAt(index));
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

	public void forEach(BiConsumer<K, V> action) {
		Objects.requireNonNull(action);
		map.entrySet().forEach(entry -> {
			action.accept(entry.getKey(), entry.getValue());
		});
	}

	// ----- type enum
	public static enum Type {
		KEY_SORTED, VALUE_SORTED;
	}

	// ----- order enum
	public static enum Order {

		NATURAL(1),
		REVERSE(-1);

		private final int signum;

		private Order(int value) {
			this.signum = value;
		}

	}

	// ----- static methods
	public static <TK, TV> SortedHashMap<TK, TV> asMap(Type type, Order order, Object... objects) {
		if (objects.length != 0 && objects.length % 2 != 0) throw new IllegalArgumentException("size isn't a multiple of 2");
		SortedHashMap<TK, TV> map = new SortedHashMap<>(type, order);
		for (int i = 0; i < objects.length; i += 2) {
			map.put((TK) objects[i], (TV) objects[i + 1]);
		}
		return map;
	}

	public static <TK, TV> SortedHashMap<TK, TV> keySorted() {
		return new SortedHashMap<>(Type.KEY_SORTED, Order.NATURAL);
	}

	public static <TK, TV> SortedHashMap<TK, TV> keySortedReverse() {
		return new SortedHashMap<>(Type.KEY_SORTED, Order.REVERSE);
	}

	public static <TK, TV> SortedHashMap<TK, TV> valueSorted() {
		return new SortedHashMap<>(Type.VALUE_SORTED, Order.NATURAL);
	}

	public static <TK, TV> SortedHashMap<TK, TV> valueSortedReverse() {
		return new SortedHashMap<>(Type.VALUE_SORTED, Order.REVERSE);
	}

}
