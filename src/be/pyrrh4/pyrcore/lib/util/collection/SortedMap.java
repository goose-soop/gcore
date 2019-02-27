package be.pyrrh4.pyrcore.lib.util.collection;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import be.pyrrh4.pyrcore.lib.util.Utils;

public class SortedMap<K, V> implements Map<K, V>
{
	// ------------------------------------------------------------
	// Order
	// ------------------------------------------------------------

	public enum Type {
		KEY_SORTED, VALUE_SORTED, UNSORTED;
	}

	public enum Order
	{
		NATURAL(1),
		REVERSE(-1);

		private final int value;

		private Order(int value) {
			this.value = value;
		}
	}

	// ------------------------------------------------------------
	// Fields and constructor
	// ------------------------------------------------------------

	private Map<K, V> map;
	private transient Map<K, V> lookupMap = new HashMap<K, V>();

	public SortedMap(Object... content) {
		this(Type.UNSORTED, null, content);
	}

	
	public SortedMap(final Type type, final Order order, final Object... content)
	{
		// Key sorted

		if (type != null && type.equals(Type.KEY_SORTED))
		{
			map = new TreeMap<K, V>(new Comparator<K>() {
				@Override
				public int compare(final K k1, final K k2)
				{
					if (k1 == null) {
						return -order.value;
					}

					if (k2 == null) {
						return order.value;
					}

					if (!(k1 instanceof Comparable<?> && k2 instanceof Comparable<?>)) {
						return 0;
					}

					int cmp = ((Comparable<K>) k1).compareTo(k2);

					if (cmp == 0) {// same so sort by value
						V v1 = lookupMap.get(k1), v2 = lookupMap.get(k2);
						if (!(v1 instanceof Comparable<?> && v2 instanceof Comparable<?>)) {
							return 0;
						}
						return order.value * ((Comparable<V>) v1).compareTo(v2);
					}

					return order.value * cmp;
				}
			});
		}

		// Value sorted

		else if (type != null && type.equals(Type.VALUE_SORTED))
		{
			map = new TreeMap<K, V>(new Comparator<K>() {
				@Override
				public int compare(final K k1, final K k2)
				{
					V v1 = lookupMap.get(k1);

					if (v1 == null) {
						return -order.value;
					}

					V v2 = lookupMap.get(k2);

					if (v2 == null) {
						return order.value;
					}

					int cmp = ((Comparable<V>) v1).compareTo(v2);

					if (cmp == 0) {// same so sort by key
						if (!(k1 instanceof Comparable<?> && k2 instanceof Comparable<?>)) {
							return 0;
						}
						return order.value * ((Comparable<K>) k1).compareTo(k2);
					}

					return order.value * cmp;
				}
			});
		}

		// Unsorted

		else {
			map = new HashMap<K, V>();
		}

		// Content

		if (content.length != 0 && content.length % 2 != 0) throw new IllegalArgumentException("size isn't a multiple of 2");

		for (int i = 0; i < content.length; i += 2) {
			map.put((K) content[i], (V) content[i + 1]);
		}
	}

	// ------------------------------------------------------------
	// Override
	// ------------------------------------------------------------

	@Override
	public void clear() {
		lookupMap.clear();
		map.clear();
	}

	@Override
	public boolean containsKey(final Object key) {
		return lookupMap.containsKey(key);
	}

	@Override
	public boolean containsValue(final Object value) {
		return lookupMap.containsValue(value);
	}

	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		return map.entrySet();
	}

	@Override
	public boolean equals(final Object obj) {
		return map.equals(obj);
	}

	@Override
	public V get(final Object key) {
		return lookupMap.get(key);
	}

	@Override
	public int hashCode() {
		return map.hashCode();
	}

	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}

	@Override
	public Set<K> keySet() {
		return map.keySet();
	}

	@Override
	public V put(final K key, final V value)
	{
		lookupMap.put(key, value);
		return map.put(key, value);
	}

	@Override
	public void putAll(final Map<? extends K, ? extends V> m)
	{
		lookupMap.putAll(m);
		map.putAll(m);
	}

	@Override
	public V remove(final Object key)
	{
		lookupMap.remove(key);
		return map.remove(key);
	}

	@Override
	public int size() {
		return map.size();
	}

	@Override
	public Collection<V> values() {
		return map.values();
	}

	@Override
	public String toString()
	{
		if (isEmpty()) {
			return "{}";
		}

		String str = "{ ";
		for (K k : map.keySet()) str += "[" + k.toString() + " -- " + map.get(k).toString() + "] || ";
		str = str.substring(0, str.length() - " || ".length()) + " }";
		return str;
	}

	// ------------------------------------------------------------
	// Methods
	// ------------------------------------------------------------

	public K getKeyByValue(V v) {
		for (K k : keySet()) {
			if (get(k).equals(v))
				return k;
		}
		return null;
	}

	public K getKeyAt(int index) {
		if (index < 0 || index >= map.size()) throw new IndexOutOfBoundsException("index " + index + ", size " + map.size());
		return Utils.getSetElement(keySet(), index);
	}

	// ------------------------------------------------------------
	// Static methods
	// ------------------------------------------------------------

	
	public static <TK, TV> SortedMap<TK, TV> createMap(Object... objects)
	{
		if (objects.length != 0 && objects.length % 2 != 0) throw new IllegalArgumentException("size isn't a multiple of 2");
		SortedMap<TK, TV> map = new SortedMap<TK, TV>();

		for (int i = 0; i < objects.length; i += 2) {
			map.put((TK) objects[i], (TV) objects[i + 1]);
		}

		return map;
	}
}
