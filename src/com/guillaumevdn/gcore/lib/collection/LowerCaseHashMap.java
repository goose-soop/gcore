package com.guillaumevdn.gcore.lib.collection;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.guillaumevdn.gcore.lib.object.ObjectUtils;

/**
 * @author GuillaumeVDN
 */
public class LowerCaseHashMap<V> extends HashMap<String, V> {

	private static final long serialVersionUID = 8645174203269985787L;

	@Override
	public boolean containsKey(Object key) {
		return super.containsKey(lower(key));
	}

	@Override
	public V get(Object key) {
		return super.get(lower(key));
	}

	@Override
	public V getOrDefault(Object key, V defaultValue) {
		return super.getOrDefault(lower(key), defaultValue);
	}

	@Override
	public V put(String key, V value) {
		return super.put(lower(key), value);
	}

	@Override
	public V putIfAbsent(String key, V value) {
		return super.putIfAbsent(lower(key), value);
	}

	@Override
	public void putAll(Map<? extends String, ? extends V> map) {
		map.forEach((key, value) -> put(key, value));
	}

	@Override
	public boolean replace(String key, V oldValue, V newValue) {
		return super.replace(lower(key), oldValue, newValue);
	}

	@Override
	public V replace(String key, V value) {
		return super.replace(lower(key), value);
	}

	@Override
	public V compute(String key, BiFunction<? super String, ? super V, ? extends V> remappingFunction) {
		return super.compute(lower(key), remappingFunction);
	}

	@Override
	public V computeIfAbsent(String key, Function<? super String, ? extends V> mappingFunction) {
		return super.computeIfAbsent(lower(key), mappingFunction);
	}

	@Override
	public V computeIfPresent(String key, BiFunction<? super String, ? super V, ? extends V> remappingFunction) {
		return super.computeIfPresent(lower(key), remappingFunction);
	}

	@Override
	public V merge(String key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
		return super.merge(lower(key), value, remappingFunction);
	}

	@Override
	public V remove(Object key) {
		return super.remove(lower(key));
	}

	@Override
	public boolean remove(Object key, Object mustBeValue) {
		return Objects.deepEquals(remove(key), mustBeValue);
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
