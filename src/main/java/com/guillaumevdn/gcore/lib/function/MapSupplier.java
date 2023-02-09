package com.guillaumevdn.gcore.lib.function;

import java.util.HashMap;
import java.util.Map;

/**
 * @author GuillaumeVDN
 */
@FunctionalInterface
public interface MapSupplier<K, V> {

	void fill(Map<K, V> map);

	default Map<K, V> get(Map<K, V> map) {
		fill(map);
		return map;
	}

	default Map<K, V> get() {
		return get(new HashMap<>());
	}

}
