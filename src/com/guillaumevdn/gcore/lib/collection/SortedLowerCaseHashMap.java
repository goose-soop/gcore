package com.guillaumevdn.gcore.lib.collection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.guillaumevdn.gcore.lib.object.ObjectUtils;

/**
 * @author GuillaumeVDN
 */
public class SortedLowerCaseHashMap<V> implements Cloneable {

	private final Type type;
	private final Order order;
	private final LowerCaseHashMap<V> map;
	private final Comparator<String> keyComparator;

	public SortedLowerCaseHashMap(Type type, Order order, int initialCapacity, float loadFactor) {
		if (type == null) throw new IllegalArgumentException("type can't be null");
		if (order == null) throw new IllegalArgumentException("order can't be null");
		this.type = type;
		this.order = order;
		this.map = new LowerCaseHashMap<>(initialCapacity, loadFactor);

		// key sorted
		if (type.equals(Type.KEY_SORTED)) {
			keyComparator = new Comparator<String>() {
				@Override
				public int compare(final String k1, final String k2) {
					// null
					if (k1 == null) return -order.signum;
					if (k2 == null) return order.signum;
					// compare by key
					return order.signum * k1.compareTo(k2);
				}
			};
		}
		// value sorted
		else {
			keyComparator = new Comparator<String>() {
				@Override
				public int compare(final String k1, final String k2) {
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
	}

	/**
	 * @param key the key
	 * @return true if the map contains a value for this key
	 */
	public boolean containsKey(String key) {
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
	public V get(String key) {
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
	 * @return an immutable set of keys for this map, eventually sorted depending on this map type
	 */
	public List<String> keySet() {
		return keySet(Function.identity());
	}

	/**
	 * @return an immutable set of keys for this map, eventually sorted depending on this map type
	 */
	public <T> List<T> keySet(Function<String, T> mapper) {
		return Collections.unmodifiableList(map.keySet().stream().sorted(keyComparator).map(mapper).collect(Collectors.toList()));
	}

	/**
	 * @return an immutable set of keys for this map, sorted and reverted depending on this map type
	 * @throws IllegalStateException if the set isn't sorted
	 */
	public List<String> revertedKeySet() {
		if (keyComparator == null) {
			throw new IllegalStateException("map isn't sorted");
		}
		return Collections.unmodifiableList(CollectionUtils.asRevertSet(CollectionUtils.asSortedSet(map.keySet(), keyComparator)));
	}

	/**
	 * Map a value to a key
	 * @param key the key
	 * @param value the value
	 * @return the value previously associated with this key (a null value might mean that it was mapped with a null value, or that there was no mapping as well)
	 */
	public V put(String key, V value) {
		return map.put(key, value);
	}

	public void putAll(Map<String, ? extends V> putAll) {
		map.putAll(putAll);
	}

	/**
	 * Remove a key from the map
	 * @param key the key
	 * @return the value previously associated with this key (a null value might mean that it was mapped with a null value, or that there was no mapping as well)
	 */
	public V remove(String key) {
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
		for (String k : keySet()) {
			list.add(get(k));
		}
		return Collections.unmodifiableList(list);
	}

	/**
	 * @return an immutable list of keys for this map, eventually sorted depending on this map type
	 */
	public List<V> revertedValues() {
		List<V> list = new ArrayList<V>();
		for (String k : revertedKeySet()) {
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
		for (String k : keySet()) {
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
		SortedLowerCaseHashMap<V> other = (SortedLowerCaseHashMap<V>) obj;
		return other.type.equals(type) && other.order.equals(order) && other.keySet().equals(keySet());
	}

	@Override
	public SortedLowerCaseHashMap<V> clone() {
		SortedLowerCaseHashMap<V> clone = new SortedLowerCaseHashMap<V>(getType(), getOrder(), size(), 1f);
		forEach((key, value) -> clone.put(key, value));
		return this;
	}

	// ----- methods
	public String getKeyByValue(V value) {
		for (String key : keySet()) {
			if (get(key).equals(value)) {
				return key;
			}
		}
		return null;
	}

	public String getKeyAt(int index) {
		if (index < 0 || index >= map.size()) throw new IndexOutOfBoundsException("index " + index + ", size " + map.size());
		Iterator<String> iterator = keySet().iterator();
		int i = -1;
		while (iterator.hasNext()) {
			String key = iterator.next();
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

	public int indexOf(String key) {
		int i = -1;
		for (String k : keySet()) {
			++i;
			if (key == null ? k == null : key.equals(k)) {
				return i;
			}
		}
		return -1;
	}

	public void forEach(BiConsumer<String, V> action) {
		Objects.requireNonNull(action);
		keySet().forEach(key -> {
			action.accept(key, get(key));
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
	public static <TV> SortedLowerCaseHashMap<TV> asMap(Type type, Order order, int initialCapacity, float loadFactor, Object... objects) {
		if (objects.length != 0 && objects.length % 2 != 0) throw new IllegalArgumentException("size isn't a multiple of 2");
		SortedLowerCaseHashMap<TV> map = new SortedLowerCaseHashMap<>(type, order, initialCapacity, loadFactor);
		for (int i = 0; i < objects.length; i += 2) {
			map.put((String) objects[i], (TV) objects[i + 1]);
		}
		return map;
	}

	public static <TV> SortedLowerCaseHashMap<TV> keySorted(int initialCapacity, float loadFactor) {
		return new SortedLowerCaseHashMap<>(Type.KEY_SORTED, Order.NATURAL, initialCapacity, loadFactor);
	}

	public static <TV> SortedLowerCaseHashMap<TV> keySortedReverse(int initialCapacity, float loadFactor) {
		return new SortedLowerCaseHashMap<>(Type.KEY_SORTED, Order.REVERSE, initialCapacity, loadFactor);
	}

	public static <TV> SortedLowerCaseHashMap<TV> valueSorted(int initialCapacity, float loadFactor) {
		return new SortedLowerCaseHashMap<>(Type.VALUE_SORTED, Order.NATURAL, initialCapacity, loadFactor);
	}

	public static <TV> SortedLowerCaseHashMap<TV> valueSortedReverse(int initialCapacity, float loadFactor) {
		return new SortedLowerCaseHashMap<>(Type.VALUE_SORTED, Order.REVERSE, initialCapacity, loadFactor);
	}

}
