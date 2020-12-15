package com.guillaumevdn.gcore.lib.collection;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * @author GuillaumeVDN
 */
public final class PositionCache<V> {

	private Map<Integer, Map<Integer, Map<Integer, V>>> map;
	private int initialCapacityZ, initialCapacityY;

	public PositionCache(int initialCapacityX, int initialCapacityZ, int initialCapacityY) {
		map = new ConcurrentHashMap<>(initialCapacityX);
	}

	public boolean contains(int x, int y, int z) {
		return getY(x, z).containsKey(y);
	}

	public V computeIfAbsent(int x, int y, int z, Supplier<V> ifAbsent) {
		return getY(x, z).computeIfAbsent(y, __ -> ifAbsent.get());
	}

	public void add(int x, int y, int z, V value) {
		getY(x, z).put(y, value);
	}

	public void remove(int x, int y, int z) {
		getY(x, z).remove(y);
	}

	public void clear() {
		map.clear();
	}

	private Map<Integer, V> getY(int x, int z) {
		return map.computeIfAbsent(x, __ -> new ConcurrentHashMap<>(initialCapacityZ)).computeIfAbsent(z, __ -> new ConcurrentHashMap<>(initialCapacityY));
	}

}
